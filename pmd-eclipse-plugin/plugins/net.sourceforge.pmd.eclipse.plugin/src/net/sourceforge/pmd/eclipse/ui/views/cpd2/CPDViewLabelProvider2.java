/*
 * Created on 13.10.2006
 *
 * Copyright (c) 2006, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.pmd.eclipse.ui.views.cpd2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.TokenEntry;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;

/**
 * 
 * 
 * @author Brian Remedios
 */

public class CPDViewLabelProvider2 extends LabelProvider implements ITableLabelProvider {

    /*
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
     */
    public Image getColumnImage(Object element, int columnIndex) {
        Image image = null;
        
        final TreeNode node = (TreeNode) element;
        final Object value = node.getValue();
        
        // the second Column gets an Image depending on,
        // if the Element is a Match or TokenEntry
        switch (columnIndex) {
        case 0:
            if (value instanceof Match) {               
             //   image = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            } else if (value instanceof TokenEntry) {
                image = PlatformUI.getWorkbench().getSharedImages().getImage(SharedImages.IMG_OPEN_MARKER);
            }
            break;

        default:
            // let the image null.

        }

        return image;
    }

    private int lineCountFor(TreeNode node) {
    	
    	Object source = node.getValue();
    
    	 if (source instanceof Match) {
             return node.getChildren().length;
    	 }
    	 
    	 return -1;
    }
    
    public static String pathFor(TokenEntry entry) {
    	
       final IPath path = Path.fromOSString(entry.getTokenSrcID());
       final IResource resource = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(path);
       if (resource != null) {
           return resource.getProjectRelativePath().removeFileExtension().toString().replace(IPath.SEPARATOR, '.');
       } else {
    	   return "?";
       }
    }
    
    public static TokenEntry[] entriesFor(Match match) {

    	Set<TokenEntry> entrySet = match.getMarkSet();
    	TokenEntry[] entries = new TokenEntry[entrySet.size()];
    	entries = entrySet.toArray(entries);
    	Arrays.sort(entries);
    	
    	return entries;
    }
    
    public static String[] sourcesFor(Match match) {
    	
    	TokenEntry[] entries = entriesFor(match);
    	
    	String[] classNames = new String[entries.length];
    	
    	int i = 0;
    	for (TokenEntry entry : entries) {
    		classNames[i++] = pathFor(entry);
    	}
    	
    	return classNames;
    }
    
    public static Map<String, TokenEntry> entriesByClassnameFor(Match match) {
    	
    	TokenEntry[] entries = entriesFor(match);
    	Map<String, TokenEntry> entriesByName = new HashMap<String, TokenEntry>(entries.length);
    	
    	for (TokenEntry entry : entries) {
    		entriesByName.put( pathFor(entry), entry);
    	}
    	
    	return entriesByName;
    }
    
    /*
     * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
     */
    public String getColumnText(Object element, int columnIndex) {
        final TreeNode node = (TreeNode) element;
        final Object value = node.getValue();
        String result = "";

        switch (columnIndex) {
        case 0: int count = lineCountFor(node);
        		if (count > 0) result = Integer.toString(count);
        		break;
        // show the source 
        case 1:
            if (value instanceof String) {          
                result = String.valueOf(value);
                if (result.endsWith("\r")) result = result.substring(0, result.length()-1);
            }
            if (value instanceof Match) {          
           //     do nothing, let the painter show it
            }
            break;
        default:
            // let text empty
        }

        return result;
    }

}
