package net.sourceforge.pmd;

import net.sourceforge.pmd.ast.JavaParser;

import java.io.InputStream;
import java.io.Reader;

public class JLS1_1 implements JLSVersion {

    public JavaParser createParser(InputStream in) {
        throw new RuntimeException("Not yet implemented");
    }

    public JavaParser createParser(Reader in) {
        throw new RuntimeException("Not yet implemented");
    }
}
