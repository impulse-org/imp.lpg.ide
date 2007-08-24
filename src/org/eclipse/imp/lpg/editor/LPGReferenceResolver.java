/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.editor;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IReferenceResolver;

public class LPGReferenceResolver implements IReferenceResolver, ILanguageService {
    public LPGReferenceResolver() {
	super();
    }

    /**
     * Get the target for a given source node in the AST represented by a
     * given Parse Controller.
     */
    public Object getLinkTarget(Object node, IParseController parseController) {
	if (!(node instanceof IASTNodeToken))
	    return null;
	JikesPG ast= (JikesPG) parseController.getCurrentAst();
	Object def= ASTUtils.findDefOf((IASTNodeToken) node, (JikesPG) ast, parseController);
	return def;
    }

    /**
     * Get the text associated with a given node for use in a link
     * from (or to) that node
     */
    public String getLinkText(Object node) {
	if (node instanceof ASTNode) {
	    return ((ASTNode) node).getLeftIToken().toString();
	} else {
	    System.err.println("JikesPGReferenceResolver.getLinkText(): odd; given object is not an ASTNode");
	    return node.getClass().toString();
	}
    }
}
