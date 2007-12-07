package org.eclipse.imp.lpg.parser;

import org.eclipse.imp.lpg.parser.LPGParser.action_segment;
import org.eclipse.imp.lpg.parser.LPGParser.rule;

class JavaActionBlockUserDefinedVisitor extends JavaActionBlockVisitor 
{
    public boolean visit(rule n) {
        action_segment a = n.getopt_action_segment();
        if (a != null)
            parseLPGUserAction(a);
        return false;
    }
}
