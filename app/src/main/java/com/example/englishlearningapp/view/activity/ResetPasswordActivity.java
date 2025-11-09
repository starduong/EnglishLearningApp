package com.example.englishlearningapp.view.activity;

import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.util.PasswordUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ResetPasswordActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText etPhone, etOTP, etNewPassword, etConfirmPassword;
    private Button btnSendOTP, btnResetPassword;
    private TextView tvCountdown, tvMessage;

    // Data Components
    private UserDAO userDAO;
    private FirebaseAuth mAuth;
    private String verificationId;
    private CountDownTimer countDownTimer;
    private long lastSentTime = 0;

    private static final String TAG = "ResetPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        // Edge-to-edge setup
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initData();
        setupClickListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.etPhone);
        etOTP = findViewById(R.id.etOTP);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvMessage = findViewById(R.id.tvMessage);
    }

    private void initData() {
        userDAO = new UserDAO(this);
        mAuth = FirebaseAuth.getInstance();
        hideMessage();
    }

    private void setupClickListeners() {
        btnSendOTP.setOnClickListener(v -> sendOTP());
        btnResetPassword.setOnClickListener(v -> verifyAndResetPassword());
    }

    // ========== BƯỚC 1: GỬI OTP ==========
    private void sendOTP() {
        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showMessage("Vui lòng nhập số điện thoại", true);
            return;
        }

        // Chống spam: chỉ cho gửi lại sau 30s
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSentTime < 30_000) {
            long remaining = (30_000 - (currentTime - lastSentTime)) / 1000;
            showMessage("Vui lòng đợi " + remaining + " giây trước khi gửi lại!", true);
            return;
        }

        // Kiểm tra số điện thoại có trong DB không
        if (!isPhoneExists(phone)) {
            showMessage("Số điện thoại chưa được đăng ký!", true);
            return;
        }

        String formattedPhone = formatPhone(phone);
        showMessage("Đang gửi mã OTP...", false);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(formattedPhone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
        lastSentTime = currentTime;
    }

    // ========== CALLBACKS FIREBASE ==========
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    Log.d(TAG, "onVerificationCompleted");
                    verifyOTP(credential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Log.e(TAG, "onVerificationFailed", e);
                    String errorMsg = "Lỗi gửi OTP: ";
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        errorMsg += "Số điện thoại không hợp lệ";
                    } else {
                        errorMsg += e.getMessage();
                    }
                    showMessage(errorMsg, true);
                }

                @Override
                public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    Log.d(TAG, "onCodeSent");
                    verificationId = id;
                    showMessage("Mã OTP đã gửi thành công!", false);
                    showOTPFields();
                    startCountdown();
                }
            };

    // ========== HIỂN THỊ/ẨN Ô OTP ==========
    private void showOTPFields() {
        etOTP.setVisibility(View.VISIBLE);
        etNewPassword.setVisibility(View.VISIBLE);
        etConfirmPassword.setVisibility(View.VISIBLE);
        btnResetPassword.setVisibility(View.VISIBLE);
        btnSendOTP.setVisibility(View.GONE);
        tvCountdown.setVisibility(View.VISIBLE);
        etPhone.setEnabled(false); // Khóa số điện thoại
    }

    private void hideOTPFields() {
        etOTP.setVisibility(View.GONE);
        etNewPassword.setVisibility(View.GONE);
        etConfirmPassword.setVisibility(View.GONE);
        btnResetPassword.setVisibility(View.GONE);
        btnSendOTP.setVisibility(View.VISIBLE);
        tvCountdown.setVisibility(View.GONE);
        etPhone.setEnabled(true);
        verificationId = null;
    }

    // ========== ĐẾM NGƯỢC 60 GIÂY ==========
    private void startCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(60_000, 1_000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                tvCountdown.setText("Mã OTP hết hạn sau: " + seconds + "s");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Mã OTP đã hết hạn!");
                tvCountdown.setVisibility(View.GONE);
                hideOTPFields();
                showMessage("Mã OTP đã hết hạn. Vui lòng gửi lại!", true);
            }
        }.start();
    }

    // ========== XÁC NHẬN + ĐẶT LẠI MẬT KHẨU ==========
    private void verifyAndResetPassword() {
        String otp = Objects.requireNonNull(etOTP.getText()).toString().trim();
        String newPass = Objects.requireNonNull(etNewPassword.getText()).toString().trim();
        String confirmPass = Objects.requireNonNull(etConfirmPassword.getText()).toString().trim();

        // Validate OTP
        if (otp.length() != 6) {
            showMessage("Mã OTP phải có 6 số", true);
            return;
        }

        // Validate mật khẩu
        if (newPass.length() < 6) {
            showMessage("Mật khẩu phải có ít nhất 6 ký tự", true);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showMessage("Xác nhận mật khẩu không khớp", true);
            return;
        }

        // Xác minh OTP
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        verifyOTP(credential);
    }

    private void verifyOTP(PhoneAuthCredential credential) {
        showMessage("Đang xác minh...", false);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "OTP verified successfully");
                        // Cập nhật mật khẩu mới (đã mã hóa)
                        String phone = Objects.requireNonNull(etPhone.getText()).toString().trim();
                        String cleanPhone = phone.replace("+84", "").replaceFirst("^0", "");
                        String newPassword = Objects.requireNonNull(etNewPassword.getText()).toString().trim();
                        String hashedPassword = PasswordUtils.hash(newPassword);

                        if (updatePassword(cleanPhone, hashedPassword)) {
                            showMessage("Đặt lại mật khẩu thành công!", false);
                            Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_LONG).show();
                            if (countDownTimer != null) countDownTimer.cancel();
                            finish(); // Quay lại Login
                        } else {
                            showMessage("Lỗi cập nhật mật khẩu", true);
                        }
                    } else {
                        Log.e(TAG, "OTP verification failed", task.getException());
                        String errorMsg = "Mã OTP không đúng! ";
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMsg += "Vui lòng kiểm tra lại mã OTP.";
                        } else {
                            errorMsg += Objects.requireNonNull(task.getException()).getMessage();
                        }
                        showMessage(errorMsg, true);
                    }
                });
    }

    // ========== USERDAO INTEGRATION ==========

    /**
     * Kiểm tra số điện thoại có tồn tại trong DB không bằng cách gọi UserDAO
     */
    private boolean isPhoneExists(String phone) {
        try {
            return userDAO.isPhoneExists(formatPhone(phone));
        } catch (Exception e) {
            Log.e(TAG, "isPhoneExists error", e);
            return false;
        }
    }

    /**
     * Cập nhật mật khẩu (đã mã hóa) bằng cách gọi UserDAO
     */
    private boolean updatePassword(String phone, String hashedPassword) {
        try {
            return userDAO.updatePasswordByPhone(phone, hashedPassword);
        } catch (SQLException e) {
            Log.e(TAG, "updatePassword error", e);
            return false;
        }
    }

    // ========== HELPER METHODS ==========
    private String formatPhone(String phone) {
        if (phone.startsWith("+84")) {
            return phone;
        }
        if (phone.startsWith("0")) {
            phone = phone.replaceFirst("^0", "");
            phone = "+84" + phone;
        } else {
            phone = "+84" + phone;
        }
        return phone;
    }

    private void showMessage(String message, boolean isError) {
        tvMessage.setText(message);
        tvMessage.setTextColor(getResources().getColor(
                isError ? android.R.color.holo_red_dark : android.R.color.holo_green_dark, null
        ));
        tvMessage.setVisibility(View.VISIBLE);
    }

    private void hideMessage() {
        tvMessage.setVisibility(View.GONE);
    }

    // ========== LIFECYCLE ==========
    @Override
    protected void onDestroy() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (userDAO != null) {
            userDAO.close();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onPause();
    }
}
