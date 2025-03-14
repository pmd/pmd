// From https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/antlr4/InputAntlr4AstRegressionSingleCommaInArrayInit.java
class AnnotationCommaArrayInit {
    @Foo({,}) void b() { }
    @interface Foo { int[] value(); }
}
