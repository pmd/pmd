package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.symboltable.Applier;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableUsageFinderFunction;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

import org.junit.Test;
public class VariableUsageFinderFunctionTest {

    @Test
    public void testLookingForUsed() {
        ASTVariableDeclaratorId variableDeclarationIdNode = new ASTVariableDeclaratorId(1);
        variableDeclarationIdNode.setImage("x");
        VariableNameDeclaration nameDeclaration = new VariableNameDeclaration(variableDeclarationIdNode);
        List<NameOccurrence> nameOccurrences = new ArrayList<NameOccurrence>();
        nameOccurrences.add(new JavaNameOccurrence(new DummyJavaNode(2), "x"));

        Map<NameDeclaration, List<NameOccurrence>> declarations = new HashMap<NameDeclaration, List<NameOccurrence>>();
        declarations.put(nameDeclaration, nameOccurrences);

        List<NameDeclaration> vars = new ArrayList<NameDeclaration>();
        vars.add(nameDeclaration);

        VariableUsageFinderFunction f = new VariableUsageFinderFunction(declarations);
        Applier.apply(f, vars.iterator());
        Map<NameDeclaration, List<NameOccurrence>> p = f.getUsed();
        assertEquals(1, p.size());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VariableUsageFinderFunctionTest.class);
    }
}
