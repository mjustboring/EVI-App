package com.spmaurya.exposys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.aabhasjindal.otptextview.OtpTextView;

public class ResetDialog implements DialogInterface.OnShowListener {

    public static final String TAG = ResetDialog.class.getSimpleName();
    private final AlertDialog mDialog;
    private final OtpDialog mOtpDialog;

    private TextInputEditText mEmail;
    private TextInputLayout mEmailLayout;

    private TextInputEditText mPassword;
    private TextInputLayout mPasswordLayout;
    private TextInputEditText mConfirmPassword;
    private TextInputLayout mConfirmPasswordLayout;
    private TextView mSubmit;

    private TextView mFindMe;
    private TextView mNotMe;
    private TextView mNext;
    private final ProgressDialog mProgressDialog;

    private ViewGroup mAccountContainer;
    private ViewGroup mPasswordContainer;

    private final User mUser;

    private TextView mName;
    private TextView mPhone;
    private TextView mStudy;
    private ImageView mAvatar;
    private ImageView mCancel;
    private View mAvatarDimmer;
    private View mAvatarProgress;
    private final Runnable mRunnable;

    public ResetDialog(final Context context, Runnable runnable) {

        mUser = new User();
        mOtpDialog = new OtpDialog(context);

        mRunnable = runnable;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.reset);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnShowListener(this);

