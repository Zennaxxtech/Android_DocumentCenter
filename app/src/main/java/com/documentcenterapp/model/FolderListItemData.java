package com.documentcenterapp.model;

public class FolderListItemData {

    /**
     * FolderNo : 201805031725457372172467689808021080
     * FolderName : Company ABCD
     * ParentNo : 0
     * CreateBy : admin
     * CreateDate : /Date(1525339545750+0800)/
     * FileList :
     */

    private String FolderNo;
    private String FolderName;
    private String ParentNo;
    private String CreateBy;
    private String CreateDate;
    private String FileList;

    public String getFolderNo() {
        return FolderNo;
    }

    public void setFolderNo(String FolderNo) {
        this.FolderNo = FolderNo;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String FolderName) {
        this.FolderName = FolderName;
    }

    public String getParentNo() {
        return ParentNo;
    }

    public void setParentNo(String ParentNo) {
        this.ParentNo = ParentNo;
    }

    public String getCreateBy() {
        return CreateBy;
    }

    public void setCreateBy(String CreateBy) {
        this.CreateBy = CreateBy;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String CreateDate) {
        this.CreateDate = CreateDate;
    }

    public String getFileList() {
        return FileList;
    }

    public void setFileList(String FileList) {
        this.FileList = FileList;
    }
}
