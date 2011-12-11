/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import net.sourceforge.pmd.TestBase;

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
    }
}
