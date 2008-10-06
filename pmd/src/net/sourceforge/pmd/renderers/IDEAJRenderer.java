/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer for IntelliJ IDEA integration.
 */
public class IDEAJRenderer extends OnTheFlyRenderer {

    public static final String NAME = "ideaj";

    public static final String SOURCE_PATH = "sourcePath";
    public static final String CLASS_AND_METHOD_NAME = "classAndMethodName";
    public static final String FILE_NAME = "fileName";

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    private static final String PATH_SEPARATOR = System.getProperty("path.separator");

    private final String sourcePath;
    private final String classAndMethodName;
    private final String fileName;

    public IDEAJRenderer(Properties properties) {
	super(NAME, "IntelliJ IDEA integration.", properties);
	super.defineProperty(SOURCE_PATH, "Source path.");
	super.defineProperty(CLASS_AND_METHOD_NAME,
		"Class and Method name, pass '.method' when processing a directory.");
	super.defineProperty(FILE_NAME, "File name.");

	this.sourcePath = properties.getProperty(SOURCE_PATH);
	this.classAndMethodName = properties.getProperty(CLASS_AND_METHOD_NAME);
	this.fileName = properties.getProperty(FILE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
	Writer writer = getWriter();
	if (classAndMethodName.equals(".method")) {
	    // working on a directory tree
	    renderDirectoy(writer, violations);
	} else {
	    // working on one file
	    renderFile(writer, violations);
	}
    }

    private void renderDirectoy(Writer writer, Iterator<RuleViolation> violations) throws IOException {
	SourcePath sourcePath = new SourcePath(this.sourcePath);
	StringBuffer buf = new StringBuffer();
	while (violations.hasNext()) {
	    buf.setLength(0);
	    RuleViolation rv = violations.next();
	    buf.append(rv.getDescription() + PMD.EOL);
	    buf.append(" at ").append(getFullyQualifiedClassName(rv.getFilename(), sourcePath)).append(".method(");
	    buf.append(getSimpleFileName(rv.getFilename())).append(':').append(rv.getBeginLine()).append(')').append(
		    PMD.EOL);
	    writer.write(buf.toString());
	}
    }

    private void renderFile(Writer writer, Iterator<RuleViolation> violations) throws IOException {
	StringBuffer buf = new StringBuffer();
	while (violations.hasNext()) {
	    buf.setLength(0);
	    RuleViolation rv = violations.next();
	    buf.append(rv.getDescription()).append(PMD.EOL);
	    buf.append(" at ").append(this.classAndMethodName).append('(').append(this.fileName).append(':').append(
		    rv.getBeginLine()).append(')').append(PMD.EOL);
	    writer.write(buf.toString());
	}
    }

    private String getFullyQualifiedClassName(String fileName, SourcePath sourcePath) {
	String classNameWithSlashes = sourcePath.clipPath(fileName);
	String className = classNameWithSlashes.replace(FILE_SEPARATOR.charAt(0), '.');
	return className.substring(0, className.length() - 5);
    }

    private String getSimpleFileName(String fileName) {
	return fileName.substring(fileName.lastIndexOf(FILE_SEPARATOR) + 1);
    }

    private static class SourcePath {

	private Set<String> paths = new HashSet<String>();

	public SourcePath(String sourcePathString) {
	    for (StringTokenizer st = new StringTokenizer(sourcePathString, PATH_SEPARATOR); st.hasMoreTokens();) {
		paths.add(st.nextToken());
	    }
	}

	public String clipPath(String fullFilename) {
	    for (String path : paths) {
		if (fullFilename.startsWith(path)) {
		    return fullFilename.substring(path.length() + 1);
		}
	    }
	    throw new RuntimeException("Couldn't find src path for " + fullFilename);
	}
    }
}
