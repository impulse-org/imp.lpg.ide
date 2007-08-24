/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.compare;

import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.AbstractVisitor;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.GlobalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.IJikesPG_item;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG_itemList;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.Visitor;
import org.eclipse.imp.lpg.parser.LPGParser.action_segmentList;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpecList;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.nonTermList;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.rules_segment;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminalList;

/**
 * A simple visitor that produces the immediate children of a given AST node, each
 * wrapped by a LPGStructureNode.<br>
 * The implementation below actually omits certain uninteresting intermediate nodes
 * from the structure.
 */
// TODO RMF 11/15/2006 - Can we do without this hand-written class?
public class GetChildrenVisitor extends AbstractVisitor implements Visitor {
    private final static Object[] NO_CHILDREN= new Object[0];

    Object[] fChildren= NO_CHILDREN;

    private final LPGStructureNode fStructureNode;

    public GetChildrenVisitor(LPGStructureNode node) {
        fStructureNode= node;
    }

    public void unimplementedVisitor(String s) { }

    public boolean visit(JikesPG n) {
	option_specList options= n.getoptions_segment();
        JikesPG_itemList itemList= n.getJikesPG_INPUT();
        int count= 1 + itemList.size();

        fChildren= new Object[count];
        fChildren[0]= new LPGStructureNode(options, fStructureNode, LPGStructureNode.OPTION, "options");

        for(int i= 0; i < itemList.size(); i++) {
            IJikesPG_item item= itemList.getJikesPG_itemAt(i);

            fChildren[i+1]= new LPGStructureNode((ASTNode) item, fStructureNode, LPGStructureNode.BODY, item.getLeftIToken().toString());
        }
        return false;
    }

    public boolean visit(GlobalsSeg n) {
        action_segmentList globals= (action_segmentList) n.getglobals_segment();
        fChildren= new Object[globals.size()];

        for(int i=0; i < globals.size(); i++)
            fChildren[i]= new LPGStructureNode(globals.getElementAt(i), fStructureNode, LPGStructureNode.GLOBAL, "global");
        return false;
    }

    public boolean visit(DefineSeg n) {
        defineSpecList defs= (defineSpecList) n.getdefine_segment();
        fChildren= new Object[defs.size()];
        for(int i=0; i < defs.size(); i++) {
            defineSpec ds= defs.getdefineSpecAt(i);
            fChildren[i]= new LPGStructureNode(ds, fStructureNode, LPGStructureNode.DEFINE, ds.getmacro_name_symbol().toString());
        }
        return false;
    }

    public boolean visit(TerminalsSeg n) {
        terminalList termList= (terminalList) n.getterminals_segment();
        int N= termList.size();

        fChildren= new Object[N];
        for(int i=0; i < N; i++) {
            terminal term= (terminal) termList.getElementAt(i);
            fChildren[N-i-1]= new LPGStructureNode(term, fStructureNode, LPGStructureNode.TERMINAL, term.getterminal_symbol().toString());
        }
        return false;
    }

    public boolean visit(RulesSeg n) {
        rules_segment rulesSegment= n.getrules_segment();
        nonTermList nonTermList= rulesSegment.getnonTermList();
        int N= nonTermList.size();

        fChildren= new Object[N];
        for(int i=0; i < N; i++) {
            nonTerm nt= (nonTerm) nonTermList.getElementAt(i);
            fChildren[N-i-1]= new LPGStructureNode(nt, fStructureNode, LPGStructureNode.NONTERMINAL, nt.getruleNameWithAttributes().getSYMBOL().toString());
        }
        return false;
    }

    public Object[] getChildren() {
        return fChildren;
    }
}