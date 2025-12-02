package com.example.englishlearningapp.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.englishlearningapp.data.model.Word;
import com.example.englishlearningapp.data.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class WordDAO {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;
    private static final String TAG = "WordDAO";

    public WordDAO(Context context) {
        dbHelper = new DbHelper(context.getApplicationContext());
        open();
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening writable database", e);
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }

    // INSERT
    public long insertWord(Word word) {
        dbOpen();
        ContentValues values = wordToContentValues(word);

        long result = -1;
        try {
            result = db.insertOrThrow(DbHelper.TABLE_WORD, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Insert word failed", e);
        }
        return result;
    }

    // UPDATE
    public int updateWord(Word word) {
        dbOpen();
        ContentValues values = wordToContentValues(word);

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_WORD, values,
                    DbHelper.COLUMN_WORD_ID + "=?", new String[]{word.getId()});
        } catch (Exception e) {
            Log.e(TAG, "Update word failed", e);
        }
        return result;
    }

    // DELETE
    public int deleteWord(String wordId) {
        dbOpen();
        int result = 0;
        try {
            result = db.delete(DbHelper.TABLE_WORD,
                    DbHelper.COLUMN_WORD_ID + "=?", new String[]{wordId});
        } catch (Exception e) {
            Log.e(TAG, "Delete word failed", e);
        }
        return result;
    }

    // GET BY ID
    public Word getWordById(String wordId) {
        dbOpen();
        Cursor cursor = null;
        try {
            cursor = db.query(DbHelper.TABLE_WORD,
                    null,
                    DbHelper.COLUMN_WORD_ID + " = ?",
                    new String[]{wordId},
                    null, null, null);

            if (cursor.moveToFirst()) {
                return cursorToWord(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "getWordById failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // GET ALL WORDS BY USER
    public List<Word> getWordsByUser(String userId) {
        dbOpen();
        List<Word> wordList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DbHelper.TABLE_WORD,
                    null,
                    DbHelper.COLUMN_USER_ID + " = ?",
                    new String[]{userId},
                    null, null,
                    DbHelper.COLUMN_ADDED_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    wordList.add(cursorToWord(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getWordsByUser failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return wordList;
    }

    // GET FAVORITE WORDS BY USER
    public List<Word> getFavoriteWords(String userId) {
        dbOpen();
        List<Word> wordList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DbHelper.TABLE_WORD,
                    null,
                    DbHelper.COLUMN_USER_ID + " = ? AND " +
                            DbHelper.COLUMN_IS_FAVORITE + " = 1",
                    new String[]{userId},
                    null, null,
                    DbHelper.COLUMN_ADDED_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    wordList.add(cursorToWord(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getFavoriteWords failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return wordList;
    }

    // GET WORDS NEED REVIEW (FINAL VERSION)
    public List<Word> getWordsNeedReview(String userId) {
        dbOpen();
        List<Word> wordList = new ArrayList<>();
        Cursor cursor = null;

        try {
            long currentTime = System.currentTimeMillis();

            // Query từ hàm dưới, gắn vào phiên bản hàm trên
            String query =
                    "SELECT * FROM " + DbHelper.TABLE_WORD +
                            " WHERE " + DbHelper.COLUMN_USER_ID + " = ?" +
                            " AND (" +
                            DbHelper.COLUMN_NEXT_REVIEW_DATE + " <= ?" +
                            " OR " + DbHelper.COLUMN_NEXT_REVIEW_DATE + " = 0" +
                            " OR " + DbHelper.COLUMN_NEXT_REVIEW_DATE + " IS NULL" +
                            ")" +
                            " ORDER BY " + DbHelper.COLUMN_PRIORITY + " DESC, " +
                            DbHelper.COLUMN_NEXT_REVIEW_DATE + " ASC";

            cursor = db.rawQuery(query, new String[]{
                    userId,
                    String.valueOf(currentTime)
            });

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    wordList.add(cursorToWord(cursor));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.e(TAG, "getWordsNeedReview failed", e);

        } finally {
            if (cursor != null) cursor.close();
        }

        // DEBUG log
        Log.d("WordDAO", "Found " + wordList.size() + " words to review for user " + userId);

        for (Word w : wordList) {
            Log.d("WordDAO", "Review word: " + w.getEnglishWord()
                    + ", Next review: " + w.getNextReviewDate()
                    + ", Current: " + System.currentTimeMillis());
        }

        return wordList;
    }


    // GET TODAY'S STATS
    public WordStats getTodayStats(String userId) {
        dbOpen();
        WordStats stats = new WordStats();
        Cursor cursor = null;

        try {
            // Tổng số từ
            String totalSql = "SELECT COUNT(*) FROM " + DbHelper.TABLE_WORD +
                    " WHERE " + DbHelper.COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(totalSql, new String[]{userId});
            if (cursor.moveToFirst()) {
                stats.totalWords = cursor.getInt(0);
            }
            cursor.close();

            // Từ đã thành thạo
            String masteredSql = "SELECT COUNT(*) FROM " + DbHelper.TABLE_WORD +
                    " WHERE " + DbHelper.COLUMN_USER_ID + " = ? AND " +
                    DbHelper.COLUMN_MASTERY_LEVEL + " = " + Word.MASTERY_MASTERED;
            cursor = db.rawQuery(masteredSql, new String[]{userId});
            if (cursor.moveToFirst()) {
                stats.masteredWords = cursor.getInt(0);
            }
            cursor.close();

            // Từ cần ôn hôm nay
            long currentTime = System.currentTimeMillis();
            String reviewSql = "SELECT COUNT(*) FROM " + DbHelper.TABLE_WORD +
                    " WHERE " + DbHelper.COLUMN_USER_ID + " = ? AND " +
                    DbHelper.COLUMN_NEXT_REVIEW_DATE + " <= ?";
            cursor = db.rawQuery(reviewSql, new String[]{userId, String.valueOf(currentTime)});
            if (cursor.moveToFirst()) {
                stats.wordsToReview = cursor.getInt(0);
            }
            cursor.close();

            // Từ thêm hôm nay
            long todayStart = getStartOfDay();
            String todaySql = "SELECT COUNT(*) FROM " + DbHelper.TABLE_WORD +
                    " WHERE " + DbHelper.COLUMN_USER_ID + " = ? AND " +
                    DbHelper.COLUMN_ADDED_DATE + " >= ?";
            cursor = db.rawQuery(todaySql, new String[]{userId, String.valueOf(todayStart)});
            if (cursor.moveToFirst()) {
                stats.wordsAddedToday = cursor.getInt(0);
            }

            // Tính tỷ lệ thành công
            String successSql = "SELECT SUM(" + DbHelper.COLUMN_REVIEW_COUNT + "), " +
                    "SUM(" + DbHelper.COLUMN_CORRECT_COUNT + ") FROM " +
                    DbHelper.TABLE_WORD + " WHERE " +
                    DbHelper.COLUMN_USER_ID + " = ?";
            cursor = db.rawQuery(successSql, new String[]{userId});
            if (cursor.moveToFirst()) {
                int totalReviews = cursor.getInt(0);
                int totalCorrect = cursor.getInt(1);
                if (totalReviews > 0) {
                    stats.successRate = (double) totalCorrect / totalReviews * 100;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getTodayStats failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return stats;
    }

    // SEARCH WORDS
    public List<Word> searchWords(String userId, String query) {
        dbOpen();
        List<Word> wordList = new ArrayList<>();
        Cursor cursor = null;
        try {
            String selection = DbHelper.COLUMN_USER_ID + " = ? AND (" +
                    DbHelper.COLUMN_ENGLISH_WORD + " LIKE ? OR " +
                    DbHelper.COLUMN_VIETNAMESE_MEANING + " LIKE ? OR " +
                    DbHelper.COLUMN_ENGLISH_DEFINITION + " LIKE ?)";

            String[] selectionArgs = new String[]{
                    userId,
                    "%" + query + "%",
                    "%" + query + "%",
                    "%" + query + "%"
            };

            cursor = db.query(DbHelper.TABLE_WORD,
                    null,
                    selection,
                    selectionArgs,
                    null, null,
                    DbHelper.COLUMN_ADDED_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    wordList.add(cursorToWord(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "searchWords failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return wordList;
    }

    // HELPER: Đảm bảo DB mở
    private void dbOpen() {
        if (db == null || !db.isOpen()) {
            open();
        }
    }

    // HELPER: Chuyển Word → ContentValues
    private ContentValues wordToContentValues(Word word) {
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_WORD_ID, word.getId());
        values.put(DbHelper.COLUMN_USER_ID, word.getUserId());
        values.put(DbHelper.COLUMN_ENGLISH_WORD, word.getEnglishWord());
        values.put(DbHelper.COLUMN_PRONUNCIATION, word.getPronunciation());
        values.put(DbHelper.COLUMN_VIETNAMESE_MEANING, word.getVietnameseMeaning());
        values.put(DbHelper.COLUMN_ENGLISH_DEFINITION, word.getEnglishDefinition());
        values.put(DbHelper.COLUMN_EXAMPLE_SENTENCE, word.getExampleSentence());
        values.put(DbHelper.COLUMN_EXAMPLE_TRANSLATION, word.getExampleTranslation());
        values.put(DbHelper.COLUMN_PART_OF_SPEECH, word.getPartOfSpeech());
        values.put(DbHelper.COLUMN_SYNONYMS, word.getSynonyms());
        values.put(DbHelper.COLUMN_ANTONYMS, word.getAntonyms());
        values.put(DbHelper.COLUMN_TAGS, word.getTags());
        values.put(DbHelper.COLUMN_IMAGE_URL, word.getImageUrl());
        values.put(DbHelper.COLUMN_AUDIO_URL, word.getAudioUrl());
        values.put(DbHelper.COLUMN_DIFFICULTY_LEVEL, word.getDifficultyLevel());
        values.put(DbHelper.COLUMN_MASTERY_LEVEL, word.getMasteryLevel());
        values.put(DbHelper.COLUMN_ADDED_DATE, word.getAddedDate());
        values.put(DbHelper.COLUMN_LAST_REVIEWED, word.getLastReviewed());
        values.put(DbHelper.COLUMN_NEXT_REVIEW_DATE, word.getNextReviewDate());
        values.put(DbHelper.COLUMN_REVIEW_COUNT, word.getReviewCount());
        values.put(DbHelper.COLUMN_CORRECT_COUNT, word.getCorrectCount());
        values.put(DbHelper.COLUMN_WRONG_COUNT, word.getWrongCount());
        values.put(DbHelper.COLUMN_IS_FAVORITE, word.isFavorite() ? 1 : 0);
        values.put(DbHelper.COLUMN_NOTES, word.getNotes());
        values.put(DbHelper.COLUMN_PRIORITY, word.getPriority());
        values.put(DbHelper.COLUMN_SOURCE, word.getSource());
        return values;
    }

    // HELPER: Chuyển Cursor → Word
    private Word cursorToWord(Cursor cursor) {
        Word word = new Word();
        word.setId(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_WORD_ID)));
        word.setUserId(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_USER_ID)));
        word.setEnglishWord(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ENGLISH_WORD)));
        word.setPronunciation(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PRONUNCIATION)));
        word.setVietnameseMeaning(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_VIETNAMESE_MEANING)));
        word.setEnglishDefinition(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ENGLISH_DEFINITION)));
        word.setExampleSentence(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_EXAMPLE_SENTENCE)));
        word.setExampleTranslation(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_EXAMPLE_TRANSLATION)));
        word.setPartOfSpeech(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PART_OF_SPEECH)));
        word.setSynonyms(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_SYNONYMS)));
        word.setAntonyms(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ANTONYMS)));
        word.setTags(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_TAGS)));
        word.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IMAGE_URL)));
        word.setAudioUrl(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_AUDIO_URL)));
        word.setDifficultyLevel(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_DIFFICULTY_LEVEL)));
        word.setMasteryLevel(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_MASTERY_LEVEL)));
        word.setAddedDate(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ADDED_DATE)));
        word.setLastReviewed(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_LAST_REVIEWED)));
        word.setNextReviewDate(cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NEXT_REVIEW_DATE)));
        word.setReviewCount(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_REVIEW_COUNT)));
        word.setCorrectCount(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_CORRECT_COUNT)));
        word.setWrongCount(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_WRONG_COUNT)));
        word.setFavorite(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_IS_FAVORITE)) == 1);
        word.setNotes(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_NOTES)));
        word.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PRIORITY)));
        word.setSource(cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_SOURCE)));
        return word;
    }

    // HELPER: Lấy thời điểm bắt đầu ngày hôm nay
    private long getStartOfDay() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    // Class cho thống kê
    public static class WordStats {
        public int totalWords;
        public int masteredWords;
        public int wordsToReview;
        public int wordsAddedToday;
        public double successRate;

        public WordStats() {
            totalWords = 0;
            masteredWords = 0;
            wordsToReview = 0;
            wordsAddedToday = 0;
            successRate = 0.0;
        }
    }
}