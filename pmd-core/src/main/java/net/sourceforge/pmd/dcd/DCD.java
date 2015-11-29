/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.dcd.graph.UsageGraph;
import net.sourceforge.pmd.dcd.graph.UsageGraphBuilder;
import net.sourceforge.pmd.util.FileFinder;
import net.sourceforge.pmd.util.filter.Filter;
import net.sourceforge.pmd.util.filter.Filters;

/**
 * The Dead Code Detector is used to find dead code.  What is dead code?
 * Dead code is code which is not used by other code?  It exists, but it not
 * used.  Unused code is clutter, which can generally be a candidate for
 * removal.
 * <p>
 * When performing dead code detection, there are various sets of files/classes
 * which must be identified.  An analogy of the dead code analysis as
 * a <em>foot race</em> is used to help clarify each of these sets:
 * <ol>
 * <li>The <em>direct users</em> is the set of Classes which will always be
 * parsed to determine what code they use.  This set is the starting point of
 * the race.</li>
 * <li>The <em>indirect users</em> is the set of Classes which will only be
 * parsed if they are accessed by code in the <em>direct users</em> set, or
 * in the <em>indirect users</em> set.  This set is the course of the race.</li>
 * <li>The <em>dead code candidates</em> are the set of Classes which are the
 * focus of the dead code detection.  This set is the finish line of the
 * race.</li>
 * </ol>
 * <p>
 * Typically there is intersection between the set of <em>direct users</em>,
 * <em>indirect users</em> and <em>dead code candidates</em>, although it is
 * not required.  If the sets are defined too tightly, there the potential for
 * a lot of code to be considered as dead code.  You may need to expand the
 * <em>direct users</em> or <em>indirect users</em> sets, or explore using
 * different options.
 *
 * @author Ryan Gustafson <ryan.gustafson@gmail.com>,
 */
public class DCD {
	//
	// TODO Implement the direct users, indirect users, and dead code
	// candidate sets.  Use the pmd.util.filter.Filter APIs.  Need to come up
	// with something like Ant's capabilities for <fileset>, it's a decent way
	// to describe a collection of files in a directory structure.  That or we
	// just adopt Ant, and screw command line/external configuration?
	//
	// TODO Better yet, is there a way to enumerate all available classes using
	// ClassLoaders instead of having to specify Java file names as surrogates
	// for the Classes we truly desire?
	//
	// TODO Methods defined on classes/interfaces not within the scope of
	// analysis which are implemented/overridden, are not usage violations.
	//
	// TODO Static final String and primitive types are often inlined by the
	// compiler, so there may actually be no explicit usages.
	//
	// TODO Ignore "public static void main(String[])"
	//
	// TODO Check for method which is always overridden, and never called
	// directly.
	//
	// TODO For methods, record which classes/interfaces methods they are
	// overriding/implementing.
	//
	// TODO Allow recognition of indirect method patterns, like those used by
	// EJB Home and Remote interfaces with corresponding implementation classes.
	//
	// TODO
	// 1) For each class/member, a set of other class/members which reference.
	// 2) For every class/member which is part of an interface or super-class,
	// allocate those references to the interface/super-class.
	//

	public static void dump(UsageGraph usageGraph, boolean verbose) {
		usageGraph.accept(new DumpNodeVisitor(), Boolean.valueOf(verbose));
	}

	public static void report(UsageGraph usageGraph, boolean verbose) {
		usageGraph.accept(new UsageNodeVisitor(), Boolean.valueOf(verbose));
	}

	public static void main(String[] args) throws Exception {
		// 1) Directories
		List<File> directories = new ArrayList<>();
		directories.add(new File("C:/pmd/workspace/pmd-trunk/src"));

		// Basic filter
		FilenameFilter javaFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// Recurse on directories
				if (new File(dir, name).isDirectory()) {
					return true;
				} else {
					return name.endsWith(".java");
				}
			}
		};

		// 2) Filename filters
		List<FilenameFilter> filters = new ArrayList<>();
		filters.add(javaFilter);

		assert directories.size() == filters.size();

		// Find all files, convert to class names
		List<String> classes = new ArrayList<>();
		for (int i = 0; i < directories.size(); i++) {
			File directory = directories.get(i);
			FilenameFilter filter = filters.get(i);
			List<File> files = new FileFinder().findFilesFrom(directory, filter, true);
			for (File file : files) {
				String name = file.getPath();

				// Chop off directory
				name = name.substring(directory.getPath().length() + 1);

				// Drop extension
				name = name.replaceAll("\\.java$", "");

				// Trim path separators
				name = name.replace('\\', '.');
				name = name.replace('/', '.');

				classes.add(name);
			}
		}

		long start = System.currentTimeMillis();

		// Define filter for "indirect users" and "dead code candidates".
		// TODO Need to support these are different concepts.
		List<String> includeRegexes = Arrays.asList(new String[] { "net\\.sourceforge\\.pmd\\.dcd.*", "us\\..*" });
		List<String> excludeRegexes = Arrays.asList(new String[] { "java\\..*", "javax\\..*", ".*\\.twa\\..*" });
		Filter<String> classFilter = Filters.buildRegexFilterExcludeOverInclude(includeRegexes, excludeRegexes);
		System.out.println("Class filter: " + classFilter);

		// Index each of the "direct users"
		UsageGraphBuilder builder = new UsageGraphBuilder(classFilter);
		int total = 0;
		for (String clazz : classes) {
			System.out.println("indexing class: " + clazz);
			builder.index(clazz);
			total++;
			if (total % 20 == 0) {
				System.out.println(total + " : " + total / ((System.currentTimeMillis() - start) / 1000.0));
			}
		}

		// Reporting
		boolean dump = true;
		boolean deadCode = true;
		UsageGraph usageGraph = builder.getUsageGraph();
		if (dump) {
			System.out.println("--- Dump ---");
			dump(usageGraph, true);
		}
		if (deadCode) {
			System.out.println("--- Dead Code ---");
			report(usageGraph, true);
		}
		long end = System.currentTimeMillis();
		System.out.println("Time: " + (end - start) / 1000.0);
	}
}