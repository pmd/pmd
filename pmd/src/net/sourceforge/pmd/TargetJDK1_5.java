package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaCharStream;
import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

public class TargetJDK1_5 implements TargetJDKVersion {

    public JavaParser createParser(InputStream in) {
        JavaParser jp = new JavaParser(new JavaCharStream(in));
        jp.setJDK15();
        return jp;
    }

    public JavaParser createParser(Reader in) {
        JavaParser jp = new JavaParser(new JavaCharStream(in));
        jp.setJDK15();
        return jp;
    }
}
