/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
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
