package com.example.englishlearningapp.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.payment.VNPay;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UpgradeProActivity extends AppCompatActivity {
    private static final String TAG = "UpgradeProActivity";

    // UI Components
    private ImageButton btnBack;
    private MaterialButton btnLifetime, btnYearly, btnMonthly;
    private TextView tvContactSupport;
    private View cardLifetime, cardYearly, cardMonthly;

    // Package constants
    private static final long PRICE_LIFETIME = 849000;
    private static final long PRICE_YEARLY = 389000;
    private static final long PRICE_MONTHLY = 349000;

    // Package codes
    private static final String PACKAGE_LIFETIME = "LIFETIME";
    private static final String PACKAGE_YEARLY = "YEARLY";
    private static final String PACKAGE_MONTHLY = "MONTHLY";

    // SharedPreferences keys
    private static final String PREFS_NAME = "english_learning_prefs";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_PRO_USER = "is_pro_user";
    private static final String KEY_PRO_EXPIRY_DATE = "pro_expiry_date";
    private static final String KEY_PRO_PACKAGE_TYPE = "pro_package_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upgrade_pro);
        setupWindowInsets();

        initViews();
        setupClickListeners();
        setupCardSelection();
        checkCurrentProStatus();
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
        btnLifetime = findViewById(R.id.btnLifetime);
        btnYearly = findViewById(R.id.btnYearly);
        btnMonthly = findViewById(R.id.btnMonthly);
        tvContactSupport = findViewById(R.id.tvContactSupport);

        cardLifetime = findViewById(R.id.cardLifetime);
        cardYearly = findViewById(R.id.cardYearly);
        cardMonthly = findViewById(R.id.cardMonthly);
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> onBackPressed());

        // Package selection buttons
        btnLifetime.setOnClickListener(v -> showPaymentConfirmation(PACKAGE_LIFETIME, PRICE_LIFETIME, "Vĩnh viễn"));
        btnYearly.setOnClickListener(v -> showPaymentConfirmation(PACKAGE_YEARLY, PRICE_YEARLY, "1 năm"));
        btnMonthly.setOnClickListener(v -> showPaymentConfirmation(PACKAGE_MONTHLY, PRICE_MONTHLY, "3 tháng"));

        // Contact support
        tvContactSupport.setOnClickListener(v -> openContactSupport());
    }

    private void setupCardSelection() {
        // Add click listeners to cards for visual feedback
        cardLifetime.setOnClickListener(v -> {
            cardLifetime.setAlpha(0.8f);
            cardYearly.setAlpha(1.0f);
            cardMonthly.setAlpha(1.0f);
            btnLifetime.performClick();
        });

        cardYearly.setOnClickListener(v -> {
            cardLifetime.setAlpha(1.0f);
            cardYearly.setAlpha(0.8f);
            cardMonthly.setAlpha(1.0f);
            btnYearly.performClick();
        });

        cardMonthly.setOnClickListener(v -> {
            cardLifetime.setAlpha(1.0f);
            cardYearly.setAlpha(1.0f);
            cardMonthly.setAlpha(0.8f);
            btnMonthly.performClick();
        });
    }

    private void checkCurrentProStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isPro = prefs.getBoolean(KEY_IS_PRO_USER, false);
        long expiryDate = prefs.getLong(KEY_PRO_EXPIRY_DATE, 0);
        String packageType = prefs.getString(KEY_PRO_PACKAGE_TYPE, "");

        if (isPro && expiryDate > 0) {
            if (System.currentTimeMillis() <= expiryDate) {
                // User is still Pro
                String expiryDateStr = formatDate(expiryDate);
                Toast.makeText(this,
                        "Bạn đang sử dụng gói " + getPackageName(packageType) +
                                "\nHết hạn: " + expiryDateStr,
                        Toast.LENGTH_LONG).show();
            } else {
                // Pro has expired
                handleProExpired();
            }
        }
    }

    private void handleProExpired() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Gói Pro đã hết hạn")
                .setMessage("Gói Pro của bạn đã hết hạn. Vui lòng gia hạn để tiếp tục sử dụng các tính năng đặc biệt.")
                .setPositiveButton("Gia hạn ngay", (dialog, which) -> {
                    // Keep the dialog open, user can select package
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showPaymentConfirmation(String packageType, long amount, String packageName) {
        String formattedAmount = formatCurrency(amount);
        String description = getPackageDescription(packageType);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage(
                        "Bạn đang chọn gói: " + packageName + "\n" +
                                "Giá: " + formattedAmount + "\n\n" +
                                description + "\n\n" +
                                "Bạn có chắc chắn muốn thanh toán?"
                )
                .setPositiveButton("Thanh toán ngay", (dialog, which) -> {
                    dialog.dismiss();
                    processPayment(packageType, amount, packageName);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void processPayment(String packageType, long amount, String packageName) {
        showLoading("Đang chuẩn bị thanh toán...");

        try {
            // Generate unique order ID
            String orderId = generateOrderId(packageType);

            // Get payment URL from VNPay
            String paymentUrl = VNPay.getPaymentUrl(orderId, amount);

            // Log payment attempt
            Log.i(TAG, "Payment attempt - Package: " + packageType +
                    ", Amount: " + amount + ", OrderId: " + orderId);

            // Open browser for payment
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentUrl));
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(browserIntent);

            hideLoading();

        } catch (Exception e) {
            hideLoading();
            Log.e(TAG, "Payment processing error: " + e.getMessage(), e);
            showErrorDialog("Lỗi xử lý thanh toán",
                    "Đã xảy ra lỗi khi tạo thanh toán. Vui lòng thử lại sau.\n\n" +
                            "Chi tiết: " + e.getMessage());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntentData(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntentData(getIntent());
    }

    private void handleIntentData(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && "myapp".equals(uri.getScheme())) {
            handleVNPayResponse(uri);
        }
    }

    private void handleVNPayResponse(Uri uri) {
        // Extract all VNPay parameters
        String responseCode = uri.getQueryParameter("vnp_ResponseCode");
        String orderId = uri.getQueryParameter("vnp_TxnRef");
        String amount = uri.getQueryParameter("vnp_Amount");
        String bankCode = uri.getQueryParameter("vnp_BankCode");
        String transactionNo = uri.getQueryParameter("vnp_TransactionNo");
        String payDate = uri.getQueryParameter("vnp_PayDate");
        String orderInfo = uri.getQueryParameter("vnp_OrderInfo");
        String secureHash = uri.getQueryParameter("vnp_SecureHash");

        Log.d(TAG, "VNPay Response - Code: " + responseCode +
                ", OrderId: " + orderId + ", Amount: " + amount);

        if ("00".equals(responseCode)) {
            // Payment successful
            String packageType = extractPackageTypeFromOrderId(orderId);
            handlePaymentSuccess(orderId, amount, packageType, transactionNo, payDate);

            // Log successful payment
            logSuccessfulPayment(orderId, amount, bankCode, transactionNo, payDate, packageType);
        } else {
            // Payment failed
            handlePaymentFailure(responseCode, orderInfo, orderId);
        }
    }

    private void handlePaymentSuccess(String orderId, String amount,
                                      String packageType, String transactionNo, String payDate) {
        long amountInVND = parseAmount(amount);
        String formattedAmount = formatCurrency(amountInVND);
        String packageName = getPackageName(packageType);

        // Update user status in database
        boolean updateSuccess = updateUserToPro(packageType);

        // Show success dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("🎉 Thanh toán thành công!")
                .setMessage(
                        "Chúc mừng! Bạn đã nâng cấp thành công.\n\n" +
                                "📋 Thông tin đơn hàng:\n" +
                                "• Mã đơn: " + orderId + "\n" +
                                "• Gói: " + packageName + "\n" +
                                "• Số tiền: " + formattedAmount + "\n" +
                                (transactionNo != null ? "• Mã giao dịch: " + transactionNo + "\n" : "") +
                                (payDate != null ? "• Thời gian: " + formatPayDate(payDate) + "\n" : "") +
                                "\n✅ Tài khoản của bạn đã được nâng cấp lên Pro.\n" +
                                "Bạn có thể sử dụng tất cả tính năng đặc biệt ngay lập tức!"
                )
                .setPositiveButton("Tuyệt vời!", (dialog, which) -> {
                    dialog.dismiss();
                    notifyProStatusChanged(packageType);
                    setResult(RESULT_OK);
                    finish();
                })
                .setCancelable(false)
                .show();

        // Set link movement method for any links
        AlertDialog dialog = builder.show();
        TextView messageTextView = dialog.findViewById(android.R.id.message);
        if (messageTextView != null) {
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    private void handlePaymentFailure(String responseCode, String orderInfo, String orderId) {
        String errorMessage = getVNPayErrorMessage(responseCode);
        String detailedMessage = errorMessage +
                (orderInfo != null ? "\n\nThông tin: " + orderInfo : "") +
                (orderId != null ? "\nMã đơn: " + orderId : "");

        new MaterialAlertDialogBuilder(this)
                .setTitle("❌ Thanh toán không thành công")
                .setMessage(detailedMessage)
                .setPositiveButton("Thử lại", (dialog, which) -> {
                    dialog.dismiss();
                    // User can try again by selecting a package
                })
                .setNegativeButton("Đóng", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNeutralButton("Liên hệ hỗ trợ", (dialog, which) -> {
                    dialog.dismiss();
                    openContactSupport();
                })
                .setCancelable(false)
                .show();
    }

    private boolean updateUserToPro(String packageType) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, "");

        if (userId.isEmpty()) {
            Log.e(TAG, "Cannot update Pro status: User ID not found");
            return false;
        }

        try {
            UserDAO userDAO = new UserDAO(this);

            // Calculate expiry date based on package
            long expiryDate = calculateExpiryDate(packageType);

            // Update in database
            int rowsUpdated = userDAO.updateAccountType(userId, User.ACCOUNT_TYPE_PRO, expiryDate);

            if (rowsUpdated > 0) {
                // Update SharedPreferences
                prefs.edit()
                        .putBoolean(KEY_IS_PRO_USER, true)
                        .putLong(KEY_PRO_EXPIRY_DATE, expiryDate)
                        .putString(KEY_PRO_PACKAGE_TYPE, packageType)
                        .apply();

                Log.i(TAG, "User upgraded to Pro - ID: " + userId +
                        ", Package: " + packageType +
                        ", Expiry: " + formatDate(expiryDate));
                return true;
            } else {
                Log.e(TAG, "Failed to update user in database - ID: " + userId);
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating user to Pro: " + e.getMessage(), e);
            return false;
        }
    }

    private long calculateExpiryDate(String packageType) {
        Calendar calendar = Calendar.getInstance();

        switch (packageType) {
            case PACKAGE_LIFETIME:
                // Lifetime: set to year 2100
                calendar.set(2100, Calendar.DECEMBER, 31, 23, 59, 59);
                return calendar.getTimeInMillis();

            case PACKAGE_YEARLY:
                // 1 year (365 days)
                calendar.add(Calendar.YEAR, 1);
                return calendar.getTimeInMillis();

            case PACKAGE_MONTHLY:
                // 3 months
                calendar.add(Calendar.MONTH, 3);
                return calendar.getTimeInMillis();

            default:
                return System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000);
        }
    }

    private void notifyProStatusChanged(String packageType) {
        // Send broadcast to notify other components
        Intent broadcastIntent = new Intent("PRO_STATUS_CHANGED");
        broadcastIntent.putExtra("is_pro", true);
        broadcastIntent.putExtra("package_type", packageType);
        broadcastIntent.putExtra("expiry_date", calculateExpiryDate(packageType));
        broadcastIntent.putExtra("timestamp", System.currentTimeMillis());

        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);

        // Also send global broadcast for system-wide notification
        sendBroadcast(new Intent("com.example.englishlearningapp.PRO_UPGRADED"));

        Log.d(TAG, "Pro status change broadcast sent for package: " + packageType);
    }

    private void openContactSupport() {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@englishlearningapp.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hỗ trợ nâng cấp Pro - English Learning App");

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
            } else {
                Toast.makeText(this,
                        "Không tìm thấy ứng dụng email. Vui lòng liên hệ: support@englishlearningapp.com",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening contact support: " + e.getMessage());
            Toast.makeText(this, "Liên hệ hỗ trợ: support@englishlearningapp.com", Toast.LENGTH_LONG).show();
        }
    }

    // ==================== HELPER METHODS ====================

    private String generateOrderId(String packageType) {
        return packageType + "_" + System.currentTimeMillis() + "_" +
                (int) (Math.random() * 1000);
    }

    private String extractPackageTypeFromOrderId(String orderId) {
        if (orderId != null && orderId.contains("_")) {
            String[] parts = orderId.split("_");
            if (parts.length > 0) {
                return parts[0];
            }
        }
        return PACKAGE_LIFETIME;
    }

    private long parseAmount(String amount) {
        try {
            return Long.parseLong(amount) / 100;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @SuppressLint("DefaultLocale")
    private String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        return formatter.format(amount) + " VND";
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private String formatPayDate(String payDate) {
        try {
            // VNPay date format: yyyyMMddHHmmss
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(payDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            return payDate;
        }
    }

    private String getPackageName(String packageType) {
        switch (packageType) {
            case PACKAGE_LIFETIME:
                return "Vĩnh viễn";
            case PACKAGE_YEARLY:
                return "1 năm";
            case PACKAGE_MONTHLY:
                return "3 tháng";
            default:
                return "Không xác định";
        }
    }

    private String getPackageDescription(String packageType) {
        switch (packageType) {
            case PACKAGE_LIFETIME:
                return "• Thanh toán một lần, sử dụng trọn đời\n" +
                        "• Tiết kiệm 48% so với gói hàng tháng\n" +
                        "• Không cần lo về gia hạn";
            case PACKAGE_YEARLY:
                return "• Tiết kiệm 29% so với gói hàng tháng\n" +
                        "• Chỉ 32.417 VND/tháng\n" +
                        "• Tự động gia hạn hàng năm";
            case PACKAGE_MONTHLY:
                return "• Trải nghiệm tất cả tính năng Pro\n" +
                        "• Linh hoạt, có thể hủy bất kỳ lúc nào\n" +
                        "• 116.333 VND/tháng";
            default:
                return "";
        }
    }

    private String getVNPayErrorMessage(String responseCode) {
        switch (responseCode) {
            case "01":
                return "Giao dịch không thành công";
            case "02":
                return "Giao dịch đã bị hủy";
            case "03":
                return "Tài khoản không đủ số dư";
            case "04":
                return "Khách hàng không hoàn tất giao dịch";
            case "05":
                return "VNPAY không xác nhận được giao dịch";
            case "06":
                return "VNPAY đã gửi yêu cầu hoàn tiền";
            case "07":
                return "Giao dịch bị nghi ngờ gian lận";
            case "08":
                return "Giao dịch bị từ chối do thẻ/Tài khoản chưa đăng ký dịch vụ";
            case "09":
                return "Giao dịch bị từ chối";
            case "10":
                return "Hết hạn thanh toán";
            case "11":
                return "Giao dịch bị từ chối bởi ngân hàng";
            case "12":
                return "Thẻ/Tài khoản bị khóa";
            case "13":
                return "Sai mật khẩu xác thực";
            case "24":
                return "Giao dịch không thành công do khách hàng hủy";
            case "51":
                return "Tài khoản không đủ số dư";
            case "65":
                return "Tài khoản đã vượt quá hạn mức giao dịch trong ngày";
            case "75":
                return "Ngân hàng thanh toán đang bảo trì";
            case "79":
                return "Khách hàng nhập sai mật khẩu thanh toán quá số lần quy định";
            case "99":
                return "Lỗi không xác định từ ngân hàng";
            default:
                return "Thanh toán thất bại với mã lỗi: " + responseCode;
        }
    }

    private void logSuccessfulPayment(String orderId, String amount, String bankCode,
                                      String transactionNo, String payDate, String packageType) {
        Log.i(TAG, "✅ Payment Successfully Processed\n" +
                "   Order ID: " + orderId + "\n" +
                "   Package: " + packageType + "\n" +
                "   Amount: " + (Long.parseLong(amount) / 100) + " VND\n" +
                "   Bank: " + (bankCode != null ? bankCode : "N/A") + "\n" +
                "   Transaction: " + (transactionNo != null ? transactionNo : "N/A") + "\n" +
                "   Date: " + (payDate != null ? payDate : "N/A"));
    }

    private void showLoading(String message) {
        // You can implement a loading dialog here
        // For now, just show a toast
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideLoading() {
        // Hide loading dialog if implemented
    }

    private void showErrorDialog(String title, String message) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up if needed
    }
}