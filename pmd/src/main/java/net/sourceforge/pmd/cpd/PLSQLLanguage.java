/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @author Stuart Turton sturton@users.sourceforge.net
 */
package net.sourceforge.pmd.cpd;

public class PLSQLLanguage extends AbstractLanguage {
	public PLSQLLanguage() {
		super(new PLSQLTokenizer()
		      ,".sql"
		      ,".trg" //Triggers
		      ,".prc",".fnc" // Standalone Procedures and Functions 
		      ,".pld" // Oracle*Forms 
		      ,".pls" ,".plh" ,".plb" // Packages
		      ,".pck" ,".pks" ,".pkh" ,".pkb" // Packages
		      ,".typ" ,".tyb" // Object Types
		      ,".tps" ,".tpb" // Object Types
		     );
	}
}
