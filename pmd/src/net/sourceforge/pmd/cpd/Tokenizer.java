package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.io.Reader;

public interface Tokenizer {
    void tokenize(SourceCode tokens, Tokens tokenEntries, Reader input) throws IOException;
}
