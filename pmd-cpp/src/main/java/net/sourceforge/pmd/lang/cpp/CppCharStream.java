/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp;

import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.SimpleCharStream;

/**
 * A SimpleCharStream, that supports the continuation of lines via backslash+newline,
 * which is used in C/C++.
 *
 * @author Andreas Dangel
 */
@Deprecated
@InternalApi
public class CppCharStream extends SimpleCharStream {

    private static final Pattern CONTINUATION = Pattern.compile("\\\\\\n|\\\\\\r\\n");
    private static final char BACKSLASH = '\\';
    private static final char NEWLINE = '\n';
    private static final char CARRIAGE_RETURN = '\r';

    public CppCharStream(Reader dstream) {
        super(dstream);
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
}
