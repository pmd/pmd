/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassTypeResolver extends JavaParserVisitorAdapter {

	private static Map<String, Class> myPrimitiveTypes;

	private static PMDASMClassLoader pmdClassLoader = new PMDASMClassLoader();

	static {
		Map<String, Class> thePrimitiveTypes = new HashMap<String, Class>();
		thePrimitiveTypes.put("short", Short.TYPE);
		thePrimitiveTypes.put("byte", Byte.TYPE);
		thePrimitiveTypes.put("char", Character.TYPE);
		thePrimitiveTypes.put("int", Integer.TYPE);
		thePrimitiveTypes.put("long", Long.TYPE);
		thePrimitiveTypes.put("float", Float.TYPE);
		thePrimitiveTypes.put("double", Double.TYPE);
		thePrimitiveTypes.put("boolean", Boolean.TYPE);
		thePrimitiveTypes.put("void", Void.TYPE);
		myPrimitiveTypes = Collections.unmodifiableMap(thePrimitiveTypes);
	}

	private Map<String, String> importedClasses;

	private String className;

	public Object visit(ASTCompilationUnit node, Object data) {
		try {
			populateClassName(node);
		} catch (ClassNotFoundException e) {
			populateImports(node);
		} catch (NoClassDefFoundError e) {
			populateImports(node);
		}
		return super.visit(node, data);
	}

	/**
	 * If the outer class wasn't found then we'll get in here
	 * 
	 * @param node
	 */
	private void populateImports(ASTCompilationUnit node) {
		List<ASTImportDeclaration> theImportDeclarations = node.findChildrenOfType(ASTImportDeclaration.class);
		importedClasses = new HashMap<String, String>();

		// go through the imports
		for (ASTImportDeclaration anImportDeclaration : theImportDeclarations) {
			if (!anImportDeclaration.isImportOnDemand()) {
				String strPackage = anImportDeclaration.getPackageName();
				String strName = anImportDeclaration.getImportedName();
				importedClasses.put(strName, strName);
				importedClasses.put(strName.substring(strPackage.length() + 1), strName);
			}
		}

		importedClasses.put("String", "java.lang.String");
		importedClasses.put("Object", "java.lang.Object");
	}

	private void populateClassName(ASTCompilationUnit node) throws ClassNotFoundException {
		ASTClassOrInterfaceDeclaration decl = (ASTClassOrInterfaceDeclaration) node
				.getFirstChildOfType(ASTClassOrInterfaceDeclaration.class);
		if (decl != null) {
			ASTPackageDeclaration pkgDecl = (ASTPackageDeclaration) node
					.getFirstChildOfType(ASTPackageDeclaration.class);
			className = pkgDecl == null ? decl.getImage() : ((ASTName) pkgDecl.jjtGetChild(0)).getImage() + "."
					+ decl.getImage();
			pmdClassLoader.loadClass(className);
			importedClasses = pmdClassLoader.getImportedClasses(className);
		}
	}

	public Object visit(ASTClassOrInterfaceType node, Object data) {

		String className = node.getImage();
		String qualifiedName = className;
		Class myType = myPrimitiveTypes.get(className);
		if (myType == null && importedClasses != null) {
			if (importedClasses.containsKey(className)) {
				qualifiedName = importedClasses.get(className);
			} else if (importedClasses.containsValue(className)) {
				qualifiedName = className;
			}
			if (qualifiedName != null) {
				try {
					/*
					 * TODO - the map right now contains just class names. if we use a map of
					 * classname/class then we don't have to hit the class loader for every type -
					 * much faster
					 */
					myType = pmdClassLoader.loadClass(qualifiedName);
				} catch (ClassNotFoundException e) {
					//@TODO What should we do if it's not found?
				}
			}
		}
		if (myType != null) {
			node.setType(myType);
		}
		return data;
	}
}
