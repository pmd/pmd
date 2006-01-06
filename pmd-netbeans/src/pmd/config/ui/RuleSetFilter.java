/*
 *  Copyright (c) 2002-2006, the pmd-netbeans team
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *  LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 *  OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 *  DAMAGE.
 */
package pmd.config.ui;

import javax.swing.filechooser.FileFilter;

/**
 * Filename filter for accepting ruleset XML files (.xml) or ruleset JAR files (.jar).
 */
public class RuleSetFilter extends FileFilter {
	
	public static final int JARS = 1;
	public static final int RULESETS = 2;
	
	private int type;
	public RuleSetFilter( int type ) {
		this.type = type;
	}
	public boolean accept(java.io.File file) {
		if( file.isDirectory() ) {
			return true;
		}
		if( type == RULESETS ) {
			return file.getName().toLowerCase().endsWith( "xml" );
		}
		else {
			return file.getName().toLowerCase().endsWith( "jar" );
		}
	}	

	public String getDescription() {
		if( type == RULESETS ) {
			return "RuleSet (*.xml)";
		}
		else {
			return "Rules (*.jar)";
		}
	}
	
}
