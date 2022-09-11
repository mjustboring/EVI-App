package com.spmaurya.exposys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class OtpDialog implements DialogInterface.OnShowListener {

    private final AlertDialog mDialog;
    private final Context mContext;
    private final FirebaseAuth mAuth;
    private OnOtpVerification mOnOtpVerification;
    private String mPhone;

    private String mVerificationID;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // Below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(@NonNull String verificationID,
                               @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationID, forceResendingToken);
            // When we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            mVerificationID = verificationID;
        }

        // This method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.

                setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("___", "onVerificationFailed: " + e.getMessage());
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        showProgressBar(true);
        // below line is used for getting getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.

        if (!isConnected()) return;

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // if the code is correct and the task is successful
                        // we are sending our user to new activity.
                        if (mOnOtpVerification != null) {
                            mOnOtpVerification.onSuccess();
                        }
                        dismiss();
                    } else {

                        if (mDialog.isShowing()) {
                             OtpTextView otpTextView = mDialog.findViewById(R.id.otp_view);
                             otpTextView.showError();
                        }

                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        if (mOnOtpVerification != null) {
                            mOnOtpVerification.onFailure(message);
                        }
                        Log.d("___", "onComplete: " + message);
                    }
                    showProgressBar(false);
                });
    }

    public OtpDialog(final Context context) {

        mContext = context;

        FirebaseApp.initializeApp(mContext);
        mAuth = FirebaseAuth.getInstance();
        mOnOtpVerification = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.otp);
        mDialog = builder.create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnShowListener(this);
    }

    public void setOnOtpVerificationListener(OnOtpVerification callbacks) {
        this.mOnOtpVerification = callbacks;
    }

    public void show() {
        mDialog.show();
    }

    @Override
    public void onShow(DialogInterface dialogInterface) {

        @NonNull final OtpTextView otpTextView = mDialog.findViewById(R.id.otp_view);
        @NonNull final TextView otpPhone = mDialog.findViewById(R.id.otp_heading_phone);
        @NonNull final TextView otpCancel = mDialog.findViewById(R.id.otp_cancel);

        String phone = mPhone;
        int len = phone.length(), h = len >> 1;
        int st = ((len-h) >> 1) + 2, en = st + h;
        StringBuilder b = new StringBuilder();
        b.append(phone.substring(0, st));
        while (st++ < en) b.append('X');
        b.append(phone.substring(en));
        phone = b.toString();

        otpPhone.setText(phone);

        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {}
            @Override
            public void onOTPComplete(String otp) {
                verifyCode(otp);
            }
        });

        otpCancel.setOnClickListener(tv -> mDialog.cancel());
    }

    private void showProgressBar(boolean flag) {
        if (mDialog.isShowing()) {
            View root = mDialog.findViewById(R.id.otp_root);
            View progressBar = mDialog.findViewById(R.id.otp_progress_bar);
            if (flag) {
                progressBar.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ColorDrawable colorDrawable = new ColorDrawable();
                    colorDrawable.setColor(0xC0000000);
                    root.setForeground(colorDrawable);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ColorDrawable colorDrawable = new ColorDrawable();
                    colorDrawable.setColor(0x00000000);
                    root.setForeground(colorDrawable);
                }
            }
        }
    }

    public void setText(String code) {
        if (mDialog.isShowing()) {
            @NonNull final OtpTextView otpTextView = mDialog.findViewById(R.id.otp_view);
            otpTextView.setOTP(code);
        }
    }

    public void dismiss() {
        showProgressBar(false);
        mDialog.dismiss();
    }

    public interface OnOtpVerification {
        void onSuccess();
        void onFailure(String message);
    }

    public void sendVerificationCode(String phone) {

        if (!Patterns.PHONE.matcher(phone).matches()) {
            Toast.makeText(mContext, "Phone Number is Invalid!", Toast.LENGTH_SHORT).show();
            return;
        }

        mPhone = phone;
        show();
        setText("");

        // This method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mPhone)            // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity((Activity) mContext)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();

        if (!isConnected()) return;

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        Toast.makeText(mContext, "No Network!", Toast.LENGTH_SHORT).show();
        return false;
    }
}
