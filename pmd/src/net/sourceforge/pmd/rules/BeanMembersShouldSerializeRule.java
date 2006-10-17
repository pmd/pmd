/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class BeanMembersShouldSerializeRule extends AbstractRule {

	private String prefixProperty;

    private static final PropertyDescriptor prefixDescriptor = new StringProperty(
    	"prefix", "Prefix somethingorother?", "", 1.0f
    	);
    
    private static final Map propertyDescriptorsByName = asFixedMap(prefixDescriptor);
    	
	
	public Object visit(ASTCompilationUnit node, Object data) {
		prefixProperty = getStringProperty(prefixDescriptor);
		super.visit(node, data);
		return data;
	}
	
	private static String[] imagesOf(List simpleNodes) {
		
        String[] imageArray = new String[simpleNodes.size()];
        
        for (int i = 0; i < simpleNodes.size(); i++) {
        	imageArray[i] = ((SimpleNode) simpleNodes.get(i)).getImage();
        }
        return imageArray;
	}
	
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        Map methods = node.getScope().getEnclosingClassScope().getMethodDeclarations();
        List getSetMethList = new ArrayList(methods.size());
        for (Iterator i = methods.keySet().iterator(); i.hasNext();) {
            ASTMethodDeclarator mnd = ((MethodNameDeclaration) i.next()).getMethodNameDeclaratorNode();
            if (isBeanAccessor(mnd)) {
                getSetMethList.add(mnd);
            }
        }

        String[] methNameArray = imagesOf(getSetMethList);
        
        Arrays.sort(methNameArray);

        Map vars = node.getScope().getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (((List) vars.get(decl)).isEmpty() || decl.getAccessNodeParent().isTransient() || decl.getAccessNodeParent().isStatic()) {
                continue;
            }
            String varName = trimIfPrefix(decl.getImage());
            varName = varName.substring(0, 1).toUpperCase() + varName.substring(1, varName.length());
            boolean hasGetMethod = Arrays.binarySearch(methNameArray, "get" + varName) >= 0 || Arrays.binarySearch(methNameArray, "is" + varName) >= 0;
            boolean hasSetMethod = Arrays.binarySearch(methNameArray, "set" + varName) >= 0;
            if (!hasGetMethod || !hasSetMethod) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private String trimIfPrefix(String img) {
        if (prefixProperty != null && img.startsWith(prefixProperty)) {
            return img.substring(prefixProperty.length());
        }
        return img;
    }

    private boolean isBeanAccessor(ASTMethodDeclarator meth) {
    	
    	String methodName = meth.getImage();
    	
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return true;
        }
        if (methodName.startsWith("is")) {
            ASTResultType ret = (ASTResultType) meth.jjtGetParent().jjtGetChild(0);
            List primitives = ret.findChildrenOfType(ASTPrimitiveType.class);
            if (!primitives.isEmpty() && ((ASTPrimitiveType) primitives.get(0)).isBoolean()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @return Map
     */
    protected Map propertiesByName() {
    	return propertyDescriptorsByName;
    }
}
