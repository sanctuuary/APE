package util;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static util.Evaluation.fail;
import static util.Evaluation.success;
import static util.TestResources.getAbsoluteRoot;

public class GitHubRepo {

    private String absoluteLocalRoot;
    private String repository;
    private String commit;
    private HashMap<Integer, GitFile> files;

    public GitHubRepo(String repository, String commitOrBranch) {
        this.absoluteLocalRoot = Paths.get(getAbsoluteRoot(), "temp").toString();
        this.repository = repository;
        this.commit = commitOrBranch;
        this.files = new HashMap<>();
    }

    public String getRoot() {
        return this.absoluteLocalRoot;
    }

    public void cleanUp() {
        files.clear();
        try {
            File directory = new File(this.absoluteLocalRoot);
            FileUtils.deleteDirectory(directory);
            assertFalse(directory.exists());
        } catch (IOException e) {
            System.out.println(String.format("Could not delete folder '%s':\n%s", this.absoluteLocalRoot, e.getMessage()));
        }
    }

    public boolean canConnect() {
        try {
            final URL url = new URL("https://github.com/");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getFile(String filePath, String commit) {
        int key = GitFile.hash(commit, filePath);
        if (!files.containsKey(key)) {
            files.put(key, new GitFile(absoluteLocalRoot, repository, commit, filePath));
        }
        return files.get(key).getFilePath();
    }

    public String getFile(String filePath) {
        return getFile(filePath, this.commit);
    }

    public JSONObject getJSONObject(String filePath, String commit) {
        try {
            return new JSONObject(FileUtils.readFileToString(new File(getFile(filePath, commit)), "utf-8"));
        } catch (Exception e) {
            fail(String.format("Could not read %s(%s..) to a JSONObject.", filePath, commit.substring(0, 7)));
            return null;
        }
    }

    public JSONObject getJSONObject(String filePath) {
        return getJSONObject(filePath, this.commit);
    }

    private static int file_count = 0;

    public String createJSONFile(JSONObject jsonObject, String name) {
        File f = new File(Paths.get(this.getRoot(), this.repository, new SimpleDateFormat("yyyyMMdd_HHmmss_").format(new Date()) + name + "_" + (file_count++) + ".json").toAbsolutePath().toString());
        try (FileWriter fw = new FileWriter(f)) {
            f.createNewFile();
            fw.write(jsonObject.toString(2));
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Cannot create file '%s'", name);
        }
        return f.getAbsolutePath();
    }

    private static class GitFile {

        private final String root;
        private final String repository;
        private final String commit;
        private final String relative_file_path;

        public GitFile(String root, String repository, String commit, String relative_file_path) {
            this.root = root;
            this.repository = resolvePath(repository);
            this.commit = commit;
            this.relative_file_path = resolvePath(relative_file_path);

            if (!exists()) {
                createFile();
                fetchFile();
            }
        }

        private static String resolvePath(String path) {
            return path.replace("./", "").replace("/", "\\");
        }

        private static void createDirectory(Path directory) {
            if (!Files.exists(directory) && !new File(directory.toString()).mkdirs()) {
                fail("Could not create directory " + directory.toString());
            }
        }

        public static int hash(String commit, String file) {
            return Objects.hash(commit, resolvePath(file));
        }

        public String getFilePath() {
            return Paths.get(root, repository, commit, relative_file_path).toString();
        }

        private boolean exists() {
            return Files.exists(Paths.get(getFilePath()));
        }

        private void createFile() {

            Path file = Paths.get(getFilePath());

            createDirectory(file.getParent());

            if (!Files.exists(file) && !Files.isDirectory(file)) {
                try {
                    Files.createFile(file);
                } catch (IOException e) {
                    fail("Could not create file " + file.toString());
                }
            }
        }

        private void fetchFile() {
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
            } catch (Exception e) {
                fail("Could not download file " + toString());
            }
        }

        @Override
        public String toString() {
            return String.format("https://github.com/%s/blob/%s/%s", this.repository, this.commit, this.relative_file_path);
        }
    }
}
