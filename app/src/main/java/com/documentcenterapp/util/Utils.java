/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.documentcenterapp.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.documentcenterapp.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by amitshekhar on 13/11/17.
 */

public final class Utils {

    private Utils() {
        // no instance
    }

    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = new File(Environment.getExternalStorageDirectory(), "DocumentCenter");
            if (!file.exists()) {
                file.mkdir();
            }
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }

    public String getPublicAlbumStorageDir(Context context) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "");
        if (!file.mkdirs()) {
        }
        return file.getAbsolutePath();
    }

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }

   /* private static String getBytesToMBString(long bytes){

        double b = bytes;
        double k = bytes/1024.0;
        double m = ((bytes/1024.0)/1024.0);
        double g = (((bytes/1024.0)/1024.0)/1024.0);
        double t = ((((bytes/1024.0)/1024.0)/1024.0)/1024.0);

        if ( t>1 ) {
            return String.format(Locale.ENGLISH, "%.2fTB", (((bytes/1024.0)/1024.0)/1024.0)/1024.0);
        } else if ( g>1 ) {
            return String.format(Locale.ENGLISH, "%.2fGB", ((bytes/1024.0)/1024.0)/1024.0);
        } else if ( m>1 ) {
            return String.format(Locale.ENGLISH, "%.2fMB", (bytes/1024.0)/1024.0);
        } else if ( k>1 ) {
            return String.format(Locale.ENGLISH, "%.2fKB", bytes/1024.0);
        } else {
            return String.format(Locale.ENGLISH, "%.2fBytes", bytes);
        }
    }*/

    private static String size(int size) {
        String hrSize = "";

        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");
        if (t > 1) {
            hrSize = dec.format(t).concat(" %.2fTB");
        } else if (g > 1) {
            hrSize = dec.format(g).concat(" %.2fGB");
        } else if (m > 1) {
            hrSize = dec.format(m).concat(" %.2fMB");
        } else if (k > 1) {
            hrSize = dec.format(k).concat(" %.2fKB");
        } else {
            hrSize = dec.format(b).concat(" %.2fBytes");
        }
        return hrSize;
    }

    public static String getDownloadSpeedString(@NonNull final Context context, final long downloadedBytesPerSecond) {
        if (downloadedBytesPerSecond < 0) {
            return "";
        }
        double kb = (double) downloadedBytesPerSecond / (double) 1000;
        double mb = kb / (double) 1000;
        final DecimalFormat decimalFormat = new DecimalFormat(".##");
        if (mb >= 1) {
            return context.getString(R.string.download_speed_mb, decimalFormat.format(mb));
        } else if (kb >= 1) {
            return context.getString(R.string.download_speed_kb, decimalFormat.format(kb));
        } else {
            return context.getString(R.string.download_speed_bytes, downloadedBytesPerSecond);
        }
    }

    public static String formatSpeed(float speed) {
        if (speed < 1024) {
            return String.format("%.2f KB/s", speed);
        } else if (speed < 1024 * 1024) {
            return String.format("%.2f kB/s", speed / 1024);
        } else if (speed < 1024 * 1024 * 1024) {
            return String.format("%.2f MB/s", speed / 1024 / 1024);
        } else {
            return String.format("%.2f GB/s", speed / 1024 / 1024 / 1024);
        }
    }
}
