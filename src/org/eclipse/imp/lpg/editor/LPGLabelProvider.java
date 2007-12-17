/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Jul 7, 2006
 */
package org.eclipse.imp.lpg.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.imp.editor.ModelTreeNode;
import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.ILPGResources;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.parser.LPGParser.*;
import org.eclipse.imp.utils.MarkerUtils;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class LPGLabelProvider implements ILabelProvider, ILanguageService {
    private Set<ILabelProviderListener> fListeners= new HashSet<ILabelProviderListener>();

    private static ImageRegistry sImageRegistry= LPGRuntimePlugin.getInstance().getImageRegistry();

    private static Image DEFAULT_IMAGE= sImageRegistry.get(ILPGResources.DEFAULT_AST);

    private static Image GRAMMAR_FILE_IMAGE= sImageRegistry.get(ILPGResources.GRAMMAR_FILE);

    private static Image GRAMMAR_FILE_ERROR_IMAGE= sImageRegistry.get(ILPGResources.GRAMMAR_FILE_ERROR);

    private static Image GRAMMAR_FILE_WARNING_IMAGE= sImageRegistry.get(ILPGResources.GRAMMAR_FILE_WARNING);

    public Image getImage(Object element) {
	if (element instanceof IFile) {
	    IFile file= (IFile) element;
	    int sev= MarkerUtils.getMaxProblemMarkerSeverity(file, IResource.DEPTH_ONE);

	    switch(sev) {
	    case IMarker.SEVERITY_ERROR: return GRAMMAR_FILE_ERROR_IMAGE;
	    case IMarker.SEVERITY_WARNING: return GRAMMAR_FILE_WARNING_IMAGE;
	    default:
		return GRAMMAR_FILE_IMAGE;
	    }
	}
	ASTNode n= (element instanceof ModelTreeNode) ?
	        (ASTNode) ((ModelTreeNode) element).getASTNode() :
                (ASTNode) element;

	return getImageFor(n);
    }

    public static Image getImageFor(ASTNode n) {
	return DEFAULT_IMAGE;
    }

    public String getText(Object element) {
	ASTNode n= (element instanceof ModelTreeNode) ?
                (ASTNode) ((ModelTreeNode) element).getASTNode() :
                (ASTNode) element;

	return getLabelFor(n);
    }

    public static String getLabelFor(ASTNode n) {
	if (n instanceof JikesPG)
	    return "grammar";
	if (n instanceof option_specList)
	    return "options";
	if (n instanceof AliasSeg)
	    return "aliases";
	if (n instanceof DefineSeg)
	    return "defines";
        if (n instanceof ExportSeg)
            return "export";
	if (n instanceof GlobalsSeg)
	    return "globals";
	if (n instanceof HeadersSeg)
	    return "headers";
	if (n instanceof IdentifierSeg)
	    return "identifiers";
	if (n instanceof ImportSeg)
	    return "imports";
	if (n instanceof IncludeSeg)
	    return "includes";
	if (n instanceof JikesPG_itemList)
	    return "item list";
	if (n instanceof KeywordsSeg)
	    return "keywords";
	if (n instanceof NoticeSeg)
	    return "notice";
	if (n instanceof StartSeg)
	    return "start symbol";
	if (n instanceof RulesSeg)
	    return "rules";
	if (n instanceof TerminalsSeg)
	    return "terminals";
	if (n instanceof TypesSeg)
	    return "types";

	if (n instanceof option_spec)
	    return "option spec";
	if (n instanceof optionList)
	    return "%option " + ((optionList) n).getoptionAt(0).getSYMBOL() + "...";
	if (n instanceof nonTermList)
	    return "non-terminals";
	if (n instanceof option) {
            option o= (option) n;
	    return o.getSYMBOL().toString() + (o.getoption_value() != null ? o.getoption_value().toString() : "");
        }
	if (n instanceof defineSpecList)
	    return "defines";
	if (n instanceof defineSpec)
	    return /*"macro " +*/((defineSpec) n).getmacro_name_symbol().toString();
	if (n instanceof nonTerm)
	    return /*"non-terminal " +*/((nonTerm) n).getruleNameWithAttributes().getSYMBOL().toString();
	if (n instanceof terminal)
	    return /*"terminal " +*/((terminal) n).getterminal_symbol().toString();
	if (n instanceof include_segment)
	    return ((include_segment) n).getSYMBOL().toString();
	if (n instanceof action_segmentList)
	    return "actions";
	if (n instanceof action_segment)
	    return ((action_segment) n).getBLOCK().toString();
	if (n instanceof terminalList)
	    return "terminals";
	if (n instanceof start_symbol0)
	    return ((start_symbol0) n).getSYMBOL().toString();
	if (n instanceof drop_commandList)
	    return "drop";
	if (n instanceof drop_command0)
	    return "drop symbols";
	if (n instanceof drop_command1)
	    return "drop rules";
	if (n instanceof drop_rule)
	    return ((drop_rule) n).getSYMBOL().toString();
	if (n instanceof drop_ruleList)
	    return "rules";
	if (n instanceof rule) {
            rule r= (rule) n;
            nonTerm nt= (nonTerm) r.getParent().getParent();
            String nonTermName= nt.getruleNameWithAttributes().getSYMBOL().toString();
	    return nonTermName + " " + nt.getproduces() + " " + r.getsymWithAttrsList().toString();
        }
	if (n instanceof symWithAttrsList)
	    return ((symWithAttrsList) n).toString();
	if (n instanceof keywordSpecList)
	    return "keywords";
	if (n instanceof keywordSpec) {
	    keywordSpec kspec= (keywordSpec) n;
	    return kspec.getterminal_symbol().toString() + (kspec.getname() != null ? " ::= " + kspec.getname().toString() : "");
	}
	if (n instanceof rules_segment)
	    return "rules";
//	if (n instanceof types_segment1)
//	    return "types???";
	if (n instanceof SYMBOLList)
	    return n.toString();
	if (n instanceof type_declarationsList)
	    return "types";
	if (n instanceof type_declarations)
	    return ((type_declarations) n).getSYMBOL().toString();
	if (n instanceof import_segment)
	    return "import " + ((import_segment) n).getSYMBOL().toString();
	if (n instanceof ASTNodeToken)
	    return ((ASTNodeToken) n).toString();

	return "<???>";
    }

    public void addListener(ILabelProviderListener listener) {
	fListeners.add(listener);
    }

    public void dispose() {}

    public boolean isLabelProperty(Object element, String property) {
	return false;
    }

    public void removeListener(ILabelProviderListener listener) {
	fListeners.remove(listener);
    }
}
