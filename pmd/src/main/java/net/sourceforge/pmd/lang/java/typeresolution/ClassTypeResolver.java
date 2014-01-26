/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSingleMemberAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.ast.TypeNode;

//
// Helpful reading:
// http://www.janeg.ca/scjp/oper/promotions.html
// http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html
//

public class ClassTypeResolver extends JavaParserVisitorAdapter {

	private static final Logger LOG = Logger.getLogger(ClassTypeResolver.class.getName());

	private static final Map<String, Class<?>> PRIMITIVE_TYPES;
	private static final Map<String, String> JAVA_LANG;

	static {
		// Note: Assumption here that primitives come from same parent ClassLoader regardless of what ClassLoader we are passed
		Map<String, Class<?>> thePrimitiveTypes = new HashMap<String, Class<?>>();
		thePrimitiveTypes.put("void", Void.TYPE);
		thePrimitiveTypes.put("boolean", Boolean.TYPE);
		thePrimitiveTypes.put("byte", Byte.TYPE);
		thePrimitiveTypes.put("char", Character.TYPE);
		thePrimitiveTypes.put("short", Short.TYPE);
		thePrimitiveTypes.put("int", Integer.TYPE);
		thePrimitiveTypes.put("long", Long.TYPE);
		thePrimitiveTypes.put("float", Float.TYPE);
		thePrimitiveTypes.put("double", Double.TYPE);
		PRIMITIVE_TYPES = Collections.unmodifiableMap(thePrimitiveTypes);

		Map<String, String> theJavaLang = new HashMap<String, String>();
		theJavaLang.put("Boolean", "java.lang.Boolean");
		theJavaLang.put("Byte", "java.lang.Byte");
		theJavaLang.put("Character", "java.lang.Character");
		theJavaLang.put("CharSequence", "java.lang.CharSequence");
		theJavaLang.put("Class", "java.lang.Class");
		theJavaLang.put("ClassLoader", "java.lang.ClassLoader");
		theJavaLang.put("Cloneable", "java.lang.Cloneable");
		theJavaLang.put("Comparable", "java.lang.Comparable");
		theJavaLang.put("Compiler", "java.lang.Compiler");
		theJavaLang.put("Double", "java.lang.Double");
		theJavaLang.put("Float", "java.lang.Float");
		theJavaLang.put("InheritableThreadLocal", "java.lang.InheritableThreadLocal");
		theJavaLang.put("Integer", "java.lang.Integer");
		theJavaLang.put("Long", "java.lang.Long");
		theJavaLang.put("Math", "java.lang.Math");
		theJavaLang.put("Number", "java.lang.Number");
		theJavaLang.put("Object", "java.lang.Object");
		theJavaLang.put("Package", "java.lang.Package");
		theJavaLang.put("Process", "java.lang.Process");
		theJavaLang.put("Runnable", "java.lang.Runnable");
		theJavaLang.put("Runtime", "java.lang.Runtime");
		theJavaLang.put("RuntimePermission", "java.lang.RuntimePermission");
		theJavaLang.put("SecurityManager", "java.lang.SecurityManager");
		theJavaLang.put("Short", "java.lang.Short");
		theJavaLang.put("StackTraceElement", "java.lang.StackTraceElement");
		theJavaLang.put("StrictMath", "java.lang.StrictMath");
		theJavaLang.put("String", "java.lang.String");
		theJavaLang.put("StringBuffer", "java.lang.StringBuffer");
		theJavaLang.put("System", "java.lang.System");
		theJavaLang.put("Thread", "java.lang.Thread");
		theJavaLang.put("ThreadGroup", "java.lang.ThreadGroup");
		theJavaLang.put("ThreadLocal", "java.lang.ThreadLocal");
		theJavaLang.put("Throwable", "java.lang.Throwable");
		theJavaLang.put("Void", "java.lang.Void");
		JAVA_LANG = Collections.unmodifiableMap(theJavaLang);
	}

