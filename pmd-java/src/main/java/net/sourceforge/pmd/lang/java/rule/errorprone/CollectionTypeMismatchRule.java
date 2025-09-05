/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.OptionalBool;

/**
 * Detects method calls on collections where the passed object cannot possibly be in the collection
 * due to type mismatch. This helps catch potential programming errors where incompatible types
 * are used with collection methods like contains(), remove(), indexOf(), etc.
 * 
 * Examples of violations:
 * - List&lt;Integer&gt; list; list.remove("string"); // String cannot be in Integer list
 * - Map&lt;String, Integer&gt; map; map.get(42); // Integer key cannot be in String-keyed map
 */
public class CollectionTypeMismatchRule extends AbstractJavaRulechainRule {

    // Collection methods that take a single Object parameter
    private static final InvocationMatcher COLLECTION_CONTAINS = InvocationMatcher.parse("java.util.Collection#contains(java.lang.Object)");
    private static final InvocationMatcher COLLECTION_REMOVE = InvocationMatcher.parse("java.util.Collection#remove(java.lang.Object)");
    private static final InvocationMatcher LIST_INDEX_OF = InvocationMatcher.parse("java.util.List#indexOf(java.lang.Object)");
    private static final InvocationMatcher LIST_LAST_INDEX_OF = InvocationMatcher.parse("java.util.List#lastIndexOf(java.lang.Object)");
    private static final InvocationMatcher DEQUE_REMOVE_FIRST_OCCURRENCE = InvocationMatcher.parse("java.util.Deque#removeFirstOccurrence(java.lang.Object)");
    private static final InvocationMatcher DEQUE_REMOVE_LAST_OCCURRENCE = InvocationMatcher.parse("java.util.Deque#removeLastOccurrence(java.lang.Object)");
    
    // Collection methods that take a Collection parameter  
    private static final InvocationMatcher COLLECTION_REMOVE_ALL = InvocationMatcher.parse("java.util.Collection#removeAll(java.util.Collection)");
    private static final InvocationMatcher COLLECTION_RETAIN_ALL = InvocationMatcher.parse("java.util.Collection#retainAll(java.util.Collection)");
    private static final InvocationMatcher COLLECTION_CONTAINS_ALL = InvocationMatcher.parse("java.util.Collection#containsAll(java.util.Collection)");
    
    // Map methods that take key parameters
    private static final InvocationMatcher MAP_CONTAINS_KEY = InvocationMatcher.parse("java.util.Map#containsKey(java.lang.Object)");
    private static final InvocationMatcher MAP_GET = InvocationMatcher.parse("java.util.Map#get(java.lang.Object)");
    private static final InvocationMatcher MAP_GET_OR_DEFAULT = InvocationMatcher.parse("java.util.Map#getOrDefault(java.lang.Object,_)");
    private static final InvocationMatcher MAP_REMOVE_ONE_PARAM = InvocationMatcher.parse("java.util.Map#remove(java.lang.Object)");
    
    // Map methods that take value parameters
    private static final InvocationMatcher MAP_CONTAINS_VALUE = InvocationMatcher.parse("java.util.Map#containsValue(java.lang.Object)");
    private static final InvocationMatcher HASHTABLE_CONTAINS = InvocationMatcher.parse("java.util.Hashtable#contains(java.lang.Object)");
    private static final InvocationMatcher CONCURRENT_HASHMAP_CONTAINS = InvocationMatcher.parse("java.util.concurrent.ConcurrentHashMap#contains(java.lang.Object)");

    // Map methods that take key-value parameters
    private static final InvocationMatcher MAP_REMOVE_TWO_PARAM = InvocationMatcher.parse("java.util.Map#remove(java.lang.Object,java.lang.Object)");

