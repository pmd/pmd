/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.PMD;

import java.util.Iterator;
import org.apache.commons.lang3.StringEscapeUtils;

public class CSVRenderer implements Renderer {

	private char separator;
	private boolean lineCountPerFile;

	public static final char DEFAULT_SEPARATOR = ',';
	public static final boolean DEFAULT_LINECOUNTPERFILE = false;

	public CSVRenderer() {
		this(DEFAULT_SEPARATOR, DEFAULT_LINECOUNTPERFILE);
	}

	public CSVRenderer(boolean lineCountPerFile) {
		this(DEFAULT_SEPARATOR, lineCountPerFile);
	}

	public CSVRenderer(char separatorChar) {
		this(separatorChar, DEFAULT_LINECOUNTPERFILE);
	}
	
	public CSVRenderer(char separatorChar, boolean lineCountPerFile) {
		this.separator = separatorChar;
		this.lineCountPerFile = lineCountPerFile;
	}
	
    public String render(Iterator<Match> matches) {
        StringBuilder csv = new StringBuilder(1000);

        if (!lineCountPerFile) {
            csv.append("lines").append(separator);
        }
        csv.append("tokens").append(separator)
           .append("occurrences")
           .append(PMD.EOL);

        while (matches.hasNext()) {
            Match match = matches.next();

            if (!lineCountPerFile) {
                csv.append(match.getLineCount()).append(separator);
            }
            csv.append(match.getTokenCount()).append(separator)
               .append(match.getMarkCount()).append(separator);
            for (Iterator<Mark> marks = match.iterator(); marks.hasNext();) {
                Mark mark = marks.next();

                csv.append(mark.getBeginLine()).append(separator);
                if (lineCountPerFile) {
                    csv.append(mark.getLineCount()).append(separator);
                }
                csv.append(StringEscapeUtils.escapeCsv(mark.getFilename()));
                if (marks.hasNext()) {
                    csv.append(separator);
                }
            }
            csv.append(PMD.EOL);
        }
        return csv.toString();
    }
}
