/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.JavaParserTokenManager;

import java.io.InputStream;
import java.io.Reader;

public class TargetJDK1_4 implements TargetJDKVersion {

    public JavaParser createParser(InputStream in) {
        return new JavaParser(new JavaCharStream(in));
    }

    public JavaParser createParser(Reader in) {
        return new JavaParser(new JavaCharStream(in));
    }

    public JavaParserTokenManager createJavaParserTokenManager(Reader in) {
        return new JavaParserTokenManager(new JavaCharStream(in));
    }
}
