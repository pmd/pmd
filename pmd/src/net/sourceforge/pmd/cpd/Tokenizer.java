package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;

public interface Tokenizer {
    void tokenize(TokenList tokens, Reader input) throws IOException;
}
