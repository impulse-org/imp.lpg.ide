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

/*
 * Created on Mar 24, 2006
 */
package org.eclipse.imp.lpg.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lpg.runtime.IAst;
import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EofSeg;
import org.eclipse.imp.lpg.parser.LPGParser.GlobalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.HeadersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.IdentifierSeg;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.NoticeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RuleName;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.SYMBOLList;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.action_segment;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.nonTermList;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_LEFT_PAREN_symbol_list_RIGHT_PAREN;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.rules_segment;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol__MACRO_NAME;
import org.eclipse.imp.lpg.parser.LPGParser.start_symbol__SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.symAttrs;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__EMPTY_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__SYMBOL_optAttrList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol__SYMBOL;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.ISourceFormatter;

public class LPGFormatter implements ILanguageService, ISourceFormatter {
    private int fIndentSize= 6;
    private String fIndentString;

    private boolean fIndentProducesToWidestNonTerm= false;

    public void formatterStarts(String initialIndentation) {
        // Should pick up preferences here
        fIndentSize= 4;
        StringBuffer buff= new StringBuffer(fIndentSize);
        for(int i=0; i < fIndentSize; i++)
            buff.append(' ');
        fIndentString= buff.toString();
        fIndentProducesToWidestNonTerm= false;
    }

    public String format(IParseController parseController, String content, boolean isLineStart, String indentation, int[] positions) {
        final StringBuffer fBuff= new StringBuffer();
        final Set fAdjunctTokens= new HashSet();
        final IAst[] adjunctNode= new ASTNode[1];
        final List/*<IToken>*/ fFollowingAdjuncts= new ArrayList();
        LPG root= (LPG) parseController.getCurrentAst();

        root.accept(new LPGParser.AbstractVisitor() {
            private int prodCount;
            private int prodIndent;
            public void unimplementedVisitor(String s) {
                System.out.println("Unhandled node type: " + s);
            }
            public boolean preVisit(IAst n) {
                IToken left= n.getLeftIToken();
                IToken[] precAdjuncts= left.getPrecedingAdjuncts();

                for(int i= 0; i < precAdjuncts.length; i++) {
                    IToken adjunct= precAdjuncts[i];
                    if (!fAdjunctTokens.contains(adjunct)) {
                        fBuff.append(adjunct);
                        fBuff.append('\n');
                    }
                    fAdjunctTokens.add(adjunct);
                }

                if (fFollowingAdjuncts.size() == 0) {
                    IToken right= n.getRightIToken();
                    IToken[] follAdjuncts= right.getFollowingAdjuncts();

                    for(int i= 0; i < follAdjuncts.length; i++) {
                	IToken adjunct= follAdjuncts[i];

                	if (!fAdjunctTokens.contains(adjunct)) {
                	    fFollowingAdjuncts.add(adjunct);
                	    fAdjunctTokens.add(adjunct);
                	}
                    }
                    adjunctNode[0]= n;
                }
                return true;
            }
            public void postVisit(IAst n) {
        	if (n == adjunctNode[0]) {
        	    for(Iterator iter= fFollowingAdjuncts.iterator(); iter.hasNext(); ) {
        		IToken adjunct= (IToken) iter.next();
		    
        		fBuff.append(adjunct);
        		fBuff.append('\n');
        	    }
        	    fFollowingAdjuncts.clear();
        	}
            }
            public boolean visit(option_spec n) {
                fBuff.append("%options ");
                return true;
            }
            public boolean visit(optionList n) {
        	for(int i=0; i < n.size(); i++) {
        	    if (i > 0) fBuff.append(", ");
        	    final option opt= n.getoptionAt(i);
		    opt.accept(this);
        	}
        	return false;
            }
            public void endVisit(option_spec n) {
                fBuff.append('\n');
            }
            public boolean visit(option n) {
                fBuff.append(n.getSYMBOL());
                return true;
            }
            public boolean visit(option_value__EQUAL_SYMBOL n) {
                fBuff.append("=" + n.getSYMBOL());
                return false;
            }
            public boolean visit(option_value__EQUAL_LEFT_PAREN_symbol_list_RIGHT_PAREN n) {
                fBuff.append('(');
                SYMBOLList symList= n.getsymbol_list();
                for(int i=0; i < symList.size(); i++) {
                    if (i > 0) fBuff.append(',');
                    fBuff.append(symList.getSYMBOLAt(i));
                }
                fBuff.append(')');
                return false;
            }
            public boolean visit(NoticeSeg n) {
                fBuff.append("$Notice\n");
                return true;
            }
            public void endVisit(NoticeSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(GlobalsSeg n) {
                fBuff.append("$Globals\n");
                return true;
            }
            public void endVisit(GlobalsSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(HeadersSeg n) {
                fBuff.append("$Headers\n");
                return true;
            }
            public void endVisit(HeadersSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(IdentifierSeg n) {
                fBuff.append("$Identifier\n");
                return true;
            }
            public void endVisit(IdentifierSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(EofSeg n) {
                fBuff.append("$EOF\n");
                return true;
            }
            public void endVisit(EofSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(terminal_symbol__SYMBOL n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return false;
            }
            public boolean visit(DefineSeg n) {
                fBuff.append("$Define\n");
                return true;
            }
            public void endVisit(DefineSeg n) {
                fBuff.append("$End\n\n");
            }
            public void endVisit(defineSpec n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getmacro_name_symbol());
                fBuff.append(' ');
                fBuff.append(n.getmacro_segment());
                fBuff.append('\n');
            }
            public boolean visit(TerminalsSeg n) {
                fBuff.append("$Terminals\n");
                return true;
            }
            public void endVisit(TerminalsSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(terminal n) {
                fBuff.append(fIndentString + n.getterminal_symbol());
                if (n.getoptTerminalAlias() != null)
                    fBuff.append(" ::= " + n.getoptTerminalAlias().getname());
                fBuff.append('\n');
                return false;
            }
            public boolean visit(StartSeg n) {
                fBuff.append("$Start\n");
                return true;
            }
            public void endVisit(StartSeg n) {
                fBuff.append("$End\n\n");
            }
            public boolean visit(start_symbol__SYMBOL n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return false;
            }
            public boolean visit(start_symbol__MACRO_NAME n) {
                fBuff.append(n.getMACRO_NAME());
                return false;
            }
            public boolean visit(RulesSeg n) {
                fBuff.append("$Rules\n");
                if (fIndentProducesToWidestNonTerm) {
                    rules_segment rulesSegment= n.getrules_segment();
                    nonTermList nonTermList= rulesSegment.getnonTermList();
                    int maxLHSSymWid= 0;
                    for(int i=0; i < nonTermList.size(); i++) {
                        int lhsSymWid= nonTermList.getElementAt(i).getLeftIToken().toString().length();
                        if (lhsSymWid > maxLHSSymWid) maxLHSSymWid= lhsSymWid;
                    }
                    prodIndent= fIndentSize + maxLHSSymWid + 1;
                }
                return true;
            }
            public void endVisit(RulesSeg n) {
                fBuff.append("$End\n");
            }
            public boolean visit(nonTerm n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getruleNameWithAttributes().getSYMBOL());
                if (fIndentProducesToWidestNonTerm) {
                    for(int i=n.getruleNameWithAttributes().getSYMBOL().toString().length() + fIndentSize + 1; i <= prodIndent; i++)
                        fBuff.append(' ');
                } else
                    fBuff.append(' ');
                fBuff.append(n.getproduces());
                prodCount= 0;
                if (!fIndentProducesToWidestNonTerm)
                    prodIndent= fIndentSize + n.getruleNameWithAttributes().getSYMBOL().toString().length() + 1;
                return true;
            }
            public boolean visit(RuleName n) {
        	fBuff.append(n.getSYMBOL());
        	return true;
            }
//            public boolean visit(enumBitSpec n) {
//        	fBuff.append('|');
//        	fBuff.append(n.getclassName());
//        	fBuff.append('|');
//        	return false;
//            }
//            public boolean visit(enumListSpec n) {
//        	fBuff.append('[');
//        	fBuff.append(n.getclassName());
//        	fBuff.append(']');
//        	return false;
//            }
//            public boolean visit(enumValueSpec n) {
//        	fBuff.append('#');
//        	fBuff.append(n.getclassName());
//        	fBuff.append('#');
//        	return false;
//            }
            public void endVisit(nonTerm n) {
                fBuff.append('\n');
            }
            public boolean visit(rule n) {
                if (prodCount > 0) {
                    fBuff.append('\n');
                    for(int i=0; i < prodIndent; i++)
                        fBuff.append(' ');
                    fBuff.append("|  ");
                }
                prodCount++;
                return true;
            }
            public boolean visit(action_segment n) {
                fBuff.append(n.getBLOCK());
                fBuff.append('\n');
                return false;
            }
            public boolean visit(symWithAttrs__EMPTY_KEY n) {
                fBuff.append(' ');
                fBuff.append(n.getEMPTY_KEY());
                return false;
            }
            public boolean visit(symWithAttrs__SYMBOL_optAttrList n) {
                fBuff.append(' ');
                fBuff.append(n.getSYMBOL());
                return false;
            }
            public boolean visit(symAttrs n) {
                fBuff.append(' ');
                fBuff.append(n.getMACRO_NAME());
                return false;
            }
        });

	return fBuff.toString();
    }

    public void formatterStops() {
	// TODO Auto-generated method stub
    }
}
