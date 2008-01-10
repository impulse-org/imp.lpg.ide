package org.eclipse.imp.lpg.editor;

import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.AbstractVisitor;
import org.eclipse.imp.lpg.parser.LPGParser.AliasSeg;
import org.eclipse.imp.lpg.parser.LPGParser.AstSeg;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EofSeg;
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
import org.eclipse.imp.lpg.parser.LPGParser.NoticeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.SYMBOLList;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TrailersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TypesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.import_segment;
import org.eclipse.imp.lpg.parser.LPGParser.include_segment;
import org.eclipse.imp.lpg.parser.LPGParser.name0;
import org.eclipse.imp.lpg.parser.LPGParser.name1;
import org.eclipse.imp.lpg.parser.LPGParser.name2;
import org.eclipse.imp.lpg.parser.LPGParser.name3;
import org.eclipse.imp.lpg.parser.LPGParser.name4;
import org.eclipse.imp.lpg.parser.LPGParser.name5;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.optTerminalAlias;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value0;
import org.eclipse.imp.lpg.parser.LPGParser.option_value1;
import org.eclipse.imp.lpg.parser.LPGParser.produces0;
import org.eclipse.imp.lpg.parser.LPGParser.produces1;
import org.eclipse.imp.lpg.parser.LPGParser.produces2;
import org.eclipse.imp.lpg.parser.LPGParser.produces3;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol0;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol1;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs0;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs1;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol1;
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
                if (value instanceof option_value0)
                    createSubItem(n);
                else if (value instanceof option_value1)
                    createSubItem(n);
            } else
                createSubItem(n);
            return true;
        }

        public boolean visit(import_segment n) {
            pushSubItem(n);
            return false;
        }

        public void endVisit(import_segment n) {
            popSubItem();
        }

        public boolean visit(NoticeSeg n) {
            pushSubItem(n);
            return false;
        }

        public void endVisit(NoticeSeg n) {
            popSubItem();
        }

        public boolean visit(AliasSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(AliasSeg n) {
            popSubItem();
        }

        public boolean visit(DefineSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(DefineSeg n) {
            popSubItem();
        }

        public boolean visit(defineSpec n) {
            createSubItem((ASTNode) n.getmacro_name_symbol());
            return true;
        }

        public boolean visit(EofSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(EofSeg n) {
            popSubItem();
        }

        public boolean visit(ExportSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(ExportSeg n) {
            popSubItem();
        }

        public boolean visit(terminal_symbol1 n) {
            createSubItem((ASTNode) n.getMACRO_NAME());
            return false;
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

        // SMS 27 Jun 2006
        // Trailers were omitted originally but they can occur
        public boolean visit(TrailersSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TrailersSeg n) {
            popSubItem();
        }

        public void endVisit(include_segment n) {
            pushSubItem(n);
        }

        public boolean visit(IdentifierSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(IdentifierSeg n) {
            popSubItem();
        }

        public boolean visit(KeywordsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(KeywordsSeg n) {
            popSubItem();
        }

        public boolean visit(StartSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(StartSeg n) {
            popSubItem();
        }

        public void endVisit(start_symbol0 n) {
            createSubItem(n);
        }

        public void endVisit(start_symbol1 n) {
            createSubItem(n);
        }

        public boolean visit(TerminalsSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TerminalsSeg n) {
            popSubItem();
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

        public boolean visit(AstSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(AstSeg n) {
            popSubItem();
        }

        public boolean visit(RulesSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(RulesSeg n) {
            popSubItem();
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

        public void endVisit(symWithAttrs0 n) {
            fRHSLabel.append(' ');
            fRHSLabel.append(n.getIToken().toString());
        }

        public void endVisit(symWithAttrs1 n) {
            fRHSLabel.append(' ');
            fRHSLabel.append(n.getSYMBOL().toString());
        }

        public boolean visit(TypesSeg n) {
            pushSubItem(n);
            return true;
        }

        public void endVisit(TypesSeg n) {
            popSubItem();
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
            if (produces instanceof produces0)
                return ((produces0) produces).getLeftIToken().toString();
            else if (produces instanceof produces1)
                return ((produces1) produces).getLeftIToken().toString();
            else if (produces instanceof produces2)
                return ((produces2) produces).getLeftIToken().toString();
            else if (produces instanceof produces3)
                return ((produces3) produces).getLeftIToken().toString();
            else
                return "<???>";
        }

        public String nameImage(Iname name) {
            if (name instanceof name0)
                return ((name0) name).getLeftIToken().toString();
            else if (name instanceof name1)
                return ((name1) name).getLeftIToken().toString();
            else if (name instanceof name2)
                return "$empty";
            else if (name instanceof name3)
                return "$error";
            else if (name instanceof name4)
                return "$eol";
            else if (name instanceof name5)
                return ((name5) name).getLeftIToken().toString();
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
