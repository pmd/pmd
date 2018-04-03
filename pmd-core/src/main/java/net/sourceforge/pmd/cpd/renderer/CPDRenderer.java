/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.cpd.Match;

public interface CPDRenderer {
    void render(Iterator<Match> matches, Writer writer) throws IOException;
}
