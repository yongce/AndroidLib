package me.ycdev.android.lib.common.demo.service;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.NonNull;

import me.ycdev.android.lib.common.ipc.ServiceConnector;

public class RemoteServiceConnector extends ServiceConnector<IDemoService> {
    private static final String SERVICE_NAME = "RemoteService";

    public RemoteServiceConnector(Context cxt) {
        super(cxt, SERVICE_NAME);
    }

    @NonNull
    @Override
    protected Intent getServiceIntent() {
        return new Intent(mAppContext, RemoteService.class);
    }

    @Override
    protected IDemoService asInterface(IBinder service) {
        return IDemoService.Stub.asInterface(service);
    }
}
