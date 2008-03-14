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
