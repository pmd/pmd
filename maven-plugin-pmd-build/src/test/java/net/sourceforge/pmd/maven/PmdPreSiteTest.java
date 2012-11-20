package net.sourceforge.pmd.maven;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Before;
import org.junit.Test;

public class PmdPreSiteTest extends AbstractMojoTestCase {

    @Before
    @Override
    public void setUp() throws Exception {
	super.setUp();
	FileUtils.deleteDirectory( new File( getBasedir(), "target/unit/sample-pmd" ) );
    }

    @Test
    public void testMojo() throws Exception {
	FileUtils.copyDirectoryStructure(new File("src/test/resources/sample-pmd"),
		new File("target/unit/sample-pmd"));

	File pom = getTestFile( "target/unit/sample-pmd/pom.xml" );
	assertNotNull( pom );
	assertTrue( pom.exists() );

	PmdPreSite myMojo = (PmdPreSite) lookupMojo( "pmd-pre-site", pom );
	assertNotNull( myMojo );
	myMojo.execute();


	String codeSizeRuleset = IOUtils.toString(new File("target/unit/sample-pmd/target/generated-xdocs/rules/java/codesize.xml").toURI());
	assertTrue(codeSizeRuleset.contains("minimum"));

	String basicRuleset = IOUtils.toString(new File("target/unit/sample-pmd/target/generated-xdocs/rules/java/basic.xml").toURI());
	assertEquals(1, StringUtils.countMatches(basicRuleset, "<subsection"));

	String indexPage = IOUtils.toString(new File("target/unit/sample-pmd/target/generated-xdocs/rules/index.xml").toURI());
	assertFalse(indexPage.contains("<li>: </li>"));

	String site = IOUtils.toString(new File("target/unit/sample-pmd/src/site/site.xml").toURI());
	assertTrue(site.contains("<item name=\"Basic\""));
	assertTrue(site.contains("<item name=\"Code Size\""));
	assertTrue(site.indexOf("<item name=\"Basic\"") < site.indexOf("<item name=\"Code Size\""));
    }


}
