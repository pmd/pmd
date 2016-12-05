/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.List;

/**
 * Simple tokenizer for PHP.
 */
public class PHPTokenizer implements Tokenizer {

    @Override
    public void tokenize(SourceCode tokens, Tokens tokenEntries) {
        List<String> code = tokens.getCode();
        for (int i = 0; i < code.size(); i++) {
            String currentLine = code.get(i);
            for (int j = 0; j < currentLine.length(); j++) {
                char tok = currentLine.charAt(j);
                if (!Character.isWhitespace(tok) && tok != '{' && tok != '}' && tok != ';') {
                    tokenEntries.add(new TokenEntry(String.valueOf(tok), tokens.getFileName(), i + 1));
                }
            }
        }
        tokenEntries.add(TokenEntry.getEOF());
    }
}
