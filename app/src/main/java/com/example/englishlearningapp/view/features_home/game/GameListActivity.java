package com.example.englishlearningapp.view.features_home.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.englishlearningapp.R;

public class GameListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        setupClickListeners();
    }

    private void setupClickListeners() {
        // Hangman Game
        CardView cardHangman = findViewById(R.id.cardHangman);
        if (cardHangman != null) {
            cardHangman.setOnClickListener(v -> {
                Intent intent = new Intent(this, GameSettingsActivity.class);
                intent.putExtra("GAME_TYPE", "HANGMAN");
                startActivity(intent);
            });
        }
        
        // Word Scramble Game
        CardView cardWordScramble = findViewById(R.id.cardWordScramble);
        if (cardWordScramble != null) {
            cardWordScramble.setOnClickListener(v -> {
                Intent intent = new Intent(this, GameSettingsActivity.class);
                intent.putExtra("GAME_TYPE", "WORD_SCRAMBLE");
                startActivity(intent);
            });
        }
        
        // Memory Match Game (Coming Soon)
        CardView cardMemoryMatch = findViewById(R.id.cardMemoryMatch);
        if (cardMemoryMatch != null) {
            cardMemoryMatch.setOnClickListener(v -> {
                showComingSoonMessage("🃏 Memory Match");
            });
        }
        
        // Typing Test Game (Coming Soon)
        CardView cardTypingTest = findViewById(R.id.cardTypingTest);
        if (cardTypingTest != null) {
            cardTypingTest.setOnClickListener(v -> {
                showComingSoonMessage("⌨️ Typing Test");
            });
        }
        
        // Picture Match Game (Coming Soon)
        CardView cardPictureMatch = findViewById(R.id.cardPictureMatch);
        if (cardPictureMatch != null) {
            cardPictureMatch.setOnClickListener(v -> {
                showComingSoonMessage("🖼️ Picture Match");
            });
        }
        
        // Quiz Adventure Game (Coming Soon)
        CardView cardQuizAdventure = findViewById(R.id.cardQuizAdventure);
        if (cardQuizAdventure != null) {
            cardQuizAdventure.setOnClickListener(v -> {
                showComingSoonMessage("🗺️ Quiz Adventure");
            });
        }
    }
    
    private void showComingSoonMessage(String gameName) {
        android.widget.Toast.makeText(this, 
            gameName + " is coming soon! Stay tuned for updates 🚀", 
            android.widget.Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
