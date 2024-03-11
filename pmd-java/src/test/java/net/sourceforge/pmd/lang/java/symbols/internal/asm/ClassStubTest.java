/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

class ClassStubTest {
    // while parsing the annotation type, ClassStub's parseLock.ensureParsed()
    // is called multiple times, reentering the parselock while the status is
    // still BEING_PARSED.
    @Test
    void loadAndParseAnnotation() {
        // class stub - annotation type
        TypeSystem typeSystem = TypeSystem.usingClassLoaderClasspath(JavaParsingHelper.class.getClassLoader());
        JClassSymbol classSymbol = typeSystem.getClassSymbol("java.lang.Deprecated");
        PSet<String> annotationAttributeNames = classSymbol.getAnnotationAttributeNames();
        assertFalse(annotationAttributeNames.isEmpty());
    }
}
