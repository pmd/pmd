/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.util.StringUtil;

/**
 * @deprecated This is just a toy rule that counts the proportion of resolved types in a codebase,
 *     not meant as a real rule
 */
@Deprecated
@SuppressWarnings("PMD")
public class TypeResTestRule extends AbstractJavaRule {

    public static final ThreadLocal<String> FILENAME =
        ThreadLocal.withInitial(() -> "/*unknown*/");

    private static class State {

        public int fileId = 0;
        public long numResolved = 0;
        public long numerrors = 0;
        public long numUnresolved = 0;

        void print() {
            System.out.println();
            System.out.println(fileId);
            System.out.println("Resolved: " + numResolved + ", unresolved " + numUnresolved);
            double rate = numResolved / (double) (numUnresolved + numResolved);
            System.out.println("Resolved " + Math.floor(1000000 * rate) / 10000 + "%");
            System.out.println("Errors\t" + numerrors);
        }

        int absorb(State other) {
            fileId += other.fileId;
            numResolved += other.numResolved;
            numUnresolved += other.numUnresolved;
            numerrors += other.numerrors;

            return fileId;
        }

    }

    private static final State STATIC = new State();

    private State state = new State();

    private static final boolean IS_SINGLE_FILE;


    static {
        IS_SINGLE_FILE = "true".equalsIgnoreCase(System.getProperties().getOrDefault("PRINT_ALL_UNRESOLVED", "").toString());
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        FILENAME.set(((RuleContext) data).getSourceCodeFile().toString());
        for (JavaNode descendant : node.descendants()) {
            visit(descendant, data);
        }
        return data;
    }

    @Override
    public Object visit(JavaNode node, Object data) {

        if (node instanceof TypeNode) {
            try {
                if (isUnresolved(((TypeNode) node).getTypeMirror())) {
                    if (true) {
                        System.err.println("Unresolved at " + position(node, data) + "\t"
                                               + StringUtil.escapeJava(StringUtils.truncate(node.toString(), 100)));
                    }
                    state.numUnresolved++;
                } else {
                    state.numResolved++;
                }
            } catch (Throwable e) {
                System.err.println(position(node, data));
                e.printStackTrace();
                state.numerrors++;
                if (e instanceof Error) {
                    // throw e;
                }
            }
        }

        return data;
    }


    private static boolean isUnresolved(@NonNull JTypeMirror s) {
        TypeSystem ts = s.getTypeSystem();
        return s == ts.UNRESOLVED_TYPE || s == ts.ERROR_TYPE || s.getSymbol() != null && s.getSymbol().isUnresolved();
    }

    @NonNull
    public String position(JavaNode node, Object data) {
        return "In: " + ((RuleContext) data).getSourceCodeFile() + ":" + node.getBeginLine() + ":" + node.getBeginColumn();
    }

    @Override
    public void end(RuleContext ctx) {
        super.end(ctx);
        state.fileId++;
        if (state.fileId % 200 == 0) {
            int fid;
            synchronized (STATIC) {
                fid = STATIC.absorb(state);
            }
            state = new State();

            if (fid % 400 == 0) {
                synchronized (STATIC) {
                    if (STATIC.fileId % 400 == 0) {
                        STATIC.print();
                    }
                }
            }
        }
    }
}
