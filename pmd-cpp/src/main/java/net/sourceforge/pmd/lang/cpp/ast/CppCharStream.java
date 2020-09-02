/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.ast;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.SimpleCharStream;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.CpdCompat;

/**
 * A SimpleCharStream, that supports the continuation of lines via backslash+newline,
 * which is used in C/C++.
 *
 * @author Andreas Dangel
 */
public class CppCharStream extends SimpleCharStream {

    private static final Pattern CONTINUATION = Pattern.compile("\\\\\\n|\\\\\\r\\n");
    private static final char BACKSLASH = '\\';
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';

    CppCharStream(JavaccTokenDocument document) {
        super(document);
    }


    @Override
    public char readChar() throws IOException {
        char c = super.readChar();
        if (c == BACKSLASH) {
            char c1 = super.readChar();
            if (c1 == NEWLINE) {
                c = super.readChar();
            } else if (c1 == CARRIAGE_RETURN) {
                char c2 = super.readChar();
                if (c2 == NEWLINE) {
                    c = super.readChar();
                } else {
                    backup(2);
                }
            } else {
                backup(1);
            }
        }
        return c;
    }

    @Override
    public char[] GetSuffix(int len) {
        String image = GetImage();
        return image.substring(image.length() - len, image.length()).toCharArray();
    }

    @Override
    public String GetImage() {
        String image = super.GetImage();
        return CONTINUATION.matcher(image).replaceAll("");
    }

    public static CppCharStream newCppCharStream(Reader dstream) {
        String source = CharStreamFactory.toString(dstream);
        JavaccTokenDocument document = new JavaccTokenDocument(TextDocument.readOnlyString(source, CpdCompat.dummyVersion())) {
            @Override
            protected @Nullable String describeKindImpl(int kind) {
                return CppTokenKinds.describe(kind);
            }
        };
        return new CppCharStream(document);
    }
}