	private final PMDASMClassLoader pmdClassLoader;
	private Map<String, String> importedClasses;
	private List<String> importedOnDemand;
	private int anonymousClassCounter = 0;
	
	public ClassTypeResolver() {
		this(ClassTypeResolver.class.getClassLoader());
	}

	public ClassTypeResolver(ClassLoader classLoader) {
		pmdClassLoader = PMDASMClassLoader.getInstance(classLoader);
	}

	// FUTURE ASTCompilationUnit should not be a TypeNode.  Clean this up accordingly.
	@Override
	public Object visit(ASTCompilationUnit node, Object data) {
		String className = null;
		try {
			importedOnDemand = new ArrayList<String>();
			importedClasses = new HashMap<String, String>();
			className = getClassName(node);
			if (className != null) {
				populateClassName(node, className);
			}
		} catch (ClassNotFoundException e) {
			LOG.log(Level.FINE, "Could not find class " + className + ", due to: " + e.getClass().getName() + ": " + e.getMessage());
		} catch (LinkageError e) {
			LOG.log(Level.WARNING, "Could not find class " + className + ", due to: " + e.getClass().getName() + ": " + e.getMessage());
		} finally {
			populateImports(node);
		}
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTImportDeclaration node, Object data) {
		ASTName importedType = (ASTName)node.jjtGetChild(0);
		if (importedType.getType() != null) {
			node.setType(importedType.getType());
		} else {
			populateType(node, importedType.getImage());
		}

		if (node.getType() != null) {
			node.setPackage(node.getType().getPackage());
		}
		return data;
	}

