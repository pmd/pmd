/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.StringProperty;
import net.sourceforge.pmd.rules.regex.RegexHelper;

import org.jaxen.JaxenException;

/**
 * <p>A generic rule that can be configured to "count" classes of certain
 * type based on either their name (full name, prefix, suffixes anything can
 * be matched with a regex), and/or
 * their type.</p>
 *
 * <p>Example of configurations:
 * 		<!-- Property order is MANDATORY !!! -->
 * 		<!-- Several regexes may be provided to ensure a match... -->
 * 		<property 	name="nameMatch" description="a regex on which to match"
 * 					value="^Abstract.*Bean*$,^*EJB*$"/>
 * 		<!-- An operand to refine match strategy TODO: Not implemented yet !!! -->
 * 		<property 	name"operand"	description=""
 * 					value="and"/> <!-- possible values are and/or -->
 * 		<!-- Must be a full name to ensure type control !!! -->
 * 		<property 	name="typeMatch" description="a regex to match on implements/extends classname"
 * 					value="javax.servlet.Filter"/>
 * 		<!-- Define after how many occurences one should log a violation -->
 * 		<property 	name="threshold"	description="Defines how many occurences are legal"
 * 					value="2"/>
 * 		<!-- TODO: Add a parameter to allow "ignore" pattern based on name -->
 * </p>
 *
 * @author Ryan Gutafson, rgustav@users.sourceforge.net
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class GenericClassCounterRule extends AbstractJavaRule {


	private static final PropertyDescriptor nameMatchDescriptor = new StringProperty("nameMatch",
			"A series of regex, separeted by ',' to match on the classname", new String[] {""},1.0f,',');

	private static final PropertyDescriptor operandDescriptor = new StringProperty("operand",
			"or/and value to refined match criteria",new String(),2.0f);

	private static final PropertyDescriptor typeMatchDescriptor = new StringProperty("typeMatch",
			"A series of regex, separeted by ',' to match on implements/extends classname",new String[]{""},3.0f,',');

	private static final PropertyDescriptor thresholdDescriptor = new StringProperty("threshold",
			"Defines how many occurences are legal",new String(),4.0f);


	private List<Pattern> namesMatch = new ArrayList<Pattern>(0);
	private List<Pattern> typesMatch = new ArrayList<Pattern>(0);
	private List<SimpleNode> matches = new ArrayList<SimpleNode>(0);
	private List<String> simpleClassname = new ArrayList<String>(0);


	@SuppressWarnings("PMD") // When the rule is finished, this field will be used.
	private String operand;
	private int threshold;

	private static String COUNTER_LABEL;

	/**
	 *	Default empty constructor
	 */
	public GenericClassCounterRule() {
		super();
	}

	private List<String> arrayAsList(String[] array) {
		List<String> list = new ArrayList<String>(array.length);
		int nbItem = 0;
		while (nbItem < array.length )
			list.add(array[nbItem++]);
		return list;
	}

	protected void init(){
		// Creating the attribute name for the rule context
		COUNTER_LABEL = this.getClass().getSimpleName() + ".number of match";
		// Constructing the request from the input parameters
		this.namesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(nameMatchDescriptor)));
		this.operand = getStringProperty(operandDescriptor);
		this.typesMatch = RegexHelper.compilePatternsFromList(arrayAsList(getStringProperties(typeMatchDescriptor)));
		String thresholdAsString = getStringProperty(thresholdDescriptor);
		this.threshold = Integer.valueOf(thresholdAsString);
		// Initializing list of match
		this.matches = new ArrayList<SimpleNode>();

	}

	 @Override
     public void start(RuleContext ctx) {
		 // Adding the proper attribute to the context
         ctx.setAttribute(COUNTER_LABEL, new AtomicLong());
         super.start(ctx);
     }

     @Override
     public Object visit(ASTCompilationUnit node, Object data) {
    	 init();
    	 return super.visit(node,data);
     }

     @Override
     public Object visit(ASTImportDeclaration node, Object data) {
    	 // Is there any imported types that match ?
    	 for (Pattern pattern : this.typesMatch) {
    		 if ( RegexHelper.isMatch(pattern,node.getImportedName())) {
    			 if ( simpleClassname == null )
    				 simpleClassname = new ArrayList<String>(1);
    			 simpleClassname.add(node.getImportedName());
    		 }
    		 // FIXME: use type resolution framework to deal with star import ?
    	 }
         return super.visit(node, data);
     }

	@Override
	public Object visit(ASTClassOrInterfaceType classType,Object data) {
		// Is extends/implements list using one of the previous match on import ?
		// FIXME: use type resolution framework to deal with star import ?
		for (String matchType : simpleClassname) {
			if ( searchForAMatch(matchType,classType)) {
				addAMatch(classType, data);
			}
		}
		// TODO: implements the "operand" functionnality
		// Is there any names that actually match ?
		for (Pattern pattern : this.namesMatch)
			if ( RegexHelper.isMatch(pattern, classType.getImage()))
				addAMatch(classType, data);
		return super.visit(classType, data);
	}

	private void addAMatch(SimpleNode node,Object data) {
		// We have a match, we increment
		RuleContext ctx = (RuleContext)data;
		AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
		total.incrementAndGet();
		// And we keep a ref on the node for the report generation
		this.matches.add(node);
	}

	@SuppressWarnings("unchecked")
    private boolean searchForAMatch(String matchType,SimpleNode node) {
		boolean status = false;
    	 String xpathQuery = "//ClassOrInterfaceDeclaration[" +
							"(./ExtendsList/ClassOrInterfaceType[@Image = '" + matchType + "'])" +
							"or" +
							"(./ImplementsList/ClassOrInterfaceType[@Image = '" + matchType + "'])" +
							"]";
		try
		{
			List list = node.findChildNodesWithXPath(xpathQuery);
			if ( list != null && list.size() > 0 ) {
				// We got a match !
				status = true;
			}
		}
		catch (JaxenException e) {
			// Most likely, a should never happen exception...
			e.printStackTrace();
		}
		return status;
	}

	@Override
    public void end(RuleContext ctx) {
		AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
        // Do we have a violation ?
        if ( total.get() > this.threshold )
        	for (SimpleNode node : this.matches)
        		addViolation(ctx,node , new Object[] { total });
		// Cleaning the context for the others rules
		ctx.removeAttribute(COUNTER_LABEL);
		super.start(ctx);
     }
}
