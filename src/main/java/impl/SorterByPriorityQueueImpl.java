package impl;

import interfaces.FileUtils;
import interfaces.Sorter;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SorterByPriorityQueueImpl implements Sorter {
    private final Logger logger = Logger.getLogger(SorterByPriorityQueueImpl.class.getName());

    private final List<File> tempFileParts;
    private final static String OUTPUT_FILE = "out.txt";
    private final FileUtils fileUtils;

    public SorterByPriorityQueueImpl(List<File> tempFileParts) {
        this.tempFileParts = tempFileParts;
        this.fileUtils = new FileUtilsImpl();
    }

    @Override
    public void sort() {
        sortTempFiles();

        // если у нас только одна часть временного файла - переименовываем ее
        if (tempFileParts.size() > 1) {
            mergeAndSort();
        } else {
            File file = new File(tempFileParts.get(0).toString());
            file.renameTo(new File(OUTPUT_FILE));
        }

        logger.log(Level.INFO, "Program finished successfully");
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
        logger.log(Level.INFO, "Start merging Temp files");
        BufferedWriter writer = null;
        BufferedReader reader = null;
        List<StringLineReader> readers = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILE)));
            readers = new ArrayList<>();

            for (File tempFilePart : tempFileParts) {
                reader = new BufferedReader(new FileReader(tempFilePart));
                readers.add(new StringLineReader(reader));
            }

            PriorityQueue<StringLineReader> queue = new PriorityQueue<>(readers.size(), (o1, o2) -> {
                if (o1.getValue() == null || o2.getValue() == null) {
                    return 0;
                }
                return o1.getValue().toLowerCase().compareTo(o2.getValue().toLowerCase());
            });

            queue.addAll(readers);
            while (!queue.isEmpty()) {

                StringLineReader poll = queue.poll();
                writer.write(poll.getValue());
                writer.write("\n");
                poll.shiftValue();

                if (poll.getValue() != null) {
                    queue.add(poll);
                }
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());
        } finally {
            try {

                if (readers != null) {
                    for (StringLineReader slr : readers) {
                        slr.bufferedReader.close();
                    }
                }

                if (reader != null) {
                    reader.close();
                }

                if (writer != null) {
                    writer.close();
                }
                fileUtils.deleteFiles(tempFileParts);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
        logger.log(Level.INFO, "Merging Temp files is Done!");
    }

    @Getter
    private static class StringLineReader {
        private String value;
        private final BufferedReader bufferedReader;

        public StringLineReader(BufferedReader reader) throws IOException {
            this.bufferedReader = reader;
            this.value = reader.readLine();
        }

        public void shiftValue() {
            try {
                value = bufferedReader.readLine();
            } catch (IOException e) {
                value = null;
            }
        }

    }
}