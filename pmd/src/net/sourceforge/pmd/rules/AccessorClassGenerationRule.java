/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNestedClassDeclaration;
import net.sourceforge.pmd.ast.ASTNestedInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 1. Note all private constructors.
 * 2. Note all instantiations from outside of the class by way of the private
 *    constructor.
 * 3. Flag instantiations.
 *
 *
 * Parameter types can not be matched because they can come as exposed members
 * of classes.  In this case we have no way to know what the type is.  We can
 * make a best effort though which can filter some?
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 * @author David Konecny (david.konecny@)
 */
public class AccessorClassGenerationRule extends AbstractRule {
    private int classID = -1;
    private List classDataList;
    private String packageName;

    private ClassData getCurrentClassData() {
        return (ClassData) classDataList.get(classID);
    }

    private void setClassID(int ID) {
        classID = ID;
    }

    private int getClassID() {
        return classID;
    }

    private String getPackageName() {
        return packageName;
    }

    //remove = Fire.
    //value = someFire.Fighter
    //        0123456789012345
    //index = 4
    //remove.size() = 5
    //value.substring(0,4) = some
    //value.substring(4 + remove.size()) = Fighter
    //return "someFighter"
    private static String stripString(String remove, String value) {
        String returnValue;
        int index = value.indexOf(remove);
        if (index != -1) {	//if the package name can start anywhere but 0 plese inform the author because this will break
            returnValue = value.substring(0, index) + value.substring(index + remove.length());
        } else {
            returnValue = value;
        }
        return returnValue;
    }

    /**
     *
     */
    private class ClassData {
        /** The name of this class */
        private String m_ClassName;
        /** List of private constructors within this class */
        private List m_PrivateConstructors;
        /** List of instantiations of objects within this class */
        private List m_Instantiations;
        /** List of outer class names that exist above this class */
        private List m_ClassQualifyingNames;

        public ClassData(String className) {
            m_ClassName = className;
            m_PrivateConstructors = new ArrayList();
            m_Instantiations = new ArrayList();
            m_ClassQualifyingNames = new ArrayList();
        }

        public void addInstantiation(AllocData ad) {
            m_Instantiations.add(ad);
        }

        public Iterator getInstantiationIterator() {
            return m_Instantiations.iterator();
        }

        public void addConstructor(ASTConstructorDeclaration cd) {
            m_PrivateConstructors.add(cd);
        }

        public Iterator getPrivateConstructorIterator() {
            return m_PrivateConstructors.iterator();
        }

        public String getClassName() {
            return m_ClassName;
        }

        public void addClassQualifyingName(String name) {
            m_ClassQualifyingNames.add(name);
        }

        public Iterator getClassQualifyingNames() {
            return m_ClassQualifyingNames.iterator();
        }

        public List getClassQualifyingNamesList() {
            return m_ClassQualifyingNames;
        }
    }

    private static class AllocData {
        private String m_Name;
        private int m_ArgumentCount;
        private ASTAllocationExpression m_ASTAllocationExpression;
        private boolean isArray = false;

        public AllocData(ASTAllocationExpression node, String aPackageName, List classQualifyingNames) {
            if (node.jjtGetChild(1) instanceof ASTArguments) {
                ASTArguments aa = (ASTArguments) node.jjtGetChild(1);
                m_ArgumentCount = aa.getArgumentCount();
                //Get name and strip off all superfluous data
                //strip off package name if it is current package
                ASTName an = (ASTName) node.jjtGetChild(0);
                m_Name = stripString(aPackageName + ".", an.getImage());

                //strip off outer class names
                //try OuterClass, then try OuterClass.InnerClass, then try OuterClass.InnerClass.InnerClass2, etc...
                STRIPPING: {
                    String findName = "";
                    for (ListIterator li = classQualifyingNames.listIterator(classQualifyingNames.size()); li.hasPrevious();) {
                        String aName = (String) li.previous();
                        findName = aName + "." + findName;
                        if (m_Name.startsWith(findName)) {
                            //strip off name and exit
                            m_Name = m_Name.substring(findName.length());
                            break;
                        }
                    }
                }
            } else if (node.jjtGetChild(1) instanceof ASTArrayDimsAndInits) {
                //this is incomplete because I dont need it.
                //				child 0 could be primitive or object (ASTName or ASTPrimitiveType)
                isArray = true;
            }
            m_ASTAllocationExpression = node;
        }

        public String getName() {
            return m_Name;
        }

        public int getArgumentCount() {
            return m_ArgumentCount;
        }

