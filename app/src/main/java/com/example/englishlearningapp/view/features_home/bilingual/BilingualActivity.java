package com.example.englishlearningapp.view.features_home.bilingual;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;

import java.util.Locale;

public class BilingualActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ImageButton btnBack;
    private TextView textFromLang, textToLang;
    private EditText editTextInput;
    private TextView textViewOutput, textInputLabel, textOutputLabel;
    private Button btnTranslate, btnClear, btnCopy, btnSpeak;
    private ImageButton btnSwap;
    private ProgressBar progressBar;

    private TranslationHelper translationHelper;
    private TextToSpeech textToSpeech;

    private String currentFromLang = "en";
    private String currentToLang = "vi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bilingual);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupClickListeners();
        setupTranslationComponents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        textFromLang = findViewById(R.id.textFromLang);
        textToLang = findViewById(R.id.textToLang);
        editTextInput = findViewById(R.id.editTextInput);
        textViewOutput = findViewById(R.id.textViewOutput);
        textInputLabel = findViewById(R.id.textInputLabel);
        textOutputLabel = findViewById(R.id.textOutputLabel);
        btnTranslate = findViewById(R.id.btnTranslate);
        btnClear = findViewById(R.id.btnClear);
        btnCopy = findViewById(R.id.btnCopy);
        btnSpeak = findViewById(R.id.btnSpeak);
        btnSwap = findViewById(R.id.btnSwap);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnTranslate.setOnClickListener(v -> handleTranslation());

        btnClear.setOnClickListener(v -> {
            editTextInput.setText("");
            textViewOutput.setText("Kết quả dịch sẽ hiển thị ở đây...");
            hideActionButtons();
            Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
        });

        btnCopy.setOnClickListener(v -> copyToClipboard());
        btnSpeak.setOnClickListener(v -> speakOutput());

        btnSwap.setOnClickListener(v -> swapLanguages());
    }

    private void setupTranslationComponents() {
        translationHelper = new TranslationHelper(this);
        textToSpeech = new TextToSpeech(this, this);
        preloadTranslationModels();
    }

    private void swapLanguages() {
        String tempLang = currentFromLang;
        currentFromLang = currentToLang;
        currentToLang = tempLang;

        updateLanguageLabels();

        String inputText = editTextInput.getText().toString();
        String outputText = textViewOutput.getText().toString();

        if (!outputText.equals("Kết quả dịch sẽ hiển thị ở đây...") &&
                !outputText.startsWith("Lỗi:")) {
            editTextInput.setText(outputText);
            textViewOutput.setText(inputText);
        }

        Toast.makeText(this, "Đã đổi chiều dịch", Toast.LENGTH_SHORT).show();
    }

    private void updateLanguageLabels() {
        String fromLangName = getLanguageName(currentFromLang);
        String toLangName = getLanguageName(currentToLang);

        textFromLang.setText(fromLangName);
        textToLang.setText(toLangName);

        textInputLabel.setText(fromLangName + ":");
        textOutputLabel.setText(toLangName + ":");

        if (currentFromLang.equals("en")) {
            editTextInput.setHint("Nhập văn bản tiếng Anh...");
        } else {
            editTextInput.setHint("Nhập văn bản tiếng Việt...");
        }
    }

    private String getLanguageName(String langCode) {
        switch (langCode) {
            case "en":
                return "Tiếng Anh";
            case "vi":
                return "Tiếng Việt";
            default:
                return langCode;
        }
    }

    private void handleTranslation() {
        String inputText = editTextInput.getText().toString().trim();

        if (inputText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập văn bản cần dịch", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inputText.length() > 5000) {
            Toast.makeText(this, "Văn bản quá dài (>5000 ký tự). Vui lòng chia nhỏ", Toast.LENGTH_LONG).show();
            return;
        }

        showLoading();

        translationHelper.translateText(inputText, currentFromLang, currentToLang,
                new TranslationHelper.TranslationCallback() {
                    @Override
                    public void onSuccess(String translatedText) {
                        runOnUiThread(() -> {
                            hideLoading();
                            textViewOutput.setText(translatedText);
                            showActionButtons();
                            Toast.makeText(BilingualActivity.this, "Dịch thành công!", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            hideLoading();
                            textViewOutput.setText("Lỗi: " + errorMessage);
                            hideActionButtons();
                            Toast.makeText(BilingualActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        btnTranslate.setEnabled(false);
        textViewOutput.setText("Đang dịch...");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        btnTranslate.setEnabled(true);
    }

    private void showActionButtons() {
        btnCopy.setVisibility(View.VISIBLE);
        btnSpeak.setVisibility(View.VISIBLE);
    }

    private void hideActionButtons() {
        btnCopy.setVisibility(View.GONE);
        btnSpeak.setVisibility(View.GONE);
    }

    private void copyToClipboard() {
        String translatedText = textViewOutput.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Dịch", translatedText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Đã sao chép vào clipboard", Toast.LENGTH_SHORT).show();
    }

    private void speakOutput() {
        String textToSpeak = textViewOutput.getText().toString();
        if (textToSpeak != null && !textToSpeak.isEmpty()) {
            Locale locale = currentToLang.equals("vi") ? new Locale("vi", "VN") : Locale.US;
            textToSpeech.setLanguage(locale);
            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void preloadTranslationModels() {
        new Thread(() -> {
            translationHelper.downloadBothModels(new TranslationHelper.DownloadCallback() {
                @Override
                public void onProgress(String message) {
                    Log.d("Translation", message);
                }

                @Override
                public void onError(String errorMessage) {
                    Log.e("Translation", errorMessage);
                }
            });
        }).start();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // TTS initialized successfully
        } else {
            Toast.makeText(this, "Không thể khởi tạo tính năng đọc văn bản", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}