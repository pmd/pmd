/*
 * ConstructorCallsOverridableMethodRule.java
 * dnoyeb@users.sourceforge.net
 * Created on February 5, 2003, 1:54 PM
 */

package net.sourceforge.pmd.rules;

import java.util.*;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

/**
 * Searches through all methods and constructors called from constructors.  It
 * marks as dangerous any call to overridable methods from non-private
 * constructors.  It marks as dangerous any calls to dangerous private constructors
 * from non-private constructors.
 *
 *
 * @todo Currently can't compare method signatures because types are not known
 *       from call.  Impossible to tell types with current architecture.
 * @todo Currently can't tell super() from this().
 * @author dnoyeb@users.sourceforge.net
 */
public class ConstructorCallsOverridableMethodRule extends net.sourceforge.pmd.AbstractRule {

    /**
     *		2: method();
     *			ASTPrimaryPrefix
     *				ASTName
     *			ASTPrimarySuffix
     *				*ASTArguments
     *		3: a.method();
     *			ASTPrimaryPrefix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *		3: this.method();
     *			ASTPrimaryPrefix -> this
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *		4: this.a.method();
     *			ASTPrimaryPrefix -> this
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      4: ClassName.this.method();
     *			ASTPrimaryPrefix -> ClassName
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *		5: ClassName.this.a.method();
     *			ASTPrimaryPrefix -> ClassName
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      5: Package.ClassName.this.method();
     *			ASTPrimaryPrefix -> Package
     *			ASTPrimarySuffix -> ClassName
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      6: Package.ClassName.this.a.method();
     *			ASTPrimaryPrefix -> Package
     *			ASTPrimarySuffix -> ClassName
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      5: OuterClass.InnerClass.this.method();
     *			ASTPrimaryPrefix -> OuterClass
     *			ASTPrimarySuffix -> InnerClass
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *      6: OuterClass.InnerClass.this.a.method();
     *			ASTPrimaryPrefix -> OuterClass
     *			ASTPrimarySuffix -> InnerClass
     *			ASTPrimarySuffix -> this
     *			ASTPrimarySuffix -> a
     *			ASTPrimarySuffix -> method
     *			ASTPrimarySuffix -> ()
     *				ASTArguments
     *
     *      3..n:	Class.InnerClass[0].InnerClass[n].this.method();
     *				ASTPrimaryPrefix -> Class[0]
     *				ASTPrimarySuffix -> InnerClass[0..n]
     *				ASTPrimarySuffix -> this
     *				ASTPrimarySuffix -> method
     *				ASTPrimarySuffix -> ()
     *					ASTArguments
     *
     *		Evaluate right to left
     *
     */
    public static class MethodInvocation {
        private String m_Name;
        private ASTArguments m_Args;
        private ASTPrimaryExpression m_Ape;
        private List m_VariableNames;
        private List m_PackageClassNames;
        private int m_ArgumentSize;

        private MethodInvocation(ASTPrimaryExpression ape, List packageClassNames, List variableNames, String name, int argumentSize){
            m_Ape = ape;
            m_PackageClassNames = packageClassNames;
            m_VariableNames = variableNames;
            m_Name = name;
            m_ArgumentSize = argumentSize;
        }



        public String getName(){
            return m_Name;
        }
        protected void setName(String name){
            m_Name = name;
        }
        public int getArgumentCount(){
            return m_ArgumentSize;
        }
        protected void setArgumentCount(int argumentSize){
            m_ArgumentSize = argumentSize;
        }
        public List getVariableNames(){
            return m_VariableNames;//new ArrayList(variableNames);
        }
        protected void setVariableNames(List variableNames){
            m_VariableNames = variableNames;
        }
        public List getPackageClassNames(){
            return m_PackageClassNames;
        }
        protected void setPackageClassNames(List packageClassNames){
            m_PackageClassNames = packageClassNames;
        }
        public net.sourceforge.pmd.ast.ASTArguments getArguments(){
            return m_Args;
        }
        protected void setArguments(net.sourceforge.pmd.ast.ASTArguments args){
            m_Args = args;
        }

        public ASTPrimaryExpression getASTPrimaryExpression(){
            return m_Ape;
        }

