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

    private String encoding;

    public XMLRenderer() {
        this(System.getProperty("file.encoding"));
    }
    
    public XMLRenderer(String e) {
        this.encoding = e;
    }
	
    public String render(Iterator<Match> matches) {
        StringBuffer buffer = new StringBuffer(300);
        buffer.append("<?xml version=\"1.0\" encoding=\"");
        buffer.append(encoding);
        buffer.append("\"?>").append(PMD.EOL);
        buffer.append("<pmd-cpd>").append(PMD.EOL);
        Match match;
        while (matches.hasNext()) {
            match = matches.next();
            buffer.append("<duplication lines=\"");
            buffer.append(match.getLineCount());
            buffer.append("\" tokens=\"");
            buffer.append(match.getTokenCount());
            buffer.append("\">").append(PMD.EOL);

            TokenEntry mark;
            for (Iterator<TokenEntry> iterator = match.iterator(); iterator.hasNext();) {
                mark = iterator.next();
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
