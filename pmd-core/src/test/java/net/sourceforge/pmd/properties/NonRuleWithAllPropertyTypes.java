/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.inRange;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.util.ClassUtil;

/**
 * A non-functional rule containing all property types. Used for testing UIs.
 *
 * Steps required to use with Eclipse Plugin:
 *
 * update your chosen ruleset xml file to include this 'rule' compile new PMD
 * jars copy both the pmd5.0.jar and pmd-test-5.0.jar to the eclipse-plugin/lib
 * directory update the /manifest.mf file to ensure it includes the
 * pmd-test-5.0.jar
 *
 * @author Brian Remedios
 */
public class NonRuleWithAllPropertyTypes extends AbstractRule {

    // descriptors are public to enable us to write external tests
    public static final StringProperty SINGLE_STR = new StringProperty("singleStr", "String value", "hello world", 3.0f);
    public static final StringMultiProperty MULTI_STR = new StringMultiProperty("multiStr", "Multiple string values",
                                                                                new String[] {"hello", "world"}, 5.0f, '|');
    public static final PropertyDescriptor<Integer> SINGLE_INT = PropertyFactory.intProperty("singleInt").desc("Single integer value").require(inRange(1, 10)).defaultValue(8).build();
    public static final IntegerMultiProperty MULTI_INT = new IntegerMultiProperty("multiInt", "Multiple integer values",
                                                                                  0, 10, new Integer[] {1, 2, 3, 4}, 5.0f);
    public static final LongProperty SINGLE_LONG = new LongProperty("singleLong", "Single long value", 1L, 10L, 8L,
                                                                    3.0f);
    public static final LongMultiProperty MULTI_LONG = new LongMultiProperty("multiLong", "Multiple long values", 0L,
                                                                             10L, new Long[] {1L, 2L, 3L, 4L}, 5.0f);
    public static final BooleanProperty SINGLE_BOOL = new BooleanProperty("singleBool", "Single boolean value", true,
                                                                          6.0f);
    public static final BooleanMultiProperty MULTI_BOOL = new BooleanMultiProperty("multiBool",
                                                                                   "Multiple boolean values", new Boolean[] {true, false}, 5.0f);
    public static final CharacterProperty SINGLE_CHAR = new CharacterProperty("singleChar", "Single character", 'a',
                                                                              5.0f);
    public static final CharacterMultiProperty MULTI_CHAR = new CharacterMultiProperty("multiChar",
                                                                                       "Multiple characters", new Character[] {'a', 'e', 'i', 'o', 'u'}, 6.0f, '|');
    public static final FloatProperty SINGLE_FLOAT = new FloatProperty("singleFloat", "Single float value", .9f, 10f, .9f,
                                                                       5.0f);
    public static final FloatMultiProperty MULTI_FLOAT = new FloatMultiProperty("multiFloat", "Multiple float values",
                                                                                0f, 5f, new Float[] {1f, 2f, 3f}, 6.0f);
    public static final TypeProperty SINGLE_TYPE = new TypeProperty("singleType", "Single type", String.class,
                                                                    new String[] {"java.lang"}, 5.0f);
    public static final TypeMultiProperty MULTI_TYPE = new TypeMultiProperty("multiType", "Multiple types",
                                                                             Arrays.<Class>asList(Integer.class, Object.class), new String[] {"java.lang"}, 6.0f);

    private static final Map<String, Class> ENUM_MAPPINGS;

    static {
        Map<String, Class> tmp = new HashMap<>();
        tmp.put("String", String.class);
        tmp.put("Object", Object.class);
        ENUM_MAPPINGS = Collections.unmodifiableMap(tmp);
    }


    public static final EnumeratedProperty<Class> ENUM_TYPE = new EnumeratedProperty<>("enumType", "Enumerated choices", ENUM_MAPPINGS, Object.class, Class.class, 5.0f);


    public static final EnumeratedMultiProperty<Class> MULTI_ENUM_TYPE = new EnumeratedMultiProperty<>("multiEnumType", "Multiple enumerated choices", ENUM_MAPPINGS,
                                                                                                       Arrays.<Class>asList(String.class, Object.class), Class.class, 5.0f);

    private static final Method STRING_LENGTH = ClassUtil.methodFor(String.class, "length", ClassUtil.EMPTY_CLASS_ARRAY);

    public static final MethodProperty SINGLE_METHOD = new MethodProperty("singleMethod", "Single method",
                                                                          STRING_LENGTH, new String[] {"java.lang"}, 5.0f);

    private static final Method STRING_TO_LOWER_CASE = ClassUtil.methodFor(String.class, "toLowerCase",
                                                                           ClassUtil.EMPTY_CLASS_ARRAY);

    public static final MethodMultiProperty MULTI_METHOD = new MethodMultiProperty("multiMethod", "Multiple methods",
                                                                                   new Method[] {STRING_LENGTH, STRING_TO_LOWER_CASE}, new String[] {"java.lang"}, 6.0f);


    public NonRuleWithAllPropertyTypes() {
        super();
        definePropertyDescriptor(SINGLE_STR);
        definePropertyDescriptor(MULTI_STR);
        definePropertyDescriptor(SINGLE_INT);
        definePropertyDescriptor(MULTI_INT);
        definePropertyDescriptor(SINGLE_LONG);
        definePropertyDescriptor(MULTI_LONG);
        definePropertyDescriptor(SINGLE_BOOL);
        definePropertyDescriptor(MULTI_BOOL);
        definePropertyDescriptor(SINGLE_CHAR);
        definePropertyDescriptor(MULTI_CHAR);
        definePropertyDescriptor(SINGLE_FLOAT);
        definePropertyDescriptor(MULTI_FLOAT);
        definePropertyDescriptor(SINGLE_TYPE);
        definePropertyDescriptor(MULTI_TYPE);
        definePropertyDescriptor(ENUM_TYPE);
        definePropertyDescriptor(SINGLE_METHOD);
        definePropertyDescriptor(MULTI_METHOD);
        definePropertyDescriptor(MULTI_ENUM_TYPE);
    }


    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
    }
}
