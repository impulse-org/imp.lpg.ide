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

import java.util.HashMap;
import java.util.List;

import lpg.runtime.ILexStream;

import org.eclipse.imp.lpg.parser.LPGParser.*;
import org.eclipse.imp.services.base.FolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

public class FoldingUpdater extends FolderBase {

    private class FoldingVisitor extends AbstractVisitor {
        public void unimplementedVisitor(String s) { }

        // TODO Make an LPG-specific extension of FolderBase that knows about IAst's and IToken's, and hoist this code up there.
        private void makeAnnotation(ASTNode n) {
            int start= n.getLeftIToken().getStartOffset();
            int len= n.getRightIToken().getEndOffset() - start + 1;

            // Following is necessary if one edits an empty grammar file; there's an
            // options segment with an empty textual extent, which causes Position()
            // a heartache.
            if (len <= 0)
                return;

            ILexStream ls= n.getLeftIToken().getPrsStream().getLexStream();

            while (start + len < ls.getStreamLength() && (ls.getCharValue(start+len) == ' ' || ls.getCharValue(start+len) == '\t')) {
                len++;
            }
            // For some reason, simply testing against Character.LINE_SEPARATOR here doesn't work.
            if (start + len < ls.getStreamLength() && (ls.getCharValue(start+len) == '\n' || ls.getCharValue(start+len) == '\r'))
                len++;
            FoldingUpdater.this.makeAnnotation(start, len);
        }

        public boolean visit(option_specList n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(AliasSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(DefineSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(ExportSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(GlobalsSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(HeadersSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(IdentifierSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(ImportSeg n) {
            makeAnnotation(n);
            return true;
        }

        public boolean visit(drop_command0 n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(drop_command1 n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(IncludeSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(KeywordsSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(NoticeSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(RulesSeg n) {
            makeAnnotation(n);
            return true;
        }

        public boolean visit(rule n) {
            final action_segment optAction= n.getopt_action_segment();

            if (optAction != null) {
                // Make the action block and any surrounding whitespace foldable.
                final ILexStream lexStream= optAction.getIToken().getPrsStream().getLexStream();
                int start= optAction.getLeftIToken().getStartOffset();
                int len= optAction.getRightIToken().getEndOffset() - start + 3;

                while (Character.isWhitespace(lexStream.getCharValue(start - 1))) {
                    start--;
                    len++;
                }
                while (Character.isWhitespace(lexStream.getCharValue(start + len - 1)))
                    len++;
                len--;
                FoldingUpdater.this.makeAnnotation(start, len);
            }
            return false;
        }

        public boolean visit(TerminalsSeg n) {
            makeAnnotation(n);
            return false;
        }

        public boolean visit(TypesSeg n) {
            makeAnnotation(n);
            return false;
        }
    };

    public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, List<Annotation> annotations, Object ast) {
        ASTNode theAST= (ASTNode) ast;
        FoldingVisitor foldingVisitor= new FoldingVisitor();
        theAST.accept(foldingVisitor);
    }
}
