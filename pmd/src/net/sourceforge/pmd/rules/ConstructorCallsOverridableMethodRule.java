/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNestedClassDeclaration;
import net.sourceforge.pmd.ast.ASTNestedInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Searches through all methods and constructors called from constructors.  It
 * marks as dangerous any call to overridable methods from non-private
 * constructors.  It marks as dangerous any calls to dangerous private constructors
 * from non-private constructors.
 *
 *
 * @todo match parameter types.  Agressive strips off any package names.  Normal
 *       compares the names as is.
 *
 * @todo What about interface declarations which can have internal classes
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 */
public final class ConstructorCallsOverridableMethodRule extends net.sourceforge.pmd.AbstractRule {
    /**
     *		2: method();
     *			ASTPrimaryPrefix
     *				ASTName			image = "method"
     *			ASTPrimarySuffix
     *				*ASTArguments
     *		3: a.method();
     *			ASTPrimaryPrefix ->
     *				ASTName			image = "a.method" ???
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *		3: this.method();
     *			ASTPrimaryPrefix -> this image=null
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *
     *          super.method();
     *			ASTPrimaryPrefix -> image = "method"
     *			ASTPrimarySuffix -> image = null
     *				ASTArguments ->
     *
     *          super.a.method();
     *			ASTPrimaryPrefix -> image = "a"
     *			ASTPrimarySuffix -> image = "method"
     *			ASTPrimarySuffix -> image = null
     *				ASTArguments ->

     *
     *		4: this.a.method();
     *			ASTPrimaryPrefix -> image = null
     *			ASTPrimarySuffix -> image = "a"
     *			ASTPrimarySuffix -> image = "method"
     *			ASTPrimarySuffix ->
     *				ASTArguments
     *
     *      4: ClassName.this.method();
     *			ASTPrimaryPrefix
     *				ASTName	image = "ClassName"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> image = "method"
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *		5: ClassName.this.a.method();
     *			ASTPrimaryPrefix
     *				ASTName image = "ClassName"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> image="a"
     *			ASTPrimarySuffix -> image="method"
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      5: Package.ClassName.this.method();
     *			ASTPrimaryPrefix
     *				ASTName image ="Package.ClassName"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> image="method"
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      6: Package.ClassName.this.a.method();
     *			ASTPrimaryPrefix
     *				ASTName image ="Package.ClassName"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      5: OuterClass.InnerClass.this.method();
     *			ASTPrimaryPrefix
     *				ASTName image = "OuterClass.InnerClass"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      6: OuterClass.InnerClass.this.a.method();
     *			ASTPrimaryPrefix
     *				ASTName image = "OuterClass.InnerClass"
     *			ASTPrimarySuffix -> this image=null
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *
     *			OuterClass.InnerClass.this.a.method().method().method();
     *			ASTPrimaryPrefix
     *				ASTName image = "OuterClass.InnerClass"
     *			ASTPrimarySuffix -> this		image=null
     *			ASTPrimarySuffix -> a			image='a'
     *			ASTPrimarySuffix -> method		image='method'
     *			ASTPrimarySuffix -> ()			image=null
     *				ASTArguments
     *			ASTPrimarySuffix -> method		image='method'
     *			ASTPrimarySuffix -> ()			image=null
     *				ASTArguments
     *			ASTPrimarySuffix -> method		image='method'
     *			ASTPrimarySuffix -> ()			image=null
     *				ASTArguments
     *
     *      3..n:	Class.InnerClass[0].InnerClass[n].this.method();
     *				ASTPrimaryPrefix
     *					ASTName image = "Class[0]..InnerClass[n]"
     *				ASTPrimarySuffix -> image=null
     *				ASTPrimarySuffix -> method
     *				ASTPrimarySuffix -> ()
     *					ASTArguments
     *
     *		super.aMethod();
     *			ASTPrimaryPrefix -> aMethod
     *			ASTPrimarySuffix -> ()
     *
     *		Evaluate right to left
     *
     */
    private static class MethodInvocation {
        private String m_Name;
        private ASTPrimaryExpression m_Ape;
        private List m_ReferenceNames;
        private List m_QualifierNames;
        private int m_ArgumentSize;
        private boolean m_Super;

