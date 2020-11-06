/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

/**
 * Built-in Modelica types. There are only four <b>basic</b> variants: <code>Boolean</code>, <code>Integer</code>,
 * <code>Real</code> and <code>String</code> but they can have modifications applied (such as min/max values),
 * so do not introduce them as singletones for extendability in the future.
 */
public class ModelicaBuiltinType implements ModelicaType {
    public enum BaseType {
        BOOLEAN("Boolean"),
        INTEGER("Integer"),
        REAL("Real"),
        STRING("String");

        private final String name;

        BaseType(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final BaseType baseType;

    ModelicaBuiltinType(BaseType tpe) {
        baseType = tpe;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    @Override
    public String getSimpleTypeName() {
        return baseType.toString();
    }

    @Override
    public String getFullTypeName() {
        return baseType.toString();
    }

    @Override
    public String getDescriptiveName() {
        return getFullTypeName();
    }
}
