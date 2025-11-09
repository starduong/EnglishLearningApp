package com.example.englishlearningapp.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.util.PasswordUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private UserDAO userDAO;

    private TextInputLayout usernameLayout, passwordLayout;
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private CheckBox chkRemember;
    private TextView txtSignUp, txtForgotPassword;
    private ImageView googleLogin;

    private final ActivityResultLauncher<Intent> googleSignInLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                } else {
                    Toast.makeText(this, "Đăng nhập Google bị hủy", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        autoLogin();
        event();
        setupGoogleSignIn();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        userDAO = new UserDAO(this);

        usernameLayout = findViewById(R.id.username_input_layout);
        passwordLayout = findViewById(R.id.password_input_layout);

        edtUsername = findViewById(R.id.username);
        edtPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.btnLogin);
        chkRemember = findViewById(R.id.remember_me);
        txtSignUp = findViewById(R.id.sign_up);
        txtForgotPassword = findViewById(R.id.forgot_password);
        googleLogin = findViewById(R.id.google_login);
    }

    private void autoLogin() {
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isRemembered = prefs.getBoolean("remember", false);
        String savedUsername = prefs.getString("username", "");

        if (isRemembered && !savedUsername.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", savedUsername);
            intent.putExtra("data", bundle);
            startActivity(intent);
            finish();
        }
    }

    private void event() {
        btnLogin.setOnClickListener(v -> checkLogin());
        txtSignUp.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        txtForgotPassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));
        googleLogin.setOnClickListener(v -> signInWithGoogle());
    }

    private void checkLogin() {
        String username = getText(edtUsername);
        String password = getText(edtPassword);

        boolean valid = true;
        resetErrors();

        if (username.isEmpty()) {
            usernameLayout.setError("Username không được để trống!");
            valid = false;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Mật khẩu không được để trống!");
            valid = false;
        }
        if (!valid) return;

        User user = userDAO.getUserByUsername(username);
        if (user != null && PasswordUtils.verify(password, user.getPassword())) {
            saveRememberMe(username);
            goToMainActivity(user);
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveRememberMe(String username) {
        if (chkRemember.isChecked()) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("username", username);
            editor.putBoolean("remember", true);
            editor.apply();
        }
    }

    private void goToMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void resetErrors() {
        usernameLayout.setError(null);
        passwordLayout.setError(null);
    }

    /* ============== GOOGLE SIGN IN =================== */
    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed", e);
            Toast.makeText(this, "Đăng nhập Google thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        com.google.firebase.auth.AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && mAuth.getCurrentUser() != null) {
                        String uid = mAuth.getCurrentUser().getUid();
                        String email = mAuth.getCurrentUser().getEmail();
                        String fullName = mAuth.getCurrentUser().getDisplayName();
                        String photoUrl = mAuth.getCurrentUser().getPhotoUrl() != null
                                ? mAuth.getCurrentUser().getPhotoUrl().toString() : "";

                        saveUserToSQLite(uid, fullName, email, photoUrl);
                    } else {
                        Log.e(TAG, "Firebase Auth failed", task.getException());
                        Toast.makeText(this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToSQLite(String uid, String fullName, String email, String photoUrl) {
        User existingUser = userDAO.getUserByUsername(email);
        if (existingUser == null) {
            // TẠO MẬT KHẨU NGẪU NHIÊN 8 KÝ TỰ
            String randomPassword = PasswordUtils.RandomPasswordGenerator.generate(8);
            String hashedPassword = PasswordUtils.hash(randomPassword);

            User newUser = new User(uid, email, hashedPassword, email, "", fullName, "", photoUrl);
            long result = userDAO.insertUser(newUser);

            if (result != -1) {
                showRandomPasswordDialog(randomPassword);
                saveRememberMe(email);
                goToMainActivity(newUser);
            } else {
                Toast.makeText(this, "Lỗi tạo tài khoản", Toast.LENGTH_SHORT).show();
            }
        } else {
            saveRememberMe(email);
            goToMainActivity(existingUser);
        }
    }

    // HIỂN THỊ MẬT KHẨU NGẪU NHIÊN (CHỈ LẦN ĐẦU)
    private void showRandomPasswordDialog(String password) {
        new AlertDialog.Builder(this)
                .setTitle("Đăng nhập thành công!")
                .setMessage("Mật khẩu tự động: " + password + "\n\n(Bạn có thể đổi mật khẩu sau trong phần Cài đặt)")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        if (userDAO != null) {
            userDAO.close();
        }
        super.onDestroy();
    }
}