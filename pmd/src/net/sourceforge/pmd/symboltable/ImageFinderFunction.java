/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.UnaryFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageFinderFunction implements UnaryFunction {

    private Set images = new HashSet();
    private NameDeclaration decl;

    public ImageFinderFunction(String img) {
        images.add(img);
    }

    public ImageFinderFunction(List imageList) {
        images.addAll(imageList);
    }

    public void applyTo(Object o) {
        NameDeclaration nameDeclaration = (NameDeclaration) o;
        if (images.contains(nameDeclaration.getImage())) {
            decl = nameDeclaration;
        }
    }

    public NameDeclaration getDecl() {
        return this.decl;
    }
}
