import myDownloader.Downloadable;
import myDownloader.Downloader;

import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) {
        List<Downloadable> list = new ArrayList<>();
        list.add(new Student("2016211551","http://zzzia.net/cet/2016211551.jpg"));
        list.add(new Student("2016211552","http://zzzia.net/cet/2016211552.jpg"));
        //链式批量下载
        Downloader.with(list)
                .setSavePath("/Users/jiangzilai/IdeaProjects/Downloader")
                .setListener(System.out::println)
                .download();
    }

    static class Student implements Downloadable {
        private String name;
        private String url;

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
            return name + ".jpg";
        }
    }
}