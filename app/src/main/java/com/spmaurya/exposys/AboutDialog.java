package com.spmaurya.exposys;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class AboutDialog {

    private final AlertDialog mDialog;

    public AboutDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.about);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        mDialog.show();
    }
}
