/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;


import static net.sourceforge.pmd.properties.ValueParserConstants.METHOD_PARSER;

import java.lang.reflect.Method;
import java.util.Collections;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorBuilderConversionWrapper;
import net.sourceforge.pmd.properties.builders.SinglePackagedPropertyBuilder;
import net.sourceforge.pmd.properties.modules.MethodPropertyModule;


/**
 * Defines a property type that can specify a single method to use as part of a rule.
 *
 * <p>Rule developers can limit the rules to those within designated packages per the 'legalPackages' argument in the
 * constructor which can be an array of partial package names, i.e., ["java.lang", "com.mycompany" ].</p>
 *
 * @deprecated Not useful, will be remove by 7.0.0
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 * @deprecated Will be removed with 7.0.0 with no scheduled replacement
 */
@Deprecated
public final class MethodProperty extends AbstractPackagedProperty<Method> {


    /**
     * Constructor for MethodProperty.
     *
     * @param theName           Name of the property
     * @param theDescription    Description
     * @param theDefault        Default value
     * @param legalPackageNames Legal packages
     * @param theUIOrder        UI order
     */
    public MethodProperty(String theName, String theDescription, Method theDefault, String[] legalPackageNames,
                          float theUIOrder) {
        this(theName, theDescription, theDefault, legalPackageNames, theUIOrder, false);
    }


    /** Master constructor. */
    private MethodProperty(String theName, String theDescription, Method theDefault, String[] legalPackageNames,
                           float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally,
            new MethodPropertyModule(legalPackageNames, Collections.singletonList(theDefault)));
    }


    /**
     * Constructor for MethodProperty using a string as a default value.
     *
     * @param theName           Name of the property
     * @param theDescription    Description
     * @param defaultMethodStr  Default value, that will be parsed into a Method object
     * @param legalPackageNames Legal packages
     * @param theUIOrder        UI order
     *
     * @deprecated will be removed in 7.0.0
     */
    public MethodProperty(String theName, String theDescription, String defaultMethodStr, String[] legalPackageNames,
                          float theUIOrder) {
        this(theName, theDescription, METHOD_PARSER.valueOf(defaultMethodStr),
            legalPackageNames, theUIOrder, false);
    }


    @Override
    protected String asString(Method value) {
        return MethodPropertyModule.asString(value);
    }


    @Override
    public Class<Method> type() {
        return Method.class;
    }


    @Override
    public Method createFrom(String valueString) throws IllegalArgumentException {
        return METHOD_PARSER.valueOf(valueString);
    }


    static PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged<Method, MethodPBuilder> extractor() {
        return new PropertyDescriptorBuilderConversionWrapper.SingleValue.Packaged<Method, MethodPBuilder>(Method.class, ValueParserConstants.METHOD_PARSER) {
            @Override
            protected MethodPBuilder newBuilder(String name) {
                return new MethodPBuilder(name);
            }
        };
    }


    public static MethodPBuilder named(String name) {
        return new MethodPBuilder(name);
    }


    public static final class MethodPBuilder extends SinglePackagedPropertyBuilder<Method, MethodPBuilder> {
        private MethodPBuilder(String name) {
            super(name);
        }


        @Override
        public MethodProperty build() {
            return new MethodProperty(name, description, defaultValue, legalPackageNames, uiOrder, isDefinedInXML);
        }
    }


}
