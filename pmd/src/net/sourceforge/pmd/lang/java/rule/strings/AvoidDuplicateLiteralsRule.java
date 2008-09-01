/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

public class AvoidDuplicateLiteralsRule extends AbstractJavaRule {

    public static final IntegerProperty THRESHOLD_DESCRIPTOR = new IntegerProperty("threshold",
	    "The number of duplicate literals reporting threshold", 1, 20, 4, 1.0f);

    public static final BooleanProperty SKIP_ANNOTATIONS_DESCRIPTOR = new BooleanProperty("skipAnnotations",
	    "Skip literals within Annotations.", false, 2.0f);

    public static final StringProperty EXCEPTION_LIST_DESCRIPTOR = new StringProperty("exceptionlist",
	    "Strings in that list are skipped", null, 3.0f);

    public static final CharacterProperty SEPARATOR_DESCRIPTOR = new CharacterProperty("separator",
	    "Separator used in the exceptionlist", ',', 4.0f);

    public static final StringProperty EXCEPTION_FILE_DESCRIPTOR= new StringProperty("exceptionfile",
	    "File containing strings to skip (one string per line), only used if exceptionlist is not set", null, 5.0f);

    public static class ExceptionParser {

	private static final char ESCAPE_CHAR = '\\';
	private char delimiter;

	public ExceptionParser(char delimiter) {
	    this.delimiter = delimiter;
	}

	public Set<String> parse(String s) {
	    Set<String> result = new HashSet<String>();
	    StringBuffer currentToken = new StringBuffer();
	    boolean inEscapeMode = false;
	    for (int i = 0; i < s.length(); i++) {
		if (inEscapeMode) {
		    inEscapeMode = false;
		    currentToken.append(s.charAt(i));
		    continue;
		}
		if (s.charAt(i) == ESCAPE_CHAR) {
		    inEscapeMode = true;
		    continue;
		}
		if (s.charAt(i) == delimiter) {
		    result.add(currentToken.toString());
		    currentToken = new StringBuffer();
		} else {
		    currentToken.append(s.charAt(i));
		}
	    }
	    if (currentToken.length() > 0) {
		result.add(currentToken.toString());
	    }
	    return result;
	}
    }

    private Map<String, List<ASTLiteral>> literals = new HashMap<String, List<ASTLiteral>>();
    private Set<String> exceptions = new HashSet<String>();
    
    public AvoidDuplicateLiteralsRule() {
	definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
	definePropertyDescriptor(SKIP_ANNOTATIONS_DESCRIPTOR);
	definePropertyDescriptor(EXCEPTION_LIST_DESCRIPTOR);
	definePropertyDescriptor(SEPARATOR_DESCRIPTOR);
	definePropertyDescriptor(EXCEPTION_FILE_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	literals.clear();

	if (getProperty(EXCEPTION_LIST_DESCRIPTOR) != null) {
	    ExceptionParser p = new ExceptionParser(getProperty(SEPARATOR_DESCRIPTOR));
	    exceptions = p.parse(getProperty(EXCEPTION_LIST_DESCRIPTOR));
	} else if (getProperty(EXCEPTION_FILE_DESCRIPTOR) != null) {
	    exceptions = new HashSet<String>();
	    LineNumberReader reader = null;
	    try {
		reader = new LineNumberReader(new BufferedReader(new FileReader(new File(
			getProperty(EXCEPTION_FILE_DESCRIPTOR)))));
		String line;
		while ((line = reader.readLine()) != null) {
		    exceptions.add(line);
		}
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    } finally {
		try {
		    if (reader != null) {
			reader.close();
		    }
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	    }
	}

	super.visit(node, data);

	int threshold = getProperty(THRESHOLD_DESCRIPTOR);
	for (String key : literals.keySet()) {
	    List<ASTLiteral> occurrences = literals.get(key);
	    if (occurrences.size() >= threshold) {
		Object[] args = new Object[] { key, Integer.valueOf(occurrences.size()),
			Integer.valueOf(occurrences.get(0).getBeginLine()) };
		addViolation(data, occurrences.get(0), args);
	    }
	}
	return data;
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
	// just catching strings of 5 chars or more (including the enclosing quotes) for now - no numbers
	if (node.getImage() == null || node.getImage().indexOf('\"') == -1 || node.getImage().length() < 5) {
	    return data;
	}

	// skip any exceptions
	if (exceptions.contains(node.getImage().substring(1, node.getImage().length() - 1))) {
	    return data;
	}

	// Skip literals in annotations
	if (getProperty(SKIP_ANNOTATIONS_DESCRIPTOR) && node.getFirstParentOfType(ASTAnnotation.class) != null) {
	    return data;
	}

	if (literals.containsKey(node.getImage())) {
	    List<ASTLiteral> occurrences = literals.get(node.getImage());
	    occurrences.add(node);
	} else {
	    List<ASTLiteral> occurrences = new ArrayList<ASTLiteral>();
	    occurrences.add(node);
	    literals.put(node.getImage(), occurrences);
	}

	return data;
    }
}
