/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;

public class UnnecessaryVarargsArrayCreationRule extends AbstractJavaRulechainRule {

    // we visit array allocations because they are less frequent than
    // method calls
    public UnnecessaryVarargsArrayCreationRule() {
        super(ASTArrayAllocation.class);
    }

    @Override
    public Object visit(ASTArrayAllocation array, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (array.getArrayInitializer() == null) {
            return null;
        }

        JavaNode parent = array.getParent();
        if (parent instanceof ASTArgumentList && array.getIndexInParent() == parent.getNumChildren() - 1) {
            // node is the last param in an arguments list
            InvocationNode call = (InvocationNode) parent.getParent();
            OverloadSelectionResult info = call.getOverloadSelectionInfo();
            if (info.isFailed() || info.isVarargsCall()
                || !info.getMethodType().isVarargs()) {
                return null;
            }

            List<JTypeMirror> formals = info.getMethodType().getFormalParameters();
            JTypeMirror lastFormal = formals.get(formals.size() - 1);

            if (array.getTypeMirror().equals(lastFormal)) {
                // If type not equal, then it would not actually be equivalent to remove the array creation.
                // That case may be caught by ConfusingArgumentToVarargsMethod
                if (isRequiredForOverloadResolution(info, (ASTArgumentList) parent, array)) {
                    // Removing the explicit array would make another overload applicable,
                    // i.e. the call would become ambiguous or select a different overload.
                    // The explicit array is required, do not report. See #6611.
                    return null;
                }
                ctx.addViolation(array);
            }
        }

        return null;
    }

    /**
     * Returns true if removing the explicit array creation (i.e. expanding its
     * initializer elements into the varargs parameter) would make another
     * overload of the called executable applicable. In that case the explicit
     * array is required for overload resolution: removing it would either
     * change the selected overload or make the call ambiguous (compile error),
     * so the rule must not flag it.
     *
     * <p>Competing constructors come from the selected executable's own class
     * (exhaustive, since constructors are not inherited). Competing methods are
     * streamed from the declaring type, which includes overloads inherited from
     * supertypes, so any same-named overload in the class hierarchy is considered.
     *
     * <p>The applicability check is a conservative approximation of
     * JLS &sect;15.12.2.2-15.12.2.4: it is based on subtyping (reference and
     * primitive widening) and treats a generic parameter type by its upper
     * bound, which may consider a generic overload applicable that would
     * otherwise need inference to confirm. This may cause a few false-negatives,
     * but no false-positives.
     */
    private static boolean isRequiredForOverloadResolution(OverloadSelectionResult info,
                                                           ASTArgumentList argList,
                                                           ASTArrayAllocation array) {
        JExecutableSymbol selected = info.getMethodType().getSymbol();
        if (selected == null) {
            return false;
        }
        JClassSymbol owner = selected.getEnclosingClass();

        JTypeMirror componentType = ((JArrayType) array.getTypeMirror()).getComponentType();
        if (TypeOps.isUnresolved(componentType)) {
            // Cannot reason reliably about the expanded argument types.
            return false;
        }

        List<JTypeMirror> expandedArgTypes = expandedArgumentTypes(argList, componentType, array);

        List<? extends JExecutableSymbol> siblings =
                selected instanceof JConstructorSymbol
                        ? owner.getConstructors()
                        : methodsNamed(info.getMethodType().getDeclaringType(), selected.getSimpleName());

        for (JExecutableSymbol sibling : siblings) {
            if (sibling == selected) { // NOPMD CompareObjectsWithEquals - skip the candidate itself by identity
                continue;
            }
            if (isApplicableTo(sibling, expandedArgTypes)) {
                return true;
            }
        }
        return false;
    }

    private static List<JTypeMirror> expandedArgumentTypes(ASTArgumentList argList,
                                                           JTypeMirror componentType,
                                                           ASTArrayAllocation array) {
        // The array allocation is the last argument; leading arguments are unchanged.
        int leadingCount = argList.getNumChildren() - 1;
        int elementCount = array.getArrayInitializer().getNumChildren();
        List<JTypeMirror> types = new ArrayList<>(leadingCount + elementCount);
        for (int i = 0; i < leadingCount; i++) {
            types.add(argList.get(i).getTypeMirror());
        }
        for (int i = 0; i < elementCount; i++) {
            types.add(componentType);
        }
        return types;
    }

    /**
     * Returns the symbols of all methods named {@code name} that are declared
     * in, or inherited by, {@code ownerType}. Inherited overloads are included
     * so that a competing overload declared in a supertype is considered.
     */
    private static List<JExecutableSymbol> methodsNamed(JTypeMirror ownerType, String name) {
        return ownerType.streamMethods(m -> name.equals(m.getSimpleName()))
                        .map(JMethodSig::getSymbol)
                        .collect(Collectors.toList());
    }

    /**
     * Checks whether the given executable is applicable to the expanded argument
     * list in either fixed-arity or variable-arity invocation mode.
     */
    private static boolean isApplicableTo(JExecutableSymbol exec, List<JTypeMirror> argTypes) {
        List<JTypeMirror> formals = exec.getFormalParameterTypes(Substitution.EMPTY);
        int paramCount = formals.size();
        int argCount = argTypes.size();

        // Fixed-arity applicability.
        if (argCount == paramCount && allConvertible(argTypes, formals, paramCount)) {
            return true;
        }

        // Variable-arity applicability.
        if (exec.isVarargs() && paramCount > 0 && argCount >= paramCount - 1) {
            JTypeMirror lastFormal = formals.get(paramCount - 1);
            if (!(lastFormal instanceof JArrayType)) {
                return false; // defensive: a varargs parameter has an array type
            }
            JTypeMirror varargsComponent = ((JArrayType) lastFormal).getComponentType();
            if (!allConvertible(argTypes, formals, paramCount - 1)) {
                return false;
            }
            for (int i = paramCount - 1; i < argCount; i++) {
                if (!convertible(argTypes.get(i), varargsComponent)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean allConvertible(List<JTypeMirror> argTypes, List<JTypeMirror> paramTypes, int count) {
        for (int i = 0; i < count; i++) {
            if (!convertible(argTypes.get(i), paramTypes.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean convertible(JTypeMirror argType, JTypeMirror paramType) {
        // Treat a generic parameter type by its upper bound, so a generic overload
        // is considered applicable whenever the argument matches the bound.
        while (paramType instanceof JTypeVar) {
            JTypeMirror bound = ((JTypeVar) paramType).getUpperBound();
            if (bound == paramType) { // NOPMD CompareObjectsWithEquals - self-referential type var guard
                break;
            }
            paramType = bound;
        }
        return TypeOps.isConvertible(argType, paramType).somehow();
    }
}
