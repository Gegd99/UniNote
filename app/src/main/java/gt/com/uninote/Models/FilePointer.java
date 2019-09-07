package gt.com.uninote.Models;

import gt.com.uninote.Interfaces.FileIO;

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
