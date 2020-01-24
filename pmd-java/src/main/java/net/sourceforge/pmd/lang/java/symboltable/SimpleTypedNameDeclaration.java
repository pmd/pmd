/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Just stores a type image and a actual type. And makes it easy to compare
 * these.
 */
public class SimpleTypedNameDeclaration implements TypedNameDeclaration {

    private final String typeImage;
    private final Class<?> type;
    private SimpleTypedNameDeclaration next;

    private static Set<String> primitiveTypes = new HashSet<>();

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

    /**
     * Creates a new {@link SimpleTypedNameDeclaration} with the given type
     *
     * @param typeImage
     *            the type image
     * @param type
     *            the actual type
     */
    public SimpleTypedNameDeclaration(String typeImage, Class<?> type) {
        this.typeImage = typeImage;
        this.type = type;
    }

    public SimpleTypedNameDeclaration(String typeImage, Class<?> type, SimpleTypedNameDeclaration next) {
        this.typeImage = typeImage;
        this.type = type;
        this.next = next;
    }

    public void addNext(SimpleTypedNameDeclaration next) {
        if (next == null) {
            return;
        }

        if (this.next == null) {
            this.next = next;
        } else {
            this.next.addNext(next);
        }
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
        String nextString = next != null ? "(next: " + next + ")" : "";
        return "SimpleType:" + type + "/" + typeImage + nextString;
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
     * Additionally - two {@link SimpleTypedNameDeclaration} are equal, if they
     * contain types, that can be cast into each other.
     * </p>
     */
    @Override
    public boolean equals(Object obj) {
        return internalEquals(obj) || internalEqualsNext(obj);
    }

    private boolean internalEqualsNext(Object obj) {
        if (next != null) {
            return next.equals(obj);
        }
        if (obj instanceof SimpleTypedNameDeclaration) {
            SimpleTypedNameDeclaration otherNext = ((SimpleTypedNameDeclaration) obj).next;
            if (otherNext != null) {
                return otherNext.equals(this);
            }
        }
        return false;
    }

    private boolean internalEquals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SimpleTypedNameDeclaration other = (SimpleTypedNameDeclaration) obj;
        if (type == null) {
            if (other.type == Object.class) {
                return true;
            }
            if (other.type != null) {
                return false;
            }
        }
        if (type != null && (type.equals(other.type) || type == Object.class)) {
            return true;
        }

        // if the type is given, only compare the type and don't care about the
        // type image
        if (type != null && other.type != null
                && (type.isAssignableFrom(other.type) || other.type.isAssignableFrom(type))) {
            return true;
        }

        if (typeImage == null) {
            if (other.typeImage != null) {
                return false;
            }
        } else if (!typeImage.equals(other.typeImage)) {
            // consider auto-boxing
            if (other.typeImage != null) {
                String lcType = typeImage.toLowerCase(Locale.ROOT);
                String otherLcType = other.typeImage.toLowerCase(Locale.ROOT);
                if (primitiveTypes.contains(lcType) && primitiveTypes.contains(otherLcType)) {
                    if (lcType.equals(otherLcType)) {
                        return true;
                    } else if (("char".equals(lcType) || "character".equals(lcType))
                            && ("char".equals(otherLcType) || "character".equals(otherLcType))) {
                        return true;
                    } else if (("int".equals(lcType) || "integer".equals(lcType))
                            && ("int".equals(otherLcType) || "integer".equals(otherLcType)
                                    || "short".equals(otherLcType) || "char".equals(otherLcType)
                                    || "character".equals(otherLcType) || "byte".equals(otherLcType))) {
                        return true;
                    } else if ("double".equals(lcType) && ("float".equals(otherLcType) || "int".equals(otherLcType)
                            || "integer".equals(otherLcType) || "long".equals(otherLcType))) {
                        return true;
                    } else if ("float".equals(lcType) && ("int".equals(otherLcType) || "integer".equals(otherLcType)
                            || "long".equals(otherLcType))) {
                        return true;
                    } else if ("long".equals(lcType) && ("int".equals(otherLcType) || "integer".equals(otherLcType)
                            || "char".equals(otherLcType) || "character".equals(otherLcType))) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
}
