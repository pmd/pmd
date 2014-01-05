/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;


import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;

/**
 * Restrictions regarding the legal placement and content of the file header.
 * 
 * @author Brian Remedios
 */
public class HeaderCommentsRule extends AbstractCommentRule {
	
	private static final String[] requiredWords = new String[] { "copyright" };
	private static final String[] requiredTags = new String[] { "author", "version" };
	
	public static final StringMultiProperty REQUIRED_TERMS_DESCRIPTOR = new StringMultiProperty("requiredTerms",
	    	"Expected terms or phrases in the code header", requiredWords, 1.0f, '|');
	   
	public static final StringMultiProperty REQUIRED_TAGS_DESCRIPTOR = new StringMultiProperty("requiredTags",
    		"Expected tags in the header", requiredTags, 2.0f, '|');
   
	enum RequiredHeaderPlacement {
		BeforePackageDeclaration("Before package"), 
		BeforeImportStatements("Before imports"), 
		BeforeTypeDeclaration("Before types"), 
		Anywhere("Anywhere"); 
		
		private final String label;
		
		RequiredHeaderPlacement(String theLabel) {
			label = theLabel;
		}
		
		public static String[] labels() {
			String[] labels = new String[values().length];
			int i=0;
			for (RequiredHeaderPlacement placement : values()) {
				labels[i++] = placement.label;
			}
			return labels;
		}
	}
	
    public static final EnumeratedProperty<RequiredHeaderPlacement> HEADER_PLACEMENT_DESCRIPTOR = new EnumeratedProperty<RequiredHeaderPlacement>(
    	    "headerPlacement",
    	    "Placement of the header comment", 
    	    RequiredHeaderPlacement.labels(), 
    	    RequiredHeaderPlacement.values(), 
    	    0, 3.0f
    	    );
		
	public HeaderCommentsRule() {
		definePropertyDescriptor(REQUIRED_TERMS_DESCRIPTOR);
		definePropertyDescriptor(REQUIRED_TAGS_DESCRIPTOR);
		definePropertyDescriptor(HEADER_PLACEMENT_DESCRIPTOR);
	}

	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
  
//		SortedMap<Integer, Object> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);

        return super.visit(cUnit, data);
    }
}
