package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.LongVariableRule;

public class LongVariableRuleTest extends RuleTst {

    private LongVariableRule rule;

    public void setUp() {
        rule = new LongVariableRule();
        rule.setMessage("Avoid long names like {0}");
        rule.addProperty("minimumLength", "12");
    }

    public void testLongVariableField() throws Throwable {
        Report report = process("LongVariableField.java", rule);
        assertEquals(1, report.size());
    }

    public void testLongVariableLocal() throws Throwable {
        Report report = process("LongVariableLocal.java", rule);
        assertEquals(1, report.size());
    }

    public void testLongVariableFor() throws Throwable {
        Report report = process("LongVariableFor.java", rule);
        assertEquals(1, report.size());
    }

    public void testLongVariableParam() throws Throwable {
        Report report = process("LongVariableParam.java", rule);
        assertEquals(1, report.size());
    }

    public void testLongVariableNone() throws Throwable {
        Report report = process("LongVariableNone.java", rule);
        assertEquals(0, report.size());
    }
}
