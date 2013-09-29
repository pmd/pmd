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
import java.io.Writer;

import net.sourceforge.pmd.lang.vm.util.LogUtil;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.context.ProxyVMContext;
import org.apache.velocity.exception.MacroOverflowException;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.Renderable;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

/**
 *  VelocimacroProxy.java
 *
 *   a proxy Directive-derived object to fit with the current directive system
 *
 * @author <a href="mailto:geirm@optonline.net">Geir Magnusson Jr.</a>
 * @version $Id: VelocimacroProxy.java 898032 2010-01-11 19:51:03Z nbubna $
 */
public class VelocimacroProxy extends Directive
{
    private String macroName;
    private String[] argArray = null;
    private String[] literalArgArray = null;
    private SimpleNode nodeTree = null;
    private int numMacroArgs = 0;
    private boolean strictArguments;
    private boolean localContextScope = false;
    private int maxCallDepth;
    private String bodyReference;

    /**
     * Return name of this Velocimacro.
     * @return The name of this Velocimacro.
     */
    public String getName()
    {
        return  macroName;
    }

    /**
     * Velocimacros are always LINE type directives.
     * @return The type of this directive.
     */
    public int getType()
    {
        return LINE;
    }

    /**
     * sets the directive name of this VM
     * 
     * @param name
     */
    public void setName(String name)
    {
        macroName = name;
    }

    /**
     * sets the array of arguments specified in the macro definition
     * 
     * @param arr
     */
    public void setArgArray(String[] arr)
    {
        argArray = arr;
        
        // for performance reasons we precache these strings - they are needed in
        // "render literal if null" functionality
        literalArgArray = new String[arr.length];
        for(int i = 0; i < arr.length; i++)
        {
            literalArgArray[i] = ".literal.$" + argArray[i];
        }

        /*
         * get the arg count from the arg array. remember that the arg array has the macro name as
         * it's 0th element
         */

        numMacroArgs = argArray.length - 1;
    }

    /**
     * @param tree
     */
    public void setNodeTree(SimpleNode tree)
    {
        nodeTree = tree;
    }

    /**
     * returns the number of ars needed for this VM
     * 
     * @return The number of ars needed for this VM
     */
    public int getNumArgs()
    {
        return numMacroArgs;
    }

    public boolean render(InternalContextAdapter context, Writer writer, Node node)
            throws IOException, MethodInvocationException, MacroOverflowException
    {
        return render(context, writer, node, null);
    }
    
    /**
     * Renders the macro using the context.
     * 
     * @param context Current rendering context
     * @param writer Writer for output
     * @param node AST that calls the macro
     * @return True if the directive rendered successfully.
     * @throws IOException
     * @throws MethodInvocationException
     * @throws MacroOverflowException
     */
    public boolean render(InternalContextAdapter context, Writer writer,
                          Node node, Renderable body)
            throws IOException, MethodInvocationException, MacroOverflowException
    {
        // wrap the current context and add the macro arguments

        // the creation of this context is a major bottleneck (incl 2x HashMap)
        final ProxyVMContext vmc = new ProxyVMContext(context, rsvc, localContextScope);

        int callArguments = node.jjtGetNumChildren();

        if (callArguments > 0)
        {
            // the 0th element is the macro name
            for (int i = 1; i < argArray.length && i <= callArguments; i++)
            {
                /*
                 * literalArgArray[i] is needed for "render literal if null" functionality.
                 * The value is used in ASTReference render-method.
                 * 
                 * The idea is to avoid generating the literal until absolutely necessary.
                 * 
                 * This makes VMReferenceMungeVisitor obsolete and it would not work anyway 
                 * when the macro AST is shared
                 */
                vmc.addVMProxyArg(context, argArray[i], literalArgArray[i], node.jjtGetChild(i - 1));
            }
        }
        
        // if this macro was invoked by a call directive, we might have a body AST here. Put it into context.
        if( body != null )
        {
            vmc.addVMProxyArg(context, bodyReference, "", body);
        }

        /*
         * check that we aren't already at the max call depth
         */
        if (maxCallDepth > 0 && maxCallDepth == vmc.getCurrentMacroCallDepth())
        {
            Object[] stack = vmc.getMacroNameStack();

            StringBuffer out = new StringBuffer(100)
                .append("Max calling depth of ").append(maxCallDepth)
                .append(" was exceeded in macro '").append(macroName)
                .append("' with Call Stack:");
            for (int i = 0; i < stack.length; i++)
            {
                if (i != 0)
                {
                    out.append("->");
                }
                out.append(stack[i]);
            }
            out.append(" at " + LogUtil.formatFileString(this));
            rsvc.getLog().error(out.toString());
            
            // clean out the macro stack, since we just broke it
            while (vmc.getCurrentMacroCallDepth() > 0)
            {
                vmc.popCurrentMacroName();
            }

            throw new MacroOverflowException(out.toString());
        }

        try
        {
            // render the velocity macro
            vmc.pushCurrentMacroName(macroName);
            nodeTree.render(vmc, writer);
            vmc.popCurrentMacroName();
            return true;
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            String msg = "VelocimacroProxy.render() : exception VM = #" + macroName + "()";
            rsvc.getLog().error(msg, e);
            throw new VelocityException(msg, e);
        }
    }

