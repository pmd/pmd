/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.NameOccurrences;

public class NameOccurrencesTest extends TestCase {


    public void testNameLinkage() {
        ASTPrimaryExpression primary = new ASTPrimaryExpression(1);
        primary.testingOnly__setBeginLine(10);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        prefix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(prefix, 0);
        ASTPrimarySuffix suffix = new ASTPrimarySuffix(3);
        suffix.setImage("x");
        suffix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(suffix, 1);

        NameOccurrences occs = new NameOccurrences(primary);
        NameOccurrence thisOcc = (NameOccurrence) occs.iterator().next();
        NameOccurrence xOcc = (NameOccurrence) occs.getNames().get(1);
        assertEquals(thisOcc.getNameForWhichThisIsAQualifier(), xOcc);
    }

    // super
    public void testSuper() {
        ASTPrimaryExpression primary = new ASTPrimaryExpression(1);
        primary.testingOnly__setBeginLine(10);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesSuperModifier();
        prefix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(prefix, 0);

        NameOccurrences occs = new NameOccurrences(primary);
        assertEquals("super", ((NameOccurrence) occs.getNames().get(0)).getImage());
    }

    // this
    public void testThis() {
        ASTPrimaryExpression primary = new ASTPrimaryExpression(1);
        primary.testingOnly__setBeginLine(10);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        prefix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(prefix, 0);

        NameOccurrences occs = new NameOccurrences(primary);
        assertEquals("this", ((NameOccurrence) occs.getNames().get(0)).getImage());
    }

    // this.x
    public void testFieldWithThis() {
        ASTPrimaryExpression primary = new ASTPrimaryExpression(1);
        primary.testingOnly__setBeginLine(10);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        prefix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(prefix, 0);
        ASTPrimarySuffix suffix = new ASTPrimarySuffix(3);
        suffix.setImage("x");
        suffix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(suffix, 1);

        NameOccurrences occs = new NameOccurrences(primary);
        assertEquals("this", ((NameOccurrence) occs.getNames().get(0)).getImage());
        assertEquals("x", ((NameOccurrence) occs.getNames().get(1)).getImage());
    }

    // x
    public void testField() {
        ASTPrimaryExpression primary = new ASTPrimaryExpression(1);
        primary.testingOnly__setBeginLine(10);
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.testingOnly__setBeginLine(10);
        primary.jjtAddChild(prefix, 0);
        ASTName name = new ASTName(3);
        name.setImage("x");
        prefix.jjtAddChild(name, 0);

        NameOccurrences occs = new NameOccurrences(primary);
        assertEquals("x", ((NameOccurrence) occs.getNames().get(0)).getImage());
    }


}
