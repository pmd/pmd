/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd;

import java.util.List;
import java.util.Properties;

public interface Rule {
    public static final int LOWEST_PRIORITY = 5;
    public static final String[] PRIORITIES = {"High", "Medium High", "Medium", "Medium Low", "Low"};

    String getName();

    String getMessage();

    String getDescription();

    String getExample();

    void setName(String name);

    void setMessage(String message);

    void setDescription(String description);

    void setExample(String example);

    void apply(List astCompilationUnits, RuleContext ctx);

    boolean hasProperty(String name);

    void addProperty(String name, String property);

    int getIntProperty(String name);

    boolean getBooleanProperty(String name);

    String getStringProperty(String name);

    double getDoubleProperty(String name);

    Properties getProperties();

    boolean include();

    void setInclude(boolean include);

    int getPriority();

    String getPriorityName();

    void setPriority(int priority);
}
