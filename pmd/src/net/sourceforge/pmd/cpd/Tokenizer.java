/*
 * User: tom
 * Date: Jul 30, 2002
 * Time: 9:59:48 AM
 */
package net.sourceforge.pmd.cpd;

import java.io.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public void tokenize(TokenList tokens, Reader input) throws IOException {
        int c = 0;
        int position = 0;
        while ((c = input.read()) != -1) {
            char myChar = (char)c;
            if (myChar != '\r' && myChar != '\n' && myChar != ' ') {
                tokens.add(new Token(myChar, position, tokens.getID()));
                position++;
            }
        }
    }
}
