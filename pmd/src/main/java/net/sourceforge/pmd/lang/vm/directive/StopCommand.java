package net.sourceforge.pmd.lang.vm.directive;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.directive.Evaluate;

/**
 * Stop command for directive Control objects.  In an ideal JDK,
 * this would be able to extend a RuntimeThrowable class, but we
 * don't have that. So to avoid the interface changes needed by
 * extending Throwable and the potential errant catches were we
 * to extend RuntimeException, we'll have to extend Error,
 * despite the fact that this is never an error.
 *
 * @author Nathan Bubna
 * @version $Id$
 */
public class StopCommand extends Error
{
    private static final long serialVersionUID = 2577683435802825964L;
    private Object stopMe;
    private boolean nearest = false;

    public StopCommand()
    {
        this.nearest = true;
    }

    public StopCommand(String message)
    {
        super(message);
    }

    public StopCommand(Object stopMe)
    {
        this.stopMe = stopMe;
    }

    public String getMessage()
    {
        if (stopMe != null)
        {
            // only create a useful message if requested (which is unlikely)
            return "StopCommand: "+stopMe;
        }
        else
        {
            return "StopCommand: "+super.getMessage();
        }
    }

    public boolean isFor(Object that)
    {
        if (nearest) // if we're stopping at the first chance
        {
            // save that for message
            stopMe = that;
            return true;
        }
        else if (stopMe != null) // if we have a specified stopping point
        {
            return (that == stopMe);
        }
        else // only stop for the top :)
        {
            return (that instanceof Template ||
                    that instanceof RuntimeInstance ||
                    that instanceof Evaluate);
        }
    }
}
