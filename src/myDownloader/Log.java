package myDownloader;

public class Log {
    private static boolean openLog = true;

    public static void d(String msg){
        if (!openLog) return;
        System.out.println(msg);
    }
}
