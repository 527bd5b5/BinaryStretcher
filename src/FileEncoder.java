import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Arrays;

public class FileEncoder extends FileController {
    public FileEncoder(Main main, String path) {
        super(main);

        inputPath = Paths.get(path).toAbsolutePath();

        String outputDirectory = inputPath.getParent().toString();
        String outputFileName = inputPath.getFileName().toString() + "." + main.extention;

        outputPath = Paths.get(outputDirectory, outputFileName);
    }

    public void encode() throws IOException {
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;

        try {
            println(2, inputPath.toString() + ":");
            print(2, "ファイルをエンコード ... ");

            inputStream = new BufferedInputStream(new FileInputStream(inputPath.toString()));

            if (!main.dry)
                outputStream =
                        new BufferedOutputStream(new FileOutputStream(outputPath.toString()));

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            if (!main.dry) outputStream.write(new byte[main.META_SIZE]);

            byte[] inputData = new byte[main.bufferedSize];
            byte[] outputData = new byte[main.bufferedSize * fileMag];

            int inputDataLength;

            fileLength = 0;

            while ((inputDataLength = inputStream.read(inputData)) != -1) {
                for (int i = 0; i < fileMag; i++)
                    for (int j = 0; j < inputDataLength; j++)
                        outputData[i + j * fileMag] = inputData[j];

                if (inputDataLength == main.bufferedSize) {
                    messageDigest.update(inputData);
                } else {
                    messageDigest.update(Arrays.copyOf(inputData, inputDataLength));
                }

                fileLength += inputDataLength;

                if (!main.dry) {
                    outputStream.write(outputData, 0, inputDataLength * fileMag);
                    outputStream.flush();
                }
            }

            fileHash = messageDigest.digest();

            if (!main.dry) {
                outputStream.close();

                outputStream = null;

                writeMeta();
            }

            println(2, "成功");
            printMag(4);
            printMeta(4);
        } catch (FileNotFoundException _) {
            println(2, "存在しないファイル");
        } catch (Exception e) {
            println(2, "失敗");

            if (main.logLevel >= 1) e.printStackTrace();
        } finally {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }
    }

    private void writeMeta() throws Exception {
        RandomAccessFile outputFile = null;
        Exception exception = null;

        try {
            outputFile = new RandomAccessFile(outputPath.toString(), "rwd");

            outputFile.writeInt(fileMag);
            outputFile.writeLong(fileLength);
            outputFile.write(fileHash);
        } catch (Exception e) {
            exception = e;
        } finally {
            if (outputFile != null) outputFile.close();
        }

        if (exception != null) throw exception;
    }
}
