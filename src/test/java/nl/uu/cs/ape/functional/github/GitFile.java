package nl.uu.cs.ape.functional.github;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static nl.uu.cs.ape.TestUtil.success;
import static org.junit.jupiter.api.Assertions.fail;

public class GitFile {

    private final String root;
    private final String repository;
    private final String commit;
    private final String relative_file_path;

    public GitFile(String root, String repository, String commit, String relative_file_path){
        this.root = root;
        this.repository = resolvePath(repository);
        this.commit = commit;
        this.relative_file_path = resolvePath(relative_file_path);

        if(!exists()){
            createFile();
            fetchFile();
        }
    }

    private static String resolvePath(String path){
        return path.replace("./", "").replace("/", "\\");
    }

    public String getFilePath() {
        return Paths.get(root, repository, commit, relative_file_path).toString();
    }

    private boolean exists(){
        return Files.exists(Paths.get(getFilePath()));
    }

    private void createFile() {

        Path file = Paths.get(getFilePath());

        createDirectory(file.getParent());

        if(!Files.exists(file) && !Files.isDirectory(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                fail("Could not create file " + file.toString());
            }
        }
    }

    private static void createDirectory(Path directory) {
        if(!Files.exists(directory) && !new File(directory.toString()).mkdirs()) {
            fail("Could not create directory " + directory.toString());
        }
    }

    private void fetchFile(){
        URL url;
        URLConnection con;
        DataInputStream dis;
        FileOutputStream fos;
        File targetFile;
        byte[] fileData;
        try {
            url = new URL(String.format("https://raw.githubusercontent.com/%s/%s/%s", this.repository, this.commit, this.relative_file_path).replace("\\", "/"));
            con = url.openConnection(); // open the url connection.
            dis = new DataInputStream(con.getInputStream());
            fileData = new byte[con.getContentLength()];
            for (int q = 0; q < fileData.length; q++) {
                fileData[q] = dis.readByte();
            }
            dis.close(); // close the data input stream
            targetFile = new File(getFilePath());
            fos = new FileOutputStream(targetFile);
            fos.write(fileData);  // write out the file we want to save.
            fos.close(); // close the output stream writer

            success("Downloading file '%s' from '%s'", url.getPath(), this.repository);
        }
        catch(Exception e) {
            fail("Could not download file " + toString());
        }
    }

    @Override
    public String toString() {
        return String.format("https://github.com/%s/blob/%s/%s", this.repository, this.commit, this.relative_file_path);
    }

    public static int hash(String commit, String file){
        return Objects.hash(commit, resolvePath(file));
    }
}
