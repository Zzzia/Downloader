package com.zia.downloader.core;

import com.zia.downloader.listener.DownloadListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Downloader {

    private ExecutorService executorService;
    private String savePath;
    private final List<Downloadable> downloadAbles;
    private DownloadListener listener;
    private long maxAwaitTime = 6000;

    private Downloader(List<Downloadable> downloadAbles) {
        this.downloadAbles = downloadAbles;
    }

    public static Downloader with(List<Downloadable> downloadables) {
        return new Downloader(downloadables);
    }

    public static Downloader with(Downloadable downloadable) {
        List<Downloadable> downloadables = new ArrayList<>();
        downloadables.add(downloadable);
        return with(downloadables);
    }

    public Downloader setListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }

    public Downloader setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    public Downloader download() {
        if (downloadAbles == null || downloadAbles.isEmpty()) {
            return this;
        }
        if (downloadAbles.size() == 1) {
            Downloadable downloadable = downloadAbles.get(0);
            getExecutorService().submit(new DownloadRunnable(downloadable.getUrl(), savePath, downloadable.getFileName(), listener));
        } else {
            for (Downloadable downloadAble : downloadAbles) {
                DownloadRunnable runnable = new DownloadRunnable(downloadAble.getUrl()
                        , savePath
                        , downloadAble.getFileName()
                        , listener);
                getExecutorService().submit(runnable);
            }
            try {
                getExecutorService().shutdown();
                getExecutorService().awaitTermination(maxAwaitTime, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (listener != null) listener.onFail(e);
            }
        }

        return this;
    }

    private ExecutorService getExecutorService() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

    public void setThreadPool(ExecutorService service) {
        executorService = service;
    }

    public void setMaxAwaitTime(long maxAwaitTime) {
        this.maxAwaitTime = maxAwaitTime;
    }

    public void shutDown() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }
}
