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
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.TypeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClassTypeResolver extends JavaParserVisitorAdapter {

    private static final Logger LOG = Logger.getLogger(ClassTypeResolver.class.getName());
    private static Map<String, Class> myPrimitiveTypes;
    private static Map<String, String> myJavaLang;
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

        myJavaLang = new HashMap<String, String>();
        myJavaLang.put("Boolean", "java.lang.Boolean");
        myJavaLang.put("Byte", "java.lang.Byte");
        myJavaLang.put("Character", "java.lang.Character");
        myJavaLang.put("CharSequence", "java.lang.CharSequence");
        myJavaLang.put("Class", "java.lang.Class");
        myJavaLang.put("ClassLoader", "java.lang.ClassLoader");
        myJavaLang.put("Cloneable", "java.lang.Cloneable");
        myJavaLang.put("Comparable", "java.lang.Comparable");
        myJavaLang.put("Compiler", "java.lang.Compiler");
        myJavaLang.put("Double", "java.lang.Double");
        myJavaLang.put("Float", "java.lang.Float");
        myJavaLang.put("InheritableThreadLocal", "java.lang.InheritableThreadLocal");
        myJavaLang.put("Integer", "java.lang.Integer");
        myJavaLang.put("Long", "java.lang.Long");
        myJavaLang.put("Math", "java.lang.Math");
        myJavaLang.put("Number", "java.lang.Number");
        myJavaLang.put("Object", "java.lang.Object");
        myJavaLang.put("Package", "java.lang.Package");
        myJavaLang.put("Process", "java.lang.Process");
        myJavaLang.put("Runnable", "java.lang.Runnable");
        myJavaLang.put("Runtime", "java.lang.Runtime");
        myJavaLang.put("RuntimePermission", "java.lang.RuntimePermission");
        myJavaLang.put("SecurityManager", "java.lang.SecurityManager");
        myJavaLang.put("Short", "java.lang.Short");
        myJavaLang.put("StackTraceElement", "java.lang.StackTraceElement");
        myJavaLang.put("StrictMath", "java.lang.StrictMath");
        myJavaLang.put("String", "java.lang.String");
        myJavaLang.put("StringBuffer", "java.lang.StringBuffer");
        myJavaLang.put("System", "java.lang.System");
        myJavaLang.put("Thread", "java.lang.Thread");
        myJavaLang.put("ThreadGroup", "java.lang.ThreadGroup");
        myJavaLang.put("ThreadLocal", "java.lang.ThreadLocal");
        myJavaLang.put("Throwable", "java.lang.Throwable");
        myJavaLang.put("Void", "java.lang.Void");

    }

    private Map<String, String> importedClasses;
    private List<String> importedOnDemand;

    public Object visit(ASTCompilationUnit node, Object data) {
        String className = null;
        try {
            importedOnDemand = new ArrayList<String>();
            className = getClassName(node);
            if (className != null) {
                populateClassName(node, className);
            }
        } catch (ClassNotFoundException e) {
            LOG.log(Level.FINE, "Could not find class " + className);
        } catch (NoClassDefFoundError e) {
            LOG.log(Level.WARNING, "Could not find class " + className);
        } finally {
            populateImports(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTImportDeclaration node, Object data) {
        ASTName importedType = (ASTName) node.jjtGetChild(0);
        if(importedType.getType() != null){
           node.setType(importedType.getType()); 
        } else {
            populateType(node, importedType.getImage());
        }
        
        if(node.getType() != null){
            node.setPackage(node.getType().getPackage());
        }
        return data;
    }

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        populateType(node, node.getImage());
        return data;
    }

    public Object visit(ASTName node, Object data) {
        /*
         * Only doing this for nodes where getNameDeclaration is null this means
         * it's not a named node, i.e. Static reference or Annotation Doing this
         * for memory - TODO: Investigate if there is a valid memory concern or
         * not
         */
        if (node.getNameDeclaration() == null) {
            String name = node.getImage();
            if (name.indexOf('.') != -1) {
                name = name.substring(0, name.indexOf('.'));
            }
            populateType(node, name);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node == null || node.getNameDeclaration() == null) {
            return super.visit(node, data);
        }
        String name = node.getNameDeclaration().getTypeImage();
        if (name.indexOf('.') != -1) {
            name = name.substring(0, name.indexOf('.'));
        }
        populateType(node, name);
        return super.visit(node, data);
    }

    private void populateType(TypeNode node, String className) {

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
                     * TODO - the map right now contains just class names. if we
                     * use a map of classname/class then we don't have to hit
                     * the class loader for every type - much faster
                     */
                    myType = pmdClassLoader.loadClass(qualifiedName);
                } catch (ClassNotFoundException e) {
                    myType = processOnDemand(qualifiedName);
                }
            }
        }
        if (myType != null) {
            node.setType(myType);
        }
    }
    
    /**
     * Check whether the supplied class name exists.
     */
    public static boolean classNameExists(String fullyQualifiedClassName) {
        try {
            pmdClassLoader.loadClass(fullyQualifiedClassName);
            return true;	//Class found
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private Class processOnDemand(String qualifiedName) {
        for (String entry : importedOnDemand) {
            try {
                return pmdClassLoader.loadClass(entry + "." + qualifiedName);
            } catch (Throwable e) {
            }
        }
        return null;
    }

    private String getClassName(ASTCompilationUnit node) {
        ASTClassOrInterfaceDeclaration classDecl = node.getFirstChildOfType(ASTClassOrInterfaceDeclaration.class);
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
        importedClasses = new HashMap<String, String>();

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

        importedClasses.putAll(myJavaLang);
    }

    private void populateClassName(ASTCompilationUnit node, String className) throws ClassNotFoundException {
        node.setType(pmdClassLoader.loadClass(className));
        importedClasses = pmdClassLoader.getImportedClasses(className);
    }


}