    /**
     * Initialize members of VelocimacroProxy.  called from MacroEntry
     */
    public void init(RuntimeServices rs)
    {
        rsvc = rs;
      
        // this is a very expensive call (ExtendedProperties is very slow)
        strictArguments = rs.getConfiguration().getBoolean(
            RuntimeConstants.VM_ARGUMENTS_STRICT, false);

        // support for local context scope feature, where all references are local
        // we do not have to check this at every invocation of ProxyVMContext
        localContextScope = rsvc.getBoolean(RuntimeConstants.VM_CONTEXT_LOCALSCOPE, false);
        if (localContextScope && rsvc.getLog().isWarnEnabled())
        {
            // only warn once per runtime, so this isn't obnoxious
            String key = "velocimacro.context.localscope.warning";
            Boolean alreadyWarned = (Boolean)rsvc.getApplicationAttribute(key);
            if (alreadyWarned == null)
            {
                rsvc.setApplicationAttribute(key, Boolean.TRUE);
                rsvc.getLog()
                .warn("The "+RuntimeConstants.VM_CONTEXT_LOCALSCOPE+
                      " feature is deprecated and will be removed in Velocity 2.0."+
                      " Instead, please use the $macro scope to store references"+
                      " that must be local to your macros (e.g. "+
                      "#set( $macro.foo = 'bar' ) and $macro.foo).  This $macro"+
                      " namespace is automatically created and destroyed for you at"+
                      " the beginning and end of the macro rendering.");
            }
        }

        // get the macro call depth limit
        maxCallDepth = rsvc.getInt(RuntimeConstants.VM_MAX_DEPTH);

        // get name of the reference that refers to AST block passed to block macro call
        bodyReference = rsvc.getString(RuntimeConstants.VM_BODY_REFERENCE, "bodyContent");
    }
    

    /**
     * Build an error message for not providing the correct number of arguments
     */
    private String buildErrorMsg(Node node, int numArgsProvided)
    {
        String msg = "VM #" + macroName + ": too "
          + ((getNumArgs() > numArgsProvided) ? "few" : "many") + " arguments to macro. Wanted "
          + getNumArgs() + " got " + numArgsProvided;      
        return msg;
    }
    
    /**
     * check if we are calling this macro with the right number of arguments.  If 
     * we are not, and strictArguments is active, then throw TemplateInitException.
     * This method is called during macro render, so it must be thread safe.
     */
    public void checkArgs(InternalContextAdapter context, Node node, boolean hasBody)
    {
        // check how many arguments we have
        int i = node.jjtGetNumChildren();
        
        // if macro call has a body (BlockMacro) then don't count the body as an argument
        if( hasBody )
            i--;

        // Throw exception for invalid number of arguments?
        if (getNumArgs() != i)
        {
            if (strictArguments)
            {
                /**
                 * indicate col/line assuming it starts at 0 - this will be corrected one call up
                 */
                throw new TemplateInitException(buildErrorMsg(node, i), 
                    context.getCurrentTemplateName(), 0, 0);
            }
            else if (rsvc.getLog().isDebugEnabled())
            {
                rsvc.getLog().debug(buildErrorMsg(node, i));
                return;
            }
        }
    }
}

