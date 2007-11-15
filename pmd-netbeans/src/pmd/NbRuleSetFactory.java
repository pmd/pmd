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
package pmd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sourceforge.pmd.ExternalRuleID;
import net.sourceforge.pmd.Rule;

import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

/** Special RuleSetFactory that use different classloader.
 *
 * @author  Radim Kubacki
 */
public class NbRuleSetFactory extends RuleSetFactory {

    private static final String BUNDLE_RULESET = "org/netbeans/modules/pmd/resources/badbundlecode.xml"; // NOI18N
    private static final String CLASSES_RULESET = "org/netbeans/modules/pmd/resources/innerclasses.xml"; // NOI18N
    
    private static NbRuleSetFactory instance;
    
    public static NbRuleSetFactory getDefault () {
        if (instance == null) {
            instance = new NbRuleSetFactory ();
        }
        return instance;
    }
    
    /** Creates a new instance of NbRuleSetFactory */
    public NbRuleSetFactory() {
    }
    
    /**
     * Returns an Iterator of RuleSet objects
     */
    public Iterator<RuleSet> getRegisteredRuleSets() throws RuleSetNotFoundException {
        List<RuleSet> ruleSets = new ArrayList<RuleSet>();
        ruleSets.add (createRuleSet (BUNDLE_RULESET));
        ruleSets.add (createRuleSet (CLASSES_RULESET));

        return ruleSets.iterator();
    }

    /**
     * Creates a ruleset.  If passed a comma-delimited string (rulesets/basic.xml,rulesets/unusedcode.xml)
     * it will parse that string and create a new ruleset for each item in the list.
     */
    public RuleSet createRuleSet(String name) throws RuleSetNotFoundException {
        if (name.indexOf(',') == -1) {
           return createRuleSet(tryToGetStreamTo(name));
        }

        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(name, ","); st.hasMoreTokens();) {
            String ruleSetName = st.nextToken().trim();
            RuleSet tmpRuleSet = createRuleSet(ruleSetName);
            ruleSet.addRuleSet(tmpRuleSet);
        }
        return ruleSet;
    }

    public RuleSet createRuleSet(InputStream inputStream) {
        // The only difference is changed class loader
        
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));
            ruleSet.setDescription(root.getChildNodes().item(1).getFirstChild().getNodeValue());

            NodeList rules = root.getElementsByTagName("rule");
            for (int i =0; i<rules.getLength(); i++) {
                Node ruleNode = rules.item(i);
                Rule rule = null;
                if (ruleNode.getAttributes().getNamedItem("ref") != null) {
                    ExternalRuleID externalRuleID = new ExternalRuleID(ruleNode.getAttributes().getNamedItem("ref").getNodeValue());
                    // Changed: class loader & factory
                    RuleSetFactory rsf = new NbRuleSetFactory();
                    RuleSet externalRuleSet = rsf.createRuleSet(classLoader ().getResourceAsStream(externalRuleID.getFilename()));
                    rule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
                } else {
                    // carefully here
                    rule = (Rule)Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue()).newInstance();
                    rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
                    rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
                }

                // get the description, priority, example and properties (if any)
                Node node = ruleNode.getFirstChild();
                while (node != null) {
                    if (node.getNodeName() != null && node.getNodeName().equals("description")) {
                        rule.setDescription(node.getFirstChild().getNodeValue());
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("priority")) {
                        rule.setPriority(Integer.parseInt(node.getFirstChild().getNodeValue()));
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("example")) {
                        rule.addExample(node.getFirstChild().getNextSibling().getNodeValue());
                    }

                    if (node.getNodeName().equals("properties")) {
                        Node propNode = node.getFirstChild().getNextSibling();
                        while (propNode != null && propNode.getAttributes() != null) {
                            String propName = propNode.getAttributes().getNamedItem("name").getNodeValue();
                            String propValue = propNode.getAttributes().getNamedItem("value").getNodeValue();
                            rule.addProperty(propName, propValue);
                            propNode = propNode.getNextSibling().getNextSibling();
                        }
                    }

                    node = node.getNextSibling();
                }
                ruleSet.addRule(rule);
            }
            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }

    private ClassLoader classLoader () {
        return (ClassLoader)org.openide.util.Lookup.getDefault().lookup(ClassLoader.class);
    }
    
    /** Uses system ClassLoader to get the stream */
    private InputStream tryToGetStreamTo(String name) throws RuleSetNotFoundException {
        InputStream in = classLoader ().getResourceAsStream(name);
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find ruleset " + name + "; make sure that path is on the CLASSPATH");
        }
        return in;
    }
}
