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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lpg.runtime.IAst;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.AbstractVisitor;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.ILPG_item;
import org.eclipse.imp.lpg.parser.LPGParser.Imacro_name_symbol;
import org.eclipse.imp.lpg.parser.LPGParser.IncludeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.Ioption_value;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.LPG_itemList;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_SYMBOL;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__SYMBOL_optAttrList;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.preferences.LPGConstants;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.SymbolTable;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;

public class ASTUtils {
    private ASTUtils() {}

    public static LPG getRoot(IAst node) {
        while (node != null && !(node instanceof LPG))
            node= node.getParent();
        return (LPG) node;
    }

    public static List<Imacro_name_symbol> getMacros(LPG root) {
        SymbolTable<ASTNode> st= root.symbolTable;

        // DO NOT pick up macros from any imported file! They shouldn't be
        // treated as defined in this scope!
        return st.allDefsOfType(Imacro_name_symbol.class);
    }

    public static List<nonTerm> getNonTerminals(LPG root) {
        SymbolTable<ASTNode> st= root.symbolTable;

        // TODO: pick up non-terminals from imported files
        return st.allDefsOfType(nonTerm.class);
    }

    public static List<terminal> getTerminals(LPG root) {
        SymbolTable<ASTNode> st= root.symbolTable;

        // TODO: pick up terminals from imported files???
        return st.allDefsOfType(terminal.class);
    }

    public static List<ASTNode> findItemOfType(LPG root, Class ofType) {
        LPG_itemList itemList= root.getLPG_INPUT();
        List<ASTNode> result= new ArrayList<ASTNode>();

        for(int i= 0; i < itemList.size(); i++) {
            ILPG_item item= itemList.getLPG_itemAt(i);

            if (ofType.isInstance(item))
                result.add((ASTNode) item);
        }
        return result;
    }

    public static String stripName(String rawId) {
        int idx= rawId.indexOf('$');

        return (idx >= 0) ? rawId.substring(0, idx) : rawId;
    }

