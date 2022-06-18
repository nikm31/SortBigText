package interfaces;

import java.io.File;
import java.util.List;

public interface FileUtils {

    File writeFileToDisk(String text, String filename);

    void deleteFiles(List<File> files);

}
