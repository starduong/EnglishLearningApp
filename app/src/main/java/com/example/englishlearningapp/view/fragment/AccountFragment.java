package com.example.englishlearningapp.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.view.activity.EditProfileActivity;
import com.example.englishlearningapp.view.activity.LoginActivity;
import com.example.englishlearningapp.view.activity.MainActivity;
import com.example.englishlearningapp.view.activity.NotificationsActivity;
import com.example.englishlearningapp.view.activity.SettingsActivity;
import com.example.englishlearningapp.view.activity.UpgradeProActivity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AccountFragment extends Fragment {

    private static final String TAG = "AccountFragment";
    private static final String PREFS_NAME = "english_learning_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_PRO_USER = "is_pro_user";
    private static final String KEY_PRO_EXPIRY_DATE = "pro_expiry_date";
    private static final String KEY_PRO_PACKAGE_TYPE = "pro_package_type";

    // Header buttons
    private MaterialButton btnNotification, btnSetting;

    // Profile card views
    private ImageView ivAvatar;
    private TextView tvProBadge, tvFullName, tvEmail, tvId, tvAccountType, tvProExpiry;
    private MaterialButton btnEditProfile;
    private TextView btnUpgradePro, btnLogout;

    private User currentUser;
    private UserDAO userDAO;
    private SharedPreferences sharedPreferences;

    // Broadcast receiver
    private BroadcastReceiver proStatusReceiver;

    public AccountFragment() {
        // Required empty public constructor
    }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userDAO = new UserDAO(requireContext());

        loadUserData();
        setupBroadcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        initViews(view);
        setupClickListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUserInterface();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload user data when fragment resumes (e.g., after editing profile)
        loadUserData();
        updateUserInterface();
    }

    private void initViews(View view) {
        // Header buttons
        btnNotification = view.findViewById(R.id.btnNotification);
        btnSetting = view.findViewById(R.id.btnSetting);

        // Profile card
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvProBadge = view.findViewById(R.id.tvProBadge);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvId = view.findViewById(R.id.tvId);
        tvAccountType = view.findViewById(R.id.tvAccountType);
        tvProExpiry = view.findViewById(R.id.tvProExpiry);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        // Upgrade to Pro button
        btnUpgradePro = view.findViewById(R.id.btnUpgradePro);

        // Logout button
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    private void setupBroadcastReceiver() {
        proStatusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if ("PRO_STATUS_CHANGED".equals(action)) {
                    // Reload user data when Pro status changes
                    loadUserData();
                    updateUserInterface();

                    // Show success message
                    if (intent.getBooleanExtra("is_pro", false)) {
                        String packageType = intent.getStringExtra("package_type");
                        Toast.makeText(requireContext(),
                                "🎉 Chúc mừng! Bạn đã nâng cấp lên Pro thành công!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        // Đăng ký receiver
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(proStatusReceiver,
                        new IntentFilter("PRO_STATUS_CHANGED"));
    }

    private void loadUserData() {
        String userId = sharedPreferences.getString(KEY_USER_ID, "");

        if (!TextUtils.isEmpty(userId)) {
            currentUser = userDAO.getUserById(userId);

            if (currentUser == null) {
                // Fallback to intent data
                if (getActivity() != null && getActivity().getIntent() != null) {
                    Bundle bundle = getActivity().getIntent().getExtras();
                    if (bundle != null) {
                        currentUser = (User) bundle.getSerializable("user");
                    }
                }
            }
        }
    }

    private void updateUserInterface() {
        if (currentUser != null) {
            // Display basic user info
            tvFullName.setText(currentUser.getFullname());
            tvEmail.setText(currentUser.getEmail());
            tvId.setText("ID: " + currentUser.getId());

            // Load avatar if available
            loadUserAvatar();

            // Update account type display
            updateAccountTypeDisplay();

            // Update Pro badge and expiry
            updateProStatusDisplay();

            // Update Upgrade button visibility
            updateUpgradeButton();

        } else {
            // Default display when no user data
            tvFullName.setText("Chưa đăng nhập");
            tvEmail.setText("Vui lòng đăng nhập");
            tvId.setText("ID: ---");
            tvAccountType.setText("GUEST");
            tvAccountType.setBackgroundResource(R.drawable.bg_account_type);
            tvAccountType.setTextColor(getResources().getColor(R.color.gray));

            // Hide Pro-related views
            tvProBadge.setVisibility(View.GONE);
            tvProExpiry.setVisibility(View.GONE);
            btnUpgradePro.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);
        }
    }

    private void loadUserAvatar() {
        if (currentUser != null && !TextUtils.isEmpty(currentUser.getAvatar())) {
            // Load avatar from URL using Glide
            Glide.with(requireContext())
                    .load(currentUser.getAvatar())
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            // Default avatar
            ivAvatar.setImageResource(R.drawable.ic_person_black_24dp);
        }
    }

    private void updateAccountTypeDisplay() {
        if (currentUser != null) {
            String accountType = currentUser.getAccountType();
            String displayText = currentUser.getAccountTypeDisplay();

            tvAccountType.setText(displayText);

            // Set different styles based on account type
            switch (accountType) {
                case User.ACCOUNT_TYPE_PRO:
                    tvAccountType.setBackgroundResource(R.drawable.bg_account_type_pro);
                    tvAccountType.setTextColor(getResources().getColor(R.color.orange));
                    break;

                case User.ACCOUNT_TYPE_PREMIUM:
                    tvAccountType.setBackgroundResource(R.drawable.bg_account_type_premium);
                    tvAccountType.setTextColor(getResources().getColor(R.color.deep_orange));
                    break;

                default: // FREE
                    tvAccountType.setBackgroundResource(R.drawable.bg_account_type);
                    tvAccountType.setTextColor(getResources().getColor(R.color.blue));
                    break;
            }
        }
    }

    private void updateProStatusDisplay() {
        if (currentUser != null && currentUser.isProUser()) {
            // Show Pro badge
            tvProBadge.setVisibility(View.VISIBLE);

            // Show expiry date if applicable
            long expiryDate = currentUser.getProExpiryDate();
            if (expiryDate > 0 && expiryDate < Long.MAX_VALUE) {
                tvProExpiry.setVisibility(View.VISIBLE);

                if (currentUser.isProExpired()) {
                    // Pro has expired
                    tvProExpiry.setText("⏰ Đã hết hạn");
                    tvProExpiry.setTextColor(getResources().getColor(R.color.red));
                    tvProBadge.setBackgroundResource(R.drawable.bg_pro_badge_expired);
                } else {
                    // Pro is still active
                    String expiryDateStr = formatExpiryDate(expiryDate);
                    tvProExpiry.setText("📅 Hết hạn: " + expiryDateStr);
                    tvProExpiry.setTextColor(getResources().getColor(R.color.green));
                    tvProBadge.setBackgroundResource(R.drawable.bg_pro_badge);
                }
            } else {
                // Lifetime Pro
                tvProExpiry.setVisibility(View.VISIBLE);
                tvProExpiry.setText("⭐ Vĩnh viễn");
                tvProExpiry.setTextColor(getResources().getColor(R.color.gold));
                tvProBadge.setBackgroundResource(R.drawable.bg_pro_badge_lifetime);
            }
        } else {
            // Not Pro user
            tvProBadge.setVisibility(View.GONE);
            tvProExpiry.setVisibility(View.GONE);
        }
    }

    private void updateUpgradeButton() {
        if (currentUser != null) {
            if (currentUser.isProUser() && !currentUser.isProExpired()) {
                // User is already Pro
                btnUpgradePro.setText("QUẢN LÝ GÓI PRO");
                btnUpgradePro.setBackgroundResource(R.drawable.bg_button_pro_manage);
            } else {
                // User is Free or Pro expired
                btnUpgradePro.setText("NÂNG CẤP LÊN PRO");
                btnUpgradePro.setBackgroundResource(R.drawable.bg_button_pro);
            }
        }
    }

    private String formatExpiryDate(long timestamp) {
        if (timestamp <= 0 || timestamp >= Long.MAX_VALUE - 1000000) {
            return "Vĩnh viễn";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void setupClickListeners() {
        // Header buttons
        btnNotification.setOnClickListener(v -> openNotificationsActivity());
        btnSetting.setOnClickListener(v -> openSettingsActivity());

        // Edit profile
        btnEditProfile.setOnClickListener(v -> openEditProfileActivity());

        // Upgrade/Manage Pro
        btnUpgradePro.setOnClickListener(v -> {
            if (currentUser != null && currentUser.isProUser() && !currentUser.isProExpired()) {
                // User is Pro, show management options
                showProManagementDialog();
            } else {
                // User is Free or Pro expired, open upgrade
                openUpgradeProActivity();
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void openEditProfileActivity() {
        if (currentUser != null) {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Vui lòng đăng nhập để chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProManagementDialog() {
        if (currentUser == null || !currentUser.isProUser()) return;

        String message = "🎉 Bạn đang sử dụng gói Pro!\n\n";

        if (currentUser.getProExpiryDate() > 0 && currentUser.getProExpiryDate() < Long.MAX_VALUE) {
            String expiryDate = formatExpiryDate(currentUser.getProExpiryDate());
            long daysLeft = calculateDaysLeft(currentUser.getProExpiryDate());

            message += "📅 Thời hạn: " + expiryDate + "\n";
            message += "⏰ Còn lại: " + daysLeft + " ngày\n\n";
        } else {
            message += "⭐ Gói: Vĩnh viễn\n\n";
        }

        message += "Bạn muốn làm gì?";

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Quản lý gói Pro")
                .setMessage(message)
                .setPositiveButton("Xem chi tiết", (dialog, which) -> {
                    // Open Pro details activity
                    openUpgradeProActivity();
                })
                .setNeutralButton("Gia hạn thêm", (dialog, which) -> {
                    // Open upgrade for renewal
                    openUpgradeProActivity();
                })
                .setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private long calculateDaysLeft(long expiryDate) {
        if (expiryDate <= 0) return 0;

        long currentTime = System.currentTimeMillis();
        if (expiryDate <= currentTime) return 0;

        long diff = expiryDate - currentTime;
        return diff / (1000 * 60 * 60 * 24) + 1; // +1 to include current day
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver
        if (proStatusReceiver != null) {
            LocalBroadcastManager.getInstance(requireContext())
                    .unregisterReceiver(proStatusReceiver);
        }

        // Close database connection
        if (userDAO != null) {
            userDAO.close();
        }
    }

    // Các phương thức mở activity
    private void openNotificationsActivity() {
        Intent intent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void openUpgradeProActivity() {
        Intent intent = new Intent(getActivity(), UpgradeProActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void performLogout() {
        // Clear login data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Close database connection
        if (userDAO != null) {
            userDAO.close();
        }

        // Navigate to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current activity if it's MainActivity
        if (getActivity() instanceof MainActivity) {
            getActivity().finish();
        }
    }
}