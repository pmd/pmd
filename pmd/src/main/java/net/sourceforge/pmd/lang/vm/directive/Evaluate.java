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

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import org.apache.velocity.context.EvaluateContext;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.ParserTreeConstants;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.apache.velocity.util.introspection.Info;

/**
 * Evaluates the directive argument as a VTL string, using the existing
 * context.
 *
 * @author <a href="mailto:wglass@apache.org">Will Glass-Husain</a>
 * @version $Id: Evaluate.java 898032 2010-01-11 19:51:03Z nbubna $
 * @since 1.6
 */
public class Evaluate extends Directive
{

    /**
     * Return name of this directive.
     * @return The name of this directive.
     */
    public String getName()
    {
        return "evaluate";
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
     * Initialize and check arguments.
     * @param rs
     * @param context
     * @param node
     * @throws TemplateInitException
     */
    public void init(RuntimeServices rs, InternalContextAdapter context,
                     Node node)
        throws TemplateInitException
    {
        super.init( rs, context, node );

        /**
         * Check that there is exactly one argument and it is a string or reference.
         */  
        
        int argCount = node.jjtGetNumChildren();
        if (argCount == 0)
        {
            throw new TemplateInitException(
                    "#" + getName() + "() requires exactly one argument", 
                    context.getCurrentTemplateName(),
                    node.getColumn(),
                    node.getLine());            
        }
        if (argCount > 1)
        {
            /* 
             * use line/col of second argument
             */
            
            throw new TemplateInitException(
                    "#" + getName() + "() requires exactly one argument", 
                    context.getCurrentTemplateName(),
                    node.jjtGetChild(1).getColumn(),
                    node.jjtGetChild(1).getLine());
        }
        
        Node childNode = node.jjtGetChild(0);
        if ( childNode.getType() !=  ParserTreeConstants.JJTSTRINGLITERAL &&
             childNode.getType() !=  ParserTreeConstants.JJTREFERENCE )
        {
           throw new TemplateInitException(
                   "#" + getName() + "()  argument must be a string literal or reference", 
                   context.getCurrentTemplateName(),
                   childNode.getColumn(),
                   childNode.getLine());
        }
    }
    
    /**
     * Evaluate the argument, convert to a String, and evaluate again 
     * (with the same context).
     * @param context
     * @param writer
     * @param node
     * @return True if the directive rendered successfully.
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws ParseErrorException 
     * @throws MethodInvocationException
     */
    public boolean render(InternalContextAdapter context, Writer writer,
            Node node) throws IOException, ResourceNotFoundException,
            ParseErrorException, MethodInvocationException
    {

        /*
         * Evaluate the string with the current context.  We know there is
         * exactly one argument and it is a string or reference.
         */
        
        Object value = node.jjtGetChild(0).value( context );
        String sourceText;
        if ( value != null )
        {
            sourceText = value.toString();
        } 
        else
        {
            sourceText = "";
        }
        
        /*
         * The new string needs to be parsed since the text has been dynamically generated.
         */
        String templateName = context.getCurrentTemplateName();
        SimpleNode nodeTree = null;

        try
        {
            nodeTree = rsvc.parse(new StringReader(sourceText), templateName, false);
        }
        catch (ParseException pex)
        {
            // use the line/column from the template
            Info info = new Info( templateName, node.getLine(), node.getColumn() );
            throw  new ParseErrorException( pex.getMessage(), info );
        }
        catch (TemplateInitException pex)
        {
            Info info = new Info( templateName, node.getLine(), node.getColumn() );
            throw  new ParseErrorException( pex.getMessage(), info );
        }

        /*
         * now we want to init and render.  Chain the context
         * to prevent any changes to the current context.
         */

        if (nodeTree != null)
        {
            InternalContextAdapter ica = new EvaluateContext(context, rsvc);

            ica.pushCurrentTemplateName( templateName );

            try
            {
                try
                {
                    nodeTree.init( ica, rsvc );
                }
                catch (TemplateInitException pex)
                {
                    Info info = new Info( templateName, node.getLine(), node.getColumn() );
                    throw  new ParseErrorException( pex.getMessage(), info );
                }

                try 
                {
                    preRender(ica);

                    /*
                     *  now render, and let any exceptions fly
                     */
                    nodeTree.render( ica, writer );
                }
                catch (StopCommand stop)
                {
                    if (!stop.isFor(this))
                    {
                        throw stop;
                    }
                    else if (rsvc.getLog().isDebugEnabled())
                    {
                        rsvc.getLog().debug(stop.getMessage());
                    }
                }
                catch (ParseErrorException pex)
                {
                    // convert any parsing errors to the correct line/col
                    Info info = new Info( templateName, node.getLine(), node.getColumn() );
                    throw  new ParseErrorException( pex.getMessage(), info );
                }
            }
            finally
            {
                ica.popCurrentTemplateName();
                postRender(ica);
            }
            return true;
        }

        return false;
    }

}
