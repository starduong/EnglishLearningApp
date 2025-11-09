package com.example.englishlearningapp.data.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.englishlearningapp.data.model.User;
import com.example.englishlearningapp.data.sqlite.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DbHelper dbHelper;
    private SQLiteDatabase db;
    private static final String TAG = "UserDAO";

    public UserDAO(Context context) {
        dbHelper = new DbHelper(context.getApplicationContext());
        open(); // MỞ DB NGAY KHI KHỞI TẠO
    }

    public void open() {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "Error opening writable database, trying readable", e);
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
    public long insertUser(User user) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID, user.getId());
        values.put(DbHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DbHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DbHelper.COLUMN_EMAIL, user.getEmail());
        values.put(DbHelper.COLUMN_PHONE, user.getPhone());
        values.put(DbHelper.COLUMN_FULLNAME, user.getFullname());
        values.put(DbHelper.COLUMN_BIRTH, user.getBirth());
        values.put(DbHelper.COLUMN_AVATAR, user.getAvatar());

        long result = -1;
        try {
            result = db.insertOrThrow(DbHelper.TABLE_USER, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Insert failed", e);
        }
        return result;
    }

    // UPDATE
    public int updateUser(User user) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_USERNAME, user.getUsername());
        values.put(DbHelper.COLUMN_PASSWORD, user.getPassword());
        values.put(DbHelper.COLUMN_EMAIL, user.getEmail());
        values.put(DbHelper.COLUMN_PHONE, user.getPhone());
        values.put(DbHelper.COLUMN_FULLNAME, user.getFullname());
        values.put(DbHelper.COLUMN_BIRTH, user.getBirth());
        values.put(DbHelper.COLUMN_AVATAR, user.getAvatar());

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_USER, values,
                    DbHelper.COLUMN_ID + "=?", new String[]{user.getId()});
        } catch (Exception e) {
            Log.e(TAG, "Update failed", e);
        }
        return result;
    }

    // DELETE
    public int deleteUser(String idUser) {
        dbOpen();
        int result = 0;
        try {
            result = db.delete(DbHelper.TABLE_USER,
                    DbHelper.COLUMN_ID + "=?", new String[]{idUser});
        } catch (Exception e) {
            Log.e(TAG, "Delete failed", e);
        }
        return result;
    }

    // GET ALL
    public List<User> getAllUser() {
        dbOpen();
        List<User> userList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = db.query(DbHelper.TABLE_USER, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    User user = cursorToUser(cursor);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllUser failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return userList;
    }

    // GET BY USERNAME
    public User getUserByUsername(String username) {
        dbOpen();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DbHelper.TABLE_USER,
                    null,
                    DbHelper.COLUMN_USERNAME + " = ? COLLATE NOCASE",
                    new String[]{username},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserByUsername failed", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    // CHECK USERNAME EXISTS
    public boolean isUsernameExists(String username) {
        dbOpen();
        String sql = "SELECT 1 FROM " + DbHelper.TABLE_USER + " WHERE " + DbHelper.COLUMN_USERNAME + " = ? LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{username});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "isUsernameExists error", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // CHECK EMAIL EXISTS
    public boolean isEmailExists(String email) {
        dbOpen();
        String sql = "SELECT 1 FROM " + DbHelper.TABLE_USER + " WHERE " + DbHelper.COLUMN_EMAIL + " = ? LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{email});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "isEmailExists error", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // HELPER: Đảm bảo DB mở
    private void dbOpen() {
        if (db == null || !db.isOpen()) {
            open();
        }
    }

    // HELPER: Chuyển Cursor → User
    private User cursorToUser(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_USERNAME));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PASSWORD));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PHONE));
        String fullname = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_FULLNAME));
        String birth = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_BIRTH));
        String avatar = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_AVATAR));
        return new User(id, username, password, email, phone, fullname, birth, avatar);
    }

    // ========== USERDAO INTEGRATION ==========
    // Thêm method kiểm tra số điện thoại đã tồn tại trong DB không
    public boolean isPhoneExists(String phone) {
        dbOpen();
        String sql = "SELECT 1 FROM " + DbHelper.TABLE_USER + " WHERE " + DbHelper.COLUMN_PHONE + " = ? LIMIT 1";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{phone});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "isPhoneExists error", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Thêm method cập nhật mật khẩu
    public boolean updatePasswordByPhone(String phone, String hashedPassword) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_PASSWORD, hashedPassword);

        int rows = db.update(DbHelper.TABLE_USER, values,
                DbHelper.COLUMN_PHONE + " = ?", new String[]{phone});
        return rows > 0;
    }
}