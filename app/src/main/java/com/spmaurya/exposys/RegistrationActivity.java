package com.spmaurya.exposys;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.hbb20.CountryCodePicker;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    public static final int IMAGE_CAPTURE_REQUEST_CODE = 200;
    private View mHeading;
    private ScrollView mScrollView;

    private Runnable mPendingAnim = null;
    private boolean mIsAnimating = false;
    private boolean mIsVisible = true;
    private int mHeadingHeight;

    private ProgressDialog mDialog;
    private OtpDialog mOtpDialog;

    private ImageView mAvatar;
    private ImageView mAvatarButton;

    private TextInputEditText mFirstName;
    private TextInputLayout mFirstNameLayout;
    private TextInputEditText mLastName;
    private TextInputLayout mLastNameLayout;

    private Spinner mGender;

    private TextInputEditText mBranch;
    private TextInputLayout mBranchLayout;

    private TextInputEditText mCollege;
    private TextInputLayout mCollegeLayout;

    private TextInputEditText mEmail;
    private TextInputLayout mEmailLayout;

    private CountryCodePicker mPhoneCCP;
    private TextInputEditText mPhone;
    private TextInputLayout mPhoneLayout;

    private TextInputEditText mPassword;
    private TextInputLayout mPasswordLayout;

    private TextInputEditText mConfirmPassword;
    private TextInputLayout mConfirmPasswordLayout;

    private TextInputEditText mPercentage10;
    private TextInputLayout mPercentage10Layout;

    private TextInputEditText mPercentage12;
    private TextInputLayout mPercentage12Layout;

    private TextInputEditText mPercentageUG;
    private TextInputLayout mPercentageUGLayout;

    private TextInputEditText mPercentagePG;
    private TextInputLayout mPercentagePGLayout;

    private TextInputEditText mLocation;
    private TextInputLayout mLocationLayout;

    private Spinner mInternship;
    private Spinner mDuration;

    private View mSubmitButton;
    private View mResetButton;

    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);

        mUser = new User();
        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);

        mOtpDialog = new OtpDialog(this);

        mHeading = findViewById(R.id.reg_banner);
        mScrollView = findViewById(R.id.reg_scroll_view);

        mAvatar = findViewById(R.id.reg_avatar);
        mAvatarButton = findViewById(R.id.reg_avatar_button);

        mFirstName = findViewById(R.id.reg_first_name);
        mFirstNameLayout = findViewById(R.id.reg_first_name_layout);
        mLastName = findViewById(R.id.reg_last_name);
        mLastNameLayout = findViewById(R.id.reg_last_name_layout);

        mGender = findViewById(R.id.reg_gender);

        mBranch = findViewById(R.id.reg_branch);
        mBranchLayout = findViewById(R.id.reg_branch_layout);

        mCollege = findViewById(R.id.reg_college);
        mCollegeLayout = findViewById(R.id.reg_college_layout);

        mEmail = findViewById(R.id.reg_email);
        mEmailLayout = findViewById(R.id.reg_email_layout);

        mPhoneCCP = findViewById(R.id.reg_phone_ccp);
        mPhone = findViewById(R.id.reg_phone);
        mPhoneLayout = findViewById(R.id.reg_phone_layout);

        mPassword = findViewById(R.id.reg_password);
        mPasswordLayout = findViewById(R.id.reg_password_layout);

        mConfirmPassword = findViewById(R.id.reg_confirm_password);
        mConfirmPasswordLayout = findViewById(R.id.reg_confirm_password_layout);

        mPercentage10 = findViewById(R.id.reg_10);
        mPercentage10Layout = findViewById(R.id.reg_10_layout);

        mPercentage12 = findViewById(R.id.reg_12);
        mPercentage12Layout = findViewById(R.id.reg_12_layout);

        mPercentageUG = findViewById(R.id.reg_ug);
        mPercentageUGLayout = findViewById(R.id.reg_ug_layout);

        mPercentagePG = findViewById(R.id.reg_pg);
        mPercentagePGLayout = findViewById(R.id.reg_pg_layout);

        mLocation = findViewById(R.id.reg_location);
        mLocationLayout = findViewById(R.id.reg_location_layout);

        mInternship = findViewById(R.id.reg_internship);
        mDuration = findViewById(R.id.reg_internship_duration);

        mSubmitButton = findViewById(R.id.reg_submit_button);
        mResetButton = findViewById(R.id.reg_reset_button);

