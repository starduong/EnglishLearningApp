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
        values.put(DbHelper.COLUMN_ACCOUNT_TYPE, user.getAccountType());
        values.put(DbHelper.COLUMN_PRO_EXPIRY_DATE, user.getProExpiryDate());

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
        values.put(DbHelper.COLUMN_ACCOUNT_TYPE, user.getAccountType());
        values.put(DbHelper.COLUMN_PRO_EXPIRY_DATE, user.getProExpiryDate());

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_USER, values,
                    DbHelper.COLUMN_ID + "=?", new String[]{user.getId()});
        } catch (Exception e) {
            Log.e(TAG, "Update failed", e);
        }
        return result;
    }

    // UPDATE ACCOUNT TYPE
    public int updateAccountType(String userId, String accountType, long expiryDate) {
        dbOpen();
        ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ACCOUNT_TYPE, accountType);
        values.put(DbHelper.COLUMN_PRO_EXPIRY_DATE, expiryDate);

        int result = 0;
        try {
            result = db.update(DbHelper.TABLE_USER, values,
                    DbHelper.COLUMN_ID + "=?", new String[]{userId});
        } catch (Exception e) {
            Log.e(TAG, "Update account type failed", e);
        }
        return result;
    }

    // CHECK IF USER IS PRO
    public boolean isUserPro(String userId) {
        dbOpen();
        Cursor cursor = null;
        try {
            String sql = "SELECT " + DbHelper.COLUMN_ACCOUNT_TYPE + ", " +
                    DbHelper.COLUMN_PRO_EXPIRY_DATE +
                    " FROM " + DbHelper.TABLE_USER +
                    " WHERE " + DbHelper.COLUMN_ID + " = ?";

            cursor = db.rawQuery(sql, new String[]{userId});

            if (cursor.moveToFirst()) {
                String accountType = cursor.getString(0);
                long expiryDate = cursor.getLong(1);

                // Kiểm tra loại tài khoản
                if (User.ACCOUNT_TYPE_PRO.equals(accountType) ||
                        User.ACCOUNT_TYPE_PREMIUM.equals(accountType)) {
                    // Kiểm tra thời hạn nếu có
                    if (expiryDate > 0) {
                        return System.currentTimeMillis() <= expiryDate;
                    }
                    return true; // Pro vĩnh viễn
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "isUserPro error", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return false;
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

    // GET BY ID
    public User getUserById(String userId) {
        dbOpen();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    DbHelper.TABLE_USER,
                    null,
                    DbHelper.COLUMN_ID + " = ?",
                    new String[]{userId},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                return cursorToUser(cursor);
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserById failed", e);
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

    // HELPER: Chuyển Cursor → User (CẬP NHẬT)
    private User cursorToUser(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ID));
        String username = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_USERNAME));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PASSWORD));
        String email = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PHONE));
        String fullname = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_FULLNAME));
        String birth = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_BIRTH));
        String avatar = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_AVATAR));
        String accountType = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_ACCOUNT_TYPE));
        long proExpiryDate = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLUMN_PRO_EXPIRY_DATE));

        return new User(id, username, password, email, phone, fullname, birth, avatar, accountType, proExpiryDate);
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

    // Thêm method để lấy thông tin account type
    public String getAccountType(String userId) {
        dbOpen();
        Cursor cursor = null;
        try {
            String sql = "SELECT " + DbHelper.COLUMN_ACCOUNT_TYPE +
                    " FROM " + DbHelper.TABLE_USER +
                    " WHERE " + DbHelper.COLUMN_ID + " = ?";

            cursor = db.rawQuery(sql, new String[]{userId});

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "getAccountType error", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return User.ACCOUNT_TYPE_FREE;
    }
}