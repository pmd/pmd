package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

public class TargetJDK1_4 implements TargetJDKVersion {

    public JavaParser createParser(InputStream in) {
        return new JavaParser(in);
    }

    public JavaParser createParser(Reader in) {
        return new JavaParser(in);
    }
}
