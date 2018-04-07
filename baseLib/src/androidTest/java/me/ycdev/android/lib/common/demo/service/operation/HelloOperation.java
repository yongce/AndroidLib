package me.ycdev.android.lib.common.demo.service.operation;

import android.os.RemoteException;
import android.support.annotation.NonNull;

import me.ycdev.android.lib.common.demo.service.IDemoService;
import me.ycdev.android.lib.common.ipc.IpcOperation;

public class HelloOperation implements IpcOperation<IDemoService> {
    private String mGift;

    public HelloOperation(String gift) {
        mGift = gift;
    }

    @Override
    public void execute(@NonNull IDemoService service) throws RemoteException {
        service.sayHello(mGift);
    }

    @Override
    public String toString() {
        return "Operation[Hello]";
    }
}
