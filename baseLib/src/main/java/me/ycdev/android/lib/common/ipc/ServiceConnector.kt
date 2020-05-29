package me.ycdev.android.lib.common.ipc

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import java.util.concurrent.atomic.AtomicInteger
import me.ycdev.android.lib.common.utils.Preconditions
import me.ycdev.android.lib.common.utils.WeakListenerManager
import timber.log.Timber

/**
 * @constructor
 * @param serviceName Used to print logs only.
 * @param connectLooper The looper used to connect/reconnect target Service.
 *               By default, it's the main looper.
 */
abstract class ServiceConnector<IServiceInterface> protected constructor(
    cxt: Context,
    protected var serviceName: String,
    val connectLooper: Looper = Looper.getMainLooper()
) {
    protected var appContext: Context = cxt.applicationContext

    protected var stateListeners = WeakListenerManager<ConnectStateListener>()
    private val connectWaitLock = Any()
    private val state = AtomicInteger(STATE_DISCONNECTED)
    private var connectStartTime: Long = 0
    private var serviceConnection: ServiceConnection? = null

    var service: IServiceInterface? = null
        private set

    @ConnectState
    val connectState: Int
        get() = state.get()

    /**
     * Get Intent to bind the target service.
     */
    protected abstract fun getServiceIntent(): Intent

    /**
     * Convert the IBinder object to interface.
     */
    protected abstract fun asInterface(service: IBinder): IServiceInterface

    protected open fun validatePermission(permission: String?): Boolean {
        return true // Skip to validate permission by default
    }

    fun isServiceExist(): Boolean {
        val intent = getServiceIntent()
        val servicesList = appContext.packageManager.queryIntentServices(intent, 0)
        return servicesList.size > 0 && selectTargetService(servicesList) != null
    }

    /**
     * Sub class can rewrite the candidate services select logic.
     */
    @VisibleForTesting
    internal fun selectTargetService(servicesList: List<ResolveInfo>): ComponentName? {
        Timber.tag(TAG).i("[%s] Candidate services: %d", serviceName, servicesList.size)
        Preconditions.checkArgument(servicesList.isNotEmpty())
        var serviceInfo = servicesList[0].serviceInfo
        for (info in servicesList) {
            if (!validatePermission(info.serviceInfo.permission)) {
                Timber.tag(TAG).w(
                    "Skip not-matched permission candidate: %s, perm: %s",
                    info.serviceInfo.name, info.serviceInfo.permission
                )
                continue
            }
            if (info.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == ApplicationInfo.FLAG_SYSTEM) {
                serviceInfo = info.serviceInfo // search the system candidate
                Timber.tag(TAG).i("[%s] Service from system found and select it", serviceName)
                break
            }
        }

        return if (validatePermission(serviceInfo.permission)) {
            ComponentName(serviceInfo.packageName, serviceInfo.name)
        } else null
    }

    /**
     * Add a connect state listener, using [WeakListenerManager] to manager listeners.
     * Callbacks will be invoked in [.getConnectLooper] thread.
     */
    fun addListener(listener: ConnectStateListener) {
        stateListeners.addListener(listener)
    }

    fun removeListener(listener: ConnectStateListener) {
        stateListeners.removeListener(listener)
    }

    fun connect() {
        connectServiceIfNeeded(false)
    }

    fun disconnect() {
        Timber.tag(TAG).i("[%s] disconnect service...", serviceName)
        connectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK)
        connectHandler.removeMessages(MSG_RECONNECT)
        connectHandler.removeMessages(MSG_NOTIFY_LISTENERS)
        service = null
        if (serviceConnection != null) {
            appContext.unbindService(serviceConnection!!)
            serviceConnection = null
        }
        updateConnectState(STATE_DISCONNECTED)
    }

    private fun connectServiceIfNeeded(rebind: Boolean) {
        if (service != null) {
            Timber.tag(TAG).d("[%s] service is connected", serviceName)
            return
        }
        if (!rebind) {
            if (!state.compareAndSet(STATE_DISCONNECTED, STATE_CONNECTING)) {
                Timber.tag(TAG).d("[%s] Service is under connecting", serviceName)
                return
            }
            updateConnectState(STATE_CONNECTING)
        }
        connectStartTime = SystemClock.elapsedRealtime()

        val intent = getServiceIntent()
        val servicesList = appContext.packageManager.queryIntentServices(intent, 0)
        if (servicesList.size == 0) {
            Timber.tag(TAG).w("[%s] no service component available, cannot connect", serviceName)
            updateConnectState(STATE_DISCONNECTED)
            return
        }
        val candidateService = selectTargetService(servicesList)
        if (candidateService == null) {
            Timber.tag(TAG)
                .w("[%s] no expected service component found, cannot connect", serviceName)
            updateConnectState(STATE_DISCONNECTED)
            return
        }
        // must set explicit component before bind/start service
        intent.component = candidateService

        serviceConnection = object : ServiceConnection {
            private var mConnectLost = false

            override fun onServiceConnected(cn: ComponentName, service: IBinder) {
                Timber.tag(TAG).i(
                    "[%s] service connected, cn: %s, mConnectLost: %s",
                    serviceName, cn, mConnectLost
                )
                if (!mConnectLost) {
                    // update 'mService' first, and then update the connect state and notify
                    this@ServiceConnector.service = asInterface(service)
                    connectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK)
                    updateConnectState(STATE_CONNECTED)
                } // else: waiting for reconnecting using new ServiceConnection object
            }

            override fun onServiceDisconnected(cn: ComponentName) {
                Timber.tag(TAG).i(
                    "[%s] service disconnected, cn: %s, mConnectLost: %s",
                    serviceName, cn, mConnectLost
                )
                if (mConnectLost) {
                    return
                }

                // Unbind the service and bind it again later
                mConnectLost = true
                disconnect()

                connectHandler.sendEmptyMessageDelayed(MSG_RECONNECT, 1000)
            }
        }

        Timber.tag(TAG).i("[%s] connecting service...", serviceName)
        if (!appContext.bindService(intent, serviceConnection!!, Context.BIND_AUTO_CREATE)) {
            Timber.tag(TAG).w("[%s] cannot connect", serviceName)
            updateConnectState(STATE_DISCONNECTED)
        } else {
            connectHandler.removeMessages(MSG_CONNECT_TIMEOUT_CHECK)
            connectHandler.sendEmptyMessageDelayed(
                MSG_CONNECT_TIMEOUT_CHECK,
                CONNECT_TIMEOUT_CHECK_INTERVAL
            )
        }
    }

    private fun updateConnectState(@ConnectState newState: Int) {
        if (newState != STATE_CONNECTING) {
            state.set(newState)
        }
        connectHandler.obtainMessage(MSG_NOTIFY_LISTENERS, newState, 0).sendToTarget()
    }

    /**
     * Waiting for the service connected with timeout.
     *
     * @param timeoutMillis Timeout in milliseconds to wait for the service connected.
     * 0 means no waiting and -1 means no timeout.
     */
    @WorkerThread
    fun waitForConnected(timeoutMillis: Long = -1) {
        Preconditions.checkNonMainThread()
        if (service != null) {
            Timber.tag(TAG).d("[%s] already connected", serviceName)
            return
        }

        synchronized(connectWaitLock) {
            connect()
            var sleepTime: Long = 50
            var timeElapsed: Long = 0
            while (true) {
                val connectState = state.get()
                Timber.tag(TAG).d(
                    "[%s] checking, service: %s, state: %d, time: %d/%d",
                    serviceName, service, connectState, timeElapsed, timeoutMillis
                )
                if (connectState == STATE_CONNECTED || connectState == STATE_DISCONNECTED) {
                    break
                }
                if (timeoutMillis in 0..timeElapsed) {
                    break
                }

                connect()
                try {
                    Thread.sleep(sleepTime)
                } catch (e: InterruptedException) {
                    Timber.tag(TAG).w(e, "interrupted")
                    break
                }

                timeElapsed += sleepTime
                sleepTime *= 2
                if (sleepTime > 1000) {
                    sleepTime = 1000
                }
            }
        }
    }

    private val connectHandler = object : Handler(connectLooper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_RECONNECT -> {
                    Timber.tag(TAG).d("[%s] delayed reconnect fires...", serviceName)
                    connect()
                }

                MSG_NOTIFY_LISTENERS -> {
                    @ConnectState val newState = msg.arg1
                    Timber.tag(TAG).d("State changed: %s", strConnectState(newState))
                    stateListeners.notifyListeners { listener -> listener.onStateChanged(newState) }
                }

                MSG_CONNECT_TIMEOUT_CHECK -> {
                    Timber.tag(TAG).d("checking connect timeout")
                    val curState = state.get()
                    if (SystemClock.elapsedRealtime() - connectStartTime >= FORCE_REBIND_TIME) {
                        Timber.tag(TAG).d(
                            "[%s] connect timeout, state: %s",
                            serviceName, curState
                        )
                        if (curState == STATE_CONNECTING) {
                            // force to rebind the service
                            connectServiceIfNeeded(true)
                        }
                    } else {
                        if (curState == STATE_CONNECTING) {
                            this.sendEmptyMessageDelayed(
                                MSG_CONNECT_TIMEOUT_CHECK,
                                CONNECT_TIMEOUT_CHECK_INTERVAL
                            )
                        }
                    }
                }
            }
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STATE_DISCONNECTED, STATE_CONNECTING, STATE_CONNECTED)
    annotation class ConnectState

    companion object {
        private const val TAG = "ServiceConnector"

        const val STATE_DISCONNECTED = 1
        const val STATE_CONNECTING = 2
        const val STATE_CONNECTED = 3

        private const val MSG_RECONNECT = 1
        private const val MSG_NOTIFY_LISTENERS = 2
        private const val MSG_CONNECT_TIMEOUT_CHECK = 3

        private const val CONNECT_TIMEOUT_CHECK_INTERVAL: Long = 5000 // 5s
        private const val FORCE_REBIND_TIME: Long = 30 * 1000 // 30 seconds

        fun strConnectState(state: Int): String {
            return when (state) {
                STATE_DISCONNECTED -> "disconnected"
                STATE_CONNECTING -> "connecting"
                STATE_CONNECTED -> "connected"
                else -> "unknown"
            }
        }
    }
}
