package com.example.englishlearningapp.view.features_home.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.englishlearningapp.data.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class MessageChatDAO {

    private final DbHelper dbHelper;

    public MessageChatDAO(Context context) {
        this.dbHelper = new DbHelper(context);
    }

    // =====================================================
    // INSERT MESSAGE
    // =====================================================
    public void addMessageChat(MessageChat message) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_MESSAGE_CONTENT, message.content);
        values.put(DbHelper.COLUMN_MESSAGE_IS_USER, message.isUser ? 1 : 0);
        values.put(DbHelper.COLUMN_MESSAGE_TIMESTAMP, message.timestamp);

        db.insert(DbHelper.TABLE_MESSAGE_CHAT, null, values);
        db.close();
    }

    // =====================================================
    // GET ALL CHAT MESSAGES
    // =====================================================
    public List<MessageChat> getAllMessageChat() {
        List<MessageChat> messages = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM " + DbHelper.TABLE_MESSAGE_CHAT +
                " ORDER BY " + DbHelper.COLUMN_MESSAGE_TIMESTAMP + " ASC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                MessageChat msg = new MessageChat();
                msg.id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_MESSAGE_ID));
                msg.content = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_MESSAGE_CONTENT));
                msg.isUser = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_MESSAGE_IS_USER)) == 1;
                msg.timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_MESSAGE_TIMESTAMP));

                messages.add(msg);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return messages;
    }

    // =====================================================
    // CLEAR ALL MESSAGES
    // =====================================================
    public void clearChat() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DbHelper.TABLE_MESSAGE_CHAT, null, null);
        db.close();
    }
}
