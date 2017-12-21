package myDownloader;

import java.io.File;

class DownLoaderHelper {
    static boolean mkDir(String root){
        File file = new File(root);
        if (!file.exists() && !file.mkdirs()) {
            Log.d("路径创建失败..");
            return false;
        }
        return true;
    }

    static String convertSize(int fileSize){
        int kb = fileSize / 1024;
        int mb = kb / 1024;
        int gb = mb / 1024;
        if (gb != 0){
            return gb + " Gb";
        }else if (mb != 0){
            return mb + " Mb";
        }else{
            return kb + " Kb";
        }
    }

    /**
     * 检查路径并返回带有/结尾的路径
     * @param source 路径
     * @return
     */
    static String optimizeRoot(String source){
        source = source.trim();
        int length = source.length();
        if (source.charAt(length - 1) != '/'){
            source = source + "/";
        }
        return source;
    }
}
