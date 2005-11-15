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
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\"?>");
        buffer.append("<pmd-cpd>" + PMD.EOL);
        for (; matches.hasNext();) {
            Match match = (Match) matches.next();
            buffer.append("<duplication");
            buffer.append(" lines=\"");
            buffer.append(match.getLineCount());
            buffer.append("\"");
            buffer.append(" tokens=\"");
            buffer.append(match.getTokenCount());
            buffer.append("\">" + PMD.EOL);

            for (Iterator iterator = match.iterator(); iterator.hasNext();) {
                TokenEntry mark = (TokenEntry) iterator.next();
                buffer.append("<file");
                buffer.append(" line=\"");
                buffer.append(mark.getBeginLine());
                buffer.append("\"");
                buffer.append(" path=\"");
                buffer.append(mark.getTokenSrcID());
                buffer.append("\"/>" + PMD.EOL);
            }
            String codeFragment = match.getSourceCodeSlice();
            if (codeFragment != null) {
                buffer.append("<codefragment>"  + PMD.EOL + "<![CDATA[" + PMD.EOL + StringUtil.replaceString(codeFragment, "]]>", "]]&gt;") + PMD.EOL + "]]>"  + PMD.EOL + "</codefragment>" + PMD.EOL);
            }
            buffer.append("</duplication>" + PMD.EOL);
        }
        buffer.append("</pmd-cpd>");
        return buffer.toString();
    }
}