        private static MethodInvocation getMethod(ASTPrimaryExpression node){
            MethodInvocation meth = null;
            int i = node.jjtGetNumChildren();
            if ( i > 1) {//should always be at least 2, probably can eliminate this check
                //start at end which is guaranteed, work backwards
                Node lastNode = node.jjtGetChild(i-1);
                if(lastNode.jjtGetNumChildren() == 1 && (lastNode.jjtGetChild(0) instanceof ASTArguments)){ //this should always be the case, probably can eliminate this check
                    //start putting method together
                    //System.out.println("Putting method together now");
                    List varNames = new ArrayList();
                    List packagesAndClasses = new ArrayList(); //look in JLS for better name here;
                    String methodName=null;
                    ASTArguments args = (ASTArguments)lastNode.jjtGetChild(0) ;
                    int numOfArguments = args.getArgumentCount();

                    int thisIndex=-1;
                    //search all nodes except last for 'this'.  this will be at: node 0, node 1, nowhere
                    for(int x = 0; x < i-1; x++){
                        Node child = node.jjtGetChild(x);
                        String name = null;
                        if(child instanceof ASTPrimarySuffix){ //check suffix type match
                            name = getNameFromSuffix((ASTPrimarySuffix)child);
                            //System.out.println("found name suffix of : " + name);
                        }
                        else if(child instanceof ASTPrimaryPrefix){ //check prefix type match
                            name = getNameFromPrefix((ASTPrimaryPrefix)child);
                            //System.out.println("found name prefix of : " + name);
                        }
                        else{
                            System.err.println("Bad Format error");
                        }
                        //'this' comes as a null.  Their must be a better check!?
                        if(name == null){
                            thisIndex = x;
                            break;
                        }
                    }
                    if(thisIndex != -1){
                        //System.out.println("Found this: " + thisIndex);
                        //variable names are all nodes between this and the argument holding suffix
                        for(int x= thisIndex + 1 ; x< i-1;x++){
                            Node child = node.jjtGetChild(x);
                            String suffixName;
                            if(child instanceof ASTPrimarySuffix){ //all should be primary suffix after 'this'
                                suffixName = getNameFromSuffix((ASTPrimarySuffix)child);
                                //System.out.println("Found suffix: " + suffixName);
                                if(x == i-2){ //method name
                                    methodName = suffixName;
                                }
                                else{ //variable name
                                    varNames.add(suffixName);
                                }
                            }
                        }
                        //everything before 'this' is package or class name
                        //this will be at 0 or 1.  if its at 1, then we have package/class names preceeding
                        if(thisIndex > 0){
                            Node child = node.jjtGetChild(0);
                            if(child instanceof ASTPrimaryPrefix){ //check prefix type match
                                String toParse = getNameFromPrefix((ASTPrimaryPrefix)child);
                                //System.out.println("parsing for class/package names in : " + toParse);
                                java.util.StringTokenizer st = new java.util.StringTokenizer(toParse,".");
                                while(st.hasMoreTokens()){
                                    packagesAndClasses.add(st.nextToken());
                                }
                            }
                            else{
                                System.err.println("Bad Format error");
                            }
                        }
                    }
                    else { //if no this, everything is method name or variable
                        //var names all in prefix, method name at end
                        //System.out.println("no this found:");
                        Node child = node.jjtGetChild(0);
                        if(child instanceof ASTPrimaryPrefix){ //first is always ASTPrimaryPrefix
                            String toParse = getNameFromPrefix((ASTPrimaryPrefix)child);
                            //System.out.println("parsing for var names in : " + toParse);
                            java.util.StringTokenizer st = new java.util.StringTokenizer(toParse,".");
                            while(st.hasMoreTokens()){
                                String value = st.nextToken();
                                if(!st.hasMoreTokens()){ //method name
                                    methodName = value;
                                }
                                else { //variable name
                                    varNames.add(value);
                                }
                            }
                        }
                    }
                    meth = new MethodInvocation( node, packagesAndClasses, varNames, methodName,numOfArguments);
                }
            }
            return meth;
        }

