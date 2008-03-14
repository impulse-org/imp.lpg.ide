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

import org.eclipse.imp.lpg.ILPGResources;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
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
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
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
import org.eclipse.imp.services.base.OutlinerBase;
import org.eclipse.swt.graphics.Image;

public class Outliner extends OutlinerBase {
    /**
     * A visitor for ASTs. Its purpose is to create outline-tree items for AST
     * node types that are to appear in the outline view.
     */
    private final class OutlineVisitor extends AbstractVisitor {

        public void unimplementedVisitor(String s) {
            // Sometimes useful for debugging
            // System.out.println(s);
        }

        private StringBuffer fRHSLabel;

        // SMS 27 Jun 2006
        // This creates a "Options" item regardless of whether
        // there actually are options (so try not doing that)
        public boolean visit(JikesPG n) {
            // pushTopItem("Options", n);
            return true;
        }

        public void endVisit(JikesPG n) {
            // popSubItem();
        }

        // SMS 27 Jun 2006
        // Options_segments occur only where there are options
        // lines in the source, so use these as the basis of
        // creating "Options" items. These will be created
        // one per line rather than one per file (but then,
        // that's what the file actually says).
        public boolean visit(option_specList n) {
            pushTopItem("Options", n);
            return true;
        }

        public void endVisit(option_specList n) {
            popSubItem();
        }

        public boolean visit(option n) {
            Ioption_value value= n.getoption_value();

            if (value != null) {
                if (value instanceof option_value0)
                    createSubItem(symbolImage(n.getSYMBOL()) + " = " + symbolImage(((option_value0) value).getSYMBOL()), n);
                else if (value instanceof option_value1)
                    createSubItem(symbolImage(n.getSYMBOL()) + " = " + symbolListImage(((option_value1) value).getsymbol_list()), n);
            } else
                createSubItem(symbolImage(n.getSYMBOL()), n);
            return true;
        }

        public boolean visit(import_segment n) {
            pushTopItem("Import", n);
            return false;
        }

        public void endVisit(import_segment n) {
            popSubItem();
        }

        public boolean visit(NoticeSeg n) {
            pushTopItem("Notice", n);
            return false;
        }

        public void endVisit(NoticeSeg n) {
            popSubItem();
        }

        public boolean visit(AliasSeg n) {
            pushTopItem("Alias", n);
            return true;
        }

        public void endVisit(AliasSeg n) {
            popSubItem();
        }

        public boolean visit(DefineSeg n) {
            pushTopItem("Define", n);
            return true;
        }

        public void endVisit(DefineSeg n) {
            popSubItem();
        }

        public boolean visit(defineSpec n) {
            createSubItem(symbolImage(n.getmacro_name_symbol()), (ASTNode) n.getmacro_name_symbol());
            return true;
        }

        public boolean visit(EofSeg n) {
            pushTopItem("EOF", n);
            return true;
        }

        public void endVisit(EofSeg n) {
            popSubItem();
        }

        public boolean visit(ExportSeg n) {
            pushTopItem("Export", n);
            return true;
        }

        public void endVisit(ExportSeg n) {
            popSubItem();
        }

        public boolean visit(terminal_symbol1 n) {
            createSubItem(n.getMACRO_NAME().toString(), (ASTNode) n.getMACRO_NAME());
            return false;
        }

        public boolean visit(GlobalsSeg n) {
            pushTopItem("Globals", n);
            return true;
        }

        public void endVisit(GlobalsSeg n) {
            popSubItem();
        }

        public boolean visit(HeadersSeg n) {
            pushTopItem("Headers", n);
            return true;
        }

        public void endVisit(HeadersSeg n) {
            popSubItem();
        }

        // SMS 27 Jun 2006
        // Trailers were omitted originally but they can occur
        public boolean visit(TrailersSeg n) {
            pushTopItem("Trailers", n);
            return true;
        }

        public void endVisit(TrailersSeg n) {
            popSubItem();
        }

        public void endVisit(include_segment n) {
            createTopItem("Include " + n.getSYMBOL(), n);
        }

        public boolean visit(IdentifierSeg n) {
            pushTopItem("Identifiers", n);
            return true;
        }

        public void endVisit(IdentifierSeg n) {
            popSubItem();
        }

        public boolean visit(KeywordsSeg n) {
            pushTopItem("Keywords", n);
            return true;
        }

        public void endVisit(KeywordsSeg n) {
            popSubItem();
        }

        public boolean visit(StartSeg n) {
            pushTopItem("Start", n);
            return true;
        }

        public void endVisit(StartSeg n) {
            popSubItem();
        }

        public void endVisit(start_symbol0 n) {
            createSubItem(symbolImage(n), n);
        }

        public void endVisit(start_symbol1 n) {
            createSubItem("Start = " + symbolImage(n), n);
        }

        public boolean visit(TerminalsSeg n) {
            pushTopItem("Terminals", n);
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
            createSubItem(label, (ASTNode) symbol);
        }

        public boolean visit(AstSeg n) {
            pushTopItem("Ast", n);
            return true;
        }

        public void endVisit(AstSeg n) {
            popSubItem();
        }

        public boolean visit(RulesSeg n) {
            pushTopItem("Rules", n);
            return true;
        }

        public void endVisit(RulesSeg n) {
            popSubItem();
        }

        public boolean visit(nonTerm n) {
            if (n.getruleList().size() > 1)
                pushSubItem(symbolImage(n.getruleNameWithAttributes().getSYMBOL()), n);
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
            createSubItem(fRHSLabel.toString(), n);
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
            pushTopItem("Types", n);
            return true;
        }

        public void endVisit(TypesSeg n) {
            popSubItem();
        }

        public boolean visit(type_declarations n) {
            pushSubItem(symbolImage(n.getSYMBOL()), n);
            return true;
        }

        public void endVisit(type_declarations n) {
            popSubItem();
        }

    } // End OutlineVisitor

    // FIXME Should dispose() this image at some point, no?
    private static final Image sTreeItemImage= LPGRuntimePlugin.getInstance().getImageRegistry().get(ILPGResources.OUTLINE_ITEM);

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

    // SMS 18 Apr 2007
    // Added to implement abstract method in OutlinerBase
    // (new supertype)
    protected void sendVisitorToAST(Object node) {
        ASTNode root= (ASTNode) node;
        root.accept(new OutlineVisitor());
    }
}
