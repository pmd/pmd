/*
 * Created on 22 juin 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.core.rulesets;

import net.sourceforge.pmd.eclipse.core.rulesets.impl.RuleSetsManagerImplTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.PriorityTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.PropertiesTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.PropertyTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSetTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSetsTest;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleTest;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/21 23:06:53  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 *
 */

public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Tests for net.sourceforge.pmd.eclipse.core.rulesets");
        //$JUnit-BEGIN$
        suite.addTestSuite(RuleTest.class);
        suite.addTestSuite(PriorityTest.class);
        suite.addTestSuite(PropertiesTest.class);
        suite.addTestSuite(PropertyTest.class);
        suite.addTestSuite(RuleSetsTest.class);
        suite.addTestSuite(RuleSetTest.class);
        suite.addTestSuite(RuleSetsManagerImplTest.class);
        //$JUnit-END$
        return suite;
    }

}