    public CollectionTypeMismatchRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (COLLECTION_CONTAINS.matchesCall(node)
                || COLLECTION_REMOVE.matchesCall(node)
                || LIST_INDEX_OF.matchesCall(node)
                || LIST_LAST_INDEX_OF.matchesCall(node)
                || DEQUE_REMOVE_FIRST_OCCURRENCE.matchesCall(node)
                || DEQUE_REMOVE_LAST_OCCURRENCE.matchesCall(node)
        ) {
            checkCollectionElementCompatibility(node, ctx);
        } else if (COLLECTION_REMOVE_ALL.matchesCall(node)
                || COLLECTION_RETAIN_ALL.matchesCall(node)
                || COLLECTION_CONTAINS_ALL.matchesCall(node)
        ) {
            checkCollectionToCollectionCompatibility(node, ctx);
        } else if (MAP_CONTAINS_KEY.matchesCall(node)
                || MAP_GET.matchesCall(node)
                || MAP_GET_OR_DEFAULT.matchesCall(node)
                || MAP_REMOVE_ONE_PARAM.matchesCall(node)
        ) {
            checkMapKeyCompatibility(node, ctx);
        } else if (MAP_CONTAINS_VALUE.matchesCall(node)
                || HASHTABLE_CONTAINS.matchesCall(node)
                || CONCURRENT_HASHMAP_CONTAINS.matchesCall(node)) {
            checkMapValueCompatibility(node, ctx);
        } else if (MAP_REMOVE_TWO_PARAM.matchesCall(node)) {
            checkMapKeyValueCompatibility(node, ctx);
        }
        
