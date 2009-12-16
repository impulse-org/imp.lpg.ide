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

import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.AbstractVisitor;
import org.eclipse.imp.lpg.parser.LPGParser.AliasSeg;
import org.eclipse.imp.lpg.parser.LPGParser.AstSeg;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EofSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EolSeg;
import org.eclipse.imp.lpg.parser.LPGParser.ErrorSeg;
import org.eclipse.imp.lpg.parser.LPGParser.ExportSeg;
import org.eclipse.imp.lpg.parser.LPGParser.GlobalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.HeadersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.IdentifierSeg;
import org.eclipse.imp.lpg.parser.LPGParser.Iname;
import org.eclipse.imp.lpg.parser.LPGParser.Ioption_value;
import org.eclipse.imp.lpg.parser.LPGParser.Iproduces;
import org.eclipse.imp.lpg.parser.LPGParser.Isymbol_list;
import org.eclipse.imp.lpg.parser.LPGParser.Iterminal_symbol;
import org.eclipse.imp.lpg.parser.LPGParser.KeywordsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.NamesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.NoticeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.PredecessorSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RecoverSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.SYMBOLList;
import org.eclipse.imp.lpg.parser.LPGParser.SoftKeywordsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TrailersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TypesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.import_segment;
import org.eclipse.imp.lpg.parser.LPGParser.include_segment;
import org.eclipse.imp.lpg.parser.LPGParser.name__EMPTY_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.name__EOL_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.name__ERROR_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.name__IDENTIFIER_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.name__MACRO_NAME;
import org.eclipse.imp.lpg.parser.LPGParser.name__SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.optTerminalAlias;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_LEFT_PAREN_symbol_list_RIGHT_PAREN;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.produces__ARROW;
import org.eclipse.imp.lpg.parser.LPGParser.produces__EQUIVALENCE;
import org.eclipse.imp.lpg.parser.LPGParser.produces__PRIORITY_ARROW;
import org.eclipse.imp.lpg.parser.LPGParser.produces__PRIORITY_EQUIVALENCE;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol__MACRO_NAME;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol__SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__EMPTY_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__SYMBOL_optAttrList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol__MACRO_NAME;
import org.eclipse.imp.lpg.parser.LPGParser.type_declarations;
import org.eclipse.imp.services.base.TreeModelBuilderBase;

public class LPGTreeModelBuilder extends TreeModelBuilderBase {
    @Override
    public void visitTree(Object root) {
    	if (root == null) return;
    	
        ASTNode rootNode= (ASTNode) root;
        LPGModelVisitor visitor= new LPGModelVisitor();

        rootNode.accept(visitor);
    }

    private class LPGModelVisitor extends AbstractVisitor {
        private StringBuffer fRHSLabel;

        @Override
        public void unimplementedVisitor(String s) { }

