package org.eclipse.safari.jikespg.actions;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.safari.jikespg.JikesPGRuntimePlugin;
import org.eclipse.safari.jikespg.parser.ASTUtils;
import org.eclipse.safari.jikespg.parser.JikesPGParser.*;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.uide.editor.UniversalEditor;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.refactoring.RefactoringStarter;

public class GenerateSentenceAction extends TextEditorAction {
    private final IFile fGrammarFile;
    private final ASTNode fNode;
    private final ASTNode fRoot;
    private final Set fRecursiveSet= new HashSet();

    public GenerateSentenceAction(UniversalEditor editor) {
	super(LanguageActionContributor.ResBundle, "generateSentence.", editor);

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

	if (fNode instanceof symWithAttrs1) {
	    symWithAttrs1 sym= (symWithAttrs1) fNode;
	    nt= ASTUtils.findDefOf((IASTNodeToken) sym, (JikesPG) fRoot);
	} else if (fNode instanceof IASTNodeToken) {
	    IASTNodeToken tok= (IASTNodeToken) fNode;
	    nt= ASTUtils.findDefOf(tok, (JikesPG) fRoot);
	} else
	    nt= fNode;
	if (!(nt instanceof nonTerm)) {
	    MessageDialog.openError(editor.getSite().getShell(), "Error", "Can only generate sentence for a non-terminal.");
	    return;
	}
	nonTerm fNonTerm= (nonTerm) nt;

	String result= generateSentenceFor(fNonTerm);

	MessageDialog.openInformation(editor.getSite().getShell(), "Sentence", result);
    }

    private String generateSentenceFor(ASTNode node) {
	if (node instanceof nonTerm) {
	    return generateSentenceFor((nonTerm) node);
	} else if (node instanceof terminal) {
	    terminal term= (terminal) node;
	    return term.getLeftIToken().toString();
	} else if (node instanceof IASTNodeToken) {
	    final String tokStr= ((IASTNodeToken) node).toString();
	    if (!tokStr.equals("$empty"))
		return tokStr;
	    return "";
	} else if (node instanceof rule)
	    return generateSentenceFor((rule) node);
	return "??";
    }

    private String generateSentenceFor(nonTerm nonTerm) {
	ruleList rhSides= nonTerm.getruleList();
	StringBuffer buff= new StringBuffer();

	if (isRecursive(nonTerm)) {
	    if (!fRecursiveSet.contains(nonTerm)) {
		fRecursiveSet.add(nonTerm);
		buff.append(generateSentenceFor(findARecursiveCase(nonTerm)));
	    } else
		buff.append(generateSentenceFor(findRootCase(nonTerm)));
	} else {
	    // Arbitrarily pick the first production rule
	    buff.append(generateSentenceFor(rhSides.getElementAt(0)));
	}
	return buff.toString();
    }

    private String generateSentenceFor(rule rhsElem) {
	StringBuffer buff= new StringBuffer();
	symWithAttrsList symList= rhsElem.getsymWithAttrsList();

	for(int symIdx= 0; symIdx < symList.size(); symIdx++) {
	    final IsymWithAttrs sym= symList.getsymWithAttrsAt(symIdx);
	    ASTNode def= ASTUtils.findDefOf((IASTNodeToken) sym, (JikesPG) fRoot);

	    if (def != null)
		buff.append(generateSentenceFor(def));
	    else
		buff.append(generateSentenceFor((ASTNode) sym));
	    if (symIdx < symList.size() - 1)
		buff.append(' ');
	}
	return buff.toString();
    }

    /**
     * @return The "root case," i.e., a production rule for the given non-terminal that is non-recursive
     */
    private rule findRootCase(nonTerm nonTerm) {
	String nonTermName= stripAnnotations(nonTerm.getruleNameWithAttributes().getSYMBOL().toString());
	ruleList rhSides= nonTerm.getruleList();
	for(int i=0; i < rhSides.size(); i++) {
	    rule rhsElem= (rule) rhSides.getElementAt(i);
	    symWithAttrsList symList= rhsElem.getsymWithAttrsList();
	    boolean recursive= false;

	    for(int j= 0; j < symList.size(); j++) {
		IsymWithAttrs sym= symList.getsymWithAttrsAt(j);
		String symName= stripAnnotations(sym.getLeftIToken().toString());

		if (symName.equals(nonTermName)) {
		    recursive= true;
		    break;
		}
	    }
	    if (!recursive)
		return rhsElem;
	}
	return null;
    }

    private rule findARecursiveCase(nonTerm nonTerm) {
	String nonTermName= stripAnnotations(nonTerm.getruleNameWithAttributes().getSYMBOL().toString());
	ruleList rhSides= nonTerm.getruleList();
	for(int i=0; i < rhSides.size(); i++) {
	    rule rhsElem= (rule) rhSides.getElementAt(i);
	    symWithAttrsList symList= rhsElem.getsymWithAttrsList();

	    for(int j= 0; j < symList.size(); j++) {
		IsymWithAttrs sym= symList.getsymWithAttrsAt(j);
		String symName= stripAnnotations(sym.getLeftIToken().toString());

		if (symName.equals(nonTermName))
		    return rhsElem;
	    }
	}
	return null;
    }

    private String stripAnnotations(String symbol) {
	String result= symbol;
	while (result.lastIndexOf('$') >= 0)
	    result= result.substring(0, result.lastIndexOf('$'));
	return result;
    }

    private boolean isRecursive(nonTerm nonTerm) {
	String nonTermName= stripAnnotations(nonTerm.getruleNameWithAttributes().getSYMBOL().toString());
	ruleList rhSides= nonTerm.getruleList();
	for(int i=0; i < rhSides.size(); i++) {
	    rule rule= (rule) rhSides.getElementAt(i);
	    symWithAttrsList symList= rule.getsymWithAttrsList();

	    for(int j= 0; j < symList.size(); j++) {
		IsymWithAttrs sym= symList.getsymWithAttrsAt(j);
		String symName= stripAnnotations(sym.getLeftIToken().toString());

		if (symName.equals(nonTermName))
		    return true;
	    }
	}
	return false;
    }

    private boolean isEmpty(rule rhsElem) {
	symWithAttrsList symList= rhsElem.getsymWithAttrsList();

	return (symList.size() == 1 && symList.getsymWithAttrsAt(0).getLeftIToken().toString().equals("$empty"));
    }

    private ASTNode getAST(UniversalEditor editor) {
	IParseController parseController= editor.getParseController();

	return (ASTNode) parseController.getCurrentAst();
    }

    private ASTNode findNode(UniversalEditor editor) {
	Point sel= editor.getSelection();
	IParseController parseController= editor.getParseController();
	IASTNodeLocator locator= parseController.getNodeLocator();

	return (ASTNode) locator.findNode(fRoot, sel.x);
    }
}
