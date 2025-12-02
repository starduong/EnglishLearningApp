package com.example.englishlearningapp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.model.Notification;
import com.example.englishlearningapp.util.NotificationManager;
import com.example.englishlearningapp.view.adapter.NotificationAdapter;

import java.util.List;

public class NotificationsActivity extends BaseActivity {

    private NotificationManager notificationManager;
    private NotificationAdapter adapter;
    private List<Notification> notificationList;

    // Views
    private RecyclerView recyclerViewNotifications;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View layoutEmpty;
    private ImageButton btnBack, btnClearAll, btnMarkAllRead;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo NotificationManager
        notificationManager = new NotificationManager(this);

        // Tạo dữ liệu mẫu nếu cần
        notificationManager.createSampleNotificationsIfNeeded();

        initViews();
        setupRecyclerView();
        setupListeners();
        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách khi quay lại
        loadNotifications();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đóng kết nối database
        if (notificationManager != null) {
            notificationManager.close();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnClearAll = findViewById(R.id.btnClearAll);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        tvTitle = findViewById(R.id.tvTitle);

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        // Set title từ string resource
        tvTitle.setText(getString(R.string.notifications));
    }

    private void setupRecyclerView() {
        // Setup RecyclerView
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        recyclerViewNotifications.setAdapter(adapter);

        // Setup click listener
        adapter.setOnNotificationClickListener(new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(int position, Notification notification) {
                // Đánh dấu đã đọc
                notificationManager.markAsRead(notification.getId());

                // Xử lý action của thông báo
                handleNotificationAction(notification);

                // Cập nhật UI
                notification.setRead(true);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onDeleteClick(int position, Notification notification) {
                showDeleteConfirmationDialog(position, notification);
            }
        });
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Mark all as read
        btnMarkAllRead.setOnClickListener(v -> {
            notificationManager.markAllAsRead();
            loadNotifications();
        });

        // Clear all notifications
        btnClearAll.setOnClickListener(v -> {
            showClearAllConfirmationDialog();
        });

        // Pull to refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadNotifications();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void loadNotifications() {
        // Lấy danh sách thông báo
        notificationList = notificationManager.getNotifications();

        // Cập nhật adapter
        if (adapter != null) {
            adapter.updateNotifications(notificationList);
        }

        // Hiển thị/ẩn empty state
        if (notificationList.isEmpty()) {
            recyclerViewNotifications.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void handleNotificationAction(Notification notification) {
        String action = notification.getAction();
        if (action == null || action.isEmpty()) {
            return;
        }

        // Xử lý các action khác nhau
        if (action.startsWith("open_lesson:")) {
            String lessonId = action.substring("open_lesson:".length());
            openLessonActivity(lessonId);
        } else if (action.equals("open_lesson_list")) {
            openLessonListActivity();
        } else if (action.equals("open_settings")) {
            openSettingsActivity();
        } else if (action.equals("open_premium")) {
            openPremiumActivity();
        } else if (action.equals("open_achievements")) {
            openAchievementsActivity();
        } else if (action.equals("open_home")) {
            // Quay về Home
            finish();
        }

        Toast.makeText(this, "Đã mở: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialog(int position, Notification notification) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa thông báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    notificationManager.deleteNotification(notification.getId());
                    notificationList.remove(position);
                    adapter.notifyItemRemoved(position);

                    // Kiểm tra nếu danh sách rỗng
                    if (notificationList.isEmpty()) {
                        recyclerViewNotifications.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showClearAllConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tất cả thông báo")
                .setMessage("Bạn có chắc chắn muốn xóa tất cả thông báo?")
                .setPositiveButton("Xóa tất cả", (dialog, which) -> {
                    notificationManager.deleteAllNotifications();
                    loadNotifications();
                    Toast.makeText(this, "Đã xóa tất cả thông báo", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Các phương thức mở activity (cần implement)
    private void openLessonActivity(String lessonId) {
        // Intent intent = new Intent(this, LessonActivity.class);
        // intent.putExtra("lesson_id", lessonId);
        // startActivity(intent);
        Toast.makeText(this, "Mở bài học: " + lessonId, Toast.LENGTH_SHORT).show();
    }

    private void openLessonListActivity() {
        // Intent intent = new Intent(this, LessonListActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Mở danh sách bài học", Toast.LENGTH_SHORT).show();
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void openPremiumActivity() {
        // Intent intent = new Intent(this, PremiumActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Mở trang nâng cấp Premium", Toast.LENGTH_SHORT).show();
    }

    private void openAchievementsActivity() {
        // Intent intent = new Intent(this, AchievementsActivity.class);
        // startActivity(intent);
        Toast.makeText(this, "Mở trang thành tích", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}