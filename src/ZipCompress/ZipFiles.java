package ZipCompress;

import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.*;

public class ZipFiles {

    private static void zipFiles(List<File> f) {
        try {

                File firstFile = new File(f.get(0).getAbsolutePath());
                String zipFileName = "Files/Compressed/"+firstFile.getName().concat(".zip");

                FileOutputStream fos = new FileOutputStream(zipFileName);
                ZipOutputStream zos = new ZipOutputStream(fos);

                for (File file : f) {
                    zos.putNextEntry(new ZipEntry(new File(file.toString()).getName()));
                    System.out.println("You Choose This Files :\n"+file.toString());
                    byte[] bytes = Files.readAllBytes(Paths.get(file.toString()));
                    zos.write(bytes, 0, bytes.length);
                    zos.closeEntry();
                }

                zos.close();
            System.out.println("Compress MultiFiles to Zip ended");



        } catch (FileNotFoundException ex) {
            System.err.println("A file does not exist: " + ex);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex);
        }
    }

    public static void main() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files","*.*"));
        List<File> f=  fc.showOpenMultipleDialog(null);
        zipFiles(f);
    }
}