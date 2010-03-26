package net.sourceforge.pmd.eclipse.ui.quickfix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAndReplace extends AbstractFix {

	private final String searchStr;
	private final String replaceStr;
	private final Pattern pattern;
	
	public SearchAndReplace(String searchString, String replacement) {
		super("Search & replace");
		
		searchStr = searchString;
		replaceStr = replacement;
		pattern = Pattern.compile(searchStr);
	}

    /**
     * @see net.sourceforge.pmd.eclipse.Fix#fix(java.lang.String, int)
     */
    public String fix(String sourceCode, int lineNumber) {
        
        Matcher matcher = pattern.matcher(sourceCode);
        return matcher.replaceAll(replaceStr);
    }

}
