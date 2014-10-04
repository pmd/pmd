/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;

/**
 * 1. Note all private constructors.
 * 2. Note all instantiations from outside of the class by way of the private
 * constructor.
 * 3. Flag instantiations.
 * <p/>
 * <p/>
 * Parameter types can not be matched because they can come as exposed members
 * of classes.  In this case we have no way to know what the type is.  We can
 * make a best effort though which can filter some?
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 * @author David Konecny (david.konecny@)
 * @author Romain PELISSE, belaran@gmail.com, patch bug#1807370
 */
public class AccessorClassGenerationRule extends AbstractJavaRule {

    private List<ClassData> classDataList = new ArrayList<ClassData>();
    private int classID = -1;
    private String packageName;

    public Object visit(ASTEnumDeclaration node, Object data) {
        return data;  // just skip Enums
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        classDataList.clear();
        packageName = node.getScope().getEnclosingScope(SourceFileScope.class).getPackageName();
        return super.visit(node, data);
    }

    private static class ClassData {
        private String className;
        private List<ASTConstructorDeclaration> privateConstructors;
        private List<AllocData> instantiations;
        /**
         * List of outer class names that exist above this class
         */
        private List<String> classQualifyingNames;

        public ClassData(String className) {
            this.className = className;
            this.privateConstructors = new ArrayList<ASTConstructorDeclaration>();
            this.instantiations = new ArrayList<AllocData>();
            this.classQualifyingNames = new ArrayList<String>();
        }

        public void addInstantiation(AllocData ad) {
            instantiations.add(ad);
        }

        public Iterator<AllocData> getInstantiationIterator() {
            return instantiations.iterator();
        }

        public void addConstructor(ASTConstructorDeclaration cd) {
            privateConstructors.add(cd);
        }

        public Iterator<ASTConstructorDeclaration> getPrivateConstructorIterator() {
            return privateConstructors.iterator();
        }

        public String getClassName() {
            return className;
        }

        public void addClassQualifyingName(String name) {
            classQualifyingNames.add(name);
        }

        public List<String> getClassQualifyingNamesList() {
            return classQualifyingNames;
        }
    }

    private static class AllocData {
        private String name;
        private int argumentCount;
        private ASTAllocationExpression allocationExpression;
        private boolean isArray;

        public AllocData(ASTAllocationExpression node, String aPackageName, List<String> classQualifyingNames) {
            if (node.jjtGetChild(1) instanceof ASTArguments) {
                ASTArguments aa = (ASTArguments) node.jjtGetChild(1);
                argumentCount = aa.getArgumentCount();
                //Get name and strip off all superfluous data
                //strip off package name if it is current package
                if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
                    throw new RuntimeException("BUG: Expected a ASTClassOrInterfaceType, got a " + node.jjtGetChild(0).getClass());
                }
                ASTClassOrInterfaceType an = (ASTClassOrInterfaceType) node.jjtGetChild(0);
                name = stripString(aPackageName + '.', an.getImage());

                //strip off outer class names
                //try OuterClass, then try OuterClass.InnerClass, then try OuterClass.InnerClass.InnerClass2, etc...
                String findName = "";
                for (ListIterator<String> li = classQualifyingNames.listIterator(classQualifyingNames.size()); li.hasPrevious();) {
                    String aName = li.previous();
                    findName = aName + '.' + findName;
                    if (name.startsWith(findName)) {
                        //strip off name and exit
                        name = name.substring(findName.length());
                        break;
                    }
                }
            } else if (node.jjtGetChild(1) instanceof ASTArrayDimsAndInits) {
                //this is incomplete because I dont need it.
                //				child 0 could be primitive or object (ASTName or ASTPrimitiveType)
                isArray = true;
            }
            allocationExpression = node;
        }

        public String getName() {
            return name;
        }

        public int getArgumentCount() {
            return argumentCount;
        }

        public ASTAllocationExpression getASTAllocationExpression() {
            return allocationExpression;
        }

