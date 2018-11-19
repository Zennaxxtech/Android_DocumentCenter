package com.documentcenterapp.util;

import com.downloader.Progress;
import com.liulishuo.filedownloader.BaseDownloadTask;

public interface OnProgressListener extends com.downloader.OnProgressListener {

    void onProgress(BaseDownloadTask task, Progress progress);

    @Override
    void onProgress(Progress progress);
}
