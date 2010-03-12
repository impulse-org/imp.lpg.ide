/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
 *******************************************************************************/

package org.eclipse.imp.lpg.compare;

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
import org.eclipse.imp.lpg.parser.LPGParser.NamesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.NoticeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.PredecessorSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RecoverSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RulesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.SoftKeywordsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TrailersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TypesSeg;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol__SYMBOL;
import org.eclipse.imp.services.ICompareNodeIdentifier;

public class CompareNodeIdentifier implements ICompareNodeIdentifier {
    private static final Class<?>[] nodeTypes= {
        option_spec.class, AliasSeg.class, AstSeg.class, DefineSeg.class, EofSeg.class, EolSeg.class,
        ErrorSeg.class, ExportSeg.class, GlobalsSeg.class, HeadersSeg.class, IdentifierSeg.class,
        ImportSeg.class, IncludeSeg.class, KeywordsSeg.class, NamesSeg.class, NoticeSeg.class,
        PredecessorSeg.class, RecoverSeg.class, RulesSeg.class, SoftKeywordsSeg.class, StartSeg.class,
        TerminalsSeg.class, TrailersSeg.class, TypesSeg.class, defineSpec.class, terminal.class,
        nonTerm.class, option.class, ruleList.class, rule.class
    };

    public CompareNodeIdentifier() { }

    public String getID(Object o) {
        if (o instanceof option) { return ((option) o).getSYMBOL().toString(); }
        if (o instanceof defineSpec) { return ((defineSpec) o).getmacro_name_symbol().toString(); }
        if (o instanceof nonTerm) { return ((nonTerm) o).getruleNameWithAttributes().getSYMBOL().toString(); }
        if (o instanceof rule) {
            nonTerm nt= (nonTerm) (((rule) o).getParent().getParent());
            if (nt.getruleList().size() == 1) { // hack - the tree elides the nonTerm if there's only 1 rule
                return nt.getruleNameWithAttributes().getSYMBOL().toString();
            }
        }
        if (o instanceof terminal) { return ((terminal) o).getterminal_symbol().toString(); }
        if (o instanceof terminal_symbol__SYMBOL) { return ((terminal_symbol__SYMBOL) o).getSYMBOL().toString(); }
        for(int i=0; i < nodeTypes.length; i++) {
            if (nodeTypes[i].isInstance(o)) {
                return nodeTypes[i].getName();
            }
        }
        return o.toString();
    }

    public int getTypeCode(Object o) {
        for(int i=0; i < nodeTypes.length; i++) {
            if (nodeTypes[i].isInstance(o)) {
                return i;
            }
        }
        return -1;
    }
}
