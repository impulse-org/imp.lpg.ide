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

package org.eclipse.imp.lpg.parser;

import org.eclipse.imp.lpg.parser.LPGParser.action_segment;
import org.eclipse.imp.lpg.parser.LPGParser.rule;

class JavaActionBlockAutomaticVisitor extends JavaActionBlockVisitor 
{
    public boolean visit(rule n) {
        action_segment a = n.getopt_action_segment();
        if (a != null)
            parseClassBodyDeclarationsopt(a);
        return false;
    }
}
