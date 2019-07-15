package gt.com.gtnote.Models;

/**
 * Provides utility for editing Strings in a text which is currently being edited by a user
 */
public class TextEditOperations {
    
    /**
     * Inserts a String before and after the selection.
     * @param text the original text
     * @param selectionStart the first index that is included in the selection
     * @param selectionEnd the first index after selectionStart that is not included in the selection anymore
     * @param insertBefore this String will be inserted before the selection
     * @param insertAfter this String will be inserted after the selection
     * @return
     */
    public String surroundSelection(String text, int selectionStart, int selectionEnd, String insertBefore, String insertAfter) {
        String before = text.substring(0, selectionStart);
        String middle = text.substring(selectionStart, selectionEnd);
        String after = text.substring(selectionEnd);
        return before + insertBefore + middle + insertAfter + after;
    }
    
    /**
     * Inserts content in text at a specific position
     * @param text the original text
     * @param cursorPosition the index of the character which the cursor is in front of
     * @param content the string to insert at that position
     * @return
     */
    public String insert(String text, int cursorPosition, String content) {
        String before = text.substring(0, cursorPosition);
        String after = text.substring(cursorPosition);
        return before + content + after;
    }
}
