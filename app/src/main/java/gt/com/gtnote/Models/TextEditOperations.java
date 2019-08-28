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

    public String cutToRandomLength(String text)
    {
        int minLength = (int)(Math.random() * 70) + 50;

        return cutToReasonableLength(text, minLength, minLength + 20, 10);
    }
    
    /**
     * Cuts the string at a reasonable position.
     * @param text
     * @param minLength
     * @param maxLength
     * @param newlineWeight how many characters a newline is equal to
     * @return
     */
    public String cutToReasonableLength(String text, int minLength, int maxLength, int newlineWeight) {
        int currentLength = 0;
        int index = 0;
        
        // reach minLength
        while (currentLength < minLength && index < text.length() - 1) {
            char c = text.charAt(index);
            
            if (c == '\n') {
                currentLength += newlineWeight;
            } else {
                currentLength += 1;
            }
            index++;
        }
        
        // return if string end is already reached
        if (index == text.length()) {
            return text;
        }
        
        // save this as the first reasonable index, in case nothing better appears
        int reasonableIndex = index;
        
        // now look until maxLength is reached if something better appears
        
        if (maxLength > text.length()) {
            maxLength = text.length();
        }
        
        while (currentLength < maxLength) {
            char c = text.charAt(index);
    
            if (c == ' ' || c == '\n') {
                reasonableIndex = index;
                break;
            } else {
                currentLength++;
            }
            
            index++;
        }
        
        return text.substring(0, reasonableIndex + 1);
    }
    
    /**
     * Just for layout testing.
     * @param minWords
     * @param maxWords
     * @return
     */
    public String getRandomString(int minWords, int maxWords) {
        int numberOfWords = minWords + (int) Math.floor(1 + Math.random() * (maxWords - minWords));
        String[] words = new String[] {
                "a man", "war", "peace", "apple", "avocado", "tree",
                "looks", "walks", "screams", "whispers", "dreams",
                "evil",  "good", "very long", "incredibly stupid", "hostile", "fragile",
        };
        StringBuilder sb = new StringBuilder();
        boolean sentenceStart = true;
        for (int i = 0; i < numberOfWords; i++) {
            String word = words[(int) Math.floor(Math.random() * words.length)];
            
            if (sentenceStart) {
                sentenceStart = false;
                
                // capitalize first letter
                sb.append(word.substring(0, 1).toUpperCase());
                sb.append(word.substring(1));
            } else {
                sb.append(word);
            }
            
            if (Math.random() < 0.2) {
                sentenceStart = true;
                sb.append(".");
            }
            
            if (i + 1 < numberOfWords) {
    
                if (sentenceStart && Math.random() < 0.5) {
                    sb.append("\n");
                } else {
                    sb.append(" ");
                }
            }
        }
        if (!sentenceStart) {
            sb.append(".");
        }
        return sb.toString();
    }
}
