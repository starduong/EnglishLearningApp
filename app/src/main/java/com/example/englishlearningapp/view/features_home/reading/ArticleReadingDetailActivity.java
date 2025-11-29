package com.example.englishlearningapp.view.features_home.reading;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.englishlearningapp.R;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ArticleReadingDetailActivity extends AppCompatActivity {

    private static final String TAG = "ArticleDetailActivity";

    // Views
    private ImageButton btnBack, btnPlayPause;
    private TextView tvArticleTitle, tvLevel, tvDate, tvReadingContent;
    private TextView tvCurrentTime, tvTotalTime;
    private ImageView ivArticleImage, ivVocabularyExpand, ivExercisesExpand;
    private TabLayout tabLayout;
    private RecyclerView rvVocabularyReading;
    private LinearLayout layoutAudio, layoutExercises, vocabularyHeader, exercisesHeader;
    private SeekBar audioSeekBar;

    // Data
    private ArticleReading article;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private boolean isEnglishContent = true;
    private boolean isVocabularyExpanded = true;
    private boolean isExercisesExpanded = true;

    // Audio handling
    private Handler seekBarHandler = new Handler();
    private Runnable updateSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_article_reading_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadArticleData();
        setupClickListeners();
        setupTabLayout();
        setupSeekBar();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        tvArticleTitle = findViewById(R.id.tvArticleTitle);
        tvLevel = findViewById(R.id.tvLevel);
        tvDate = findViewById(R.id.tvDate);
        tvReadingContent = findViewById(R.id.tvReadingContent);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        ivArticleImage = findViewById(R.id.ivArticleImage);
        tabLayout = findViewById(R.id.tabLayout);
        rvVocabularyReading = findViewById(R.id.rvVocabularyReading);
        layoutAudio = findViewById(R.id.layoutAudio);
        layoutExercises = findViewById(R.id.layoutExercises);
        audioSeekBar = findViewById(R.id.audioSeekBar);

        // Views for expand/collapse
        ivVocabularyExpand = findViewById(R.id.ivVocabularyExpand);
        vocabularyHeader = findViewById(R.id.vocabulary_header);
        ivExercisesExpand = findViewById(R.id.ivExercisesExpand);
        exercisesHeader = findViewById(R.id.exercises_header);

        // Setup RecyclerView
        rvVocabularyReading.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadArticleData() {
        try {
            String articleJson = getIntent().getStringExtra("article");
            if (articleJson != null && !articleJson.isEmpty()) {
                Gson gson = new Gson();
                article = gson.fromJson(articleJson, ArticleReading.class);

                if (article != null) {
                    displayArticleData();
                } else {
                    showErrorAndFinish("Failed to parse article data");
                }
            } else {
                showErrorAndFinish("No article data received");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading article data", e);
            showErrorAndFinish("Error loading article: " + e.getMessage());
        }
    }

    private void showErrorAndFinish(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void displayArticleData() {
        if (article == null) return;

        // Set basic info
        tvArticleTitle.setText(article.getTitle());
        tvLevel.setText(article.getLevel());
        tvDate.setText(formatDate(article.getCreatedDate()));

        // Set image
        setArticleImageFromAssets(article.getImage());

        // Set audio player visibility and duration
        setupAudioPlayer();

        // Set reading content (default English)
        displayReadingContent(true);

        // Setup vocabulary
        setupVocabulary();

        // Setup exercises
        setupExercises();
    }

    private String formatDate(String dateString) {
        try {
            return dateString != null ? dateString : "Unknown date";
        } catch (Exception e) {
            return "Unknown date";
        }
    }

    private void setArticleImageFromAssets(String imageName) {
        if (imageName == null || imageName.isEmpty()) {
            ivArticleImage.setImageResource(R.drawable.bg_placeholder_topic_listening);
            return;
        }

        try {
            String imagePath = "reading/image/" + imageName;
            InputStream inputStream = getAssets().open(imagePath);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ivArticleImage.setImageBitmap(bitmap);
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "Error loading image: " + imageName, e);
            ivArticleImage.setImageResource(R.drawable.bg_placeholder_topic_listening);
        }
    }

    private void setupAudioPlayer() {
        if (article.getAudio() != null && !article.getAudio().isEmpty()) {
            if (isAudioFileExists(article.getAudio())) {
                layoutAudio.setVisibility(View.VISIBLE);

                if (article.getDuration() != null && !article.getDuration().isEmpty()) {
                    tvTotalTime.setText(article.getDuration());
                } else {
                    tvTotalTime.setText("0:00");
                }

                tvCurrentTime.setText("0:00");
                audioSeekBar.setProgress(0);
            } else {
                layoutAudio.setVisibility(View.GONE);
                Log.d(TAG, "Audio file not found in assets: " + article.getAudio());
            }
        } else {
            layoutAudio.setVisibility(View.GONE);
            Log.d(TAG, "No audio file specified for this article");
        }
    }

    private boolean isAudioFileExists(String audioFileName) {
        try {
            InputStream inputStream = getAssets().open("reading/audio/" + audioFileName);
            inputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void displayReadingContent(boolean showEnglish) {
        if (article == null || article.getContent() == null) {
            tvReadingContent.setText("No content available");
            return;
        }

        List<String> contentList = showEnglish ? article.getContent().getReadingTextEn() : article.getContent().getReadingTextVi();

        if (contentList != null && !contentList.isEmpty()) {
            StringBuilder contentBuilder = new StringBuilder();
            for (int i = 0; i < contentList.size(); i++) {
                contentBuilder.append(contentList.get(i));
                if (i < contentList.size() - 1) {
                    contentBuilder.append("\n\n");
                }
            }
            tvReadingContent.setText(contentBuilder.toString());
        } else {
            tvReadingContent.setText("No content available in " + (showEnglish ? "English" : "Vietnamese"));
        }
    }

    // ==================== VOCABULARY SECTION ====================
    private void setupVocabulary() {
        if (article.getContent() != null && article.getContent().getVocabulary() != null && !article.getContent().getVocabulary().isEmpty()) {

            Log.d(TAG, "Setting up vocabulary with " + article.getContent().getVocabulary().size() + " items");

            VocabularyReadingAdapter adapter = new VocabularyReadingAdapter(article.getContent().getVocabulary());
            rvVocabularyReading.setAdapter(adapter);

            // Tắt divider mặc định (đường line đen)
            rvVocabularyReading.setLayoutManager(new LinearLayoutManager(this));
            rvVocabularyReading.setNestedScrollingEnabled(false); // mượt hơn khi trong ScrollView

            // Đảm bảo không có divider
            rvVocabularyReading.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.set(0, 0, 0, 0); // không để khoảng cách divider
                }
            });

            rvVocabularyReading.setVisibility(View.VISIBLE);
            rvVocabularyReading.setAlpha(1f);
            isVocabularyExpanded = true;
            ivVocabularyExpand.setRotation(0);

        } else {
            vocabularyHeader.setVisibility(View.GONE);
            rvVocabularyReading.setVisibility(View.GONE);
        }
    }

    private void toggleVocabularyVisibility() {
        isVocabularyExpanded = !isVocabularyExpanded;

        if (isVocabularyExpanded) {
            // Show vocabulary with animation
            rvVocabularyReading.setVisibility(View.VISIBLE);
            ivVocabularyExpand.animate().rotation(0).setDuration(300).start();

            rvVocabularyReading.animate().alpha(1f).translationY(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        } else {
            // Hide vocabulary with animation
            ivVocabularyExpand.animate().rotation(180).setDuration(300).start();

            rvVocabularyReading.animate().alpha(0f).translationY(-20).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> rvVocabularyReading.setVisibility(View.GONE)).start();
        }

        applyHeaderClickEffect(vocabularyHeader);
    }

    // ==================== EXERCISES SECTION ====================
    private void setupExercises() {
        if (article.getContent() == null || article.getContent().getExercises() == null || article.getContent().getExercises().isEmpty()) {

            Log.d(TAG, "No exercises found, hiding entire section");
            exercisesHeader.setVisibility(View.GONE);
            layoutExercises.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "Setting up exercises with " + article.getContent().getExercises().size() + " exercises");

        // Show exercises (default is visible)
        exercisesHeader.setVisibility(View.VISIBLE);
        layoutExercises.setVisibility(View.VISIBLE);
        layoutExercises.setAlpha(1f);
        isExercisesExpanded = true;
        ivExercisesExpand.setRotation(0);

        layoutExercises.removeAllViews();

        for (ExerciseReading exercise : article.getContent().getExercises()) {
            View exerciseView = createExerciseView(exercise);
            layoutExercises.addView(exerciseView);
        }
    }

    private void toggleExercisesVisibility() {
        isExercisesExpanded = !isExercisesExpanded;

        if (isExercisesExpanded) {
            // Show exercises with animation
            layoutExercises.setVisibility(View.VISIBLE);
            ivExercisesExpand.animate().rotation(0).setDuration(300).start();

            layoutExercises.animate().alpha(1f).translationY(0).setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator()).start();
        } else {
            // Hide exercises with animation
            ivExercisesExpand.animate().rotation(180).setDuration(300).start();

            layoutExercises.animate().alpha(0f).translationY(-20).setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator()).withEndAction(() -> layoutExercises.setVisibility(View.GONE)).start();
        }

        applyHeaderClickEffect(exercisesHeader);
    }

    private void applyHeaderClickEffect(View header) {
        // Ripple effect
        header.postDelayed(() -> {
            header.setPressed(false);
        }, 200);

        // Add slight scale effect for header
        header.animate().scaleX(0.98f).scaleY(0.98f).setDuration(100).withEndAction(() -> header.animate().scaleX(1f).scaleY(1f).setDuration(100).start()).start();
    }

    // ==================== EXERCISE VIEW CREATION ====================
    private View createExerciseView(ExerciseReading exercise) {
        View view = getLayoutInflater().inflate(R.layout.item_exercise_reading, layoutExercises, false);

        TextView tvExerciseTitle = view.findViewById(R.id.tvExerciseTitle);
        ImageView ivExerciseExpand = view.findViewById(R.id.ivExerciseExpand);
        LinearLayout layoutExerciseContent = view.findViewById(R.id.layoutExerciseContent);
        LinearLayout layoutQuestions = view.findViewById(R.id.layoutQuestions);

        tvExerciseTitle.setText(exercise.getTitle());

        // Add questions based on exercise type
        if (exercise.getQuestions() != null && !exercise.getQuestions().isEmpty()) {
            for (MultipleChoiceQuestion question : exercise.getQuestions()) {
                View questionView = createMultipleChoiceView(question);
                layoutQuestions.addView(questionView);
            }
        } else if (exercise.getText() != null && !exercise.getText().isEmpty()) {
            View questionView = createFillInBlankView(exercise);
            layoutQuestions.addView(questionView);
        }

        // Expand/Collapse functionality for individual exercise
        view.setOnClickListener(v -> {
            boolean isExpanded = layoutExerciseContent.getVisibility() == View.VISIBLE;
            layoutExerciseContent.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            ivExerciseExpand.setRotation(isExpanded ? 0 : 180);

            layoutExerciseContent.animate().alpha(isExpanded ? 0 : 1).setDuration(200).start();
        });

        return view;
    }

    private View createMultipleChoiceView(MultipleChoiceQuestion question) {
        View view = getLayoutInflater().inflate(R.layout.item_question_multiple_choice, null);

        TextView tvQuestion = view.findViewById(R.id.tvQuestion);
        LinearLayout layoutOptions = view.findViewById(R.id.layoutOptions);
        MaterialButton btnCheckAnswer = view.findViewById(R.id.btnCheckAnswer);
        MaterialButton btnAgain = view.findViewById(R.id.btnAgain);

        tvQuestion.setText(question.getQuestion());

        // Variable to store selected answer
        final String[] selectedAnswer = {null};

        // Create option buttons
        if (question.getOptions() != null) {
            for (int i = 0; i < question.getOptions().size(); i++) {
                String option = question.getOptions().get(i);
                MaterialButton optionButton = new MaterialButton(this, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);

                optionButton.setText(option);
                optionButton.setTag(i);
                optionButton.setCornerRadius(8);
                optionButton.setStrokeColorResource(R.color.gray);
                optionButton.setStrokeWidth(1);

                // Set initial colors
                optionButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                optionButton.setTextColor(getResources().getColor(R.color.selector_option_button));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 4, 0, 4);
                optionButton.setLayoutParams(params);

                // Khi chọn đáp án
                optionButton.setOnClickListener(v -> {
                    // Reset tất cả
                    for (int j = 0; j < layoutOptions.getChildCount(); j++) {
                        MaterialButton btn = (MaterialButton) layoutOptions.getChildAt(j);
                        btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
                        btn.setStrokeColorResource(R.color.gray);
                        btn.setTextColor(ContextCompat.getColor(this, R.color.selector_option_button));
                    }

                    // Tô màu cái được chọn
                    MaterialButton currentBtn = (MaterialButton) v;
                    currentBtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                    currentBtn.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    currentBtn.setStrokeColorResource(R.color.colorPrimary);

                    int selectedIndex = (int) v.getTag();
                    selectedAnswer[0] = question.getOptions().get(selectedIndex);
                });

                layoutOptions.addView(optionButton);
            }
        }

        btnCheckAnswer.setOnClickListener(v -> {
            if (selectedAnswer[0] == null) {
                Toast.makeText(this, "Please select an answer first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tô màu đáp án đúng = xanh (dựa vào correct_answer là "a", "b", "c")
            String correctAnswerLetter = question.getCorrectAnswer().toLowerCase().trim();

            // Map letter to index: a=0, b=1, c=2, etc.
            int correctIndex = -1;
            if (correctAnswerLetter.equals("a")) correctIndex = 0;
            else if (correctAnswerLetter.equals("b")) correctIndex = 1;
            else if (correctAnswerLetter.equals("c")) correctIndex = 2;

            if (correctIndex >= 0 && correctIndex < layoutOptions.getChildCount()) {
                MaterialButton correctButton = (MaterialButton) layoutOptions.getChildAt(correctIndex);
                correctButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(ArticleReadingDetailActivity.this, R.color.correct_green)));
                correctButton.setTextColor(ContextCompat.getColor(ArticleReadingDetailActivity.this, android.R.color.white));
                correctButton.setStrokeColorResource(R.color.correct_green);
            }

            // Disable interaction and show Again button
            btnCheckAnswer.setEnabled(false);
            btnAgain.setVisibility(View.VISIBLE);
            for (int i = 0; i < layoutOptions.getChildCount(); i++) {
                layoutOptions.getChildAt(i).setEnabled(false);
            }
        });

        btnAgain.setOnClickListener(v -> {
            resetMultipleChoice(layoutOptions, btnCheckAnswer, btnAgain);
            selectedAnswer[0] = null;
        });

        return view;
    }

    private void resetMultipleChoice(LinearLayout layoutOptions, MaterialButton btnCheckAnswer, MaterialButton btnAgain) {
        // Reset all buttons
        for (int i = 0; i < layoutOptions.getChildCount(); i++) {
            View child = layoutOptions.getChildAt(i);
            if (child instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) child;
                button.setEnabled(true);
                button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.transparent)));
                button.setStrokeColorResource(R.color.gray);
                button.setTextColor(ContextCompat.getColor(this, R.color.selector_option_button));
            }
        }

        // Reset buttons
        btnCheckAnswer.setEnabled(true);
        btnAgain.setVisibility(View.GONE);
    }


    // ==================== FILL IN BLANK EXERCISE ====================
    private View createFillInBlankView(ExerciseReading exercise) {
        View view = getLayoutInflater().inflate(R.layout.item_question_fill_blank, null);

        TextView tvInstruction = view.findViewById(R.id.tvInstruction);
        TextView tvExerciseText = view.findViewById(R.id.tvExerciseText);
        TextView tvWordBankLabel = view.findViewById(R.id.tvWordBankLabel);
        FlexboxLayout layoutWordBank = view.findViewById(R.id.layoutWordBank);
        LinearLayout layoutInputs = view.findViewById(R.id.layoutInputs);
        LinearLayout layoutCorrectAnswers = view.findViewById(R.id.layoutCorrectAnswers);
        MaterialButton btnCheckAnswer = view.findViewById(R.id.btnCheckAnswer);
        MaterialButton btnAgain = view.findViewById(R.id.btnAgain);

        tvInstruction.setText("Complete the following text:");

        // Display text with numbered blanks
        String displayText = exercise.getText();
        for (int i = 1; i <= 5; i++) {
            displayText = displayText.replace("{" + i + "}", "______");
        }
        tvExerciseText.setText(displayText);

        // Create Word Bank if available
        if (exercise.getWordBank() != null && !exercise.getWordBank().isEmpty()) {
            tvWordBankLabel.setVisibility(View.VISIBLE);
            layoutWordBank.setVisibility(View.VISIBLE);
            createWordBank(exercise.getWordBank(), layoutWordBank);
        }

        // Create input fields
        if (exercise.getAnswers() != null) {
            createInputFields(exercise, layoutInputs, layoutCorrectAnswers);
        }

        btnCheckAnswer.setOnClickListener(v -> {
            showFillInBlankResults(exercise, layoutInputs, layoutCorrectAnswers, btnCheckAnswer, btnAgain, tvWordBankLabel, layoutWordBank);
        });

        btnAgain.setOnClickListener(v -> {
            resetFillInBlank(exercise, layoutInputs, layoutCorrectAnswers, btnCheckAnswer, btnAgain, tvWordBankLabel, layoutWordBank);
        });

        return view;
    }

    private void createWordBank(List<String> wordBank, com.google.android.flexbox.FlexboxLayout layoutWordBank) {
        layoutWordBank.removeAllViews();

        for (String word : wordBank) {
            TextView wordView = new TextView(this);
            wordView.setText(word);
            wordView.setTextSize(13.5f);
            wordView.setTypeface(null, android.graphics.Typeface.BOLD);
            wordView.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            wordView.setPadding(20, 12, 20, 12);
            wordView.setBackgroundResource(R.drawable.bg_word_bank); // bạn đã có drawable này

            com.google.android.flexbox.FlexboxLayout.LayoutParams params = new com.google.android.flexbox.FlexboxLayout.LayoutParams(com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT, com.google.android.flexbox.FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 12, 12);
            wordView.setLayoutParams(params);

            layoutWordBank.addView(wordView);
        }
    }

    private void createInputFields(ExerciseReading exercise, LinearLayout layoutInputs, LinearLayout layoutCorrectAnswers) {
        layoutInputs.removeAllViews();
        layoutCorrectAnswers.removeAllViews();

        // Create input fields
        for (int i = 1; i <= 5; i++) {
            String answer = getAnswerByNumber(exercise.getAnswers(), i);
            if (answer != null) {
                // Input field
                TextInputLayout inputLayout = new TextInputLayout(this);
                TextInputEditText editText = new TextInputEditText(this);

                inputLayout.setHint("Blank " + i);
                inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                inputLayout.setBoxCornerRadii(8f, 8f, 8f, 8f);
                editText.setTag(i);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 8, 0, 8);
                inputLayout.setLayoutParams(params);

                inputLayout.addView(editText);
                layoutInputs.addView(inputLayout);

                // Display correct answer (initially hidden)
                TextView correctAnswerView = new TextView(this);
                correctAnswerView.setText("Blank " + i + ": " + answer);
                correctAnswerView.setTextColor(getResources().getColor(R.color.correct_green));
                correctAnswerView.setTextSize(12f);
                correctAnswerView.setPadding(0, 4, 0, 4);
                layoutCorrectAnswers.addView(correctAnswerView);
            }
        }
    }

    private void showFillInBlankResults(ExerciseReading exercise, LinearLayout layoutInputs, LinearLayout layoutCorrectAnswers, MaterialButton btnCheckAnswer, MaterialButton btnAgain, TextView tvWordBankLabel, FlexboxLayout layoutWordBank) {
        int correctCount = 0;
        int totalBlanks = 0;

        for (int i = 0; i < layoutInputs.getChildCount(); i++) {
            View child = layoutInputs.getChildAt(i);
            if (child instanceof TextInputLayout) {
                TextInputEditText editText = (TextInputEditText) ((TextInputLayout) child).getEditText();

                if (editText != null) {
                    int blankNumber = (int) editText.getTag();
                    String userAnswer = editText.getText() != null ? editText.getText().toString().trim() : "";
                    String correctAnswer = getAnswerByNumber(exercise.getAnswers(), blankNumber);

                    totalBlanks++;
                    if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        correctCount++;
                        editText.setBackgroundTintList(getResources().getColorStateList(R.color.correct_green));
                        editText.setTextColor(getResources().getColor(android.R.color.white));
                    } else {
                        editText.setBackgroundTintList(getResources().getColorStateList(R.color.incorrect_red));
                        editText.setTextColor(getResources().getColor(android.R.color.white));
                    }

                    editText.setEnabled(false);
                }
            }
        }

        layoutCorrectAnswers.setVisibility(View.VISIBLE);

        // Display score
        TextView scoreView = new TextView(this);
        scoreView.setText("Score: " + correctCount + "/" + totalBlanks);
        scoreView.setTextColor(getResources().getColor(R.color.colorPrimary));
        scoreView.setTextSize(14f);
        scoreView.setTypeface(null, android.graphics.Typeface.BOLD);
        scoreView.setPadding(0, 16, 0, 8);
        layoutCorrectAnswers.addView(scoreView, 0);

        // Disable Check Answer button and show Again button
        btnCheckAnswer.setEnabled(false);
        btnAgain.setVisibility(View.VISIBLE);

        // Hide word bank after checking
        tvWordBankLabel.setVisibility(View.GONE);
        layoutWordBank.setVisibility(View.GONE);
    }

    private void resetFillInBlank(ExerciseReading exercise, LinearLayout layoutInputs, LinearLayout layoutCorrectAnswers, MaterialButton btnCheckAnswer, MaterialButton btnAgain, TextView tvWordBankLabel, FlexboxLayout layoutWordBank) {
        // Reset input fields
        for (int i = 0; i < layoutInputs.getChildCount(); i++) {
            View child = layoutInputs.getChildAt(i);
            if (child instanceof TextInputLayout) {
                TextInputEditText editText = (TextInputEditText) ((TextInputLayout) child).getEditText();
                if (editText != null) {
                    editText.setText("");
                    editText.setEnabled(true);
                    editText.setBackgroundTintList(null);
                    editText.setTextColor(getResources().getColor(android.R.color.black));
                }
            }
        }

        // Reset layout
        layoutCorrectAnswers.setVisibility(View.GONE);
        layoutCorrectAnswers.removeAllViews();

        // Reset buttons and show word bank again
        btnCheckAnswer.setEnabled(true);
        btnAgain.setVisibility(View.GONE);

        if (exercise.getWordBank() != null && !exercise.getWordBank().isEmpty()) {
            tvWordBankLabel.setVisibility(View.VISIBLE);
            layoutWordBank.setVisibility(View.VISIBLE);
        }
    }

    private String getAnswerByNumber(FillInBlankAnswers answers, int number) {
        if (answers == null) return null;

        switch (number) {
            case 1:
                return answers.getAnswer1();
            case 2:
                return answers.getAnswer2();
            case 3:
                return answers.getAnswer3();
            case 4:
                return answers.getAnswer4();
            case 5:
                return answers.getAnswer5();
            default:
                return null;
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnPlayPause.setOnClickListener(v -> toggleAudioPlayback());

        // Add click listeners for Vocabulary and Exercises headers
        vocabularyHeader.setOnClickListener(v -> toggleVocabularyVisibility());
        exercisesHeader.setOnClickListener(v -> toggleExercisesVisibility());
    }

    // ==================== AUDIO METHODS ====================
    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isEnglishContent = tab.getPosition() == 0;
                displayReadingContent(isEnglishContent);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupSeekBar() {
        audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void toggleAudioPlayback() {
        if (article == null || article.getAudio() == null) {
            Toast.makeText(this, "Audio not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isPlaying) {
            startAudioPlayback();
        } else {
            pauseAudioPlayback();
        }
    }

    private void startAudioPlayback() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                String audioPath = "reading/audio/" + article.getAudio();

                android.content.res.AssetFileDescriptor afd = getAssets().openFd(audioPath);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();

                mediaPlayer.prepare();

                mediaPlayer.setOnCompletionListener(mp -> {
                    Log.d(TAG, "Audio playback completed");
                    stopAudioPlayback();
                });

                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
                    Toast.makeText(ArticleReadingDetailActivity.this, "Audio playback error", Toast.LENGTH_SHORT).show();
                    stopAudioPlayback();
                    return true;
                });

                int duration = mediaPlayer.getDuration();
                audioSeekBar.setMax(duration);
                tvTotalTime.setText(formatTime(duration));
            }

            mediaPlayer.start();
            isPlaying = true;
            btnPlayPause.setImageResource(R.drawable.ic_pause_48);
            startSeekBarUpdate();
            Log.d(TAG, "Audio playback started");

        } catch (IOException e) {
            Log.e(TAG, "Error playing audio: " + e.getMessage(), e);
            Toast.makeText(this, "Cannot play audio file", Toast.LENGTH_LONG).show();
            resetMediaPlayer();
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage(), e);
            Toast.makeText(this, "Audio playback failed", Toast.LENGTH_SHORT).show();
            resetMediaPlayer();
        }
    }

    private void pauseAudioPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play_48);
        stopSeekBarUpdate();
    }

    private void stopAudioPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
        }
        isPlaying = false;
        btnPlayPause.setImageResource(R.drawable.ic_play_48);
        stopSeekBarUpdate();
        resetSeekBar();
    }

    private void resetMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void startSeekBarUpdate() {
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    audioSeekBar.setProgress(currentPosition);
                    tvCurrentTime.setText(formatTime(currentPosition));
                    seekBarHandler.postDelayed(this, 1000);
                }
            }
        };
        seekBarHandler.post(updateSeekBar);
    }

    private void stopSeekBarUpdate() {
        if (updateSeekBar != null) {
            seekBarHandler.removeCallbacks(updateSeekBar);
        }
    }

    private void resetSeekBar() {
        audioSeekBar.setProgress(0);
        tvCurrentTime.setText("0:00");
    }

    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isPlaying) {
            pauseAudioPlayback();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSeekBarUpdate();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}