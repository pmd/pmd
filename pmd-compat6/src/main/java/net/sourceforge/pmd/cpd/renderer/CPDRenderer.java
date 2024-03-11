/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.cpd.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.cpd.Match;

/**
 * @deprecated Use {@link net.sourceforge.pmd.cpd.CPDReportRenderer}
 */
@Deprecated
public interface CPDRenderer {
    void render(Iterator<Match> matches, Writer writer) throws IOException;
}
