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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lpg.runtime.IAst;
import lpg.runtime.IToken;

import org.eclipse.imp.language.ILanguageService;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.AliasSeg;
import org.eclipse.imp.lpg.parser.LPGParser.AstSeg;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EofSeg;
import org.eclipse.imp.lpg.parser.LPGParser.EolSeg;
import org.eclipse.imp.lpg.parser.LPGParser.ErrorSeg;
import org.eclipse.imp.lpg.parser.LPGParser.ExportSeg;
import org.eclipse.imp.lpg.parser.LPGParser.GlobalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.HeadersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.IdentifierSeg;
import org.eclipse.imp.lpg.parser.LPGParser.ImportSeg;
import org.eclipse.imp.lpg.parser.LPGParser.IncludeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.KeywordsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.NamesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.NoticeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.PredecessorSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RecoverSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RuleName;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.SYMBOLList;
import org.eclipse.imp.lpg.parser.LPGParser.SoftKeywordsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TrailersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TypesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.action_segment;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.drop_command__DROPRULES_KEY_drop_rules;
import org.eclipse.imp.lpg.parser.LPGParser.drop_rule;
import org.eclipse.imp.lpg.parser.LPGParser.import_segment;
import org.eclipse.imp.lpg.parser.LPGParser.include_segment;
import org.eclipse.imp.lpg.parser.LPGParser.keywordSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nameSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.nonTermList;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_LEFT_PAREN_symbol_list_RIGHT_PAREN;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.recover_symbol;
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

    private enum SegmentKeywordCase {
        Uppercase, Lowercase, Capitalized
    }

    private SegmentKeywordCase fSegmentKeywordCase= SegmentKeywordCase.Capitalized;

    public void formatterStarts(String initialIndentation) {
        // Should pick up preferences here
        fIndentSize= 4;
        StringBuffer buff= new StringBuffer(fIndentSize);
        for(int i= 0; i < fIndentSize; i++)
            buff.append(' ');
        fIndentString= buff.toString();
        fIndentProducesToWidestNonTerm= false;
    }

    public String format(IParseController parseController, String content, boolean isLineStart, String indentation, int[] positions) {
        final StringBuffer fBuff= new StringBuffer();
        final Set<IToken> fAdjunctTokens= new HashSet<IToken>();
        final IAst[] adjunctNode= new ASTNode[1];
        final List<IToken> fFollowingAdjuncts= new ArrayList<IToken>();
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

                for(IToken adjunct: precAdjuncts) {
                    if (!fAdjunctTokens.contains(adjunct)) {
                        fBuff.append("    "); // BUG do something more sensible here
                        fBuff.append(adjunct);
                        fBuff.append('\n');
                    }
                    fAdjunctTokens.add(adjunct);
                }
                if (fFollowingAdjuncts.size() == 0) {
                    IToken right= n.getRightIToken();
                    IToken[] follAdjuncts= right.getFollowingAdjuncts();

                    for(IToken adjunct: follAdjuncts) {
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
                    for(IToken adjunct: fFollowingAdjuncts) {
                        fBuff.append("    "); // BUG do something more sensible here
                        fBuff.append(adjunct);
                        fBuff.append('\n');
                    }
                    fFollowingAdjuncts.clear();
                }
            }

            @Override
            public void endVisit(option_specList n) {
                fBuff.append('\n');
            }

            public boolean visit(option_spec n) {
                fBuff.append("%options ");
                return true;
            }

            private void emitSegmentKeyword(String keyword) {
                fBuff.append('%');
                switch(fSegmentKeywordCase) {
                    case Lowercase:
                        fBuff.append(keyword.toLowerCase());
                        break;
                    case Uppercase:
                        fBuff.append(keyword.toUpperCase());
                        break;
                    case Capitalized:
                        String lowerKey= keyword.toLowerCase();
                        String capKey= Character.toUpperCase(lowerKey.charAt(0)) + lowerKey.substring(1);
                        fBuff.append(capKey);
                        break;
                }
                fBuff.append('\n');
            }

            public boolean visit(optionList n) {
                for(int i= 0; i < n.size(); i++) {
                    if (i > 0) fBuff.append(",");
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
                for(int i= 0; i < symList.size(); i++) {
                    if (i > 0) fBuff.append(',');
                    fBuff.append(symList.getSYMBOLAt(i));
                }
                fBuff.append(')');
                return false;
            }

            public boolean visit(NoticeSeg n) {
                emitSegmentKeyword("Notice");
                return true;
            }

            public void endVisit(NoticeSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(GlobalsSeg n) {
                emitSegmentKeyword("Globals");
                fBuff.append(fIndentString);
                return true;
            }

            public void endVisit(GlobalsSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(HeadersSeg n) {
                emitSegmentKeyword("Headers");
                return true;
            }

            public void endVisit(HeadersSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(IdentifierSeg n) {
                emitSegmentKeyword("Identifier");
                return true;
            }

            public void endVisit(IdentifierSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(EofSeg n) {
                emitSegmentKeyword("EOF");
                return true;
            }

            public void endVisit(EofSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(terminal_symbol__SYMBOL n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return false;
            }

            public boolean visit(DefineSeg n) {
                emitSegmentKeyword("Define");
                return true;
            }

            public void endVisit(DefineSeg n) {
                appendSegmentEnd();
            }

            public void endVisit(defineSpec n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getmacro_name_symbol());
                fBuff.append(' ');
                fBuff.append(n.getmacro_segment());
                fBuff.append('\n');
            }

            public boolean visit(TerminalsSeg n) {
                emitSegmentKeyword("Terminals");
                return true;
            }

            public void endVisit(TerminalsSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(terminal n) {
                fBuff.append(fIndentString + n.getterminal_symbol());
                if (n.getoptTerminalAlias() != null)
                    fBuff.append(" ::= " + n.getoptTerminalAlias().getname());
                fBuff.append('\n');
                return false;
            }

            public boolean visit(StartSeg n) {
                emitSegmentKeyword("Start");
                return true;
            }

            public void endVisit(StartSeg n) {
                appendSegmentEnd();
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

            @Override
            public boolean visit(AstSeg n) {
                emitSegmentKeyword("AST");
                return true;
            }

            @Override
            public void endVisit(AstSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(EolSeg n) {
                emitSegmentKeyword("EOL");
                return true;
            }

            @Override
            public void endVisit(EolSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(ErrorSeg n) {
                emitSegmentKeyword("Error");
                return true;
            }

            @Override
            public void endVisit(ErrorSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(ExportSeg n) {
                emitSegmentKeyword("Export");
                return true;
            }

            @Override
            public void endVisit(ExportSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(ImportSeg n) {
                emitSegmentKeyword("Import");
                return true;
            }

            @Override
            public boolean visit(import_segment n) {
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return super.visit(n);
            }

            @Override
            public boolean visit(drop_command__DROPRULES_KEY_drop_rules n) {
                fBuff.append(n.getDROPRULES_KEY());
                return true;
            }

            @Override
            public boolean visit(drop_rule n) {
                fBuff.append(n.getSYMBOL());
                if (n.getoptMacroName() != null) {
                    fBuff.append(' ');
                    fBuff.append(n.getoptMacroName());
                }
                fBuff.append(' ');
                fBuff.append(n.getproduces());
                fBuff.append(' ');
                // ruleList taken care of one-by-one, by visit(rule)
                return super.visit(n);
            }

            @Override
            public void endVisit(ImportSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(IncludeSeg n) {
                emitSegmentKeyword("Include");
                return true;
            }

            @Override
            public boolean visit(include_segment n) {
                fBuff.append(fIndentString);
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return false;
            }

            @Override
            public void endVisit(IncludeSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(KeywordsSeg n) {
                emitSegmentKeyword("Keywords");
                return true;
            }

            @Override
            public boolean visit(keywordSpec n) {
                fBuff.append(n.getname());
                if (n.getproduces() != null) {
                    fBuff.append(' ');
                    fBuff.append(n.getproduces());
                    fBuff.append(' ');
                    fBuff.append(n.getterminal_symbol());
                    fBuff.append('\n');
                }
                return super.visit(n);
            }

            @Override
            public void endVisit(KeywordsSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(NamesSeg n) {
                emitSegmentKeyword("Names");
                return true;
            }

            @Override
            public boolean visit(nameSpec n) {
                fBuff.append(n.getname());
                fBuff.append(' ');
                fBuff.append(n.getproduces());
                fBuff.append(' ');
                fBuff.append(n.getname3());
                fBuff.append('\n');
                return super.visit(n);
            }

            @Override
            public void endVisit(NamesSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(PredecessorSeg n) {
                emitSegmentKeyword("Predecessor");
                return true;
            }

            @Override
            public void endVisit(PredecessorSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(RecoverSeg n) {
                emitSegmentKeyword("Recover");
                return true;
            }

            @Override
            public boolean visit(recover_symbol n) {
                fBuff.append(n.getSYMBOL());
                fBuff.append('\n');
                return false;
            }

            @Override
            public void endVisit(RecoverSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(SoftKeywordsSeg n) {
                emitSegmentKeyword("SoftKeywords");
                return true;
            }

            @Override
            public void endVisit(SoftKeywordsSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(TrailersSeg n) {
                emitSegmentKeyword("Trailers");
                return true;
            }

            @Override
            public void endVisit(TrailersSeg n) {
                appendSegmentEnd();
            }

            @Override
            public boolean visit(TypesSeg n) {
                emitSegmentKeyword("Types");
                return true;
            }

            @Override
            public void endVisit(TypesSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(RulesSeg n) {
                emitSegmentKeyword("Rules");
                if (fIndentProducesToWidestNonTerm) {
                    rules_segment rulesSegment= n.getrules_segment();
                    nonTermList nonTermList= rulesSegment.getnonTermList();
                    int maxLHSSymWid= 0;
                    for(int i= 0; i < nonTermList.size(); i++) {
                        int lhsSymWid= nonTermList.getElementAt(i).getLeftIToken().toString().length();
                        if (lhsSymWid > maxLHSSymWid) maxLHSSymWid= lhsSymWid;
                    }
                    prodIndent= fIndentSize + maxLHSSymWid + 1;
                }
                return true;
            }

            public void endVisit(RulesSeg n) {
                appendSegmentEnd();
            }

            public boolean visit(nonTerm n) {
                fBuff.append(fIndentString);
                RuleName lhsName= n.getruleNameWithAttributes();
                fBuff.append(lhsName);
                if (fIndentProducesToWidestNonTerm) {
                    for(int i= lhsName.toString().length() + fIndentSize + 1; i <= prodIndent; i++) {
                        fBuff.append(' ');
                    }
                } else {
                    fBuff.append(' ');
                }
                fBuff.append(n.getproduces());
//                fBuff.append(' ');
                prodCount= 0;
                if (!fIndentProducesToWidestNonTerm) {
                    prodIndent= fIndentSize + lhsName.toString().length() + 1;
                }
                return true;
            }

            public boolean visit(RuleName n) {
                return true;
            }

            // public boolean visit(enumBitSpec n) {
            // fBuff.append('|');
            // fBuff.append(n.getclassName());
            // fBuff.append('|');
            // return false;
            // }
            // public boolean visit(enumListSpec n) {
            // fBuff.append('[');
            // fBuff.append(n.getclassName());
            // fBuff.append(']');
            // return false;
            // }
            // public boolean visit(enumValueSpec n) {
            // fBuff.append('#');
            // fBuff.append(n.getclassName());
            // fBuff.append('#');
            // return false;
            // }
            public void endVisit(nonTerm n) {
                fBuff.append('\n');
            }

            public boolean visit(rule n) {
                if (prodCount > 0) {
                    fBuff.append('\n');
                    for(int i= 0; i < prodIndent; i++)
                        fBuff.append(' ');
                    fBuff.append("|  ");
                }
                prodCount++;
                return true;
            }

            public boolean visit(action_segment n) {
                fBuff.append(' ');
                fBuff.append(n.getBLOCK());
                fBuff.append('\n');
                return false;
            }

            @Override
            public boolean visit(AliasSeg n) {
                fBuff.append("%Alias");
                return true;
            }

            @Override
            public void endVisit(AliasSeg n) {
                appendSegmentEnd();
            }

            private void appendSegmentEnd() {
                emitSegmentKeyword("End");
                fBuff.append('\n');
            }

            public boolean visit(symWithAttrs__EMPTY_KEY n) {
                fBuff.append(' ');
                fBuff.append(n.getEMPTY_KEY());
                return false;
            }

            public boolean visit(symWithAttrs__SYMBOL_optAttrList n) {
                fBuff.append(' ');
                fBuff.append(n.getSYMBOL());
                symAttrs attr= n.getoptAttrList();
                if (attr != null && attr.getMACRO_NAME() != null) {
                    fBuff.append(attr.getMACRO_NAME());
                }
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
