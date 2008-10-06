/*
 * Created on 18 juin 2006
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

package net.sourceforge.pmd.eclipse.core.rulesets.vo;

/**
 * This class is a value objet that composes the structure of a rulesets object.
 * It holds the definition of a rule which is actually a reference to a known
 * ruleset with some overriden information such as properties and message.
 * 
 * @author Herlin
 * 
 */

public class Rule {
    private String ref = "";
    private String message;
    private Priority priority;
    private Properties properties;
    private net.sourceforge.pmd.Rule pmdRule;

    /**
     * Getter for the message attribute. A null value means the message is not
     * overriden.
     * 
     * @return Returns the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Setter for the message attribute.
     * 
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter for the priority attribute. A null value means it is not
     * overriden.
     * 
     * @return Returns the priority.
     */
    public Priority getPriority() {
        return this.priority;
    }

    /**
     * Setter for the priority attribute.
     * 
     * @param priority The priority to set.
     */
    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    /**
     * Getter for the properties attribute. A null value or an empty set means
     * no property is overriden.
     * 
     * @return Returns the properties.
     */
    public Properties getProperties() {
        return this.properties;
    }

    /**
     * Setter for the properties attribute.
     * 
     * @param properties The properties to set.
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Getter for the ref attribute. It cannot be null and should not be an
     * empty String. If it is an empty String, that means it has not been
     * initialized and should be considered an invalid.
     * 
     * @return Returns the ref.
     */
    public String getRef() {
        return this.ref;
    }

    /**
     * Setter for the ref attribute. Setting a null value or an empty string is
     * not allowed.
     * 
     * @param ref The ref to set.
     */
    public void setRef(String ref) {
        if (ref == null) {
            throw new IllegalArgumentException("ref cannot be null");
        }
        if (ref.trim().length() == 0) { // NOPMD by Herlin on 20/06/06 23:25
            throw new IllegalArgumentException("ref cannot be an empty string");
        }

        this.ref = ref;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        boolean equal = false;

        if (arg0 instanceof Rule) {
            final Rule r = (Rule) arg0;
            equal = this.ref.equals(r.ref);
            equal = equal && (((this.priority == null) && (r.priority == null)) || (this.priority.equals(r.priority)));
            equal = equal && (((this.properties == null) && (r.properties == null)) || (this.properties.equals(r.properties)));
        }

        return equal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int hashCode = this.ref.hashCode();
        if (this.priority != null) {
            hashCode += this.priority.hashCode() * 13 * 13;
        }
        if (this.properties != null) {
            hashCode += this.properties.hashCode() * 21 * 21;
        }
        
        return hashCode;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Rule ref=" + this.ref + " message=" + this.message + " priority=" + this.priority.toString() + " properties="
                + this.properties.toString();
    }

    /**
     * Getter for the pmdRule attribute.
     * This is the PMD rule that is linked to this rule reference.
     * 
     * @return Returns the PMD Rule object.
     */
    public net.sourceforge.pmd.Rule getPmdRule() {
        return this.pmdRule;
    }

    /**
     * Setter for the PMD Rule associated with this rule reference.
     * 
     * @param pmdRule The PMD Rule to set. Cannot be null
     */
    public void setPmdRule(net.sourceforge.pmd.Rule pmdRule) {
        if (pmdRule == null) {
            throw new IllegalArgumentException("pmdRule cannot be null");
        }

        this.pmdRule = pmdRule;
    }

}
