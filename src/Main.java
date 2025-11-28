import java.io.*;
import java.util.*;

public class Main {
    public static final String TITLE = "Binary Stretcher";
    public static final String VERSION = "1.0.0";
    public static final int META_SIZE = 44;

    public int mode = 0; // 1: encode, 2: decode
    public int fileMag = 4;
    public int bufferedSize = 1024;
    public String extention = "bsx";
    public boolean dry = false;
    public boolean fix = true;
    public int logLevel = 4;
    public List<String> targetPathList = new ArrayList<String>();

    public static void main(String[] args) {
        new Main(args);
    }

    public Main(String[] args) {
        Option.set(this, args);

        if (mode == 0) {
            System.err.println("実行モードを指定する必要があります。");

            System.exit(1);
        } else if (targetPathList.size() == 0) {
            System.err.println("1つ以上のファイルを指定する必要があります。");

            System.exit(1);
        }

        for (String targetPath : targetPathList) {
            try {
                switch (mode) {
                    case 1:
                        new FileEncoder(this, targetPath).encode();

                        break;
                    case 2:
                        new FileDecoder(this, targetPath).decode();

                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
