/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.invariants;

import java.io.BufferedReader;
import java.io.IOException;

import net.sourceforge.pmd.lang.Parser;

public interface InvariantOperations {
    BufferedReader getScratchReader() throws IOException;

    Parser getParser();
}
