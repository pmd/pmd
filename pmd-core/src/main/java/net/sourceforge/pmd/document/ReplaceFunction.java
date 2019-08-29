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
        public void replace(RegionByOffset region, String text) {

        }

        @Override
        public void commit() {

        }
    };


    /**
     * Replace the content of a region with some text.
     */
    void replace(RegionByOffset region, String text);


    void commit() throws IOException;


    static ReplaceFunction bufferedFile(String originalBuffer, Path path, Charset charSet) {

        return new ReplaceFunction() {

            private StringBuilder builder = new StringBuilder(originalBuffer);

            @Override
            public void replace(RegionByOffset region, String text) {
                builder.replace(region.getOffset(), region.getOffsetAfterEnding(), text);
            }

            @Override
            public void commit() throws IOException {
                Files.write(path, builder.toString().getBytes(charSet));
            }
        };
    }

}
