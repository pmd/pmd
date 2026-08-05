/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.TypeNode;

/**
 * Regression for inconsistent arity of the same unresolved type name
 * (e.g. Builder used once with 2 type args and later with 3). Analysis
 * must not crash when building the type-parameter substitution.
 */
class UnresolvedGenericArityMismatchTest extends BaseParserTest {

    @Test
    void analysisDoesNotCrashWhenUnresolvedTypeArityDiffersAcrossUsages() {
        String source = ""
            + "public class ComboBox2Test {\n"
            + "  public class ComboBoxIntStringItem extends ComboBox1<Integer, String> {\n"
            + "    public ComboBoxIntStringItem() {\n"
            + "      super(new Builder<Integer, String>() {});\n"
            + "    }\n"
            + "    public static class Item extends Item1<Integer, String> {\n"
            + "      public Item(Integer key, String displayValue) { super(key, displayValue); }\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public class ComboBoxIntStringLongItem extends ComboBox2<Integer, String, Long> {\n"
            + "    public ComboBoxIntStringLongItem() {\n"
            + "      super(new Builder<Integer, String, Long>() {});\n"
            + "    }\n"
            + "    public static class Item extends Item2<Integer, String, Long> {\n"
            + "      public Item(Integer key, String displayValue, Long displayValue2) {\n"
            + "        super(key, displayValue);\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        ASTCompilationUnit acu = assertDoesNotThrow(() -> java.parse(source));

        // Force full type resolution of every type-bearing node, including
        // getTypeParamSubst paths used while resolving constructors/superclass.
        assertDoesNotThrow(() -> acu.descendants(TypeNode.class)
                                    .forEach(n -> n.getTypeMirror().toString()));
        assertDoesNotThrow(() -> acu.descendants(ASTConstructorCall.class)
                                    .forEach(n -> {
                                        JTypeMirror tm = n.getTypeMirror();
                                        if (tm instanceof JClassType) {
                                            ((JClassType) tm).getTypeParamSubst();
                                            ((JClassType) tm).getSuperClass();
                                        }
                                    }));
    }
}
