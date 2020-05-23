package com.zia.downloader.listener;

import java.io.File;

public interface FullDownloadListener{

    void onProgress(float ratio);

    void onSuccess(File file);

    void onFail(Exception e);

    void onResponseCode(int code);

    void onContentLength(long contentLength);

    void onMessage(String msg);

    void onRelocate(String url);
}
