/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTFieldDeclarationStatementsTest extends ApexParserTestBase {

    @Test
    void getSimpleTypeName() {
        ASTFieldDeclarationStatements fields = parse("class Foo { String field; }").descendants(ASTFieldDeclarationStatements.class).first();
        assertEquals("String", fields.getTypeName());
        assertTrue(fields.getTypeArguments().isEmpty());
    }

    @Test
    void getListTypeName() {
        ASTFieldDeclarationStatements fields = parse("class Foo { List<String> field; }").descendants(ASTFieldDeclarationStatements.class).first();
        assertEquals("List<String>", fields.getTypeName());
        assertEquals(1, fields.getTypeArguments().size());
        assertEquals("String", fields.getTypeArguments().get(0));
    }

    @Test
    void getListTypeNameComponents() {
        ASTFieldDeclarationStatements fields = parse("class Foo { my.List<my.String> field; }").descendants(ASTFieldDeclarationStatements.class).first();
        assertEquals("my.List<my.String>", fields.getTypeName());
        assertEquals(1, fields.getTypeArguments().size());
        assertEquals("my.String", fields.getTypeArguments().get(0));
    }

    @Test
    void getNestedListTypeName() {
        ASTFieldDeclarationStatements fields = parse("class Foo { List<List<String>> field; }").descendants(ASTFieldDeclarationStatements.class).first();
        assertEquals("List<List<String>>", fields.getTypeName());
        assertEquals(1, fields.getTypeArguments().size());
        assertEquals("List<String>", fields.getTypeArguments().get(0));
    }

    @Test
    void getMapTypeName() {
        ASTFieldDeclarationStatements fields = parse("class Foo { Map<String,Integer> field; }").descendants(ASTFieldDeclarationStatements.class).first();
        assertEquals("Map<String, Integer>", fields.getTypeName());
        assertEquals(2, fields.getTypeArguments().size());
        assertEquals("String", fields.getTypeArguments().get(0));
        assertEquals("Integer", fields.getTypeArguments().get(1));
    }
}
