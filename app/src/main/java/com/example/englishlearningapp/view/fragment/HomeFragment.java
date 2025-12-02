package com.example.englishlearningapp.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.view.activity.NotificationsActivity;
import com.example.englishlearningapp.view.activity.SettingsActivity;
import com.example.englishlearningapp.view.adapter.BannerAdapter;
import com.example.englishlearningapp.view.features_home.bilingual.BilingualActivity;
import com.example.englishlearningapp.view.features_home.blog.BlogActivity;
import com.example.englishlearningapp.view.features_home.book.BookActivity;
import com.example.englishlearningapp.view.features_home.browser.BrowserActivity;
import com.example.englishlearningapp.view.features_home.chat.ChatActivity;
import com.example.englishlearningapp.view.features_home.dictionary.Definition;
import com.example.englishlearningapp.view.features_home.dictionary.DictionaryRepository;
import com.example.englishlearningapp.view.features_home.dictionary.DictionaryResponse;
import com.example.englishlearningapp.view.features_home.dictionary.Meaning;
import com.example.englishlearningapp.view.features_home.dictionary.Phonetic;
import com.example.englishlearningapp.view.features_home.dictionary.TranslationRepository;
import com.example.englishlearningapp.view.features_home.epub.EpubActivity;
import com.example.englishlearningapp.view.features_home.exercises.ExerciseActivity;
import com.example.englishlearningapp.view.features_home.game.GameActivity;
import com.example.englishlearningapp.view.features_home.grammar.GrammarActivity;
import com.example.englishlearningapp.view.features_home.listening.ListeningActivity;
import com.example.englishlearningapp.view.features_home.news.NewsActivity;
import com.example.englishlearningapp.view.features_home.reading.ReadingActivity;
import com.example.englishlearningapp.view.features_home.video.VideoActivity;
import com.example.englishlearningapp.view.features_home.vocabulary.VocabularyActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {

    // Header buttons
    private MaterialButton btnNotification, btnSetting;

    // Dictionary views
    private TextInputLayout dictionarySearchLayout;
    private TextInputEditText etDictionarySearch;
    private CardView cardDictionaryResult;
    private TextView tvWord, tvPhonetic, tvMeaning, tvExample, tvExampleTranslation, tvDictionaryError;
    private ImageButton btnSound;
    private LinearLayout layoutExample, layoutAdditionalMeanings, layoutMainSynonymsAntonyms;
    private ProgressBar progressBarDictionary;

    // Grid items
    private MaterialCardView cardGrammar, cardListening, cardReading, cardVocabulary;
    private MaterialCardView cardExercise, cardNews, cardVideo, cardGame, cardBilingual, cardChat;
    private MaterialCardView cardBook, cardBrowser, cardEpub, cardBlog;

    // Banner variables
    private ViewPager2 bannerViewPager;
    private LinearLayout bannerIndicator;
    private BannerAdapter bannerAdapter;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private List<String> bannerFiles = new ArrayList<>();
    private Runnable bannerRunnable;

    // Dictionary variables
    private DictionaryRepository dictionaryRepository;
    private MediaPlayer mediaPlayer;
    private Handler dictionaryHandler = new Handler();
    private Runnable dictionaryRunnable;

    private TranslationRepository translationRepository;
    private TextView tvMeaningVietnamese;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dictionaryRepository = new DictionaryRepository();
        translationRepository = new TranslationRepository();
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupBanner();
        setupDictionary();
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        // Header buttons
        btnNotification = view.findViewById(R.id.btnNotification);
        btnSetting = view.findViewById(R.id.btnSetting);

        // Dictionary views
        dictionarySearchLayout = view.findViewById(R.id.dictionarySearchLayout);
        etDictionarySearch = view.findViewById(R.id.etDictionarySearch);
        cardDictionaryResult = view.findViewById(R.id.cardDictionaryResult);
        tvWord = view.findViewById(R.id.tvWord);
        tvPhonetic = view.findViewById(R.id.tvPhonetic);
        tvMeaning = view.findViewById(R.id.tvMeaning);
        tvMeaningVietnamese = view.findViewById(R.id.tvMeaningVietnamese);
        tvExample = view.findViewById(R.id.tvExample);
        tvExampleTranslation = view.findViewById(R.id.tvExampleTranslation);
        tvDictionaryError = view.findViewById(R.id.tvDictionaryError);
        btnSound = view.findViewById(R.id.btnSound);
        layoutExample = view.findViewById(R.id.layoutExample);
        layoutAdditionalMeanings = view.findViewById(R.id.layoutAdditionalMeanings);
        layoutMainSynonymsAntonyms = view.findViewById(R.id.layoutMainSynonymsAntonyms);
        progressBarDictionary = view.findViewById(R.id.progressBarDictionary);

        // Banner views
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);

        // Grid 1: 4 items
        cardGrammar = view.findViewById(R.id.card_grammar);
        cardListening = view.findViewById(R.id.card_listening);
        cardReading = view.findViewById(R.id.card_reading);
        cardVocabulary = view.findViewById(R.id.card_vocabulary);

        // Grid 2: 6 items
        cardExercise = view.findViewById(R.id.card_exercise);
        cardNews = view.findViewById(R.id.card_news);
        cardVideo = view.findViewById(R.id.card_video);
        cardGame = view.findViewById(R.id.card_game);
        cardBilingual = view.findViewById(R.id.card_bilingual);
        cardChat = view.findViewById(R.id.card_chat);

        // Grid 3: 4 items
        cardBook = view.findViewById(R.id.card_book);
        cardBrowser = view.findViewById(R.id.card_browser);
        cardEpub = view.findViewById(R.id.card_epub);
        cardBlog = view.findViewById(R.id.card_blog);
    }

    private void setupBanner() {
        // Lấy danh sách file banner từ assets
        loadBannerFilesFromAssets();

        if (bannerFiles.isEmpty()) {
            // Nếu không có banner, ẩn ViewPager2
            bannerViewPager.setVisibility(View.GONE);
            return;
        }

        // Setup adapter
        bannerAdapter = new BannerAdapter(requireContext(), bannerFiles);
        bannerViewPager.setAdapter(bannerAdapter);

        // Setup indicator
        setupIndicator();

        // Setup auto scroll
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 3000); // Reset timer
            }
        });

        // Auto scroll runnable
        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerFiles.size() > 1) {
                    int currentPosition = bannerViewPager.getCurrentItem();
                    int nextPosition = (currentPosition + 1) % bannerFiles.size();
                    bannerViewPager.setCurrentItem(nextPosition, true);
                }
                bannerHandler.postDelayed(this, 3000); // 3 seconds
            }
        };

        // Start auto scroll
        bannerHandler.postDelayed(bannerRunnable, 3000);

        // Setup view pager transformer for better UX
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float r = 1 - Math.abs(position);
            page.setScaleY(0.85f + r * 0.15f);
        });
        bannerViewPager.setPageTransformer(transformer);

        // Click listener for banner
        bannerAdapter.setOnItemClickListener(position -> {
            // Xử lý khi click vào banner
            openBannerAction(position);
        });
    }

    private void loadBannerFilesFromAssets() {
        try {
            // Lấy danh sách file trong thư mục banners
            String[] files = requireContext().getAssets().list("banners");
            if (files != null) {
                for (String file : files) {
                    // Chỉ thêm các file ảnh
                    if (file.toLowerCase().endsWith(".jpg") ||
                            file.toLowerCase().endsWith(".jpeg") ||
                            file.toLowerCase().endsWith(".png") ||
                            file.toLowerCase().endsWith(".webp")) {
                        bannerFiles.add("banners/" + file);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Sắp xếp banner files để đảm bảo thứ tự (tùy chọn)
        sortBannerFiles();

        // Nếu không tìm thấy file trong assets, ẩn banner
        if (bannerFiles.isEmpty()) {
            bannerViewPager.setVisibility(View.GONE);
        }
    }

    private void sortBannerFiles() {
        // Sắp xếp banner theo tên file để đảm bảo thứ tự
        bannerFiles.sort((file1, file2) -> {
            return extractNumber(file1) - extractNumber(file2);
        });
    }

    private int extractNumber(String fileName) {
        // Trích xuất số từ tên file (ví dụ: banner_1.jpg -> 1)
        try {
            String number = fileName.replaceAll("\\D+", "");
            return number.isEmpty() ? 0 : Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setupIndicator() {
        bannerIndicator.removeAllViews();

        if (bannerFiles.size() <= 1) {
            // Ẩn indicator nếu chỉ có 1 banner
            bannerIndicator.setVisibility(View.GONE);
            return;
        }

        bannerIndicator.setVisibility(View.VISIBLE);
        ImageView[] indicators = new ImageView[bannerFiles.size()];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(params);
            bannerIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = bannerIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) bannerIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageResource(R.drawable.indicator_active);
            } else {
                imageView.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }

    private void openBannerAction(int position) {
        // Xử lý khi click vào banner
        String bannerFile = bannerFiles.get(position);

        // Dựa vào tên file banner để xác định action
        if (bannerFile.contains("ielts") || bannerFile.contains("vocabulary")) {
            openIeltsVocabularyActivity();
        }
        // Thêm các điều kiện khác tùy theo nội dung banner
    }

    private void openIeltsVocabularyActivity() {
        // Mở activity IELTS Vocabulary
        // Intent intent = new Intent(getActivity(), IeltsVocabularyActivity.class);
        // startActivity(intent);
    }

    private void setupDictionary() {
        // Xử lý search khi nhấn icon search
        dictionarySearchLayout.setEndIconOnClickListener(v -> {
            searchDictionaryWithAPI();
        });

        // Xử lý search khi nhấn enter
        etDictionarySearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchDictionaryWithAPI();
                return true;
            }
            return false;
        });

        // Auto search với debounce
        etDictionarySearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Debounce - chờ 800ms sau khi user ngừng gõ
                dictionaryHandler.removeCallbacks(dictionaryRunnable);
                dictionaryRunnable = () -> {
                    String word = s.toString().trim();
                    if (word.length() > 0 && word.length() < 50) { // Giới hạn độ dài
                        searchDictionaryWithAPI();
                    } else if (word.isEmpty()) {
                        hideDictionaryResult();
                    }
                };
                dictionaryHandler.postDelayed(dictionaryRunnable, 800);
            }
        });

        // Ẩn kết quả ban đầu
        hideDictionaryResult();
    }

    private void searchDictionaryWithAPI() {
        String word = etDictionarySearch.getText().toString().trim();
        if (word.isEmpty()) {
            hideDictionaryResult();
            return;
        }

        showLoading();

        dictionaryRepository.searchWord(word, new DictionaryRepository.DictionaryCallback() {
            @Override
            public void onSuccess(List<DictionaryResponse> response) {
                requireActivity().runOnUiThread(() -> {
                    if (response != null && !response.isEmpty()) {
                        displayDictionaryResult(response.get(0)); // Lấy kết quả đầu tiên
                    } else {
                        showDictionaryError("Không tìm thấy kết quả");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    showDictionaryError(errorMessage);
                });
            }
        });
    }

    private void displayDictionaryResult(DictionaryResponse response) {
        cardDictionaryResult.setVisibility(View.VISIBLE);
        progressBarDictionary.setVisibility(View.GONE);

        // Hiển thị từ và phiên âm
        tvWord.setText(response.getWord());

        // Lấy phiên âm
        String phoneticText = response.getPhonetic();
        if (phoneticText == null || phoneticText.isEmpty()) {
            for (Phonetic phonetic : response.getPhonetics()) {
                if (phonetic.getText() != null && !phonetic.getText().isEmpty()) {
                    phoneticText = phonetic.getText();
                    break;
                }
            }
        }
        tvPhonetic.setText(phoneticText != null ? phoneticText : "");

        // Hiển thị nghĩa chính (cả tiếng Anh và tiếng Việt)
        if (response.getMeanings() != null && !response.getMeanings().isEmpty()) {
            Meaning firstMeaning = response.getMeanings().get(0);
            if (firstMeaning.getDefinitions() != null && !firstMeaning.getDefinitions().isEmpty()) {
                String mainDefinition = firstMeaning.getDefinitions().get(0).getDefinition();

                // Hiển thị nghĩa gốc tiếng Anh
                tvMeaning.setText(mainDefinition);

                // Hiển thị loading cho bản dịch
                tvMeaningVietnamese.setText("Đang dịch...");

                // Dịch tự động nghĩa chính
                translateText(mainDefinition, "vi", translatedText -> {
                    tvMeaningVietnamese.setText(translatedText);
                });
            }

            // Hiển thị synonyms/antonyms cho nghĩa chính nếu có
            displaySynonymsAntonymsForMainMeaning(firstMeaning);
        }

        // Hiển thị và dịch ví dụ
        displayAndTranslateExamples(response.getMeanings());

        // Hiển thị các nghĩa khác với dịch tự động
        displayAdditionalMeanings(response.getMeanings());

        // Setup audio button
        setupAudioButton(response.getPhonetics());
    }

    private void displaySynonymsAntonymsForMainMeaning(Meaning meaning) {
        layoutMainSynonymsAntonyms.removeAllViews();

        boolean hasContent = false;

        // Hiển thị synonyms cho nghĩa chính
        if (meaning.getSynonyms() != null && !meaning.getSynonyms().isEmpty()) {
            LinearLayout synonymsLayout = createSynonymsAntonymsLayout("Từ đồng nghĩa: ",
                    TextUtils.join(", ", meaning.getSynonyms()), "#4CAF50");
            layoutMainSynonymsAntonyms.addView(synonymsLayout);
            hasContent = true;
        }

        // Hiển thị antonyms cho nghĩa chính
        if (meaning.getAntonyms() != null && !meaning.getAntonyms().isEmpty()) {
            LinearLayout antonymsLayout = createSynonymsAntonymsLayout("Từ trái nghĩa: ",
                    TextUtils.join(", ", meaning.getAntonyms()), "#F44336");
            layoutMainSynonymsAntonyms.addView(antonymsLayout);
            hasContent = true;
        }

        layoutMainSynonymsAntonyms.setVisibility(hasContent ? View.VISIBLE : View.GONE);
    }

    private LinearLayout createSynonymsAntonymsLayout(String title, String content, String color) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 8, 0, 8);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        tvTitle.setText(title);
        tvTitle.setTextColor(getResources().getColor(R.color.gray));
        tvTitle.setTextSize(12);
        tvTitle.setTypeface(null, Typeface.BOLD);

        TextView tvContent = new TextView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        params.setMarginStart(8); // Thêm khoảng cách giữa title và content
        tvContent.setLayoutParams(params);
        tvContent.setText(content);
        tvContent.setTextColor(Color.parseColor(color));
        tvContent.setTextSize(12);
        tvContent.setMaxLines(3); // Giới hạn 3 dòng
        tvContent.setEllipsize(TextUtils.TruncateAt.END);

        layout.addView(tvTitle);
        layout.addView(tvContent);

        return layout;
    }

    private void displayAndTranslateExamples(List<Meaning> meanings) {
        boolean hasExample = false;

        if (meanings != null) {
            for (Meaning meaning : meanings) {
                if (meaning.getDefinitions() != null) {
                    for (Definition definition : meaning.getDefinitions()) {
                        if (definition.getExample() != null && !definition.getExample().isEmpty()) {
                            layoutExample.setVisibility(View.VISIBLE);
                            String example = definition.getExample();
                            tvExample.setText(example);

                            // Dịch tự động ví dụ
                            translateText(example, "vi", translatedText -> {
                                tvExampleTranslation.setText(translatedText);
                            });

                            hasExample = true;
                            break;
                        }
                    }
                }
                if (hasExample) break;
            }
        }

        if (!hasExample) {
            layoutExample.setVisibility(View.GONE);
        }
    }

    private void displayAdditionalMeanings(List<Meaning> meanings) {
        layoutAdditionalMeanings.removeAllViews();

        if (meanings != null && !meanings.isEmpty()) {
            layoutAdditionalMeanings.setVisibility(View.VISIBLE);

            for (int i = 0; i < meanings.size(); i++) {
                Meaning meaning = meanings.get(i);

                if (meaning.getDefinitions() != null && !meaning.getDefinitions().isEmpty()) {
                    View meaningView = LayoutInflater.from(getContext())
                            .inflate(R.layout.item_word_meaning, layoutAdditionalMeanings, false);

                    TextView tvType = meaningView.findViewById(R.id.tvWordType);
                    TextView tvMeaningText = meaningView.findViewById(R.id.tvWordMeaning);
                    TextView tvMeaningVietnamese = meaningView.findViewById(R.id.tvWordMeaningVietnamese);
                    LinearLayout layoutSynonyms = meaningView.findViewById(R.id.layoutSynonyms);
                    LinearLayout layoutAntonyms = meaningView.findViewById(R.id.layoutAntonyms);
                    TextView tvSynonyms = meaningView.findViewById(R.id.tvSynonyms);
                    TextView tvAntonyms = meaningView.findViewById(R.id.tvAntonyms);

                    // Hiển thị loại từ
                    String partOfSpeech = meaning.getPartOfSpeech();
                    tvType.setText(partOfSpeech != null ? partOfSpeech : "Khác");

                    // Hiển thị định nghĩa đầu tiên (tiếng Anh)
                    String definition = meaning.getDefinitions().get(0).getDefinition();
                    tvMeaningText.setText(definition);

                    // Hiển thị loading cho bản dịch
                    tvMeaningVietnamese.setText("Đang dịch...");

                    // Dịch tự động nghĩa
                    translateText(definition, "vi", tvMeaningVietnamese::setText);

                    // Hiển thị synonyms từ MEANING level (quan trọng!)
                    if (meaning.getSynonyms() != null && !meaning.getSynonyms().isEmpty()) {
                        layoutSynonyms.setVisibility(View.VISIBLE);
                        String synonymsText = TextUtils.join(", ", meaning.getSynonyms());
                        tvSynonyms.setText(synonymsText);
                    } else {
                        layoutSynonyms.setVisibility(View.GONE);
                    }

                    // Hiển thị antonyms từ MEANING level (quan trọng!)
                    if (meaning.getAntonyms() != null && !meaning.getAntonyms().isEmpty()) {
                        layoutAntonyms.setVisibility(View.VISIBLE);
                        String antonymsText = TextUtils.join(", ", meaning.getAntonyms());
                        tvAntonyms.setText(antonymsText);
                    } else {
                        layoutAntonyms.setVisibility(View.GONE);
                    }

                    layoutAdditionalMeanings.addView(meaningView);

                    // Thêm divider giữa các meanings (trừ cái cuối cùng)
                    if (i < meanings.size() - 1) {
                        View divider = new View(getContext());
                        divider.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                1
                        ));
                        divider.setBackgroundColor(getResources().getColor(R.color.divider_color));
                        layoutAdditionalMeanings.addView(divider);
                    }
                }
            }
        } else {
            layoutAdditionalMeanings.setVisibility(View.GONE);
        }
    }

    private void setupAudioButton(List<Phonetic> phonetics) {
        String audioUrl = null;

        // Tìm URL audio hợp lệ đầu tiên
        if (phonetics != null) {
            for (Phonetic phonetic : phonetics) {
                if (phonetic.getAudio() != null && !phonetic.getAudio().isEmpty()) {
                    audioUrl = phonetic.getAudio();
                    break;
                }
            }
        }

        final String finalAudioUrl = audioUrl;

        if (finalAudioUrl != null) {
            btnSound.setVisibility(View.VISIBLE);
            btnSound.setOnClickListener(v -> playPronunciation(finalAudioUrl));
        } else {
            btnSound.setVisibility(View.GONE);
        }
    }

    private void playPronunciation(String audioUrl) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> mp.start());

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show());
                return false;
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Lỗi tải âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading() {
        cardDictionaryResult.setVisibility(View.VISIBLE);
        progressBarDictionary.setVisibility(View.VISIBLE);
        tvDictionaryError.setVisibility(View.GONE);
        layoutExample.setVisibility(View.GONE);
        layoutAdditionalMeanings.setVisibility(View.GONE);
        layoutMainSynonymsAntonyms.setVisibility(View.GONE);
        btnSound.setVisibility(View.GONE);

        // Hiển thị các phần nghĩa nhưng với nội dung rỗng
        tvMeaning.setVisibility(View.VISIBLE);
        tvMeaningVietnamese.setVisibility(View.VISIBLE);
        tvMeaning.setText("");
        tvMeaningVietnamese.setText("");
    }

    // Phương thức dịch text
    private void translateText(String text, String targetLang, TranslationCallback callback) {
        translationRepository.translateText(text, targetLang, new TranslationRepository.TranslationCallback() {
            @Override
            public void onSuccess(String translatedText) {
                requireActivity().runOnUiThread(() -> {
                    callback.onTranslationReady(translatedText);
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    // Nếu dịch thất bại, giữ nguyên text gốc
                    callback.onTranslationReady(text);
                });
            }
        });
    }

    // Interface cho callback dịch thuật
    private interface TranslationCallback {
        void onTranslationReady(String translatedText);
    }


    private void showDictionaryError(String message) {
        cardDictionaryResult.setVisibility(View.VISIBLE);
        progressBarDictionary.setVisibility(View.GONE);
        tvDictionaryError.setVisibility(View.VISIBLE);
        tvDictionaryError.setText(message);
        layoutExample.setVisibility(View.GONE);
        layoutAdditionalMeanings.setVisibility(View.GONE);
        layoutMainSynonymsAntonyms.setVisibility(View.GONE);
        btnSound.setVisibility(View.GONE);

        // Ẩn các phần nghĩa
        tvMeaning.setVisibility(View.GONE);
        tvMeaningVietnamese.setVisibility(View.GONE);
    }

    private void hideDictionaryResult() {
        cardDictionaryResult.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        // Header buttons
        btnNotification.setOnClickListener(v -> openNotificationsActivity());
        btnSetting.setOnClickListener(v -> openSettingsActivity());

        // Grid 1 click listeners
        cardGrammar.setOnClickListener(v -> openGrammarActivity());
        cardListening.setOnClickListener(v -> openListeningActivity());
        cardReading.setOnClickListener(v -> openReadingActivity());
        cardVocabulary.setOnClickListener(v -> openVocabularyActivity());

        // Grid 2 click listeners
        cardExercise.setOnClickListener(v -> openExerciseActivity());
        cardNews.setOnClickListener(v -> openNewsActivity());
        cardVideo.setOnClickListener(v -> openVideoActivity());
        cardGame.setOnClickListener(v -> openGameActivity());
        cardBilingual.setOnClickListener(v -> openBilingualActivity());
        cardChat.setOnClickListener(v -> openChatActivity());

        // Grid 3 click listeners
        cardBook.setOnClickListener(v -> openBookActivity());
        cardBrowser.setOnClickListener(v -> openBrowserActivity());
        cardEpub.setOnClickListener(v -> openEpubActivity());
        cardBlog.setOnClickListener(v -> openBlogActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
        dictionaryHandler.removeCallbacks(dictionaryRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerFiles.size() > 1) {
            bannerHandler.postDelayed(bannerRunnable, 3000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        bannerHandler.removeCallbacks(bannerRunnable);
        dictionaryHandler.removeCallbacks(dictionaryRunnable);
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

    private void openGrammarActivity() {
        Intent intent = new Intent(getActivity(), GrammarActivity.class);
        startActivity(intent);
    }

    private void openListeningActivity() {
        Intent intent = new Intent(getActivity(), ListeningActivity.class);
        startActivity(intent);
    }

    private void openReadingActivity() {
        Intent intent = new Intent(getActivity(), ReadingActivity.class);
        startActivity(intent);
    }

    private void openVocabularyActivity() {
        Intent intent = new Intent(getActivity(), VocabularyActivity.class);
        startActivity(intent);
    }

    private void openExerciseActivity() {
        Intent intent = new Intent(getActivity(), ExerciseActivity.class);
        startActivity(intent);
    }

    private void openNewsActivity() {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        startActivity(intent);
    }

    private void openVideoActivity() {
        Intent intent = new Intent(getActivity(), VideoActivity.class);
        startActivity(intent);
    }

    private void openGameActivity() {
        Intent intent = new Intent(getActivity(), GameActivity.class);
        startActivity(intent);
    }

    private void openBilingualActivity() {
        Intent intent = new Intent(getActivity(), BilingualActivity.class);
        startActivity(intent);
    }

    private void openChatActivity() {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        startActivity(intent);
    }

    private void openBookActivity() {
        Intent intent = new Intent(getActivity(), BookActivity.class);
        startActivity(intent);
    }

    private void openBrowserActivity() {
        Intent intent = new Intent(getActivity(), BrowserActivity.class);
        startActivity(intent);
    }

    private void openEpubActivity() {
        Intent intent = new Intent(getActivity(), EpubActivity.class);
        startActivity(intent);
    }

    private void openBlogActivity() {
        Intent intent = new Intent(getActivity(), BlogActivity.class);
        startActivity(intent);
    }
}