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

import org.eclipse.imp.services.base.LanguageSyntaxPropertiesBase;

public class LPGSyntaxProperties extends LanguageSyntaxPropertiesBase {
    public String getBlockCommentStart() {
        return null;
    }

    public String getBlockCommentContinuation() {
        return null;
    }

    public String getBlockCommentEnd() {
        return null;
    }

    public String getSingleLineCommentPrefix() {
        return "--";
    }

    public String[][] getFences() {
        return null;
    }
}