//        putStubData();

        initListeners();
    }

    private void initListeners() {

        mPhoneCCP.registerCarrierNumberEditText(mPhone);


        // The following feature is conditional
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Getting the Banner height when the views the created to neglect any undesirable outcome
            mHeading.getViewTreeObserver().
                    addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mHeading.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            mHeadingHeight = mHeading.getMeasuredHeight();
                        }
                    });

            // Animator Listener for setting some very useful flag responsible for knowing the current animation state
            final Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
                @Override public void onAnimationCancel(Animator a) {}
                @Override public void onAnimationRepeat(Animator a) {}
                @Override public void onAnimationStart(Animator a) { mIsAnimating = true; }
                @Override public void onAnimationEnd(Animator a) {
                    mIsAnimating = false;

                    // It hold any pending animation
                    // It can happen when another animation should start but another animation is still running
                    // So we will waite to let it finish first then start the new on
                    if (mPendingAnim != null) {
                        mPendingAnim.run();
                        mPendingAnim = null;
                    }
                }
            };

            // It contains the animation code responsible to show the banner
            Runnable show = () -> {
                ValueAnimator animator = ValueAnimator.ofInt(0, mHeadingHeight);
                animator.setDuration(500);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(valueAnimator -> {
                    ViewGroup.LayoutParams params = mHeading.getLayoutParams();
                    params.height = (int) valueAnimator.getAnimatedValue();
                    mHeading.setLayoutParams(params);
                });
                animator.addListener(animListener);
                animator.start();
            };

            // It contains the animation code responsible to hide the banner
            Runnable hide = () -> {
                ValueAnimator animator = ValueAnimator.ofInt(mHeadingHeight, 0);
                animator.setDuration(500);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.addUpdateListener(valueAnimator -> {
                    ViewGroup.LayoutParams params = mHeading.getLayoutParams();
                    params.height = (int) valueAnimator.getAnimatedValue();
                    mHeading.setLayoutParams(params);
                });
                animator.addListener(animListener);
                animator.start();
            };

            // Scroll listener to trigger the hide/show animations when desired scroll happens
            mScrollView.setOnScrollChangeListener((sv, x, y, ox, oy) -> {

                if (!mScrollView.canScrollVertically(-1)) {
                    if (!mIsVisible) {
                        if (mIsAnimating) {
                            mPendingAnim = show;
                        } else {
                            show.run();
                        }
                        mIsVisible = true;
                    }
                } else {
                    if (mIsVisible) {
                        if (mIsAnimating) {
                            mPendingAnim = hide;
                        } else {
                            hide.run();
                        }
                        mIsVisible = false;
                    }
                }
            });
        }

        Validator.setSpinnerListener(mGender, () -> {
            if (mUser.getAvatar() != null) return;
            int avatar;
            switch ((String) mGender.getSelectedItem()) {
                case "Male": avatar = R.drawable.ic_avatar_male; break;
                case "Female": avatar = R.drawable.ic_avatar_female; break;
                case "Other": avatar = R.drawable.ic_avatar_other; break;
                default: avatar = R.drawable.ic_avatar;
            }
            mAvatar.setImageResource(avatar);
        });

        Validator.setTextWatcher(mFirstName, () -> Validator.firstName(mFirstName, mFirstNameLayout));
        Validator.setTextWatcher(mLastName, () -> Validator.lastName(mLastName, mLastNameLayout));
        Validator.setTextWatcher(mBranch, () -> Validator.branch(mBranch, mBranchLayout));
        Validator.setTextWatcher(mCollege, () -> Validator.college(mCollege, mCollegeLayout));
        Validator.setTextWatcher(mEmail, () -> Validator.email(mEmail, mEmailLayout));
        Validator.setTextWatcher(mPhone, () -> Validator.phone(mPhone, mPhoneLayout));
        Validator.setTextWatcher(mPassword, () -> Validator.password(mPassword, mPasswordLayout));
        Validator.setTextWatcher(mConfirmPassword, () -> Validator.validateText(
                mConfirmPassword, mConfirmPasswordLayout, "Confirm Password Required!"));

        Validator.setTextWatcher(mPercentage10, () -> Validator.percentage10(mPercentage10, mPercentage10Layout));
        Validator.setTextWatcher(mPercentage12, () -> Validator.percentage12(mPercentage12, mPercentage12Layout));
        Validator.setTextWatcher(mPercentageUG, () -> Validator.percentageUG(mPercentageUG, mPercentageUGLayout));
        Validator.setTextWatcher(mPercentagePG, () -> Validator.percentagePG(mPercentagePG, mPercentagePGLayout));

        Validator.setSpinnerListener(mInternship, () -> Validator.validateSpinner(mInternship));
        Validator.setTextWatcher(mLocation, () -> Validator.location(mLocation, mLocationLayout));

        Validator.setSpinnerListener(mDuration,
                () -> Validator.validateSpinner(mDuration));

        mAvatarButton.setOnClickListener(iv -> ImagePicker.with(this)
                .cropSquare()
                .compress(256)
                .maxResultSize(512, 512)
                .start(IMAGE_CAPTURE_REQUEST_CODE));

        mSubmitButton.setOnClickListener(v -> {

            if (!isConnected()) return;

            if (!Validator.firstName(mFirstName, mFirstNameLayout)) {
                Validator.requestFocus(mFirstNameLayout, mFirstName);
                return;
            }
            if (!Validator.lastName(mLastName, mLastNameLayout)) {
                Validator.requestFocus(mLastNameLayout, mLastName);
                return;
            }
            if (!Validator.branch(mBranch, mBranchLayout)) {
                Validator.requestFocus(mBranchLayout, mBranch);
                return;
            }
            if (!Validator.college(mCollege, mCollegeLayout)) {
                Validator.requestFocus(mCollegeLayout, mCollege);
                return;
            }
            if (!Validator.email(mEmail, mEmailLayout)) {
                Validator.requestFocus(mEmailLayout, mEmail);
                return;
            }
            if (!Validator.phone(mPhone, mPhoneLayout)) {
                Validator.requestFocus(mPhoneLayout, mPhone);
                return;
            }
            if (!Validator.password(mPassword, mPasswordLayout)) {
                Validator.requestFocus(mPasswordLayout, mPassword);
                return;
            }

            String password = Objects.requireNonNull(mPassword.getText()).toString();
            String confirmPassword = Objects.requireNonNull(mConfirmPassword.getText()).toString();

            if (!password.equals(confirmPassword)) {
                mConfirmPasswordLayout.setError("Password and Confirm Password are not same!");
                Validator.requestFocus(mConfirmPasswordLayout, mConfirmPassword);
                return;
            }

            if (!Validator.percentage10(mPercentage10, mPercentage10Layout)) {
                Validator.requestFocus(mPercentage10Layout, mPercentage10);
                return;
            }
            if (!Validator.percentage12(mPercentage12, mPercentage12Layout)) {
                Validator.requestFocus(mPercentage12Layout, mPercentage12);
                return;
            }
            if (!Validator.percentageUG(mPercentageUG, mPercentageUGLayout)) {
                Validator.requestFocus(mPercentageUGLayout, mPercentageUG);
                return;
            }
            if (!Validator.percentagePG(mPercentagePG, mPercentagePGLayout)) {
                Validator.requestFocus(mPercentagePGLayout, mPercentagePG);
                return;
            }
            if (!Validator.validateSpinner(mInternship)) {
                Validator.requestFocus(mInternship);
                Toast.makeText(this, "Select Internship seeking for", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Validator.location(mLocation, mLocationLayout)) {
                Validator.requestFocus(mLocationLayout, mLocation);
                return;
            }
            if (!Validator.validateSpinner(mDuration)) {
                Validator.requestFocus(mDuration);
                Toast.makeText(this, "Select Duration of Internship", Toast.LENGTH_SHORT).show();
                return;
            }

            mUser.setFirstName(Objects.requireNonNull(mFirstName.getText()).toString().trim());
            mUser.setLastName(Objects.requireNonNull(mLastName.getText()).toString().trim());
            if (mGender.getSelectedItemPosition() != 0) {
                mUser.setGender((String) mGender.getSelectedItem());
            }
            mUser.setBranch(Objects.requireNonNull(mBranch.getText()).toString().trim());
            mUser.setCollege(Objects.requireNonNull(mCollege.getText()).toString().trim());
            mUser.setEmail(Objects.requireNonNull(mEmail.getText()).toString().trim());
            mUser.setPhone(Objects.requireNonNull(mPhoneCCP.getFullNumberWithPlus()).trim());
            mUser.setPassword(Objects.requireNonNull(mPassword.getText()).toString());
            mUser.setPercentage10(Objects.requireNonNull(mPercentage10.getText()).toString().trim());
            mUser.setPercentage12(Objects.requireNonNull(mPercentage12.getText()).toString().trim());
            mUser.setPercentageUG(Objects.requireNonNull(mPercentageUG.getText()).toString().trim());
            mUser.setPercentagePG(Objects.requireNonNull(mPercentagePG.getText()).toString().trim());
            mUser.setInternship((String) mInternship.getSelectedItem());
            mUser.setLocation(Objects.requireNonNull(mLocation.getText()).toString().trim());
            mUser.setDuration((String) mDuration.getSelectedItem());

            if (!isConnected()) return;

            mOtpDialog.setOnOtpVerificationListener(new OtpDialog.OnOtpVerification() {
                @Override
                public void onSuccess() {

                    mDialog.setMessage("Uploading avatar...");
                    mDialog.show();
                    mUser.uploadAvatar(new User.OnTaskListener() {

                        @Override
                        public void onSuccess() {

                            mDialog.setMessage("Uploading data...");
                            mUser.uploadData(new User.OnTaskListener() {

                                @Override
                                public void onSuccess() {

                                    mDialog.dismiss();

                                    Toast.makeText(RegistrationActivity.this,
                                            "You're successfully registered.", Toast.LENGTH_SHORT).show();

                                    Log.d("___", "onSuccess: " + mUser.getAvatar());

                                    Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                    intent.putExtra("user", mUser.toBundle());
                                    intent.putExtra("comingFrom", 1);
                                    startActivity(intent);
                                    finishAffinity();
                                }

                                @Override
                                public void onFail() {
                                    mDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this,
                                            "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        @Override
                        public void onFail() {
                            mDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this,
                                    "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(
                            RegistrationActivity.this, message, Toast.LENGTH_LONG).show();
                }
            });

            mDialog.setMessage("Wait for a moment...");
            mDialog.show();

            mUser.exists(new User.OnExistListener() {

                @Override
                public void onExist() {
                    mDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,
                            "Email is already registered!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNotExist() {
                    mDialog.dismiss();
                    mOtpDialog.sendVerificationCode(mUser.getPhone());
                }
                @Override
                public void onFail() {
                    mDialog.dismiss();
                    Toast.makeText(RegistrationActivity.this,
                            "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        mResetButton.setOnClickListener(v -> {

            mAvatar.setImageResource(R.drawable.ic_avatar);
            mUser.setAvatar(null);

            mFirstName.setText(null);
            mFirstNameLayout.setError(null);
            mLastName.setText(null);
            mLastNameLayout.setError(null);

            mGender.setSelection(0);

            mBranch.setText(null);
            mBranchLayout.setError(null);

            mCollege.setText(null);
            mCollegeLayout.setError(null);

            mEmail.setText(null);
            mEmailLayout.setError(null);

            mPhone.setText(null);
            mPhoneLayout.setError(null);

            mPassword.setText(null);
            mPasswordLayout.setError(null);

            mConfirmPassword.setText(null);
            mConfirmPasswordLayout.setError(null);

            mPercentage10.setText(null);
            mPercentage10Layout.setError(null);

            mPercentage12.setText(null);
            mPercentage12Layout.setError(null);

            mPercentageUG.setText(null);
            mPercentageUGLayout.setError(null);

            mPercentagePG.setText(null);
            mPercentagePGLayout.setError(null);

            mLocation.setText(null);
            mLocationLayout.setError(null);

            mInternship.setSelection(0);
            mInternship.setSelected(false);
            mDuration.setSelection(0);
            mDuration.setSelected(false);

            mFirstNameLayout.setErrorEnabled(false);
            mLastNameLayout.setErrorEnabled(false);
            mBranchLayout.setErrorEnabled(false);
            mCollegeLayout.setErrorEnabled(false);
            mEmailLayout.setErrorEnabled(false);
            mPhoneLayout.setErrorEnabled(false);
            mPasswordLayout.setErrorEnabled(false);
            mConfirmPasswordLayout.setErrorEnabled(false);
            mPercentage10Layout.setErrorEnabled(false);
            mPercentage12Layout.setErrorEnabled(false);
            mPercentageUGLayout.setErrorEnabled(false);
            mPercentagePGLayout.setErrorEnabled(false);
            mLocationLayout.setErrorEnabled(false);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE
                && resultCode == RESULT_OK && data != null) {
            if (data.getData() != null) {
                mUser.setAvatarUri(data.getData());
                mAvatar.setImageURI(data.getData());
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isConnected() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        Toast.makeText(this, "No Network!", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*
        // Sample stub data for debugging purpose...

        @SuppressLint("SetTextI18n")
        private void putStubData() {
            mFirstName.setText("Sp");
            mLastName.setText("Maurya");
            mGender.setSelection(1);
            mBranch.setText("B.Tech (Computer Science)");
            mCollege.setText("MIT University");
            mEmail.setText("spm.kvmrn@yahoo.com");
            mPhone.setText("8171247915");
            mPassword.setText("Spm123@expo");
            mConfirmPassword.setText("Spm123@expo");
            mPercentage10.setText("86");
            mPercentage12.setText("78");
            mPercentageUG.setText("80");
            mPercentagePG.setText("100");
            mInternship.setSelection(6);
            mLocation.setText("Mathura");
            mDuration.setSelection(3);
        }
     */
}