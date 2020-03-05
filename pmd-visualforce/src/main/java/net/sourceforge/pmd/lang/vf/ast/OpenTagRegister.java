/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Utility class to keep track of unclosed tags. The mechanism is rather simple.
 * If a end tag (&lt;/x&gt;) is encountered, it will iterate through the open
 * tag list and it will mark the first tag named 'x' as closed. If other tags
 * have been opened after 'x' ( &lt;x&gt; &lt;y&gt; &lt;z&gt; &lt;/x&gt;) it
 * will mark y and z as unclosed.
 *
 * @author Victor Bucutea
 *
 */
@Deprecated
@InternalApi
public class OpenTagRegister {

    private List<ASTElement> tagList = new ArrayList<>();

    public void openTag(ASTElement elm) {
        if (elm == null || StringUtil.isEmpty(elm.getName())) {
            throw new IllegalStateException("Tried to open a tag with empty name");
        }

        tagList.add(elm);
    }

    /**
     *
     * @param closingTagName
     * @return true if a matching tag was found. False if no tag with this name
     *         was ever opened ( or registered )
     */
    public boolean closeTag(String closingTagName) {
        if (StringUtil.isEmpty(closingTagName)) {
            throw new IllegalStateException("Tried to close a tag with empty name");
        }

        int lastRegisteredTagIdx = tagList.size() - 1;
        /*
         * iterate from top to bottom and look for the last tag with the same
         * name as element
         */
        boolean matchingTagFound = false;
        List<ASTElement> processedElmnts = new ArrayList<>();
        for (int i = lastRegisteredTagIdx; i >= 0; i--) {
            ASTElement parent = tagList.get(i);
            String parentName = parent.getName();

            processedElmnts.add(parent);
            if (parentName.equals(closingTagName)) {
                // mark this tag as being closed
                parent.setUnclosed(false);
                // tag has children it cannot be empty
                parent.setEmpty(false);
                matchingTagFound = true;
                break;
            } else {
                // only mark as unclosed if tag is not
                // empty (e.g. <tag/> is empty and properly closed)
                if (!parent.isEmpty()) {
                    parent.setUnclosed(true);
                }

                parent.setEmpty(true);
            }
        }

        /*
         * remove all processed tags. We should look for rogue tags which have
         * no start (unopened tags) e.g. " <a> <b> <b> </z> </a>" if "</z>" has
         * no open tag in the list (and in the whole document) we will consider
         * </a> as the closing tag for <a>.If on the other hand tags are
         * interleaved: <x> <a> <b> <b> </x> </a> then we will consider </x> the
         * closing tag of <x> and </a> a rogue tag or the closing tag of a
         * potentially open <a> parent tag ( but not the one after the <x> )
         */
        if (matchingTagFound) {
            tagList.removeAll(processedElmnts);
        }

        return matchingTagFound;
    }

    public void closeTag(ASTElement z) {
        closeTag(z.getName());
    }
}