    protected static List<String> collectIncludedFiles(LPG root, ICompilationUnit refUnit) {
        List<String> result= new ArrayList<String>();
        option_specList optSeg= root.getoptions_segment();

        for(int i= 0; i < optSeg.size(); i++) {
            option_spec optSpec= optSeg.getoption_specAt(i);
            optionList optList= optSpec.getoption_list();
            for(int o= 0; o < optList.size(); o++) {
                option opt= optList.getoptionAt(o);
                IASTNodeToken sym= opt.getSYMBOL();
                String optName= sym.toString();

                if (optName.equals("import_terminals")
                        || optName.equals("template")
                        || optName.equals("filter")) {
                    Ioption_value optValue= opt.getoption_value();
                    if (optValue instanceof option_value__EQUAL_SYMBOL) {
                        String fileName= ((option_value__EQUAL_SYMBOL) optValue).getSYMBOL().toString();
                        result.add(fileName);
                        if (optName.equals("import_terminals")) {
                            // pick up defs from the filter
                            IPath filterPath= refUnit.getPath().removeLastSegments(1).append(fileName);
                            ICompilationUnit filterUnit= ModelFactory.open(filterPath, refUnit.getProject());
                            LPG filterRoot= (LPG) findAndParseSourceFile(refUnit.getProject(),
                                    filterPath, fileName, new NullProgressMonitor());

                            if (filterRoot != null) {
                                List<String> level2Incs= collectIncludedFiles(filterRoot, filterUnit);
                                result.addAll(level2Incs);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Object findDefOf(IASTNodeToken s, LPG root,
            ICompilationUnit refUnit, IProgressMonitor monitor) {
        String id= stripName(s.toString());
        List<String> includedFiles= collectIncludedFiles(root, refUnit);
        for(String fileName : includedFiles) {
            LPG includedRoot= (LPG) findAndParseSourceFile(refUnit.getProject(),
                    refUnit.getPath(), fileName, monitor);

            if (includedRoot != null) {
                ASTNode decl= includedRoot.symbolTable.get(id);

                if (decl != null)
                    return decl;
            }
        }
        return null;
    }

    public static Object findDefOf(IASTNodeToken s, LPG root,
            IParseController parseController) {
        // This would use the auto-generated bindings if they were implemented
        // already...
        String id= stripName(s.toString());
        ASTNode decl= root.symbolTable.get(id);

        if (decl == null || ((ASTNodeToken) s).parent == decl) { // just found the same spot;
            // try a little harder
            Object def= findDefOf(s, root, ModelFactory.open(parseController
                    .getPath(), parseController.getProject()),
                    new NullProgressMonitor());

            if (def != null)
                return def;
        }

        if (decl == null) {
            ASTNode node= (ASTNode) s;
            ASTNode parent= (ASTNode) node.getParent();
            ASTNode grandParent= (ASTNode) parent.getParent();

            if (grandParent instanceof option) {
                option opt= (option) grandParent;
                String optName= opt.getSYMBOL().toString();

                if (optName.equals("import_terminals")
                        || optName.equals("template")
                        || optName.equals("filter")) {
                    return lookupImportedFile(parseController.getProject(),
                            parseController.getPath(), id);
                }
            } else if (parent instanceof IncludeSeg) {
                IncludeSeg iseg= (IncludeSeg) parent;
                String includeFile= iseg.getinclude_segment().getSYMBOL()
                        .toString();

                return lookupImportedFile(parseController.getProject(),
                        parseController.getPath(), includeFile);
            }
        }
        return decl;
    }

    public static ICompilationUnit lookupImportedFile(
            ISourceProject srcProject, IPath referencingFile, String fileName) {
        IPath refPath= referencingFile.removeLastSegments(1);
        IProject project= srcProject.getRawProject();

        // First look for the referenced file in the same folder
        if (project.getFile(refPath.append(fileName)).exists())
            return ModelFactory.open(refPath.append(fileName), srcProject);
        // Next see whether 'fileName' is actually a project-relative path
        if (project.getFile(fileName).exists())
            return ModelFactory.open(new Path(fileName), srcProject);

        final List<IPathEntry> buildPath= srcProject.getBuildPath();

        for(IPathEntry entry : buildPath) {
            final IPath candidatePath= project.getLocation().append(
                    entry.getPath()).append(fileName);
            if (project.getFile(candidatePath).exists()) {
                return ModelFactory.open(candidatePath, srcProject);
            }
        }
        IPreferencesService prefService= new PreferencesService(project,
                LPGRuntimePlugin.getInstance().getLanguageID());
        String includeSearchPath= prefService
                .getStringPreference(LPGConstants.P_INCLUDEPATHTOUSE);
        String[] includeDirs= includeSearchPath.split(";");

        for(int i= 0; i < includeDirs.length; i++) {
            IPath includeDirPath= new Path(includeDirs[i]);
            IPath includeFile= includeDirPath.append(fileName);

            // TODO Let the ModelFactory try to open the file (encapsulating the EFS-based code and the check for existence)
            if (new File(includeFile.toOSString()).exists())
                return ModelFactory.open(includeFile, srcProject);
        }
        return null;
    }

    public static ICompilationUnit lookupSourceFile(ISourceProject project,
            IPath refLocation, String filePath) {
        // Can an ICompilationUnit refer to non-existent cu???

        // First try to find the file relative to the referencing location
        ICompilationUnit icu= ModelFactory.open(project.getRawProject()
                .getFile(
                        refLocation.removeFirstSegments(1)
                                .removeLastSegments(1).append(filePath)),
                project);

        if (icu == null)
            icu= ModelFactory.open(new Path(filePath), project);

        return icu;
    }

    public static Object findAndParseSourceFile(ISourceProject project,
            IPath refLocation, String fileName, IProgressMonitor monitor) {
        ICompilationUnit unit= lookupSourceFile(project, refLocation, fileName);

        if (unit != null)
            return unit.getAST(null, monitor);
        return null;
    }

    public static List<ASTNode> findRefsOf(final nonTerm nonTerm) {
        final List<ASTNode> result= new ArrayList<ASTNode>();
        LPG root= getRoot(nonTerm);
        List<nonTerm> nonTerms= getNonTerminals(root);

        // Indexed search would be nice here...
        for(int i= 0; i < nonTerms.size(); i++) {
            nonTerm nt= nonTerms.get(i);
            final String nonTermName= nonTerm.getruleNameWithAttributes()
                    .getSYMBOL().toString();

            nt.accept(new AbstractVisitor() {
                public void unimplementedVisitor(String s) {}

                public boolean visit(symWithAttrs__SYMBOL_optAttrList n) {
                    if (n.getSYMBOL().toString().equals(nonTermName))
                        result.add(n);
                    return super.visit(n);
                }
                // public boolean visit(symWithAttrs2 n) {
                // if (n.getSYMBOL().toString().equals(nonTermName))
                // result.add(n);
                // return super.visit(n);
                // }
            });
        }
        return result;
    }

    /**
     * Finds all of the occurrences of a terminal symbol among the nonterminals
     * in the grammar rules.
     * 
     * @param term
     *            The terminal symbol
     * @return A list of the AST nodes that represent matches of the given
     *         terminal node
     */
    public static List<ASTNode> findRefsOf(final terminal term) {
        final List<ASTNode> result= new ArrayList<ASTNode>();
        LPG root= getRoot(term);
        List<nonTerm> nonTerms= getNonTerminals(root);

        // Indexed search would be nice here...
        for(int i= 0; i < nonTerms.size(); i++) {
            nonTerm nt= nonTerms.get(i);
            final String symbolName= term.toString();

            nt.accept(new AbstractVisitor() {
                public void unimplementedVisitor(String s) {}

                public boolean visit(symWithAttrs__SYMBOL_optAttrList n) {
                    if (n.getSYMBOL().toString().equals(symbolName))
                        result.add(n);
                    return super.visit(n);
                }
            });
        }
        return result;
    }

}
