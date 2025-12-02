package com.example.englishlearningapp.view.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.englishlearningapp.R;
import com.example.englishlearningapp.data.dao.UserDAO;
import com.example.englishlearningapp.data.dao.WordDAO;
import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.data.model.Word;
import com.example.englishlearningapp.util.DateUtils;
import com.example.englishlearningapp.view.activity.AddWordActivity;
import com.example.englishlearningapp.view.activity.NotificationsActivity;
import com.example.englishlearningapp.view.activity.SettingsActivity;
import com.example.englishlearningapp.view.activity.WordDetailActivity;
import com.example.englishlearningapp.view.activity.WordReviewActivity;
import com.example.englishlearningapp.view.adapter.WordAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WordFragment extends Fragment implements WordAdapter.OnWordItemClickListener {
    private static final String TAG = "WordFragment";

    // UI Components
    private EditText etSearch;
    private TextView tvTotalWords, tvWordsToReview, tvTodayAdded, tvSuccessRate;
    private MaterialCardView cardReview;
    private MaterialButton btnAddWord, btnStartReview, btnAddFirstWord;
    private TabLayout tabLayout;
    private RecyclerView rvWords;
    private ProgressBar progressBar;
    private View layoutEmptyState;
    private FloatingActionButton fabAddWord;

    // Bottom Sheet Views
    private View bottomSheet;
    private TextView tvBottomSheetTitle, tvSheetWord, tvSheetPronunciation, tvSheetPartOfSpeech;
    private TextView tvSheetVietnameseMeaning, tvSheetEnglishDefinition, tvSheetExample;
    private TextView tvSheetExampleTranslation, tvSheetReviewCount, tvSheetSuccessRate, tvSheetMasteryLevel;
    private ImageButton btnCloseBottomSheet, btnPlayPronunciation;
    private MaterialButton btnMarkFavorite, btnPracticeWord;
    private ImageView ivSheetWordImage;

    // Media Player cho audio
    private MediaPlayer mediaPlayer;

    // Data
    private User currentUser;
    private UserDAO userDAO;
    private WordDAO wordDAO;
    private WordAdapter wordAdapter;
    private List<Word> wordList = new ArrayList<>();
    private WordDAO.WordStats wordStats;

    // Current word for bottom sheet
    private Word currentBottomSheetWord;

    // Tab positions
    private static final int TAB_ALL = 0;
    private static final int TAB_REVIEW = 1;
    private static final int TAB_FAVORITE = 2;
    private static final int TAB_RECENT = 3;

    public WordFragment() {
        // Required empty public constructor
    }

    public static WordFragment newInstance() {
        return new WordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDAO = new UserDAO(requireContext());
        wordDAO = new WordDAO(requireContext());
        mediaPlayer = new MediaPlayer();
        loadCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        initViews(view);
        setupSearch();
        setupTabs();
        setupRecyclerView();
        setupBottomSheet();
        setupClickListeners();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadTodayStats();
        loadWords(TAB_ALL);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void loadCurrentUser() {
        String userId = requireActivity().getSharedPreferences("english_learning_prefs", 0).getString("user_id", "");

        if (!TextUtils.isEmpty(userId)) {
            currentUser = userDAO.getUserById(userId);
        }

        if (currentUser == null) {
            Log.e(TAG, "No user found!");
        }
    }

    private void initViews(View view) {
        // Toolbar và Search
        etSearch = view.findViewById(R.id.etSearch);

        tvTotalWords = view.findViewById(R.id.tvTotalWords);
        tvWordsToReview = view.findViewById(R.id.tvWordsToReview);
        tvTodayAdded = view.findViewById(R.id.tvTodayAdded);
        tvSuccessRate = view.findViewById(R.id.tvSuccessRate);
        cardReview = view.findViewById(R.id.cardReview);

        // Buttons
        btnAddWord = view.findViewById(R.id.btnAddWord);
        btnStartReview = view.findViewById(R.id.btnStartReview);
        btnAddFirstWord = view.findViewById(R.id.btnAddFirstWord);

        // Tabs và List
        tabLayout = view.findViewById(R.id.tabLayout);
        rvWords = view.findViewById(R.id.rvWords);
        progressBar = view.findViewById(R.id.progressBar);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // FAB
        fabAddWord = view.findViewById(R.id.fabAddWord);

        // Bottom Sheet
        bottomSheet = view.findViewById(R.id.bottomSheet);
        tvBottomSheetTitle = view.findViewById(R.id.tvBottomSheetTitle);
        tvSheetWord = view.findViewById(R.id.tvSheetWord);
        tvSheetPronunciation = view.findViewById(R.id.tvSheetPronunciation);
        tvSheetPartOfSpeech = view.findViewById(R.id.tvSheetPartOfSpeech);
        tvSheetVietnameseMeaning = view.findViewById(R.id.tvSheetVietnameseMeaning);
        tvSheetEnglishDefinition = view.findViewById(R.id.tvSheetEnglishDefinition);
        tvSheetExample = view.findViewById(R.id.tvSheetExample);
        tvSheetExampleTranslation = view.findViewById(R.id.tvSheetExampleTranslation);
        tvSheetReviewCount = view.findViewById(R.id.tvSheetReviewCount);
        tvSheetSuccessRate = view.findViewById(R.id.tvSheetSuccessRate);
        tvSheetMasteryLevel = view.findViewById(R.id.tvSheetMasteryLevel);
        btnCloseBottomSheet = view.findViewById(R.id.btnCloseBottomSheet);
        btnPlayPronunciation = view.findViewById(R.id.btnPlayPronunciation);
        btnMarkFavorite = view.findViewById(R.id.btnMarkFavorite);
        btnPracticeWord = view.findViewById(R.id.btnPracticeWord);
        ivSheetWordImage = view.findViewById(R.id.ivSheetWordImage);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (TextUtils.isEmpty(query)) {
                    loadWords(tabLayout.getSelectedTabPosition());
                } else {
                    searchWords(query);
                }
            }
        });
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadWords(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupRecyclerView() {
        wordAdapter = new WordAdapter(wordList, this, requireContext());
        rvWords.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWords.setAdapter(wordAdapter);
    }

    private void setupBottomSheet() {
        btnCloseBottomSheet.setOnClickListener(v -> hideBottomSheet());
        btnMarkFavorite.setOnClickListener(v -> toggleFavorite());
        btnPracticeWord.setOnClickListener(v -> {
            if (currentBottomSheetWord != null) {
                showBottomSheetPracticeDialog(currentBottomSheetWord);
            }
        });
        btnPlayPronunciation.setOnClickListener(v -> playPronunciation());

        // Click ảnh để xem toàn màn hình
        ivSheetWordImage.setOnClickListener(v -> {
            if (currentBottomSheetWord != null && currentBottomSheetWord.getImageUrl() != null) {
                openFullscreenImage(currentBottomSheetWord.getImageUrl());
            }
        });
    }

    private void setupClickListeners() {
        btnAddWord.setOnClickListener(v -> openAddWordActivity());
        btnStartReview.setOnClickListener(v -> startReview());
        btnAddFirstWord.setOnClickListener(v -> openAddWordActivity());
        cardReview.setOnClickListener(v -> {
            Objects.requireNonNull(tabLayout.getTabAt(TAB_REVIEW)).select();
        });
        fabAddWord.setOnClickListener(v -> openAddWordActivity());
    }

    private void loadTodayStats() {
        if (currentUser == null) return;

        wordStats = wordDAO.getTodayStats(currentUser.getId());

        tvTotalWords.setText(String.valueOf(wordStats.totalWords));
        tvWordsToReview.setText(String.valueOf(wordStats.wordsToReview));
        tvTodayAdded.setText(String.valueOf(wordStats.wordsAddedToday));
        tvSuccessRate.setText(String.format(Locale.getDefault(), "%.0f%%", wordStats.successRate));

        // Highlight review card if there are words to review
        if (wordStats.wordsToReview > 0) {
            cardReview.setCardBackgroundColor(requireContext().getColor(R.color.orange_light));
        } else {
            cardReview.setCardBackgroundColor(requireContext().getColor(R.color.yellow_light));
        }
    }

    private void loadWords(int tabPosition) {
        if (currentUser == null) {
            showEmptyState();
            return;
        }

        showLoading();
        wordList.clear();

        switch (tabPosition) {
            case TAB_ALL:
                wordList.addAll(wordDAO.getWordsByUser(currentUser.getId()));
                break;

            case TAB_REVIEW:
                wordList.addAll(wordDAO.getWordsNeedReview(currentUser.getId()));
                break;

            case TAB_FAVORITE:
                wordList.addAll(wordDAO.getFavoriteWords(currentUser.getId()));
                break;

            case TAB_RECENT:
                // Get words added in the last 7 days
                List<Word> allWords = wordDAO.getWordsByUser(currentUser.getId());
                long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L);
                for (Word word : allWords) {
                    if (word.getAddedDate() >= sevenDaysAgo) {
                        wordList.add(word);
                    }
                }
                break;
        }

        hideLoading();
        updateUI();
    }

    private void searchWords(String query) {
        if (currentUser == null || TextUtils.isEmpty(query)) return;

        wordList.clear();
        wordList.addAll(wordDAO.searchWords(currentUser.getId(), query));
        wordAdapter.notifyDataSetChanged();
        updateUI();
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        rvWords.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void updateUI() {
        if (wordList.isEmpty()) {
            showEmptyState();
        } else {
            showWordList();
        }
        wordAdapter.notifyDataSetChanged();
    }

    private void showEmptyState() {
        rvWords.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private void showWordList() {
        rvWords.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
    }

    private void refreshData() {
        loadTodayStats();
        loadWords(tabLayout.getSelectedTabPosition());
    }

    private void openAddWordActivity() {
        Intent intent = new Intent(requireActivity(), AddWordActivity.class);
        intent.putExtra("user_id", currentUser != null ? currentUser.getId() : "");
        startActivity(intent);
    }

    private void startReview() {
        if (wordStats.wordsToReview > 0) {
            Intent intent = new Intent(requireContext(), WordReviewActivity.class);
            intent.putExtra("user_id", currentUser != null ? currentUser.getId() : "");
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Không có từ nào cần ôn hôm nay", Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomSheetPracticeDialog(Word word) {
        String[] options = {
                "Đưa vào danh sách cần ôn",
                "Luyện tập ngay",
                "Lên lịch ôn tập",
                "Hủy"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn cho: " + word.getEnglishWord())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // Đưa vào danh sách cần ôn
                            word.setNextReviewDate(System.currentTimeMillis());
                            word.setLastReviewed(System.currentTimeMillis());
                            wordDAO.updateWord(word);

                            Toast.makeText(requireContext(),
                                    "Đã thêm vào danh sách cần ôn",
                                    Toast.LENGTH_SHORT).show();

                            refreshData();
                            hideBottomSheet();
                            break;

                        case 1: // Luyện tập ngay
                            startPracticeActivity(word);
                            break;

                        case 2: // Lên lịch ôn tập
                            showScheduleDialog(word);
                            break;

                        case 3: // Hủy
                            break;
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    // THÊM PHƯƠNG THỨC Lên lịch ôn tập
    private void showScheduleDialog(Word word) {
        String[] scheduleOptions = {
                "Ôn sau 1 giờ",
                "Ôn sau 3 giờ",
                "Ôn sau 6 giờ",
                "Ôn sau 12 giờ",
                "Ôn sau 1 ngày",
                "Ôn sau 3 ngày",
                "Ôn sau 1 tuần",
                "Hủy"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Lên lịch ôn tập: " + word.getEnglishWord())
                .setItems(scheduleOptions, (dialog, which) -> {
                    long delayMillis = 0;
                    String message = "";

                    switch (which) {
                        case 0:
                            delayMillis = 60 * 60 * 1000L;
                            message = "1 giờ";
                            break;
                        case 1:
                            delayMillis = 3 * 60 * 60 * 1000L;
                            message = "3 giờ";
                            break;
                        case 2:
                            delayMillis = 6 * 60 * 60 * 1000L;
                            message = "6 giờ";
                            break;
                        case 3:
                            delayMillis = 12 * 60 * 60 * 1000L;
                            message = "12 giờ";
                            break;
                        case 4:
                            delayMillis = 24 * 60 * 60 * 1000L;
                            message = "1 ngày";
                            break;
                        case 5:
                            delayMillis = 3 * 24 * 60 * 60 * 1000L;
                            message = "3 ngày";
                            break;
                        case 6:
                            delayMillis = 7 * 24 * 60 * 60 * 1000L;
                            message = "1 tuần";
                            break;
                        case 7:
                            return; // Hủy
                    }

                    // Tính thời gian ôn tập mới
                    long nextReviewTime = System.currentTimeMillis() + delayMillis;
                    word.setNextReviewDate(nextReviewTime);
                    wordDAO.updateWord(word);

                    Toast.makeText(requireContext(),
                            "Đã lên lịch ôn tập sau " + message,
                            Toast.LENGTH_SHORT).show();

                    refreshData();
                    hideBottomSheet();
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    // THÊM PHƯƠNG THỨC Mở activity luyện tập
    private void startPracticeActivity(Word word) {
        // Tạo intent mở activity luyện tập
        Toast.makeText(requireContext(),
                "Mở chế độ luyện tập cho: " + word.getEnglishWord(),
                Toast.LENGTH_SHORT).show();

        // TODO: Mở activity luyện tập
        // Intent intent = new Intent(requireContext(), PracticeActivity.class);
        // intent.putExtra("word_id", word.getId());
        // startActivity(intent);
    }

    // WordAdapter callbacks
    @Override
    public void onWordClick(Word word) {
        showWordDetail(word);
    }

    @Override
    public void onFavoriteClick(Word word, boolean isFavorite) {
        word.setFavorite(isFavorite);
        wordDAO.updateWord(word);
        Toast.makeText(requireContext(), isFavorite ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
        refreshData();
    }

    @Override
    public void onPracticeClick(Word word) {
        showPracticeModeDialog(word);
    }

    @Override
    public void onEditClick(Word word) {
        openEditWord(word);
    }

    @Override
    public void onDeleteClick(Word word) {
        deleteWord(word);
    }

    @Override
    public void onAudioPlayClick(Word word) {
        playWordAudio(word);
    }

    private void showWordDetail(Word word) {
        currentBottomSheetWord = word;

        // Populate bottom sheet
        tvSheetWord.setText(word.getEnglishWord());
        tvSheetPronunciation.setText(word.getPronunciation() != null ? "/" + word.getPronunciation() + "/" : "");
        tvSheetPartOfSpeech.setText(word.getPartOfSpeech());
        tvSheetVietnameseMeaning.setText(word.getVietnameseMeaning());
        tvSheetEnglishDefinition.setText(word.getEnglishDefinition());
        tvSheetExample.setText(word.getExampleSentence());
        tvSheetExampleTranslation.setText(word.getExampleTranslation());
        tvSheetReviewCount.setText(String.valueOf(word.getReviewCount()));
        tvSheetSuccessRate.setText(String.format(Locale.getDefault(), "%.0f%%", word.getSuccessRate()));
        tvSheetMasteryLevel.setText(word.getMasteryLevelText());

        // Hiển thị ảnh nếu có
        if (word.getImageUrl() != null && !word.getImageUrl().isEmpty()) {
            ivSheetWordImage.setVisibility(View.VISIBLE);
            Glide.with(requireContext()).load(word.getImageUrl()).transform(new CenterCrop(), new RoundedCorners(12)).placeholder(R.drawable.ic_image_placeholder).error(R.drawable.ic_image_error).into(ivSheetWordImage);
        } else {
            ivSheetWordImage.setVisibility(View.GONE);
        }

        // Enable/disable audio button
        if (word.getAudioUrl() != null && !word.getAudioUrl().isEmpty()) {
            btnPlayPronunciation.setVisibility(View.VISIBLE);
        } else {
            btnPlayPronunciation.setVisibility(View.GONE);
        }

        // Update favorite button
        updateFavoriteButton(word.isFavorite());

        // Show bottom sheet
        bottomSheet.setVisibility(View.VISIBLE);
    }

    private void hideBottomSheet() {
        bottomSheet.setVisibility(View.GONE);
        currentBottomSheetWord = null;
    }

    private void updateFavoriteButton(boolean isFavorite) {
        if (isFavorite) {
            btnMarkFavorite.setIconResource(R.drawable.ic_favorite_24);
            btnMarkFavorite.setText("Bỏ yêu thích");
            btnMarkFavorite.setIconTintResource(R.color.favorite_red);
        } else {
            btnMarkFavorite.setIconResource(R.drawable.ic_favorite_border_24);
            btnMarkFavorite.setText("Yêu thích");
            btnMarkFavorite.setIconTintResource(R.color.gray);
        }
    }

    private void toggleFavorite() {
        if (currentBottomSheetWord != null) {
            boolean newFavoriteState = !currentBottomSheetWord.isFavorite();
            currentBottomSheetWord.setFavorite(newFavoriteState);
            wordDAO.updateWord(currentBottomSheetWord);
            updateFavoriteButton(newFavoriteState);
            refreshData();
        }
    }


    private void showPracticeModeDialog(Word word) {
        String[] practiceModes = {
                "Học từ mới",
                "Ôn tập nhanh",
                "Kiểm tra viết",
                "Nghe và chọn",
                "Trắc nghiệm",
                "Hủy"
        };

        new AlertDialog.Builder(requireContext())
                .setTitle("Chọn chế độ luyện tập: " + word.getEnglishWord())
                .setItems(practiceModes, (dialog, which) -> {
                    switch (which) {
                        case 0: // Học từ mới
                            // Đặt lại ngày ôn tập
                            resetWordForReview(word);
                            startLearningMode(word);
                            break;

                        case 1: // Ôn tập nhanh
                            resetWordForReview(word);
                            startQuickReview(word);
                            break;

                        case 2: // Kiểm tra viết
                            //startWritingTest(word);
                            Toast.makeText(requireContext(), "Tính năng tra từ điển đang được phát triển", Toast.LENGTH_SHORT).show();
                            break;

                        case 3: // Nghe và chọn
                            //startListeningTest(word);
                            Toast.makeText(requireContext(), "Tính năng tra từ điển đang được phát triển", Toast.LENGTH_SHORT).show();
                            break;

                        case 4: // Trắc nghiệm
                            //startQuizMode(word);
                            Toast.makeText(requireContext(), "Tính năng tra từ điển đang được phát triển", Toast.LENGTH_SHORT).show();
                            break;

                        case 5: // Hủy
                            break;
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    // THÊM PHƯƠNG THỨC resetWordForReview
    private void resetWordForReview(Word word) {
        // Đặt lại nextReviewDate về hiện tại
        word.setNextReviewDate(System.currentTimeMillis());
        word.setLastReviewed(System.currentTimeMillis());
        word.setReviewCount(word.getReviewCount() + 1);

        // Cập nhật vào database
        wordDAO.updateWord(word);

        // Thông báo
        Toast.makeText(requireContext(),
                "Đã đưa từ \"" + word.getEnglishWord() + "\" vào danh sách cần ôn",
                Toast.LENGTH_SHORT).show();

        // Refresh dữ liệu
        refreshData();
    }

    // THÊM CÁC PHƯƠNG THỨC LUYỆN TẬP
    private void startLearningMode(Word word) {
        // Mở activity học từ mới
        showLearningDialog(word);
    }

    private void startQuickReview(Word word) {
        // Ôn tập nhanh
        showQuickReviewDialog(word);
    }

    private void showLearningDialog(Word word) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Học từ: " + word.getEnglishWord())
                .setMessage("Bạn sẽ học từ này với:\n\n" +
                        "• Phát âm: " + word.getPronunciation() + "\n" +
                        "• Nghĩa: " + word.getVietnameseMeaning() + "\n" +
                        "• Ví dụ: " + word.getExampleSentence())
                .setPositiveButton("Bắt đầu học", (dialog, which) -> {
                    // Logic học từ
                    Toast.makeText(requireContext(), "Bắt đầu học từ", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showQuickReviewDialog(Word word) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Ôn tập nhanh: " + word.getEnglishWord())
                .setMessage("Nghĩa của từ này là gì?")
                .setPositiveButton("Xem đáp án", (dialog, which) -> {
                    // Hiển thị đáp án
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Đáp án")
                            .setMessage(word.getEnglishWord() + " = " + word.getVietnameseMeaning())
                            .setPositiveButton("Tiếp tục", null)
                            .show();
                })
                .setNegativeButton("Bỏ qua", null)
                .show();
    }

    private void openEditWord(Word word) {
        Intent intent = new Intent(requireActivity(), AddWordActivity.class);
        intent.putExtra("word", word);
        intent.putExtra("is_edit", true);
        intent.putExtra("user_id", currentUser != null ? currentUser.getId() : "");
        startActivity(intent);
    }

    private void deleteWord(Word word) {
        // Delete from database
        boolean success = wordDAO.deleteWord(word.getId()) > 0;

        if (success) {
            // Remove from list
            wordAdapter.removeWord(word);

            // Update stats
            refreshData();

            Toast.makeText(requireContext(), "Đã xóa từ: " + word.getEnglishWord(), Toast.LENGTH_SHORT).show();

            // If we're viewing this word in bottom sheet, hide it
            if (currentBottomSheetWord != null && currentBottomSheetWord.getId().equals(word.getId())) {
                hideBottomSheet();
            }
        } else {
            Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void playWordAudio(Word word) {
        if (word.getAudioUrl() == null || word.getAudioUrl().isEmpty()) {
            Toast.makeText(requireContext(), "Không có phát âm cho từ này", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(word.getAudioUrl());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                Toast.makeText(requireContext(), "Đang phát âm thanh", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                Toast.makeText(requireContext(), "Phát âm hoàn tất", Toast.LENGTH_SHORT).show();
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(requireContext(), "Lỗi phát âm thanh", Toast.LENGTH_SHORT).show();
                return false;
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Lỗi tải âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPronunciation() {
        if (currentBottomSheetWord != null) {
            playWordAudio(currentBottomSheetWord);
        }
    }

    private void openFullscreenImage(String imageUrl) {
        // Mở activity hoặc dialog để xem ảnh toàn màn hình
        // Intent intent = new Intent(requireContext(), FullscreenImageActivity.class);
        // intent.putExtra("image_url", imageUrl);
        // startActivity(intent);
        Toast.makeText(requireContext(), "Mở ảnh toàn màn hình: " + imageUrl, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userDAO != null) {
            userDAO.close();
        }
        if (wordDAO != null) {
            wordDAO.close();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
