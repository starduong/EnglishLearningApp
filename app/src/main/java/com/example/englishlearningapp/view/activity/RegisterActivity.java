package com.example.englishlearningapp.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.util.PasswordUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private UserDAO userDAO;
    private TextInputLayout emailLayout, phoneLayout, fullNameLayout, birthLayout, usernameLayout, passwordLayout, confirmPasswordLayout;
    private TextInputEditText edtEmail, edtPhone, edtFullName, edtBirth, edtUsername, edtPassword, edtConfirmPassword;
    private Button btnRegister;
    private CheckBox termsCheckbox;
    private TextView txtSignIn;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final Calendar birthCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        event();
    }

    private void init() {
        userDAO = new UserDAO(this);

        // Layouts
        emailLayout = findViewById(R.id.email_input_layout);
        phoneLayout = findViewById(R.id.phone_input_layout);
        fullNameLayout = findViewById(R.id.fullName_input_layout);
        birthLayout = findViewById(R.id.birth_input_layout);
        usernameLayout = findViewById(R.id.username_input_layout);
        passwordLayout = findViewById(R.id.password_input_layout);
        confirmPasswordLayout = findViewById(R.id.confirm_password_input_layout);

        // EditTexts
        edtEmail = findViewById(R.id.email);
        edtPhone = findViewById(R.id.phone);
        edtFullName = findViewById(R.id.fullName);
        edtBirth = findViewById(R.id.birth);
        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);
        edtConfirmPassword = findViewById(R.id.confirm_password);

        // Others
        btnRegister = findViewById(R.id.btnRegister);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        txtSignIn = findViewById(R.id.sign_in);

        // Prevent keyboard input for birth
        edtBirth.setFocusable(false);
        edtBirth.setClickable(true);
    }

    private void event() {
        edtBirth.setOnClickListener(v -> showDatePickerDialog());
        btnRegister.setOnClickListener(v -> {
            if (validateAll()) {
                performRegister();
            }
        });
        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showDatePickerDialog() {
        int year = birthCalendar.get(Calendar.YEAR);
        int month = birthCalendar.get(Calendar.MONTH);
        int day = birthCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    birthCalendar.set(y, m, d);
                    edtBirth.setText(dateFormat.format(birthCalendar.getTime()));
                    birthLayout.setError(null);
                },
                year, month, day
        );
        // Giới hạn ngày tối đa là hôm nay
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private boolean validateAll() {
        boolean valid = true;
        resetErrors();

        String email = getText(edtEmail);
        String phone = getText(edtPhone);
        String fullName = getText(edtFullName);
        String birth = getText(edtBirth);
        String username = getText(edtUsername);
        String password = getText(edtPassword);
        String confirmPassword = getText(edtConfirmPassword);

        // Email
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Email không được để trống!");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Email không hợp lệ!");
            valid = false;
        } else if (!isEmailAvailable(email)) {
            emailLayout.setError("Email đã tồn tại!");
            valid = false;
        }

        // Phone
        if (TextUtils.isEmpty(phone)) {
            phoneLayout.setError("Số điện thoại không được để trống!");
            valid = false;
        } else {
            String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
            String digits = formatPhone(cleanPhone);
            if (!digits.matches("^\\+?\\d+$")) {
                phoneLayout.setError("Chỉ được chứa số!");
                valid = false;
            } else if (digits.length() < 8 || digits.length() > 15) {
                phoneLayout.setError("Độ dài không hợp lệ!");
                valid = false;
            }
        }

        // FullName
        if (TextUtils.isEmpty(fullName)) {
            fullNameLayout.setError("Họ tên không được để trống!");
            valid = false;
        }

        // Birth
        if (TextUtils.isEmpty(birth)) {
            birthLayout.setError("Vui lòng chọn ngày sinh!");
            valid = false;
        }

        // Username
        if (TextUtils.isEmpty(username)) {
            usernameLayout.setError("Username không được để trống!");
            valid = false;
        } else if (username.length() < 4) {
            usernameLayout.setError("Tối thiểu 4 ký tự!");
            valid = false;
        } else if (!isUsernameAvailable(username)) {
            usernameLayout.setError("Username đã tồn tại!");
            valid = false;
        }

        // Password
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Mật khẩu không được để trống!");
            valid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("Tối thiểu 6 ký tự!");
            valid = false;
        }

        // Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Vui lòng xác nhận mật khẩu!");
            valid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Mật khẩu không khớp!");
            valid = false;
        }

        // Terms
        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý điều khoản!", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }

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

    private void resetErrors() {
        emailLayout.setError(null);
        phoneLayout.setError(null);
        fullNameLayout.setError(null);
        birthLayout.setError(null);
        usernameLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private boolean isUsernameAvailable(String username) {
        return !userDAO.isUsernameExists(username);
    }

    private boolean isEmailAvailable(String email) {
        return !userDAO.isEmailExists(email);
    }

    private void performRegister() {
        String email = getText(edtEmail);
        String phone = formatPhone(getText(edtPhone));
        String fullName = getText(edtFullName);
        String birth = getText(edtBirth);
        String username = getText(edtUsername);
        String password = getText(edtPassword);
        String avatar = "";

        String hashedPassword = PasswordUtils.hash(password); // BCrypt
        String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        User newUser = new User(userId, username, hashedPassword, email, phone, fullName, birth, avatar);

        long result = userDAO.insertUser(newUser);
        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
        }
    }
}