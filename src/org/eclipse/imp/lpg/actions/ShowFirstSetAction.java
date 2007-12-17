/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.IsymWithAttrs;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.lpg.parser.LPGParser.SymbolTable;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs1;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrsList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;

public class ShowFirstSetAction extends TextEditorAction {
    private final IFile fGrammarFile;
    private final ASTNode fNode;
    private final JikesPG fRoot;
    private nonTerm fNonTerm;
    private Set<IASTNodeToken> fFirstSet;

    public ShowFirstSetAction(UniversalEditor editor) {
	super(LanguageActionContributor.ResBundle, "showFirstSet.", editor);

	IEditorInput input= editor.getEditorInput();

	if (input instanceof IFileEditorInput) {
	    IFileEditorInput fileInput= (IFileEditorInput) input;

	    fGrammarFile= fileInput.getFile();
	    fRoot= getAST(editor);
	    fNode= findNode(editor);
	} else {
	    fGrammarFile= null;
	    fRoot= null;
	    fNode= null;
	}
    }

    public void run() {
	UniversalEditor editor= (UniversalEditor) this.getTextEditor();
	IParseController parseController= editor.getParseController();
	Object nt;

	if (fNode instanceof symWithAttrs1) {
	    symWithAttrs1 sym= (symWithAttrs1) fNode;
	    nt= ASTUtils.findDefOf(sym, fRoot, parseController);
	} else if (fNode instanceof IASTNodeToken) {
	    IASTNodeToken tok= (IASTNodeToken) fNode;
	    nt= ASTUtils.findDefOf(tok, fRoot, parseController);
	} else
	    nt= fNode;

	if (!(nt instanceof nonTerm)) {
	    MessageDialog.openError(editor.getSite().getShell(), "Error", "Can only show first-set of a non-terminal.");
	    return;
	}
	fNonTerm= (nonTerm) nt;

	collectFirstSet();
	StringBuffer buff= new StringBuffer();
	for(Iterator<IASTNodeToken> iter= fFirstSet.iterator(); iter.hasNext(); ) {
	    IASTNodeToken tok= iter.next();
	    buff.append(tok.toString()).append('\n');
	}
	MessageDialog.openInformation(editor.getSite().getShell(), "First set of " + fNonTerm.getruleNameWithAttributes().getSYMBOL(),
		buff.toString());
    }

    private void collectFirstSet() {
	// Collect the terminals that can appear at the beginning of a sentence matching fNonTerm
	final SymbolTable symbolTable= fRoot.symbolTable;
	Stack<nonTerm> workList= new Stack<nonTerm>();
	Set<nonTerm> processed= new HashSet<nonTerm>();
	fFirstSet= new HashSet<IASTNodeToken>();
	Set<nonTerm> epsilonSet= new HashSet<nonTerm>();

	workList.push(fNonTerm);
	while (!workList.empty()) {
	    nonTerm nt= workList.pop();
	    processed.add(nt);
	    ruleList rules= nt.getruleList();
	    for(int i=0; i < rules.size(); i++) {
		rule r= rules.getruleAt(i);
		symWithAttrsList syms= r.getsymWithAttrsList();
		IsymWithAttrs firstSym= null;
		ASTNode node;
		int symIdx=0; 

		// The following really needs to be replaced by something that computes the
		// set of non-terminals that *transitively* produce epsilon, *before* doing
		// any of this processing.
		do {
		    firstSym= syms.getsymWithAttrsAt(symIdx++);
		    node= symbolTable.lookup(firstSym.toString());
		    if (firstSym.toString().equals("$empty"))
			epsilonSet.add(nt);
		} while (symIdx < syms.size() && firstSym.toString().equals("$empty") || epsilonSet.contains(node));

		if (node == null) {
		    if (!firstSym.toString().equals("$empty"))
			fFirstSet.add(firstSym);
		} else if (node instanceof terminal) {
		    if (!fFirstSet.contains(node)) {
			final terminal thisTerm= (terminal) node;
			fFirstSet.add(thisTerm.getterminal_symbol());
		    }
		} else if (node instanceof nonTerm) {
		    if (!processed.contains(node)) {
			workList.add((nonTerm) node);
		    }
		}
	    }
	}
	dumpTerms(fFirstSet, "First Set:");
    }

    private void dumpTerms(Set<IASTNodeToken> tokSet, String headerMsg) {
	System.out.println(headerMsg);
	for(IASTNodeToken tok: tokSet) {
	    System.out.println(tok);
	}
    }

    private JikesPG getAST(UniversalEditor editor) {
	IParseController parseController= editor.getParseController();

	return (JikesPG) parseController.getCurrentAst();
    }

    private ASTNode findNode(UniversalEditor editor) {
	Point sel= editor.getSelection();
	IParseController parseController= editor.getParseController();
	ISourcePositionLocator locator= parseController.getNodeLocator();

	return (ASTNode) locator.findNode(fRoot, sel.x);
    }
}
