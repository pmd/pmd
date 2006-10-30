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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 * An annotation implementation marking the line where a PMD rule violation was found.
 * This class tracks all constructed instances of this annotation type, and can remove
 * any of them. It listens to property-change events from the lines at which the
 * annotations are attached, and removes the annotations when the lines change or are
 * removed.
 */
public class PMDAnnotation extends Annotation implements PropertyChangeListener {

	/** The error message shown on mouseover on the pmd icon */
	private String errormessage = null;
	
	/** The annotations currently existing. */
	private static List<PMDAnnotation> annotations = new ArrayList<PMDAnnotation>();
	
	private PMDAnnotation() {}
	
	public static final PMDAnnotation getNewInstance() {
		PMDAnnotation pmd = new PMDAnnotation();
		annotations.add( pmd );
		return pmd;
	}
	
	public static final void clearAll() {
        for (PMDAnnotation anno: annotations) {
			anno.detach();
		}
		annotations.clear();
	}

	/**
	 * The annotation type.
	 *
	 * @return the string "pmd-annotation"
	 */
	public String getAnnotationType() {
		return "pmd-annotation";
	}


	/**
	 * Sets the current errormessage
	 *
	 * @param message the errormessage
	 */
	public void setErrorMessage( String message ) {
		errormessage = message;
	}


	/**
	 * A short description of this annotation
	 *
	 * @return the short description
	 */
	public String getShortDescription() {
		return errormessage;
	}


	/**
	 * Invoked when the user change the content on the line where the annotation is
	 * attached
	 *
	 * @param propertyChangeEvent the event fired
	 */
	public void propertyChange( PropertyChangeEvent propertyChangeEvent ) {
		Line line = ( Line )propertyChangeEvent.getSource();
		line.removePropertyChangeListener( this );
		detach();
	}

}
