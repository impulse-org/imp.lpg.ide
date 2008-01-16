/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.editor;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.ParseController;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
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
            ASTNode def= (ASTNode) ASTUtils.findDefOf((IASTNodeToken) target, (JikesPG) ast, parseController);

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
        return new String(((ParseController) parseController).getLexer().getLexStream().getInputChars(), start, end-start+1);
    }
}