        private MethodInvocation(ASTPrimaryExpression ape, List qualifierNames, List referenceNames, String name, int argumentSize, boolean superCall) {
            m_Ape = ape;
            m_QualifierNames = qualifierNames;
            m_ReferenceNames = referenceNames;
            m_Name = name;
            m_ArgumentSize = argumentSize;
            m_Super = superCall;
        }

        public boolean isSuper() {
            return m_Super;
        }

        public String getName() {
            return m_Name;
        }

        public int getArgumentCount() {
            return m_ArgumentSize;
        }

        public List getReferenceNames() {
            return m_ReferenceNames;//new ArrayList(variableNames);
        }

        public List getQualifierNames() {
            return m_QualifierNames;
        }

        public ASTPrimaryExpression getASTPrimaryExpression() {
            return m_Ape;
        }

        public static MethodInvocation getMethod(ASTPrimaryExpression node) {
            MethodInvocation meth = null;
            int i = node.jjtGetNumChildren();
            if (i > 1) {//should always be at least 2, probably can eliminate this check
                //start at end which is guaranteed, work backwards
                Node lastNode = node.jjtGetChild(i - 1);
                if ((lastNode.jjtGetNumChildren() == 1) && (lastNode.jjtGetChild(0) instanceof ASTArguments)) { //could be ASTExpression for instance 'a[4] = 5';
                    //start putting method together
                    //					System.out.println("Putting method together now");
                    List varNames = new ArrayList();
                    List packagesAndClasses = new ArrayList(); //look in JLS for better name here;
                    String methodName = null;
                    ASTArguments args = (ASTArguments) lastNode.jjtGetChild(0);
                    int numOfArguments = args.getArgumentCount();
                    boolean superFirst = false;
                    int thisIndex = -1;

                    FIND_SUPER_OR_THIS: {
                        //search all nodes except last for 'this' or 'super'.  will be at: (node 0 | node 1 | nowhere)
                        //this is an ASTPrimarySuffix with a null image and does not have child (which will be of type ASTArguments)
                        //this is an ASTPrimaryPrefix with a null image and an ASTName that has a null image
                        //super is an ASTPrimarySuffix with a null image and does not have child (which will be of type ASTArguments)
                        //super is an ASTPrimaryPrefix with a non-null image
                        for (int x = 0; x < i - 1; x++) {
                            Node child = node.jjtGetChild(x);
                            if (child instanceof ASTPrimarySuffix) { //check suffix type match
                                ASTPrimarySuffix child2 = (ASTPrimarySuffix) child;
                                //								String name = getNameFromSuffix((ASTPrimarySuffix)child);
                                //								System.out.println("found name suffix of : " + name);
                                if (child2.getImage() == null && child2.jjtGetNumChildren() == 0) {
                                    thisIndex = x;
                                    break;
                                }
                                //could be super, could be this.  currently we cant tell difference so we miss super when
                                //XYZ.ClassName.super.method();
                                //still works though.
                            } else if (child instanceof ASTPrimaryPrefix) { //check prefix type match
                                ASTPrimaryPrefix child2 = (ASTPrimaryPrefix) child;
                                if (getNameFromPrefix(child2) == null) {
                                    if (child2.getImage() == null) {
                                        thisIndex = x;
                                        break;
                                    } else {//happens when super is used [super.method(): image = 'method']
                                        superFirst = true;
                                        thisIndex = x;
                                        //the true super is at an unusable index because super.method() has only 2 nodes [method=0,()=1]
                                        //as opposed to the 3 you might expect and which this.method() actually has. [this=0,method=1.()=2]
                                        break;
                                    }
                                }
                            }
                            //							else{
                            //								System.err.println("Bad Format error"); //throw exception, quit evaluating this compilation node
                            //							}
                        }
                    }

                    if (thisIndex != -1) {
                        //						System.out.println("Found this or super: " + thisIndex);
                        //Hack that must be removed if and when the patters of super.method() begins to logically match the rest of the patterns !!!
                        if (superFirst) { //this is when super is the first node of statement.  no qualifiers, all variables or method
                            //							System.out.println("super first");
                            FIRSTNODE:{
                                ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                                String name = child.getImage();//special case
                                if (i == 2) { //last named node = method name
                                    methodName = name;
                                } else { //not the last named node so its only var name
                                    varNames.add(name);
                                }
                            }
                            OTHERNODES:{ //variables
                                for (int x = 1; x < i - 1; x++) {
                                    Node child = node.jjtGetChild(x);
                                    ASTPrimarySuffix ps = (ASTPrimarySuffix) child;
                                    if (ps.isArguments() == false) {
                                        String name = ((ASTPrimarySuffix) child).getImage();
                                        if (x == i - 2) {//last node
                                            methodName = name;
                                        } else {//not the last named node so its only var name
                                            varNames.add(name);
                                        }
                                    }
                                }
                            }
                        } else {//not super call
                            FIRSTNODE:{
                                if (thisIndex == 1) {//qualifiers in node 0
                                    ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                                    String toParse = getNameFromPrefix(child);
                                    //									System.out.println("parsing for class/package names in : " + toParse);
                                    java.util.StringTokenizer st = new java.util.StringTokenizer(toParse, ".");
                                    while (st.hasMoreTokens()) {
                                        packagesAndClasses.add(st.nextToken());
                                    }
                                }
                            }
                            OTHERNODES:{ //other methods called in this statement are grabbed here
                                //this is at 0, then no Qualifiers
                                //this is at 1, the node 0 contains qualifiers
                                for (int x = thisIndex + 1; x < i - 1; x++) {//everything after this is var name or method name
                                    ASTPrimarySuffix child = (ASTPrimarySuffix) node.jjtGetChild(x);
                                    if (child.isArguments() == false) { //skip the () of method calls
                                        String name = getNameFromSuffix(child);
                                        //										System.out.println("Found suffix: " + suffixName);
                                        if (x == i - 2) {
                                            methodName = name;
                                        } else {
                                            varNames.add(name);
                                        }
                                    }
                                }
                            }
                        }
                    } else { //if no this or super found, everything is method name or variable
                        //System.out.println("no this found:");
                        FIRSTNODE:{ //variable names are in the prefix + the first method call [a.b.c.x()]
                            ASTPrimaryPrefix child = (ASTPrimaryPrefix) node.jjtGetChild(0);
                            String toParse = getNameFromPrefix(child);
                            //							System.out.println("parsing for var names in : " + toParse);
                            java.util.StringTokenizer st = new java.util.StringTokenizer(toParse, ".");
                            while (st.hasMoreTokens()) {
                                String value = st.nextToken();
                                if (!st.hasMoreTokens()) {
                                    if (i == 2) {//if this expression is 2 nodes long, then the last part of prefix is method name
                                        methodName = value;
                                    } else {
                                        varNames.add(value);
                                    }
                                } else { //variable name
                                    varNames.add(value);
                                }
                            }
                        }
                        OTHERNODES:{ //other methods called in this statement are grabbed here
                            for (int x = 1; x < i - 1; x++) {
                                ASTPrimarySuffix child = (ASTPrimarySuffix) node.jjtGetChild(x);
                                if (child.isArguments() == false) {
                                    String name = getNameFromSuffix(child);
                                    if (x == i - 2) {
                                        methodName = name;
                                    } else {
                                        varNames.add(name);
                                    }
                                }
                            }
                        }
                    }
                    meth = new MethodInvocation(node, packagesAndClasses, varNames, methodName, numOfArguments, superFirst);
                }
            }
            return meth;
        }

