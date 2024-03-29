package ZipCompress;

import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class ZipFile {

    private static void zipFile(String filePath) {
        try {
            File file = new File(filePath);
            String zipFileName = "Files/Compressed/"+file.getName().concat(".zip");

            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            System.out.println(zipFileName);

            zos.putNextEntry(new ZipEntry(file.getName()));

            byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();
            zos.close();
            System.out.println("Compress File to Zip ended");
        } catch (FileNotFoundException ex) {
            System.err.format("The file %s does not exist", filePath);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
    }

    public  void main() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
        File f = fc.showOpenDialog(null);

        if(f != null) {
            String input = f.getAbsolutePath();
            zipFile(input);
        }
    }
}