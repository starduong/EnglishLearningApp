package com.example.englishlearningapp.data.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullname;
    private String birth;
    private String avatar;
    private String accountType;
    private long proExpiryDate;

    // Các loại tài khoản
    public static final String ACCOUNT_TYPE_FREE = "FREE";
    public static final String ACCOUNT_TYPE_PRO = "PRO";
    public static final String ACCOUNT_TYPE_PREMIUM = "PREMIUM";

    public User() {
        this.accountType = ACCOUNT_TYPE_FREE; // Mặc định là FREE
        this.proExpiryDate = 0; // Mặc định chưa có thời hạn
    }

    public User(String id, String username, String password, String email, String phone, String fullname, String birth, String avatar) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.fullname = fullname;
        this.birth = birth;
        this.avatar = avatar;
        this.accountType = ACCOUNT_TYPE_FREE;
        this.proExpiryDate = 0;
    }

    public User(String id, String username, String password, String email, String phone, String fullname, String birth, String avatar, String accountType, long proExpiryDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.fullname = fullname;
        this.birth = birth;
        this.avatar = avatar;
        this.accountType = accountType;
        this.proExpiryDate = proExpiryDate;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public long getProExpiryDate() {
        return proExpiryDate;
    }

    public void setProExpiryDate(long proExpiryDate) {
        this.proExpiryDate = proExpiryDate;
    }

    // Helper methods
    public boolean isProUser() {
        if (ACCOUNT_TYPE_PRO.equals(accountType) || ACCOUNT_TYPE_PREMIUM.equals(accountType)) {
            // Kiểm tra thời hạn nếu có
            if (proExpiryDate > 0) {
                return System.currentTimeMillis() <= proExpiryDate;
            }
            return true; // Pro vĩnh viễn
        }
        return false;
    }

    public boolean isProExpired() {
        if (isProUser() && proExpiryDate > 0) {
            return System.currentTimeMillis() > proExpiryDate;
        }
        return false;
    }

    public void upgradeToPro(long expiryDate) {
        this.accountType = ACCOUNT_TYPE_PRO;
        this.proExpiryDate = expiryDate;
    }

    public void upgradeToPremium(long expiryDate) {
        this.accountType = ACCOUNT_TYPE_PREMIUM;
        this.proExpiryDate = expiryDate;
    }

    public void downgradeToFree() {
        this.accountType = ACCOUNT_TYPE_FREE;
        this.proExpiryDate = 0;
    }

    public String getAccountTypeDisplay() {
        switch (accountType) {
            case ACCOUNT_TYPE_FREE:
                return "Miễn phí";
            case ACCOUNT_TYPE_PRO:
                return "Pro";
            case ACCOUNT_TYPE_PREMIUM:
                return "Premium";
            default:
                return "Miễn phí";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", fullname='" + fullname + '\'' +
                ", birth='" + birth + '\'' +
                ", avatar='" + avatar + '\'' +
                ", accountType='" + accountType + '\'' +
                ", proExpiryDate=" + proExpiryDate +
                '}';
    }
}