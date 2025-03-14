
// From https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/checks/whitespace/genericwhitespace/InputGenericWhitespaceDefault.java
// and https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/antlr4/InputAntlr4AstRegressionUncommon4.java
class GenericConstructor {
    Object ok = new <String>Object();
    Object okWithPackage = new <String>java.lang.Object();
    Object ok2 = new <String>Outer.Inner();
    Object o3 = new <String>Outer().new <String>NonStatic();
    Object o4 = new <String>GenericOuter<String>();
    Object o5 = new <String>GenericOuter<String>().new <String>GenericInner<String>();
}
class Outer {
    static class Inner {}
    class NonStatic {}
}
class GenericOuter<T> {
    class GenericInner<U> { }
}

