/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.SimpleNode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AvoidDuplicateLiteralsRule extends AbstractRule {

    public static class ExceptionParser {

        private static final char ESCAPE_CHAR = '\\';
        private char delimiter;

        public ExceptionParser(char delimiter) {
            this.delimiter = delimiter;
        }

        public Set parse(String in) {
            Set result = new HashSet();
            StringBuffer currentToken = new StringBuffer();
            boolean inEscapeMode = false;
            for (int i = 0; i < in.length(); i++) {
                if (inEscapeMode) {
                    inEscapeMode = false;
                    currentToken.append(in.charAt(i));
                    continue;
                }
                if (in.charAt(i) == ESCAPE_CHAR) {
                    inEscapeMode = true;
                    continue;
                }
                if (in.charAt(i) == delimiter) {
                    result.add(currentToken.toString());
                    currentToken = new StringBuffer();
                } else {
                    currentToken.append(in.charAt(i));
                }
            }
            if (currentToken.length() > 0) {
                result.add(currentToken.toString());
            }
            return result;
        }
    }

    private static final char DEFAULT_SEPARATOR = ',';
    private static final String EXCEPTION_LIST_PROPERTY = "exceptionlist";
    private static final String SEPARATOR_PROPERTY = "separator";
    private static final String EXCEPTION_FILE_NAME_PROPERTY = "exceptionfile";

    private Map literals = new HashMap();
    private Set exceptions = new HashSet();

    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();

        if (hasProperty(EXCEPTION_LIST_PROPERTY)) {
            ExceptionParser p;
            if (hasProperty(SEPARATOR_PROPERTY)) {
                p = new ExceptionParser(getStringProperty(SEPARATOR_PROPERTY).charAt(0));
            } else {
                p = new ExceptionParser(DEFAULT_SEPARATOR);
            }
            exceptions = p.parse(getStringProperty(EXCEPTION_LIST_PROPERTY));
        } else if (hasProperty(EXCEPTION_FILE_NAME_PROPERTY)) {
            exceptions = new HashSet();
            LineNumberReader reader = null;
            try {
                reader = new LineNumberReader(new BufferedReader(new FileReader(new File(getStringProperty(EXCEPTION_FILE_NAME_PROPERTY)))));
                String line;
                while ((line = reader.readLine()) != null) {
                    exceptions.add(line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        super.visit(node, data);

        int threshold = getIntProperty("threshold");
        for (Iterator i = literals.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            List occurrences = (List) literals.get(key);
            if (occurrences.size() >= threshold) {
                Object[] args = new Object[]{key, new Integer(occurrences.size()), new Integer(((SimpleNode) occurrences.get(0)).getBeginLine())};
                addViolation(data, (SimpleNode) occurrences.get(0), args);
            }
        }
        return data;
    }

    public Object visit(ASTLiteral node, Object data) {
        // just catching strings of 5 chars or more (including the enclosing quotes) for now - no numbers
        if (node.getImage() == null || node.getImage().indexOf('\"') == -1 || node.getImage().length() < 5) {
            return data;
        }

        // skip any exceptions
        if (exceptions.contains(node.getImage().substring(1, node.getImage().length() - 1))) {
            return data;
        }

        if (literals.containsKey(node.getImage())) {
            List occurrences = (List) literals.get(node.getImage());
            occurrences.add(node);
        } else {
            List occurrences = new ArrayList();
            occurrences.add(node);
            literals.put(node.getImage(), occurrences);
        }

        return data;
    }
}

