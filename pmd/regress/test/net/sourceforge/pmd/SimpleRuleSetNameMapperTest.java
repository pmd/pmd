package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class SimpleRuleSetNameMapperTest {

    @Test
    public void testMultipleSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("unusedcode,basic");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    @Test
    public void testOneSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("basic");
        assertEquals("rulesets/basic.xml", s.getRuleSets());
    }

    @Test
    public void testMultipleRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml,rulesets/basic.xml");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    @Test
    public void testOneRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml");
        assertEquals("rulesets/unusedcode.xml", s.getRuleSets());
    }

    @Test
    public void testMix() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/unusedcode.xml,basic");
        assertEquals("rulesets/unusedcode.xml,rulesets/basic.xml", s.getRuleSets());
    }

    @Test
    public void testUnknown() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("favorites.xml");
        assertEquals("favorites.xml", s.getRuleSets());
    }

    @Test
    public void testUnknownAndSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("basic,favorites.xml");
        assertEquals("rulesets/basic.xml,favorites.xml", s.getRuleSets());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SimpleRuleSetNameMapperTest.class);
    }
}
