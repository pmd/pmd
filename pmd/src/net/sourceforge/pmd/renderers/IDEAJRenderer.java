/*
 * User: tom
 * Date: Sep 23, 2002
 * Time: 5:07:40 PM
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class IDEAJRenderer {
    protected String EOL = System.getProperty("line.separator", "\n");

    private static class SourcePath {

        private Set paths = new HashSet();

        public SourcePath(String sourcePathString) {
            for (StringTokenizer st = new StringTokenizer(sourcePathString, ";"); st.hasMoreTokens();) {
                String tok = st.nextToken();
                paths.add(tok);
            }
        }

        public String clipPath(String fullFilename) {
            for (Iterator i = paths.iterator(); i.hasNext();) {
                String path = (String)i.next();
                if (fullFilename.startsWith(path)) {
                    return fullFilename.substring(path.length()+1);
                }
            }
            throw new RuntimeException("Couldn't find src path for " + fullFilename);
        }
    }

	public String render(Report report, String classAndMethod, String file) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(rv.getDescription() + EOL);
            buf.append(" at " + classAndMethod + "(" + file + ":" + rv.getLine() +")" + EOL);
        }
        return buf.toString();
	}

    public String render(Report report, String sourcePathString) {
        SourcePath sourcePath = new SourcePath(sourcePathString);
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            RuleViolation rv = (RuleViolation) i.next();
            buf.append(rv.getDescription() + EOL);
            buf.append(" at " + getFullyQualifiedClassName(rv.getFilename(), sourcePath) + ".method(" + getSimpleFileName(rv.getFilename()) +":" + rv.getLine() +")" + EOL);
        }
        return buf.toString();
    }

    private String getFullyQualifiedClassName(String in, SourcePath sourcePath) {
        String classNameWithSlashes = sourcePath.clipPath(in);
        String className = classNameWithSlashes.replace(System.getProperty("file.separator").charAt(0),'.');
        return className.substring(0, className.indexOf(".java"));
    }

    private String getSimpleFileName(String in) {
        return in.substring(in.lastIndexOf(System.getProperty("file.separator"))+1);
    }
}
