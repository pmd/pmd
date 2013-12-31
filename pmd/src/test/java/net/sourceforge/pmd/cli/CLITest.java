/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.util.FileUtil;

import org.junit.BeforeClass;
import org.junit.Test;


/**
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class CLITest {

	private static final String TEST_OUPUT_DIRECTORY = "target/cli-tests/";

	// Points toward a folder without any source files, to avoid actually PMD and slowing down tests
	private static final String SOURCE_FOLDER = "src/main/resources";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {
		System.setProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
		File testOuputDir = new File(TEST_OUPUT_DIRECTORY);
		if (!testOuputDir.exists()) {
		    assertTrue("failed to create output directory for test:" + testOuputDir.getAbsolutePath(), testOuputDir.mkdirs());
		}
	}

        private void createTestOutputFile(String filename) {
		try {
			PrintStream out = new PrintStream(new FileOutputStream(filename));
			System.setOut(out);
			System.setErr(out);
		} catch (FileNotFoundException e) {
			fail("Can't create file " + filename + " for test.");
		}
	}

	@Test
	public void minimalArgs() {
		String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-basic,java-design"};
		runTest(args,"minimalArgs");
	}

	@Test
	public void usingDebug() {
		String[] args = { "-d", SOURCE_FOLDER, "-f" ,"text","-R", "java-basic,java-design","-debug"};
		runTest(args,"minimalArgsWithDebug");
	}


	@Test
	public void changeJavaVersion() {
		String[] args = { "-d", SOURCE_FOLDER, "-f", "text", "-R", "java-basic,java-design", "-version","1.5", "-language", "java", "-debug"};
		String resultFilename = runTest(args, "chgJavaVersion");
		assertTrue("Invalid Java version",FileUtil.findPatternInFile(new File(resultFilename), "Using Java version: Java 1.5"));
	}

	@Test
	public void useEcmaScript() {
		String[] args = { "-d", SOURCE_FOLDER, "-f", "xml", "-R", "ecmascript-basic", "-version","3","-l", "ecmascript", "-debug"};
		String resultFilename = runTest(args,"useEcmaScript");
		assertTrue("Invalid Java version",FileUtil.findPatternInFile(new File(resultFilename), "Using Ecmascript version: Ecmascript 3"));
	}

	private String runTest(String[] args, String testname) {
		String filename = TEST_OUPUT_DIRECTORY + testname + ".txt";
		long start = System.currentTimeMillis();
		createTestOutputFile(filename);
		System.out.println("Start running test " + testname);
		runPMDWith(args);
		checkStatusCode();
		System.out.println("Test finished successfully after " + (System.currentTimeMillis() - start) + "ms.");
		return filename;
	}

	private void runPMDWith(String[] args) {
		PMD.main(args);
	}

	private void checkStatusCode() {
		int statusCode = Integer.valueOf(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY));
		if ( statusCode > 0 )
			fail("PMD failed with status code:" + statusCode);
	}


}
