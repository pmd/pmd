// From https://github.com/checkstyle/checkstyle/blob/checkstyle-9.1/src/test/resources/com/puppycrawl/tools/checkstyle/grammar/InputRegressionJavaClass2.java
class c4<A,B> {
    class c4a {}

    public c4() { <String>super(); }
}
class c5 extends c4.c4a {
    c5() { new c4().super(); }
    c5(int a) { new c4().<String>super(); }
}
