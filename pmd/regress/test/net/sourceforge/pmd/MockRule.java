/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 11:05:13 AM
 */
package test.net.sourceforge.pmd;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;

import java.util.List;

public class MockRule implements Rule {

    private String name;
    private String desc;

    public MockRule(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {return name;}
    public String getDescription() {return desc;}

    public void apply(List astCompilationUnits, RuleContext ctx) {
    }
}
