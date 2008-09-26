/*
 * Created on 7 juin 2005
 *
 * Copyright (c) 2005, PMD for Eclipse Development Team
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
package net.sourceforge.pmd.core;

import net.sourceforge.pmd.eclipse.plugin.PMDActivator;

/**
 * The plugin class for the Core PMD Plugin.
 *
 * @author Philippe Herlin
 * @version $Revision$
 *
 * $Log: PMDCorePlugin.java,v $
 * Revision 1.6  2007/06/24 15:07:29  phherlin
 * Integrate PMD v4.0rc1
 *
 * Revision 1.5  2007/01/18 20:58:27  phherlin
 * Integrate PMD v3.9
 * Revision 1.4 2006/06/20 21:04:49 phherlin Enable
 * PMD and fix error level violations
 *
 * Revision 1.3 2006/04/10 20:58:18 phherlin Update to PMD 3.6
 *
 * Revision 1.2 2005/07/02 14:33:05 phherlin Implement the RuleSets extension
 * point
 *
 * Revision 1.1 2005/06/07 22:39:57 phherlin Implementing extra ruleset
 * declaration
 *
 *
 */
public class PMDCorePlugin {

	/**
	 * Returns the shared instance.
	 */
	public static PMDActivator getDefault() {
		return PMDActivator.getDefault();
	}

}
