/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;

public class VSRenderer implements Renderer {

    public String render(Iterator<Match> matches) {
    	
        StringBuilder buffer = new StringBuilder(300);
        
        for (Match match; matches.hasNext();) {
            match = matches.next();
            Mark mark;
            for (Iterator<Mark> iterator = match.iterator(); iterator.hasNext();) {
                mark = iterator.next();
                buffer.append(mark.getFilename());
                buffer.append('(').append(mark.getBeginLine()).append("):");
                buffer.append(" Between lines " + mark.getBeginLine() + " and " + (mark.getBeginLine()+match.getLineCount()) + PMD.EOL);
            }
        }
        return buffer.toString();
    }
}
