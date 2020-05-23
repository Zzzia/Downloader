package com.zia.downloader;

import com.zia.downloader.core.Downloadable;
import com.zia.downloader.core.Downloader;
import com.zia.downloader.listener.DownloadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class Main {

    private static final String savePath = "/Users/jiangzilai/IdeaProjects/Downloader";

    public static void main(String[] args) {
        List<Downloadable> list = new ArrayList<>();
        list.add(new Student("icon.png", "http://zzzia.net/image/icon.png"));
        list.add(new Student("head.png", "http://zzzia.net/image/head.png"));
        //链式批量下载
        Downloader.with(list)
                .setSavePath(savePath)
                .setListener(new DownloadListener() {
                    @Override
                    public void onProgress(float ratio) {
                        System.out.println("ratio=" + ratio);
                    }

                    @Override
                    public void onSuccess(File file) {
                        System.out.println("file=" + file.getAbsolutePath());
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponseCode(int code) {
                        System.out.println("code=" + code);
                    }

                    @Override
                    public void onContentLength(long contentLength) {
                        System.out.println("contentLength=" + contentLength);
                    }

                    @Override
                    public void onMessage(String msg) {
                        System.out.println("msg=" + msg);
                    }

                    @Override
                    public void onRelocate(String url) {
                        System.out.println("url=" + url);
                    }
                })
                .download();
    }

    private static class Student implements Downloadable {
        private final String name;
        private final String url;

        Student(String name, String url) {
            this.name = name;
            this.url = url;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getFileName() {
            return name;
        }
    }
}