package com.zia.downloader.core;

import com.zia.downloader.listener.DownloadListener;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadRunnable implements Runnable {

    private final String url;
    private final String path;
    private final String fileName;
    private int fileLength, currentFileLength = 0;
    private final DownloadListener downloadListener;

    public DownloadRunnable(String url, String path, String fileName) {
        this(url, path, fileName, null);
    }

    public DownloadRunnable(String url, String path, String fileName, DownloadListener downloadListener) {
        this.url = url;
        this.path = DownLoaderHelper.optimizeRoot(path);
        this.fileName = fileName;
        this.downloadListener = downloadListener;
    }

    @Override
    public void run() {
        if (!DownLoaderHelper.mkDir(path)) {
            if (downloadListener != null) {
                downloadListener.onFail(new FileNotFoundException("创建文件夹失败"));
            }
            return;
        }

        HttpURLConnection conn = null;
        InputStream inputStream = null;

        try {
            String realUrl = ReLocateUtil.getLocate(url);
            if (!realUrl.equals(url)) {
                if (downloadListener != null) downloadListener.onRelocate(realUrl);
            }

            URL httpUrl = new URL(realUrl);

            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.connect();

            int code = conn.getResponseCode();
            if (downloadListener != null) downloadListener.onResponseCode(code);
            if (code < 200 || code > 206) {
                if (downloadListener != null) downloadListener.onMessage(realUrl + " 状态码不正确，code == " + code);
            }

            fileLength = conn.getContentLength();
            if (downloadListener != null) downloadListener.onContentLength(fileLength);

            inputStream = conn.getInputStream();
            loadSteam(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            if (downloadListener != null) {
                downloadListener.onFail(e);
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    //写入本地
    private void loadSteam(InputStream inputStream) throws IOException {
        if (downloadListener != null) downloadListener.onProgress(0);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        String file = path + fileName;
        FileOutputStream fileOut = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOut);
        byte[] buf = new byte[4096];
        int lastRatio = 0;
        int length = bufferedInputStream.read(buf);
        while (length != -1) {
            bufferedOutputStream.write(buf, 0, length);
            length = bufferedInputStream.read(buf);
            currentFileLength = currentFileLength + length;
            float ratio = (float) currentFileLength / (float) fileLength * 100;
            if (downloadListener != null && lastRatio != (int) (ratio * 100)) {//变化超过0.01时回调
                downloadListener.onProgress(ratio);
                lastRatio = (int) (ratio * 100);
            }
        }
        if (downloadListener != null) downloadListener.onProgress(100f);
        bufferedOutputStream.close();
        bufferedInputStream.close();
        if (downloadListener != null) downloadListener.onSuccess(new File(file));
    }
}
