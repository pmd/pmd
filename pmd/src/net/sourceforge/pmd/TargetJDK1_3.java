package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

public class TargetJDK1_3 implements TargetJDKVersion {

    public JavaParser createParser(InputStream in) {
        JavaParser jp = new JavaParser(in);
        jp.setAssertAsIdentifier();
        return jp;
    }

    public JavaParser createParser(Reader in) {
        JavaParser jp = new JavaParser(in);
        jp.setAssertAsIdentifier();
        return jp;
    }
}
