/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;


/**
 * Flags missing @Override annotations.
 *
 * @author Clément Fournier
 * @since 6.2.0
 */
public class MissingOverrideRule extends AbstractJavaRule {

    private static final Logger LOG = Logger.getLogger(MissingOverrideRule.class.getName());
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

        try {
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
        } catch (final LinkageError e) {
            // we may have an incomplete auxclasspath
            return null;
        }
    }


    /**
     * Returns the set of methods declared in this type that are overridden.
     *
     * @param exploredType The type to explore
     */
    private Set<Method> overriddenMethods(Class<?> exploredType) {
        return overriddenMethodsRec(exploredType, true, new HashSet<>(Arrays.asList(exploredType.getDeclaredMethods())), new HashSet<Method>(), new HashSet<Class<?>>(), false);
    }


    private Set<Method> overriddenMethodsRec(Class<?> exploredType, boolean skip, Set<Method> candidates, Set<Method> result, Set<Class<?>> alreadyExplored, boolean onlyPublic) {

        if (candidates.isEmpty() || alreadyExplored.contains(exploredType)) {
            return result;
        }

        alreadyExplored.add(exploredType);

        if (!skip) {
            Set<Method> toRemove = new HashSet<>();
            for (Method dm : exploredType.getDeclaredMethods()) {
                if (onlyPublic && !Modifier.isPublic(dm.getModifiers())
                        || Modifier.isPrivate(dm.getModifiers())
                        || Modifier.isStatic(dm.getModifiers())) {
                    continue;
                }

                for (Method cand : candidates) {
                    if (Modifier.isPrivate(dm.getModifiers()) || Modifier.isStatic(dm.getModifiers())) {
                        continue;
                    }

                    if (cand.getName().equals(dm.getName()) && Arrays.equals(cand.getParameterTypes(), dm.getParameterTypes())) {
                        // cand is overriden
                        result.add(cand);
                        toRemove.add(cand);
                        // Several methods are eligible, because of return type covariance
                        // We could do away with adding only the first one to the result,
                        // but then the other would stay in the candidates and we'd explore
                        // the rest of the tree unnecessarily
                    }
                }
                candidates.removeAll(toRemove); // no need to look for it elsewhere
            }
        }

        if (candidates.isEmpty()) {
            return result;
        }

        Class<?> superClass = exploredType.getSuperclass();
        if (superClass != null) {
            overriddenMethodsRec(superClass, false, candidates, result, alreadyExplored, false);
        }

        for (Class<?> iface : exploredType.getInterfaces()) {
            overriddenMethodsRec(iface, false, candidates, result, alreadyExplored, false);
        }

        if (exploredType.isInterface() && exploredType.getInterfaces().length == 0) {
            // implicit public object member declarations
            if (!alreadyExplored.contains(Object.class)) {
                overriddenMethodsRec(Object.class, false, candidates, result, alreadyExplored, true);
            }
        }

        return result;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (currentLookup.isEmpty() || currentLookup.peek() == null) {
            return super.visit(node, data);
        }

        for (ASTAnnotation annot : node.getDeclaredAnnotations()) {
            if (Override.class.equals(annot.getType())) {
                // we assume the compiler has already checked it, so it's correct
                return super.visit(node, data);
            }
        }

        try {
            boolean overridden = currentLookup.peek().isOverridden(node.getName(), node.getFormalParameters());
            if (overridden) {
                addViolation(data, node, new Object[]{PrettyPrintingUtil.displaySignature(node)});
            }
        } catch (NoSuchMethodException e) {
            // may happen in the body of an enum constant,
            // because the method lookup used is the one of
            // the parent class.
            LOG.fine("MissingOverride encountered unexpected method " + node.getName());
            // throw new RuntimeException(e); // uncomment when enum constants are handled by typeres
        }
        return super.visit(node, data);
    }



    private static class MethodLookup {

        // method name, parameter count, methods
        private final Map<String, Map<Integer, List<Method>>> map;
        private final Set<Method> overridden;


        private MethodLookup(Map<String, Map<Integer, List<Method>>> map, Set<Method> overridden) {
            this.map = map;
            this.overridden = overridden;

            for (Map<Integer, List<Method>> overloadSet : map.values()) {
                for (Entry<Integer, List<Method>> sameParamCountMethods : overloadSet.entrySet()) {
                    // bridges have the same name and param count as the bridged method
                    resolveBridges(sameParamCountMethods.getValue());
                }
            }
        }


        private void resolveBridges(List<Method> overloads) {
            if (overloads.size() <= 1) {
                return;
            }

            // partition the overloads
            List<Method> bridges = new ArrayList<>();
            List<Method> notBridges = new ArrayList<>();

            for (Method m : overloads) {
                if (m.isBridge()) {
                    bridges.add(m);
                } else {
                    notBridges.add(m);
                }
            }

            if (bridges.isEmpty()) {
                return;
            }

            // each bridge necessarily calls another non-bridge method, which is overridden

            if (notBridges.size() == bridges.size()) {
                // This is a good heuristic, though not perfect.

                // Most of the time, bridges is one-to-one to notBridges, and we can safely assume that
                // all non-bridge methods are overridden, since they need a bridge method

                // This chokes on overloads which don't override a previous definition, in which case there's no
                // generated bridge for that overload. Short of statically analysing type parameters, or reading
                // the bytecode to find the delegation call, we have no way to find out which of the overloads is
                // overridden. An example of that is in RuleViolationComparator: there's one bridge
                // compare(Object, Object) for the overload compare(RV, RV), but we can't know because there's
                // another overload, compare(String, String) which is equally eligible

                // It's also possible that several bridges are generated for the same method, when the method
                // was already redefined several times with a different bound. An example of that is in
                // PropertyDescriptorConversionWrapper.SingleValue.Packaged and similar subclasses: each of the
                // levels of the hierarchy redefine the original method with a tighter bound on the type parameter.
                // Three bridges are generated for populate() on concrete builder classes.

                // The two situations could happen together, and if they balance out, that gives a false positive
                // with the current test (size are equal). If they don't balance out, then we don't report anything,
                // which is why this test seems the safest.

                // Depending on the real-world frequency of those two situations happening together, we may rather
                // use notBridges.size() <= bridges.size(), to remove FNs caused by additional bridges.

                overridden.addAll(notBridges);
            }

            overloads.removeAll(bridges); // better prune the candidate overloads anyway
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
         * Tries to determine if the method with the given name and parameter count is overridden
         *
         * @return True or false
         *
         * @throws NoSuchMethodException if no method is registered with this name and paramcount, which is a bug
         */
        boolean isOverridden(String name, ASTFormalParameters params) throws NoSuchMethodException {
            List<Method> methods = getMethods(name, params.size());

            if (methods.size() == 1) { // only one method with this name and parameter count, we can conclude
                return overridden.contains(methods.get(0));
            } else { // several overloads with same name and count, cannot be determined without comparing parameters
                Class<?>[] paramTypes = getParameterTypes(params);
                if (paramTypes == null) {
                    return false;
                }
                for (Method m : methods) {
                    if (Arrays.equals(m.getParameterTypes(), paramTypes)) {
                        // we found our overload
                        return overridden.contains(m);
                    }
                }
                return false;
            }
        }


        private static Class<?>[] getParameterTypes(ASTFormalParameters params) {
            Class<?>[] paramTypes = new Class[params.size()];
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


    }


}



