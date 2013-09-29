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
import java.io.IOException;

import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;


/**
 * Base class for all directives used in Velocity.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author Nathan Bubna
 * @version $Id: Directive.java 778045 2009-05-23 22:17:46Z nbubna $
 */
public abstract class Directive implements DirectiveConstants, Cloneable
{
    private int line = 0;
    private int column = 0;
    private boolean provideScope = false;
    private String templateName;

    /**
     *
     */
    protected RuntimeServices rsvc = null;

    /**
     * Return the name of this directive.
     * @return The name of this directive.
     */
    public abstract String getName();

    /**
     * Get the directive type BLOCK/LINE.
     * @return The directive type BLOCK/LINE.
     */
    public abstract int getType();

    /**
     * Allows the template location to be set.
     * @param line
     * @param column
     */
    public void setLocation( int line, int column )
    {
        this.line = line;
        this.column = column;
    }

    /**
     * Allows the template location to be set.
     * @param line
     * @param column
     */
    public void setLocation(int line, int column, String templateName)
    {
        setLocation(line, column);
        this.templateName = templateName;
    }

    /**
     * for log msg purposes
     * @return The current line for log msg purposes.
     */
    public int getLine()
    {
        return line;
    }

    /**
     * for log msg purposes
     * @return The current column for log msg purposes.
     */
    public int getColumn()
    {
        return column;
    }
    
    /**
     * @return The template file name this directive was defined in, or null if not 
     * defined in a file.
     */
    public String getTemplateName()
    {
      return templateName;
    }

    /**
     * @returns the name to be used when a scope control is provided for this
     * directive.
     */
    public String getScopeName()
    {
        return getName();
    }

    /**
     * @return true if there will be a scope control injected into the context
     * when rendering this directive.
     */
    public boolean isScopeProvided()
    {
        return provideScope;
    }

    /**
     * How this directive is to be initialized.
     * @param rs
     * @param context
     * @param node
     * @throws TemplateInitException
     */
    public void init( RuntimeServices rs, InternalContextAdapter context,
                      Node node)
        throws TemplateInitException
    {
        rsvc = rs;

        String property = getScopeName()+'.'+RuntimeConstants.PROVIDE_SCOPE_CONTROL;
        this.provideScope = rsvc.getBoolean(property, provideScope);
    }

    /**
     * How this directive is to be rendered
     * @param context
     * @param writer
     * @param node
     * @return True if the directive rendered successfully.
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws ParseErrorException
     * @throws MethodInvocationException
     */
    public abstract boolean render( InternalContextAdapter context,
                                    Writer writer, Node node )
           throws IOException, ResourceNotFoundException, ParseErrorException,
                MethodInvocationException;


    /**
     * This creates and places the scope control for this directive
     * into the context (if scope provision is turned on).
     */
    protected void preRender(InternalContextAdapter context)
    {
        if (isScopeProvided())
        {
            String name = getScopeName();
            Object previous = context.get(name);
            context.put(name, makeScope(previous));
        }
    }

    protected Scope makeScope(Object prev)
    {
        return new Scope(this, prev);
    }

    /**
     * This cleans up any scope control for this directive after rendering,
     * assuming the scope control was turned on.
     */
    protected void postRender(InternalContextAdapter context)
    {
        if (isScopeProvided())
        {
            String name = getScopeName();
            Object obj = context.get(name);
            
            try
            {
                Scope scope = (Scope)obj;
                if (scope.getParent() != null)
                {
                    context.put(name, scope.getParent());
                }
                else if (scope.getReplaced() != null)
                {
                    context.put(name, scope.getReplaced());
                }
                else
                {
                    context.remove(name);
                }
            }
            catch (ClassCastException cce)
            {
                // the user can override the scope with a #set,
                // since that means they don't care about a replaced value
                // and obviously aren't too keen on their scope control,
                // and especially since #set is meant to be handled globally,
                // we'll assume they know what they're doing and not worry
                // about replacing anything superseded by this directive's scope
            }
        }
    }

}
