package impl;

import interfaces.FileUtils;
import interfaces.Sorter;

import java.io.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SorterImpl implements Sorter {
    private final Logger logger = Logger.getLogger(SorterImpl.class.getName());
    private final FileUtils fileUtils;
    private final Comparator<String> sorter;
    private final List<File> tempFileParts;
    private final static String OUTPUT_FILE = "out.txt";

    public SorterImpl(List<File> tempFileParts) {
        this.sorter = Comparator.naturalOrder();
        this.fileUtils = new FileUtilsImpl();
        this.tempFileParts = tempFileParts;
    }

    @Override
    public void sort() {
        sortTempFiles();
        mergeChunks();
    }

    // сортировка частей большого файла
    private void sortTempFiles() {
        logger.log(Level.INFO, "Start sorting Temp files");
        try {
            for (File output : tempFileParts) {

                FileReader fileReader = new FileReader(output.toString());
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                List<String> lines = new ArrayList<>();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    lines.add(line);
                }

                bufferedReader.close();
                lines.sort(Collator.getInstance());

                FileWriter writer = new FileWriter(output.toString());
                for (String str : lines) {
                    writer.write(str + "\r\n");
                }

                writer.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    // объединение частей в итоговый файл с сотировкой
    private void mergeChunks() {
        Map<StringContainer, BufferedReader> map = new HashMap<>();
        List<BufferedReader> readers = new ArrayList<>();
        BufferedWriter writer = null;
        StringContainerComparator delegate = new StringContainerComparator();

        logger.log(Level.INFO, "Start sorting and merging Temp files");

        try {
            FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 0; i < tempFileParts.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(tempFileParts.get(i)));
                readers.add(reader);
                String line = reader.readLine();
                if (line != null) {
                    map.put(new StringContainer(line), readers.get(i));
                }
            }

            List<StringContainer> sorted = new LinkedList<>(map.keySet());

            while (map.size() > 0) {
                sorted.sort(delegate);
                StringContainer line = sorted.remove(0);
                writer.write(line.string);
                writer.write("\n");
                BufferedReader reader = map.remove(line);
                String nextLine = reader.readLine();

                if (nextLine != null) {
                    StringContainer sw = new StringContainer(nextLine);
                    map.put(sw, reader);
                    sorted.add(sw);
                }

            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        } finally {
            try {
                for (BufferedReader br: readers) {
                    br.close();
                }
                fileUtils.deleteFiles(tempFileParts); // delete this line to view generated temp files
                writer.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Finally error: ", e.getMessage());
            }
        }
    }

    private class StringContainerComparator implements Comparator<StringContainer> {

        @Override
        public int compare(StringContainer o1, StringContainer o2) {
            return sorter.compare(o1.string, o2.string);
        }

    }

    private static class StringContainer implements Comparable<StringContainer> {

        private final String string;

        public StringContainer(String line) {
            this.string = line;
        }

        @Override
        public int compareTo(StringContainer o) {
            return string.compareTo(o.string);
        }

    }

}