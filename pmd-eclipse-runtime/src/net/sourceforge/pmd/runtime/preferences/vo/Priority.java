/*
 * Created on 17 juin 2006
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

package net.sourceforge.pmd.runtime.preferences.vo;

/**
 * This class is a value objet that composes the structure of a rulesets object.
 * It holds a priority definition, ie a integer value that describes the level
 * of a rule. This class if fundamentally an enumeration that is implemented as
 * a type safe enumeration, but with constraints of Java Beans to allow
 * serialization. (Note: we are still in JDK 1.5 and we don't use enum yet).
 * 
 * @author Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2006/06/18 22:33:02  phherlin
 * Begin to implement a new model for the plugin to handle rules and rulesets.
 *
 * 
 */

public class Priority {
    public static final int LEVEL1_LITTERAL = 1;
    public static final Priority LEVEL1 = new Priority(LEVEL1_LITTERAL);
    public static final int LEVEL2_LITTERAL = 2;
    public static final Priority LEVEL2 = new Priority(LEVEL2_LITTERAL);
    public static final int LEVEL3_LITTERAL = 3;
    public static final Priority LEVEL3 = new Priority(LEVEL3_LITTERAL);
    public static final int LEVEL4_LITTERAL = 4;
    public static final Priority LEVEL4 = new Priority(LEVEL4_LITTERAL);
    public static final int LEVEL5_LITTERAL = 5;
    public static final Priority LEVEL5 = new Priority(LEVEL5_LITTERAL);

    private int priority = LEVEL3_LITTERAL;

    /**
     * Default constructor to be compatible with Java Beans definition and to
     * allow serialization. To be legal, the priority is set to default level 3.
     * 
     */
    public Priority() {
        super();
    }

    /**
     * Enumeration constructor as defined by the Type Safe Enumeration idiom.
     * 
     * @param priority the priority level
     */
    private Priority(int priority) {
        super();
        this.priority = priority;
    }

    /**
     * Getter for the priority value. Defined only to be compatible with Java
     * Beans
     * 
     * @return Returns the priority.
     */
    public int getPriorityValue() {
        return this.priority;
    }

    /**
     * Setter for the priority value. Defined only to be compatible with Java
     * Beans
     * 
     * @param priority The priority to set.
     */
    public void setPriority(int priority) {
        if ((priority < LEVEL1_LITTERAL) || (priority > LEVEL5_LITTERAL)) {
            throw new IllegalArgumentException("priority value invalid ; was " + priority + " and should be between "
                    + LEVEL1_LITTERAL + " and " + LEVEL5_LITTERAL);
        }

        this.priority = priority;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object arg0) {
        boolean equal = false;

        if (arg0 instanceof Priority) {
            Priority p = (Priority) arg0;
            equal = p.priority == this.priority;
        }

        return equal;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new Integer(this.priority).hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "priority value=" + this.priority;
    }

}
