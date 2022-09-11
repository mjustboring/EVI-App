package com.spmaurya.exposys;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

@SuppressWarnings("unused")
@IgnoreExtraProperties
public final class User {

    @Exclude private static final String TAG = User.class.getSimpleName();
    @Exclude private static final HashMap<Character, Character> CHARSET = new HashMap<>();

    private String avatar = null;
    private String firstName = null;
    private String lastName = null;
    private String gender = null;
    private String branch = null;
    private String college = null;
    private String email = null;
    private String phone = null;
    private String password = null;
    private String percentage10 = null;
    private String percentage12 = null;
    private String percentageUG = null;
    private String percentagePG = null;
    private String internship = null;
    private String location = null;
    private String duration = null;

    public User() {}

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Exclude
    public void setAvatarUri(Uri avatar) {
        this.avatar = avatar.toString();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Exclude
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPercentage10() {
        return percentage10;
    }

    public void setPercentage10(String percentage10) {
        this.percentage10 = percentage10;
    }

    public String getPercentage12() {
        return percentage12;
    }

    public void setPercentage12(String percentage12) {
        this.percentage12 = percentage12;
    }

    public String getPercentageUG() {
        return percentageUG;
    }

    public void setPercentageUG(String percentageUG) {
        this.percentageUG = percentageUG;
    }

    public String getPercentagePG() {
        return percentagePG;
    }

    public void setPercentagePG(String percentagePG) {
        this.percentagePG = percentagePG;
    }

    public String getInternship() {
        return internship;
    }

    public void setInternship(String internship) {
        this.internship = internship;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Exclude
    public String getUserID() {

        if (email == null) return null;

        if (CHARSET.isEmpty()) {
            char v = 'A';
            for (char c = '0'; c <= '9'; c++) {
                CHARSET.put(c, c);
            }
            for (char c = 'a'; c <= 'z'; c++) {
                CHARSET.put(c, c);
            }
            for (char c : ".!#$%&'*+-/=?^_`{}|@,:;<>\"".toCharArray()) {
                CHARSET.put(c, v++);
            }
        }

        StringBuilder builder = new StringBuilder();
        for (char c : email.toCharArray()) {
            builder.append(CHARSET.get(c));
        }

        return builder.toString();
    }

    @Exclude
    public HashMap<String, Object> toHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("avatar", avatar);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("gender", gender);
        map.put("branch", branch);
        map.put("college", college);
        map.put("email", email);
        map.put("phone", phone);
        map.put("password", password);
        map.put("percentage10", percentage10);
        map.put("percentage12", percentage12);
        map.put("percentageUG", percentageUG);
        map.put("percentagePG", percentagePG);
        map.put("internship", internship);
        map.put("location", location);
        map.put("duration", duration);
        return map;
    }

    @Exclude
    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("avatar", avatar);
        bundle.putString("firstName", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("gender", gender);
        bundle.putString("branch", branch);
        bundle.putString("college", college);
        bundle.putString("email", email);
        bundle.putString("phone", phone);
        bundle.putString("password", password);
        bundle.putString("percentage10", percentage10);
        bundle.putString("percentage12", percentage12);
        bundle.putString("percentageUG", percentageUG);
        bundle.putString("percentagePG", percentagePG);
        bundle.putString("internship", internship);
        bundle.putString("location", location);
        bundle.putString("duration", duration);
        return bundle;
    }

    @Exclude
    static public Bundle getFrom(SharedPreferences preferences) {
        Bundle bundle = new Bundle();
        bundle.putString("avatar", preferences.getString("avatar", ""));
        bundle.putString("firstName", preferences.getString("firstName", ""));
        bundle.putString("lastName", preferences.getString("lastName", ""));
        bundle.putString("gender", preferences.getString("gender", ""));
        bundle.putString("branch", preferences.getString("branch", ""));
        bundle.putString("college", preferences.getString("college", ""));
        bundle.putString("email", preferences.getString("email", ""));
        bundle.putString("phone", preferences.getString("phone", ""));
        bundle.putString("password", preferences.getString("password", ""));
        bundle.putString("percentage10", preferences.getString("percentage10", ""));
        bundle.putString("percentage12", preferences.getString("percentage12", ""));
        bundle.putString("percentageUG", preferences.getString("percentageUG", ""));
        bundle.putString("percentagePG", preferences.getString("percentagePG", ""));
        bundle.putString("internship", preferences.getString("internship", ""));
        bundle.putString("location", preferences.getString("location", ""));
        bundle.putString("duration", preferences.getString("duration", ""));
        return bundle;
    }

    @Exclude
    static public void putTo(SharedPreferences preferences, Bundle bundle) {
        if (bundle == null) return;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("avatar", bundle.getString("avatar", ""));
        editor.putString("firstName", bundle.getString("firstName", ""));
        editor.putString("lastName", bundle.getString("lastName", ""));
        editor.putString("gender", bundle.getString("gender", ""));
        editor.putString("branch", bundle.getString("branch", ""));
        editor.putString("college", bundle.getString("college", ""));
        editor.putString("email", bundle.getString("email", ""));
        editor.putString("phone", bundle.getString("phone", ""));
        editor.putString("password", bundle.getString("password", ""));
        editor.putString("percentage10", bundle.getString("percentage10", ""));
        editor.putString("percentage12", bundle.getString("percentage12", ""));
        editor.putString("percentageUG", bundle.getString("percentageUG", ""));
        editor.putString("percentagePG", bundle.getString("percentagePG", ""));
        editor.putString("internship", bundle.getString("internship", ""));
        editor.putString("location", bundle.getString("location", ""));
        editor.putString("duration", bundle.getString("duration", ""));
        editor.apply();
    }

    @Exclude
    static public void putToClear(SharedPreferences preferences) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("avatar");
        editor.remove("firstName");
        editor.remove("lastName");
        editor.remove("gender");
        editor.remove("branch");
        editor.remove("college");
        editor.remove("email");
        editor.remove("phone");
        editor.remove("password");
        editor.remove("percentage10");
        editor.remove("percentage12");
        editor.remove("percentageUG");
        editor.remove("percentagePG");
        editor.remove("internship");
        editor.remove("location");
        editor.remove("duration");
        editor.apply();
    }

