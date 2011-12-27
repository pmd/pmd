package net.sourceforge.pmd;


/**
 * A descriptor for a command line option along with a functor for applying it to
 * a typed configuration target.
 * 
 * TODO future: incorporate command-specific error handling
 * 
 * @author Brian Remedios
 * @param <T>
 */
public class CmdLineOption<T extends Object> {

	public final String		id;
	public final String		description;
	public final int		parameterCount;
	
	private final Applicator<T> applicator;
	
	public interface Applicator<T> {
		public void apply(T config, String[] args, int idx);
	}
	
	/**
	 * Usually used for boolean-type parameters that enable a specific capability
	 * 
	 * @param theId
	 * @param theDescription
	 * @param theApplicator
	 */
	public CmdLineOption(String theId, String theDescription, Applicator<T> theApplicator) {
		this(theId, theDescription, 0, theApplicator);
	}
	
	/**
	 * Used for capabilities that use additional follow-on parameters
	 * 
	 * @param theId
	 * @param theDescription
	 * @param paramCount
	 * @param theApplicator
	 */
	public CmdLineOption(String theId, String theDescription, int paramCount, Applicator<T> theApplicator) {
		id = theId;
		description = theDescription;
		parameterCount = paramCount;
		applicator = theApplicator;
	}

	public void apply(T config, String[] args, int idx) {
		applicator.apply(config, args, idx);
	}

}
