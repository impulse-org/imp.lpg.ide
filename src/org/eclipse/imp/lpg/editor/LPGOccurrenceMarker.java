/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.IOccurrenceMarker;

public class LPGOccurrenceMarker implements IOccurrenceMarker {

    public LPGOccurrenceMarker() {
    }

    public String getKindName() {
	return "References";
    }

    public List<Object> getOccurrencesOf(IParseController parseController, Object entity) {
	ASTNode node= (ASTNode) entity;
	JikesPG root= (JikesPG) parseController.getCurrentAst();
	List<Object> result;

	System.out.println();
	if (node instanceof IASTNodeToken) {
	    IASTNodeToken nTok= (IASTNodeToken) node;
	    result= Collections.singletonList(ASTUtils.findDefOf(nTok, root, parseController));
	} else {
	    result= Collections.emptyList();
	}
	System.out.println("LPG occurrences = " + result);
	return result;
    }
}
