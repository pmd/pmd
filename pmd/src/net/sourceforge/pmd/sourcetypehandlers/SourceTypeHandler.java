package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.Parser;

/**
 * Interface for obtaining the classes necessary for checking source files
 * of a specific language.
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface SourceTypeHandler {
	
	/**
	 * Get the Parser.
	 * @return
	 */
	Parser getParser();
	
	/**
	 * Get the DataFlowFacade.
	 * @return
	 */
	VisitorStarter getDataFlowFacade();
	
	/**
	 * Get the SymbolFacade.
	 * @return
	 */
	VisitorStarter getSymbolFacade();
}
