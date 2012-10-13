/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.pmd.TestBase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author rpelisse
 *
 */
public class PmdBuildTaskTest extends TestBase {

    @Test
    public void antTask() throws Exception {
	PmdBuildTask task = new PmdBuildTask();
	task.setRulesDirectory(TEST_DIR + "rulesets");
	task.setTarget(TEST_DIR + "target");
	task.setSiteXml(TEST_DIR + "site/site.pre.xml");
	task.setSiteXmlTarget(TEST_DIR + "site/site.xml");
	task.execute();

	String site = IOUtils.toString(new File(TEST_DIR + "site/site.xml").toURI());
	assertTrue(site.contains("<item name=\"Basic\""));
	assertTrue(site.contains("<item name=\"Code Size\""));
	assertTrue(site.indexOf("<item name=\"Basic\"") < site.indexOf("<item name=\"Code Size\""));
    }
}
