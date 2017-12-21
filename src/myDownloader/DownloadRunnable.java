package myDownloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class DownloadRunnable implements Runnable, FileValue {

    private String url, path, fileName;
    private int fileLength, currentFileLength = 0;
    private DownloadListener downloadListener;
    private int position;

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
            Log.d("创建文件路径失败..");
            return;
        }

        HttpURLConnection conn = null;
        InputStream inputStream = null;

        try {
            URL httpUrl = new URL(url);

            conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.connect();

            if (!CheckHelper.checkCode(conn.getResponseCode(), this)) return;

            fileLength = conn.getContentLength();
            Log.d(fileName + " Size: " + DownLoaderHelper.convertSize(fileLength));

            inputStream = conn.getInputStream();
            loadSteam(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
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
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        FileOutputStream fileOut = new FileOutputStream(path + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOut);
        byte[] buf = new byte[4096];
        int lastRatio = 0;
        int length = bufferedInputStream.read(buf);
        while (length != -1) {
            bufferedOutputStream.write(buf, 0, length);
            length = bufferedInputStream.read(buf);
            currentFileLength = currentFileLength + length;
            float ratio = (float) currentFileLength / (float) fileLength * 100;
            if (downloadListener != null && lastRatio != (int)(ratio * 100)) {//变化超过0.01时回调
                downloadListener.getRatio(ratio);
                lastRatio = (int) (ratio * 100);
            }
        }
        if (downloadListener != null) downloadListener.getRatio(100f);
        bufferedOutputStream.close();
        bufferedInputStream.close();
    }

    @Override
    public int getFileLength() {
        return fileLength;
    }

    @Override
    public int getCurrentLength() {
        return currentFileLength;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int getPosition() {
        return position;
    }
}