    @Exclude
    public void exists(@NonNull OnExistListener onExistListener) {

        String userID = getUserID();
        if (userID == null) return;

        final DatabaseReference users = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(userID);

        users.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                onExistListener.onExist();
            } else {
                onExistListener.onNotExist();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "exists: Failed -> " + e.getMessage(), e);
            onExistListener.onFail();
        });
    }

    @Exclude
    public void uploadAvatar(@NonNull OnTaskListener onTaskListener) {

        String userID = getUserID();
        if (userID == null) return;

        StorageReference userProfiles = FirebaseStorage
                .getInstance()
                .getReference()
                .child("userProfiles")
                .child(userID);

        if (avatar == null) {
            onTaskListener.onSuccess();
            return;
        }

        UploadTask uploadTask = userProfiles.putFile(Uri.parse(getAvatar()));

        uploadTask.addOnSuccessListener(taskSnapshot -> userProfiles.getDownloadUrl()
                .addOnSuccessListener(downloadUrl -> {
                    setAvatarUri(downloadUrl);
                    onTaskListener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadAvatar: " +
                            "URL Retrieval Failed -> " + e.getMessage(), e);
                    onTaskListener.onFail();
                }))
        .addOnFailureListener(e -> {
            Log.e(TAG, "uploadAvatar: Failed -> " + e.getMessage(), e);
            onTaskListener.onFail();
        });
    }

    @Exclude
    public void fetch(@NonNull OnLoginListener onLoginListener) {

        String userID = getUserID();
        if (userID == null) return;

        final DatabaseReference users = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(userID);

        users.get().addOnSuccessListener(dataSnapshot -> {
            User user;
            if (dataSnapshot.exists() && (user = dataSnapshot.getValue(User.class)) != null) {
                if (user.getPassword().equals(password)) {
                    updateFrom(user);
                    onLoginListener.onSuccess();
                } else {
                    onLoginListener.onIncorrect();
                }
            } else {
                onLoginListener.onNotExist();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "fetch: Failed -> " + e.getMessage(), e);
            onLoginListener.onFail();
        });
    }

    @Exclude
    public void fetch(@NonNull OnFetchListener onFetchListener) {

        String userID = getUserID();
        if (userID == null) return;

        final DatabaseReference users = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(userID);

        users.get().addOnSuccessListener(dataSnapshot -> {
            User user;
            if (dataSnapshot.exists() && (user = dataSnapshot.getValue(User.class)) != null) {
                updateFrom(user);
                onFetchListener.onSuccess();
            } else {
                onFetchListener.onNotExist();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "fetch: Failed -> " + e.getMessage(), e);
            onFetchListener.onFail();
        });
    }

    @Exclude
    public void updateFrom(User user) {

        if (user == null) return;

        avatar = user.getAvatar() == null ? avatar : user.getAvatar();
        firstName = user.getFirstName() == null ? firstName : user.getFirstName();
        lastName = user.getLastName() == null ? lastName : user.getLastName();
        gender = user.getGender() == null ? gender : user.getGender();
        branch = user.getBranch() == null ? branch : user.getBranch();
        college = user.getCollege() == null ? college : user.getCollege();
        email = user.getEmail() == null ? email : user.getEmail();
        phone = user.getPhone() == null ? phone : user.getPhone();
        password = user.getPassword() == null ? password : user.getPassword();
        percentage10 = user.getPercentage10() == null ? percentage10 : user.getPercentage10();
        percentage12 = user.getPercentage12() == null ? percentage12 : user.getPercentage12();
        percentageUG = user.getPercentageUG() == null ? percentageUG : user.getPercentageUG();
        percentagePG = user.getPercentagePG() == null ? percentagePG : user.getPercentagePG();
        internship = user.getInternship() == null ? internship : user.getInternship();
        location = user.getLocation() == null ? location : user.getLocation();
        duration = user.getDuration() == null ? duration : user.getDuration();
    }

    @Exclude
    public void updateFrom(Bundle bundle) {

        if (bundle == null) return;

        avatar = bundle.getString("avatar") == null ? avatar : bundle.getString("avatar");
        firstName = bundle.getString("firstName") == null ? firstName : bundle.getString("firstName");
        lastName = bundle.getString("lastName") == null ? lastName : bundle.getString("lastName");
        gender = bundle.getString("gender") == null ? gender : bundle.getString("gender");
        branch = bundle.getString("branch") == null ? branch : bundle.getString("branch");
        college = bundle.getString("college") == null ? college : bundle.getString("college");
        email = bundle.getString("email") == null ? email : bundle.getString("email");
        phone = bundle.getString("phone") == null ? phone : bundle.getString("phone");
        password = bundle.getString("password") == null ? password : bundle.getString("password");
        percentage10 = bundle.getString("percentage10") == null ? percentage10 : bundle.getString("percentage10");
        percentage12 = bundle.getString("percentage12") == null ? percentage12 : bundle.getString("percentage12");
        percentageUG = bundle.getString("percentageUG") == null ? percentageUG : bundle.getString("percentageUG");
        percentagePG = bundle.getString("percentagePG") == null ? percentagePG : bundle.getString("percentagePG");
        internship = bundle.getString("internship") == null ? internship : bundle.getString("internship");
        location = bundle.getString("location") == null ? location : bundle.getString("location");
        duration = bundle.getString("duration") == null ? duration : bundle.getString("duration");
    }

    @Exclude
    public void uploadData(@NonNull OnTaskListener onTaskListener) {

        String userID = getUserID();
        if (userID == null) return;

        DatabaseReference users = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("users")
                .child(userID);

        users.setValue(this)
                .addOnSuccessListener(unused -> onTaskListener.onSuccess())
                .addOnFailureListener(e -> {
                    Log.e(TAG, "uploadData: Failed -> " + e.getMessage(), e);
                    onTaskListener.onFail();
                });
    }

    public interface OnExistListener {
        void onExist();
        void onNotExist();
        void onFail();
    }

    public interface OnLoginListener {
        void onIncorrect();
        void onNotExist();
        void onSuccess();
        void onFail();
    }

    public interface OnFetchListener {
        void onNotExist();
        void onSuccess();
        void onFail();
    }

    public interface OnTaskListener {
        void onSuccess();
        void onFail();
    }
}
