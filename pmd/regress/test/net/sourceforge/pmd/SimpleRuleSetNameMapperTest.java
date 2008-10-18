package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.SimpleRuleSetNameMapper;

import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class SimpleRuleSetNameMapperTest {

    @Test
    public void testOneSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("java-basic");
        assertEquals("rulesets/java/basic.xml", s.getRuleSets());
    }

    @Test
    public void testMultipleSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("java-unusedcode,java-basic");
        assertEquals("rulesets/java/unusedcode.xml,rulesets/java/basic.xml", s.getRuleSets());
    }

    @Test
    public void testOneRelease() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("50");
        assertEquals("rulesets/50.xml", s.getRuleSets());
    }

    @Test
    public void testOneRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/java/unusedcode.xml");
        assertEquals("rulesets/java/unusedcode.xml", s.getRuleSets());
    }

    @Test
    public void testMultipleRegular() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/java/unusedcode.xml,rulesets/java/basic.xml");
        assertEquals("rulesets/java/unusedcode.xml,rulesets/java/basic.xml", s.getRuleSets());
    }

    @Test
    public void testMix() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("rulesets/java/unusedcode.xml,xml-basic");
        assertEquals("rulesets/java/unusedcode.xml,rulesets/xml/basic.xml", s.getRuleSets());
    }

    @Test
    public void testUnknown() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("nonexistant.xml");
        assertEquals("nonexistant.xml", s.getRuleSets());
    }

    @Test
    public void testUnknownAndSimple() {
        SimpleRuleSetNameMapper s = new SimpleRuleSetNameMapper("jsp-basic,nonexistant.xml");
        assertEquals("rulesets/jsp/basic.xml,nonexistant.xml", s.getRuleSets());
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(SimpleRuleSetNameMapperTest.class);
    }
}
