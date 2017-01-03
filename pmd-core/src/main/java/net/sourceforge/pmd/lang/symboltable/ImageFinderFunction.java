/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.symboltable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.util.SearchFunction;

public class ImageFinderFunction implements SearchFunction<NameDeclaration> {

    private final Set<String> images;
    private NameDeclaration decl;

    public ImageFinderFunction(String img) {
        images = Collections.singleton(img);
    }

    public ImageFinderFunction(List<String> imageList) {
        images = new HashSet<>(imageList);
    }

    @Override
    public boolean applyTo(NameDeclaration nameDeclaration) {
        if (images.contains(nameDeclaration.getImage())) {
            decl = nameDeclaration;
            return false;
        }
        return true;
    }

    public NameDeclaration getDecl() {
        return this.decl;
    }
}
