/*
 *  Copyright (c) 2002-2003, Ole-Martin Mørk
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

import java.util.Comparator;
import net.sourceforge.pmd.Rule;

/**
 * A comparator used to sort the rules in the listbox in the option panel.
 * @author  ole martin mørk
 */
public class RuleComparator implements Comparator{
	
	/**
	 * Compares the name of the first rule to the second
	 * @param rule1 the first rule
	 * @param rule2 the second rule
	 * @return rule1.compareTo( rule2 )
	 */
	public int compare(Object rule1, Object rule2) {
		int compared = 0;
		if( rule1 instanceof Rule && rule2 instanceof Rule ) {
			compared =  ( (Rule)rule1).getName().compareTo( ( (Rule)rule2).getName() );
		}
		return compared;
	}	
}