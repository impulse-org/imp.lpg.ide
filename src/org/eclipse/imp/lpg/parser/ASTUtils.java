/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
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
import org.eclipse.imp.lpg.parser.LPGParser.IJikesPG_item;
import org.eclipse.imp.lpg.parser.LPGParser.Imacro_name_symbol;
import org.eclipse.imp.lpg.parser.LPGParser.IncludeSeg;
import org.eclipse.imp.lpg.parser.LPGParser.Ioption_value;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG_itemList;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value0;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs1;
import org.eclipse.imp.lpg.parser.LPGParser.terminal;
import org.eclipse.imp.lpg.preferences.LPGPreferencesDialogConstants;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.jface.preference.IPreferenceStore;

public class ASTUtils {
    private ASTUtils() { }

    public static JikesPG getRoot(IAst node) {
	while (node != null && !(node instanceof JikesPG))
	    node = node.getParent();
	return (JikesPG) node;
    }

    public static List<Imacro_name_symbol> getMacros(JikesPG root) {
	LPGParser.SymbolTable st= root.symbolTable;

        // DO NOT pick up macros from any imported file! They shouldn't be treated as defined in this scope!
        return st.allDefsOfType(Imacro_name_symbol.class);
    }

    public static List<nonTerm> getNonTerminals(JikesPG root) {
	LPGParser.SymbolTable st= root.symbolTable;

        // TODO: pick up non-terminals from imported files
        return st.allDefsOfType(nonTerm.class);
    }

    public static List<terminal> getTerminals(JikesPG root) {
	LPGParser.SymbolTable st= root.symbolTable;

        // TODO: pick up terminals from imported files???
        return st.allDefsOfType(terminal.class);
    }

    public static List<ASTNode> findItemOfType(JikesPG root, Class ofType) {
        JikesPG_itemList itemList= root.getJikesPG_INPUT();
        List<ASTNode> result= new ArrayList<ASTNode>();

        for(int i=0; i < itemList.size(); i++) {
            IJikesPG_item item= itemList.getJikesPG_itemAt(i);

            if (ofType.isInstance(item))
                result.add((ASTNode) item);
        }
        return result;
    }

    public static String stripName(String rawId) {
	int idx= rawId.indexOf('$');

	return (idx >= 0) ? rawId.substring(0, idx) : rawId;
    }

    protected static List<String> collectIncludedFiles(JikesPG root) {
	List<String> result= new ArrayList<String>();
	option_specList optSeg= root.getoptions_segment();

	for(int i=0; i < optSeg.size(); i++) {
	    option_spec optSpec= optSeg.getoption_specAt(i);
	    optionList optList= optSpec.getoption_list();
	    for(int o= 0; o < optList.size(); o++) {
		option opt= optList.getoptionAt(o);
		IASTNodeToken sym= opt.getSYMBOL();
		String optName= sym.toString();

		if (optName.equals("import_terminals") || optName.equals("template") || optName.equals("filter")) {
		    Ioption_value optValue= opt.getoption_value();
		    if (optValue instanceof option_value0) {
			result.add(((option_value0) optValue).getSYMBOL().toString());
		    }
		}
	    }
	}
	return result;
    }

    public static Object findDefOf(IASTNodeToken s, JikesPG root, ICompilationUnit refUnit, IProgressMonitor monitor) {
        String id= stripName(s.toString());
	List<String> includedFiles= collectIncludedFiles(root);
	for(String fileName : includedFiles) {
	    JikesPG includedRoot= (JikesPG) findAndParseSourceFile(refUnit.getProject(), refUnit.getPath(), fileName, monitor);

	    if (includedRoot != null) {
		ASTNode decl= includedRoot.symbolTable.lookup(id);

		if (decl != null)
		    return decl;
	    }
	}
	return null;
    }

