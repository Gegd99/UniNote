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
    
    /**
     * returns true if the cursor is one of these:
     * <ul>
     *     <li>set at the beginning of a word</li>
     *     <li>set at the end of a word</li>
     *     <li>inside the word</li>
     * </ul>
     *
     * @param text
     * @param cursorPosition should be >= 0 and < text.length()
     * @return
     */
    public boolean isCursorTouchingWord(String text, int cursorPosition) {
        int indexLeft = cursorPosition - 1;
        int indexRight = cursorPosition;
        
        char left = ' ';
        char right = ' ';
        
        if (indexLeft >= 0) {
            left = text.charAt(indexLeft);
        }
        if (indexRight < text.length()) {
            right = text.charAt(indexRight);
        }
        
        return !isWhitespace(left) || !isWhitespace(right);
    }
    
    public boolean isWhitespace(char character) {
        return character == ' ' || character == '\n';
    }
    
    /**
     * Finds first character of the touched word.
     * @param text
     * @param cursorPosition if the cursor was placed here, it would touch the word
     * @return index of the first character of the touched word
     */
    public int findWordBeginning(String text, int cursorPosition) {
        while (cursorPosition > 0) {
            cursorPosition--;
            if (isWhitespace(text.charAt(cursorPosition))) {
                return cursorPosition + 1;
            }
        }
        return 0;
    }
    
    /**
     * Finds last character of the touched word.
     * @param text
     * @param cursorPosition if the cursor was placed here, it would touch the word
     * @return index of the last character of the touched word
     */
    public int findWordEnd(String text, int cursorPosition) {
        while (cursorPosition < text.length()) {
            if (isWhitespace(text.charAt(cursorPosition))) {
                return cursorPosition - 1;
            }
            cursorPosition++;
        }
        return text.length() - 1;
    }
}
