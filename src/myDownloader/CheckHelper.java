package myDownloader;

class CheckHelper {
    static boolean checkCode(int code, FileValue value) {
        if (code < 200 || code > 206) {
            Log.d(value.getFileName() + "下载失败，code == " + code);
            return false;
        } else return true;
    }

}
