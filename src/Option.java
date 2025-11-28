import java.io.*;

public class Option {
    public static void set(Main main, String[] args) {
        int i = 0;

        try {
            while (i < args.length) {
                switch (args[i]) {
                    case "-e":
                        main.mode = 1;

                        break;
                    case "-d":
                        main.mode = 2;

                        break;
                    case "-m":
                        main.fileMag = Integer.parseInt(args[++i]);

                        break;
                    case "-b":
                        main.bufferedSize = Integer.parseInt(args[++i]);

                        break;
                    case "--extention":
                        main.extention = args[++i];

                        break;
                    case "--dry-run":
                        main.dry = true;

                        break;
                    case "--no-fix":
                        main.fix = false;

                        break;
                    case "--log-level":
                        main.logLevel = Integer.parseInt(args[++i]);

                        break;
                    case "-v":
                    case "--version":
                        System.err.println(main.TITLE + " " + main.VERSION);

                        System.exit(0);

                        return;
                    case "-h":
                    case "--help":
                        printTextFile("help.txt");

                        System.exit(0);

                        return;
                    default:
                        main.targetPathList.add(args[i]);

                        break;
                }

                i++;
            }
        } catch (NumberFormatException _) {
            System.err.println(String.format("%d番目の引数には数値を指定する必要があります。", i));

            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException _) {
            System.err.println("オプションの後ろに値を指定する必要があります。");

            System.exit(1);
        }
    }

    public static void printTextFile(String path) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(path)));

            String line;

            while ((line = bufferedReader.readLine()) != null) System.out.println(line);

            bufferedReader.close();
        } catch (FileNotFoundException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
