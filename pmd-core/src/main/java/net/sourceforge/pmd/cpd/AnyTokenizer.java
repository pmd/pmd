/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * This class does a best-guess try-anything tokenization.
 *
 * @author jheintz
 */
public class AnyTokenizer implements Tokenizer {
    public static final String TOKENS = " \t!#$%^&*(){}-=+<>/\\`~;:";

    @Override
    public void tokenize(SourceCode sourceCode, Tokens tokenEntries) {
        StringBuilder sb = sourceCode.getCodeBuffer();
        try (BufferedReader reader = new BufferedReader(new CharArrayReader(sb.toString().toCharArray()))) {
            int lineNumber = 1;
            String line = reader.readLine();
            while (line != null) {
                StringTokenizer tokenizer = new StringTokenizer(line, TOKENS, true);
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    if (!" ".equals(token) && !"\t".equals(token)) {
                        tokenEntries.add(new TokenEntry(token, sourceCode.getFileName(), lineNumber));
                    }
                }
                // advance iteration variables
                line = reader.readLine();
                lineNumber++;
            }
        } catch (IOException ignored) {
            ignored.printStackTrace();
        } finally {
            tokenEntries.add(TokenEntry.getEOF());
        }
    }
}
