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
package pmd.config.ui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * Data model for the ruleset list UI.
 */
public class RuleSetListModel extends AbstractListModel{
	
	private List list = new ArrayList();
	
	public Object getElementAt(int param) {
		return list.get( param );
	}	

	public int getSize() {
		return list.size();
	}	
	
	public List getList() {
		return list;
	}
	
	public boolean contains( Object o ) {
		return list.contains( o );
	}
	
	public void removeElementAt( int i ) {
		list.remove( i );
		fireIntervalRemoved( this, i, i+1 );
	}
	
	public void addElement( Object o ) {
		list.add( o );
		fireIntervalAdded(o, getSize()-1, getSize() );
	}
	
	public void setList( List list ) {
		this.list = list;
		fireIntervalAdded( list, 0, list.size() );
	}
	
}
