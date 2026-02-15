/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnnecessaryInterfaceDeclarationRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> ALLOWED_INTERFACES
        = PropertyFactory.stringListProperty("allowedInterfaces")
        .defaultValues("java.io.Serializable")
        .desc("Interfaces that are allowed to be declared explicitly.")
        .build();

    public UnnecessaryInterfaceDeclarationRule() {
        super(ASTClassDeclaration.class);
        definePropertyDescriptor(ALLOWED_INTERFACES);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object context) {
        Map<JTypeMirror, JavaNode> directSupertypes = new HashMap<>();
        ASTList<?> ext = node.children(ASTExtendsList.class).first();
        ASTList<?> impl = node.children(ASTImplementsList.class).first();
        Stream.of(ext, impl).filter(Objects::nonNull).forEach(list -> {
            for (ASTClassType supertypeNode : list.children(ASTClassType.class)) {
                JTypeMirror supertype = supertypeNode.getTypeMirror();
                for (Map.Entry<JTypeMirror, JavaNode> supertype1 : directSupertypes.entrySet()) {
                    checkRelated(supertypeNode, supertype, supertype1.getKey(), context);
                    checkRelated(supertype1.getValue(), supertype1.getKey(), supertype, context);
                }
                directSupertypes.put(supertype, supertypeNode);
            }
        });
        return null;
    }

    private void checkRelated(JavaNode supertypeNode, JTypeMirror supertype, JTypeMirror supertype1,
                              Object context) {
        List<String> allowed = getProperty(ALLOWED_INTERFACES);
        if (allowed.contains(supertype.toString())) {
            return;
        }
        if (TypeTestUtil.isA(supertype, supertype1)) {
            asCtx(context).addViolation(supertypeNode, supertype, supertype1);
        }
    }
}
