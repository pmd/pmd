/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint.TypePrettyPrinter;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
import net.sourceforge.pmd.util.StringUtil;

/**
 * A strategy to log the execution traces of {@link Infer}.
 */
@SuppressWarnings("PMD.UncommentedEmptyMethodBody")
public interface TypeInferenceLogger {

    // computeCompileTimeDecl


    default void polyResolutionFailure(JavaNode node) { }

    default void noApplicableCandidates(MethodCallSite site) { }

    default void noCompileTimeDeclaration(MethodCallSite site) { }

    default void startInference(JMethodSig sig, MethodCallSite site, MethodResolutionPhase phase) { }

    default void endInference(@Nullable JMethodSig result) { }

    default void fallbackInvocation(JMethodSig ctdecl, MethodCallSite site) { }

    default void skipInstantiation(JMethodSig partiallyInferred, MethodCallSite site) { }

    default void ambiguityError(MethodCallSite site, @Nullable MethodCtDecl selected, List<MethodCtDecl> m1) { }

    // instantiateImpl


    default void ctxInitialization(InferenceContext ctx, JMethodSig sig) { }

    default void startArgsChecks() { }

    default void startArg(int i, ExprMirror expr, JTypeMirror formal) { }

    default void skipArgAsNonPertinent(int i, ExprMirror expr) { }

    default void functionalExprNeedsInvocationCtx(JTypeMirror targetT, ExprMirror expr) { }

    default void endArg() { }

    default void endArgsChecks() { }

    default void startReturnChecks() { }

    default void endReturnChecks() { }

    default void propagateAndAbort(InferenceContext context, InferenceContext parent) { }

    // ivar events


    default void boundAdded(InferenceContext ctx, InferenceVar var, BoundKind kind, JTypeMirror bound, boolean isSubstitution) { }

    default void ivarMerged(InferenceContext ctx, InferenceVar var, InferenceVar delegate) { }

    default void ivarInstantiated(InferenceContext ctx, InferenceVar var, JTypeMirror inst) { }


    /**
     * Log that the instantiation of the method type m for the given
     * call site failed. The exception provides a detail message.
     * Such an event is perfectly normal and may happen repeatedly
     * when performing overload resolution.
     *
     * <p>Exceptions occuring in an {@link MethodResolutionPhase#isInvocation() invocation phase}
     * are compile-time errors though.
     *
     * @param exception Failure record
     */
    default void logResolutionFail(ResolutionFailure exception) { }

    default boolean isNoop() {
        return false;
    }

    /**
     * Return an instance for concurrent use in another thread.
     * If this is Noop, then return the same instance because it's
     * thread-safe.
     */
    TypeInferenceLogger newInstance();

    static TypeInferenceLogger noop() {
        return SimpleLogger.NOOP;
    }


    class SimpleLogger implements TypeInferenceLogger {

        static final TypeInferenceLogger NOOP = new TypeInferenceLogger() {
            @Override
            public boolean isNoop() {
                return true;
            }

            @Override
            public TypeInferenceLogger newInstance() {
                return this;
            }
        };


        protected final PrintStream out;
        protected static final int LEVEL_INCREMENT = 4;
        private int level;
        private String indent;

        protected static final String ANSI_RESET = "\u001B[0m";
        protected static final String ANSI_BLUE = "\u001B[34m";
        protected static final String ANSI_PURPLE = "\u001B[35m";
        protected static final String ANSI_GRAY = "\u001B[37m";
        protected static final String ANSI_RED = "\u001B[31m";
        protected static final String ANSI_YELLOW = "\u001B[33m";

        private static final String TO_BLUE =
            Matcher.quoteReplacement(ANSI_BLUE) + "$0" + Matcher.quoteReplacement(ANSI_RESET);

        private static final String TO_WHITE =
            Matcher.quoteReplacement(ANSI_GRAY) + "$0" + Matcher.quoteReplacement(ANSI_RESET);

        private static final Pattern IVAR_PATTERN = Pattern.compile("['^][α-ωa-z]\\d*");
        private static final Pattern IDENT_PATTERN = Pattern.compile("\\b(?<!['^])(?!extends|super|capture|of|)[\\w]++(?!\\.)<?|-?>++");

        protected String color(Object str, String color) {
            return SystemUtils.IS_OS_UNIX ? color + str + ANSI_RESET : str.toString();
        }

        protected static String colorIvars(Object str) {
            return doColor(str, IVAR_PATTERN, TO_BLUE);
        }

        protected static String colorPunct(Object str) {
            return doColor(str, IDENT_PATTERN, TO_WHITE);
        }

