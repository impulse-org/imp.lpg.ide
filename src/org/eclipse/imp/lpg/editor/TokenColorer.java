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

package org.eclipse.imp.lpg.editor;

import lpg.runtime.IToken;

import org.eclipse.imp.lpg.parser.LPGParsersym;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.ITokenColorer;
import org.eclipse.jface.text.IRegion;
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

        COMMENT= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_CYAN),
                null, SWT.ITALIC);
        SYMBOL= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_RED),
                null, SWT.NORMAL);
        KEYWORD= new TextAttribute(display.getSystemColor(SWT.COLOR_BLUE),
                null, SWT.BOLD);
        EMPTY= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY),
                null, SWT.BOLD);
        LITERAL= new TextAttribute(display.getSystemColor(SWT.COLOR_DARK_GRAY),
                null, SWT.ITALIC);
        ANNOTATION= new TextAttribute(
                display.getSystemColor(SWT.COLOR_MAGENTA), null, SWT.ITALIC);
    }

    public TextAttribute getColoring(IParseController controller, Object o) {
        IToken token= (IToken) o;

        if (token.getKind() == LPGParsersym.TK_EMPTY_KEY)
            return EMPTY;
        if (((SimpleLPGParseController) controller).isKeyword(token.getKind()))
            return KEYWORD;
        if (token.getKind() == LPGParsersym.TK_SYMBOL) {
            int tokStartOffset= token.getStartOffset();
            char ch= ((SimpleLPGParseController) controller).getParser().getIPrsStream().getInputChars()[tokStartOffset];
            if (ch == '\'' || ch == '"')
                return LITERAL;
            return SYMBOL;
        }
        if (token.getKind() == LPGParsersym.TK_SINGLE_LINE_COMMENT)
            return COMMENT;
        if (token.getKind() == LPGParsersym.TK_MACRO_NAME)
            return ANNOTATION;

        return null;
    }

    public IRegion calculateDamageExtent(IRegion seed, IParseController ctlr) {
        return seed; // TODO naive, doesn't work in general
    }
}
