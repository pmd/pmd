/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.util.SearchFunction;

public class ImageFinderFunction implements SearchFunction<NameDeclaration> {

    private Set<String> images = new HashSet<>();
    private NameDeclaration decl;

    public ImageFinderFunction(String img) {
        images.add(img);
    }

    public ImageFinderFunction(List<String> imageList) {
        images.addAll(imageList);
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
