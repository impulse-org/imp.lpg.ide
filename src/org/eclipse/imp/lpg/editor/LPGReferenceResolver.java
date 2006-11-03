package org.eclipse.safari.jikespg.editor;

import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.editor.IReferenceResolver;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.safari.jikespg.parser.ASTUtils;
import org.eclipse.safari.jikespg.parser.JikesPGParser.ASTNode;
import org.eclipse.safari.jikespg.parser.JikesPGParser.IASTNodeToken;
import org.eclipse.safari.jikespg.parser.JikesPGParser.JikesPG;

public class JikesPGReferenceResolver implements IReferenceResolver, ILanguageService {
    public JikesPGReferenceResolver() {
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
	final ASTNode def= ASTUtils.findDefOf((IASTNodeToken) node, (JikesPG) ast);
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
