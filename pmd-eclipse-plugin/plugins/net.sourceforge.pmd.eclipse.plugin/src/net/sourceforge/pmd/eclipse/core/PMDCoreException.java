/*
 * Created on 2 sept. 2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.core;

/**
 * Root exception of the CORE plug-in
 *
 * @author Herlin
 * @version $Revision$
 *
 * $Log$
 * Revision 1.1  2006/10/06 16:42:47  phherlin
 * Continue refactoring of rullesets management
 *
 *
 */

public class PMDCoreException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public PMDCoreException() {
        super();
    }

    /**
     * Constructor with a message and a root cause.
     * @param arg0 exception message.
     * @param arg1 root cause exception.
     */
    public PMDCoreException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    /**
     * Constructor with only a message.
     * @param arg0 exception message.
     */
    public PMDCoreException(String arg0) {
        super(arg0);
    }

    /**
     * Constructor with a root cause exception only
     * @param arg0 root cause exception
     */
    public PMDCoreException(Throwable arg0) {
        super(arg0);
    }

}
