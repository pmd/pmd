/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashSet;
import java.util.Set;

/**
 * Just stores a type image and a actual type.
 * And makes it easy to compare these.
 */
public class SimpleTypedNameDeclaration implements TypedNameDeclaration {

    final private String typeImage;
    final private Class<?> type;

    /**
     * Creates a new {@link SimpleTypedNameDeclaration} with the given type
     * @param typeImage the type image
     * @param type the actual type
     */
    public SimpleTypedNameDeclaration(String typeImage, Class<?> type) {
        this.typeImage = typeImage;
        this.type = type;
    }

    @Override
    public String getTypeImage() {
        return typeImage;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SimpleType:" + type + "/" + typeImage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((typeImage == null) ? 0 : typeImage.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Additionally - two {@link SimpleTypedNameDeclaration} are equal, if
     * they contain types, that can be cast into each other.
     * </p>
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SimpleTypedNameDeclaration other = (SimpleTypedNameDeclaration) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        }
        if (type != null && type.equals(other.type))
            return true;

        // if the type is given, only compare the type and don't care about the type image
        if (type != null && other.type != null && (type.isAssignableFrom(other.type) || other.type.isAssignableFrom(type)))
            return true;

        if (typeImage == null) {
            if (other.typeImage != null)
                return false;
        } else if (!typeImage.equals(other.typeImage)) {
            // consider auto-boxing
            if (other.typeImage != null) {
                String lcType = typeImage.toLowerCase();
                String otherLcType = other.typeImage.toLowerCase();
                if (primitiveTypes.contains(lcType) && primitiveTypes.contains(otherLcType)) {
                    if (lcType.equals(otherLcType)) {
                        return true;
                    } else if ((lcType.equals("char") || lcType.equals("character"))
                            && (otherLcType.equals("char") || otherLcType.equals("character"))) {
                        return true;
                    } else if ((lcType.equals("int") || lcType.equals("integer")) && (
                            otherLcType.equals("int")
                            || otherLcType.equals("integer")
                            || otherLcType.equals("short")
                            || otherLcType.equals("char")
                            || otherLcType.equals("character")
                            || otherLcType.equals("byte")
                            )) {
                        return true;
                    } else if (lcType.equals("double") && (
                            otherLcType.equals("float")
                            || otherLcType.equals("int")
                            || otherLcType.equals("integer")
                            || otherLcType.equals("long")
                            )) {
                        return true;
                    } else if (lcType.equals("float") && (
                            otherLcType.equals("int")
                            || otherLcType.equals("integer")
                            || otherLcType.equals("long")
                            )) {
                        return true;
                    } else if (lcType.equals("long") && (
                            otherLcType.equals("int")
                            || otherLcType.equals("integer")
                            || otherLcType.equals("char")
                            || otherLcType.equals("character")
                            )) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private static Set<String> primitiveTypes = new HashSet<String>();
    static {
        primitiveTypes.add("float");
        primitiveTypes.add("double");
        primitiveTypes.add("int");
        primitiveTypes.add("integer");
        primitiveTypes.add("long");
        primitiveTypes.add("byte");
        primitiveTypes.add("short");
        primitiveTypes.add("boolean");
        primitiveTypes.add("char");
        primitiveTypes.add("character");
    }
}
