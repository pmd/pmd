package net.sourceforge.pmd.rx.rules;

import net.sourceforge.pmd.rx.facts.*;

import org.drools.*;
import org.drools.spi.*;
import org.drools.semantic.java.*;

public class DuplicateImport
    extends org.drools.spi.Rule
    implements Action, FilterCondition
{
    private Declaration req[] = new Declaration[2];

    public DuplicateImport() {
	super("DuplicateImport");
	    
	try {
	    req[0] = new Declaration( new JavaObjectType( ImportFact.class ),
				      "import-0" );
	    req[1] = new Declaration( new JavaObjectType( ImportFact.class ),
				      "import-1" );
	    
	    addParameterDeclaration( req[0] );
	    addParameterDeclaration( req[1] );
	    
	    addFilterCondition( this );

	    setAction( this );

	} catch (DeclarationAlreadyCompleteException dce) {
	    // Shouldn't happen. . .
	    dce.printStackTrace();
	    throw new RuntimeException( dce.getMessage() );
	}
    }

    // FilterCondition
    public Declaration[] getRequiredTupleMembers() {
	return req;
    }
    
    public String getPackage( ImportFact impFact ) {
	if (impFact.isOnDemand()) {
	    return impFact.getImportPackage();
	} else {
	    String impPack = impFact.getImportPackage();
	    return impPack.substring( 0, 
				      impPack.lastIndexOf("."));
	}
    }

    public boolean isAllowed( Tuple tuple ) {
	ImportFact import0 = (ImportFact) tuple.get( req[0] );
	ImportFact import1 = (ImportFact) tuple.get( req[1] );

	if (import0.getACU() != import1.getACU()) {
	    return false;
	}
	
	if (import0.getLineNumber() == import1.getLineNumber()) {
	    return false;
	}

	if (import0.isOnDemand()) {
	    return getPackage( import0 ).equals( getPackage( import1 ));
	}

	if (import1.isOnDemand()) {
	    return getPackage( import0 ).equals( getPackage( import1 ));
	}

	return import0.getImportPackage().equals( import1.getImportPackage());
    }

    public void invoke(Tuple tuple,
		       WorkingMemory memory ) {
	try {
	    System.err.println("---- invoke ----");
	    System.err.println( tuple.get(req[0]) );
	    System.err.println( tuple.get(req[1]) );

	    if (tuple.get(req[0]) == null) return;
	    if (tuple.get(req[1]) == null) return;

	    if (isAllowed( tuple )) {
		ImportFact import0 = (ImportFact) tuple.get(req[0]);
		ImportFact import1 = (ImportFact) tuple.get(req[1]);

		memory.assertObject(new RuleViolationFact( import0,
						    "Duplicate Import" ));
		memory.assertObject(new RuleViolationFact( import1,
						    "Duplicate Import" ));
	    } else { return; }
	} catch (AssertionException ase) {
	    ase.printStackTrace();
	    throw new RuntimeException( ase.getMessage());
	}
    }
}
