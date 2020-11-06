/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSignature;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.qname.QualifiedNameFactory;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

/**
 * Tests functionality of PackageStats
 *
 * @author Clément Fournier
 */
public class PackageStatsTest extends BaseNonParserTest {

    private PackageStats pack;


    @Before
    public void setUp() {
        pack = new PackageStats();
    }


    @Test
    public void testAddClass() {
        JavaTypeQualifiedName qname = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("org.foo.Boo");

        assertNull(pack.getClassStats(qname, false));
        assertNotNull(pack.getClassStats(qname, true));

        // now it's added, this shouldn't return null
        assertNotNull(pack.getClassStats(qname, false));
    }


    @Test
    public void testAddOperation() {
        final String TEST = "package org.foo; class Boo{ "
            + "public void foo(){}}";

        ASTMethodOrConstructorDeclaration node = java.getNodes(ASTMethodDeclaration.class, TEST).get(0);

        JavaOperationQualifiedName qname = node.getQualifiedName();
        JavaOperationSignature signature = JavaOperationSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, new JavaOperationSigMask()));

        ClassStats clazz = pack.getClassStats(qname.getClassName(), true);
        clazz.addOperation("foo()", signature);
        assertTrue(pack.hasMatchingSig(qname, new JavaOperationSigMask()));
    }


    @Test
    public void testAddField() {
        final String TEST = "package org.foo; class Boo{ "
            + "public String bar;}";

        ASTFieldDeclaration node = java.getNodes(ASTFieldDeclaration.class, TEST).get(0);

        JavaTypeQualifiedName qname = (JavaTypeQualifiedName) QualifiedNameFactory.ofString("org.foo.Boo");
        String fieldName = "bar";
        JavaFieldSignature signature = JavaFieldSignature.buildFor(node);

        assertFalse(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));

        ClassStats clazz = pack.getClassStats(qname, true);
        clazz.addField(fieldName, signature);
        assertTrue(pack.hasMatchingSig(qname, fieldName, new JavaFieldSigMask()));
    }


}