        return null;
    }
    
    private void checkCollectionElementCompatibility(ASTMethodCall node, RuleContext ctx) {
        JTypeMirror qualifierType = getQualifierType(node);
        if (!(qualifierType instanceof JClassType)) {
            return;
        }
        
        JTypeMirror elementType = getCollectionElementType((JClassType) qualifierType);
        if (elementType == null) {
            return;
        }
        
        ASTExpression firstArg = getFirstArgument(node);
        JTypeMirror argType = firstArg.getTypeMirror();
        checkCompatible(node, ctx, argType, elementType);
    }

    private boolean checkCompatible(ASTMethodCall node, RuleContext ctx, JTypeMirror argType, JTypeMirror elementType) {
        if (!isCompatibleType(node, argType, elementType)) {
            ctx.addViolation(node, argType.toString(), elementType.toString());
            return false;
        }
        return true;
    }

    private void checkCollectionToCollectionCompatibility(ASTMethodCall node, RuleContext ctx) {
        JTypeMirror qualifierType = getQualifierType(node);
        if (!(qualifierType instanceof JClassType)) {
            return;
        }
        
        JTypeMirror elementType = getCollectionElementType((JClassType) qualifierType);
        if (elementType == null) {
            return;
        }
        
        ASTExpression firstArg = getFirstArgument(node);
        JTypeMirror argType = firstArg.getTypeMirror();
        if (argType instanceof JClassType && isCollectionType((JClassType) argType)) {
            JTypeMirror argElementType = getCollectionElementType((JClassType) argType);
            if (argElementType != null) {
                checkCompatible(node, ctx, argElementType, elementType);
            }
        }
    }
    
    private void checkMapKeyCompatibility(ASTMethodCall node, RuleContext ctx) {
        JTypeMirror qualifierType = getQualifierType(node);
        if (!(qualifierType instanceof JClassType)) {
            return;
        }
        
        JTypeMirror keyType = getMapKeyType((JClassType) qualifierType);
        if (keyType == null) {
            return;
        }
        
        ASTExpression firstArg = getFirstArgument(node);
        JTypeMirror argType = firstArg.getTypeMirror();
        checkCompatible(node, ctx, argType, keyType);
    }
    
    private void checkMapValueCompatibility(ASTMethodCall node, RuleContext ctx) {
        JTypeMirror qualifierType = getQualifierType(node);
        if (!(qualifierType instanceof JClassType)) {
            return;
        }
        
        JTypeMirror valueType = getMapValueType((JClassType) qualifierType);
        if (valueType == null) {
            return;
        }
        
        ASTExpression firstArg = getFirstArgument(node);
        JTypeMirror argType = firstArg.getTypeMirror();
        checkCompatible(node, ctx, argType, valueType);
    }
    
    private void checkMapKeyValueCompatibility(ASTMethodCall node, RuleContext ctx) {
        JTypeMirror qualifierType = getQualifierType(node);
        if (!(qualifierType instanceof JClassType)) {
            return;
        }
        
        JTypeMirror keyType = getMapKeyType((JClassType) qualifierType);
        JTypeMirror valueType = getMapValueType((JClassType) qualifierType);
        if (keyType == null || valueType == null) {
            return;
        }
        
        ASTExpression keyArg = getFirstArgument(node);
        ASTExpression valueArg = getSecondArgument(node);
        
        JTypeMirror keyArgType = keyArg.getTypeMirror();
        JTypeMirror valueArgType = valueArg.getTypeMirror();

        if (checkCompatible(node, ctx, keyArgType, keyType)) {
            checkCompatible(node, ctx, valueArgType, valueType);
        }
    }
    
    private boolean isCollectionType(JClassType type) {
        return TypeTestUtil.isA(java.util.Collection.class, type);
    }
    
    private JTypeMirror getCollectionElementType(JClassType collectionType) {
        JClassType asSuperCollection = collectionType.getAsSuper(collectionType.getTypeSystem().getClassSymbol(java.util.Collection.class));
        if (asSuperCollection.getTypeArgs().isEmpty()) {
            // Raw collection type without generics
            return null;
        }
        JTypeMirror elementType = asSuperCollection.getTypeArgs().get(0);
        return resolveWildcardBound(elementType);
    }
    
    private JTypeMirror getMapKeyType(JClassType mapType) {
        JClassType asSuperMap = mapType.getAsSuper(mapType.getTypeSystem().getClassSymbol(java.util.Map.class));
        if (asSuperMap.getTypeArgs().isEmpty()) {
            // Raw map type without generics
            return null;
        }
        JTypeMirror keyType = asSuperMap.getTypeArgs().get(0);
        return resolveWildcardBound(keyType);
    }
    
    private JTypeMirror getMapValueType(JClassType mapType) {
        JClassType asSuperMap = mapType.getAsSuper(mapType.getTypeSystem().getClassSymbol(java.util.Map.class));
        if (asSuperMap.getTypeArgs().isEmpty()) {
            // Raw map type without generics
            return null;
        }
        JTypeMirror valueType = asSuperMap.getTypeArgs().get(1);
        return resolveWildcardBound(valueType);
    }

    private JTypeMirror resolveWildcardBound(JTypeMirror type) {
        return TypeOps.wildUpperBound(type);
    }
    
    private boolean isCompatibleType(ASTMethodCall node, JTypeMirror argType, JTypeMirror expectedType) {
        // If argType is unresolved, be conservative and assume compatibility
        // This prevents false positives when external dependencies can't be resolved
        if (TypeOps.isUnresolved(argType)) {
            return true;
        }
        Infer infer = InternalApiBridge.getInferenceEntryPoint(node);

        return infer.areTypesMaybeRelated(argType.box(), expectedType.box()) != OptionalBool.NO;

        // Handle wildcards and captured type variables - they should be compatible with any type
        // since they represent unknown types that could potentially be the expected type
        // if (argType instanceof JWildcardType
        //     || argType instanceof JTypeVar && ((JTypeVar) argType).isCaptured()) {
        //     return true;
        // }
        //
        // // Check basic convertibility using raw types (ignore generics)
        // // This will cause some false-negaties, but doing this right would lead to much more complicated code
        // JTypeMirror rawArgType = getRawType(argType);
        // JTypeMirror rawExpectedType = getRawType(expectedType);
        //
        // if (TypeOps.isConvertible(rawArgType, rawExpectedType).somehow()
        //         || TypeOps.isConvertible(rawExpectedType, rawArgType).somehow()
        // ) {
        //     return true;
        // }
        //
        // // Check primitive/wrapper compatibility (autoboxing/unboxing)
        // if (argType.isPrimitive()) {
        //     // Check if autoboxed primitive is compatible with expected type
        //     JTypeMirror boxedArgType = argType.box();
        //     return TypeOps.isConvertible(boxedArgType, expectedType).somehow();
        // }
        //
    }
    
    private JTypeMirror getRawType(JTypeMirror type) {
        if (type instanceof JClassType) {
            JClassType classType = (JClassType) type;
            return classType.getErasure();
        }
        return type;
    }
    

    private JTypeMirror getQualifierType(ASTMethodCall node) {
        return node.getQualifier() != null ? node.getQualifier().getTypeMirror() : null;
    }
    
    private ASTExpression getFirstArgument(ASTMethodCall node) {
        return node.getArguments().get(0);
    }

    private ASTExpression getSecondArgument(ASTMethodCall node) {
        return node.getArguments().get(1);
    }
}
