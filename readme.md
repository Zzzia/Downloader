# java文件下载工具

* 多线程
* 进度显示
* 重定向


使用方法：

~~~ java
class Main {

    private static final String savePath = "/Users/jiangzilai/IdeaProjects/Downloader";

    public static void main(String[] args) {
        List<Downloadable> list = new ArrayList<>();
        list.add(new Image("icon.png", "http://zzzia.net/image/icon.png"));
        list.add(new Image("head.png", "http://zzzia.net/image/head.png"));
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

    private static class Image implements Downloadable {
        private final String name;
        private final String url;

        Image(String name, String url) {
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
~~~