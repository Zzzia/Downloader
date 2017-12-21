package myDownloader;

public interface FileValue {
    int getFileLength();
    int getCurrentLength();
    String getUrl();
    String getFileName();
    String getPath();
    int getPosition();
}
