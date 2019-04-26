/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.typedefinition;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;

public class JavaTypeDefinitionSimpleTest {

    /**
     * Tests the raw type {@code ArrayList}.
     */
    @Test
    public void arrayListWithoutBoundGenerics() {
        JavaTypeDefinition typeDef = JavaTypeDefinition.forClass(ArrayList.class);
        Assert.assertTrue(typeDef.isGeneric());
        Assert.assertTrue(typeDef.isRawType());
        Assert.assertEquals(1, typeDef.getTypeParameterCount());

        JavaTypeDefinition genericType = typeDef.getGenericType(0);
        Assert.assertFalse(genericType.isGeneric());
        Assert.assertEquals(Object.class, genericType.getType());
    }

    /**
     * Tests the type {@code ArrayList<String>}.
     */
    @Test
    public void arrayListOfString() {
        JavaTypeDefinition typeDef = JavaTypeDefinition.forClass(ArrayList.class, JavaTypeDefinition.forClass(String.class));
        Assert.assertTrue(typeDef.isGeneric());
        Assert.assertEquals(1, typeDef.getTypeParameterCount());
        Assert.assertTrue(typeDef.isClassOrInterface());
        Assert.assertFalse(typeDef.isArrayType());

        JavaTypeDefinition genericType = typeDef.getGenericType(0);
        Assert.assertFalse(genericType.isGeneric());
        Assert.assertEquals(String.class, genericType.getType());

        JavaTypeDefinition genericTypeByName = typeDef.getGenericType("E");
        Assert.assertEquals(String.class, genericTypeByName.getType());
    }

    @Test
    public void array() {
        JavaTypeDefinition typeDef = JavaTypeDefinition.forClass(String[].class);
        Assert.assertFalse(typeDef.isGeneric());
        Assert.assertTrue(typeDef.isArrayType());
        Assert.assertFalse(typeDef.isClassOrInterface());
        Assert.assertEquals(String.class, typeDef.getElementType().getType());
        Assert.assertFalse(typeDef.isPrimitive());
    }

    @Test
    public void primitive() {
        JavaTypeDefinition typeDef = JavaTypeDefinition.forClass(int.class);
        Assert.assertTrue(typeDef.isPrimitive());
        Assert.assertFalse(typeDef.isClassOrInterface());
    }
}
