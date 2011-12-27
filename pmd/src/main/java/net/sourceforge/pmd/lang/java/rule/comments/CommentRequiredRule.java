package net.sourceforge.pmd.lang.java.rule.comments;

import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
/**
 *
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractCommentRule {

	enum CommentRequirement {
		Required("Required"),
		Ignored("Ignored"),
		Unwanted("Unwanted");

		private final String label;

		CommentRequirement(String theLabel) {
			label = theLabel;
		}

		public static String[] labels() {
			String[] labels = new String[values().length];
			int i=0;
			for (CommentRequirement requirement : values()) {
				labels[i++] = requirement.label;
			}
			return labels;
		}
	}

    public static final EnumeratedProperty<CommentRequirement> HEADER_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
    	    "headerCommentRequirement",
    	    "Header comments",
    	    CommentRequirement.labels(),
    	    CommentRequirement.values(),
    	    0, 1.0f
    	    );

    public static final EnumeratedProperty<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
    	    "fieldCommentRequirement",
    	    "Field comments",
    	    CommentRequirement.labels(),
    	    CommentRequirement.values(),
    	    0, 2.0f
    	    );

    public static final EnumeratedProperty<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
    	    "publicMethodCommentRequirement",
    	    "Public method comments",
    	    CommentRequirement.labels(),
    	    CommentRequirement.values(),
    	    0, 3.0f
    	    );

    public static final EnumeratedProperty<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
    	    "protectedMethodCommentRequirement",
    	    "Protected method comments",
    	    CommentRequirement.labels(),
    	    CommentRequirement.values(),
    	    0, 4.0f
    	    );

	public CommentRequiredRule() {
		definePropertyDescriptor(HEADER_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
	}

	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {

//		SortedMap<Integer, Object> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);

        return super.visit(cUnit, data);
    }

	public boolean allCommentsAreIgnored() {

		return getProperty(HEADER_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored &&
			getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored &&
			getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored &&
			getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored ;
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	@Override
	public String dysfunctionReason() {
		return allCommentsAreIgnored() ?
				"All comment types are ignored" :
				null;
	}
}
