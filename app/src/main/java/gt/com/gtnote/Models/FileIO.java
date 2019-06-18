package gt.com.gtnote.Models;

/**
 * interface that provides read / write utility for files
 */
public interface FileIO {
    String read(String path);
    void write(String path, String source);
    boolean fileExists(String path);
}
