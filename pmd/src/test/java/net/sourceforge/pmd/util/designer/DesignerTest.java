/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.designer;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;

import org.junit.Test;

/**
 * Unit tests for {@link Designer}
 */
public class DesignerTest {

    /**
     * Unit test for https://sourceforge.net/p/pmd/bugs/1168/
     */
    @Test
    public void testCopyXmlToClipboard() {
        Node compilationUnit = Designer.getCompilationUnit(LanguageVersion.JAVA_18.getLanguageVersionHandler(), "public class Foo {}");
        String xml = Designer.getXmlTreeCode(compilationUnit);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                "<CompilationUnit BeginColumn=\"1\" BeginLine=\"1\" EndColumn=\"19\" EndLine=\"1\" FindBoundary=\"false\"\n" + 
                "                 Image=\"\"\n" + 
                "                 SingleLine=\"true\"\n" + 
                "                 declarationsAreInDefaultPackage=\"true\">\n" + 
                "   <TypeDeclaration BeginColumn=\"1\" BeginLine=\"1\" EndColumn=\"19\" EndLine=\"1\" FindBoundary=\"false\"\n" + 
                "                    Image=\"\"\n" + 
                "                    SingleLine=\"true\">\n" + 
                "      <ClassOrInterfaceDeclaration Abstract=\"false\" BeginColumn=\"8\" BeginLine=\"1\" Default=\"false\" EndColumn=\"19\"\n" + 
                "                                   EndLine=\"1\"\n" + 
                "                                   Final=\"false\"\n" + 
                "                                   FindBoundary=\"false\"\n" + 
                "                                   Image=\"Foo\"\n" + 
                "                                   Interface=\"false\"\n" + 
                "                                   Modifiers=\"1\"\n" + 
                "                                   Native=\"false\"\n" + 
                "                                   Nested=\"false\"\n" + 
                "                                   PackagePrivate=\"false\"\n" + 
                "                                   Private=\"false\"\n" + 
                "                                   Protected=\"false\"\n" + 
                "                                   Public=\"true\"\n" + 
                "                                   SingleLine=\"true\"\n" + 
                "                                   Static=\"false\"\n" + 
                "                                   Strictfp=\"false\"\n" + 
                "                                   Synchronized=\"false\"\n" + 
                "                                   Transient=\"false\"\n" + 
                "                                   Volatile=\"false\">\n" + 
                "         <ClassOrInterfaceBody BeginColumn=\"18\" BeginLine=\"1\" EndColumn=\"19\" EndLine=\"1\" FindBoundary=\"false\"\n" + 
                "                               Image=\"\"\n" + 
                "                               SingleLine=\"true\"/>\n" + 
                "      </ClassOrInterfaceDeclaration>\n" + 
                "   </TypeDeclaration>\n" + 
                "</CompilationUnit>", xml);
    }
}