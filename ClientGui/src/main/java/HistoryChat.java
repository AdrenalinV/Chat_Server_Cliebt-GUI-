import javafx.collections.ObservableList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class HistoryChat {


    public static List<String> readHistory(String name, int lineLimit) {
        List<String> data = new ArrayList<>(lineLimit);
        File file = new File("History_" + name + ".txt");
        if (file.exists()) {
            try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
                long poz = rf.length() - 1;                 // последний символ в файле
                long tpoz = poz;                            // конец текущей строки
                int tLine = lineLimit;                      // кол-во строк которое нужно прочитать
                byte[] rowData = new byte[1000];            // буфер виной кодировка
                int c;                                      // символ
                while (poz >= 0 && tLine > 0) {             // пока не начало или лимит строк
                    rf.seek(poz);
                    c = rf.read();
                    if (c == '\n') {                        // конец строки
                        rf.read(rowData, 0, (int) (tpoz - poz));
                        data.add(0, new String(rowData, 0, (int) (tpoz - poz), StandardCharsets.UTF_8));
                        tLine--;
                        tpoz = poz - 1;
                    }
                    if (poz == 0) {                         // пришли в начало
                        rf.seek(poz);
                        rf.read(rowData, 0, (int) (tpoz - poz));
                        data.add(0, new String(rowData, 0, (int) (tpoz - poz), StandardCharsets.UTF_8));
                        tLine--;
                    }
                    poz--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static void writeHistory(ObservableList<String> data, String name) {
        File file = new File("History_" + name + ".txt");
        try (FileWriter wf = new FileWriter(file)) {
            for (String msg : data) {
                wf.write(msg + '\n');
            }
        }  catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

}
