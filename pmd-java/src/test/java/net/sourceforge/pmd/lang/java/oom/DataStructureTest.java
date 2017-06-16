/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ParserTst;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.QualifiedName;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.FieldSignature;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSigMask;
import net.sourceforge.pmd.lang.java.oom.signature.OperationSignature;

/**
 * Tests functionality of the whole data structure (PackageStats, ClassStats, OperationStats). The behaviour of the
 * structure is very encapsulated, so the API to test is restricted per class.
 *
 * @author Clément Fournier
 */
public class DataStructureTest extends ParserTst {

    private PackageStats pack;

    @Before
    public void setUp() {
        pack = new PackageStats();
    }

    @Test
    public void testAddClass() {
        QualifiedName qname = QualifiedName.parseName("org.foo.Boo");

        assertNull(pack.getClassStats(qname, false));
        assertNotNull(pack.getClassStats(qname, true));

        // now it's added, this shouldn't return null
        assertNotNull(pack.getClassStats(qname, false));
    }

    @Test
    public void testAddOperation() {
        final String TEST = "package org.foo; class Boo{ "
            + "public void foo(){}}";

        ASTMethodOrConstructorDeclaration node = getOrderedNodes(ASTMethodDeclaration.class, TEST).get(0);

        QualifiedName qname = node.getQualifiedName();
        OperationSignature signature = OperationSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, new OperationSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addOperation("foo()", signature);
        assertTrue(pack.hasMatchingSig(qname, new OperationSigMask()));
    }

    @Test
    public void testAddField() {
        final String TEST = "package org.foo; class Boo{ "
            + "public String bar;}";

        ASTFieldDeclaration node = getOrderedNodes(ASTFieldDeclaration.class, TEST).get(0);

        QualifiedName qname = QualifiedName.parseName("org.foo.Boo");
        String fieldName = "bar";
        FieldSignature signature = FieldSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, fieldName, new FieldSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addField(fieldName, signature);
        assertTrue(pack.hasMatchingSig(qname, fieldName, new FieldSigMask()));
    }
}
