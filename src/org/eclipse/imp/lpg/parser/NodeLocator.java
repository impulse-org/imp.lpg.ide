package org.eclipse.safari.jikespg.parser;

import lpg.runtime.IToken;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.safari.jikespg.parser.JikesPGParser.ASTNode;
import org.eclipse.uide.model.ICompilationUnit;
import org.eclipse.uide.parser.IASTNodeLocator;

public class NodeLocator implements IASTNodeLocator {
    private ASTNode fResult= null;

    public NodeLocator(ParseController controller) {
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

    private class LocatingVisitor extends JikesPGParser.AbstractVisitor {
        private final int fStartOffset;
        private final int fEndOffset;

        public LocatingVisitor(int offset) {
            this(offset, offset);
        }

        public LocatingVisitor(int startOffset, int endOffset) {
            fStartOffset= startOffset;
            fEndOffset= endOffset;
        }

        public ASTNode getResult() { return fResult; }

        public void unimplementedVisitor(String s) {
//            System.out.println(s);
        }

        public void postVisit(ASTNode n) {
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
    
    public int getStartOffset(Object node) {
	if (node instanceof ASTNode) {
	    ASTNode n = (ASTNode) node;
	    return n.getLeftIToken().getStartOffset();
	} else if (node instanceof ICompilationUnit) {
	    ICompilationUnit icu= (ICompilationUnit) node;
	    return 0;
	}
	return 0;
    }
    
    
    public int getEndOffset(Object node) {
	if (node instanceof ASTNode) {
	    ASTNode n = (ASTNode) node;
	    return n.getRightIToken().getEndOffset();
	} else if (node instanceof ICompilationUnit) {
	    return 0;
	}
	return 0;
    }
    
    
    public int getLength(Object  node) {
    	ASTNode n = (ASTNode) node;
    	return getEndOffset(n) - getStartOffset(n);
    }

    /**
     * @return the workspace-relative path to the source file containing the given node
     */
    public IPath getPath(Object node) {
	// TODO Once we have pseudo-nodes for external decls (of the sort the reference
	// resolver can return for cross-compilation-unit references), make this do
	// something reasonable with them.
	if (node instanceof ASTNode) {
	    ASTNode n = (ASTNode) node;
	    return new Path(n.leftIToken.getPrsStream().getFileName());
	} else if (node instanceof ICompilationUnit) {
            // TODO RMF 5 June 2007 - Perhaps this logic belongs elsewhere, on a base class?
	    ICompilationUnit icu= (ICompilationUnit) node;
	    if (icu.getPath().isAbsolute()) {
                if (icu.getPath().getDevice() == null) {
                    final IWorkspaceRoot wsRoot= icu.getProject().getRawProject().getWorkspace().getRoot();
                    return wsRoot.getLocation().append(icu.getPath());
                }
                return icu.getPath();
            } return icu.getProject().getRawProject().getFullPath().append(icu.getPath());
	}
	return null;
    }
}