    public static Object findDefOf(IASTNodeToken s, JikesPG root, IParseController parseController) {
        // This would use the auto-generated bindings if they were implemented already...
        String id= stripName(s.toString());
        ASTNode decl= root.symbolTable.lookup(id);

        if (((ASTNodeToken) s).parent == decl) { // just found the same spot; try a little harder
            Object def= findDefOf(s, root, ModelFactory.open(parseController.getPath(), parseController.getProject()), new NullProgressMonitor());

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

        	if (optName.equals("import_terminals") || optName.equals("template") || optName.equals("filter")) {
        	    return lookupImportedFile(parseController.getProject(), parseController.getPath(), id);
        	}
            } else if (parent instanceof IncludeSeg) {
		IncludeSeg iseg= (IncludeSeg) parent;
		String includeFile= iseg.getinclude_segment().getSYMBOL().toString();

		return lookupImportedFile(parseController.getProject(), parseController.getPath(), includeFile);
            }
        }
        return decl;
    }

    public static ICompilationUnit lookupImportedFile(ISourceProject srcProject, IPath refFile, String fileName) {
	IPath refPath= refFile.removeLastSegments(1);
	IProject project= srcProject.getRawProject();

	if (project.getFile(refPath.append(fileName)).exists())
	    return ModelFactory.open(refPath.append(fileName), srcProject);
	if (project.getFile(fileName).exists())
	    return ModelFactory.open(new Path(fileName), srcProject);

	final List<IPathEntry> buildPath= srcProject.getBuildPath();

	for(IPathEntry entry : buildPath) {
	    final IPath candidatePath= project.getLocation().append(entry.getPath()).append(fileName);
	    if (project.getFile(candidatePath).exists()) {
		return ModelFactory.open(candidatePath, srcProject);
	    }
	}
	IPreferencesService prefService= new PreferencesService(project, LPGRuntimePlugin.getLanguageID());
	String includeSearchPath= prefService.getStringPreference(LPGPreferencesDialogConstants.P_INCLUDEPATHTOUSE);
	String[] includeDirs= includeSearchPath.split(";");

	for(int i= 0; i < includeDirs.length; i++) {
	    IPath includeDirPath= new Path(includeDirs[i]);
	    IPath includeFile= includeDirPath.append(fileName);

	    if (new File(includeFile.toOSString()).exists())
		return ModelFactory.open(includeFile, srcProject);
	}
	return null;
    }

    public static ICompilationUnit lookupSourceFile(ISourceProject project, IPath refLocation, String filePath) {
	// Can an ICompilationUnit refer to non-existent cu???

	// First try to find the file relative to the referencing location
	ICompilationUnit icu= ModelFactory.open(project.getRawProject().getFile(refLocation.removeFirstSegments(1).removeLastSegments(1).append(filePath)), project);

	if (icu == null)
	    icu= ModelFactory.open(new Path(filePath), project);

	return icu;
    }

    public static Object findAndParseSourceFile(ISourceProject project, IPath refLocation, String fileName, IProgressMonitor monitor) {
	ICompilationUnit unit= lookupSourceFile(project, refLocation, fileName);

	if (unit != null)
	    return unit.getAST(null, monitor);
	return null;
    }

    public static List<ASTNode> findRefsOf(final nonTerm nonTerm) {
	final List<ASTNode> result= new ArrayList<ASTNode>();
	JikesPG root= getRoot(nonTerm);
	List<nonTerm> nonTerms= getNonTerminals(root);

	// Indexed search would be nice here...
	for(int i=0; i < nonTerms.size(); i++) {
	    nonTerm nt= nonTerms.get(i);
	    final String nonTermName= nonTerm.getruleNameWithAttributes().getSYMBOL().toString();

	    nt.accept(new AbstractVisitor() {
		public void unimplementedVisitor(String s) { }
		public boolean visit(symWithAttrs1 n) {
		    if (n.getSYMBOL().toString().equals(nonTermName))
			result.add(n);
		    return super.visit(n);
		}
//		public boolean visit(symWithAttrs2 n) {
//		    if (n.getSYMBOL().toString().equals(nonTermName))
//			result.add(n);
//		    return super.visit(n);
//		}
	    });
	}
	return result;
    }
}
