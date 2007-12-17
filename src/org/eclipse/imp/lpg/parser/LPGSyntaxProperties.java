package org.eclipse.imp.lpg.parser;

import org.eclipse.imp.language.ILanguageSyntaxProperties;

public class LPGSyntaxProperties implements ILanguageSyntaxProperties {

    public String getBlockCommentStart() {
        return null;
    }

    public String getBlockCommentContinuation() {
        return "*";
    }

    public String getBlockCommentEnd() {
        return null;
    }

    public String getSingleLineCommentPrefix() {
        return "--";
    }

    public String[][] getFences() {
        // TODO Auto-generated method stub
        return null;
    }

    public int[] getIdentifierComponents(String ident) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getIdentifierConstituentChars() {
        // TODO Auto-generated method stub
        return null;
    }
}
