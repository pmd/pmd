/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class OccurrenceFinder extends JavaParserVisitorAdapter {

    // Maybe do some sort of State pattern thingy for when NameDeclaration
    // is empty/not empty?
    private final Set<NameDeclaration> declarations = new HashSet<>();

    private final Set<NameDeclaration> additionalDeclarations = new HashSet<>();

    @Override
    public Object visit(ASTResource node, Object data) {
        // is this a concise resource reference?
        if (node.getNumChildren() == 1) {
            ASTName nameNode = (ASTName) node.getChild(0);
            for (StringTokenizer st = new StringTokenizer(nameNode.getImage(), "."); st.hasMoreTokens();) {
                JavaNameOccurrence occ = new JavaNameOccurrence(nameNode, st.nextToken());
                new Search(occ).execute();
            }
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        NameFinder nameFinder = new NameFinder(node);

        declarations.clear();
        additionalDeclarations.clear();

        List<JavaNameOccurrence> names = nameFinder.getNames();
        for (JavaNameOccurrence occ : names) {
            Search search = new Search(occ);
            if (declarations.isEmpty()) {
                // doing the first name lookup
                search.execute();
                declarations.addAll(search.getResult());
                if (declarations.isEmpty()) {
                    // we can't find it, so just give up
                    // when we decide to do full symbol resolution
                    // force this to either find a symbol or throw a
                    // SymbolNotFoundException
                    break;
                }
            } else {
                for (NameDeclaration decl : declarations) {
                    // now we've got a scope we're starting with, so work from
                    // there
                    Scope startingScope = decl.getScope();
                    // in case the previous found declaration is a class
                    // reference
                    // for a class inside the same source file
                    // we need to search this class
                    // e.g. the list of name occurrence could come from
                    // outerClassRef.member. See also bug #1302
                    if (decl instanceof VariableNameDeclaration) {
                        String typeImage = ((VariableNameDeclaration) decl).getTypeImage();
                        ClassNameDeclaration clazzDeclaration = startingScope.getEnclosingScope(SourceFileScope.class)
                                .findClassNameDeclaration(typeImage);
                        if (clazzDeclaration != null) {
                            startingScope = clazzDeclaration.getScope();
                        }
                    }
                    search.execute(startingScope);
                    Set<NameDeclaration> result = search.getResult();
                    additionalDeclarations.addAll(result);
                    if (result.isEmpty()) {
                        // nothing found
                        // This seems to be a lack of type resolution here.
                        // Theoretically we have the previous declaration node
                        // and
                        // know from there the Type of
                        // the variable. The current occurrence (occ) should
                        // then be
                        // found in the declaration of
                        // this type. The type however may or may not be known
                        // to
                        // PMD (see aux classpath).

                        // we can't find it, so just give up
                        // when we decide to do full symbol resolution
                        // force this to either find a symbol or throw a
                        // SymbolNotFoundException
                        break;
                    }
                }
                declarations.addAll(additionalDeclarations);
            }
        }
        return super.visit(node, data);
    }

}
