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

package org.eclipse.imp.lpg.navigation;

import java.util.Arrays;
import java.util.List;

import lpg.runtime.IAst;

import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.AbstractVisitor;
import org.eclipse.imp.lpg.parser.LPGParser.DefineSeg;
import org.eclipse.imp.lpg.parser.LPGParser.GlobalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.HeadersSeg;
import org.eclipse.imp.lpg.parser.LPGParser.RecoverSeg;
import org.eclipse.imp.lpg.parser.LPGParser.StartSeg;
import org.eclipse.imp.lpg.parser.LPGParser.TerminalsSeg;
import org.eclipse.imp.lpg.parser.LPGParser.defineSpec;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.rule;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.services.INavigationTargetFinder;

public class NavigationTargetFinder implements INavigationTargetFinder {
    private List<Class<? extends ASTNode>> RELEVANT_CLASSES= Arrays.asList(rule.class, option_spec.class, TerminalsSeg.class, terminal.class, HeadersSeg.class, DefineSeg.class, defineSpec.class, GlobalsSeg.class, RecoverSeg.class, StartSeg.class, nonTerm.class);

    private boolean nodeIsRelevant(IAst node) {
        for(Class<?> clazz: RELEVANT_CLASSES) {
            if (clazz.isAssignableFrom(node.getClass())) {
                return true;
            }
        }
        return false;
    }

    public Object getNextTarget(Object cur, Object root) {
        IAst curNode= (IAst) cur;
        IAst rootNode= (IAst) root;
        final IAst[] result= new IAst[1];
        final int curNodeOffset= curNode.getLeftIToken().getStartOffset();

        rootNode.accept(new AbstractVisitor() {
            @Override
            public boolean preVisit(IAst element) {
                int thisOffset= element.getLeftIToken().getStartOffset();

                // This relies on the fact that sibling AST nodes are visited in the order
                // that they appear in the source code, so that the first node encountered
                // that satisfies the condition below is the immediately following node of
                // one of the appropriate types.
                if (result[0] == null && nodeIsRelevant(element) && thisOffset > curNodeOffset) {
                    result[0]= element;
                }
                return true;
            }

            @Override
            public void postVisit(IAst element) { }

            @Override
            public void unimplementedVisitor(String s) { }
        });
        return result[0];
    }

    public Object getPreviousTarget(Object cur, Object root) {
        IAst curNode= (IAst) cur;
        IAst rootNode= (IAst) root;
        final IAst[] result= new IAst[1];
        final int curNodeOffset= curNode.getLeftIToken().getStartOffset();

        rootNode.accept(new AbstractVisitor() {
            @Override
            public boolean preVisit(IAst element) {
                int thisOffset= element.getLeftIToken().getStartOffset();

                // This relies on the fact that sibling AST nodes are visited in the order
                // that they appear in the source code, so that the last node encountered
                // that satisfies the condition below is the immediately preceding node of
                // one of the appropriate types.
                if (nodeIsRelevant(element) && thisOffset < curNodeOffset) {
                    result[0]= element;
                }
                return true;
            }

            @Override
            public void postVisit(IAst element) { }

            @Override
            public void unimplementedVisitor(String s) { }
        });
        return result[0];
    }

    public Object getEnclosingConstruct(Object cur, Object root) {
        IAst curNode= (IAst) cur;
        int curNodeOffset= curNode.getLeftIToken().getStartOffset();
        int curNodeEnd= curNode.getRightIToken().getEndOffset();
        IAst parent= curNode.getParent();

        while (parent != null && parent.getLeftIToken().getStartOffset() == curNodeOffset && parent.getRightIToken().getEndOffset() == curNodeEnd) {
            parent= parent.getParent();
        }
        return parent;
    }
}
