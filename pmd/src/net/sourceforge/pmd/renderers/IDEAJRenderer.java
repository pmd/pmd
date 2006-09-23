/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.IRuleViolation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class IDEAJRenderer extends AbstractRenderer implements Renderer {

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");
	
    private static class SourcePath {

        private Set paths = new HashSet();

        public SourcePath(String sourcePathString) {
            for (StringTokenizer st = new StringTokenizer(sourcePathString, PATH_SEPARATOR); st.hasMoreTokens();) {
                paths.add(st.nextToken());
            }
        }

        public String clipPath(String fullFilename) {
            for (Iterator i = paths.iterator(); i.hasNext();) {
                String path = (String) i.next();
                if (fullFilename.startsWith(path)) {
                    return fullFilename.substring(path.length() + 1);
                }
            }
            throw new RuntimeException("Couldn't find src path for " + fullFilename);
        }
    }

    private String[] args;

    public IDEAJRenderer(String[] args) {
        this.args = args;
    }

    public String render(Report report) {
        if (args[4].equals(".method")) {
            // working on a directory tree
            String sourcePath = args[3];
            return render(report, sourcePath);
        }
        // working on one file
        String classAndMethodName = args[4];
        String singleFileName = args[5];
        return render(report, classAndMethodName, singleFileName);
    }

    private String render(Report report, String sourcePathString) {
        SourcePath sourcePath = new SourcePath(sourcePathString);
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(rv.getDescription() + PMD.EOL);
            buf.append(" at " + getFullyQualifiedClassName(rv.getFilename(), sourcePath) + ".method(" + getSimpleFileName(rv.getFilename()) + ":" + rv.getBeginLine() + ")" + PMD.EOL);
        }
        return buf.toString();
    }

    private String render(Report report, String classAndMethod, String file) {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(rv.getDescription() + PMD.EOL);
            buf.append(" at " + classAndMethod + "(" + file + ":" + rv.getBeginLine() + ")" + PMD.EOL);
        }
        return buf.toString();
    }

    private String getFullyQualifiedClassName(String in, SourcePath sourcePath) {
        String classNameWithSlashes = sourcePath.clipPath(in);
        String className = classNameWithSlashes.replace(FILE_SEPARATOR.charAt(0), '.');
        return className.substring(0, className.length() - 5);
    }

    private String getSimpleFileName(String in) {
        return in.substring(in.lastIndexOf(FILE_SEPARATOR) + 1);
    }
}
