/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.StringUtil;

import java.util.Iterator;

/**
 * @author Philippe T'Seyen
 */
public class XMLRenderer implements Renderer {
	
    public String render(Iterator matches) {
        StringBuffer buffer = new StringBuffer(300);
        buffer.append("<?xml version=\"1.0\"  encoding=\"UTF-8\"?>");
        buffer.append("<pmd-cpd>").append(PMD.EOL);
        Match match;
        while (matches.hasNext()) {
            match = (Match) matches.next();
            buffer.append("<duplication lines=\"");
            buffer.append(match.getLineCount());
            buffer.append("\" tokens=\"");
            buffer.append(match.getTokenCount());
            buffer.append("\">").append(PMD.EOL);

            TokenEntry mark;
            for (Iterator iterator = match.iterator(); iterator.hasNext();) {
                mark = (TokenEntry) iterator.next();
                buffer.append("<file line=\"");
                buffer.append(mark.getBeginLine());
                buffer.append("\" path=\"");
                buffer.append(mark.getTokenSrcID());
                buffer.append("\"/>").append(PMD.EOL);
            }
            String codeFragment = match.getSourceCodeSlice();
            if (codeFragment != null) {
                buffer.append("<codefragment>").append(PMD.EOL);
                buffer.append("<![CDATA[").append(PMD.EOL);
                buffer.append(StringUtil.replaceString(codeFragment, "]]>", "]]&gt;")).append(PMD.EOL + "]]>" + PMD.EOL + "</codefragment>" + PMD.EOL);
            }
            buffer.append("</duplication>").append(PMD.EOL);
        }
        buffer.append("</pmd-cpd>");
        return buffer.toString();
    }
}
