package ro.balamaci.jlogstore.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * @author sbalamaci
 */
public class FileUtil {

    public static void deleteDir(String directory) throws IOException {
        Path pathToBeDeleted = Paths.get(directory);

        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

}