        public void show() {
            System.out.println("<MethodInvocation>");
            List pkg = getQualifierNames();
            System.out.println("  <Qualifiers>");
            for (Iterator it = pkg.iterator(); it.hasNext();) {
                String name = (String) it.next();
                System.out.println("    " + name);
            }
            System.out.println("  </Qualifiers>");
            System.out.println("  <Super>" + isSuper() + "</Super>");
            List vars = getReferenceNames();
            System.out.println("  <References>");
            for (Iterator it = vars.iterator(); it.hasNext();) {
                String name = (String) it.next();
                System.out.println("    " + name);
            }
            System.out.println("  </References>");
            System.out.println("  <Name>" + getName() + "</Name>");
            System.out.println("</MethodInvocation>");
        }
    }

    private final class ConstructorInvocation {
        private ASTExplicitConstructorInvocation m_Eci;
        private String name;
        private int count = 0;

        public ConstructorInvocation(ASTExplicitConstructorInvocation eci) {
            m_Eci = eci;
            List l = new ArrayList();
            eci.findChildrenOfType(ASTArguments.class, l);
            if (l.size() > 0) {
                ASTArguments aa = (ASTArguments) l.get(0);
                count = aa.getArgumentCount();
            }
            name = eci.getImage();
        }

        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation() {
            return m_Eci;
        }

