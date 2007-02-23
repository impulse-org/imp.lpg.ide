package org.eclipse.safari.jikespg.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.safari.jikespg.editor.HoverHelper;
import org.eclipse.safari.jikespg.parser.JikesPGParser.*;

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

    public static ASTNode findDefOf(IASTNodeToken s, JikesPG root) {
        // This would use the auto-generated bindings if they were implemented already...
        String id= HoverHelper.stripName(s.toString());

        return root.symbolTable.lookup(id);
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
