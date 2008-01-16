/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
package org.eclipse.imp.lpg.compare;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.IEditableContent;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.IResourceProvider;
import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.IStructureCreator;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.lpg.parser.ParseController;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class StructureMergeViewerCreator implements IViewerCreator {
    private static class JikesPGStructureCreator implements IStructureCreator {
        private final CompareConfiguration fConfig;

        public JikesPGStructureCreator(CompareConfiguration config) {
            fConfig= config;
        }

        public String getName() {
            return "JikesPG Grammar Structure Compare";
        }

        public IStructureComparator getStructure(Object input) {
            try {
                IResource res= null;
                IDocument doc= CompareUI.getDocument(input);
                IStreamContentAccessor sca;

                if (input instanceof IResourceProvider) {
                    res= ((IResourceProvider) input).getResource();
                } else
                    return null;

                if (input instanceof IStreamContentAccessor) {
                    sca= (IStreamContentAccessor) input;
                } else
                    return null;

                if (doc == null) { // Set up a document
                    String contents= StreamUtils.readStreamContents(sca);
                    char[] buffer= null;
                        
                    if (contents != null) {
                        int n= contents.length();
                        buffer= new char[n];
                        contents.getChars(0, n, buffer, 0);
                                
                        doc= new Document(contents);
                        CompareUI.registerDocument(input, doc);
                    }
                }

                return new LPGStructureNode(parseStream(sca, res.getFullPath()), doc, 0, "root");
            } catch (IOException io) {
            } catch (CoreException ce) {
            }
            return null;
        }

        private ASTNode parseStream(IStreamContentAccessor sca, IPath path) throws IOException, CoreException {
            String contents= StreamUtils.readStreamContents(sca);

            return parseContents(contents, path);
        }

        private ASTNode parseContents(String contents, IPath path) {
        	IParseController parser = new ParseController();
            return (ASTNode) parser.parse(contents, false, new NullProgressMonitor());
        }

        public IStructureComparator locate(Object path, Object input) {
            LPGStructureNode inputAST= (LPGStructureNode) input;

            // TODO Figure out when/why this is called and implement :-)
            return null;
        }

        public String getContents(Object node, boolean ignoreWhitespace) {
            LPGStructureNode wrapper= (LPGStructureNode) node;
            ASTNode astNode= wrapper.getASTNode();

            return (astNode != null) ? astNode.toString() : "";
        }

        public void save(IStructureComparator node, Object input) {
            if (node instanceof LPGStructureNode && input instanceof IEditableContent) {
                IDocument document= ((LPGStructureNode) node).getDocument();
                IEditableContent bca= (IEditableContent) input;
                String contents= document.get();
                String encoding= null;

                if (input instanceof IEncodedStreamContentAccessor) {
                    try {
                        encoding= ((IEncodedStreamContentAccessor)input).getCharset();
                    } catch (CoreException e1) {
                        // ignore
                    }
                }
                if (encoding == null)
                    encoding= ResourcesPlugin.getEncoding();
                byte[] bytes;                           
                try {
                    bytes= contents.getBytes(encoding);
                } catch (UnsupportedEncodingException e) {
                    bytes= contents.getBytes();     
                }
                bca.setContent(bytes);
            }
        }
    }

    private static final class JikesPGStructureDiffViewer extends StructureDiffViewer {
        private IStructureCreator fStructureCreator;

        private JikesPGStructureDiffViewer(Composite parent, CompareConfiguration config) {
            super(parent, config);
            fStructureCreator= new JikesPGStructureCreator(config);
            setStructureCreator(fStructureCreator);
        }
    }

    public Viewer createViewer(final Composite parent, final CompareConfiguration config) {
        return new JikesPGStructureDiffViewer(parent, config);
    }
}
