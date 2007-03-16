package org.eclipse.safari.jikespg.editor;

import org.eclipse.safari.jikespg.parser.ASTUtils;
import org.eclipse.safari.jikespg.parser.JikesPGParser.ASTNode;
import org.eclipse.safari.jikespg.parser.JikesPGParser.IASTNodeToken;
import org.eclipse.safari.jikespg.parser.JikesPGParser.JikesPG;
import org.eclipse.safari.jikespg.parser.JikesPGParser.nonTerm;
import org.eclipse.safari.jikespg.parser.JikesPGParser.terminal;
import org.eclipse.uide.core.IDocumentationProvider;
import org.eclipse.uide.core.ILanguageService;
import org.eclipse.uide.model.ICompilationUnit;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.utils.HTMLPrinter;

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
        return new String(parseController.getLexer().getLexStream().getInputChars(), start, end-start+1);
    }
}
