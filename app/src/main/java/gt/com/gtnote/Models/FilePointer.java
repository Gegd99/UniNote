package gt.com.gtnote.Models;

import gt.com.gtnote.Interfaces.FileIO;

class FilePointer {
    
    private String path;
    private FileIO fileIO;
    
    FilePointer(String path, FileIO fileIO) {
        this.path = path;
        this.fileIO = fileIO;
    }
    
    String read() {
        return fileIO.read(path);
    }
    
    void write(String source) {
        fileIO.write(path, source);
    }
}
