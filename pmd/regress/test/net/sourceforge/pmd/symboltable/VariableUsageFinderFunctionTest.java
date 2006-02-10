package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.VariableUsageFinderFunction;
import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VariableUsageFinderFunctionTest extends TestCase {

    public void testLookingForUsed() {
        ASTVariableDeclaratorId variableDeclarationIdNode = new ASTVariableDeclaratorId(1);
        variableDeclarationIdNode.setImage("x");
        NameDeclaration nameDeclaration = new VariableNameDeclaration(variableDeclarationIdNode);
        List nameOccurrences = new ArrayList();
        nameOccurrences.add(new NameOccurrence(new SimpleJavaNode(2), "x"));

        Map declarations = new HashMap();
        declarations.put(nameDeclaration, nameOccurrences);

        List vars = new ArrayList();
        vars.add(nameDeclaration);

        VariableUsageFinderFunction f = new VariableUsageFinderFunction(declarations);
        Applier.apply(f, vars.iterator());
        Map p = f.getUsed();
        assertEquals(1, p.size());
    }
}
