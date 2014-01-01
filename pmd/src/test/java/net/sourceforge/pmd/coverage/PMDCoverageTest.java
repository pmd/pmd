/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.coverage;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sourceforge.pmd.PMD;

import org.junit.Test;



public class PMDCoverageTest {

	/**
	 * Test some of the PMD command line options
	 */
	@Test
	public void testPmdOptions() {
		runPmd("-d src/main/java/net/sourceforge/pmd/lang/java/rule/design -f text -R rulesets/internal/all-java.xml -version 1.5 -language java -stress -benchmark");
	}

	/**
	 * Run the PMD command line tool, i.e. call PMD.main().
	 * 
	 * @param commandLine
	 */
	private void runPmd(String commandLine) {
		String[] args;
		args = commandLine.split("\\s");

		File f = null;
		try {
			f = File.createTempFile("pmd", ".txt");
			int n = args.length;
			String[] a = new String[n + 2];
			System.arraycopy(args, 0, a, 0, n);
			a[n] = "-reportfile";
			a[n + 1] = f.getAbsolutePath();
			args = a;

			PMD.run(args);

			// FIXME: check that output doesn't have parsing errors
		} catch (IOException ioe) {
			fail("Problem creating temporary file: " + ioe.getLocalizedMessage());
		} finally {
			if (f != null) f.delete();
		}
	}

	/**
	 * Name of the configuration file used by testResourceFileCommands().
	 */
	private static final String PMD_CONFIG_FILE = "pmd_tests.conf";

	/**
	 * Run PMD using the command lines found in PMD_CONFIG_FILE.
	 */
	@Test
	public void testResourceFileCommands() {

		InputStream is = getClass().getResourceAsStream(PMD_CONFIG_FILE);

		if (is != null) {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(is));
				String l;
				while ((l = r.readLine()) != null) {
					l = l.trim();
					if (l.length() == 0 || l.charAt(0) == '#') {
						continue;
					}

					runPmd(l);
				}
				r.close();
			} catch (IOException ioe) {
				fail("Problem reading config file: " + ioe.getLocalizedMessage());
			}
		} else {
			fail("Missing config file: " + PMD_CONFIG_FILE);
		}
	}

	public static junit.framework.Test suite() {
		return new junit.framework.JUnit4TestAdapter(PMDCoverageTest.class);
	}
}
