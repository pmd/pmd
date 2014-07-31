/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

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
        } else if (!typeImage.equals(other.typeImage))
            return false;
        return true;
    }
}
