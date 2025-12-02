package com.example.englishlearningapp.view.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.WordDAO;
import com.example.englishlearningapp.data.model.Word;
import com.example.englishlearningapp.view.features_home.dictionary.Definition;
import com.example.englishlearningapp.view.features_home.dictionary.DictionaryRepository;
import com.example.englishlearningapp.view.features_home.dictionary.DictionaryResponse;
import com.example.englishlearningapp.view.features_home.dictionary.Meaning;
import com.example.englishlearningapp.view.features_home.dictionary.Phonetic;
import com.example.englishlearningapp.view.features_home.dictionary.TranslationRepository;
import com.example.englishlearningapp.view.features_home.dictionary.UnsplashRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AddWordActivity extends AppCompatActivity {

    // UI Components
    private ImageButton btnBack;
    private TextView tvTitle, btnSave;
    private TextInputEditText etEnglishWord, etPronunciation, etVietnameseMeaning;
    private AutoCompleteTextView etPartOfSpeech;
    private TextInputEditText etEnglishDefinition, etExampleSentence, etExampleTranslation;
    private TextInputEditText etSynonyms, etAntonyms, etTags, etNotes;
    private RadioGroup rgDifficulty;
    private Slider sliderPriority;
    private TextView tvPriorityValue;
    private Switch switchFavorite;
    private ImageView ivExpandAdvanced;
    private View layoutAdvancedSettings;
    private MaterialButton btnFromDictionary, btnFromCamera;
    private ProgressBar progressBar;

    // Data
    private WordDAO wordDAO;
    private Word currentWord;
    private boolean isEditMode = false;
    private String userId;

    // API Repositories
    private DictionaryRepository dictionaryRepository;
    private TranslationRepository translationRepository;
    private UnsplashRepository unsplashRepository;

    // Debounce scheduler
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> fetchDataFuture;
    private Handler handler = new Handler();

    // Part of Speech options and mapping
    private final String[] partOfSpeechOptions = {"Danh từ (n)", "Động từ (v)", "Tính từ (adj)", "Trạng từ (adv)", "Đại từ (pron)", "Giới từ (prep)", "Liên từ (conj)", "Thán từ (interj)", "Khác"};

    private final String[][] partOfSpeechMap = {
            {"noun", "Danh từ (n)"},
            {"verb", "Động từ (v)"},
            {"adjective", "Tính từ (adj)"},
            {"adverb", "Trạng từ (adv)"},
            {"pronoun", "Đại từ (pron)"},
            {"preposition", "Giới từ (prep)"},
            {"conjunction", "Liên từ (conj)"},
            {"interjection", "Thán từ (interj)"}
    };

    // Store data from APIs
    private String currentImageUrl;
    private String currentAudioUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        setupWindowInsets();

        // Get data from intent
        getIntentData();

        // Initialize repositories
        dictionaryRepository = new DictionaryRepository();
        translationRepository = new TranslationRepository();
        unsplashRepository = new UnsplashRepository();

        wordDAO = new WordDAO(this);
        initViews();
        setupClickListeners();
        setupAutoComplete();
        setupSlider();
        setupTextWatcher();

        if (isEditMode && currentWord != null) {
            populateWordData();
        }

        // Handle back press with OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (hasUnsavedChanges()) {
                    showUnsavedChangesDialog();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("user_id");

        if (intent.hasExtra("is_edit") && intent.getBooleanExtra("is_edit", false)) {
            currentWord = (Word) intent.getSerializableExtra("word");
            isEditMode = true;
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        btnSave = findViewById(R.id.btnSave);

        // Form fields
        etEnglishWord = findViewById(R.id.etEnglishWord);
        etPronunciation = findViewById(R.id.etPronunciation);
        etPartOfSpeech = findViewById(R.id.etPartOfSpeech);
        etVietnameseMeaning = findViewById(R.id.etVietnameseMeaning);
        etEnglishDefinition = findViewById(R.id.etEnglishDefinition);
        etExampleSentence = findViewById(R.id.etExampleSentence);
        etExampleTranslation = findViewById(R.id.etExampleTranslation);
        etSynonyms = findViewById(R.id.etSynonyms);
        etAntonyms = findViewById(R.id.etAntonyms);
        etTags = findViewById(R.id.etTags);
        etNotes = findViewById(R.id.etNotes);

        // Advanced settings
        rgDifficulty = findViewById(R.id.rgDifficulty);
        sliderPriority = findViewById(R.id.sliderPriority);
        tvPriorityValue = findViewById(R.id.tvPriorityValue);
        switchFavorite = findViewById(R.id.switchFavorite);
        ivExpandAdvanced = findViewById(R.id.ivExpandAdvanced);
        layoutAdvancedSettings = findViewById(R.id.layoutAdvancedSettings);

        // Quick add buttons
        btnFromDictionary = findViewById(R.id.btnFromDictionary);
        btnFromCamera = findViewById(R.id.btnFromCamera);

        // IMPORTANT: Tạo ProgressBar programmatically vì layout không có
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);

        // Thêm ProgressBar vào layout - FIX LỖI CAST
        ScrollView mainContainer = findViewById(R.id.main);
        if (mainContainer != null) {
            // Lấy LinearLayout con đầu tiên trong ScrollView
            ViewGroup scrollViewContent = (ViewGroup) mainContainer.getChildAt(0);
            if (scrollViewContent instanceof LinearLayout) {
                LinearLayout linearLayout = (LinearLayout) scrollViewContent;

                // Tạo layout params cho ProgressBar
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
                params.setMargins(0, 16, 0, 16);

                progressBar.setLayoutParams(params);

                // Thêm ProgressBar vào vị trí sau header (index 1)
                linearLayout.addView(progressBar, 1);
            }
        }

        // Set title based on mode
        if (isEditMode) {
            tvTitle.setText("Chỉnh sửa từ");
            btnSave.setText("CẬP NHẬT");
        } else {
            tvTitle.setText("Thêm từ mới");
            btnSave.setText("LƯU");
        }
    }

    private void setupTextWatcher() {
        etEnglishWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String word = s.toString().trim();
                if (!TextUtils.isEmpty(word) && word.length() > 2 && !isEditMode) {
                    // Debounce API calls
                    if (fetchDataFuture != null) {
                        fetchDataFuture.cancel(false);
                    }

                    Runnable fetchDataRunnable = () -> {
                        runOnUiThread(() -> {
                            showLoading(true);
                            fetchWordData(word);
                        });
                    };
                    fetchDataFuture = scheduler.schedule(fetchDataRunnable, 800, TimeUnit.MILLISECONDS);
                }
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void fetchWordData(String word) {
        // Fetch from Dictionary API
        dictionaryRepository.searchWord(word, new DictionaryRepository.DictionaryCallback() {
            @Override
            public void onSuccess(List<DictionaryResponse> response) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (response != null && !response.isEmpty()) {
                        DictionaryResponse dictionaryResponse = response.get(0);
                        processDictionaryResponse(dictionaryResponse);

                        // Fetch image from Unsplash
                        fetchWordImage(word);
                    } else {
                        Toast.makeText(AddWordActivity.this, "Không tìm thấy từ trong từ điển", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AddWordActivity.this, "Lỗi kết nối từ điển: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void processDictionaryResponse(DictionaryResponse response) {
        // Set pronunciation
        if (response.getPhonetics() != null && !response.getPhonetics().isEmpty()) {
            String pronunciation = "";
            for (Phonetic phonetic : response.getPhonetics()) {
                if (phonetic.getText() != null && !phonetic.getText().isEmpty()) {
                    pronunciation = phonetic.getText();
                    // Store audio URL
                    if (phonetic.getAudio() != null && !phonetic.getAudio().isEmpty()) {
                        currentAudioUrl = phonetic.getAudio();
                    }
                    break;
                }
            }
            if (!pronunciation.isEmpty()) {
                etPronunciation.setText(pronunciation);
            }
        }

        // Process meanings
        if (response.getMeanings() != null && !response.getMeanings().isEmpty()) {
            StringBuilder englishDefinitionBuilder = new StringBuilder();
            StringBuilder exampleSentenceBuilder = new StringBuilder();
            StringBuilder synonymsBuilder = new StringBuilder();
            StringBuilder antonymsBuilder = new StringBuilder();

            String firstPartOfSpeech = "";
            String firstDefinition = "";

            for (Meaning meaning : response.getMeanings()) {
                // Set part of speech (only first one)
                if (firstPartOfSpeech.isEmpty() && meaning.getPartOfSpeech() != null) {
                    firstPartOfSpeech = meaning.getPartOfSpeech();
                    String vietnamesePartOfSpeech = mapPartOfSpeech(meaning.getPartOfSpeech());
                    etPartOfSpeech.setText(vietnamesePartOfSpeech);
                }

                // Get definitions
                if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
                    for (int i = 0; i < Math.min(meaning.getDefinitions().size(), 3); i++) {
                        Definition definition = meaning.getDefinitions().get(i);

                        // Get first definition for Vietnamese translation
                        if (i == 0 && definition.getDefinition() != null && firstDefinition.isEmpty()) {
                            firstDefinition = definition.getDefinition();
                        }

                        // English definition
                        if (definition.getDefinition() != null) {
                            if (englishDefinitionBuilder.length() > 0) {
                                englishDefinitionBuilder.append("\n");
                            }
                            englishDefinitionBuilder.append("• ").append(definition.getDefinition());
                        }

                        // Example sentence
                        if (definition.getExample() != null) {
                            if (exampleSentenceBuilder.length() > 0) {
                                exampleSentenceBuilder.append("\n");
                            }
                            exampleSentenceBuilder.append("• ").append(definition.getExample());
                        }
                    }
                }

                // Synonyms
                if (meaning.getSynonyms() != null && !meaning.getSynonyms().isEmpty()) {
                    for (String synonym : meaning.getSynonyms()) {
                        if (synonymsBuilder.length() > 0) synonymsBuilder.append(", ");
                        synonymsBuilder.append(synonym);
                    }
                }

                // Antonyms
                if (meaning.getAntonyms() != null && !meaning.getAntonyms().isEmpty()) {
                    for (String antonym : meaning.getAntonyms()) {
                        if (antonymsBuilder.length() > 0) antonymsBuilder.append(", ");
                        antonymsBuilder.append(antonym);
                    }
                }
            }

            // Set text to fields
            if (englishDefinitionBuilder.length() > 0) {
                etEnglishDefinition.setText(englishDefinitionBuilder.toString());
            }

            if (exampleSentenceBuilder.length() > 0) {
                etExampleSentence.setText(exampleSentenceBuilder.toString());
                // Auto translate example sentences
                translateText(exampleSentenceBuilder.toString(), "vi",
                        translatedText -> etExampleTranslation.setText(translatedText));
            }

            if (synonymsBuilder.length() > 0) {
                etSynonyms.setText(synonymsBuilder.toString());
            }

            if (antonymsBuilder.length() > 0) {
                etAntonyms.setText(antonymsBuilder.toString());
            }

            // Auto translate the first definition to Vietnamese
            if (!firstDefinition.isEmpty()) {
                translateText(firstDefinition, "vi",
                        translatedText -> etVietnameseMeaning.setText(translatedText));
            }
        }
    }

    private void fetchWordImage(String word) {
        unsplashRepository.getRandomImage(word, new UnsplashRepository.UnsplashCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                runOnUiThread(() -> {
                    currentImageUrl = imageUrl;
                    Toast.makeText(AddWordActivity.this, "Đã tải ảnh minh họa", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                // Silently fail - image is optional
                currentImageUrl = null;
            }
        });
    }

    private void translateText(String text, String targetLang, TranslationCallback callback) {
        translationRepository.translateText(text, targetLang, new TranslationRepository.TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                runOnUiThread(() -> callback.onTranslationReady(translatedText));
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    // If translation fails, keep original text
                    callback.onTranslationReady(text);
                });
            }
        });
    }

    private interface TranslationCallback {
        void onTranslationReady(String translatedText);
    }

    private String mapPartOfSpeech(String englishPartOfSpeech) {
        for (String[] mapping : partOfSpeechMap) {
            if (mapping[0].equalsIgnoreCase(englishPartOfSpeech)) {
                return mapping[1];
            }
        }
        return "Khác";
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        // Save button
        btnSave.setOnClickListener(v -> saveWord());

        // Expand/Collapse advanced settings
        ivExpandAdvanced.setOnClickListener(v -> toggleAdvancedSettings());

        // Part of speech click
        etPartOfSpeech.setOnClickListener(v -> showPartOfSpeechDialog());

        // Quick add options
        btnFromDictionary.setOnClickListener(v -> openDictionary());
        btnFromCamera.setOnClickListener(v -> openCamera());
    }

    private void setupAutoComplete() {
        // Part of speech auto complete
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, partOfSpeechOptions);

        ((MaterialAutoCompleteTextView) etPartOfSpeech).setAdapter(adapter);
    }

    private void setupSlider() {
        sliderPriority.addOnChangeListener((slider, value, fromUser) -> {
            int intValue = (int) value;
            tvPriorityValue.setText(String.valueOf(intValue));
        });
    }

    private void showPartOfSpeechDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn loại từ");
        builder.setItems(partOfSpeechOptions, (dialog, which) -> {
            etPartOfSpeech.setText(partOfSpeechOptions[which]);
        });
        builder.show();
    }

    private void toggleAdvancedSettings() {
        if (layoutAdvancedSettings.getVisibility() == View.VISIBLE) {
            layoutAdvancedSettings.setVisibility(View.GONE);
            ivExpandAdvanced.setRotation(0);
        } else {
            layoutAdvancedSettings.setVisibility(View.VISIBLE);
            ivExpandAdvanced.setRotation(180);
        }
    }

    private void populateWordData() {
        if (currentWord == null) return;

        etEnglishWord.setText(currentWord.getEnglishWord());
        etPronunciation.setText(currentWord.getPronunciation());
        etPartOfSpeech.setText(currentWord.getPartOfSpeech());
        etVietnameseMeaning.setText(currentWord.getVietnameseMeaning());
        etEnglishDefinition.setText(currentWord.getEnglishDefinition());
        etExampleSentence.setText(currentWord.getExampleSentence());
        etExampleTranslation.setText(currentWord.getExampleTranslation());
        etSynonyms.setText(currentWord.getSynonyms());
        etAntonyms.setText(currentWord.getAntonyms());
        etTags.setText(currentWord.getTags());
        etNotes.setText(currentWord.getNotes());

        // Difficulty level
        switch (currentWord.getDifficultyLevel()) {
            case Word.DIFFICULTY_EASY:
                rgDifficulty.check(R.id.rbEasy);
                break;
            case Word.DIFFICULTY_MEDIUM:
                rgDifficulty.check(R.id.rbMedium);
                break;
            case Word.DIFFICULTY_HARD:
                rgDifficulty.check(R.id.rbHard);
                break;
        }

        // Priority
        sliderPriority.setValue(currentWord.getPriority());

        // Favorite
        switchFavorite.setChecked(currentWord.isFavorite());
    }

    private boolean validateForm() {
        boolean isValid = true;

        // English word validation
        String englishWord = etEnglishWord.getText().toString().trim();
        if (TextUtils.isEmpty(englishWord)) {
            etEnglishWord.setError("Vui lòng nhập từ tiếng Anh");
            isValid = false;
        } else {
            etEnglishWord.setError(null);
        }

        // Vietnamese meaning validation
        String vietnameseMeaning = etVietnameseMeaning.getText().toString().trim();
        if (TextUtils.isEmpty(vietnameseMeaning)) {
            etVietnameseMeaning.setError("Vui lòng nhập nghĩa tiếng Việt");
            isValid = false;
        } else {
            etVietnameseMeaning.setError(null);
        }

        if (!isValid) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc (*)", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void saveWord() {
        if (!validateForm()) {
            return;
        }

        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        Word word;
        if (isEditMode && currentWord != null) {
            word = currentWord;
        } else {
            word = new Word();
            word.setId(generateWordId());
            word.setUserId(userId);
            word.setAddedDate(System.currentTimeMillis());
            word.setSource("Dictionary API");

            // THÊM: Set các giá trị mặc định cho từ mới
            word.setMasteryLevel(0); // Mới thêm = level 0
            word.setReviewCount(0);
            word.setCorrectCount(0);
            word.setWrongCount(0);

            // QUAN TRỌNG: Tính toán nextReviewDate cho từ mới
            // Từ mới cần ôn sau 1 ngày
            // long nextReviewDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000L); // 1 ngày
            // Test: Set nextReviewDate là 1 phút sau (thay vì 1 ngày)
            // long nextReviewDate = System.currentTimeMillis() + (60 * 1000L); // 1 phút sau
            long nextReviewDate = System.currentTimeMillis() + (1 * 1000L); // 1 s
            word.setNextReviewDate(nextReviewDate);
            word.setLastReviewed(0); // Chưa ôn lần nào
        }

        // Get data from form
        word.setEnglishWord(etEnglishWord.getText().toString().trim());
        word.setPronunciation(etPronunciation.getText().toString().trim());
        word.setPartOfSpeech(etPartOfSpeech.getText().toString().trim());
        word.setVietnameseMeaning(etVietnameseMeaning.getText().toString().trim());
        word.setEnglishDefinition(etEnglishDefinition.getText().toString().trim());
        word.setExampleSentence(etExampleSentence.getText().toString().trim());
        word.setExampleTranslation(etExampleTranslation.getText().toString().trim());
        word.setSynonyms(etSynonyms.getText().toString().trim());
        word.setAntonyms(etAntonyms.getText().toString().trim());
        word.setTags(etTags.getText().toString().trim());
        word.setNotes(etNotes.getText().toString().trim());

        // Add imageUrl and audioUrl from APIs
        if (currentImageUrl != null) {
            word.setImageUrl(currentImageUrl);
        }
        if (currentAudioUrl != null) {
            word.setAudioUrl(currentAudioUrl);
        }

        // Difficulty level
        int difficultyLevel = Word.DIFFICULTY_MEDIUM;
        int checkedId = rgDifficulty.getCheckedRadioButtonId();
        if (checkedId == R.id.rbEasy) {
            difficultyLevel = Word.DIFFICULTY_EASY;
        } else if (checkedId == R.id.rbHard) {
            difficultyLevel = Word.DIFFICULTY_HARD;
        }
        word.setDifficultyLevel(difficultyLevel);

        // Priority
        word.setPriority((int) sliderPriority.getValue());

        // Favorite
        word.setFavorite(switchFavorite.isChecked());

        // Save to database
        boolean success;
        if (isEditMode) {
            success = wordDAO.updateWord(word) > 0;
        } else {
            success = wordDAO.insertWord(word) > 0;
        }

        if (success) {
            String message = isEditMode ? "Cập nhật từ thành công" : "Thêm từ mới thành công";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("word_added", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            String message = isEditMode ? "Cập nhật từ thất bại" : "Thêm từ mới thất bại";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private String generateWordId() {
        return "word_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void openDictionary() {
        Toast.makeText(this, "Tính năng tra từ điển đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private void openCamera() {
        Toast.makeText(this, "Tính năng chụp ảnh đang được phát triển", Toast.LENGTH_SHORT).show();
    }

    private boolean hasUnsavedChanges() {
        if (isEditMode && currentWord != null) {
            // Check if any field has changed
            String englishWord = etEnglishWord.getText().toString().trim();
            String vietnameseMeaning = etVietnameseMeaning.getText().toString().trim();

            return !englishWord.equals(currentWord.getEnglishWord()) ||
                    !vietnameseMeaning.equals(currentWord.getVietnameseMeaning()) ||
                    !TextUtils.equals(etPronunciation.getText().toString().trim(), currentWord.getPronunciation()) ||
                    !TextUtils.equals(etPartOfSpeech.getText().toString().trim(), currentWord.getPartOfSpeech());
        } else {
            // Check if any field is not empty
            return !TextUtils.isEmpty(etEnglishWord.getText().toString().trim()) ||
                    !TextUtils.isEmpty(etVietnameseMeaning.getText().toString().trim());
        }
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thoát không lưu")
                .setMessage("Bạn có thay đổi chưa lưu. Bạn có chắc chắn muốn thoát?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setNegativeButton("Ở lại", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wordDAO != null) {
            wordDAO.close();
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }
}
