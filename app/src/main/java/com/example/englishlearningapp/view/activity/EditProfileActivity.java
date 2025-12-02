package com.example.englishlearningapp.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    // UI Components
    private ImageButton btnBack;
    private TextView btnSave, tvChangeAvatar, tvAccountType, tvUserId, tvProExpiry;
    private CircleImageView ivAvatar;
    private MaterialButton btnChangeAvatar, btnChangePassword;
    private TextInputEditText etFullName, etEmail, etPhone, etBirthDate, etUsername;
    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmPassword;
    private View layoutProExpiry;

    // User data
    private User currentUser;
    private UserDAO userDAO;
    private Uri selectedAvatarUri;

    // Date picker
    private final Calendar calendar = Calendar.getInstance();

    // Activity result launchers
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatarUri = result.getData().getData();
                    if (selectedAvatarUri != null) {
                        Glide.with(this)
                                .load(selectedAvatarUri)
                                .placeholder(R.drawable.ic_person_black_24dp)
                                .error(R.drawable.ic_person_black_24dp)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .circleCrop()
                                .into(ivAvatar);

                        tvChangeAvatar.setText("Đã chọn ảnh mới");
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    // Handle camera photo
                    // You would need to implement this based on your camera implementation
                    Toast.makeText(this, "Chức năng chụp ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setupWindowInsets();

        // Get user data from intent
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userDAO = new UserDAO(this);
        initViews();
        setupClickListeners();
        loadUserData();
        setupTextWatchers();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        ivAvatar = findViewById(R.id.ivAvatar);
        tvChangeAvatar = findViewById(R.id.tvChangeAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Form fields
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etBirthDate = findViewById(R.id.etBirthDate);
        etUsername = findViewById(R.id.etUsername);

        // Password fields
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        layoutCurrentPassword = findViewById(R.id.layoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.layoutNewPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);

        // Account info
        tvAccountType = findViewById(R.id.tvAccountType);
        tvUserId = findViewById(R.id.tvUserId);
        tvProExpiry = findViewById(R.id.tvProExpiry);
        layoutProExpiry = findViewById(R.id.layoutProExpiry);
    }

    private void loadUserData() {
        if (currentUser == null) return;

        // Load avatar
        if (!TextUtils.isEmpty(currentUser.getAvatar())) {
            Glide.with(this)
                    .load(currentUser.getAvatar())
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(ivAvatar);
        }

        // Set user data
        etFullName.setText(currentUser.getFullname());
        etEmail.setText(currentUser.getEmail());
        etPhone.setText(currentUser.getPhone());
        etBirthDate.setText(currentUser.getBirth());
        etUsername.setText(currentUser.getUsername());

        // Set account info
        tvAccountType.setText(currentUser.getAccountTypeDisplay());
        tvUserId.setText(currentUser.getId());

        // Show Pro expiry if user is Pro
        if (currentUser.isProUser() && currentUser.getProExpiryDate() > 0) {
            layoutProExpiry.setVisibility(View.VISIBLE);
            if (currentUser.getProExpiryDate() == Long.MAX_VALUE) {
                tvProExpiry.setText("Vĩnh viễn");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvProExpiry.setText(sdf.format(new Date(currentUser.getProExpiryDate())));

                if (currentUser.isProExpired()) {
                    tvProExpiry.setTextColor(getResources().getColor(R.color.incorrect_red));
                }
            }
        }
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Save button
        btnSave.setOnClickListener(v -> saveProfile());

        // Change avatar
        btnChangeAvatar.setOnClickListener(v -> showAvatarOptionsDialog());
        tvChangeAvatar.setOnClickListener(v -> showAvatarOptionsDialog());

        // Birth date picker
        etBirthDate.setOnClickListener(v -> showDatePickerDialog());

        // Change password button
        btnChangePassword.setOnClickListener(v -> changePassword());
    }

    private void setupTextWatchers() {
        // Password validation
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateNewPassword();
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validatePasswordConfirmation();
            }
        });
    }

    private void showAvatarOptionsDialog() {
        String[] options = {"Chọn từ thư viện", "Chụp ảnh mới", "Xóa ảnh đại diện", "Hủy"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("Đổi ảnh đại diện")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Choose from gallery
                            pickImageFromGallery();
                            break;
                        case 1: // Take photo
                            takePhotoWithCamera();
                            break;
                        case 2: // Remove avatar
                            removeAvatar();
                            break;
                        // case 3 is Cancel
                    }
                })
                .show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void takePhotoWithCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            takePhotoLauncher.launch(intent);
        } else {
            Toast.makeText(this, "Không tìm thấy ứng dụng camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeAvatar() {
        ivAvatar.setImageResource(R.drawable.ic_person_black_24dp);
        selectedAvatarUri = null;
        tvChangeAvatar.setText("Đổi ảnh đại diện");
        Toast.makeText(this, "Đã xóa ảnh đại diện", Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    etBirthDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean validateForm() {
        boolean isValid = true;

        // Validate full name
        String fullName = etFullName.getText().toString().trim();
        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Vui lòng nhập họ và tên");
            isValid = false;
        } else if (fullName.length() < 2) {
            etFullName.setError("Họ và tên phải có ít nhất 2 ký tự");
            isValid = false;
        }

        // Validate email
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            isValid = false;
        } else if (!email.equals(currentUser.getEmail())) {
            // Check if new email already exists
            if (userDAO.isEmailExists(email)) {
                etEmail.setError("Email đã được sử dụng");
                isValid = false;
            }
        }

        // Validate phone
        String phone = etPhone.getText().toString().trim();
        if (!TextUtils.isEmpty(phone)) {
            if (phone.length() < 10 || phone.length() > 15) {
                etPhone.setError("Số điện thoại không hợp lệ");
                isValid = false;
            } else if (!phone.equals(currentUser.getPhone())) {
                // Check if new phone already exists
                if (userDAO.isPhoneExists(phone)) {
                    etPhone.setError("Số điện thoại đã được sử dụng");
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    private void validateNewPassword() {
        String newPassword = etNewPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(newPassword)) {
            if (newPassword.length() < 6) {
                layoutNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            } else {
                layoutNewPassword.setError(null);
            }
        } else {
            layoutNewPassword.setError(null);
        }
    }

    private void validatePasswordConfirmation() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(confirmPassword)) {
            if (!newPassword.equals(confirmPassword)) {
                layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            } else {
                layoutConfirmPassword.setError(null);
            }
        } else {
            layoutConfirmPassword.setError(null);
        }
    }

    private void saveProfile() {
        if (!validateForm()) {
            Toast.makeText(this, "Vui lòng kiểm tra lại thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update user object
        currentUser.setFullname(etFullName.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        currentUser.setPhone(etPhone.getText().toString().trim());
        currentUser.setBirth(etBirthDate.getText().toString().trim());

        // TODO: Upload avatar if changed
        // if (selectedAvatarUri != null) {
        //     uploadAvatarToServer();
        // }

        // Update in database
        int rowsUpdated = userDAO.updateUser(currentUser);

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();

            // Set result to notify AccountFragment
            Intent resultIntent = new Intent();
            resultIntent.putExtra("updated_user", currentUser);
            setResult(RESULT_OK, resultIntent);

            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword() {
        String currentPassword = etCurrentPassword.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate current password
        if (TextUtils.isEmpty(currentPassword)) {
            layoutCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            return;
        }

        // TODO: Verify current password (you need to implement password verification)
        // For now, just check if it's not empty
        if (!currentPassword.equals("actual_current_password")) {
            layoutCurrentPassword.setError("Mật khẩu hiện tại không đúng");
            return;
        }

        // Validate new password
        if (TextUtils.isEmpty(newPassword)) {
            layoutNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return;
        }

        if (newPassword.length() < 6) {
            layoutNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        // Validate confirmation
        if (TextUtils.isEmpty(confirmPassword)) {
            layoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu mới");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            layoutConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            return;
        }

        // TODO: Update password in database
        // This is a placeholder - you need to implement password hashing
        boolean passwordUpdated = userDAO.updatePasswordByPhone(currentUser.getPhone(), newPassword);

        if (passwordUpdated) {
            // Clear password fields
            etCurrentPassword.setText("");
            etNewPassword.setText("");
            etConfirmPassword.setText("");

            // Clear errors
            layoutCurrentPassword.setError(null);
            layoutNewPassword.setError(null);
            layoutConfirmPassword.setError(null);

            Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void finish() {
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        if (currentUser == null) return false;

        // Check if any field has changed
        boolean nameChanged = !Objects.requireNonNull(etFullName.getText()).toString().trim().equals(currentUser.getFullname());
        boolean emailChanged = !Objects.requireNonNull(etEmail.getText()).toString().trim().equals(currentUser.getEmail());
        boolean phoneChanged = !Objects.requireNonNull(etPhone.getText()).toString().trim().equals(currentUser.getPhone());
        boolean birthChanged = !Objects.requireNonNull(etBirthDate.getText()).toString().trim().equals(currentUser.getBirth());
        boolean avatarChanged = selectedAvatarUri != null;

        return nameChanged || emailChanged || phoneChanged || birthChanged || avatarChanged;
    }

    private void showUnsavedChangesDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Thoát chỉnh sửa")
                .setMessage("Bạn có thay đổi chưa lưu. Bạn có chắc chắn muốn thoát?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    dialog.dismiss();
                    super.onBackPressed();
                })
                .setNegativeButton("Ở lại", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
    }
}