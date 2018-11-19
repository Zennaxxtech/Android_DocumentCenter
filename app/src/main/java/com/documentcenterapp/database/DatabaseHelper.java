package com.documentcenterapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.documentcenterapp.model.DownloadItemListData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ravi on 15/03/18.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "download_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create notes table
        db.execSQL(DownloadItemListData.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + DownloadItemListData.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertNote(String name,String fileSize, String downladlink, String icon, String date) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // no need to add them
        values.put(DownloadItemListData.COLUMN_FILE_NAME, name);
        values.put(DownloadItemListData.COLUMN_FILE_SIZE, fileSize);
        values.put(DownloadItemListData.COLUMN_DOWNLOAD_LINK, downladlink);
        values.put(DownloadItemListData.COLUMN_FILE_ICON, icon);
        values.put(DownloadItemListData.COLUMN_DATE, date);

        // insert row
        long id = db.insert(DownloadItemListData.TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted row id
        return id;
    }

    public DownloadItemListData getNote(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DownloadItemListData.TABLE_NAME,
                new String[]{DownloadItemListData.COLUMN_FILE_ID, DownloadItemListData.COLUMN_FILE_NAME, DownloadItemListData.COLUMN_DATE, DownloadItemListData.COLUMN_FILE_SIZE, DownloadItemListData.COLUMN_DOWNLOAD_LINK, DownloadItemListData.COLUMN_FILE_ICON},
                DownloadItemListData.COLUMN_FILE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        DownloadItemListData note = new DownloadItemListData(
                cursor.getInt(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_ID)),
                cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_NAME)),
                cursor.getInt(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_SIZE)),
                cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_DOWNLOAD_LINK)),
                cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_ICON)),
                cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_DATE)));

        // close the db connection
        cursor.close();

        return note;
    }

    public List<DownloadItemListData> getAllNotes() {
        List<DownloadItemListData> notes = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + DownloadItemListData.TABLE_NAME + " ORDER BY " +
                DownloadItemListData.COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DownloadItemListData note = new DownloadItemListData();
                note.setFileId(cursor.getInt(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_ID)));
                note.setFileName(cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_NAME)));
                note.setFileSize(cursor.getInt(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_SIZE)));
                note.setDownladLink(cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_DOWNLOAD_LINK)));
                note.setIcon(cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_FILE_ICON)));
                note.setDate(cursor.getString(cursor.getColumnIndex(DownloadItemListData.COLUMN_DATE)));

                notes.add(note);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return notes list
        return notes;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + DownloadItemListData.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public int updateNote(DownloadItemListData note) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DownloadItemListData.COLUMN_FILE_NAME, note.getFileName());

        // updating row
        return db.update(DownloadItemListData.TABLE_NAME, values, DownloadItemListData.COLUMN_FILE_ID + " = ?",
                new String[]{String.valueOf(note.getFileId())});
    }

    public void deleteNote(DownloadItemListData note) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DownloadItemListData.TABLE_NAME, DownloadItemListData.COLUMN_FILE_ID + " = ?",
                new String[]{String.valueOf(note.getFileId())});
        db.close();
    }
}
