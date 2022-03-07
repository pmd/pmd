/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.document.CpdCompat;
import net.sourceforge.pmd.lang.document.TextDocument;

public final class CharStreamFactory {

    private CharStreamFactory() {
        // util class
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(Reader input) throws IOException {
        return simpleCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(Reader input,
                                              Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker)
        throws IOException {
        String source = IOUtils.toString(input);
        JavaccTokenDocument document = documentMaker.apply(TextDocument.readOnlyString(source, CpdCompat.dummyVersion()));
        return new SimpleCharStream(document);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(Reader input) throws IOException {
        return javaCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(Reader input, Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker)
        throws IOException {
        String source = IOUtils.toString(input);
        JavaccTokenDocument tokens = documentMaker.apply(TextDocument.readOnlyString(source, CpdCompat.dummyVersion()));
        return new JavaCharStream(tokens);
    }

}
