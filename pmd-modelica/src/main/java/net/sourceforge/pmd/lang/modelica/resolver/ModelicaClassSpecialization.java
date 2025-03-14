/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Enumerates "specialized kinds of classes" (package, model, connector, etc.) that define
 * some restrictions and enhancements on what can be defined inside and how can they be used.
 *
 * See "4.6 Specialized classes" from MLS 3.4
 */
public enum ModelicaClassSpecialization {
    CLASS("class"),
    MODEL("model"),
    RECORD("record"),
    OPERATOR_RECORD("operator record"),
    BLOCK("block"),
    CONNECTOR("connector"),
    EXPANDABLE_CONNECTOR("expandable connector"),
    TYPE("type"),
    PACKAGE("package"),
    FUNCTION("function"),
    PURE_FUNCTION("pure function"),
    OPERATOR_FUNCTION("operator function"),
    PURE_OPERATOR_FUNCTION("pure operator function"),
    OPERATOR("operator");

    private String name;

    ModelicaClassSpecialization(String name) {
        this.name = name;
    }

    public static ModelicaClassSpecialization getFunctionSpecialization(boolean isPure, boolean isOperator) {
        if (isPure) {
            return isOperator ? PURE_OPERATOR_FUNCTION : PURE_FUNCTION;
        } else {
            return isOperator ? OPERATOR_FUNCTION : FUNCTION;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
