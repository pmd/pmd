package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.*;
import java.util.*;

public class TextRenderer implements Renderer {

    protected String EOL = System.getProperty("line.separator", "\n");

		public String render(Report report) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(EOL + rv.getFilename());
            buf.append("\t" + Integer.toString(rv.getLine()));
            buf.append("\t" + rv.getDescription());
        }
        return buf.toString();
		}
}
