/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * See https://github.com/pmd/pmd/pull/5004: With Java 22, "_" are unnamed variables.
 * In Java 9-21 "_" is a restricted keyword and cannot be used as a variable name.
 * Before Java 9, "_" is still a valid name, but not a unnmaed variable.
 */
class UnnamedVariable {
    void method() {
        int _ = 1;
    }

    void method2(int _) { }
}
