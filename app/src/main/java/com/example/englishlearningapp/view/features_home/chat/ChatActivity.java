package com.example.englishlearningapp.view.features_home.chat;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText editTextMessage;
    private Button buttonSend;
    private Button buttonClear;
    private LinearLayout layoutMessages;
    private ScrollView scrollViewChat;

    private MessageChatDAO messageChatDAO;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private boolean isFirstInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        setupChatComponents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        buttonClear = findViewById(R.id.buttonClear);
        layoutMessages = findViewById(R.id.layoutMessages);
        scrollViewChat = findViewById(R.id.scrollViewChat);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        buttonSend.setOnClickListener(v -> {
            String text = editTextMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                if (!isNetworkAvailable()) {
                    Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
                    return;
                }
                animateSendButton();
                saveUserMessageAndSendApi(text);
                editTextMessage.setText("");
            } else {
                Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonClear.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(ChatActivity.this)
                    .setTitle("Clear Chat")
                    .setMessage("Are you sure you want to clear all messages?")
                    .setPositiveButton("Clear", (dialog, which) -> {
                        messageChatDAO.clearChat();
                        loadMessages();
                        Toast.makeText(ChatActivity.this, "Chat cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .setCancelable(true)
                    .show();
        });
    }

    private void setupChatComponents() {
        messageChatDAO = new MessageChatDAO(this);
        setupEditTextAnimation();
        loadMessages();
    }

    private void setupEditTextAnimation() {
        editTextMessage.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && isFirstInput) {
                isFirstInput = false;
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(editTextMessage, "scaleX", 1.0f, 1.05f, 1.0f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(editTextMessage, "scaleY", 1.0f, 1.05f, 1.0f);
                ObjectAnimator alpha = ObjectAnimator.ofFloat(editTextMessage, "alpha", 0.7f, 1.0f);

                scaleX.setDuration(300);
                scaleY.setDuration(300);
                alpha.setDuration(300);

                scaleX.setInterpolator(new OvershootInterpolator());
                scaleY.setInterpolator(new OvershootInterpolator());

                scaleX.start();
                scaleY.start();
                alpha.start();
            }
        });
    }

    private void animateSendButton() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(buttonSend, "scaleX", 1.0f, 1.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(buttonSend, "scaleY", 1.0f, 1.2f, 1.0f);

        scaleX.setDuration(200);
        scaleY.setDuration(200);

        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());

        scaleX.start();
        scaleY.start();
    }

    private void saveUserMessageAndSendApi(String userText) {
        MessageChat userMessage = new MessageChat();
        userMessage.content = userText;
        userMessage.isUser = true;
        userMessage.timestamp = System.currentTimeMillis();
        messageChatDAO.addMessageChat(userMessage);

        loadMessages();
        addTypingIndicator();

        String enhancedPrompt = "Bạn là trợ lý dạy tiếng anh chuyên nghiệp." +
                "\nUser question: " + userText +
                "\nVui lòng trả lời ngắn gọn nhưng rõ ràng và đủ ý.";

        executor.execute(() -> {
            try {
                String apiResponse = ApiChatService.getGeminiResponse(enhancedPrompt);

                runOnUiThread(() -> {
                    removeTypingIndicator();

                    try {
                        JSONObject jsonObject = new JSONObject(apiResponse);

                        if (jsonObject.has("error")) {
                            String errorMessage = jsonObject.getString("error");
                            showErrorToast(errorMessage);
                            addErrorMessage("Failed to get response: " + errorMessage);
                            return;
                        }

                        String botReply = jsonObject.optString("text", "No response from bot");
                        MessageChat botMessage = new MessageChat();
                        botMessage.content = botReply;
                        botMessage.isUser = false;
                        botMessage.timestamp = System.currentTimeMillis();
                        messageChatDAO.addMessageChat(botMessage);

                        loadMessages();

                    } catch (JSONException e) {
                        showErrorToast("Error parsing response");
                        addErrorMessage("Response format error");
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    removeTypingIndicator();
                    showErrorToast("Network error: " + e.getMessage());
                    addErrorMessage("Network connection failed");
                });
            }
        });
    }

    private void addTypingIndicator() {
        runOnUiThread(() -> {
            TextView typingView = new TextView(this);
            typingView.setId(View.generateViewId());
            typingView.setText("Bot is typing...");
            typingView.setTextColor(Color.GRAY);
            typingView.setPadding(24, 16, 24, 16);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            params.gravity = Gravity.START;
            typingView.setLayoutParams(params);

            layoutMessages.addView(typingView);
            scrollToBottom();
        });
    }

    private void removeTypingIndicator() {
        runOnUiThread(() -> {
            View typingView = layoutMessages.findViewWithTag("typing");
            if (typingView != null) {
                layoutMessages.removeView(typingView);
            }
        });
    }

    private void addErrorMessage(String message) {
        MessageChat errorMsg = new MessageChat();
        errorMsg.content = message;
        errorMsg.isUser = false;
        errorMsg.timestamp = System.currentTimeMillis();
        messageChatDAO.addMessageChat(errorMsg);
        loadMessages();
    }

    private void scrollToBottom() {
        scrollViewChat.post(() -> scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, "Error: " + message, Toast.LENGTH_LONG).show();
    }

    private void loadMessages() {
        layoutMessages.removeAllViews();
        List<MessageChat> messages = messageChatDAO.getAllMessageChat();

        for (MessageChat msg : messages) {
            TextView textView = new TextView(this);

            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setCornerRadii(msg.isUser ?
                    new float[]{30, 30, 8, 30, 30, 30, 30, 8} :
                    new float[]{8, 30, 30, 30, 30, 8, 30, 30});

            bgDrawable.setColor(msg.isUser ?
                    ContextCompat.getColor(this, R.color.user_message) :
                    ContextCompat.getColor(this, R.color.bot_message));

            textView.setBackground(bgDrawable);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.75),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 8, 8, 8);
            params.gravity = msg.isUser ? Gravity.END : Gravity.START;

            textView.setLayoutParams(params);
            textView.setTextColor(Color.WHITE);
            textView.setPadding(24, 16, 24, 16);

            CharSequence formattedText = formatMarkdown(msg.content);
            textView.setText(formattedText);
            textView.setTextSize(16);
            textView.setMaxLines(Integer.MAX_VALUE);

            textView.setAlpha(0f);
            textView.setTranslationY(50f);
            layoutMessages.addView(textView);
            textView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .start();
        }

        scrollViewChat.post(() -> scrollViewChat.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private CharSequence formatMarkdown(String text) {
        if (text == null || text.isEmpty()) return "";

        SpannableString spannable = new SpannableString(text);

        Pattern boldPattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
        Matcher boldMatcher = boldPattern.matcher(text);

        while (boldMatcher.find()) {
            int start = boldMatcher.start();
            int end = boldMatcher.end();
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        String cleanedText = spannable.toString().replace("**", "");
        spannable = new SpannableString(cleanedText);

        spannable = new SpannableString(spannable.toString().replace("* ", "• "));

        spannable = new SpannableString(spannable.toString()
                .replace("### ", "")
                .replace("## ", "")
                .replace("# ", ""));

        return spannable;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}