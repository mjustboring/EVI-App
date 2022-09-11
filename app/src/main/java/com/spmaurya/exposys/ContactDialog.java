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

public class ContactDialog implements DialogInterface.OnShowListener {

    private final AlertDialog mDialog;

    public ContactDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.contact);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.setOnShowListener(this);
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {

        @NonNull final View addressMapButton = mDialog.findViewById(R.id.contact_address_map_button);
        @NonNull final View addressCopyButton = mDialog.findViewById(R.id.contact_address_copy_button);
        @NonNull final View email = mDialog.findViewById(R.id.contact_email);
        @NonNull final View emailCopy = mDialog.findViewById(R.id.contact_email_copy);
        @NonNull final View phone = mDialog.findViewById(R.id.contact_phone);
        @NonNull final View phoneCopy = mDialog.findViewById(R.id.contact_phone_copy);

        addressCopyButton.setOnClickListener(view -> addToClipboard("Address",
                getString(R.string.company_name)+",\n"+getString(R.string.contact_address_text)));

        addressMapButton.setOnClickListener(view ->
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://maps.app.goo.gl/sJuKZJ3uX6vMbVs5A"))));

        email.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL,
                    new String[]{getString(R.string.contact_email_text)});
            try {
                getContext().startActivity(intent);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });

        emailCopy.setOnClickListener(view ->
                addToClipboard("Email", R.string.contact_email_text));

        phone.setOnClickListener(view ->
                getContext().startActivity(new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:"+getString(R.string.contact_phone_text)))));

        phoneCopy.setOnClickListener(view ->
                addToClipboard("Phone", R.string.contact_phone_text));

    }

    private String getString(int id) {
        return getContext().getResources().getString(id);
    }

    public Context getContext() {
        return mDialog.getContext();
    }

    private void addToClipboard(String label, int id) {

        ClipboardManager clipboardManager = (ClipboardManager)
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(
                ClipData.newPlainText(label, getString(id)));

        Toast.makeText(getContext(), label + " Copied!", Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("SameParameterValue")
    private void addToClipboard(String label, String text) {

        ClipboardManager clipboardManager = (ClipboardManager)
                getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));

        Toast.makeText(getContext(), label + " Copied!", Toast.LENGTH_SHORT).show();
    }
}
