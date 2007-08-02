/*
 * Created on Oct 28, 2005
 */
package org.eclipse.imp.lpg.editor;

import lpg.runtime.IToken;

import org.eclipse.imp.editor.ITokenColorer;
import org.eclipse.imp.lpg.parser.LPGLexer;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class TokenColorer implements ITokenColorer {
    static TextAttribute COMMENT;

    static TextAttribute SYMBOL;

    static TextAttribute KEYWORD;

    static TextAttribute LITERAL;

    static TextAttribute EMPTY;

    static TextAttribute ANNOTATION;

    public TokenColorer() {
	Display display= Display.getCurrent();

	COMMENT= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_CYAN), null, SWT.ITALIC);
	SYMBOL= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_RED), null, SWT.NORMAL);
	KEYWORD= new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE), null, SWT.BOLD);
	EMPTY= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY), null, SWT.BOLD);
	LITERAL= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY), null, SWT.ITALIC);
	ANNOTATION= new TextAttribute(display.getSystemColor(SWT.COLOR_MAGENTA), null, SWT.ITALIC);
    }

    public TextAttribute getColoring(IParseController controller, IToken token) {
	if (token.getKind() == LPGLexer.TK_EMPTY_KEY)
	    return EMPTY;
	if (controller.isKeyword(token.getKind()))
	    return KEYWORD;
	if (token.getKind() == LPGLexer.TK_SYMBOL) {
	    char ch= controller.getLexer().getLexStream().getInputChars()[token.getStartOffset()];
	    if (ch == '\'' || ch == '"')
		return LITERAL;
	    return SYMBOL;
	}
	if (token.getKind() == LPGLexer.TK_SINGLE_LINE_COMMENT)
	    return COMMENT;
	if (token.getKind() == LPGLexer.TK_MACRO_NAME)
	    return ANNOTATION;

	return null;
    }
}
