package me.ycdev.android.lib.common.demo.service.operation;

import android.os.RemoteException;
import androidx.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import me.ycdev.android.lib.common.demo.service.IDemoService;
import me.ycdev.android.lib.common.ipc.IpcOperation;

public class HelloOperation implements IpcOperation<IDemoService> {
    private String mGift;
    private CountDownLatch mLatch;

    public HelloOperation(String gift) {
        mGift = gift;
    }

    public HelloOperation setNotifier(CountDownLatch latch) {
        mLatch = latch;
        return this;
    }

    @Override
    public void execute(@NonNull IDemoService service) throws RemoteException {
        service.sayHello(mGift);
        if (mLatch != null) {
            mLatch.countDown();
        }
    }

    @Override
    public String toString() {
        return "Operation[Hello]";
    }
}
