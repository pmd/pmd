/*
 * Copyright (c) 2002, Ole-Martin Mørk
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
package pmd;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.text.Annotation;
import org.openide.text.Line;

/**
 * Just a class thats mission is to mark the line where the
 * error is. It's using pmd-annotation type to mark the line
 * @author  ole martin mørk
 */
public class PMDAnnotation extends Annotation implements PropertyChangeListener {
    
	/** The error message shown on mouseover on the pmd icon */
	private String errormessage = null;
	
    /**
     * The annotation type.
     * @return org-netbeans-core-compiler-error
     */
    public String getAnnotationType() {
        return "pmd-annotation";
    }
    
	/**
	 * Sets the current errormessage
	 * @param message the errormessage
	 */
	public void setErrorMessage( String message ) {
		errormessage = message;
	}
	
    /**
     * A short description of this annotation
     * @return the short description
     */
    public String getShortDescription() {
        return errormessage;
    }
    
	/**
	 * Invoked when the user change the content on the line where the
	 * annotation is attached
	 * @param propertyChangeEvent the event fired
	 */
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        Line line = (Line)propertyChangeEvent.getSource();
        line.removePropertyChangeListener( this );
        detach();
    }
}