        private void show(){
            //StringBuffer sb = new StringBuffer();
            System.out.println("<MethodInvocation>");
            List pkg = getPackageClassNames();
            System.out.println("  <Packaging>");
            for(Iterator it = pkg.iterator();it.hasNext();){
                String name = (String)it.next();
                System.out.println("    " + name);
            }
            System.out.println("  </Packaging>");
            List vars = getVariableNames();
            System.out.println("  <Variables>");
            for(Iterator it = vars.iterator();it.hasNext();){
                String name = (String)it.next();
                System.out.println("    " + name);
            }
            System.out.println("  </Variables>");
            System.out.println("  <Name>");
            System.out.println("    " + getName());
            System.out.println("  </Name>");
            System.out.println("</MethodInvocation>");
        }
    }

    public static class ConstructorInvocation {
        private ASTExplicitConstructorInvocation m_Eci;
        private String name;
        private int count =0;
        public ConstructorInvocation(ASTExplicitConstructorInvocation eci){
            m_Eci = eci;
            List l = new ArrayList();
            eci.findChildrenOfType(ASTArguments.class,l);
            if(l.size() > 0){
                ASTArguments aa = (ASTArguments) l.get(0);
                count = aa.getArgumentCount();
            }
            name = eci.getImage();
        }
        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation(){
            return m_Eci;
        }
        public int getArgumentCount(){
            return count;
        }
        public String getName(){
            return name;
        }
    }

    public static class Holder {
        private ASTMethodDeclarator m_Amd;
        private boolean m_Dangerous = false;

        public Holder(ASTMethodDeclarator amd){
            m_Amd = amd;
        }
        public ASTMethodDeclarator getASTMethodDeclarator(){
            return m_Amd;
        }
        public boolean isDangerous(){
            return m_Dangerous;
        }
        public void setDangerous(boolean dangerous){
            m_Dangerous = dangerous;
        }
    }

    public static class ConstructorHolder {
        private ASTConstructorDeclaration m_Cd;
        private boolean m_Dangerous;
        private ConstructorInvocation m_Ci;
        private boolean m_CiInitialized;

        public boolean isDangerous(){
            return m_Dangerous;
        }

        public void setDangerous(boolean dangerous){
            m_Dangerous = dangerous;
        }

        public ConstructorHolder(ASTConstructorDeclaration cd){
            m_Cd = cd;
        }
        public ASTConstructorDeclaration getASTConstructorDeclaration(){
            return m_Cd;
        }
        public ConstructorInvocation getCalledConstructor(){
            if(m_CiInitialized == false){
                initCI();
            }
            return m_Ci;
        }
        public ASTExplicitConstructorInvocation getASTExplicitConstructorInvocation(){
            ASTExplicitConstructorInvocation eci = null;
            if(m_CiInitialized == false){
                initCI();
            }
            if(m_Ci != null){
                eci = m_Ci.getASTExplicitConstructorInvocation();
            }
            return eci;
        }

        private void initCI(){
            List expressions = new ArrayList();
            m_Cd.findChildrenOfType(ASTExplicitConstructorInvocation.class, expressions); //only 1...
            if(expressions.size() >0){
                ASTExplicitConstructorInvocation eci = (ASTExplicitConstructorInvocation)expressions.get(0);
                m_Ci = new ConstructorInvocation(eci);
                //System.out.println("Const call " + eci.getImage()); //super or this???
            }
            m_CiInitialized = true;
        }

    }

    /**
     * 1 package per class. holds info for evaluating a single class.
     */
    public static class EvalPackage{

        public EvalPackage(String className){
            m_ClassName = className;
        }
        public String m_ClassName;
        public List calledMethods = new ArrayList();//meths called from constructor
        public Map allMethodsOfClass = new HashMap();

        public List calledConstructors = new ArrayList();//all constructors called from constructor
        public Map allPrivateConstructorsOfClass = new HashMap();
    }

	/**
	 * Used to itentify which class we are parsing.
	 */
	private static int classID;
	/**
	 * Used to itentify which class we are parsing.
	 */
	private static Integer classIDKey;
	/**
	 * 1 package per class.
	 */
	private Map evalPackages = new HashMap();
	
	private EvalPackage getEvalPackage(){
		return (EvalPackage) evalPackages.get(classIDKey);
	}
	
