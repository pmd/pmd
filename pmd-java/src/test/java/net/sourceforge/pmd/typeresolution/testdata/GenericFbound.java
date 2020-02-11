/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

/**
 * @author Clément Fournier
 */
public class GenericFbound<T extends GenericFbound<T>> {

    public class Inst extends GenericFbound<Inst> {

    }

    public class InstRaw extends GenericFbound {

    }

    public class InstRec extends GenericFbound<T> {

    }

}
