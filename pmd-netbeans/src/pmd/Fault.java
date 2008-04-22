/*
 *  Copyright (c) 2002-2003, the pmd-netbeans team
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
package pmd;

import net.sourceforge.pmd.PMDException;

/**
 * Represents a PMD rule violation.
 */
public class Fault implements Comparable<Fault> {

    private final int line;
    private final String clazz;
    private final String message;

    /**
     * Creates a new instance of Fault
     *
     * @param line the line of the fault
     * @param clazz the class of the fault
     * @param message the pmd message
     */
    public Fault(int line, String clazz, String message) {
        this.line = line;
        this.message = message;
        this.clazz = clazz;
    }

    /**
     * Creates a new instance of Fault
     *
     * @param line the line of the fault
     * @param clazz the class of the fault
     * @param exception the PMD exception on which the fault is based, not null
     */
    public Fault(int line, String clazz, PMDException exception) {
        this.line = line;
        Throwable reason = exception.getCause();
        if (reason == null) {
            this.message = exception.getMessage();
        } else if (reason.getMessage() == null) {
            this.message = exception.getMessage() + "; " + reason.toString();
        } else {
            this.message = exception.getMessage() + "; " + reason.getMessage();
        }
        this.clazz = clazz;
    }


	/**
	 * Compares <code>obj</code> to <code>this</code>. Sorts by linenumber
	 *
	 * @param object the other object
	 * @return this.linenumber - that.linenumber
	 */
	public int compareTo( Fault object ) {
		int compared = 0;
        compared = clazz.compareTo( object.clazz );
        if (compared == 0 ) {
            compared = line - object.line;
        }
		return compared;
	}


	/**
	 * Returns a string representation of this object
	 *
	 * @return the fault as a string @see#getFault()
	 */
	public String toString() {
		return getFault();
	}


	/**
	 * Returns the fault as listed in the output pane
	 *
	 * @return the fault as a string
	 */
	public String getFault() {
		return clazz + " [" + line + "]: " + message;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Parses the fault and returns the linenumber
	 *
	 * @param fault the fault
	 * @return the linenumber @see#getFault()
	 */
	public static int getLineNum( String fault ) {
		return Integer.parseInt( fault.substring( fault.indexOf( '[' ) + 1, fault.indexOf( ']' ) ) );
	}


	/**
	 * Parses the fault and returns the errormessage
	 *
	 * @param fault the output message
	 * @return The errormessage
	 */
	public static String getErrorMessage( String fault ) {
		return fault.substring( fault.indexOf( ":" ) + 2 );
	}
	
	public int getLine() {
		return line;
	}
	
}
