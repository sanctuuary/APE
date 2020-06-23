package nl.uu.cs.ape.functional.github;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.util.HashMap;

import static nl.uu.cs.ape.TestUtil.*;
import static org.junit.jupiter.api.Assertions.*;

public class GitHubRepo {

    private String absoluteLocalRoot;
    private String repository;
    private String commit;
    private HashMap<Integer, GitFile> files;

    public GitHubRepo(String repository){
        this.absoluteLocalRoot = Paths.get(getAbsoluteResourceRoot(), "temp").toString();
        this.repository = repository;
        this.files = new HashMap<>();
    }

    public void setCommit(String commit){
        this.commit = commit;
    }

    public String getRoot(){
        return this.absoluteLocalRoot;
    }

    public void cleanUp(){
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

    public String getFile(String filePath){
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

    public JSONObject getJSONObject(String filePath){
        return getJSONObject(filePath, this.commit);
    }
}
