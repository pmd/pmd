package test.com.infoether.pmd;

import junit.framework.*;
import com.infoether.pmd.*;
import java.util.*;

public class RuleViolationTest extends TestCase {
    public RuleViolationTest(String name) {
        super(name);
    }
    
    public void testBasic() {
        RuleViolation r = new RuleViolation(new Rule() {
            public String getName() {return "name";}
            public String getDescription()  {return "desc";}
        }, 2);
        assertTrue(r.getText().indexOf("name") != -1);
    }

    public void testBasic2() {
        RuleViolation r = new RuleViolation(new Rule() {
            public String getName() {return "name";}
            public String getDescription()  {return "desc";}
        }, 2, "foo");
        assertTrue(r.getText().indexOf("foo") != -1);
    }
}
