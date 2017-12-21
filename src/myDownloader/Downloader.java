package myDownloader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {

    private ExecutorService cachedThreadPool;
    private String savePath;
    private List<Downloadable> downloadables;
    private DownloadListener listener;

    private Downloader(List<Downloadable> downloadables){
        this.downloadables = downloadables;
    }

    public static Downloader with(List<Downloadable> downloadables){
        return new Downloader(downloadables);
    }

    public static Downloader with(Downloadable downloadable){
        List<Downloadable> downloadables = new ArrayList<>();
        downloadables.add(downloadable);
        return with(downloadables);
    }

    public Downloader setListener(DownloadListener listener) {
        this.listener = listener;
        return this;
    }

    public Downloader setSavePath(String savePath){
        this.savePath = savePath;
        return this;
    }

    public void download(){
        if (downloadables.size() == 1){
            Downloadable downloadable = downloadables.get(0);
            getCachedThreadPool().submit(new DownloadRunnable(downloadable.getUrl(),savePath,downloadable.getFileName(),listener));
        }else if(downloadables.size() <= 0){
            return;
        }else{
            final int[] size = {0};
            DownloadListener singleFileListener = ratio -> {
                if (ratio == 100f) {
                    size[0]++;
                    listener.getRatio((float) size[0] / (float)downloadables.size() * 100);
                }
            };
            for (int i = 0; i < downloadables.size(); i++) {
                DownloadRunnable runnable = new DownloadRunnable(downloadables.get(i).getUrl()
                        ,savePath
                        ,downloadables.get(i).getFileName()
                        ,singleFileListener);
                getCachedThreadPool().submit(runnable);
            }
        }

    }

    private ExecutorService getCachedThreadPool(){
        if (cachedThreadPool == null){
            cachedThreadPool = Executors.newCachedThreadPool();
        }
        return cachedThreadPool;
    }
}
