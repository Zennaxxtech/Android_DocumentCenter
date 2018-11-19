package com.documentcenterapp.requestResponse;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


@SuppressLint("NewApi")
public class AndyUtils {
    static float density = 1;
    private static ProgressDialog mProgressDialog;
    private static Dialog mDialog;
    private static ImageView imageView;
    private static Animation anim;


    public static void showToast(String msg, Context ctx) {

        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts decimal degrees to radians : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    /* :: This function converts radians to decimal degrees : */
    /* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec("DocCenter!app@2189INNO7016ways#0".getBytes(), "AES");
            byte[] finalIvs = new byte[16];
            int len = "0123456789ABCDEF".getBytes().length > 16 ? 16 : "0123456789ABCDEF".getBytes().length;
            System.arraycopy("0123456789ABCDEF".getBytes(), 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivps);
            return Base64.encodeToString(cipher.doFinal(data.getBytes()), Base64.DEFAULT).trim().replaceAll("\n", "").replaceAll("\r", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String decrypt(@NonNull String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec("DocCenter!app@2189INNO7016ways#0".getBytes(), "AES");
            byte[] finalIvs = new byte[16];
            int len = "0123456789ABCDEF".getBytes().length > 16 ? 16 : "0123456789ABCDEF".getBytes().length;
            System.arraycopy("0123456789ABCDEF".getBytes(), 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String dataDecrypt(@NonNull String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKeySpec = new SecretKeySpec("DocCenter!app@2189INNO7016ways#0".getBytes(), "AES");
            byte[] finalIvs = new byte[16];
            int len = "0123456789ABCDEF".getBytes().length > 16 ? 16 : "0123456789ABCDEF".getBytes().length;
            System.arraycopy("0123456789ABCDEF".getBytes(), 0, finalIvs, 0, len);
            IvParameterSpec ivps = new IvParameterSpec(finalIvs);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivps);
            return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
