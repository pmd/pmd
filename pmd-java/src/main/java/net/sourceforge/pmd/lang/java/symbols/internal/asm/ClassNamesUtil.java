/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.apache.commons.lang3.NotImplementedException;

/**
 * When dealing with classes we have to handle a bunch of different
 * kinds of names. From higher level to lower level:
 * <ul>
 * <li>Canonical name: {@code a.b.C.D}
 * <li>Binary name: {@code a.b.C$D}
 * <li>Internal name: {@code a/b/C$D}
 * </ul>
 *
 * <p>Canonical names are on the Java language level. They are how you
 * type a reference to a class from an another arbitrary class. Some classes
 * may not even have one, eg local classes cannot be referenced from outside
 * their scope.
 *
 * <p>Binary names lift the ambiguity between inner class selection and
 * package name that exists in canonical names. They're more convenient
 * to work with when loading classes. They're typically the kind of name
 * you find when using reflective APIs.
 *
 * <p>Internal names are burned into class files are they allow getting
 * a file path to the referenced class file just by appending {@code .class}.
 * They are only useful at the level of class files, eg when using ASM.
 *
 * <p><i>Type descriptors</i> are another class of "names" that use internal names,
 * but are more general, as they can represent all kinds of types. Eg the
 * type descriptor for class {@code a.b.C.D} is {@code La/b/C$D;}, the one of
 * {@code boolean} is {@code Z}, and the one of {@code boolean[]} is {@code [Z}.
 *
 * <p><i>Type signatures</i> are a superset of type descriptors that can
 * also represent generic types. These need to be parsed when reading info
 * from a class file.
 */
public final class ClassNamesUtil {

    private ClassNamesUtil() {
        // utility class
    }


    public static String getTypeDescriptor(Class<?> klass) {
        if (klass.isPrimitive()) {
            throw new NotImplementedException("Doesn't handle primitive types");
        } else if (klass.isArray()) {
            return "[" + getTypeDescriptor(klass.getComponentType());
        } else {
            return "L" + getInternalName(klass) + ";";
        }
    }

    public static String getInternalName(Class<?> klass) {
        return klass.getName().replace('.', '/');
    }

    public static String internalToBinaryName(String internal) {
        return internal.replace('/', '.');
    }

    public static String classDescriptorToBinaryName(String descriptor) {
        return internalToBinaryName(classDescriptorToInternalName(descriptor));
    }

    public static String classDescriptorToInternalName(String descriptor) {
        return descriptor.substring(1, descriptor.length() - 1); // remove L and ;
    }

    public static String binaryToInternal(String binary) {
        return binary.replace('.', '/');
    }

}
