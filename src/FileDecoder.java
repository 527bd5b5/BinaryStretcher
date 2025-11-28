import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.Arrays;

public class FileDecoder extends FileController {
    public FileDecoder(Main main, String path) {
        super(main);

        inputPath = Paths.get(path).toAbsolutePath();

        String outputDirectory = inputPath.getParent().toString();
        String outputFileName = removeExtention(inputPath.getFileName().toString(), main.extention);

        outputPath = Paths.get(outputDirectory, outputFileName);
    }

    public void decode() throws IOException {
        RandomAccessFile targetFile = null;
        BufferedOutputStream outputStream = null;

        try {
            println(2, inputPath.toString() + ":");
            print(2, "ファイルをデコード ... ");

            targetFile = new RandomAccessFile(inputPath.toString(), "rw");

            if (!main.dry)
                outputStream =
                        new BufferedOutputStream(new FileOutputStream(outputPath.toString()));

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            readMeta(targetFile);

            byte[] inputData = new byte[main.bufferedSize * fileMag];
            byte[] outputData = new byte[main.bufferedSize];
            int[] inputDataChunk = new int[8];

            long outputFileLength = 0;
            long corruptedBitNum = 0;

            int inputDataLength;

            while ((inputDataLength = targetFile.read(inputData)) != -1) {
                boolean needReturnSeek = false;

                for (int i = 0; i < inputDataLength; i++) {
                    int m = i % fileMag;

                    for (int j = 0; j < 8; j++)
                        inputDataChunk[j] += (inputData[i] << j & 0x80) >> 7;

                    if (m != fileMag - 1) continue;

                    byte outputDataChunk = (byte) 0x00;
                    boolean needFix = false;

                    for (int j = 0; j < 8; j++) {
                        int bit = inputDataChunk[j] > fileMag / 2 ? 1 : 0;

                        outputDataChunk = (byte) (outputDataChunk << 1 | bit);

                        if (inputDataChunk[j] % fileMag != 0) {
                            corruptedBitNum++;

                            needFix = true;
                        }
                    }

                    outputData[i / fileMag] = outputDataChunk;

                    if (needFix && main.fix) {
                        targetFile.seek(
                                main.META_SIZE + outputFileLength * fileMag + i - fileMag + 1);
                        targetFile.write(createByteArray(fileMag, outputDataChunk));

                        needReturnSeek = true;
                    }

                    inputDataChunk = new int[8];
                }

                int outputDataLength = inputDataLength / fileMag;

                if (outputDataLength == main.bufferedSize) {
                    messageDigest.update(outputData);
                } else {
                    messageDigest.update(Arrays.copyOf(outputData, outputDataLength));
                }

                outputFileLength += outputDataLength;

                if (!main.dry) {
                    outputStream.write(outputData, 0, outputDataLength);
                    outputStream.flush();
                }

                if (needReturnSeek) targetFile.seek(main.META_SIZE + outputFileLength * fileMag);
            }

            byte[] outputFileHash = messageDigest.digest();

            int level;

            if (fileLength != outputFileLength) {
                println(2, "ファイルの長さが異なります");

                level = 3;
            } else if (!Arrays.equals(fileHash, outputFileHash)) {
                println(2, "ファイルのハッシュ値が異なります");

                level = 3;
            } else if (corruptedBitNum != 0) {
                println(2, "成功 (" + (main.fix ? "修復済み" : "破損有り") + ")");

                level = 3;
            } else {
                println(2, "成功");

                level = 4;
            }

            printStatus(level, "破損ビット数: " + corruptedBitNum);
            printMag(level);
            printMeta(level, "入力された", fileLength, fileHash);
            printMeta(level, "出力された", outputFileLength, outputFileHash);
        } catch (FileNotFoundException _) {
            println(2, "存在しないファイル");
        } catch (Exception e) {
            println(2, "失敗");

            if (main.logLevel >= 1) e.printStackTrace();
        } finally {
            if (targetFile != null) targetFile.close();
            if (outputStream != null) outputStream.close();
        }
    }

    private void readMeta(RandomAccessFile targetFile) throws Exception {
        fileMag = targetFile.readInt();
        fileLength = targetFile.readLong();
        fileHash = new byte[32];

        targetFile.read(fileHash);
    }
}
