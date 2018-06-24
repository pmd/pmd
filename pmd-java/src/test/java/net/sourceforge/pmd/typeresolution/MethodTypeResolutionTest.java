/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.typeresolution.MethodTypeResolution;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

public class MethodTypeResolutionTest {

    @Test
    public void testBoxingRules() {
        assertSame(Boolean.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(boolean.class)).getType());
        assertSame(Double.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(double.class)).getType());
        assertSame(Float.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(float.class)).getType());
        assertSame(Long.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(long.class)).getType());
        assertSame(Integer.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(int.class)).getType());
        assertSame(Character.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(char.class)).getType());
        assertSame(Short.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(short.class)).getType());
        assertSame(Byte.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(byte.class)).getType());
        assertSame(Void.class, MethodTypeResolution.boxPrimitive(JavaTypeDefinition.forClass(void.class)).getType());
    }
}
