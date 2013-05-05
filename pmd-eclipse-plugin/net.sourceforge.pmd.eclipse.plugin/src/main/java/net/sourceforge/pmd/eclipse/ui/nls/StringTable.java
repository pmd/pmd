/*
 * Created on 18 mai 2006
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

package net.sourceforge.pmd.eclipse.ui.nls;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.util.IOUtil;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;

/**
 * This class implements a string table.
 * This let the UI loads all displayed strings from national properties.
 * 
 * @author Herlin
 *
 */

public class StringTable {
    private static final Logger log = Logger.getLogger(StringTable.class);
    private Properties table = null;
    
    /**
     * Get a string from the string table from its key.
     * Return the key if not found.
     */
    public String getString(String key) {
        String string = null;
        final Properties table = getTable();
        if (table != null) {
            string = table.getProperty(key, key);
        }
        
        return string;
    }
    
    /**
     * Lazy load the string table
     * @return the string table
     */
    private Properties getTable() {
    	
    	if (table != null) return table;
    	
    	InputStream is = null;
        try {
            table = new Properties();
            final URL messageTableUrl = FileLocator.find(PMDPlugin.getDefault().getBundle(), new Path("$nl$/messages.properties"), null);
            if (messageTableUrl != null) {
               is = messageTableUrl.openStream();
               table.load(is);
               }
        } catch (IOException e) {
            log.error("IO Exception when loading string table", e);
        } finally {
        	IOUtil.closeQuietly(is);
        	}
        
        return table;
    }

}
