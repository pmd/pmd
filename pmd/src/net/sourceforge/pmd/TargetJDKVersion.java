/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

public interface TargetJDKVersion {
    public JavaParser createParser(InputStream in);
    public JavaParser createParser(Reader in);
}
