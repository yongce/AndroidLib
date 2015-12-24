package me.ycdev.android.lib.commonui.base;

import android.support.annotation.NonNull;
import android.view.View;

public class ViewHolderBase {
    public @NonNull View itemView;
    public int position;

    public ViewHolderBase(@NonNull View itemView, int position) {
        this.itemView = itemView;
        this.position = position;
    }
}
