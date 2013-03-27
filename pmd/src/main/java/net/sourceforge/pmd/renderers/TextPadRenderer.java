/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;

/**
 * <p>A Renderer for running PMD via a TextPad 'tool'.  <a href="http://www.textpad.com">TextPad</a> is a text editor by Helios Software Solutions.</p>
 * <p/>
 * <p>Output lines are in the form:</p>
 * <p/>
 * <p><CODE>pathtojavafile(line#, NameOfRule):&nbsp; Specific rule violation message</CODE></p>
 * <p/>
 * <p>For example:</p>
 * <p/>
 * <p><CODE>D:\java\pmd\src\src\net\sourceforge\pmd\renderers\TextPadRenderer.java(24, AtLeastOneConstructor):&nbsp; Each class should declare at least one constructor
 * <br>D:\java\pmd\src\src\net\sourceforge\pmd\renderers\TextPadRenderer.java(26, VariableNamingConventionsRule):&nbsp; Variables should start with a lowercase character
 * <br>D:\java\pmd\src\src\net\sourceforge\pmd\renderers\TextPadRenderer.java(31, ShortVariable):&nbsp; Avoid variables with short names</CODE></p>
 *
 * @author Jeff Epstein, based upon <a href="EmacsRenderer.html">EmacsRenderer</a>, Tuesday, September 23, 2003
 */
public class TextPadRenderer extends AbstractIncrementingRenderer {

    public static final String NAME = "textpad";

    public TextPadRenderer() {
	super(NAME, "TextPad integration.");
    }

    public String defaultFileExtension() { return "txt"; }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
	Writer writer = getWriter();
	StringBuffer buf = new StringBuffer();
	while (violations.hasNext()) {
	    RuleViolation rv = violations.next();
	    buf.setLength(0);
	    //Filename
	    buf.append(rv.getFilename() + "(");
	    //Line number
	    buf.append(Integer.toString(rv.getBeginLine())).append(",  ");
	    //Name of violated rule
	    buf.append(rv.getRule().getName()).append("):  ");
	    //Specific violation message
	    buf.append(rv.getDescription()).append(PMD.EOL);
	    writer.write(buf.toString());
	}
    }
}