        public int getArgumentCount() {
            return count;
        }

        public String getName() {
            return name;
        }
    }

    private final class MethodHolder {
        private ASTMethodDeclarator m_Amd;
        private boolean m_Dangerous = false;

        public MethodHolder(ASTMethodDeclarator amd) {
            m_Amd = amd;
        }

        public ASTMethodDeclarator getASTMethodDeclarator() {
            return m_Amd;
        }

        public boolean isDangerous() {
            return m_Dangerous;
        }

        public void setDangerous(boolean dangerous) {
            m_Dangerous = dangerous;
        }
    }

    private final class ConstructorHolder {
        private ASTConstructorDeclaration m_Cd;
        private boolean m_Dangerous = false;
        private ConstructorInvocation m_Ci;
        private boolean m_CiInitialized = false;

        public ConstructorHolder(ASTConstructorDeclaration cd) {
            m_Cd = cd;
        }

        public ASTConstructorDeclaration getASTConstructorDeclaration() {
            return m_Cd;
        }

        public ConstructorInvocation getCalledConstructor() {
            if (m_CiInitialized == false) {
                initCI();
            }
            return m_Ci;
        }

        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation() {
            ASTExplicitConstructorInvocation eci = null;
            if (m_CiInitialized == false) {
                initCI();
            }
            if (m_Ci != null) {
                eci = m_Ci.getASTExplicitConstructorInvocation();
            }
            return eci;
        }

        private void initCI() {
            List expressions = new ArrayList();
            m_Cd.findChildrenOfType(ASTExplicitConstructorInvocation.class, expressions); //only 1...
            if (expressions.size() > 0) {
                ASTExplicitConstructorInvocation eci = (ASTExplicitConstructorInvocation) expressions.get(0);
                m_Ci = new ConstructorInvocation(eci);
                //System.out.println("Const call " + eci.getImage()); //super or this???
            }
            m_CiInitialized = true;
        }

        public boolean isDangerous() {
            return m_Dangerous;
        }

        public void setDangerous(boolean dangerous) {
            m_Dangerous = dangerous;
        }
    }

    /**
     * 1 package per class. holds info for evaluating a single class.
     */
    private static class EvalPackage {
        public EvalPackage() {
        }

        public EvalPackage(String className) {
            m_ClassName = className;
            calledMethods = new ArrayList();//meths called from constructor
            allMethodsOfClass = new HashMap();
            calledConstructors = new ArrayList();//all constructors called from constructor
            allPrivateConstructorsOfClass = new HashMap();
        }

        public String m_ClassName;
        public List calledMethods;
        public Map allMethodsOfClass;

        public List calledConstructors;
        public Map allPrivateConstructorsOfClass;
    }

    private static final class NullEvalPackage extends EvalPackage {
        public NullEvalPackage() {
            m_ClassName = "";
            calledMethods = Collections.EMPTY_LIST;
            allMethodsOfClass = Collections.EMPTY_MAP;
            calledConstructors = Collections.EMPTY_LIST;
            allPrivateConstructorsOfClass = Collections.EMPTY_MAP;
        }
    }

    private static final NullEvalPackage nullEvalPackage = new NullEvalPackage();


    /**
     * 1 package per class.
     */
    private final List evalPackages = new ArrayList();//could use java.util.Stack

    private EvalPackage getCurrentEvalPackage() {
        return (EvalPackage) evalPackages.get(evalPackages.size() - 1);
    }

