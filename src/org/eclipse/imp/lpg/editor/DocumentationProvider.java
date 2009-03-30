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

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.ParseController;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IDocumentationProvider;
import org.eclipse.imp.utils.HTMLPrinter;

public class DocumentationProvider implements IDocumentationProvider, ILanguageService {
    public String getDocumentation(Object target, IParseController parseController) {
	StringBuffer buff= new StringBuffer();
        ASTNode ast= (ASTNode) parseController.getCurrentAst();

        if (ast == null) return null;

        if (target instanceof IASTNodeToken) {
            ASTNode def= (ASTNode) ASTUtils.findDefOf((IASTNodeToken) target, (LPG) ast, parseController);

            if (def != null)
                return getSubstring(parseController, def.getLeftIToken().getStartOffset(), def.getRightIToken().getEndOffset());
        }
        if (target instanceof nonTerm) {
            nonTerm nt= (nonTerm) target;

            HTMLPrinter.addSmallHeader(buff, "non-terminal " + nt.getruleNameWithAttributes().getSYMBOL());
        } else if (target instanceof terminal) {
            terminal term= (terminal) target;
            HTMLPrinter.addSmallHeader(buff, "terminal " + term.getterminal_symbol());
        } else if (target instanceof ICompilationUnit) {
            ICompilationUnit icu= (ICompilationUnit) target;
            HTMLPrinter.addSmallHeader(buff, "source file " + HTMLPrinter.convertToHTMLContent(icu.getName()));
        }
//      return getSubstring(parseController, token);
        HTMLPrinter.addParagraph(buff, target.toString());
        return buff.toString();
    }

    public static String getSubstring(IParseController parseController, int start, int end) {
        return new String(((ParseController) parseController).getParser().getIPrsStream().getInputChars(), start, end-start+1);
    }
}
