/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.ant;

import junit.framework.TestCase;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import org.apache.tools.ant.BuildException;

public class PMDTaskTest extends TestCase {

    public void testNoFormattersValidation() {
        PMDTask task = new PMDTask();
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - no Formatters");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testFormatterWithNoToFileAttribute() {
        PMDTask task = new PMDTask();
        task.addFormatter(new Formatter());
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - a Formatter was missing a toFile attribute");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testNoRuleSets() {
        PMDTask task = new PMDTask();
        task.setPrintToConsole(true);
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - no rulesets");
        } catch (BuildException be) {
            // cool
        }
    }

}