        protected static String doColor(Object str, Pattern pattern, String replacement) {
            if (SystemUtils.IS_OS_UNIX) {
                return pattern.matcher(str.toString()).replaceAll(replacement);
            }
            return str.toString();
        }

        public SimpleLogger(PrintStream out) {
            this.out = out;
            updateLevel(0);
        }

        protected int getLevel() {
            return level;
        }

        protected void updateLevel(int increment) {
            level += increment;
            indent = StringUtils.repeat(' ', level);
        }

        protected void println(String str) {
            out.print(indent);
            out.println(str);
        }


        protected void endSection(String footer) {
            updateLevel(-LEVEL_INCREMENT);
            println(footer);
        }

        protected void startSection(String header) {
            println(header);
            updateLevel(+LEVEL_INCREMENT);
        }

        @Override
        public void logResolutionFail(ResolutionFailure exception) {
            if (exception.getCallSite() instanceof MethodCallSite && exception != ResolutionFailure.UNKNOWN) { // NOPMD CompareObjectsWithEquals
                ((MethodCallSite) exception.getCallSite()).acceptFailure(exception);
            }
        }

        @Override
        public void noApplicableCandidates(MethodCallSite site) {
            if (!site.isLogEnabled()) {
                return;
            }
            @Nullable JTypeMirror receiver = site.getExpr().getErasedReceiverType();
            if (receiver != null) {
                JTypeDeclSymbol symbol = receiver.getSymbol();
                if (symbol == null || symbol.isUnresolved()) {
                    return;
                }
            }

            if (site.getExpr() instanceof CtorInvocationMirror) {
                startSection("[WARNING] No potentially applicable constructors in "
                            + ((CtorInvocationMirror) site.getExpr()).getNewType());
            } else {
                startSection("[WARNING] No potentially applicable methods in " + receiver);
            }
            printExpr(site.getExpr());

            Iterator<JMethodSig> iter = site.getExpr().getAccessibleCandidates().iterator();
            if (iter.hasNext()) {
                startSection("Accessible signatures:");
                iter.forEachRemaining(it -> println(ppMethod(it)));
                endSection("");
            } else {
                println("No accessible signatures");
            }
            endSection("");
        }

        @Override
        public void noCompileTimeDeclaration(MethodCallSite site) {
            if (!site.isLogEnabled()) {
                return;
            }
            startSection("[WARNING] Compile-time declaration resolution failed.");
            printExpr(site.getExpr());
            summarizeFailures(site);
            endSection("");
        }

        private void summarizeFailures(MethodCallSite site) {
            startSection("Summary of failures:");
            site.getResolutionFailures()
                .forEach((phase, failures) -> {
                    startSection(phase.toString() + ":");
                    failures.forEach(it -> println(String.format("%-64s // while checking %s", it.getReason(), ppMethod(it.getFailedMethod()))));
                    endSection("");
                });
            endSection("");
        }

        @Override
        public void fallbackInvocation(JMethodSig ctdecl, MethodCallSite site) {
            if (!site.isLogEnabled()) {
                return;
            }
            startSection("[WARNING] Invocation type resolution failed");
            printExpr(site.getExpr());
            summarizeFailures(site);
            println("-> Falling back on " + ppHighlight(ctdecl)
                        + " (this may cause future mistakes)");
            endSection("");
        }

        @Override
        public void ambiguityError(MethodCallSite site, @Nullable MethodCtDecl selected, List<MethodCtDecl> methods) {
            println("");
            printExpr(site.getExpr());
            startSection("[WARNING] Ambiguity error: all methods are maximally specific");
            for (MethodCtDecl m : methods) {
                println(color(m.getMethodType().internalApi().originalMethod(), ANSI_RED));
            }

            if (selected != null) {
                endSection("Will select " + color(selected.getMethodType().internalApi().originalMethod(), ANSI_BLUE));
            } else {
                endSection(""); // no fallback?
            }
        }

        protected void printExpr(ExprMirror expr) {
            String exprText = expr.getLocation().getText().toString();
            exprText = exprText.replaceAll("\\R\\s+", "");
            exprText = StringUtil.escapeJava(StringUtils.truncate(exprText, 100));
            println("At:   " + fileLocation(expr));
            println("Expr: " + color(exprText, ANSI_YELLOW));
        }

        private String fileLocation(ExprMirror mirror) {
            return mirror.getLocation().getReportLocation().startPosToStringWithFile();
        }

        protected @NonNull String ppMethod(JMethodSig sig) {
            return TypePrettyPrint.prettyPrint(sig, new TypePrettyPrinter().printMethodHeader(false));
        }

