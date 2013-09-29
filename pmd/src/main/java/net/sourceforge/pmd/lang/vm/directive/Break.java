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

import java.io.Writer;

import net.sourceforge.pmd.lang.vm.util.LogUtil;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.Node;

/**
 * Break directive used for interrupting scopes.
 *
 * @author <a href="mailto:wyla@removethis.sci.fi">Jarkko Viinamaki</a>
 * @author Nathan Bubna
 * @version $Id$
 */
public class Break extends Directive
{
    private boolean scoped = false;

    /**
     * Return name of this directive.
     * @return The name of this directive.
     */
    public String getName()
    {
        return "break";
    }

    /**
     * Return type of this directive.
     * @return The type of this directive.
     */
    public int getType()
    {
        return LINE;
    }

    /**
     * Since there is no processing of content,
     * there is never a need for an internal scope.
     */
    public boolean isScopeProvided()
    {
        return false;
    }

    /**
     *  simple init - init the tree and get the elementKey from
     *  the AST
     * @param rs
     * @param context
     * @param node
     * @throws TemplateInitException
     */
    public void init(RuntimeServices rs, InternalContextAdapter context, Node node)
    {
        super.init(rs, context, node);

        int kids = node.jjtGetNumChildren();
        if (kids > 1)
        {  
            throw new VelocityException("The #stop directive only accepts a single scope object at "
                 + LogUtil.formatFileString(this));
        }
        else
        {
            this.scoped = (kids == 1);
        }
    }

    /**
     * Break directive does not actually do any rendering. 
     * 
     * This directive throws a StopCommand which signals either
     * the nearest Scope or the specified scope to stop rendering
     * its content.
     * 
     * @param context
     * @param writer
     * @param node
     * @return never, always throws a StopCommand
     */
    public boolean render(InternalContextAdapter context, Writer writer, Node node)
    {
        if (!scoped)
        {
            throw new StopCommand();
        }

        Object argument = node.jjtGetChild(0).value(context);
        if (argument instanceof Scope)
        {
            ((Scope)argument).stop();
        }
        else
        {
            throw new VelocityException(node.jjtGetChild(0).literal()+
                " is not a valid " + Scope.class.getName() + " instance at "
                + LogUtil.formatFileString(this));
        }
        return false;
    }

}