        public boolean isArray() {
            return isArray;
        }
    }

    /**
     * Outer interface visitation
     */
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            if (!(node.jjtGetParent().jjtGetParent() instanceof ASTCompilationUnit)) {
                // not a top level interface
                String interfaceName = node.getImage();
                int formerID = getClassID();
                setClassID(classDataList.size());
                ClassData newClassData = new ClassData(interfaceName);
                //store the names of any outer classes of this class in the classQualifyingName List
                ClassData formerClassData = classDataList.get(formerID);
                newClassData.addClassQualifyingName(formerClassData.getClassName());
                classDataList.add(getClassID(), newClassData);
                Object o = super.visit(node, data);
                setClassID(formerID);
                return o;
            } else {
                String interfaceName = node.getImage();
                classDataList.clear();
                setClassID(0);
                classDataList.add(getClassID(), new ClassData(interfaceName));
                Object o = super.visit(node, data);
                if (o != null) {
                    processRule(o);
                } else {
                    processRule(data);
                }
                setClassID(-1);
                return o;
            }
        } else if (!(node.jjtGetParent().jjtGetParent() instanceof ASTCompilationUnit)) {
            // not a top level class
            String className = node.getImage();
            int formerID = getClassID();
            setClassID(classDataList.size());
            ClassData newClassData = new ClassData(className);
            // TODO
            // this is a hack to bail out here
            // but I'm not sure why this is happening
            // TODO
            if (formerID == -1 || formerID >= classDataList.size()) {
                return null;
            }
            //store the names of any outer classes of this class in the classQualifyingName List
            ClassData formerClassData = classDataList.get(formerID);
            newClassData.addClassQualifyingName(formerClassData.getClassName());
            classDataList.add(getClassID(), newClassData);
            Object o = super.visit(node, data);
            setClassID(formerID);
            return o;
        }
        // outer classes
        if ( ! node.isStatic() ) {	// See bug# 1807370
        String className = node.getImage();
        classDataList.clear();
        setClassID(0);//first class
        classDataList.add(getClassID(), new ClassData(className));
        }
        Object o = super.visit(node, data);
        if (o != null && ! node.isStatic() ) { // See bug# 1807370
            processRule(o);
        } else {
            processRule(data);
        }
        setClassID(-1);
        return o;
    }

    /**
     * Store all target constructors
     */
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.isPrivate()) {
            getCurrentClassData().addConstructor(node);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTAllocationExpression node, Object data) {
        // TODO
        // this is a hack to bail out here
        // but I'm not sure why this is happening
        // TODO
        if (classID == -1 || getCurrentClassData() == null) {
            return data;
        }
        AllocData ad = new AllocData(node, packageName, getCurrentClassData().getClassQualifyingNamesList());
        if (!ad.isArray()) {
            getCurrentClassData().addInstantiation(ad);
        }
        return super.visit(node, data);
    }

    private void processRule(Object ctx) {
        //check constructors of outerIterator against allocations of innerIterator
        for (ClassData outerDataSet : classDataList) {
            for (Iterator<ASTConstructorDeclaration> constructors = outerDataSet.getPrivateConstructorIterator(); constructors.hasNext();) {
                ASTConstructorDeclaration cd = constructors.next();

                for (ClassData innerDataSet : classDataList) {
                    if (outerDataSet == innerDataSet) {
                        continue;
                    }
                    for (Iterator<AllocData> allocations = innerDataSet.getInstantiationIterator(); allocations.hasNext();) {
                        AllocData ad = allocations.next();
                        //if the constructor matches the instantiation
                        //flag the instantiation as a generator of an extra class
                        if (outerDataSet.getClassName().equals(ad.getName()) && (cd.getParameterCount() == ad.getArgumentCount())) {
                            addViolation(ctx, ad.getASTAllocationExpression());
                        }
                    }
                }
            }
        }
    }

    private ClassData getCurrentClassData() {
        // TODO
        // this is a hack to bail out here
        // but I'm not sure why this is happening
        // TODO
        if (classID >= classDataList.size()) {
            return null;
        }
        return classDataList.get(classID);
    }

    private void setClassID(int id) {
        classID = id;
    }

    private int getClassID() {
        return classID;
    }

    //remove = Fire.
    //value = someFire.Fighter
    //        0123456789012345
    //index = 4
    //remove.size() = 5
    //value.substring(0,4) = some
    //value.substring(4 + remove.size()) = Fighter
    //return "someFighter"
    
    // TODO move this into StringUtil    
    private static String stripString(String remove, String value) {
        String returnValue;
        int index = value.indexOf(remove);
        if (index != -1) {	//if the package name can start anywhere but 0 please inform the author because this will break
            returnValue = value.substring(0, index) + value.substring(index + remove.length());
        } else {
            returnValue = value;
        }
        return returnValue;
    }

}
