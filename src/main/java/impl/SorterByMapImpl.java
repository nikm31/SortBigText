package impl;

import interfaces.FileUtils;
import interfaces.Sorter;

import java.io.*;
import java.nio.file.Files;
import java.text.Collator;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SorterByMapImpl implements Sorter {
    private final Logger logger = Logger.getLogger(SorterByMapImpl.class.getName());
    private final FileUtils fileUtils;
    private final Comparator<String> sorter;
    private final List<File> tempFileParts;
    private final static String OUTPUT_FILE = "out.txt";

    public SorterByMapImpl(List<File> tempFileParts) {
        this.sorter = Comparator.naturalOrder();
        this.fileUtils = new FileUtilsImpl();
        this.tempFileParts = tempFileParts;
    }

    @Override
    public void sort() {
        sortTempFiles();
        if (tempFileParts.size() > 1) {
            mergeAndSort();
        }
    }

    // сортировка частей большого файла
    private void sortTempFiles() {
        logger.log(Level.INFO, "Start sorting Temp files");
        for (File file : tempFileParts) {
            try {
                List<String> strings = Files.readAllLines(file.toPath());
                strings.sort(Collator.getInstance());
                Files.write(file.toPath(), strings);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
        logger.log(Level.INFO, "Sorting Temp files Done!");
    }

    // объединение частей в итоговый файл с сотировкой
    private void mergeAndSort() {
        Map<StringContainer, BufferedReader> stringsPoll = new HashMap<>();
        List<BufferedReader> readers = new ArrayList<>();
        BufferedWriter writer = null;
        StringContainerComparator comparator = new StringContainerComparator();

        logger.log(Level.INFO, "Start sorting and merging Temp files");

        try {
            FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (int i = 0; i < tempFileParts.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(tempFileParts.get(i)));
                readers.add(reader);
                String line = reader.readLine();
                if (line != null) {
                    stringsPoll.put(new StringContainer(line), readers.get(i));
                }
            }

            List<StringContainer> sorted = new LinkedList<>(stringsPoll.keySet());

            while (stringsPoll.size() > 0) {
                sorted.sort(comparator);
                StringContainer line = sorted.remove(0);
                writer.write(line.line);
                writer.write("\n");
                BufferedReader reader = stringsPoll.remove(line);
                String nextLine = reader.readLine();

                if (nextLine != null) {
                    StringContainer sc = new StringContainer(nextLine);
                    stringsPoll.put(sc, reader);
                    sorted.add(sc);
                }

            }
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        } finally {
            try {
                for (BufferedReader br: readers) {
                    br.close();
                }
                if (writer != null) {
                    writer.close();
                }
                fileUtils.deleteFiles(tempFileParts); // delete this line to view generated temp files
            } catch (IOException e) {
                logger.log(Level.WARNING, "Finally error: ", e.getMessage());
            }
        }
    }

    private class StringContainerComparator implements Comparator<StringContainer> {

        @Override
        public int compare(StringContainer o1, StringContainer o2) {
            return sorter.compare(o1.line, o2.line);
        }

    }

    private static class StringContainer implements Comparable<StringContainer> {

        private final String line;

        public StringContainer(String line) {
            this.line = line;
        }

        @Override
        public int compareTo(StringContainer sc) {
            return line.compareTo(sc.line);
        }

    }

}