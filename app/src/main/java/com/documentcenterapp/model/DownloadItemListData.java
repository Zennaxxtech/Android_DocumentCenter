package com.documentcenterapp.model;

public class DownloadItemListData {

    public static final String TABLE_NAME = "downloaditemlist";
    public static final String TABLE_NAME_COMPLETE = "downloadcompleteitemlist";

    public static final String COLUMN_FILE_ID = "fileId";
    public static final String COLUMN_FILE_NAME = "fileName";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_FILE_SIZE = "fileSize";
    public static final String COLUMN_DOWNLOAD_LINK = "downladlink";
    public static final String COLUMN_FILE_ICON = "icon";

    int fileId;
    String fileName;
    int fileSize;
    String downladLink;
    String icon;
    String date;

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FILE_NAME + " TEXT,"
                    + COLUMN_FILE_SIZE + " TEXT,"
                    + COLUMN_DOWNLOAD_LINK + " TEXT,"
                    + COLUMN_FILE_ICON + " TEXT,"
                    + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public static final String CREATE_TABLE_COMPLETE =
            "CREATE TABLE " + TABLE_NAME_COMPLETE + "("
                    + COLUMN_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_FILE_NAME + " TEXT,"
                    + COLUMN_FILE_SIZE + " TEXT,"
                    + COLUMN_DOWNLOAD_LINK + " TEXT,"
                    + COLUMN_FILE_ICON + " TEXT,"
                    + COLUMN_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public DownloadItemListData() {
    }


    public DownloadItemListData(int fileId, String fileName, int fileSize, String downladLink, String icon, String date) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downladLink = downladLink;
        this.icon = icon;
        this.date = date;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownladLink() {
        return downladLink;
    }

    public void setDownladLink(String downladLink) {
        this.downladLink = downladLink;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
