/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/** Handles text updates for a {@link MutableTextDocument}. */
public interface ReplaceHandler {

    /** Does nothing. */
    ReplaceHandler NOOP = new ReplaceHandler() {
        @Override
        public CharSequence getCurrentText(MutableTextDocument doc) {
            return doc.getText();
        }

        @Override
        public void replace(TextRegion region, String text) {
            // noop
        }

        @Override
        public ReplaceHandler commit() {
            return NOOP;
        }
    };


    /**
     * Replace the content of a region with some text.
     * <ul>
     * <li>To insert some text, use an empty region
     * <li>To delete some text, use an empty text string
     * </ul>
     *
     * @param region Region of text to replace
     * @param text   Text that will replace the given region
     */
    void replace(TextRegion region, String text);


    /** Gets the latest text. */
    CharSequence getCurrentText(MutableTextDocument doc);


    /**
     * Commit the document (eg writing it to disk), and returns a new
     * document corresponding to the new document.
     *
     * @return An updated replace function
     */
    ReplaceHandler commit() throws IOException;


    /**
     * Write updates into an in-memory buffer, commit writes to disk.
     * This doesn't use any IO resources outside of the commit method.
     */
    static ReplaceHandler bufferedFile(CharSequence originalBuffer, Path path, Charset charSet) {

        return new ReplaceHandler() {

            private StringBuilder builder = new StringBuilder(originalBuffer);

            @Override
            public CharSequence getCurrentText(MutableTextDocument doc) {
                return builder;
            }

            @Override
            public void replace(TextRegion region, String text) {
                builder.replace(region.getStartOffset(), region.getEndOffset(), text);
            }

            @Override
            public ReplaceHandler commit() throws IOException {
                String done = builder.toString();
                byte[] bytes = done.getBytes(charSet);
                Files.write(path, bytes);
                return bufferedFile(done, path, charSet);
            }
        };
    }

}
