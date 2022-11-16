/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// From https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/it/resources/com/google/checkstyle/test/chapter4formatting/rule462horizontalwhitespace/InputNoWhitespaceBeforeAnnotations.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/checks/whitespace/nowhitespaceafter/InputNoWhitespaceAfterArrayDeclarationsAndAnno.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/checks/whitespace/nowhitespaceafter/InputNoWhitespaceAfterNewTypeStructure.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/java8/InputAnnotations12.java
@Target(ElementType.TYPE_USE)
@interface NonNull {}

class AnnotedArrayType {
    @NonNull int @NonNull[] @NonNull[] field1;
    @NonNull int @NonNull [] @NonNull [] field2;
    private @NonNull int array2 @NonNull [] @NonNull [];

    public String m2()@NonNull[]@NonNull[] { return null; }
    public String@NonNull[]@NonNull[] m2a() { return null; }
    public void run() {
        for (String a@NonNull[] : m2()) {
        }
    }
    void vararg(@NonNull String @NonNull [] @NonNull ... vararg2) { }
    public void vararg2(@NonNull int @NonNull ... vararg) {}
    public void vararg3(@NonNull int[] @NonNull ... vararg) {}
}
// From https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/checks/coding/avoidnoargumentsuperconstructorcall/InputAvoidNoArgumentSuperConstructorCall.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/antlr4/InputAntlr4AstRegressionAnnotationOnQualifiedTypes.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/antlr4/InputAntlr4AstRegressionNestedTypeParametersAndArrayDeclarators.java
@Target({
    ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.PARAMETER,
    ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@interface TypeAnnotation {
}

class Rectangle2D {
    class Double{}
}
class TypeAnnotations {
    // We can use type Annotations with generic type arguments
    private Map.@TypeAnnotation Entry entry;
    // Type annotations in instanceof statements
    boolean isNonNull = "string" instanceof @TypeAnnotation String;
    // java.awt.geom.Rectangle2D
    public final Rectangle2D.@TypeAnnotation Double getRect1() {
        return new Rectangle2D.Double();
    }
    public final Rectangle2D.Double getRect2() {
        return new Rectangle2D.@TypeAnnotation Double();
    }
    public final Rectangle2D.Double getRect3() {
        Rectangle2D.@TypeAnnotation Double rect = null;
        int[][] i = new int @TypeAnnotation [1] @TypeAnnotation[];
        i = new @TypeAnnotation int [1] @TypeAnnotation[];
        return rect;
    }

    class Outer {
        class Inner {
            class Inner2 {
            }
        }
        class GInner<X> {
            class GInner2<Y, Z> {}
        }
        class Static {}
        class GStatic<X, Y> {
            class GStatic2<Z> {}
        }
    }
    class MyList<K> { }
    class Test1 {
        @TypeAnnotation Outer . @TypeAnnotation GInner<@TypeAnnotation MyList<@TypeAnnotation Object @TypeAnnotation[] @TypeAnnotation[]>>
            .@TypeAnnotation GInner2<@TypeAnnotation Integer, @TypeAnnotation Object> @TypeAnnotation[] @TypeAnnotation[] f4arrtop;
    }
}
