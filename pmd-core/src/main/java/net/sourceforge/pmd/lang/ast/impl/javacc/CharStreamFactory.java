/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

import org.apache.commons.io.IOUtils;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.util.document.TextDocument;

public final class CharStreamFactory {

    private CharStreamFactory() {
        // util class
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(Reader input) {
        return simpleCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(Reader input, Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker) {
        String source = toString(input);
        JavaccTokenDocument document = documentMaker.apply(TextDocument.readOnlyString(source, null));
        return new SimpleCharStream(document);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(Reader input) {
        return javaCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(Reader input, Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker) {
        String source = toString(input);
        JavaccTokenDocument tokens = documentMaker.apply(TextDocument.readOnlyString(source, null));
        return new JavaCharStream(tokens);
    }

    /**
     * @deprecated This shouldn't be used. IOExceptions should be handled properly,
     *     ie it should be expected that creating a parse may throw an IOException,
     *     in both CPD and PMD
     */
    @Deprecated
    public static String toString(Reader dstream) {
        try (Reader r = dstream) {
            return IOUtils.toString(r);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
