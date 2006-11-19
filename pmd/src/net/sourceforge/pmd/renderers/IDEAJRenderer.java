/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

public class IDEAJRenderer extends AbstractRenderer {

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

    public void render(Writer writer, Report report) throws IOException {
        if (args[4].equals(".method")) {
            // working on a directory tree
            String sourcePath = args[3];
            render(writer, report, sourcePath);
            return;
        }
        // working on one file
        String classAndMethodName = args[4];
        String singleFileName = args[5];
        render(writer, report, classAndMethodName, singleFileName);
    }

    private void render(Writer writer, Report report, String sourcePathString) throws IOException {
        SourcePath sourcePath = new SourcePath(sourcePathString);
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(rv.getDescription() + PMD.EOL);
            buf.append(" at ").append(getFullyQualifiedClassName(rv.getFilename(), sourcePath)).append(".method(");
            buf.append(getSimpleFileName(rv.getFilename())).append(':').append(rv.getBeginLine()).append(')').append(PMD.EOL);
            writer.write(buf.toString());
        }
    }

    private void render(Writer writer, Report report, String classAndMethod, String file) throws IOException {
        StringBuffer buf = new StringBuffer();
        for (Iterator i = report.iterator(); i.hasNext();) {
            buf.setLength(0);
            IRuleViolation rv = (IRuleViolation) i.next();
            buf.append(rv.getDescription()).append(PMD.EOL);
            buf.append(" at ").append(classAndMethod).append('(').append(file).append(':').append(rv.getBeginLine()).append(')').append(PMD.EOL);
            writer.write(buf.toString());
        }
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
