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
import java.util.Collections;
import java.util.List;

import lpg.runtime.IToken;

import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.parser.LPGParser.terminal_symbol0;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.services.IOccurrenceMarker;

public class LPGOccurrenceMarker implements IOccurrenceMarker {

    public LPGOccurrenceMarker() {
    }

    public String getKindName() {
        return "References";
    }

    public List<Object> getOccurrencesOf(IParseController parseController,
            Object entity) {
        ASTNode node= (ASTNode) entity;
        LPG root= (LPG) parseController.getCurrentAst();
        List<Object> result;

        System.out.println();

        // Handles (I think) symbols in the terminals section
        // (but not in the rules)
        if (node instanceof terminal_symbol0) {
            result= new ArrayList<Object>();
            result.add(node);
            String nodeString= node.toString();
            if (parseController instanceof SimpleLPGParseController) {
                SimpleLPGParseController lpgParseController= (SimpleLPGParseController) parseController;
                ArrayList<IToken> tokens= lpgParseController.getParser().getIPrsStream().getTokens();
                for(IToken t : tokens) {
                    if (t.toString().equals(nodeString)) {
                        result.add(t);
                    }
                }
            }
            return result;
        }

        // SMS 30 Jul 2008: Note: it seems that everything that's in a rule
        // ends up being represented as an AST node token, even if it's
        // something that corresponds to a symbol
        if (node instanceof IASTNodeToken) {
            IASTNodeToken nTok= (IASTNodeToken) node;
            // result= Collections.singletonList(ASTUtils.findDefOf(nTok, root,
            // parseController));

            Object def= ASTUtils.findDefOf(nTok, root, parseController);
            if (def == null) {
                result= Collections.singletonList((Object) node);
            } else if (def instanceof nonTerm) {
                result= new ArrayList<Object>();
                result.add(def);
                result.addAll(ASTUtils.findRefsOf((nonTerm) def));
            } else if (def instanceof terminal) {
                result= new ArrayList<Object>();
                result.add(def);
                result.addAll(findAllOccurrences((terminal) def));
            } else {
                result= new ArrayList<Object>();
                result.add(node);
                result.add(def);
            }
            return result;
        }

        result= Collections.emptyList();
        // System.out.println("LPG occurrences = " + result);
        return result;
    }

    public List<Object> findAllOccurrences(terminal def) {
        List<ASTNode> refs= ASTUtils.findRefsOf(def);
        List<Object> result= new ArrayList<Object>();
        result.addAll(refs);
        return result;
    }

}
