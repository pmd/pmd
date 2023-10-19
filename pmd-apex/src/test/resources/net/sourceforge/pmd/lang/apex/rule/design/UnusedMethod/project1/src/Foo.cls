/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class Foo {
    // this method is needed, otherwise the whole class is considered as unused, as it would contain
    // then only one unused method.
    private void other() {}

    public void unusedMethod() {
        // ääää // at least 4 umlauts are required in order to
        // reproduce the ArrayIndexOutOfBoundsException (#4453)
        // up to 7.0.0-rc3 with export PMD_JAVA_OPTS="-Dfile.encoding=ISO-8859-15" (e.g. platform encoding is not UTF-8)
        other();
        // note: there should be no additional characters
        // after this method, only the two closing brackets
    }
}
