/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package org.eclipse.imp.lpg.parser;

import lpg.runtime.*;

import org.eclipse.imp.lpg.parser.LPGParser.*;
import org.eclipse.imp.parser.IParser;

public abstract class JavaActionBlockVisitor extends AbstractVisitor
{
    private JavaLexer javaLexer = new JavaLexer(); // lexer can be shared.
    private JavaParser javaParser = new JavaParser();

    public void reset(IParser env)
    {
    	PrsStream prs_stream = (PrsStream) env.getIPrsStream();
    	javaLexer.reset(prs_stream.getInputChars(), prs_stream.getFileName());
    	javaLexer.getILexStream().setMessageHandler(prs_stream.getMessageHandler());
   	}
         
    protected void parseClassBodyDeclarationsopt(action_segmentList list) {
        for (int i = 0; i < list.size(); i++)
            parseClassBodyDeclarationsopt((action_segment) list.getaction_segmentAt(i));
    }
        
    protected void parseClassBodyDeclarationsopt(action_segment n) {
        int start_offset = n.getBLOCK().getStartOffset() + 2,
            end_offset   = n.getBLOCK().getEndOffset() - 2;
        javaParser.reset(javaLexer.getILexStream()); // allocate a new parse stream.
        javaLexer.lexer(javaParser.getIPrsStream(), start_offset, end_offset);
        n.setAst(javaParser.parseClassBodyDeclarationsopt());
    }

    protected void parse(action_segmentList list) {
        for (int i = 0; i < list.size(); i++)
            parse((action_segment) list.getaction_segmentAt(i));
    }
   
    protected void parseLPGUserAction(action_segment n) {
        int start_offset = n.getBLOCK().getStartOffset() + 2,
            end_offset   = n.getBLOCK().getEndOffset() - 2;
        javaParser.reset(javaLexer.getILexStream()); // allocate a new parse stream.
        javaLexer.lexer(javaParser.getIPrsStream(), start_offset, end_offset);
        n.setAst(javaParser.parseLPGUserAction());
    }

    protected void parse(action_segment n) {
        int start_offset = n.getBLOCK().getStartOffset() + 2,
            end_offset   = n.getBLOCK().getEndOffset() - 2;
        javaParser.reset(javaLexer.getILexStream()); // allocate a new parse stream.
        javaLexer.lexer(javaParser.getIPrsStream(), start_offset, end_offset);
        n.setAst(javaParser.parser());
    }

    //
    // The visitors
    //
    public void unimplementedVisitor(String s) {
        // Sometimes useful for debugging
        //System.out.println(s);
    }

    public boolean visit(AstSeg n) {
        parseClassBodyDeclarationsopt(n.getast_segment());
        return false;
    }

    public boolean visit(GlobalsSeg n) {
        parse(n.getglobals_segment());
        return false;
    }

    public boolean visit(HeadersSeg n) {
        parseClassBodyDeclarationsopt(n.getheaders_segment());
        return false;
    }

    public boolean visit(NoticeSeg n) {
        parse(n.getnotice_segment());
        return false;
    }

    public boolean visit(TrailersSeg n) {
         parseClassBodyDeclarationsopt(n.gettrailers_segment());
         return false;
    }

    public abstract boolean visit(rule n);
}
