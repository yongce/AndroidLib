package me.ycdev.android.lib.common.demo.service;

import android.content.Context;
import android.support.annotation.NonNull;

import me.ycdev.android.lib.common.ipc.ServiceClientBase;
import me.ycdev.android.lib.common.utils.ThreadManager;

public class LocalServiceClient extends ServiceClientBase<IDemoService> {
    private static final String SERVICE_NAME = "LocalService";

    public LocalServiceClient(@NonNull Context context) {
        super(context, SERVICE_NAME, ThreadManager.getInstance().localServiceRequestIpcLooper(),
                new LocalServiceConnector(context));
    }

}
