package com.documentcenterapp.util;

import com.documentcenterapp.model.NewFolderItemData;

import java.util.Comparator;

public class SortingArraylistByObjest implements Comparator {
    public int compare(Object o1, Object o2) {
        NewFolderItemData dd1 = (NewFolderItemData) o1;// where FBFriends_Obj is your object class
        NewFolderItemData dd2 = (NewFolderItemData) o2;
        if (dd1.getFolderName() != null || dd1.getFileName() != null) {
            if (dd1.getFolderName() != null) {
                return dd1.getFolderName().compareToIgnoreCase(dd2.getFolderName());//where uname is field name
            } else {
                return dd1.getFileName().compareToIgnoreCase(dd2.getFileName());//where uname is field name
            }
        }
        return dd1.getFolderName().compareToIgnoreCase(dd2.getFolderName());//where uname is field name
    }
}