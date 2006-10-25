/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
 */
public class AccessorClassGeneration extends AbstractRule {

    private List classDataList = new ArrayList();
    private int classID = -1;
    private String packageName;

    public Object visit(ASTEnumDeclaration node, Object data) {
        return data;  // just skip Enums
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        classDataList.clear();
        packageName = node.getScope().getEnclosingSourceFileScope().getPackageName();
        return super.visit(node, data);
    }

    private static class ClassData {
        private String m_ClassName;
        private List m_PrivateConstructors;
        private List m_Instantiations;
        /**
         * List of outer class names that exist above this class
         */
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

        public List getClassQualifyingNamesList() {
            return m_ClassQualifyingNames;
        }
    }

    private static class AllocData {
        private String m_Name;
        private int m_ArgumentCount;
        private ASTAllocationExpression m_ASTAllocationExpression;
        private boolean isArray;

        public AllocData(ASTAllocationExpression node, String aPackageName, List classQualifyingNames) {
            if (node.jjtGetChild(1) instanceof ASTArguments) {
                ASTArguments aa = (ASTArguments) node.jjtGetChild(1);
                m_ArgumentCount = aa.getArgumentCount();
                //Get name and strip off all superfluous data
                //strip off package name if it is current package
                if (!(node.jjtGetChild(0) instanceof ASTClassOrInterfaceType)) {
                    throw new RuntimeException("BUG: Expected a ASTClassOrInterfaceType, got a " + node.jjtGetChild(0).getClass());
                }
                ASTClassOrInterfaceType an = (ASTClassOrInterfaceType) node.jjtGetChild(0);
                m_Name = stripString(aPackageName + ".", an.getImage());

                //strip off outer class names
                //try OuterClass, then try OuterClass.InnerClass, then try OuterClass.InnerClass.InnerClass2, etc...
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

        public ASTAllocationExpression getASTAllocationExpression() {
            return m_ASTAllocationExpression;
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
                ClassData formerClassData = (ClassData) classDataList.get(formerID);
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
            ClassData formerClassData = (ClassData) classDataList.get(formerID);
            newClassData.addClassQualifyingName(formerClassData.getClassName());
            classDataList.add(getClassID(), newClassData);
            Object o = super.visit(node, data);
            setClassID(formerID);
            return o;
        }
        // outer classes
        String className = node.getImage();
        classDataList.clear();
        setClassID(0);//first class
        classDataList.add(getClassID(), new ClassData(className));
        Object o = super.visit(node, data);
        if (o != null) {
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
        return (ClassData) classDataList.get(classID);
    }

    private void setClassID(int ID) {
        classID = ID;
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
