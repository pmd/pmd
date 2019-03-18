/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.xpath;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;


/**
 * REMOVE ME, only for test purposes.
 */
@Deprecated
public class SemanticAmbiguityChecker {


    public static SemanticAmbiguityResult semanticCheck(Node node) {
        if (!(node instanceof ASTAmbiguousName)) {
            return null;
        }

        ASTAmbiguousName name = (ASTAmbiguousName) node;

        if (name.jjtGetParent() instanceof ASTExpression) {

            List<String> segments = name.getSegments();

            String first = segments.get(0);

            if (findVar(first, name.getScope()) || findVarInImport(first, name)) {
                return SemanticAmbiguityResult.EXPR;
            }

            if (findType(name.getTypeImage(), name.getScope())
                || findType(first, name.getScope())) {
                return SemanticAmbiguityResult.TYPE;
            }
        }

        if (name.jjtGetParent() instanceof ASTType) {

            if (findType(name.getTypeImage(), name.getScope())
                || findType(name.getSegments().get(0), name.getScope())) {
                return SemanticAmbiguityResult.TYPE;
            }
        }

        return SemanticAmbiguityResult.AMBIGUOUS;
    }

    private static boolean findVar(String varName, Scope innermost) {
        Scope currentScope = innermost;

        while (currentScope != null) {
            for (Entry<VariableNameDeclaration, List<NameOccurrence>> e : currentScope.getDeclarations(VariableNameDeclaration.class).entrySet()) {
                if (e.getKey().getName().equals(varName)) {
                    return true;
                }
            }
            currentScope = currentScope.getParent();

        }

        return false;
    }

    private static boolean findVarInImport(String varName, Node innermost) {
        Node currentScope = innermost;

        while (!(currentScope instanceof ASTCompilationUnit)) {
            currentScope = currentScope.jjtGetParent();
        }

        List<ASTImportDeclaration> imports = currentScope.findChildrenOfType(ASTImportDeclaration.class);

        int numOnDemand =
            (int) imports.stream().filter(astImportDeclaration ->
                                              astImportDeclaration.isImportOnDemand()
                                                  && astImportDeclaration.isStatic()).count();

        for (ASTImportDeclaration anImport : imports) {
            if (anImport.isStatic()) {
                if (varName.equals(anImport.getImportedSimpleName())) {
                    return true;
                }
            }
        }

        //noinspection RedundantIfStatement
        if (numOnDemand == 1) {
            return true;
        } else {
            return false;
        }

    }

    private static boolean findType(String varName, Scope innermost) {
        Scope currentScope = innermost;

        while (!(currentScope instanceof SourceFileScope)) {
            currentScope = currentScope.getParent();
        }

        Set<String> explicitImports = ((SourceFileScope) currentScope).getExplicitImports();


        if (explicitImports.stream().map(it -> it.split("\\."))
                           .filter(it -> it.length > 1)
                           .map(it -> it[it.length - 1])
                           .anyMatch(it -> it.equals(varName))) {
            return true;
        }

        if (explicitImports.contains(varName)) {
            return true;
        }


        return ((SourceFileScope) currentScope).resolveType(varName) != null;
    }


    public enum SemanticAmbiguityResult {
        EXPR, TYPE, AMBIGUOUS
    }


}
