/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;


public class Statics extends StaticsSuper {

    public static final int PUBLIC_FIELD = 0;
    public final int publicField = 0;
    protected static final int PROTECTED_FIELD = 0;
    static final int PACKAGE_FIELD = 0;
    private static final int PRIVATE_FIELD = 0;


    public void publicInstanceMethod() {

    }


    private static void privateMethod() {
    }


    protected static void protectedMethod() {

    }


    static void packageMethod() {

    }


    public static void publicMethod() {

    }

    public static void publicMethod(int i) {

    }


    public static void publicMethod2() {

    }


    static class PackageStatic {

    }

    public static class PublicStatic {

    }

    protected static class ProtectedStatic {

    }

    private static class PrivateStatic {

    }


    public static class PublicShadowed {

    }

    public static class SomeClassA {

    }

    class PackageInner {

    }

    public class PublicInner {

    }

}
