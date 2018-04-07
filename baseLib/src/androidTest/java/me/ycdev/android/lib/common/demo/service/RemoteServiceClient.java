package me.ycdev.android.lib.common.demo.service;

import android.content.Context;
import android.support.annotation.NonNull;

import me.ycdev.android.lib.common.ipc.ServiceClientBase;
import me.ycdev.android.lib.common.utils.ThreadManager;

public class RemoteServiceClient extends ServiceClientBase<IDemoService> {
    private static final String SERVICE_NAME = "RemoteService";

    public RemoteServiceClient(@NonNull Context context) {
        super(context, SERVICE_NAME, ThreadManager.getInstance().remoteServiceRequestIpcLooper(),
                new RemoteServiceConnector(context));
    }

}
