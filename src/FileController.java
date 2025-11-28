import java.nio.file.*;

public class FileController {
    protected Path inputPath;
    protected Path outputPath;
    protected int fileMag;
    protected long fileLength;
    protected byte[] fileHash;

    protected final Main main;

    protected FileController(Main main) {
        this.main = main;

        fileMag = main.fileMag;
    }

    protected void printMeta(int level) {
        printMeta(level, "", fileLength, fileHash);
    }

    protected void printMeta(int level, String modifier, long fileLength, byte[] fileHash) {
        printStatus(level, modifier + "ファイルの長さ (bytes): " + fileLength);
        printStatus(
                level, modifier + "ファイルのハッシュ値 (SHA-256): " + convertByteArrayToHexString(fileHash));
    }

    protected void printMag(int level) {
        printStatus(level, "倍率: " + fileMag);
    }

    protected void printStatus(int level, String text) {
        println(level, "    " + text);
    }

    protected void print(int level, String text) {
        if (main.logLevel >= level) System.out.print(text);
    }

    protected void println(int level, String text) {
        if (main.logLevel >= level) System.out.println(text);
    }

    protected static String removeExtention(String fileName, String extention) {
        return fileName.substring(0, fileName.lastIndexOf("." + extention));
    }

    protected static byte[] createByteArray(int length, byte initial) {
        byte[] byteArray = new byte[length];

        for (int i = 0; i < length; i++) byteArray[i] = initial;

        return byteArray;
    }

    protected static String convertByteArrayToHexString(byte[] data) {
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < data.length; i++)
            stringBuffer.append(String.format("%02x", data[i] & 0xff));

        return stringBuffer.toString();
    }
}
