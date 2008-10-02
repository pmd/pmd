/*
 * Created on 21 juin 2006
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

package net.sourceforge.pmd.eclipse.core.rulesets.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.core.PMDCoreException;
import net.sourceforge.pmd.eclipse.core.rulesets.IRuleSetsManager;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.Rule;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSet;
import net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * Implementation of an IRuleSetsManager.
 * The serialization is based on the usage of Castor.
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.2  2006/10/06 16:42:46  phherlin
 * Continue refactoring of rullesets management
 *
 * Revision 1.1  2006/06/21 23:06:41  phherlin
 * Move the new rule sets management to the core plugin instead of the runtime.
 * Continue the development.
 *
 *
 */

public class RuleSetsManagerImpl implements IRuleSetsManager {
    private static final Log LOG = LogFactory.getLog(RuleSetsManagerImpl.class);
    private static final String RULESETS_MAPPING = "/net/sourceforge/pmd/eclipse/core/rulesets/impl/mapping.xml";

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.eclipse.core.rulesets.IRuleSetsManager#readFromXml(java.io.InputStream)
     */
    public RuleSets readFromXml(InputStream input) throws PMDCoreException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @throws RuleSetNotFoundException 
     * @see net.sourceforge.pmd.eclipse.core.rulesets.IRuleSetsManager#valueOf(java.lang.String[])
     */
    public RuleSet valueOf(String[] ruleSetUrls) throws PMDCoreException {
        LOG.debug("Compting value of a collection of rule set urls");
        if (ruleSetUrls == null) {
            throw new IllegalArgumentException("ruleSetUrls cannot be null");
        }
        if (ruleSetUrls.length == 0) {
            throw new IllegalArgumentException("ruleSetsUrls cannot be empty");
        }

        try {
            final RuleSet ruleSet = new RuleSet();
            
            for (int i = 0; i < ruleSetUrls.length; i++) {
                final RuleSetFactory factory = new RuleSetFactory(); // NOPMD by Herlin on 21/06/06 23:25
                final Collection rules = factory.createSingleRuleSet(ruleSetUrls[i]).getRules();
                for (final Iterator j = rules.iterator(); j.hasNext();) {
                    final net.sourceforge.pmd.Rule pmdRule = (net.sourceforge.pmd.Rule) j.next();
                    final Rule rule = new Rule(); // NOPMD by Herlin on 21/06/06 23:29
                    rule.setRef(ruleSetUrls[i] + '/' + pmdRule.getName());
                    rule.setPmdRule(pmdRule);
                    ruleSet.addRule(rule);
                }
            }
            
            return ruleSet;

        } catch (RuleSetNotFoundException e) {
            LOG.error("A RuleSetsNotFound Exception was thrown.");
            throw new PMDCoreException("A RuleSetsNotFound Exception was thrown.", e);
        }
    }

    /**
     * @see net.sourceforge.pmd.eclipse.core.rulesets.IRuleSetsManager#writeToXml(net.sourceforge.pmd.eclipse.core.rulesets.vo.RuleSets, java.io.OutputStream)
     */
    public void writeToXml(RuleSets ruleSets, OutputStream output) throws PMDCoreException {
        LOG.debug("Storing plug-in rulesets");
        try {
            LocalConfiguration.getInstance().getProperties().setProperty("org.exolab.castor.indent", "true");

            final Mapping mapping = new Mapping(this.getClass().getClassLoader());
            final URL mappingSpecUrl = this.getClass().getResource(RULESETS_MAPPING);
            mapping.loadMapping(mappingSpecUrl);

            final StringWriter writer = new StringWriter();
            final Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);
            marshaller.marshal(ruleSets);
            writer.flush();
            writer.close();
            
            output.write(writer.getBuffer().toString().getBytes());
            output.flush();

        } catch (MarshalException e) {
            LOG.error("A Marshal Exception was thrown.");
            throw new PMDCoreException("A Marshal Exception was thrown.", e);
        } catch (ValidationException e) {
            LOG.error("A Validation Exception was thrown.");
            throw new PMDCoreException("A Validation Exception was thrown.", e);
        } catch (MappingException e) {
            LOG.error("A Mapping Exception was thrown.");
            throw new PMDCoreException("A Mapping Exception was thrown.", e);
        } catch (IOException e) {
            LOG.error("A IO Exception was thrown.");
            throw new PMDCoreException("A IO Exception was thrown.", e);
        }
    }
}