        public void show() {
            System.out.println("AllocData: " + getName() + " arguments= " + getArgumentCount());
        }

        public ASTAllocationExpression getASTAllocationExpression() {
            return m_ASTAllocationExpression;
        }

        public boolean isArray() {
            return isArray;
        }
    }

    /**
     * Work on each file independently.
     * Assume a new AccessorClassGenerationRule object is created for each run?
     */
    public Object visit(ASTCompilationUnit node, Object data) {
        classDataList = new ArrayList();
        return super.visit(node, data);
    }

    private void processRule(RuleContext ctx) {
        //check constructors of outerIterator
        //against allocations of innerIterator
        for (Iterator outerIterator = classDataList.iterator(); outerIterator.hasNext();) {

            ClassData outerDataSet = (ClassData) outerIterator.next();
            for (Iterator constructors = outerDataSet.getPrivateConstructorIterator(); constructors.hasNext();) {
                ASTConstructorDeclaration cd = (ASTConstructorDeclaration) constructors.next();

                for (Iterator innerIterator = classDataList.iterator(); innerIterator.hasNext();) {
                    ClassData innerDataSet = (ClassData) innerIterator.next();
                    if (outerDataSet == innerDataSet) {
                        continue;
                    }
                    for (Iterator allocations = innerDataSet.getInstantiationIterator(); allocations.hasNext();) {
                        AllocData ad = (AllocData) allocations.next();
                        //if the constructor matches the instantiation
                        //flag the instantiation as a generator of an extra class

                        if (outerDataSet.getClassName().equals(ad.getName()) && (cd.getParameterCount() == ad.getArgumentCount())) {
                            ctx.getReport().addRuleViolation(createRuleViolation(ctx, ad.getASTAllocationExpression().getBeginLine()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Store package name to strip off in case necessary
     */
    public Object visit(ASTPackageDeclaration node, Object data) {
        packageName = ((ASTName) node.jjtGetChild(0)).getImage();
        //		System.out.println("Package is " + packageName);
        return super.visit(node, data);
    }

    /**
     * Outer interface visitation
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        String className = node.getUnmodifedInterfaceDeclaration().getImage();
        //		System.out.println("interface = " + className);
        classDataList.clear();
        setClassID(0);
        classDataList.add(getClassID(), new ClassData(className));
        Object o = super.visit(node, data);
        if (o != null) {
            processRule((RuleContext) o);
        } else {
            processRule((RuleContext) data);
        }
        setClassID(-1);
        return o;
    }

    /**
     * Inner interface visitation
     */
    public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
        String className = node.getUnmodifedInterfaceDeclaration().getImage();
        //		System.out.println("interface = " + className);
        int formerID = getClassID();
        setClassID(classDataList.size());
        ClassData newClassData = new ClassData(className);
        //store the names of any outer classes of this class in the classQualifyingName List
        ClassData formerClassData = (ClassData) classDataList.get(formerID);
        newClassData.addClassQualifyingName(formerClassData.getClassName());
        classDataList.add(getClassID(), newClassData);
        Object o = super.visit(node, data);
        setClassID(formerID);
        return o;
    }

    /**
     * Outer class declaration
     */
    public Object visit(ASTClassDeclaration node, Object data) {
        String className = ((ASTUnmodifiedClassDeclaration) node.jjtGetChild(0)).getImage();
        //		System.out.println("classname = " + className);
        classDataList.clear();
        setClassID(0);//first class
        classDataList.add(getClassID(), new ClassData(className));
        Object o = super.visit(node, data);
        if (o != null) {
            processRule((RuleContext) o);
        } else {
            processRule((RuleContext) data);
        }
        setClassID(-1);
        return o;
    }

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        String className = ((ASTUnmodifiedClassDeclaration) node.jjtGetChild(0)).getImage();
        //		System.out.println("classname = " + className);
        int formerID = getClassID();
        setClassID(classDataList.size());
        ClassData newClassData = new ClassData(className);
        //store the names of any outer classes of this class in the classQualifyingName List
        ClassData formerClassData = (ClassData) classDataList.get(formerID);
        newClassData.addClassQualifyingName(formerClassData.getClassName());
        classDataList.add(getClassID(), newClassData);
        Object o = super.visit(node, data);
        setClassID(formerID);
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
        if (classID == -1) {
            return data;
        }
        AllocData ad = new AllocData(node, getPackageName(), getCurrentClassData().getClassQualifyingNamesList());
        if (ad.isArray() == false) {
            getCurrentClassData().addInstantiation(ad);
            //ad.show();
        }
        return super.visit(node, data);
    }
}