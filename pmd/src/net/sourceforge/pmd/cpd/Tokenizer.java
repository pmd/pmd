/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 12:23:13 PM
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;

public interface Tokenizer {
    public void tokenize(TokenList tokens, Reader input) throws IOException;
}
