package ZipCompress;

import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.nio.file.attribute.*;

public class ZipDir extends SimpleFileVisitor<Path> {

    private static ZipOutputStream zos;

    private Path sourceDir;

    public ZipDir(Path sourceDir) {
        this.sourceDir = sourceDir;
    }
    public ZipDir() {

    }

    @Override
    public FileVisitResult visitFile(Path file,
                                     BasicFileAttributes attributes) {

        try {
            Path targetFile = sourceDir.relativize(file);

            zos.putNextEntry(new ZipEntry(targetFile.toString()));

            byte[] bytes = Files.readAllBytes(file);
            zos.write(bytes, 0, bytes.length);
            zos.closeEntry();

        } catch (IOException ex) {
            System.err.println(ex);
        }

        return FileVisitResult.CONTINUE;
    }

    public static void main() {

        DirectoryChooser chooser = new DirectoryChooser();
        File defaultDirectory = chooser.showDialog(null);
        chooser.setInitialDirectory(defaultDirectory);

        if (defaultDirectory != null) {
        try {
            String dirPath = defaultDirectory.getAbsolutePath();
            Path sourceDir = Paths.get(dirPath);
            String name = "Files/Compressed/"+defaultDirectory.getName();
            String zipFileName =  name.concat(".zip");
            zos = new ZipOutputStream(new FileOutputStream(zipFileName));

            Files.walkFileTree(sourceDir, new ZipDir(sourceDir));

            zos.close();
            System.out.println("Compress Directory to Zip ended");
        }
        catch (IOException ex) {
            System.err.println("I/O Error: " + ex);
        }}
    }
}