	@Override
	public Object visit(ASTTypeDeclaration node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTClassOrInterfaceType node, Object data) {
		String typeName = node.getImage();
		if (node.jjtGetParent().hasDescendantOfType(ASTClassOrInterfaceBody.class)) {
			anonymousClassCounter++;
		    AbstractNode parent = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
		    if (parent == null) {
		        parent = node.getFirstParentOfType(ASTEnumDeclaration.class);
		    }
            typeName = parent.getImage() + "$" + anonymousClassCounter;
		}
		populateType(node, typeName);
		return data;
	}

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		populateType(node, node.getImage());
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTEnumDeclaration node, Object data) {
		populateType(node, node.getImage());
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
		populateType(node, node.getImage());
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTName node, Object data) {
		/*
		 * Only doing this for nodes where getNameDeclaration is null this means
		 * it's not a named node, i.e. Static reference or Annotation Doing this
		 * for memory - TODO: Investigate if there is a valid memory concern or
		 * not
		 */
		if (node.getNameDeclaration() == null) {
			// Skip these scenarios as there is no type to populate in these cases:
			// 1) Parent is a PackageDeclaration, which is not a type
			// 2) Parent is a ImportDeclaration, this is handled elsewhere.
			if (!(node.jjtGetParent() instanceof ASTPackageDeclaration || node.jjtGetParent() instanceof ASTImportDeclaration)) {
				String name = node.getImage();
				if (name.indexOf('.') != -1) {
					name = name.substring(0, name.indexOf('.'));
				}
				populateType(node, name);
			}
		} else {
			// Carry over the type from the declaration
			if (node.getNameDeclaration().getNode() instanceof TypeNode) {
				node.setType(((TypeNode)node.getNameDeclaration().getNode()).getType());
			}
		}
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTFieldDeclaration node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTVariableDeclarator node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTVariableDeclaratorId node, Object data) {
		if (node == null || node.getNameDeclaration() == null) {
			return super.visit(node, data);
		}
		String name = node.getNameDeclaration().getTypeImage();
		if (name != null) {
    		if (name.indexOf('.') != -1) {
    			name = name.substring(0, name.indexOf('.'));
    		}
    		populateType(node, name);
		}
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTType node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTReferenceType node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTPrimitiveType node, Object data) {
		populateType(node, node.getImage());
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTConditionalExpression node, Object data) {
		super.visit(node, data);
		if (node.isTernary()) {
			// TODO Rules for Ternary are complex
		} else {
			rollupTypeUnary(node);
		}
		return data;
	}

	@Override
	public Object visit(ASTConditionalOrExpression node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTConditionalAndExpression node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTInclusiveOrExpression node, Object data) {
		super.visit(node, data);
		rollupTypeBinaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTExclusiveOrExpression node, Object data) {
		super.visit(node, data);
		rollupTypeBinaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTAndExpression node, Object data) {
		super.visit(node, data);
		rollupTypeBinaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTEqualityExpression node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTInstanceOfExpression node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTRelationalExpression node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTShiftExpression node, Object data) {
		super.visit(node, data);
		// Unary promotion on LHS is type of a shift operation
		rollupTypeUnaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTAdditiveExpression node, Object data) {
		super.visit(node, data);
		rollupTypeBinaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTMultiplicativeExpression node, Object data) {
		super.visit(node, data);
		rollupTypeBinaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTUnaryExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnaryNumericPromotion(node);
		return data;
	}

	@Override
	public Object visit(ASTPreIncrementExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTPreDecrementExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
		super.visit(node, data);
		if ("!".equals(node.getImage())) {
			populateType(node, "boolean");
		} else {
			rollupTypeUnary(node);
		}
		return data;
	}

	@Override
	public Object visit(ASTPostfixExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTCastExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

	@Override
	public Object visit(ASTPrimaryExpression node, Object data) {
		super.visit(node, data);
		if (node.jjtGetNumChildren() == 1) {
			rollupTypeUnary(node);
		} else {
			// TODO OMG, this is complicated.  PrimaryExpression, PrimaryPrefix and PrimarySuffix are all related.
		}
		return data;
	}

	@Override
	public Object visit(ASTPrimaryPrefix node, Object data) {
		super.visit(node, data);
		if (node.getImage() == null) {
			rollupTypeUnary(node);
		} else {
			// TODO OMG, this is complicated.  PrimaryExpression, PrimaryPrefix and PrimarySuffix are all related.
		}
		return data;
	}

	@Override
	public Object visit(ASTPrimarySuffix node, Object data) {
		super.visit(node, data);
		// TODO OMG, this is complicated.  PrimaryExpression, PrimaryPrefix and PrimarySuffix are all related.
		return data;
	}

	@Override
	public Object visit(ASTNullLiteral node, Object data) {
		// No explicit type
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTBooleanLiteral node, Object data) {
		populateType(node, "boolean");
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTLiteral node, Object data) {
		super.visit(node, data);
		if (node.jjtGetNumChildren() != 0) {
			rollupTypeUnary(node);
		} else {
			if (node.isIntLiteral()) {
				String image = node.getImage();
				if (image.endsWith("l") || image.endsWith("L")) {
					populateType(node, "long");
				} else {
					try {
						Integer.decode(image);
						populateType(node, "int");
					} catch (NumberFormatException e) {
						// Bad literal, 'long' requires use of 'l' or 'L' suffix.
					}
				}
			} else if (node.isFloatLiteral()) {
				String image = node.getImage();
				if (image.endsWith("f") || image.endsWith("F")) {
					populateType(node, "float");
				} else if (image.endsWith("d") || image.endsWith("D")) {
					populateType(node, "double");
				} else {
					try {
						Double.parseDouble(image);
						populateType(node, "double");
					} catch (NumberFormatException e) {
						// Bad literal, 'float' requires use of 'f' or 'F' suffix.
					}
				}
			} else if (node.isCharLiteral()) {
				populateType(node, "char");
			} else if (node.isStringLiteral()) {
				populateType(node, "java.lang.String");
			} else {
				throw new IllegalStateException("PMD error, unknown literal type!");
			}
		}
		return data;
	}

	@Override
	public Object visit(ASTAllocationExpression node, Object data) {
		super.visit(node, data);

		if (node.jjtGetNumChildren() >= 2 && node.jjtGetChild(1) instanceof ASTArrayDimsAndInits
				|| node.jjtGetNumChildren() >= 3 && node.jjtGetChild(2) instanceof ASTArrayDimsAndInits) {
			//
			// Classes for Array types cannot be found directly using reflection.
			// As far as I can tell you have to create an array instance of the necessary
			// dimensionality, and then ask for the type from the instance.  OMFG that's ugly.
			//

			// TODO Need to create utility method to allow array type creation which will use
			// caching to avoid repeated object creation.
			// TODO Modify Parser to tell us array dimensions count.
			// TODO Parser seems to do some work to handle arrays in certain case already.
			// Examine those to figure out what's going on, make sure _all_ array scenarios
			// are ultimately covered.  Appears to use a Dimensionable interface to handle
			// only a part of the APIs (not bump), but is implemented several times, so
			// look at refactoring to eliminate duplication.  Dimensionable is also used
			// on AccessNodes for some scenarios, need to account for that.  Might be
			// missing some TypeNode candidates we can add to the AST and have to deal
			// with here (e.g. FormalParameter)?  Plus some existing usages may be
			// incorrect.
		} else {
			rollupTypeUnary(node);
		}
		return data;
	}

	@Override
	public Object visit(ASTStatementExpression node, Object data) {
		super.visit(node, data);
		rollupTypeUnary(node);
		return data;
	}

    @Override
    public Object visit(ASTNormalAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTMarkerAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

    @Override
    public Object visit(ASTSingleMemberAnnotation node, Object data) {
        super.visit(node, data);
        rollupTypeUnary(node);
        return data;
    }

	// Roll up the type based on type of the first child node.
	private void rollupTypeUnary(TypeNode typeNode) {
		if (typeNode instanceof Node) {
			Node node = (Node)typeNode;
			if (node.jjtGetNumChildren() >= 1) {
				Node child = node.jjtGetChild(0);
				if (child instanceof TypeNode) {
					typeNode.setType(((TypeNode)child).getType());
				}
			}
		}
	}

	// Roll up the type based on type of the first child node using Unary Numeric Promotion per JLS 5.6.1
	private void rollupTypeUnaryNumericPromotion(TypeNode typeNode) {
		if (typeNode instanceof Node) {
			Node node = (Node)typeNode;
			if (node.jjtGetNumChildren() >= 1) {
				Node child = node.jjtGetChild(0);
				if (child instanceof TypeNode) {
					Class<?> type = ((TypeNode)child).getType();
					if (type != null) {
						if ("byte".equals(type.getName()) || "short".equals(type.getName())
								|| "char".equals(type.getName())) {
							populateType(typeNode, "int");
						} else {
							typeNode.setType(((TypeNode)child).getType());
						}
					}
				}
			}
		}
	}

	// Roll up the type based on type of the first and second child nodes using Binary Numeric Promotion per JLS 5.6.2
	private void rollupTypeBinaryNumericPromotion(TypeNode typeNode) {
		if (typeNode instanceof Node) {
			Node node = (Node)typeNode;
			if (node.jjtGetNumChildren() >= 2) {
				Node child1 = node.jjtGetChild(0);
				Node child2 = node.jjtGetChild(1);
				if (child1 instanceof TypeNode && child2 instanceof TypeNode) {
					Class<?> type1 = ((TypeNode)child1).getType();
					Class<?> type2 = ((TypeNode)child2).getType();
					if (type1 != null && type2 != null) {
						// Yeah, String is not numeric, but easiest place to handle it, only affects ASTAdditiveExpression
						if ("java.lang.String".equals(type1.getName()) || "java.lang.String".equals(type2.getName())) {
							populateType(typeNode, "java.lang.String");
						} else if ("boolean".equals(type1.getName()) || "boolean".equals(type2.getName())) {
							populateType(typeNode, "boolean");
						} else if ("double".equals(type1.getName()) || "double".equals(type2.getName())) {
							populateType(typeNode, "double");
						} else if ("float".equals(type1.getName()) || "float".equals(type2.getName())) {
							populateType(typeNode, "float");
						} else if ("long".equals(type1.getName()) || "long".equals(type2.getName())) {
							populateType(typeNode, "long");
						} else {
							populateType(typeNode, "int");
						}
					} else if (type1 != null || type2 != null) {
						// If one side is known to be a String, then the result is a String
						// Yeah, String is not numeric, but easiest place to handle it, only affects ASTAdditiveExpression
						if (type1 != null && "java.lang.String".equals(type1.getName())
								|| type2 != null && "java.lang.String".equals(type2.getName())) {
							populateType(typeNode, "java.lang.String");
						}
					}
				}
			}
		}
	}

	private void populateType(TypeNode node, String className) {

		String qualifiedName = className;
		Class<?> myType = PRIMITIVE_TYPES.get(className);
		if (myType == null && importedClasses != null) {
			if (importedClasses.containsKey(className)) {
				qualifiedName = importedClasses.get(className);
			} else if (importedClasses.containsValue(className)) {
				qualifiedName = className;
			}
			if (qualifiedName != null) {
				try {
					/*
					 * TODO - the map right now contains just class names. if we
					 * use a map of classname/class then we don't have to hit
					 * the class loader for every type - much faster
					 */
					myType = pmdClassLoader.loadClass(qualifiedName);
				} catch (ClassNotFoundException e) {
					myType = processOnDemand(qualifiedName);
				} catch (LinkageError e) {
					myType = processOnDemand(qualifiedName);
				}
			}
		}
		if (myType == null && qualifiedName != null && !qualifiedName.contains(".")) {
		    // try again with java.lang....
		    try {
		        myType = pmdClassLoader.loadClass("java.lang." + qualifiedName);
		    } catch (Exception e) {
		        // ignored
		    }
		}
		if (myType != null) {
			node.setType(myType);
		}
	}

	/**
	 * Check whether the supplied class name exists.
	 */
	public boolean classNameExists(String fullyQualifiedClassName) {
		try {
			pmdClassLoader.loadClass(fullyQualifiedClassName);
			return true; //Class found
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	private Class<?> processOnDemand(String qualifiedName) {
		for (String entry : importedOnDemand) {
			try {
				return pmdClassLoader.loadClass(entry + "." + qualifiedName);
			} catch (Throwable e) {
			}
		}
		return null;
	}

	private String getClassName(ASTCompilationUnit node) {
		ASTClassOrInterfaceDeclaration classDecl = node.getFirstDescendantOfType(ASTClassOrInterfaceDeclaration.class);
		if (classDecl == null) {
			return null; // Happens if this compilation unit only contains an enum
		}
		if (node.declarationsAreInDefaultPackage()) {
			return classDecl.getImage();
		}
		ASTPackageDeclaration pkgDecl = node.getPackageDeclaration();
		importedOnDemand.add(pkgDecl.getPackageNameImage());
		return pkgDecl.getPackageNameImage() + "." + classDecl.getImage();
	}

	/**
	 * If the outer class wasn't found then we'll get in here
	 *
	 * @param node
	 */
	private void populateImports(ASTCompilationUnit node) {
		List<ASTImportDeclaration> theImportDeclarations = node.findChildrenOfType(ASTImportDeclaration.class);

		importedClasses.putAll(JAVA_LANG);

		// go through the imports
		for (ASTImportDeclaration anImportDeclaration : theImportDeclarations) {
			String strPackage = anImportDeclaration.getPackageName();
			if (anImportDeclaration.isImportOnDemand()) {
				importedOnDemand.add(strPackage);
			} else if (!anImportDeclaration.isImportOnDemand()) {
				String strName = anImportDeclaration.getImportedName();
				importedClasses.put(strName, strName);
				importedClasses.put(strName.substring(strPackage.length() + 1), strName);
			}
		}
	}

	private void populateClassName(ASTCompilationUnit node, String className) throws ClassNotFoundException {
		node.setType(pmdClassLoader.loadClass(className));
		importedClasses.putAll(pmdClassLoader.getImportedClasses(className));
	}

}
