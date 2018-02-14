/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.1.0
 */
public class MissingOverrideRule extends AbstractJavaRule {

    private final Stack<MethodLookup> currentLookup = new Stack<>();


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        currentLookup.clear();
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        currentLookup.push(getMethodLookup(node.getType()));
        super.visit(node, data);
        currentLookup.pop();

        return data;
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        currentLookup.push(getMethodLookup(node.getType()));
        super.visit(node, data);
        currentLookup.pop();

        return data;
    }



    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        if (node.isAnonymousClass()) {
            currentLookup.push(getMethodLookup(node.getType()));
        }
        super.visit(node, data);

        if (node.isAnonymousClass()) {
            currentLookup.pop();
        }

        return data;
    }


    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        // FIXME, ASTEnumConstant needs typeres support!
        //        if (node.isAnonymousClass()) {
        //            currentExploredClass.push(node.getType());
        //        }
        super.visit(node, data);

        //        if (node.isAnonymousClass()) {
        //            currentExploredClass.pop();
        //        }

        return data;
    }


    /**
     * Returns a map of method name to methods with the same name (overloads).
     * The map contains a MethodWrapper for all methods declared in this class.
     *
     * @param exploredType Type to explore
     */
    private MethodLookup getMethodLookup(Class<?> exploredType) {
        if (exploredType == null) {
            return null;
        }

        Set<Method> overridden = overriddenMethods(exploredType);
        Map<String, Map<Integer, List<Method>>> result = new HashMap<>();

        for (Method m : exploredType.getDeclaredMethods()) {
            if (!result.containsKey(m.getName())) {
                result.put(m.getName(), new HashMap<Integer, List<Method>>());
            }

            Map<Integer, List<Method>> pCountToOverloads = result.get(m.getName());

            int paramCount = m.getParameterTypes().length;
            if (!pCountToOverloads.containsKey(paramCount)) {
                pCountToOverloads.put(paramCount, new ArrayList<Method>());
            }

            pCountToOverloads.get(paramCount).add(m);
        }

        return new MethodLookup(result, overridden);
    }


    /**
     * Returns the set of methods declared in this type that are overridden.
     *
     * @param exploredType The type to explore
     */
    private Set<Method> overriddenMethods(Class<?> exploredType) {
        return overriddenMethodsRec(exploredType, true, new HashSet<Method>(Arrays.asList(exploredType.getDeclaredMethods())), new HashSet<Method>(), new HashSet<Class<?>>());
    }


    private Set<Method> overriddenMethodsRec(Class<?> exploredType, boolean skip, Set<Method> candidates, Set<Method> result, Set<Class<?>> alreadyExplored) {

        if (candidates.isEmpty() || alreadyExplored.contains(exploredType)) {
            return result;
        }

        alreadyExplored.add(exploredType);

        if (!skip) {
            Method toRemove = null;
            for (Method dm : exploredType.getDeclaredMethods()) {
                for (Method cand : candidates) {
                    if (cand.getName().equals(dm.getName()) && Arrays.equals(cand.getParameterTypes(), dm.getParameterTypes())) {
                        // cand is overriden
                        result.add(cand);
                        toRemove = cand;
                        break;
                    }
                }
                if (toRemove != null) {
                    candidates.remove(toRemove); // no need to look for it elsewhere
                }
            }
        }

        Class<?> superClass = exploredType.getSuperclass();
        if (superClass != null) {
            overriddenMethodsRec(superClass, false, candidates, result, alreadyExplored);
        }

        for (Class<?> iface : exploredType.getInterfaces()) {
            overriddenMethodsRec(iface, false, candidates, result, alreadyExplored);
        }

        return result;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (currentLookup.peek() == null) {
            return super.visit(node, data);
        }

        for (ASTAnnotation annot : node.getDeclaredAnnotations()) {
            if (Override.class.equals(annot.getType())) {
                // we assume the compiler has already checked it, so it's correct
                return super.visit(node, data);
            }
        }

        boolean overridden = false;
        try {
            Boolean b = currentLookup.peek().isOverridden(node.getName(), node.getFormalParameters().getParameterCount());

            if (b == null) { // try harder
                Class<?>[] paramTypes = getParameterTypes(node);
                overridden = currentLookup.peek().isOverridden(node.getName(), paramTypes);
            } else {
                overridden = b;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            super.visit(node, data);
        }

        if (overridden) {
            // this method lacks an @Override annotation
            addViolation(data, node, new Object[]{node.getQualifiedName().getOperation()});
        }

        return super.visit(node, data);
    }


    private Class<?>[] getParameterTypes(ASTMethodDeclaration node) {
        ASTFormalParameters params = node.getFormalParameters();
        Class<?>[] paramTypes = new Class[params.getParameterCount()];
        int i = 0;
        for (ASTFormalParameter p : params) {
            Class<?> pType = p.getType();
            if (pType == null) {
                // fail, couldn't resolve one parameter
                return null;
            }

            paramTypes[i++] = pType;
        }
        return paramTypes;
    }


    private static class MethodLookup {

        // method name, parameter count, methods
        private final Map<String, Map<Integer, List<Method>>> map;
        private final Set<Method> overridden;


        private MethodLookup(Map<String, Map<Integer, List<Method>>> map, Set<Method> overridden) {
            this.map = map;
            this.overridden = overridden;
        }


        /**
         * Tries to determine if the method with the given name and parameter count is overridden
         *
         * @return True or false if the method succeeds, null if there was an ambiguity.
         * In that case, try harder using {@link #isOverridden(String, Class[])}.
         *
         * @throws NoSuchMethodException if no method is registered with this name and paramcount, which is a bug
         */
        public Boolean isOverridden(String name, int paramCount) throws NoSuchMethodException {
            List<Method> methods = getMethods(name, paramCount);

            if (methods.size() == 1) { // only one method with this name and parameter count, we can conclude
                return overridden.contains(methods.get(0));
            } else if (methods.size() == 2) { // maybe one of them is a bridge method
                // TODO scale that up
                int bridgeIndex = methods.get(0).isBridge() ? 0 : methods.get(1).isBridge() ? 1 : -1;
                if (bridgeIndex >= 0) {
                    return overridden.contains(methods.get(bridgeIndex));
                } else {
                    return null;
                }
            } else { // several overloads with same name and count, cannot be determined without comparing parameters
                return null;
            }
        }


        private List<Method> getMethods(String name, int paramCount) throws NoSuchMethodException {
            Map<Integer, List<Method>> overloads = map.get(name);
            if (overloads == null) {
                throw new NoSuchMethodException(name);
            }

            List<Method> methods = overloads.get(paramCount);
            if (methods == null || methods.isEmpty()) {
                throw new NoSuchMethodException(name);
            }
            return methods;
        }


        /**
         * Tries to determine if the method with the given name and parameter types is overridden
         *
         * @return True or false. Returns false if there was an ambiguity.
         *
         * @throws NoSuchMethodException if no method is registered with this name and paramcount, which is a bug
         */
        public boolean isOverridden(String name, Class<?>[] paramTypes) throws NoSuchMethodException {
            for (Method m : getMethods(name, paramTypes.length)) {
                if (Arrays.equals(m.getParameterTypes(), paramTypes)) {
                    // we found our overload
                    return overridden.contains(m);
                }
            }
            return false;
        }

    }


}