    /**
     * Adds and evaluation package and makes it current
     */
    private void putEvalPackage(EvalPackage ep) {
        evalPackages.add(ep);
    }

    private void removeCurrentEvalPackage() {
        evalPackages.remove(evalPackages.size() - 1);
    }

    private void clearEvalPackages() {
        evalPackages.clear();
    }

    /**
     * This check must be evaluated independelty for each class.  Inner classses
     * get their own EvalPackage in order to perform independent evaluation.
     */
    private Object visitClassDec(AccessNode node, Object data) {
        String className = ((ASTUnmodifiedClassDeclaration) node.jjtGetChild(0)).getImage();
        //		System.out.println("Class is " + className);
        //evaluate each level independently
        if (!node.isFinal() && !node.isStatic()) {
            putEvalPackage(new EvalPackage(className));
        } else {
            putEvalPackage(nullEvalPackage);
        }
        //store any errors caught from other passes.
        if (node instanceof ASTClassDeclaration) {
            super.visit((ASTClassDeclaration) node, data);
        } else {
            super.visit((ASTNestedClassDeclaration) node, data);
        }
        //skip this class if it has no evaluation package
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {
            //evaluate danger of all methods in class
            while (evaluateDangerOfMethods(getCurrentEvalPackage().allMethodsOfClass) == true)
                ;
            //evaluate danger of constructors
            evaluateDangerOfConstructors1(getCurrentEvalPackage().allPrivateConstructorsOfClass, getCurrentEvalPackage().allMethodsOfClass.keySet());
            while (evaluateDangerOfConstructors2(getCurrentEvalPackage().allPrivateConstructorsOfClass) == true)
                ;

            //get each method called on this object from a non-private constructor, if its dangerous flag it
            for (Iterator it = getCurrentEvalPackage().calledMethods.iterator(); it.hasNext();) {
                MethodInvocation meth = (MethodInvocation) it.next();
                //check against each dangerous method in class
                for (Iterator it2 = getCurrentEvalPackage().allMethodsOfClass.keySet().iterator(); it2.hasNext();) {
                    MethodHolder h = (MethodHolder) it2.next();
                    if (h.isDangerous()) {
                        String methName = h.getASTMethodDeclarator().getImage();
                        int count = h.getASTMethodDeclarator().getParameterCount();
                        if (meth.getName().equals(methName) && (meth.getArgumentCount() == count)) {
                            //bad call
                            RuleContext ctx = (RuleContext) data;
                            ctx.getReport().addRuleViolation(createRuleViolation(ctx, meth.getASTPrimaryExpression().getBeginLine()));
                        }
                    }
                }
            }
            //get each unsafe private constructor, and check if its called from any non private constructors
            for (Iterator privConstIter = getCurrentEvalPackage().allPrivateConstructorsOfClass.keySet().iterator(); privConstIter.hasNext();) {
                ConstructorHolder ch = (ConstructorHolder) privConstIter.next();
                if (ch.isDangerous()) { //if its dangerous check if its called from any non-private constructors
                    //System.out.println("visitClassDec Evaluating dangerous constructor with " + ch.getASTConstructorDeclaration().getParameterCount() + " params");
                    int paramCount = ch.getASTConstructorDeclaration().getParameterCount();
                    for (Iterator calledConstIter = getCurrentEvalPackage().calledConstructors.iterator(); calledConstIter.hasNext();) {
                        ConstructorInvocation ci = (ConstructorInvocation) calledConstIter.next();
                        if (ci.getArgumentCount() == paramCount) {
                            //match name  super / this !?
                            RuleContext ctx = (RuleContext) data;
                            ctx.getReport().addRuleViolation(createRuleViolation(ctx, ci.getASTExplicitConstructorInvocation().getBeginLine()));
                        }
                    }
                }
            }
        }
        //finished evaluating this class, move up a level
        removeCurrentEvalPackage();
        return data;
    }