        mProgressDialog = new ProgressDialog(mDialog.getContext());
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {

        mEmail = mDialog.findViewById(R.id.reset_email);
        mEmailLayout = mDialog.findViewById(R.id.reset_email_layout);

        mFindMe = mDialog.findViewById(R.id.reset_find_account);
        mNotMe = mDialog.findViewById(R.id.reset_not_me);
        mNext = mDialog.findViewById(R.id.reset_next);

        mName = mDialog.findViewById(R.id.reset_name);
        mPhone = mDialog.findViewById(R.id.reset_phone);
        mStudy = mDialog.findViewById(R.id.reset_study);
        mAvatar = mDialog.findViewById(R.id.reset_avatar);
        mCancel = mDialog.findViewById(R.id.reset_cancel);
        mAvatarDimmer = mDialog.findViewById(R.id.reset_avatar_dimmer);
        mAvatarProgress = mDialog.findViewById(R.id.reset_avatar_progress);

        mPassword = mDialog.findViewById(R.id.reset_password);
        mPasswordLayout = mDialog.findViewById(R.id.reset_password_layout);
        mConfirmPassword = mDialog.findViewById(R.id.reset_confirm_password);
        mConfirmPasswordLayout = mDialog.findViewById(R.id.reset_confirm_password_layout);
        mSubmit = mDialog.findViewById(R.id.reset_submit);

        mAccountContainer = mDialog.findViewById(R.id.reset_account_container);
        mPasswordContainer = mDialog.findViewById(R.id.reset_password_container);

        Validator.setTextWatcher(mEmail, () -> Validator.email(mEmail, mEmailLayout));
        Validator.setTextWatcher(mPassword, () -> Validator.password(mPassword, mPasswordLayout));
        Validator.setTextWatcher(mConfirmPassword, () -> Validator.validateText(
                mConfirmPassword, mConfirmPasswordLayout, "Confirm Password Required!"));

        mFindMe.setOnClickListener(view -> {
            if (!Validator.email(mEmail, mEmailLayout)) {
                Validator.requestFocus(mEmailLayout, mEmail);
                return;
            }
            mUser.setEmail(Objects.requireNonNull(mEmail.getText()).toString().trim());

            if (!isConnected()) return;

            mProgressDialog.setMessage("Wait for a moment...");
            mProgressDialog.show();

            mUser.fetch(new User.OnFetchListener() {

                @Override
                public void onSuccess() {

                    mProgressDialog.dismiss();
                    mName.setText(mUser.getFullName());

                    String phone = mUser.getPhone();
                    int len = phone.length(), h = len >> 1;
                    int st = ((len-h) >> 1) + 2, en = st + h;
                    StringBuilder b = new StringBuilder();
                    b.append(phone.substring(0, st));
                    while (st++ < en) b.append('X');
                    b.append(phone.substring(en));
                    phone = b.toString();

                    mPhone.setText(phone);
                    mStudy.setText(String.format("%s - %s", mUser.getBranch(), mUser.getCollege()));

                    switch (mUser.getGender()) {
                        case "Male": mAvatar.setImageResource(R.drawable.ic_avatar_male); break;
                        case "Female": mAvatar.setImageResource(R.drawable.ic_avatar_female); break;
                        case "Other": mAvatar.setImageResource(R.drawable.ic_avatar_other); break;
                        default: mAvatar.setImageResource(R.drawable.ic_avatar);
                    }

                    if (mUser.getAvatar() != null && isConnected()) {
                        ExecutorService service = Executors.newSingleThreadExecutor();
                        service.submit(() -> {
                            new Handler(Looper.getMainLooper()).post(() -> showAvatarProgress(true));
                            try {
                                URL url = new URL(mUser.getAvatar());
                                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    mAvatar.setImageBitmap(bitmap);
                                    showAvatarProgress(false);
                                });
                            } catch (IOException e) {
                                Log.e(TAG, "init -> service: " + e.getMessage(), e);
                            }
                        });
                        service.shutdown();
                    }

                    mAccountContainer.setVisibility(View.VISIBLE);
                    mFindMe.setVisibility(View.GONE);
                }

                @Override
                public void onNotExist() {
                    mProgressDialog.dismiss();
                    Toast.makeText(mDialog.getContext(),
                            "Email not Found!", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFail() {
                    mProgressDialog.dismiss();
                    Toast.makeText(mDialog.getContext(),
                            "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        mNotMe.setOnClickListener(view -> {
            mAccountContainer.setVisibility(View.GONE);
            mFindMe.setVisibility(View.VISIBLE);
            mEmail.setText("");
            Validator.requestFocus(mEmailLayout, mEmail);
        });

        mNext.setOnClickListener(view -> {

            mOtpDialog.setOnOtpVerificationListener(new OtpDialog.OnOtpVerification() {
                @Override
                public void onSuccess() {
                    mEmailLayout.setVisibility(View.GONE);
                    mAccountContainer.setVisibility(View.GONE);
                    mPasswordContainer.setVisibility(View.VISIBLE);
                }
                @Override
                public void onFailure(String message) {
                    Toast.makeText(mDialog.getContext(), message, Toast.LENGTH_SHORT).show();
                }
            });

            mOtpDialog.sendVerificationCode(mUser.getPhone());
        });

        mSubmit.setOnClickListener(view -> {
            if (Validator.password(mPassword, mPasswordLayout)) {
                String password = Objects.requireNonNull(mPassword.getText()).toString();
                String confirmPassword = Objects.requireNonNull(mConfirmPassword.getText()).toString();

                if (password.equals(confirmPassword)) {
                    mUser.setPassword(password);
                    mProgressDialog.show();

                    mUser.uploadData(new User.OnTaskListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(mDialog.getContext(),
                                    "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                        @Override
                        public void onFail() {
                            Toast.makeText(mDialog.getContext(),
                                    "Something went wrong!", Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    });

                    dismiss();
                } else {
                    mConfirmPasswordLayout.setError("New Password and Confirm Password are not same!");
                    Validator.requestFocus(mConfirmPasswordLayout, mConfirmPassword);
                }
            } else {
                Validator.requestFocus(mPasswordLayout, mPassword);
            }
        });

        mCancel.setOnClickListener(view -> dismiss());
    }

    private void showAvatarProgress(boolean flag) {
        if (flag) {
            mAvatarProgress.setVisibility(View.VISIBLE);
            mAvatarDimmer.setVisibility(View.VISIBLE);
        } else {
            mAvatarProgress.setVisibility(View.GONE);
            mAvatarDimmer.setVisibility(View.GONE);
        }
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                mDialog.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        Toast.makeText(mDialog.getContext(), "No Network!", Toast.LENGTH_SHORT).show();
        return false;
    }
}
