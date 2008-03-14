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

package org.eclipse.imp.lpg.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.IsymWithAttrs;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.lpg.parser.LPGParser.SymbolTable;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs1;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrsList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.views.GrammarInteractionView;
import org.eclipse.imp.lpg.views.GrammarInteractionView.InputHandler;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;

public class ParseNonTerminalAction extends TextEditorAction implements InputHandler {
    private final IFile fGrammarFile;
    private final ASTNode fNode;
    private final JikesPG fRoot;
    private nonTerm fNonTerm;
    private Set<nonTerm> fReachable;

    public ParseNonTerminalAction(UniversalEditor editor) {
	super(LanguageActionContributor.ResBundle, "parseNonTerminal.", editor);

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

	dumpSymbolTable();

	if (fNode instanceof symWithAttrs1) {
	    symWithAttrs1 sym= (symWithAttrs1) fNode;
	    nt= ASTUtils.findDefOf((IASTNodeToken) sym, (JikesPG) fRoot, parseController);
	} else if (fNode instanceof IASTNodeToken) {
	    IASTNodeToken tok= (IASTNodeToken) fNode;
	    nt= ASTUtils.findDefOf(tok, (JikesPG) fRoot, parseController);
	} else
	    nt= fNode;

	if (!(nt instanceof nonTerm)) {
	    MessageDialog.openError(editor.getSite().getShell(), "Error", "Can only parse sentence for a non-terminal.");
	    return;
	}
	fNonTerm= (nonTerm) nt;

	collectGrammarSubset();
	generateParser();
	loadParser();

	GrammarInteractionView.getDefault().addInputListener(this);
    }

    private void dumpSymbolTable() {
	LPGParser.SymbolTable st= fRoot.symbolTable;
	List<String> allMacros= st.allSymbolsOfType(defineSpec.class);
	List<String> allTerminals= st.allSymbolsOfType(terminal.class);
	List<String> allNonTerminals= st.allSymbolsOfType(nonTerm.class);

	for(Iterator iter= allMacros.iterator(); iter.hasNext(); ) {
	    String sym= (String) iter.next();
	    System.out.println("Macro " + sym + " => " + st.lookup(sym));
	}
	for(Iterator iter= allTerminals.iterator(); iter.hasNext(); ) {
	    String sym= (String) iter.next();
	    System.out.println("Terminal " + sym + " => " + st.lookup(sym));
	}
	for(Iterator iter= allNonTerminals.iterator(); iter.hasNext(); ) {
	    String sym= (String) iter.next();
	    System.out.println("Non-terminal " + sym + " => " + st.lookup(sym));
	}
    }

    private void collectGrammarSubset() {
	// Collect the portion of the grammar reachable from fNonTerm.
	final SymbolTable symbolTable= fRoot.symbolTable;
	Stack<nonTerm> workList= new Stack<nonTerm>();

	fReachable= new HashSet<nonTerm>();
	workList.push(fNonTerm);
	while (!workList.empty()) {
	    nonTerm nt= workList.pop();
	    ruleList rules= nt.getruleList();
	    for(int i=0; i < rules.size(); i++) {
		rule r= rules.getruleAt(i);
		symWithAttrsList syms= r.getsymWithAttrsList();

		for(int j=0; j < syms.size(); j++) {
		    IsymWithAttrs sym= syms.getsymWithAttrsAt(j);
		    ASTNode node= symbolTable.lookup(sym.toString());

		    if (node instanceof nonTerm) {
			if (!fReachable.contains(node)) {
			    final nonTerm thisNT= (nonTerm) node;
			    fReachable.add(thisNT);
			    workList.add(thisNT);
			}
		    }
		}
	    }
	}
	dumpNonTerms(fReachable, "Reachable: ");
    }

    private void dumpNonTerms(Set<nonTerm> reachable, String headerMsg) {
	System.out.println(headerMsg);
	for(nonTerm nt: reachable) {
	    System.out.println(nt);
	}
    }

    private void generateParser() {
	// Given a subset of the grammar, create a grammar input file and run
	// that through the parser generator, and run the result through javac.
	throw new IllegalArgumentException("Unimplemented");
    }

    private void loadParser() {
	// Load the generated parser into this VM, and instantiate it
	throw new IllegalArgumentException("Unimplemented");
    }

    public void handleInput(String line) {
	Object result= parseSentenceAs(fNonTerm, line);

	GrammarInteractionView.println(result.toString());
    }

    private Object parseSentenceAs(ASTNode node, String sentence) {
	throw new IllegalArgumentException("Unimplemented");
    }

    private static String stripAnnotations(String symbol) {
	String result= symbol;
	while (result.lastIndexOf('$') >= 0)
	    result= result.substring(0, result.lastIndexOf('$'));
	return result;
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
