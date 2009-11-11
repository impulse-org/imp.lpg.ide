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
import org.eclipse.imp.services.base.LPGFolderBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

public class FoldingUpdater extends LPGFolderBase {

    private class FoldingVisitor extends AbstractVisitor {
        public void unimplementedVisitor(String s) { }

        public boolean visit(option_specList n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(AliasSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(DefineSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(ExportSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(GlobalsSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(HeadersSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(IdentifierSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(ImportSeg n) {
            makeFoldable(n);
            return true;
        }

        public boolean visit(drop_command0 n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(drop_command1 n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(IncludeSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(KeywordsSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(NoticeSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(RulesSeg n) {
            makeFoldable(n);
            return true;
        }

        @Override
        public boolean visit(StartSeg n) {
            makeFoldable(n);
            return true;
        }

        public boolean visit(rule n) {
            final action_segment optAction= n.getopt_action_segment();

            if (optAction != null) {
                // Make the action block and any surrounding whitespace foldable.
                final ILexStream lexStream= optAction.getIToken().getIPrsStream().getILexStream();
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
            makeFoldable(n);
            return false;
        }

        @Override
        public boolean visit(TrailersSeg n) {
            makeFoldable(n);
            return false;
        }

        public boolean visit(TypesSeg n) {
            makeFoldable(n);
            return false;
        }
    };

    public void sendVisitorToAST(HashMap<Annotation,Position> newAnnotations, List<Annotation> annotations, Object ast) {
        ASTNode theAST= (ASTNode) ast;
        prsStream= theAST.getLeftIToken().getIPrsStream();
        FoldingVisitor foldingVisitor= new FoldingVisitor();
        theAST.accept(foldingVisitor);
    }
}