        public boolean visit(option_specList n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(option_specList n) {
            popSubItem();
        }

        public boolean visit(option n) {

            Ioption_value value= n.getoption_value();

            if (value != null) {
                if (value instanceof option_value__EQUAL_SYMBOL)
                    createSubItem(n);
                else if (value instanceof option_value__EQUAL_LEFT_PAREN_symbol_list_RIGHT_PAREN)
                    createSubItem(n);
            } else
                createSubItem(n);
            return true;
        }

        public boolean visit(AliasSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(AliasSeg n) {
            popSubItem();
        }

        public boolean visit(AstSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(AstSeg n) {
            popSubItem();
        }

        public boolean visit(DefineSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(DefineSeg n) {
            popSubItem();
        }

        public boolean visit(EofSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(EofSeg n) {
            popSubItem();
        }

        public boolean visit(EolSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(EolSeg n) {
            popSubItem();
        }

        public boolean visit(ErrorSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(ErrorSeg n) {
            popSubItem();
        }

        public boolean visit(ExportSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(ExportSeg n) {
            popSubItem();
        }

        public boolean visit(GlobalsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(GlobalsSeg n) {
            popSubItem();
        }

        public boolean visit(HeadersSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(HeadersSeg n) {
            popSubItem();
        }

        public boolean visit(IdentifierSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(IdentifierSeg n) {
            popSubItem();
        }

        public boolean visit(import_segment n) {
            pushSubItem(n);
            return false;
        }

        public void endVisit(import_segment n) {
            popSubItem();
        }

        public boolean visit(include_segment n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(include_segment n) {
            popSubItem();
        }

        public boolean visit(KeywordsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(KeywordsSeg n) {
            popSubItem();
        }

        public boolean visit(NamesSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(NamesSeg n) {
            popSubItem();
        }

        public boolean visit(NoticeSeg n) {
            pushSubItem(n);
            return false;
        }

        public void endVisit(NoticeSeg n) {
            popSubItem();
        }

        public boolean visit(PredecessorSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(PredecessorSeg n) {
            popSubItem();
        }

        public boolean visit(RecoverSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(RecoverSeg n) {
            popSubItem();
        }

        public boolean visit(RulesSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(RulesSeg n) {
            popSubItem();
        }

        public boolean visit(SoftKeywordsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(SoftKeywordsSeg n) {
            popSubItem();
        }

        public boolean visit(StartSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(StartSeg n) {
            popSubItem();
        }

        public boolean visit(TerminalsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TerminalsSeg n) {
            popSubItem();
        }

        public boolean visit(TrailersSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TrailersSeg n) {
            popSubItem();
        }

        public boolean visit(TypesSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TypesSeg n) {
            popSubItem();
        }

        public boolean visit(defineSpec n) {
            createSubItem((ASTNode) n.getmacro_name_symbol());
            return true;
        }

        public boolean visit(terminal_symbol__MACRO_NAME n) {
            createSubItem((ASTNode) n.getMACRO_NAME());
            return false;
        }

        public void endVisit(start_symbol__SYMBOL n) {
            createSubItem(n);
        }

        public void endVisit(start_symbol__MACRO_NAME n) {
            createSubItem(n);
        }

        public void endVisit(terminal n) {
            Iterminal_symbol symbol= n.getterminal_symbol();
            optTerminalAlias alias= n.getoptTerminalAlias();
            String label;
            if (alias != null) {
                Iproduces prod= alias.getproduces();
                Iname name= alias.getname();
                label= nameImage(name) + " " + producesImage(prod) + " " + symbolImage(symbol);
            } else
                label= symbolImage(symbol);
            createSubItem(symbol);
        }

        public boolean visit(nonTerm n) {
            if (n.getruleList().size() > 1)
                pushSubItem(n);
            return true;
        }

        public void endVisit(nonTerm n) {
            if (n.getruleList().size() > 1)
                popSubItem();
        }

        public boolean visit(rule n) {
            fRHSLabel= new StringBuffer();
            final nonTerm parentNonTerm= (nonTerm) n.getParent().getParent();
            if (parentNonTerm.getruleList().size() == 1) {
                fRHSLabel.append(parentNonTerm.getruleNameWithAttributes().getSYMBOL());
                fRHSLabel.append(" ::= ");
            }
            return true;
        }

        public void endVisit(rule n) {
            createSubItem(n);
        }

        public void endVisit(symWithAttrs__EMPTY_KEY n) {
            fRHSLabel.append(' ');
            fRHSLabel.append(n.getIToken().toString());
        }

        public void endVisit(symWithAttrs__SYMBOL_optAttrList n) {
            fRHSLabel.append(' ');
            fRHSLabel.append(n.getSYMBOL().toString());
        }

        // public boolean visit(types_segment1 n) {
        // return true;
        // }
        public boolean visit(type_declarations n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(type_declarations n) {
            popSubItem();
        }

        public String producesImage(Iproduces produces) {
            if (produces instanceof produces__EQUIVALENCE)
                return ((produces__EQUIVALENCE) produces).getLeftIToken().toString();
            else if (produces instanceof produces__PRIORITY_EQUIVALENCE)
                return ((produces__PRIORITY_EQUIVALENCE) produces).getLeftIToken().toString();
            else if (produces instanceof produces__ARROW)
                return ((produces__ARROW) produces).getLeftIToken().toString();
            else if (produces instanceof produces__PRIORITY_ARROW)
                return ((produces__PRIORITY_ARROW) produces).getLeftIToken().toString();
            else
                return "<???>";
        }

        public String nameImage(Iname name) {
            if (name instanceof name__SYMBOL)
                return ((name__SYMBOL) name).getLeftIToken().toString();
            else if (name instanceof name__MACRO_NAME)
                return ((name__MACRO_NAME) name).getLeftIToken().toString();
            else if (name instanceof name__EMPTY_KEY)
                return "$empty";
            else if (name instanceof name__ERROR_KEY)
                return "$error";
            else if (name instanceof name__EOL_KEY)
                return "$eol";
            else if (name instanceof name__IDENTIFIER_KEY)
                return ((name__IDENTIFIER_KEY) name).getLeftIToken().toString();
            else
                return "<???>";
        }

        private String symbolImage(IASTNodeToken symbol) {
            return symbol.getLeftIToken().toString();
        }

        private String symbolListImage(Isymbol_list symbols) {
            SYMBOLList symbolList= (SYMBOLList) symbols;
            StringBuffer buff= new StringBuffer();
            buff.append('(');
            for(int i= 0; i < symbolList.size(); i++) {
                if (i > 0)
                    buff.append(',');
                buff.append(symbolImage(symbolList.getSYMBOLAt(i)));
            }
            buff.append(')');
            return buff.toString();
        }

        private String blockImage(ASTNodeToken block) {
            return block.getLeftIToken().toString();
        }
    }
}
