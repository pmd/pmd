package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.SortedMap;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
/**
 * 
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractCommentRule {

	public static final BooleanProperty HEADER_COMMENTS_REQD_DESCRIPTOR = new BooleanProperty("headerRequired",
    		"Header comment required", true, 1.0f);
    
	public static final BooleanProperty FIELD_COMMENTS_REQD_DESCRIPTOR = new BooleanProperty("fieldRequired",
    		"Field comment required", false, 2.0f);
	
	public static final BooleanProperty PUBLIC_METHOD_COMMENTS_REQD_DESCRIPTOR = new BooleanProperty("publicRequired",
    		"Public method comment required", false, 3.0f);
	
	public static final BooleanProperty PROTECTED_METHOD_COMMENTS_REQD_DESCRIPTOR = new BooleanProperty("protectedRequired",
    		"Protected method comment required", false, 4.0f);
		
	
	public CommentRequiredRule() {
		definePropertyDescriptor(HEADER_COMMENTS_REQD_DESCRIPTOR);
		definePropertyDescriptor(FIELD_COMMENTS_REQD_DESCRIPTOR);
		definePropertyDescriptor(PUBLIC_METHOD_COMMENTS_REQD_DESCRIPTOR);
		definePropertyDescriptor(PROTECTED_METHOD_COMMENTS_REQD_DESCRIPTOR);
	}
	
	@Override
    public Object visit(ASTCompilationUnit cUnit, Object data) {
  
		SortedMap<Integer, Object> itemsByLineNumber = orderedCommentsAndDeclarations(cUnit);

        return super.visit(cUnit, data);
    }
	
	
}
