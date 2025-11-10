package com.example.englishlearningapp.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.englishlearningapp.R;
import com.example.englishlearningapp.view.activity.NotificationsActivity;
import com.example.englishlearningapp.view.activity.SettingsActivity;
import com.example.englishlearningapp.view.features_home.bilingual.BilingualActivity;
import com.example.englishlearningapp.view.features_home.blog.BlogActivity;
import com.example.englishlearningapp.view.features_home.book.BookActivity;
import com.example.englishlearningapp.view.features_home.browser.BrowserActivity;
import com.example.englishlearningapp.view.features_home.chat.ChatActivity;
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
import com.google.android.material.textfield.TextInputLayout;

public class HomeFragment extends Fragment {

    private MaterialButton btnNotification, btnSetting;
    private TextInputLayout dictionarySearchLayout;
    private MaterialCardView cardGrammar, cardListening, cardReading, cardVocabulary;
    private MaterialCardView cardExercise, cardNews, cardVideo, cardGame, cardBilingual, cardChat;
    private MaterialCardView cardBook, cardBrowser, cardEpub, cardBlog;
    private MaterialCardView cardIeltsVocabulary;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        setupClickListeners();
        return view;
    }

    private void initViews(View view) {
        // Header buttons
        btnNotification = view.findViewById(R.id.btnNotification);
        btnSetting = view.findViewById(R.id.btnSetting);

//        // Dictionary search
//        dictionarySearchLayout = view.findViewById(R.id.dictionary_search_layout);

//        // IELTS Vocabulary card
//        cardIeltsVocabulary = view.findViewById(R.id.card_ielts_vocabulary);

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

    private void setupClickListeners() {
        // Header buttons
        btnNotification.setOnClickListener(v -> openNotificationsActivity());
        btnSetting.setOnClickListener(v -> openSettingsActivity());

//        // Dictionary search
//        dictionarySearchLayout.setEndIconOnClickListener(v -> openDictionaryActivity());
//
//        // IELTS Vocabulary card
//        cardIeltsVocabulary.setOnClickListener(v -> openIeltsVocabularyActivity());

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

    // Header activities
    private void openNotificationsActivity() {
        Intent intent = new Intent(getActivity(), NotificationsActivity.class);
        startActivity(intent);
    }

    private void openSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

//    // Dictionary activity
//    private void openDictionaryActivity() {
//        Intent intent = new Intent(getActivity(), DictionaryActivity.class);
//        startActivity(intent);
//    }
//
//    // IELTS Vocabulary activity
//    private void openIeltsVocabularyActivity() {
//        Intent intent = new Intent(getActivity(), IeltsVocabularyActivity.class);
//        startActivity(intent);
//    }

    // Grid 1 activities
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

    // Grid 2 activities
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

    // Grid 3 activities
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