package net.sourceforge.pmd.rx;

import org.drools.WorkingMemory;
import org.drools.AssertionException;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.rx.facts.*;

public class DroolsVisitor
    extends JavaParserVisitorAdapter
{
    private WorkingMemory memory = null;

    private ACUFact acu = null;
    private ClassFact currentClass = null;

    public DroolsVisitor( WorkingMemory memory ) {
	this.memory = memory;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
	try {
	    acu = new ACUFact();
	    
	    memory.assertObject( acu );
	    
	    node.childrenAccept( this, data );
	    return null;
	} catch (AssertionException ase) {
	    throw new RuntimeException( ase.getMessage() );
	}
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
	try {
	    ASTName packageNameNode = (ASTName) node.jjtGetChild( 0 );

	    String packageName = packageNameNode.getImage();
	    
	    PackageFact pkg = new PackageFact( acu, packageName );
	    memory.assertObject( pkg );
	    
	    return node.childrenAccept( this, data );
	} catch (AssertionException ase) {
	    throw new RuntimeException( ase.getMessage() );
	}
    }

    public Object visit(ASTImportDeclaration node, Object data ) {
	try {
	    ASTName importNameNode = (ASTName) node.jjtGetChild( 0 );
 
	    String importName = importNameNode.getImage();
	    
	    ImportFact imp = new ImportFact( acu, importName,
					     node.isImportOnDemand(),
					     node.getBeginLine());
	    memory.assertObject( imp );
	    
	    return node.childrenAccept( this, data );
	} catch (AssertionException ase) {
	    throw new RuntimeException( ase.getMessage() );
	}
    }

    public Object visit( ASTClassBody node, Object data) {
	try {
	    ClassFact classFact = null;

	    if (node.jjtGetParent() instanceof
		ASTUnmodifiedClassDeclaration) {
		ASTUnmodifiedClassDeclaration ucDecl =
		    (ASTUnmodifiedClassDeclaration) node.jjtGetParent();
		
		String className = ucDecl.getClassName();
		classFact = new ClassFact( acu, 
					   currentClass,
					   className );
		
		if (ucDecl.jjtGetParent() instanceof AccessNode) {
		    AccessNode modifiers =
			(AccessNode) ucDecl.jjtGetParent();
		    
		    classFact.setPublic( modifiers.isPublic() );
		    classFact.setAbstract( modifiers.isAbstract() );
		    classFact.setFinal( modifiers.isFinal() );
		    classFact.setStrict( modifiers.isStrict() );
		    classFact.setStatic( modifiers.isStatic() );
		    classFact.setProtected( modifiers.isProtected() );
		    classFact.setPrivate( modifiers.isPrivate() );
		}
	    } else {
		classFact = new ClassFact( acu, currentClass,
					   null );
	    }

	    currentClass = classFact;

	    memory.assertObject( classFact );

	    return node.childrenAccept( this, data );
	} catch (AssertionException ase) {
	    throw new RuntimeException( ase.getMessage() );
	}
    }
}
