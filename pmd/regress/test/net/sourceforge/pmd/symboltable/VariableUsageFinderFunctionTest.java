package test.net.sourceforge.pmd.symboltable;

import junit.framework.TestCase;
import net.sourceforge.pmd.symboltable.VariableUsageFinderFunction;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Applier;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class VariableUsageFinderFunctionTest extends TestCase {

    public void testLookingForUsed() {
        Map decls = new HashMap();
        ASTVariableDeclaratorId id = new ASTVariableDeclaratorId(1);
        id.setImage("x");
        NameDeclaration decl = new VariableNameDeclaration(id);
        List occs = new ArrayList();
        occs.add(new NameOccurrence(new SimpleNode(2), "x"));
        decls.put(decl, occs);
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(decls, true);
        List vars = new ArrayList();
        vars.add(decl);
        Applier.apply(f, vars.iterator());
        Map p = f.getUsed();
        System.out.println("p = " + p.size());
    }
}
