/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 12:23:13 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.Reader;
import java.io.IOException;

public interface Tokenizer {
    public void tokenize(TokenList tokens, Reader input) throws IOException;
}
