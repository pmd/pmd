/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.omg.CosNaming.NamingContextOperations;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * <p>A generic rule that can be configurer to "count" classes of certains
 * types based on either their name (full name, prefix, suffixes anything can
 * be matched with a regex), and/or
 * their type.</p>
 * <p>Example of configurations:
 * 		<!-- Property order is MANDATORY !!! -->
 * 		<!-- Several regexes may be provided to ensure a match... -->
 * 		<property 	name="nameMatch" description="a regex on which to match"
 * 					value="^Abstract.*Bean*$,^*EJB*$"/>
 * 		<property 	name"operand"	description=""
 * 					value="and"/> <!-- possible values are and/or -->
 * 		<!-- Must be a full name to ensure type control !!! -->
 * 		<property 	name="typeMatch" description="a regex to match on implements/extends classname"
 * 					value="javax.servlet.Filter"/>
 * 		<!-- Define after how many occurences one should log a violation -->
 * 		<property 	name="threshold"	description="Defines how many occurences are legal"
 * 					value="2"/>
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


	private List<String> namesMatch;
	private List<String> typesMatch;

	private String operand;
	private int threshold;

	private static String COUNTER_LABEL;
	/**
	 *	Default empty constructor
	 */
	public GenericClassCounterRule() {
		super();
		init();
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
		this.namesMatch = arrayAsList(getStringProperties(nameMatchDescriptor));
		this.operand = getStringProperty(operandDescriptor);
		this.typesMatch = arrayAsList(getStringProperties(typeMatchDescriptor));
		String thresholdAsString = getStringProperty(thresholdDescriptor);
		this.threshold = Integer.valueOf(thresholdAsString);
	}

	 @Override
     public void start(RuleContext ctx) {
		 // Adding the proper attribute to the context
         ctx.setAttribute(COUNTER_LABEL, new AtomicLong());
         super.start(ctx);
     }

     @Override
     public Object visit(ASTImportDeclaration node, Object data) {
    	 // Is there any imported types that match ?
    	 // TODO:
         return super.visit(node, data);
     }

     @Override
     public Object visit(ASTClassOrInterfaceType classType,Object data) {
    	 boolean match = false;
    	 // Correlate type list from the import parsing with implements/extends list
    	 // TODO
    	 // Is there any name that match ?
    	 //
    	 if ( match ) {
    		 // We have a match, we increment
    		 RuleContext ctx = (RuleContext)data;
    		 AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
             total.incrementAndGet();
             // TODO: Keep a list of the matched classes...
    	 }
    	 return super.visit(classType, data);
     }

     @Override
     public void end(RuleContext ctx) {
             AtomicLong total = (AtomicLong)ctx.getAttribute(COUNTER_LABEL);
             // Do we have a violation ?
             if ( total.get() > this.threshold ) {
            	 //FIXME: Hum... No classname for the violation, this is an issue
            	 // A lot of tools using PMD uses this...
            	 //FIX: Add a violation BY classe's matched, this is a bit overkill
            	 // but i think is better this way
                 addViolation(ctx, null, new Object[] { total });
             }
             // Cleaning the context for the others rules
             ctx.removeAttribute(COUNTER_LABEL);
             super.start(ctx);
     }
}
