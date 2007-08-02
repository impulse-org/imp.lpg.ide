package org.eclipse.imp.lpg.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import org.eclipse.imp.parser.IASTNodeLocator;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;

public class ShowFollowSetAction extends TextEditorAction {
    private final IFile fGrammarFile;
    private final ASTNode fNode;
    private final JikesPG fRoot;
    private nonTerm fNonTerm;
    private Set<IASTNodeToken> fFollowSet;
    private Set<nonTerm> fEpsilonSet;

    public ShowFollowSetAction(UniversalEditor editor) {
	super(LanguageActionContributor.ResBundle, "showFollowSet.", editor);

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
	    MessageDialog.openError(editor.getSite().getShell(), "Error", "Can only show follow-set of a non-terminal.");
	    return;
	}
	fNonTerm= (nonTerm) nt;

	collectFollowSet();
	StringBuffer buff= new StringBuffer();
	buff.append("BOGUS BOGUS BOGUS!\n");
	for(Iterator<IASTNodeToken> iter= fFollowSet.iterator(); iter.hasNext(); ) {
	    IASTNodeToken tok= iter.next();
	    buff.append(tok.toString()).append('\n');
	}
	// TODO Better of course to show this in a non-modal view...
	MessageDialog.openInformation(editor.getSite().getShell(),
		"Follow set of " + fNonTerm.getruleNameWithAttributes().getSYMBOL(),
		buff.toString());
    }

    private void collectFollowSet() {
	// Collect the terminals that can appear just after the given non-terminal fNonTerm
	final SymbolTable symbolTable= fRoot.symbolTable;
	Stack<nonTerm> workList= new Stack<nonTerm>();
	Set<nonTerm> processed= new HashSet<nonTerm>();
	fFollowSet= new HashSet<IASTNodeToken>();
	Set<nonTerm> epsilonSet= new HashSet<nonTerm>();

	collectEpsilonSet();
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

		do {
		    firstSym= syms.getsymWithAttrsAt(symIdx++);
		    node= symbolTable.lookup(firstSym.toString());
		    if (firstSym.toString().equals("$empty"))
			epsilonSet.add(nt);
		} while (symIdx < syms.size() && firstSym.toString().equals("$empty") || epsilonSet.contains(node));

		if (node == null) {
		    if (!firstSym.toString().equals("$empty"))
			fFollowSet.add(firstSym);
		} else if (node instanceof terminal) {
		    if (!fFollowSet.contains(node)) {
			final terminal thisTerm= (terminal) node;
			fFollowSet.add(thisTerm.getterminal_symbol());
		    }
		} else if (node instanceof nonTerm) {
		    if (!processed.contains(node)) {
			workList.add((nonTerm) node);
		    }
		}
	    }
	}
    }

    private Set<nonTerm> collectEpsilonSet() {
	fEpsilonSet= new HashSet<nonTerm>();
	final SymbolTable symbolTable= fRoot.symbolTable;
	Stack<nonTerm> workList= new Stack<nonTerm>();
	List<nonTerm> allNonTerms= symbolTable.allDefsOfType(nonTerm.class);

	// Seed the epsilon set with all non-terminals that directly derive epsilon
	for(nonTerm nt : allNonTerms) {
	    ruleList rules= nt.getruleList();
	    for(int i=0; i < rules.size(); i++) {
		rule r= rules.getruleAt(i);
		symWithAttrsList syms= r.getsymWithAttrsList();
		if (syms.size() == 1) {
		    IsymWithAttrs onlySym= syms.getsymWithAttrsAt(0);
		    if (onlySym.toString().equals("$empty")) {
			fEpsilonSet.add(nt);
			break;
		    }
		}
	    }
	}
	for(nonTerm nt : fEpsilonSet) {
	    workList.add(nt);
	}
	while (!workList.empty()) {
	    nonTerm nt= workList.pop();

	    // Find references to 'nt'
	    // The following is BOGUS!!!
	    ruleList rules= nt.getruleList();
nextRule:
	    for(int i=0; i < rules.size(); i++) {
		rule r= rules.getruleAt(i);
		symWithAttrsList syms= r.getsymWithAttrsList();

		for(int symIdx=0; symIdx < syms.size(); symIdx++) {
		    IsymWithAttrs sym= syms.getsymWithAttrsAt(symIdx);
		    ASTNode node= symbolTable.lookup(sym.toString());
		    if (!sym.toString().equals("$empty") && !fEpsilonSet.contains(node)) {
			// This rule doesn't appear to derive epsilon
			continue nextRule;
		    }
		}
		// This rule derives epsilon
		fEpsilonSet.add(nt);
		workList.push(nt);
	    }
	}
	return fEpsilonSet;
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
	IASTNodeLocator locator= parseController.getNodeLocator();

	return (ASTNode) locator.findNode(fRoot, sel.x);
    }
}
