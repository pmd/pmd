/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config;

import java.util.Map;
import java.util.HashMap;
import org.netbeans.junit.NbTestCase;

/**
 * @author radim
 */
public class PMDOptionsSettingsTest extends NbTestCase {
    
    public PMDOptionsSettingsTest(String testName) {
        super(testName);
    }

    /**
     * Test of getHelpCtx method, of class pmd.RunPMDAction.
     */
    public void testRuleProperties() {
//      Preferences prefs = mock(Preferences.class);
//      PMDOptionsSettings settings = new PMDOptionsSettings(prefs);
      PMDOptionsSettings settings = PMDOptionsSettings.getDefault();
      Map<String, Map<String, String>> ruleProperties = new HashMap<String, Map<String, String>>();
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("propA", "valueA");
      properties.put("propB", "valueB");
      ruleProperties.put("rule1", properties);
      properties = new HashMap<String, String>();
      properties.put("prop1", "value1");
      ruleProperties.put("rule2", properties);
      settings.setRuleProperties(ruleProperties);

      Map<String, Map<String, String>> readRuleProperties = settings.getRuleProperties();
      assertNotNull(readRuleProperties);
      assertTrue("correct size of " + readRuleProperties, readRuleProperties.size() == ruleProperties.size());
      for(Map.Entry<String, Map<String, String>> entry : readRuleProperties.entrySet()) {
        assertTrue(ruleProperties.containsKey(entry.getKey()));
        assertEquals(entry.getValue(), ruleProperties.get(entry.getKey()));
      }
    }

}
