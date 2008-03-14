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

import lpg.runtime.IAst;
import lpg.runtime.IToken;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.parser.ISourcePositionLocator;

public class NodeLocator implements ISourcePositionLocator {
    private IAst fResult= null;
    private final ParseController fParseController;

    public NodeLocator(ParseController controller) {
	fParseController= controller;
    }

    public Object findNode(Object ast, int offset) {
        ASTNode root= (ASTNode) ast;

        if (root == null)
            return null;

        root.accept(new LocatingVisitor(offset));
        return fResult;
    }

    public Object findNode(Object ast, int startOffset, int endOffset) {
        ASTNode root= (ASTNode) ast;

        root.accept(new LocatingVisitor(startOffset, endOffset));
        return fResult;
    }

    private class LocatingVisitor extends LPGParser.AbstractVisitor {
        private final int fStartOffset;
        private final int fEndOffset;

        public LocatingVisitor(int offset) {
            this(offset, offset);
        }

        public LocatingVisitor(int startOffset, int endOffset) {
            fStartOffset= startOffset;
            fEndOffset= endOffset;
        }

        public IAst getResult() { return fResult; }

        public void unimplementedVisitor(String s) {
//            System.out.println(s);
        }

        public void postVisit(IAst n) {
            // Use postVisit() so that we find the innermost AST node that contains the given text
            // range (innermost node's postVisit() will be the first postVisit() to be called).
            if (fResult == null) {
        	IToken symLeftTok= n.getLeftIToken();
        	IToken symRightTok= n.getRightIToken();

        	// Consider position just to right of end of token part of the token
        	if (fStartOffset >= symLeftTok.getStartOffset() && fEndOffset <= symRightTok.getEndOffset()+1)
        	    fResult= n;
            }
        }
    }
    
    public int getStartOffset(Object entity) {
	if (entity instanceof IAst) {
	    IAst n = (IAst) entity;
	    return n.getLeftIToken().getStartOffset();
	} else if (entity instanceof ICompilationUnit) {
	    ICompilationUnit icu= (ICompilationUnit) entity;
	    return 0;
	} else if (entity instanceof IToken) {
	    IToken t= (IToken) entity;
            return t.getStartOffset();
        }
	return 0;
    }

    public int getEndOffset(Object entity) {
	if (entity instanceof IAst) {
	    IAst n = (IAst) entity;
	    return n.getRightIToken().getEndOffset();
	} else if (entity instanceof ICompilationUnit) {
	    return 0;
        } else if (entity instanceof IToken) {
            IToken t= (IToken) entity;
            return t.getEndOffset();
	}
	return 0;
    }

    public int getLength(Object entity) {
        if (entity instanceof ICompilationUnit) {
            return 0;
        } else {
            return getEndOffset(entity) - getStartOffset(entity) + 1;
        }
    }

    public IPath getPath(Object node) {
	// TODO Once we have pseudo-nodes for external decls (of the sort the reference
	// resolver can return for cross-compilation-unit references), make this do
	// something reasonable with them.
	if (node instanceof ASTNode) {
	    ASTNode n = (ASTNode) node;
	    return new Path(fParseController.getProject().getRawProject().getName() + "/" + n.leftIToken.getPrsStream().getFileName());
	} else if (node instanceof ICompilationUnit) {
            // TODO RMF 5 June 2007 - Perhaps this logic belongs elsewhere, on a base class?
	    ICompilationUnit icu= (ICompilationUnit) node;
	    if (icu.getPath().isAbsolute()) {
                if (icu.getPath().getDevice() == null) {
                    final IWorkspaceRoot wsRoot= icu.getProject().getRawProject().getWorkspace().getRoot();
                    return wsRoot.getLocation().append(icu.getPath());
                }
                return icu.getPath();
            }
	    return icu.getProject().getRawProject().getFullPath().append(icu.getPath());
	}
	return null;
    }
}
