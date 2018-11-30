package me.ycdev.android.lib.common.demo.service.operation;

import android.os.RemoteException;
import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import me.ycdev.android.lib.common.demo.service.IDemoService;
import me.ycdev.android.lib.common.ipc.IpcOperation;

public class WhoOperation implements IpcOperation<IDemoService> {
    private CountDownLatch mLatch;

    public WhoOperation setNotifier(CountDownLatch latch) {
        mLatch = latch;
        return this;
    }

    @Override
    public void execute(@NonNull IDemoService service) throws RemoteException {
        service.who();
        if (mLatch != null) {
            mLatch.countDown();
        }
    }

    @Override
    public String toString() {
        return "Operation[who]";
    }
}
