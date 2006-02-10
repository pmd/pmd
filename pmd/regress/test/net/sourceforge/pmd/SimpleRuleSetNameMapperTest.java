package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;

public class SimpleRuleSetNameMapperTest extends TestCase {

    public void testMultipleSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("unusedcode,basic");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    public void testOneSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("basic");
        assertEquals("rulesets/basic.xml", s.getRuleSets());
    }

    public void testMultipleRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml,rulesets/basic.xml");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    public void testOneRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml");
        assertEquals("rulesets/unusedcode.xml", s.getRuleSets());
    }

    public void testMix() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml,basic");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    public void testUnknown() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("favorites.xml");
        assertEquals("favorites.xml", s.getRuleSets());
    }

    public void testUnknownAndSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("basic,favorites.xml");
        assertEquals("rulesets/basic.xml,favorites.xml", s.getRuleSets());
    }
}