        protected @NonNull String ppHighlight(JMethodSig sig) {
            String s = ppMethod(sig);
            int paramStart = s.indexOf('(');
            String name = s.substring(0, paramStart);
            String rest = s.substring(paramStart);
            return color(name, ANSI_BLUE) + colorIvars(colorPunct(rest));
        }

        protected @NonNull String ppBound(InferenceVar ivar, BoundKind kind, JTypeMirror bound) {
            return ivar + kind.getSym() + colorIvars(colorPunct(bound));
        }

        @Override
        public TypeInferenceLogger newInstance() {
            return new SimpleLogger(out);
        }
    }

    /**
     * This is mega verbose, should only be used for unit tests.
     */
    class VerboseLogger extends SimpleLogger {


        private final Deque<Integer> marks = new ArrayDeque<>();

        public VerboseLogger(PrintStream out) {
            super(out);
            mark();
        }

        void mark() {
            marks.push(getLevel());
        }

        void rollback(String lastWords) {
            int pop = marks.pop();
            updateLevel(pop - getLevel()); // back to normal
            if (!lastWords.isEmpty()) {
                updateLevel(+LEVEL_INCREMENT);
                println(lastWords);
                updateLevel(-LEVEL_INCREMENT);
            }
        }

        @Override
        public void startInference(JMethodSig sig, MethodCallSite site, MethodResolutionPhase phase) {
            mark();
            startSection(String.format("Phase %-17s%s", phase, ppHighlight(sig)));
        }


        @Override
        public void ctxInitialization(InferenceContext ctx, JMethodSig sig) {
            println(String.format("Context %-11d%s", ctx.getId(), ppHighlight(ctx.mapToIVars(sig))));
        }

        @Override
        public void endInference(@Nullable JMethodSig result) {
            rollback(result != null ? "Success: " + ppHighlight(result)
                                    : "FAILED! SAD!");
        }

        @Override
        public void skipInstantiation(JMethodSig partiallyInferred, MethodCallSite site) {
            println("Skipping instantiation of " + partiallyInferred + ", it's already complete");
        }


        @Override
        public void startArgsChecks() {
            startSection("ARGUMENTS");
        }

        @Override
        public void startReturnChecks() {
            startSection("RETURN");
        }


        @Override
        public void propagateAndAbort(InferenceContext context, InferenceContext parent) {
            println("Ctx " + parent.getId() + " adopts " + color(context.getFreeVars(), ANSI_BLUE) + " from ctx "
                        + context.getId());
        }

        @Override
        public void startArg(int i, ExprMirror expr, JTypeMirror formalType) {
            startSection("Checking arg " + i + " against " + formalType);
            printExpr(expr);
        }

        @Override
        public void skipArgAsNonPertinent(int i, ExprMirror expr) {
            startSection("Argument " + i + " is not pertinent to applicability");
            printExpr(expr);
            endSection("");
        }

        @Override
        public void functionalExprNeedsInvocationCtx(JTypeMirror targetT, ExprMirror expr) {
            println("Target type is not a functional interface yet: " + targetT);
            println("Will wait for invocation phase before discarding.");
        }

        @Override
        public void endArgsChecks() {
            endSection("");
        }

        @Override
        public void endArg() {
            endSection("");
        }

        @Override
        public void endReturnChecks() {
            endSection("");
        }

        @Override
        public void boundAdded(InferenceContext ctx, InferenceVar ivar, BoundKind kind, JTypeMirror bound, boolean isSubstitution) {
            String message = isSubstitution ? "Changed bound" : "New bound";
            println(addCtxInfo(ctx, message) + ppBound(ivar, kind, bound));
        }

        @Override
        public void ivarMerged(InferenceContext ctx, InferenceVar var, InferenceVar delegate) {
            println(addCtxInfo(ctx, "Ivar merged") + var + " <=> " + delegate);
        }

        @Override
        public void ivarInstantiated(InferenceContext ctx, InferenceVar var, JTypeMirror inst) {
            println(addCtxInfo(ctx, "Ivar instantiated") + color(var + " := ", ANSI_BLUE) + colorIvars(inst));
        }

        private @NonNull String addCtxInfo(InferenceContext ctx, String event) {
            return String.format("%-20s(ctx %d):   ", event, ctx.getId());
        }

        @Override
        public void logResolutionFail(ResolutionFailure exception) {
            super.logResolutionFail(exception);
            println("Failed: " + exception.getReason());
        }

        @Override
        public TypeInferenceLogger newInstance() {
            return new VerboseLogger(out);
        }

    }


}
