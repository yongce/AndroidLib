package me.ycdev.android.lib.common.demo.service.operation;

import android.os.RemoteException;
import android.support.annotation.NonNull;

import me.ycdev.android.lib.common.demo.service.IDemoService;
import me.ycdev.android.lib.common.ipc.IpcOperation;

public class WhoOperation implements IpcOperation<IDemoService> {
    @Override
    public void execute(@NonNull IDemoService service) throws RemoteException {
        service.who();
    }

    @Override
    public String toString() {
        return "Operation[who]";
    }
}
