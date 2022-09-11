package com.spmaurya.exposys;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public final class Validator {

    public static final int PERCENTAGE_LOWER_BOUND = 33;
    public static final int PERCENTAGE_UPPER_BOUND = 100;

    public static boolean validateText(TextInputEditText editText, TextInputLayout layout, String errorText) {
        if (editText.getText() == null || editText.getText().toString().trim().isEmpty()) {
            layout.setError(errorText);
            return false;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
        return true;
    }

    public static boolean firstName(TextInputEditText editText, TextInputLayout layout) {
        return validateText(editText, layout, "First Name is required!");
    }

    public static boolean lastName(TextInputEditText editText, TextInputLayout layout) {
        return validateText(editText, layout, "Last Name is required!");
    }

    public static boolean branch(TextInputEditText editText, TextInputLayout layout) {
        return validateText(editText, layout, "Branch Name is required!");
    }

    public static boolean college(TextInputEditText editText, TextInputLayout layout) {
        return validateText(editText, layout, "College Name is required!");
    }

    public static boolean email(TextInputEditText editText, TextInputLayout layout) {
        String text = "";
        if (editText.getText() != null) {
            text = editText.getText().toString().trim().toLowerCase();
        }
        if (text.isEmpty()) {
            layout.setError("Email is required!");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(text).matches())  {
            layout.setError("Please enter a valid email!");
            return false;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
        return true;
    }

    public static boolean phone(TextInputEditText editText, TextInputLayout layout) {
        String text = "";
        if (editText.getText() != null) {
            text = editText.getText().toString().trim();
        }
        if (text.isEmpty()) {
            layout.setError("Phone number is required!");
            return false;
        }
        if (!Patterns.PHONE.matcher(text).matches()) {
            layout.setError("Please enter a valid phone number!");
            return false;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
        return true;
    }

    public static boolean password(TextInputEditText editText, TextInputLayout layout) {
        String text = "";
        if (editText.getText() != null) {
            text = editText.getText().toString();
        }
        if (text.isEmpty()) {
            layout.setError("Enter Password!");
            return false;
        }
        boolean [] flag = new boolean[5];
        flag[0] = (text.length() >= 8);
        for (char c : text.toCharArray()) {
            flag[1] |= (c >= 'a' && c <= 'z');
            flag[2] |= (c >= 'A' && c <= 'Z');
            flag[3] |= (c >= '0' && c <= '9');
            for (char s : " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~".toCharArray()) {
                if (flag[4] |= (c == s)) break;
            }
        }
        String message = "";
        message += (flag[0] ? "" : "Password length should be at least 8");
        if (!flag[1] && !message.isEmpty()) message += "\n";
        message += (flag[1] ? "" : "Password should contain a lowercase character");
        if (!flag[2] && !message.isEmpty()) message += "\n";
        message += (flag[2] ? "" : "Password should contain an uppercase character");
        if (!flag[3] && !message.isEmpty()) message += "\n";
        message += (flag[3] ? "" : "Password should contain a number");
        if (!flag[4] && !message.isEmpty()) message += "\n";
        message += (flag[4] ? "" : "Password should contain a special character");
        if (!message.isEmpty()) {
            layout.setError(message);
            Log.d("___", "password: " + message);
            return false;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
        return true;
    }

    public static boolean validatePercentage(TextInputEditText editText, TextInputLayout layout, String name) {
        String text = "";
        if (editText.getText() != null) {
            text = editText.getText().toString().trim();
        }
        if (text.isEmpty()) {
            if (name != null) {
                layout.setError(name + " percentage is required!");
                return false;
            }
            layout.setError(null);
            layout.setErrorEnabled(false);
            return true;
        }
        double percentage = Double.parseDouble(text);
        if (percentage < PERCENTAGE_LOWER_BOUND) {
            layout.setError("Percentage is lower then required!");
            return false;
        }
        if (percentage > PERCENTAGE_UPPER_BOUND) {
            layout.setError("Please enter a valid percentage!");
            return false;
        }
        layout.setError(null);
        layout.setErrorEnabled(false);
        return true;
    }

    public static boolean percentage10(TextInputEditText editText, TextInputLayout layout) {
        return validatePercentage(editText, layout, "10ᵗʰ");
    }

    public static boolean percentage12(TextInputEditText editText, TextInputLayout layout) {
        return validatePercentage(editText, layout, "12ᵗʰ");
    }

    public static boolean percentageUG(TextInputEditText editText, TextInputLayout layout) {
        return validatePercentage(editText, layout, "UG");
    }

    public static boolean percentagePG(TextInputEditText editText, TextInputLayout layout) {
        return validatePercentage(editText, layout, null);
    }

    public static boolean location(TextInputEditText editText, TextInputLayout layout) {
        return validateText(editText, layout, "Location is Required!");
    }

    public static boolean validateSpinner(Spinner spinner) {
        if (spinner.getSelectedItemPosition() == 0) {
            spinner.setBackgroundResource(R.drawable.spinner_error_bg);
            return false;
        }
        spinner.setBackgroundResource(R.drawable.spinner_bg);
        return true;
    }

    public static void setTextWatcher(TextInputEditText editText, Runnable runnable) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence cs, int i, int i1, int i2) {}
            @Override public void onTextChanged(CharSequence cs, int i, int i1, int i2) {}
            @Override public void afterTextChanged(Editable editable) {
                runnable.run();
            }
        });
    }

    public static void setSpinnerListener(Spinner spinner, Runnable runnable) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> av, View v, int pos, long id) {
                runnable.run();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    public static void requestFocus(View... views) {
        for (View view : views) {
            if (!view.isFocusable()) {
                view.setFocusable(true);
            }
            if (!view.isFocusableInTouchMode()) {
                view.setFocusableInTouchMode(true);
            }
            view.clearFocus();
            view.requestFocusFromTouch();
            view.requestFocus();
        }
    }
}
