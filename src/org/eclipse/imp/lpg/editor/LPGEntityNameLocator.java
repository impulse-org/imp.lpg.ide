package org.eclipse.imp.lpg.editor;

import org.eclipse.imp.lpg.parser.LPGParser.AliasSeg;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.services.IEntityNameLocator;

public class LPGEntityNameLocator implements IEntityNameLocator {
    public LPGEntityNameLocator() { }

    public Object getName(Object srcEntity) {
        if (srcEntity instanceof AliasSeg) {
            AliasSeg seg= (AliasSeg) srcEntity;
            return seg.getLeftIToken();
        }
        if (srcEntity instanceof nonTerm) {
            nonTerm nt= (nonTerm) srcEntity;
            return nt.getruleNameWithAttributes().getSYMBOL();
        }
        return null;
    }
}
