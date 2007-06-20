/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

/**
 * This is an implementation of {@link net.sourceforge.pmd.TargetJDKVersion} for
 * JDK 1.6.
 *
 */
public class TargetJDK1_6 implements TargetJDKVersion {

    /**
     * @see net.sourceforge.pmd.TargetJDKVersion#createParser(InputStream)
     */
    public JavaParser createParser(InputStream in) {
        JavaParser jp = new JavaParser(new JavaCharStream(in));
        jp.setJDK15();
        return jp;
    }

    /**
     * @see net.sourceforge.pmd.TargetJDKVersion#createParser(Reader)
     */
    public JavaParser createParser(Reader in) {
        JavaParser jp = new JavaParser(new JavaCharStream(in));
        jp.setJDK15();
        return jp;
    }

    public String getVersionString() {
        return "1.6";
    }

}
