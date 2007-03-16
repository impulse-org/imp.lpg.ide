package org.eclipse.safari.jikespg.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.safari.jikespg.JikesPGRuntimePlugin;
import org.eclipse.safari.jikespg.parser.JikesPGParser.*;
import org.eclipse.safari.jikespg.preferences.PreferenceConstants;
import org.eclipse.uide.model.CompilationUnitRef;
import org.eclipse.uide.model.ICompilationUnit;
import org.eclipse.uide.parser.IParseController;

import lpg.runtime.IAst;

public class ASTUtils {
    private ASTUtils() { }

    public static JikesPG getRoot(IAst node) {
	while (node != null && !(node instanceof JikesPG))
	    node = node.getParent();
	return (JikesPG) node;
    }

    public static List<Imacro_name_symbol> getMacros(JikesPG root) {
	JikesPGParser.SymbolTable st= root.symbolTable;

        // DO NOT pick up macros from any imported file! They shouldn't be treated as defined in this scope!
        return st.allDefsOfType(Imacro_name_symbol.class);
    }

    public static List<nonTerm> getNonTerminals(JikesPG root) {
	JikesPGParser.SymbolTable st= root.symbolTable;

        // TODO: pick up non-terminals from imported files
        return st.allDefsOfType(nonTerm.class);
    }

    public static List<terminal> getTerminals(JikesPG root) {
	JikesPGParser.SymbolTable st= root.symbolTable;

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

    public static Object findDefOf(IASTNodeToken s, JikesPG root, IParseController parseController) {
        // This would use the auto-generated bindings if they were implemented already...
        String id= stripName(s.toString());
        ASTNode decl= root.symbolTable.lookup(id);

        if (decl == null) {
            ASTNode node= (ASTNode) s;
            ASTNode parent= (ASTNode) node.getParent();
            ASTNode grandParent= (ASTNode) parent.getParent();

            if (grandParent instanceof option) {
        	option opt= (option) grandParent;
        	String optName= opt.getSYMBOL().toString();
        	if (optName.equals("import_terminals") || optName.equals("template") || optName.equals("filter")) {
        	    return lookupImportedFile(parseController, id);
        	}
            } else if (parent instanceof IncludeSeg) {
		IncludeSeg iseg= (IncludeSeg) parent;
		String includeFile= iseg.getinclude_segment().getSYMBOL().toString();

		return lookupImportedFile(parseController, includeFile);
            }
        }
        return decl;
    }

    public static ICompilationUnit lookupImportedFile(IParseController parseController, String fileName) {
	IPath srcPath= parseController.getPath().removeLastSegments(1); // location of referencing file
	if (parseController.getProject().getFile(srcPath.append(fileName)).exists())
	    return new CompilationUnitRef(parseController.getProject(), srcPath.append(fileName));
	if (parseController.getProject().getFile(fileName).exists())
	    return new CompilationUnitRef(parseController.getProject(), new Path(fileName));
	IPreferenceStore store= JikesPGRuntimePlugin.getInstance().getPreferenceStore();
	IPath includeDir = new Path(store.getString(PreferenceConstants.P_JIKESPG_INCLUDE_DIRS));

	return new CompilationUnitRef(parseController.getProject(), includeDir.append(fileName));
    }

    public static ICompilationUnit lookupSourceFile(IParseController parseController, String filePath) {
	return new CompilationUnitRef(parseController.getProject(), new Path(filePath));
    }

    public static Object findAndParseSourceFile(IParseController parseController, String fileName, IProgressMonitor monitor) {
	ICompilationUnit unit= lookupSourceFile(parseController, fileName);

	return unit.getAST(null, monitor);
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
