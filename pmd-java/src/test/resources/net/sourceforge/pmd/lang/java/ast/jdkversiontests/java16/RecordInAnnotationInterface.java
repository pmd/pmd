/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * A record can be declared as a member of an annotation interface.
 *
 * @see <a href="https://docs.oracle.com/javase/specs/jls/se16/html/jls-9.html#jls-9.6.1">JLS 9.6.1</a>
 */
public @interface RecordInAnnotationInterface {
    record Model(Object model, String viewName) {}

    record Empty() {}

    record Generic<T>(T value) {}

    String name() default "";
}
