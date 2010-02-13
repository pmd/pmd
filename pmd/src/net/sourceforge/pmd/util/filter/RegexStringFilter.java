/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A filter which uses a regular expression to match Strings. Invalid regular
 * expressions will match nothing.
 * <p>
 * Because regular expression matching is slow, and a common usage is to match
 * some sort of relative file path, the regular expression is checked to see if
 * it can be evaluated using much faster calls to
 * {@link String#endsWith(String)}.
 */
public class RegexStringFilter implements Filter<String> {
    /**
     * Matches regular expressions begin with an optional {@code ^}, then
     * {@code .*}, then a literal path, with an optional file extension, and
     * finally an optional {@code $} at the end. The {@code .} in the extension
     * may or may not be preceded by a {@code \} escape. The literal path
     * portion is determine by the absence of any of the following characters:
     * <code>\ [ ( . * ? + | { $</code>
     * 
     * There are two capturing groups in the expression. The first is for the
     * literal path. The second is for the file extension, without the escaping.
     * The concatenation of these two captures creates the {@link String} which
     * can be used with {@link String#endsWith(String)}.
     * 
     * For ease of reference, the non-Java escaped form of this pattern is:
     * <code>\^?\.\*([^\\\[\(\.\*\?\+\|\{\$]+)(?:\\?(\.\w+))?\$?</code>
     */
    private static final Pattern ENDS_WITH = Pattern
	    .compile("\\^?\\.\\*([^\\\\\\[\\(\\.\\*\\?\\+\\|\\{\\$]+)(?:\\\\?(\\.\\w+))?\\$?");

    protected String regex;
    protected Pattern pattern;
    protected String endsWith;

    public RegexStringFilter(String regex) {
	this.regex = regex;
	optimize();
    }

    public String getRegex() {
	return this.regex;
    }

    public String getEndsWith() {
	return this.endsWith;
    }

    protected void optimize() {
	final Matcher matcher = ENDS_WITH.matcher(this.regex);
	if (matcher.matches()) {
	    final String literalPath = matcher.group(1);
	    final String fileExtension = matcher.group(2);
	    if (fileExtension != null) {
		this.endsWith = literalPath + fileExtension;
	    } else {
		this.endsWith = literalPath;
	    }
	} else {
	    try {
		this.pattern = Pattern.compile(this.regex);
	    } catch (PatternSyntaxException e) {
		// If the regular expression is invalid, then pattern will be null.
	    }
	}
    }

    public boolean filter(String obj) {
	if (this.endsWith != null) {
	    return obj.endsWith(this.endsWith);
	} else if (this.pattern != null) {
	    return this.pattern.matcher(obj).matches();
	} else {
	    // The regular expression must have been bad, so it will match nothing.
	    return false;
	}
    }

    @Override
    public String toString() {
	return "matches " + this.regex;
    }
}