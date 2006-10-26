package net.sourceforge.pmd;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class SimpleRuleSetNameMapper {

    private StringBuffer rulesets = new StringBuffer();
    private Map nameMap = new HashMap();

    public SimpleRuleSetNameMapper(String ruleString) {
        populateNameMap();
        if (ruleString.indexOf(',') == -1) {
            check(ruleString);
            return;
        }
        for (StringTokenizer st = new StringTokenizer(ruleString, ","); st.hasMoreTokens();) {
            String tok = st.nextToken();
            check(tok);
        }
    }

    public String getRuleSets() {
        return rulesets.toString();
    }

    private void check(String name) {
        if (name.indexOf("rulesets") == -1 && nameMap.containsKey(name)) {
            append((String) nameMap.get(name));
        } else {
            append(name);
        }
    }

    private void append(String name) {
        if (rulesets.length() > 0) {
            rulesets.append(',');
        }
        rulesets.append(name);
    }

    private void populateNameMap() {
        nameMap.put("basic", "rulesets/basic.xml");
        nameMap.put("jsp", "rulesets/basic-jsp.xml");
        nameMap.put("jsf", "rulesets/basic-jsf.xml");
        nameMap.put("braces", "rulesets/braces.xml");
        nameMap.put("clone", "rulesets/clone.xml");
        nameMap.put("codesize", "rulesets/codesize.xml");
        nameMap.put("controversial", "rulesets/controversial.xml");
        nameMap.put("coupling", "rulesets/coupling.xml");
        nameMap.put("design", "rulesets/design.xml");
        nameMap.put("finalizers", "rulesets/finalizers.xml");
        nameMap.put("imports", "rulesets/imports.xml");
        nameMap.put("j2ee", "rulesets/j2ee.xml");
        nameMap.put("junit", "rulesets/junit.xml");
        nameMap.put("javabeans", "rulesets/javabeans.xml");
        nameMap.put("logging-java", "rulesets/logging-java.xml");
        nameMap.put("logging-jakarta", "rulesets/logging-jakarta-commons.xml");
        nameMap.put("logging-jakarta-commons", "rulesets/logging-jakarta-commons.xml");
        nameMap.put("migrating", "rulesets/migrating.xml");
        nameMap.put("naming", "rulesets/naming.xml");
        nameMap.put("optimizations", "rulesets/optimizations.xml");
        nameMap.put("scratchpad", "rulesets/scratchpad.xml");
        nameMap.put("strictexception", "rulesets/strictexception.xml");
        nameMap.put("strings", "rulesets/strings.xml");
        nameMap.put("sunsecure", "rulesets/sunsecure.xml");
        nameMap.put("typeresolution", "rulesets/typeresolution.xml");
        nameMap.put("unusedcode", "rulesets/unusedcode.xml");
        nameMap.put("33", "rulesets/releases/33.xml");
        nameMap.put("34", "rulesets/releases/34.xml");
        nameMap.put("35", "rulesets/releases/35.xml");
        nameMap.put("36", "rulesets/releases/36.xml");
        nameMap.put("37", "rulesets/releases/37.xml");
        nameMap.put("38", "rulesets/releases/38.xml");
        nameMap.put("39", "rulesets/releases/39.xml");
    }
}