	/**
	 * This check must be evaluated independelty for each class.  Inner classses
	 * get their own EvalPackage in order to perform independent evaluation.
	 * @todo differentiate between super() and this()
	 */
	private Object visitClassDec(AccessNode node,Object data){
		Node child1 =node.jjtGetChild(0);
		String className=null;
		if(child1 instanceof ASTUnmodifiedClassDeclaration){
			className = ((ASTUnmodifiedClassDeclaration)child1).getImage();
		}

		RuleContext ctx=null;
		classID++;
		classIDKey = new Integer(classID);
		//evaluate each level independently
		if(!node.isFinal() && !node.isStatic()){
			evalPackages.put(classIDKey,new EvalPackage(className));
		}
		//store any errors caught from other passes.
		ctx = (RuleContext) super.visit(node, data);

		//skip this class if it has no evaluation package
		if(getEvalPackage() != null){
			//evaluate danger of all methods in class
			while(evaluateDangerOfMethods(getEvalPackage().allMethodsOfClass) == true);
			//evaluate danger of constructors
			evaluateDangerOfConstructors1(getEvalPackage().allPrivateConstructorsOfClass,getEvalPackage().allMethodsOfClass.keySet());
			while(evaluateDangerOfConstructors2(getEvalPackage().allPrivateConstructorsOfClass) == true);
				
			//get each method called from a non-private constructor, if its dangerous flag it
			for(Iterator it = getEvalPackage().calledMethods.iterator();it.hasNext();){
				MethodInvocation meth = (MethodInvocation) it.next();
				//check against each dangerous method in class
				for(Iterator it2 = getEvalPackage().allMethodsOfClass.keySet().iterator();it2.hasNext();){
					Holder h = (Holder)it2.next();
					if(h.isDangerous()){
						String methName = h.getASTMethodDeclarator().getImage();
						int count = h.getASTMethodDeclarator().getParameterCount();
						if(meth.getName().equals(methName) && (meth.getArgumentCount() == count)){
							//bad call
							if(ctx == null){
								ctx = (RuleContext)data;
							}
							ctx.getReport().addRuleViolation(createRuleViolation(ctx, meth.getASTPrimaryExpression().getBeginLine()));
						}
					}
				}
			}
			//get each unsafe private constructor, and check if its called from any non private constructors
			for(Iterator privConstIter = getEvalPackage().allPrivateConstructorsOfClass.keySet().iterator();privConstIter.hasNext();){
				ConstructorHolder ch = (ConstructorHolder) privConstIter.next();
				if(ch.isDangerous()){ //if its dangerous check if its called from any non-private constructors
					//System.out.println("visitClassDec Evaluating dangerous constructor with " + ch.getASTConstructorDeclaration().getParameterCount() + " params");
					int paramCount = ch.getASTConstructorDeclaration().getParameterCount();
					for(Iterator calledConstIter = getEvalPackage().calledConstructors.iterator();calledConstIter.hasNext();){
						ConstructorInvocation ci = (ConstructorInvocation) calledConstIter.next();
						if(ci.getArgumentCount() == paramCount) {
							//match name  super / this !?
							if(ctx == null){
								ctx = (RuleContext)data;
							} 
							ctx.getReport().addRuleViolation(createRuleViolation(ctx,ci.getASTExplicitConstructorInvocation().getBeginLine()));
						}
					}
				}
			}
			//finished evaluating this class, move up a level
			evalPackages.remove(classIDKey);
		}
		classID--;
		classIDKey = new Integer(classID);
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
	private boolean evaluateDangerOfMethods(Map classMethodMap){
		//check each method if it calls overridable method 
		boolean found = false;
		for(Iterator methodsIter = classMethodMap.keySet().iterator();methodsIter.hasNext();){
			Holder h = (Holder)methodsIter.next();
			List calledMeths = (List)classMethodMap.get(h);
			for(Iterator calledMethsIter = calledMeths.iterator();calledMethsIter.hasNext() && (h.isDangerous() == false);){
				//if this method matches one of our dangerous methods, mark it dangerous
				MethodInvocation meth = (MethodInvocation) calledMethsIter.next();
				//System.out.println("Called meth is " + meth);
				for(Iterator innerMethsIter = classMethodMap.keySet().iterator();innerMethsIter.hasNext();){ //need to skip self here h == h3
					Holder h3 = (Holder)innerMethsIter.next();
					if(h3.isDangerous()){
						String matchMethodName = h3.getASTMethodDeclarator().getImage();
						int matchMethodParamCount = h3.getASTMethodDeclarator().getParameterCount();
						//System.out.println("matchint " + matchMethodName + " to " + methName);
						//WE MUST MATCH ON METHOD SIGNATURE AS WELL, but cant till can get sig from methDecl
						if(matchMethodName.equals(meth.getName()) && (matchMethodParamCount == meth.getArgumentCount())){
							h.setDangerous(true);
							found = true;
							break;//new 
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
	private void evaluateDangerOfConstructors1(Map classConstructorMap,Set evaluatedMethods){
		//check each constructor in the class
		for(Iterator constIter = classConstructorMap.keySet().iterator();constIter.hasNext();){
			ConstructorHolder ch = (ConstructorHolder)constIter.next();
			if(!ch.isDangerous()){//if its not dangerous then evaluate if it should be
				//if it calls dangerous method mark it as dangerous
				List calledMeths = (List)classConstructorMap.get(ch);
				//check each method it calls
				for(Iterator calledMethsIter = calledMeths.iterator();calledMethsIter.hasNext() && !ch.isDangerous();){//but thee are diff objects which represent same thing but were never evaluated, they need reevaluation
					MethodInvocation meth = (MethodInvocation)calledMethsIter.next();//CCE
					String methName = meth.getName();
					int methArgCount = meth.getArgumentCount();
					//check each of the already evaluated methods: need to optimize this out
					for(Iterator evaldMethsIter = evaluatedMethods.iterator();evaldMethsIter.hasNext();){
						Holder h = (Holder)evaldMethsIter.next();
						if(h.isDangerous()){
							String matchName = h.getASTMethodDeclarator().getImage();
							int matchParamCount = h.getASTMethodDeclarator().getParameterCount();
							if(methName.equals(matchName) && (methArgCount == matchParamCount)){
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
	 */
	private boolean evaluateDangerOfConstructors2(Map classConstructorMap){
		boolean found = false;//triggers on danger state change
		//check each constructor in the class
		for(Iterator constIter = classConstructorMap.keySet().iterator();constIter.hasNext();){
			ConstructorHolder ch = (ConstructorHolder)constIter.next();
			if(!ch.isDangerous()){//if its not dangerous then evaluate if it should be
				//if it calls dangerous constructor mark it as dangerous
				ConstructorInvocation calledC = ch.getCalledConstructor();
				String cName = calledC.getName();
				int cCount = calledC.getArgumentCount();
				for(Iterator innerConstIter = classConstructorMap.keySet().iterator();innerConstIter.hasNext() && !ch.isDangerous();){ //forget skipping self because that introduces another check for each, but only 1 hit
					ConstructorHolder h2 = (ConstructorHolder)innerConstIter.next();
					if(h2.isDangerous()){
						String matchConstName = h2.getASTConstructorDeclaration().getImage();
						int matchConstArgCount = h2.getASTConstructorDeclaration().getParameterCount();
						if(matchConstName.equals(cName) && (matchConstArgCount == cCount)){
							ch.setDangerous(true);
							found = true;
							//System.out.println("evaluateDangerOfConstructors2 setting dangerous constructor with " + ch.getASTConstructorDeclaration().getParameterCount() + " params");
						}
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
		classID =0;
		classIDKey = null;
		evalPackages.clear();
		return super.visit(node,data);
    }

	/**
	 * This check must be evaluated independelty for each class.  Inner classses
	 * get their own EvalPackage in order to perform independent evaluation.
	 */
	public Object visit(net.sourceforge.pmd.ast.ASTClassDeclaration node, Object data){
		return visitClassDec(node,data);
	}

	public Object visit(net.sourceforge.pmd.ast.ASTNestedClassDeclaration node, Object data){
		return visitClassDec(node,data);
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
		if(getEvalPackage() != null){//only evaluate if we have an eval package for this class
			List calledMethodsOfConstructor = new ArrayList();
			ConstructorHolder ch = new ConstructorHolder(node);
			addCalledMethodsOfNode(node,calledMethodsOfConstructor,getEvalPackage().m_ClassName);
			if(!node.isPrivate()){
				//these calledMethods are what we will evaluate for being called badly
				getEvalPackage().calledMethods.addAll(calledMethodsOfConstructor);
				//these called private constructors are what we will evaluate for being called badly
				//we add all constructors invoked by non-private constructors
				//but we are only interested in the private ones.  We just can't tell the difference here
				if(ch.getASTExplicitConstructorInvocation() != null){
					getEvalPackage().calledConstructors.add(ch.getCalledConstructor());
				}
			}
			else {
				//add all private constructors to list for later evaluation on if they are safe to call from another constructor
				//store this constructorHolder for later evaluation
				getEvalPackage().allPrivateConstructorsOfClass.put(ch, calledMethodsOfConstructor);
			}
		}
		return super.visit(node,data);
    }

	/**
	 * Create a Holder to hold the method.
	 * Store the Holder in the Map as the key
	 * Store each method called by the current method as a List in the Map as the Object
	 */
    public Object visit(ASTMethodDeclarator node, Object data) {
		
		if(getEvalPackage() != null){//only evaluate if we have an eval package for this class
			AccessNode parent = (AccessNode)node.jjtGetParent();
			Holder h = new Holder(node);
			if (!parent.isPrivate() && !parent.isStatic() && !parent.isFinal()) {
				h.setDangerous(true);//this method is overridable
			}
			List l = new ArrayList();
			addCalledMethodsOfNode((SimpleNode)parent,l,getEvalPackage().m_ClassName);
			getEvalPackage().allMethodsOfClass.put(h, l);
		}
        return super.visit(node, data);
    }
	
	
	
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
//Helper methods to process visits
	
	private final static void addCalledMethodsOfNode(AccessNode node, List calledMethods, String className){
		List expressions = new ArrayList();
		node.findChildrenOfType(ASTPrimaryExpression.class, expressions);
		addCalledMethodsOfNodeImpl(expressions,calledMethods,className);		
	}

	/**
	 * Adds all methods called on this instance from within this Node.
	 */
	private final static void addCalledMethodsOfNode(SimpleNode node, List calledMethods, String className){
		List expressions = new ArrayList();
		node.findChildrenOfType(ASTPrimaryExpression.class, expressions);
		addCalledMethodsOfNodeImpl(expressions,calledMethods,className);
	}

	private final static void addCalledMethodsOfNodeImpl(List expressions, List calledMethods, String className){
		for(Iterator it = expressions.iterator();it.hasNext();){
			ASTPrimaryExpression ape = (ASTPrimaryExpression)it.next();
			MethodInvocation meth = findMethod(ape,className);
			if(meth != null){
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
	public final static MethodInvocation findMethod(ASTPrimaryExpression node, String className){
		MethodInvocation meth = MethodInvocation.getMethod(node);
		boolean found = false;
//		if(meth != null){
//			meth.show();
//		}
		if(meth != null){
			found = true;
			//if this list does not contain our class name, then its not referencing our class
			//this is a cheezy test... but it errs on the side of less false hits.
			List packClass = meth.getPackageClassNames();
			if(packClass.size() > 0) {
				found = false;
				for(Iterator it = packClass.iterator();it.hasNext() && (found == false);){
					String name = (String) it.next();
					if(name.equals(className)){
						found = true;
					}
				}
			}
			//if it's a call on a variable, ignore it.
			if(meth.getVariableNames().size() > 0) {
				found = false;
			}
			if(found == false){
				meth = null;
			}
		}
		return meth;
	}
	/**
	 *  ASTPrimaryPrefix has name in child node of ASTName
	 */
	public final static String getNameFromPrefix(ASTPrimaryPrefix node) {
		String name = null;
		//should only be 1 child, if more I need more knowledge
		if(node.jjtGetNumChildren() == 1) { //safety check 
			Node nnode = node.jjtGetChild(0);
			if(nnode instanceof ASTName){ //just as easy as null check and it should be an ASTName anyway
				name = ((ASTName)nnode).getImage();
			}
		}
		return name;
	}
	/**
	 * ASTPrimarySuffix has name in itself
	 */
	public final static String getNameFromSuffix(ASTPrimarySuffix node) {
		return node.getImage();
	}



}
