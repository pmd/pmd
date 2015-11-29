/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;

/**
 * Renderer for IntelliJ IDEA integration.
 */
public class IDEAJRenderer extends AbstractIncrementingRenderer {

	private String classAndMethodName;
	private String fileName;

	public static final String NAME = "ideaj";

	public static final StringProperty FILE_NAME = new StringProperty("fileName", "File name.", "", 0);
	public static final StringProperty SOURCE_PATH = new StringProperty("sourcePath", "Source path.", "", 1);
	public static final StringProperty CLASS_AND_METHOD_NAME = new StringProperty("classAndMethodName", "Class and Method name, pass '.method' when processing a directory.", "", 2);

	private static final String FILE_SEPARATOR = System.getProperty("file.separator");
	private static final String PATH_SEPARATOR = System.getProperty("path.separator");

	public IDEAJRenderer() {
		super(NAME, "IntelliJ IDEA integration.");
		definePropertyDescriptor(FILE_NAME);
		definePropertyDescriptor(SOURCE_PATH);
		definePropertyDescriptor(CLASS_AND_METHOD_NAME);
	}

	 public String defaultFileExtension() { return "txt"; }
	 
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
	    classAndMethodName = getProperty(CLASS_AND_METHOD_NAME);
	    fileName = getProperty(FILE_NAME);

		Writer writer = getWriter();
		if (".method".equals(classAndMethodName)) {
			// working on a directory tree
			renderDirectoy(writer, violations);
		} else {
			// working on one file
			renderFile(writer, violations);
		}
	}

	private void renderDirectoy(Writer writer, Iterator<RuleViolation> violations) throws IOException {
		SourcePath sourcePath = new SourcePath(getProperty(SOURCE_PATH));
		StringBuilder buf = new StringBuilder();
		while (violations.hasNext()) {
			buf.setLength(0);
			RuleViolation rv = violations.next();
			buf.append(rv.getDescription() + PMD.EOL);
			buf.append(" at ").append(
					getFullyQualifiedClassName(rv.getFilename(), sourcePath)).append(".method(");
			buf.append(getSimpleFileName(rv.getFilename())).append(':')
					.append(rv.getBeginLine()).append(')').append(PMD.EOL);
			writer.write(buf.toString());
		}
	}

	private void renderFile(Writer writer, Iterator<RuleViolation> violations) throws IOException {
		StringBuilder buf = new StringBuilder();
		while (violations.hasNext()) {
			buf.setLength(0);
			RuleViolation rv = violations.next();
			buf.append(rv.getDescription()).append(PMD.EOL);
			buf.append(" at ").append(classAndMethodName).append('(')
					.append(fileName).append(':')
					.append(rv.getBeginLine()).append(')').append(PMD.EOL);
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

		private Set<String> paths = new HashSet<>();

		public SourcePath(String sourcePathString) {
			for (StringTokenizer st = new StringTokenizer(sourcePathString,
					PATH_SEPARATOR); st.hasMoreTokens();) {
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
