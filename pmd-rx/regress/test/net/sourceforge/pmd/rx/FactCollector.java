package test.net.sourceforge.pmd.rx;

import java.util.Set;
import java.util.HashSet;

import org.drools.*;
import org.drools.spi.*;
import org.drools.semantic.java.*;

public class FactCollector
    extends org.drools.spi.Rule
    implements Action, FilterCondition
{
    private Class clazz = null;
    private Declaration req[] = new Declaration[1];
    private Set facts = new HashSet();

    public FactCollector( Class clazz ) {
	super( "FactCollector" );
	
	try {
	    this.clazz = clazz;
	    req[0] = new Declaration( new JavaObjectType( clazz ), "item" );
	    
	    addParameterDeclaration( req[0] );
	    addFilterCondition( this );
	    setAction( this );
	} catch (DeclarationAlreadyCompleteException dce) {
	    // This should never happen. . .
	    dce.printStackTrace();
	    throw new RuntimeException( dce.getMessage() );
	}
    }

    // FilterCondition . . .
    public Declaration[] getRequiredTupleMembers() {
	return req;
    }

    public boolean isAllowed(Tuple tuple) {
	Object o = tuple.get( req[0] );
	return clazz.isInstance( o );
    }

    // Action
    public void invoke( Tuple tuple,
			WorkingMemory memory ) {
	facts.add( tuple.get( req[0] ));
    }

    // Normal stuff.
    public Set getFacts() {
	return facts;
    }
}
