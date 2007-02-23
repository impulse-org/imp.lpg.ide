package org.eclipse.safari.jikespg.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.safari.jikespg.parser.ASTUtils;
import org.eclipse.safari.jikespg.parser.JikesPGParser;
import org.eclipse.safari.jikespg.parser.ParseController;
import org.eclipse.safari.jikespg.parser.JikesPGParser.*;
import org.eclipse.safari.jikespg.views.GrammarInteractionView;
import org.eclipse.safari.jikespg.views.GrammarInteractionView.InputHandler;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;

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
	ASTNode nt;

	dumpSymbolTable();

	if (fNode instanceof symWithAttrs1) {
	    symWithAttrs1 sym= (symWithAttrs1) fNode;
	    nt= ASTUtils.findDefOf((IASTNodeToken) sym, (JikesPG) fRoot);
	} else if (fNode instanceof IASTNodeToken) {
	    IASTNodeToken tok= (IASTNodeToken) fNode;
	    nt= ASTUtils.findDefOf(tok, (JikesPG) fRoot);
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
	JikesPGParser.SymbolTable st= fRoot.symbolTable;
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
	IASTNodeLocator locator= parseController.getNodeLocator();

	return (ASTNode) locator.findNode(fRoot, sel.x);
    }
}
