/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import static net.sourceforge.pmd.properties.constraints.NumericConstraints.positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.NumericConstants;

public class TooManyFieldsRule extends AbstractPLSQLRule {

    private static final int DEFAULT_MAXFIELDS = 15;

    private Map<String, Integer> stats;
    private Map<String, PLSQLNode> nodes;

    private static final PropertyDescriptor<Integer> MAX_FIELDS_DESCRIPTOR
            = PropertyFactory.intProperty("maxfields")
                             .desc("Max allowable fields")
                             .defaultValue(DEFAULT_MAXFIELDS)
                             .require(positive())
                             .build();

    public TooManyFieldsRule() {
        definePropertyDescriptor(MAX_FIELDS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTInput node, Object data) {

        stats = new HashMap<>(5);
        nodes = new HashMap<>(5);

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPackageSpecification node, Object data) {

        int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);

        List<ASTVariableOrConstantDeclaration> l = node.findDescendantsOfType(ASTVariableOrConstantDeclaration.class);

        for (ASTVariableOrConstantDeclaration fd : l) {
            bumpCounterFor(fd);
        }
        for (String k : stats.keySet()) {
            int val = stats.get(k);
            Node n = nodes.get(k);
            if (val > maxFields) {
                addViolation(data, n);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {

        int maxFields = getProperty(MAX_FIELDS_DESCRIPTOR);

        List<ASTVariableOrConstantDeclaration> l = node.findDescendantsOfType(ASTVariableOrConstantDeclaration.class);

        for (ASTVariableOrConstantDeclaration fd : l) {
            bumpCounterFor(fd);
        }
        for (String k : stats.keySet()) {
            int val = stats.get(k);
            Node n = nodes.get(k);
            if (val > maxFields) {
                addViolation(data, n);
            }
        }
        return data;
    }

    private void bumpCounterFor(PLSQLNode clazz) {
        String key = clazz.getImage();
        if (!stats.containsKey(key)) {
            stats.put(key, NumericConstants.ZERO);
            nodes.put(key, clazz);
        }
        Integer i = stats.get(key) + 1;
        stats.put(key, i);
    }
}
