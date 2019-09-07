package gt.com.uninote.Interfaces;

/**
 * interface that provides read / write utility for files
 */
public interface FileIO {
    String read(String path);
    
    /**
     * Creates the file if it doesn't already exist.
     * Opens the file, writes to the file and closes the file.
     * @param path
     * @param source
     */
    void write(String path, String source);
    boolean fileExists(String path);
    void delete(String path);
    
    /**
     * Return a list of all files and folders in the root directory.
     * @return list of filenames and folder names
     */
    String[] list();
}
