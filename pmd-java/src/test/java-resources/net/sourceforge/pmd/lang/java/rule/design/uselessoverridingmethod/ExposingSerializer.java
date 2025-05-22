/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.uselessoverridingmethod;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.w3c.dom.Node;

class ExposingSerializer extends Serializer {


    ExposingSerializer(OutputStream out, String encoding) throws UnsupportedEncodingException {
        super(out, encoding);
    }


    /**
     * Overriding in order to change the access modifier from protected to public - so: not only merely calling super.
     *
     * <p>Method signature in super class: protected void writeChild(nu.xom.Node arg0) throws java.io.IOException;
     *
     * <p>See: https://sourceforge.net/tracker/?func=detail&aid=1415525&group_id=56262&atid=479921
     */
    public void writeChild(Node node) throws IOException {
        super.writeChild(node);
    }
}
