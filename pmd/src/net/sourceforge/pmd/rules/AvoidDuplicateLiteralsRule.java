package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;
import java.io.LineNumberReader;
import java.io.BufferedReader;

public class AvoidDuplicateLiteralsRule extends AbstractRule {

    private Map literals = new HashMap();
    private Set exceptions = new HashSet();

    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();

        if (super.hasProperty("exceptionsFile")) {
            exceptions = new HashSet();
            try {
                LineNumberReader reader = new LineNumberReader(new BufferedReader(new FileReader(new File(getStringProperty("exceptions")))));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    exceptions.add(line);
                }
                reader.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        super.visit(node, data);

        int threshold = getIntProperty("threshold");
        for (Iterator i = literals.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            List occurrences = (List) literals.get(key);
            if (occurrences.size() >= threshold) {
                Object[] args = new Object[]{new Integer(occurrences.size()), new Integer(((SimpleNode) occurrences.get(0)).getBeginLine())};
                String msg = MessageFormat.format(getMessage(), args);
                RuleContext ctx = (RuleContext) data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, ((SimpleNode) occurrences.get(0)).getBeginLine(), msg));
            }
        }
        return data;
    }

    public Object visit(ASTLiteral node, Object data) {
        if (!hasAtLeastSixParents(node)) {
            return data;
        }

        if (!(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTArgumentList)) {
            return data;
        }

        // just catching strings for now
        if (node.getImage() == null || node.getImage().indexOf('\"') == -1 || node.getImage().length() < 3) {
            return data;
        }

        // skip any exceptions
        if (exceptions.contains(node.getImage().substring(1, node.getImage().length()-1))) {
            return data;
        }

        if (literals.containsKey(node.getImage())) {
            List occurrences = (List) literals.get(node.getImage());
            occurrences.add(node);
        } else {
            List occurrences = new ArrayList();
            occurrences.add(node);
            literals.put(node.getImage(), occurrences);
        }

        return data;
    }

    private boolean hasAtLeastSixParents(Node node) {
        Node currentNode = node;
        for (int i = 0; i < 6; i++) {
            if (currentNode instanceof ASTCompilationUnit) {
                return false;
            }
            currentNode = currentNode.jjtGetParent();
        }
        return true;
    }
}

