package org.eclipse.imp.lpg.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.imp.editor.internal.OutlineInformationControl;
import org.eclipse.imp.editor.internal.OutlineInformationControl.OutlineContentProviderBase;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG_itemList;
import org.eclipse.imp.lpg.parser.LPGParser.action_segment;
import org.eclipse.imp.lpg.parser.LPGParser.action_segmentList;
import org.eclipse.imp.lpg.parser.LPGParser.include_segment;
import org.eclipse.imp.lpg.parser.LPGParser.optTerminalAlias;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_value0;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol0;

public class LPGContentProvider extends OutlineContentProviderBase implements ILanguageService {
    public LPGContentProvider() {
	this(false, null);
    }

    /**
     * Creates a new <code>JikesPGContentProvider</code>.
     *
     * @param provideMembers if <code>true</code> members below compilation units 
     * and class files are provided. 
     */
    public LPGContentProvider(boolean provideMembers, OutlineInformationControl oic) {
	super(oic, true);
    }

    /* (non-Javadoc)
     * Method declared on ITreeContentProvider.
     */
    public Object[] getChildren(Object element) {
	ASTNode node= (ASTNode) element;
	final List children= node.getChildren();
	final List collapsingAdditions= new ArrayList();

	if (node instanceof option_spec)
	    return null;
	for(Iterator iter= children.iterator(); iter.hasNext(); ) {
	    ASTNode child= (ASTNode) iter.next();

	    // Filter out certain "useless" AST node types from the child list.
	    // This prunes out entities and all their sub-children from the outline.
	    if ((child instanceof ASTNodeToken && !(child instanceof terminal_symbol0) && !(child instanceof action_segment) && !(child instanceof include_segment)) ||
		child instanceof action_segmentList ||
		child instanceof option_value0 || child instanceof optTerminalAlias || child instanceof option_spec ||
		child instanceof ruleList || child instanceof JikesPG_itemList || child instanceof optionList)
		iter.remove();
	    if (child instanceof JikesPG_itemList || child instanceof option_spec || child instanceof action_segmentList ||
	        child instanceof optionList) { // collapse this entity by replacing it with its children
		collapsingAdditions.addAll(child.getChildren());
	    }
	}
	children.addAll(collapsingAdditions);
	return children.toArray();
    }

    /* (non-Javadoc)
     * Method declared on ITreeContentProvider.
     */
    public Object getParent(Object element) {
	ASTNode node= (ASTNode) element;

	return node.getParent();
    }
}
