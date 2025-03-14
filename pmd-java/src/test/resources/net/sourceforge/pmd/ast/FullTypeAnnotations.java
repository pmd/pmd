/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.io.File;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Type annotation syntax.
 *
 * See https://checkerframework.org/jsr308/java-annotation-design.html
 * for precise spec including BNF.
 *
 * https://checkerframework.org/jsr308/java-annotation-design.html#type-names
 * is particularly interesting
 */
public class FullTypeAnnotations {

    private String myString;

    //  A type annotation appears before the type’s simple name, as in @NonNull String or java.lang.@NonNull String.
    //  Here are examples:

    // for generic typearguments to parameterized classes:

    Map<@NonNull String, @NonEmpty List<@Readonly Document>> files;

    // for generic type arguments in a generic method or constructor invocation:

    {
        o.<@NonNull String>m("...");
    }

    // for type parameterbounds, including wildcard bounds:

    class Folder<F extends @Existing File> {
    }

    Collection<? super @Existing File> field;

    // for class inheritance:

    class UnmodifiableList<T> implements @Readonly List<@Readonly T> {
    }

    // for throws clauses:

    void monitorTemperature() throws @Critical TemperatureException { }

    //  for constructor invocationresults(that is, for object creation):

    {
        new @Interned MyObject();
        new @NonEmpty @Readonly List<String>(myNonEmptyStringSet);
        myVar.new @Tainted NestedClass();

        // For generic constructors(JLS §8.8.4),the annotation follows the explicit type arguments (JLS §15.9):

        new <String>@Interned MyObject();
    }


    //    for nested types:

    Map.@NonNull Entry mapField;

    //    for casts:
    {
        myString = (@NonNull String) myObject;
        x = (@A Type1 & @B Type2) y;
        // It is not permitted to omit the Java type, as in
        // myString = (@NonNull) myObject;.
    }

    // for type tests:

    boolean isNonNull = myString instanceof @NonNull String;
    //      It is not permitted to omit the Java type, as in myString instanceof @NonNull.

    // for method and constructor references, including their receiver,
    // receiver type arguments, and type arguments to the method or
    // constructor itself:

    {
        Supplier<@Vernal Date> sup = @Vernal Date::getDay;
        sup = List<@English String>::size;
        sup = Arrays::<@NonNegative Integer>sort;

    }

    // The annotation on a given array level prefixes the brackets that
    // introduce that level. To declare a non-empty array of English-language
    // strings, write @English String @NonEmpty []. The varargs syntax “...”
    // is treated analogously to array brackets and may also be prefixed by
    // an annotation. Here are examples:

    @Readonly Document[][] docs1 = new @Readonly Document[2][12]; // array of arrays of read-only documents
    Document @Readonly [][] docs2 = new Document@Readonly[2][12]; // read-only array of arrays of documents
    Document[] @Readonly [] docs3 = new Document[2]@Readonly[12]; // array of read-only arrays of documents

    Document[] docs4@Readonly[] = new Document@Readonly[2][12]; // read-only array of arrays of documents
    Document @Readonly [] docs5[] = new Document[2]@Readonly[12]; // array of read-only arrays of documents

    { // all of the above for local vars

        @Readonly Document[][] docs1 = new @Readonly Document[2][12]; // array of arrays of read-only documents
        Document @Readonly [][] docs2 = new Document@Readonly[2][12]; // read-only array of arrays of documents
        Document[] @Readonly [] docs3 = new Document[2]@Readonly[12]; // array of read-only arrays of documents

        Document[] docs4@Readonly[] = new Document@Readonly[2][12]; // read-only array of arrays of documents
        Document @Readonly [] docs5[] = new Document[2]@Readonly[12]; // array of read-only arrays of documents

    }


    class MyClass {

        public String toString(@Readonly MyClass this) { }

        public boolean equals(Object @Readonly ... other) @K[][]{ }
        MyClass(Object @Readonly [] @ß... other) { }
    }


}
