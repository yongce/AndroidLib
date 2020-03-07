package me.ycdev.android.lib.common.ipc

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.RemoteException
import androidx.annotation.WorkerThread
import timber.log.Timber
import java.util.LinkedList

open class ServiceClientBase<IService> protected constructor(
    context: Context,
    private val serviceName: String,
    workLooper: Looper,
    var serviceConnector: ServiceConnector<IService>
) : ConnectStateListener, Handler.Callback {

    protected var appContext: Context = context.applicationContext

    @Suppress("LeakingThis")
    private val operationHandler: Handler = Handler(workLooper, this)
    private val pendingOperations = LinkedList<IpcOperation<IService>>()

    var isAutoDisconnectEnabled: Boolean = false
        private set
    private var delayToDisconnect: Long = 0

    init {
        @Suppress("LeakingThis")
        this.serviceConnector.addListener(this)
    }

    /**
     * Enable/disable "auto disconnect" feature
     * @param autoDisconnect Disconnect automatically if true
     * @param delayToDisconnect The delay time to disconnect if no operations, in milliseconds.
     */
    fun setAutoDisconnect(autoDisconnect: Boolean, delayToDisconnect: Long) {
        isAutoDisconnectEnabled = autoDisconnect
        if (autoDisconnect) {
            this.delayToDisconnect = if (delayToDisconnect > 0L) delayToDisconnect else 0L
        }
    }

    fun connect() {
        serviceConnector.connect()
    }

    /**
     * Disconnect the Service connection. This may cause the pending operations to lost!
     */
    fun disconnect() {
        disconnectDelayed(0);
    }

    /**
     * Disconnect the Service connection. This may cause the pending operations to lost!
     */
    fun disconnectDelayed(delayMs: Long) {
        operationHandler.removeMessages(MSG_DELAY_DISCONNECT);
        operationHandler.sendEmptyMessageDelayed(MSG_DELAY_DISCONNECT, delayMs);
    }

    fun addOperation(operation: IpcOperation<IService>) {
        if (serviceConnector.connectState == ServiceConnector.STATE_DISCONNECTED) {
            // try to connect if not connected or connecting
            // (such as the Service APK was installed after the previous connecting)
            // (such as autoDisconnect enabled)
            serviceConnector.connect()
        }
        operationHandler.removeMessages(MSG_AUTO_DISCONNECT)
        operationHandler.removeMessages(MSG_DELAY_DISCONNECT)
        Message.obtain(operationHandler, MSG_NEW_OPERATION, operation).sendToTarget()
    }

    override fun onStateChanged(newState: Int) {
        Timber.tag(TAG).d("[%s] Service connect state changed: %d", serviceName, newState)
        if (newState == ServiceConnector.STATE_CONNECTED) {
            operationHandler.removeMessages(MSG_AUTO_DISCONNECT)
            Message.obtain(operationHandler, MSG_PENDING_OPERATIONS).sendToTarget()
        }
    }

    @WorkerThread
    private fun handleOperation(operation: IpcOperation<IService>) {
        val service = serviceConnector.service
        if (service != null) {
            try {
                operation.execute(service)
                Timber.tag(TAG).d(
                    "[%s] Succeeded to handle incoming operation: %s",
                    serviceName, operation
                )
                return // Success
            } catch (e: RemoteException) {
                Timber.tag(TAG).w(
                    e, "[%s] Failed to handle incoming operation: %s",
                    serviceName, operation
                )
                // add it into the queue again
            } catch (e: Exception) {
                Timber.tag(TAG).e(
                    e, "[%s] Cannot execute incoming operation: %s. Discard it.",
                    serviceName, operation
                )
                return // discard the operation
            }
        }

        // Service not connected or failed to IPC
        Timber.tag(TAG).d("[%s] Added into pending queue: %s", serviceName, operation)
        pendingOperations.add(operation)
    }

    @WorkerThread
    private fun handlePendingOperations() {
        Timber.tag(TAG).d("[%s] handlePendingOperations: %d", serviceName, pendingOperations.size)
        while (serviceConnector.service != null) {
            val operation = pendingOperations.poll() ?: break
            handleOperation(operation)
        }
        Timber.tag(TAG).d(
            "[%s] handlePendingOperations done: %d",
            serviceName, pendingOperations.size
        )
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_NEW_OPERATION -> {
                @Suppress("UNCHECKED_CAST")
                val operation = msg.obj as IpcOperation<IService>
                handleOperation(operation)
            }

            MSG_PENDING_OPERATIONS -> {
                handlePendingOperations()
            }

            MSG_AUTO_DISCONNECT -> {
                Timber.tag(TAG).d("auto disconnect")
                serviceConnector.disconnect()
            }

            MSG_DELAY_DISCONNECT -> {
                Timber.tag(TAG).d("delayed disconnect")
                serviceConnector.disconnect()
            }
        }

        if (isAutoDisconnectEnabled && msg.what != MSG_AUTO_DISCONNECT) {
            operationHandler.removeMessages(MSG_AUTO_DISCONNECT)
            operationHandler.sendEmptyMessageDelayed(MSG_AUTO_DISCONNECT, delayToDisconnect)
        }

        return true
    }

    companion object {
        private const val TAG = "ServiceClientBase"

        private const val MSG_NEW_OPERATION = 1
        private const val MSG_PENDING_OPERATIONS = 2
        private const val MSG_AUTO_DISCONNECT = 3
        private const val MSG_DELAY_DISCONNECT = 4
    }
}
