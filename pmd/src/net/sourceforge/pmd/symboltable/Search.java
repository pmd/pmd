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

public class Search {
    private static final boolean TRACE = false;

    private NameOccurrence occ;
    private NameDeclaration decl;

    public Search(NameOccurrence occ) {
        if (TRACE)
            System.out.println("new search for " + occ);
        this.occ = occ;
    }

    public void execute() {
        decl = searchUpward(occ, occ.getScope());
        if (TRACE)
            System.out.println("found " + decl);
    }

    public void execute(Scope startingScope) {
        decl = searchUpward(occ, startingScope);
        if (TRACE)
            System.out.println("found " + decl);
    }

    public NameDeclaration getResult() {
        return decl;
    }

    private NameDeclaration searchUpward(NameOccurrence nameOccurrence, Scope scope) {
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            if (TRACE)
                System.out.println("moving up fm " + scope + " to " + scope.getParent());
            return searchUpward(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            return scope.addVariableNameOccurrence(nameOccurrence);
        }
        return null;
    }
}
