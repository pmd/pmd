/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import net.sourceforge.pmd.document.TextRegion.RegionByOffset;

public interface ReplaceFunction {


    ReplaceFunction NOOP = new ReplaceFunction() {
        @Override
        public CharSequence getCurrentText(MutableDocument doc) {
            return doc.getText();
        }

        @Override
        public void replace(RegionByOffset region, String text) {

        }

        @Override
        public ReplaceFunction commit() {
            return NOOP;
        }
    };


    /**
     * Replace the content of a region with some text.
     */
    void replace(RegionByOffset region, String text);


    CharSequence getCurrentText(MutableDocument doc);

    /**
     * Commit the document (eg writing it to disk), and returns a new
     * document corresponding to the new document.
     *
     * @return An updated replace function
     */
    ReplaceFunction commit() throws IOException;


    /**
     * Write updates into an in-memory buffer, commit writes to disk.
     * This doesn't use any IO resources outside of the commit method.
     */
    static ReplaceFunction bufferedFile(String originalBuffer, Path path, Charset charSet) {

        return new ReplaceFunction() {

            private StringBuilder builder = new StringBuilder(originalBuffer);

            @Override
            public CharSequence getCurrentText(MutableDocument doc) {
                return builder;
            }

            @Override
            public void replace(RegionByOffset region, String text) {
                builder.replace(region.getOffset(), region.getOffsetAfterEnding(), text);
            }

            @Override
            public ReplaceFunction commit() throws IOException {
                String done = builder.toString();
                byte[] bytes = done.getBytes(charSet);
                Files.write(path, bytes);
                return bufferedFile(done, path, charSet);
            }
        };
    }

}
