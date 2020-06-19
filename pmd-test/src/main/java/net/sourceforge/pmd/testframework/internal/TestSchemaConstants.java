/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.testframework.internal;

import net.sourceforge.pmd.internal.util.xml.SchemaConstant;

/**
 *
 */
public final class TestSchemaConstants {

    public static final SchemaConstant EXPECTED_MESSAGES = new SchemaConstant("expected-messages");
    public static final SchemaConstant MESSAGE = new SchemaConstant("message");
    public static final SchemaConstant EXPECTED_LINE_NUMBERS = new SchemaConstant("expected-linenumbers");
    public static final SchemaConstant RULE_PROPERTY = new SchemaConstant("rule-property");
    public static final SchemaConstant USE_AUXCLASSPATH = new SchemaConstant("useAuxClasspath");
    public static final SchemaConstant REGRESSION_TEST = new SchemaConstant("regressionTest");

    private TestSchemaConstants() {
        // utility class
    }

}
