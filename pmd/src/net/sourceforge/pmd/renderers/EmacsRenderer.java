/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

public class EmacsRenderer extends OnTheFlyRenderer {

    protected static final String EOL = System.getProperty("line.separator", "\n");

    public void start() throws IOException {}

    public void renderFileViolations(Iterator<IRuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        StringBuffer buf = new StringBuffer();
        while (violations.hasNext()) {
            IRuleViolation rv = violations.next();
            buf.setLength(0);
            buf.append(EOL).append(rv.getFilename());
            buf.append(':').append(Integer.toString(rv.getBeginLine()));
            buf.append(": ").append(rv.getDescription());
            writer.write(buf.toString());
        }
    }

    public void end() throws IOException {}
}
