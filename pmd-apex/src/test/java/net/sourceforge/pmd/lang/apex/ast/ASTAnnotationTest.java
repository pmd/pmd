/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;

class ASTAnnotationTest extends ApexParserTestBase {
    @Test
    void caseSensitiveName() {
        ASTUserClassOrInterface<?> parsed = parse("public with sharing class Example {\n"
                + "\n"
                + "  @istest\n"
                + "  private static void fooShouldBar() {\n"
                + "  }\n"
                + "  \n"
                + "}");
        ASTAnnotation annotation = parsed.descendants(ASTAnnotation.class).first();

        assertEquals("IsTest", annotation.getName());
        assertEquals("istest", annotation.getRawName());
        Chars text = annotation.getTextDocument().sliceOriginalText(annotation.getTextRegion());
        assertEquals("@istest", text.toString());
    }
}