    /**
     * Check the methods called on this class by each of the methods on this
     * class.  If a method calls an unsafe method, mark the calling method as
     * unsafe.  This changes the list of unsafe methods which necessitates
     * another pass.  Keep passing until you make a clean pass in which no
     * methods are changed to unsafe.
     * For speed it is possible to limit the number of passes.
     *
     * Impossible to tell type of arguments to method, so forget method matching
     * on types.  just use name and num of arguments.  will be some false hits,
     * but oh well.
     *
     * @todo investigate limiting the number of passes through config.
     */
    private boolean evaluateDangerOfMethods(Map classMethodMap) {
        //check each method if it calls overridable method
        boolean found = false;
        for (Iterator methodsIter = classMethodMap.keySet().iterator(); methodsIter.hasNext();) {
            MethodHolder h = (MethodHolder) methodsIter.next();
            List calledMeths = (List) classMethodMap.get(h);
            for (Iterator calledMethsIter = calledMeths.iterator(); calledMethsIter.hasNext() && (h.isDangerous() == false);) {
                //if this method matches one of our dangerous methods, mark it dangerous
                MethodInvocation meth = (MethodInvocation) calledMethsIter.next();
                //System.out.println("Called meth is " + meth);
                for (Iterator innerMethsIter = classMethodMap.keySet().iterator(); innerMethsIter.hasNext();) { //need to skip self here h == h3
                    MethodHolder h3 = (MethodHolder) innerMethsIter.next();
                    if (h3.isDangerous()) {
                        String matchMethodName = h3.getASTMethodDeclarator().getImage();
                        int matchMethodParamCount = h3.getASTMethodDeclarator().getParameterCount();
                        //System.out.println("matchint " + matchMethodName + " to " + methName);
                        if (matchMethodName.equals(meth.getName()) && (matchMethodParamCount == meth.getArgumentCount())) {
                            h.setDangerous(true);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }

    /**
     * marks constructors dangerous if they call any dangerous methods
     * Requires only a single pass as methods are already marked
     * @todo optimize by having methods already evaluated somehow!?
     */
    private void evaluateDangerOfConstructors1(Map classConstructorMap, Set evaluatedMethods) {
        //check each constructor in the class
        for (Iterator constIter = classConstructorMap.keySet().iterator(); constIter.hasNext();) {
            ConstructorHolder ch = (ConstructorHolder) constIter.next();
            if (!ch.isDangerous()) {//if its not dangerous then evaluate if it should be
                //if it calls dangerous method mark it as dangerous
                List calledMeths = (List) classConstructorMap.get(ch);
                //check each method it calls
                for (Iterator calledMethsIter = calledMeths.iterator(); calledMethsIter.hasNext() && !ch.isDangerous();) {//but thee are diff objects which represent same thing but were never evaluated, they need reevaluation
                    MethodInvocation meth = (MethodInvocation) calledMethsIter.next();//CCE
                    String methName = meth.getName();
                    int methArgCount = meth.getArgumentCount();
                    //check each of the already evaluated methods: need to optimize this out
                    for (Iterator evaldMethsIter = evaluatedMethods.iterator(); evaldMethsIter.hasNext();) {
                        MethodHolder h = (MethodHolder) evaldMethsIter.next();
                        if (h.isDangerous()) {
                            String matchName = h.getASTMethodDeclarator().getImage();
                            int matchParamCount = h.getASTMethodDeclarator().getParameterCount();
                            if (methName.equals(matchName) && (methArgCount == matchParamCount)) {
                                ch.setDangerous(true);
                                //System.out.println("evaluateDangerOfConstructors1 setting dangerous constructor with " + ch.getASTConstructorDeclaration().getParameterCount() + " params");
                                break;
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Constructor map should contain a key for each private constructor, and
     * maps to a List which contains all called constructors of that key.
     * marks dangerous if call dangerous private constructor
     * we ignore all non-private constructors here.  That is, the map passed in
     * should not contain any non-private constructors.
     * we return boolean in order to limit the number of passes through this method
     * but it seems as if we can forgo that and just process it till its done.
     */
    private boolean evaluateDangerOfConstructors2(Map classConstructorMap) {
        boolean found = false;//triggers on danger state change
        //check each constructor in the class
        for (Iterator constIter = classConstructorMap.keySet().iterator(); constIter.hasNext();) {
            ConstructorHolder ch = (ConstructorHolder) constIter.next();
            ConstructorInvocation calledC = ch.getCalledConstructor();
            if (calledC == null || ch.isDangerous()) {
                continue;
            }
            //if its not dangerous then evaluate if it should be
            //if it calls dangerous constructor mark it as dangerous
            int cCount = calledC.getArgumentCount();
            for (Iterator innerConstIter = classConstructorMap.keySet().iterator(); innerConstIter.hasNext() && !ch.isDangerous();) { //forget skipping self because that introduces another check for each, but only 1 hit
                ConstructorHolder h2 = (ConstructorHolder) innerConstIter.next();
                if (h2.isDangerous()) {
                    int matchConstArgCount = h2.getASTConstructorDeclaration().getParameterCount();
                    if (matchConstArgCount == cCount) {
                        ch.setDangerous(true);
                        found = true;
                        //System.out.println("evaluateDangerOfConstructors2 setting dangerous constructor with " + ch.getASTConstructorDeclaration().getParameterCount() + " params");
                    }
                }
            }
        }
        return found;
    }

    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    //The Visited Methods

    /**
     * Work on each file independently.
     */
    public Object visit(ASTCompilationUnit node, Object data) {
        clearEvalPackages();
        //		try {
        return super.visit(node, data);
        //		}
        //		catch(Exception e){
        //			e.printStackTrace();
        //		}
    }
    //for testing only
    //	public Object visit(ASTPackageDeclaration node, Object data){
    //		System.out.println("package= " + ((ASTName)node.jjtGetChild(0)).getImage());
    //		return super.visit(node,data);
    //	}

    /**
     * This check must be evaluated independelty for each class.  Inner classses
     * get their own EvalPackage in order to perform independent evaluation.
     */
    public Object visit(ASTClassDeclaration node, Object data) {
        return visitClassDec(node, data);
    }

    public Object visit(ASTNestedClassDeclaration node, Object data) {
        return visitClassDec(node, data);
    }

    public Object visit(ASTInterfaceDeclaration node, Object data) {
        putEvalPackage(nullEvalPackage);
        Object o = super.visit(node, data);//interface may have inner classes, possible? if not just skip whole interface
        removeCurrentEvalPackage();
        return o;
    }

    public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
        putEvalPackage(nullEvalPackage);
        Object o = super.visit(node, data);//interface may have inner classes, possible? if not just skip whole interface
        removeCurrentEvalPackage();
        return o;
    }


    /**
     * Non-private constructor's methods are added to a list for later safety
     * evaluation.  Non-private constructor's calls on private constructors
     * are added to a list for later safety evaluation.  Private constructors
     * are added to a list so their safety to be called can be later evaluated.
     *
     * Note: We are not checking private constructor's calls on non-private
     * constructors because all non-private constructors will be evaluated for
     * safety anyway.  This means we wont flag a private constructor as unsafe
     * just because it calls an unsafe public constructor.  We want to show only
     * 1 instance of an error, and this would be 2 instances of the same error.
     *
     * @todo eliminate the redundency
     */
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {//only evaluate if we have an eval package for this class
            List calledMethodsOfConstructor = new ArrayList();
            ConstructorHolder ch = new ConstructorHolder(node);
            addCalledMethodsOfNode(node, calledMethodsOfConstructor, getCurrentEvalPackage().m_ClassName);
            if (!node.isPrivate()) {
                //these calledMethods are what we will evaluate for being called badly
                getCurrentEvalPackage().calledMethods.addAll(calledMethodsOfConstructor);
                //these called private constructors are what we will evaluate for being called badly
                //we add all constructors invoked by non-private constructors
                //but we are only interested in the private ones.  We just can't tell the difference here
                ASTExplicitConstructorInvocation eci = ch.getASTExplicitConstructorInvocation();
                if (eci != null && eci.isThis()) {
                    getCurrentEvalPackage().calledConstructors.add(ch.getCalledConstructor());
                }
            } else {
                //add all private constructors to list for later evaluation on if they are safe to call from another constructor
                //store this constructorHolder for later evaluation
                getCurrentEvalPackage().allPrivateConstructorsOfClass.put(ch, calledMethodsOfConstructor);
            }
        }
        return super.visit(node, data);
    }

    /**
     * Create a MethodHolder to hold the method.
     * Store the MethodHolder in the Map as the key
     * Store each method called by the current method as a List in the Map as the Object
     */
    public Object visit(ASTMethodDeclarator node, Object data) {
        if (!(getCurrentEvalPackage() instanceof NullEvalPackage)) {//only evaluate if we have an eval package for this class
            AccessNode parent = (AccessNode) node.jjtGetParent();
            MethodHolder h = new MethodHolder(node);
            if (!parent.isPrivate() && !parent.isStatic() && !parent.isFinal()) {
                h.setDangerous(true);//this method is overridable
            }
            List l = new ArrayList();
            addCalledMethodsOfNode((SimpleNode) parent, l, getCurrentEvalPackage().m_ClassName);
            getCurrentEvalPackage().allMethodsOfClass.put(h, l);
        }
        return super.visit(node, data);
    }



    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////
    //Helper methods to process visits

    private static void addCalledMethodsOfNode(AccessNode node, List calledMethods, String className) {
        List expressions = new ArrayList();
        node.findChildrenOfType(ASTPrimaryExpression.class, expressions, false);
        addCalledMethodsOfNodeImpl(expressions, calledMethods, className);
    }

    /**
     * Adds all methods called on this instance from within this Node.
     */
    private static void addCalledMethodsOfNode(SimpleNode node, List calledMethods, String className) {
        List expressions = new ArrayList();
        node.findChildrenOfType(ASTPrimaryExpression.class, expressions);
        addCalledMethodsOfNodeImpl(expressions, calledMethods, className);
    }

    private static void addCalledMethodsOfNodeImpl(List expressions, List calledMethods, String className) {
        for (Iterator it = expressions.iterator(); it.hasNext();) {
            ASTPrimaryExpression ape = (ASTPrimaryExpression) it.next();
            MethodInvocation meth = findMethod(ape, className);
            if (meth != null) {
                //System.out.println("Adding call " + methName);
                calledMethods.add(meth);
            }
        }
    }

    /**
     * @todo Need a better way to match the class and package name to the actual
     *       method being called.
     * @return A method call on the class passed in, or null if no method call
     *         is found.
     */
    private static MethodInvocation findMethod(ASTPrimaryExpression node, String className) {
        if (node.jjtGetNumChildren() > 0
	    && node.jjtGetChild(0).jjtGetNumChildren() > 0
	    && node.jjtGetChild(0).jjtGetChild(0) instanceof ASTLiteral) {
            return null;
        }
        MethodInvocation meth = MethodInvocation.getMethod(node);
        boolean found = false;
        //		if(meth != null){
        //			meth.show();
        //		}
        if (meth != null) {
            //if it's a call on a variable, or on its superclass ignore it.
            if ((meth.getReferenceNames().size() == 0) && !meth.isSuper()) {
                //if this list does not contain our class name, then its not referencing our class
                //this is a cheezy test... but it errs on the side of less false hits.
                List packClass = meth.getQualifierNames();
                if (packClass.size() > 0) {
                    for (Iterator it = packClass.iterator(); it.hasNext();) {
                        String name = (String) it.next();
                        if (name.equals(className)) {
                            found = true;
                            break;
                        }
                    }
                } else {
                    found = true;
                }
            }
        }
        if (found) {
            return meth;
        } else {
            return null;
        }
    }

    /**
     *  ASTPrimaryPrefix has name in child node of ASTName
     */
    private static String getNameFromPrefix(ASTPrimaryPrefix node) {
        String name = null;
        //should only be 1 child, if more I need more knowledge
        if (node.jjtGetNumChildren() == 1) { //safety check
            Node nnode = node.jjtGetChild(0);
            if (nnode instanceof ASTName) { //just as easy as null check and it should be an ASTName anyway
                name = ((ASTName) nnode).getImage();
            }
        }
        return name;
    }

    /**
     * ASTPrimarySuffix has name in itself
     */
    private static String getNameFromSuffix(ASTPrimarySuffix node) {
        return node.getImage();
    }
}
