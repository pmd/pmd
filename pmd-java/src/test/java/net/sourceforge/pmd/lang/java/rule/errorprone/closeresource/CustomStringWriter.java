/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone.closeresource;

import java.io.IOException;
import java.io.StringWriter;

public class CustomStringWriter extends StringWriter {

    @Override
    public void close() throws IOException {
        getBuffer().setLength(0);
        getBuffer().trimToSize();
        super.close();
    }
}
