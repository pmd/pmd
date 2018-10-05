/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.testframework.PmdRuleTst;

public class AvoidThrowingRawExceptionTypesTest extends PmdRuleTst {
    public static class Throwable extends java.lang.Throwable {
        private static final long serialVersionUID = 1798165250043760600L;
    }

    public static class Exception extends java.lang.Throwable {
        private static final long serialVersionUID = -2518308549741147689L;
    }

    public static class RuntimeException extends java.lang.Throwable {
        private static final long serialVersionUID = 6341520923058239682L;
    }

    public static class Error extends java.lang.Throwable {
        private static final long serialVersionUID = -6965602141393320558L;
    }
}
