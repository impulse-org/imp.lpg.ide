package org.eclipse.imp.lpg.parser;

import lpg.runtime.*;
import org.eclipse.imp.parser.IParser;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.HashMap;
    import java.util.Set;
 
public class LPGParser implements RuleAction, IParser
{
    private PrsStream prsStream = null;
    
    private boolean unimplementedSymbolsWarning = false;

    private static ParseTable prsTable = new LPGParserprs();
    public ParseTable getParseTable() { return prsTable; }

    private DeterministicParser dtParser = null;
    public DeterministicParser getParser() { return dtParser; }

    private void setResult(Object object) { dtParser.setSym1(object); }
    public Object getRhsSym(int i) { return dtParser.getSym(i); }

    public int getRhsTokenIndex(int i) { return dtParser.getToken(i); }
    public IToken getRhsIToken(int i) { return prsStream.getIToken(getRhsTokenIndex(i)); }
    
    public int getRhsFirstTokenIndex(int i) { return dtParser.getFirstToken(i); }
    public IToken getRhsFirstIToken(int i) { return prsStream.getIToken(getRhsFirstTokenIndex(i)); }

    public int getRhsLastTokenIndex(int i) { return dtParser.getLastToken(i); }
    public IToken getRhsLastIToken(int i) { return prsStream.getIToken(getRhsLastTokenIndex(i)); }

    public int getLeftSpan() { return dtParser.getFirstToken(); }
    public IToken getLeftIToken()  { return prsStream.getIToken(getLeftSpan()); }

    public int getRightSpan() { return dtParser.getLastToken(); }
    public IToken getRightIToken() { return prsStream.getIToken(getRightSpan()); }

    public int getRhsErrorTokenIndex(int i)
    {
        int index = dtParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (err instanceof ErrorToken ? index : 0);
    }
    public ErrorToken getRhsErrorIToken(int i)
    {
        int index = dtParser.getToken(i);
        IToken err = prsStream.getIToken(index);
        return (ErrorToken) (err instanceof ErrorToken ? err : null);
    }

    public void reset(ILexStream lexStream)
    {
        prsStream = new PrsStream(lexStream);
        dtParser.reset(prsStream);

        try
        {
            prsStream.remapTerminalSymbols(orderedTerminalSymbols(), prsTable.getEoftSymbol());
        }
        catch(NullExportedSymbolsException e) {
        }
        catch(NullTerminalSymbolsException e) {
        }
        catch(UnimplementedTerminalsException e)
        {
            if (unimplementedSymbolsWarning) {
                java.util.ArrayList unimplemented_symbols = e.getSymbols();
                System.out.println("The Lexer will not scan the following token(s):");
                for (int i = 0; i < unimplemented_symbols.size(); i++)
                {
                    Integer id = (Integer) unimplemented_symbols.get(i);
                    System.out.println("    " + LPGParsersym.orderedTerminalSymbols[id.intValue()]);               
                }
                System.out.println();
            }
        }
        catch(UndefinedEofSymbolException e)
        {
            throw new Error(new UndefinedEofSymbolException
                                ("The Lexer does not implement the Eof symbol " +
                                 LPGParsersym.orderedTerminalSymbols[prsTable.getEoftSymbol()]));
        }
    }
    
    public LPGParser()
    {
        try
        {
            dtParser = new DeterministicParser(prsStream, prsTable, (RuleAction) this);
        }
        catch (NotDeterministicParseTableException e)
        {
            throw new Error(new NotDeterministicParseTableException
                                ("Regenerate LPGParserprs.java with -NOBACKTRACK option"));
        }
        catch (BadParseSymFileException e)
        {
            throw new Error(new BadParseSymFileException("Bad Parser Symbol File -- LPGParsersym.java. Regenerate LPGParserprs.java"));
        }
    }

    public LPGParser(ILexStream lexStream)
    {
        this();
        reset(lexStream);
    }

    public int numTokenKinds() { return LPGParsersym.numTokenKinds; }
    public String[] orderedTerminalSymbols() { return LPGParsersym.orderedTerminalSymbols; }
    public String getTokenKindName(int kind) { return LPGParsersym.orderedTerminalSymbols[kind]; }            
    public int getEOFTokenKind() { return prsTable.getEoftSymbol(); }
    public IPrsStream getIPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getPrsStream() { return prsStream; }

    /**
     * @deprecated replaced by {@link #getIPrsStream()}
     *
     */
    public PrsStream getParseStream() { return prsStream; }

    public Object parser()
    {
        return parser(null, 0);
    }
        
    public Object parser(Monitor monitor)
    {
        return parser(monitor, 0);
    }
        
    public Object parser(int error_repair_count)
    {
        return parser(null, error_repair_count);
    }
        
    public Object parser(Monitor monitor, int error_repair_count)
    {
        dtParser.setMonitor(monitor);

        try
        {
            return (Object) dtParser.parse();
        }
        catch (BadParseException e)
        {
            prsStream.reset(e.error_token); // point to error token

            DiagnoseParser diagnoseParser = new DiagnoseParser(prsStream, prsTable);
            diagnoseParser.diagnose(e.error_token);
        }

        return null;
    }

    //
    // Additional entry points, if any
    //
    

    public static class SymbolTable {
        private Map<String,ASTNode> table = new HashMap<String,ASTNode>();

        public void addDefinition(String name, ASTNode def) { table.put(name, def); }
        public ASTNode lookup(String name) { return table.get(name); }
        public List<String> allSymbolsOfType(Class type) {
            List<String> result= new ArrayList<String>();

            for(String sym: table.keySet()) {
                ASTNode def= table.get(sym);

                if (type.isInstance(def))
                    result.add(sym);
            }
            return result;
        }
        public <T> List<T> allDefsOfType(Class<T> type) {
            List<T> result = new ArrayList<T>();
            
            for(String sym: table.keySet()) {
                ASTNode def= table.get(sym);

                if (type.isInstance(def))
                    result.add((T) def);
            }
            return result;
        }
        public Set<String> allSymbols() { return table.keySet(); }
    }
    protected static SymbolTable symtab;
    { symtab = new SymbolTable(); }
     static public abstract class ASTNode implements IAst
    {
        public IAst getNextAst() { return null; }
        protected IToken leftIToken,
                         rightIToken;
        protected IAst parent = null;
        protected void setParent(IAst parent) { this.parent = parent; }
        public IAst getParent() { return parent; }

        public IToken getLeftIToken() { return leftIToken; }
        public IToken getRightIToken() { return rightIToken; }
        public IToken[] getPrecedingAdjuncts() { return leftIToken.getPrecedingAdjuncts(); }
        public IToken[] getFollowingAdjuncts() { return rightIToken.getFollowingAdjuncts(); }

        public String toString()
        {
            return leftIToken.getPrsStream().toString(leftIToken, rightIToken);
        }

        public ASTNode(IToken token) { this.leftIToken = this.rightIToken = token; }
        public ASTNode(IToken leftIToken, IToken rightIToken)
        {
            this.leftIToken = leftIToken;
            this.rightIToken = rightIToken;
        }

        void initialize() {}

        /**
         * A list of all children of this node, excluding the null ones.
         */
        public java.util.ArrayList getChildren()
        {
            java.util.ArrayList list = getAllChildren();
            int k = -1;
            for (int i = 0; i < list.size(); i++)
            {
                Object element = list.get(i);
                if (element != null)
                {
                    if (++k != i)
                        list.set(k, element);
                }
            }
            for (int i = list.size() - 1; i > k; i--) // remove extraneous elements
                list.remove(i);
            return list;
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public abstract java.util.ArrayList getAllChildren();

        /**
         * Since the Ast type has no children, any two instances of it are equal.
         */
        public boolean equals(Object o) { return o instanceof ASTNode; }
        public abstract int hashCode();
        public abstract void accept(IAstVisitor v);
    }

    static public abstract class AbstractASTNodeList extends ASTNode
    {
        private boolean leftRecursive;
        private java.util.ArrayList list;
        public int size() { return list.size(); }
        public ASTNode getElementAt(int i) { return (ASTNode) list.get(leftRecursive ? i : list.size() - 1 - i); }
        public java.util.ArrayList getArrayList()
        {
            if (! leftRecursive) // reverse the list 
            {
                for (int i = 0, n = list.size() - 1; i < n; i++, n--)
                {
                    Object ith = list.get(i),
                           nth = list.get(n);
                    list.set(i, nth);
                    list.set(n, ith);
                }
                leftRecursive = true;
            }
            return list;
        }
        public void add(ASTNode element)
        {
            list.add(element);
            if (leftRecursive)
                 rightIToken = element.getRightIToken();
            else leftIToken = element.getLeftIToken();
        }

        public AbstractASTNodeList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken);
            this.leftRecursive = leftRecursive;
            list = new java.util.ArrayList();
        }

        public AbstractASTNodeList(ASTNode element, boolean leftRecursive)
        {
            this(element.getLeftIToken(), element.getRightIToken(), leftRecursive);
            list.add(element);
        }

        /**
         * Make a copy of the list and return it. Note that we obtain the local list by
         * invoking getArrayList so as to make sure that the list we return is in proper order.
         */
        public java.util.ArrayList getAllChildren()
        {
            return (java.util.ArrayList) getArrayList().clone();
        }

        public abstract boolean equals(Object o);

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
            {
                ASTNode element = getElementAt(i);
                hash = hash * 31 + (element == null ? 0 : element.hashCode());
            }
            return hash;
        }
    }

    static public class ASTNodeToken extends ASTNode implements IASTNodeToken
    {
        public ASTNodeToken(IToken token) { super(token); }
        public IToken getIToken() { return leftIToken; }
        public String toString() { return leftIToken.toString(); }

        /**
         * A token class has no children. So, we return the empty list.
         */
        public java.util.ArrayList getAllChildren() { return new java.util.ArrayList(); }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof ASTNodeToken)) return false;
            ASTNodeToken other = (ASTNodeToken) o;
            return toString().equals(other.toString());
        }

        public int hashCode()
        {
            return toString().hashCode();
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>alias_lhs_macro_name
     *<li>macro_segment
     *<li>optMacroName
     *<li>include_segment
     *<li>RuleName
     *<li>symAttrs
     *<li>action_segment
     *<li>recover_symbol
     *<li>END_KEY_OPT
     *<li>alias_rhs0
     *<li>alias_rhs1
     *<li>alias_rhs2
     *<li>alias_rhs3
     *<li>alias_rhs4
     *<li>alias_rhs5
     *<li>alias_rhs6
     *<li>macro_name_symbol0
     *<li>macro_name_symbol1
     *<li>name0
     *<li>name1
     *<li>name2
     *<li>name3
     *<li>name4
     *<li>name5
     *<li>produces0
     *<li>produces1
     *<li>produces2
     *<li>produces3
     *<li>symWithAttrs0
     *<li>symWithAttrs1
     *<li>start_symbol0
     *<li>start_symbol1
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface IASTNodeToken
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>JikesPG</b>
     */
    public interface IJikesPG
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>option_specList</b>
     */
    public interface Ioptions_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>JikesPG_itemList</b>
     */
    public interface IJikesPG_INPUT
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>AliasSeg
     *<li>AstSeg
     *<li>DefineSeg
     *<li>EofSeg
     *<li>EolSeg
     *<li>ErrorSeg
     *<li>ExportSeg
     *<li>GlobalsSeg
     *<li>HeadersSeg
     *<li>IdentifierSeg
     *<li>ImportSeg
     *<li>IncludeSeg
     *<li>KeywordsSeg
     *<li>NamesSeg
     *<li>NoticeSeg
     *<li>RulesSeg
     *<li>StartSeg
     *<li>TerminalsSeg
     *<li>TrailersSeg
     *<li>TypesSeg
     *<li>RecoverSeg
     *<li>PredecessorSeg
     *</ul>
     *</b>
     */
    public interface IJikesPG_item
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>aliasSpecList</b>
     */
    public interface Ialias_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>END_KEY_OPT</b>
     */
    public interface IEND_KEY_OPT extends IASTNodeToken {}

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Iast_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>defineSpecList</b>
     */
    public interface Idefine_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface Ieof_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface Ieol_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface Ierror_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>terminal_symbolList</b>
     */
    public interface Iexport_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Iglobals_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Iheaders_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface Iidentifier_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>import_segment</b>
     */
    public interface Iimport_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>include_segment</b>
     */
    public interface Iinclude_segment extends IASTNodeToken {}

    /**
     * is implemented by <b>keywordSpecList</b>
     */
    public interface Ikeywords_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>nameSpecList</b>
     */
    public interface Inames_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Inotice_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>rules_segment</b>
     */
    public interface Irules_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>start_symbolList</b>
     */
    public interface Istart_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>terminalList</b>
     */
    public interface Iterminals_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Itrailers_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>type_declarationsList</b>
     */
    public interface Itypes_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>SYMBOLList</b>
     */
    public interface Irecover_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>symbol_pairList</b>
     */
    public interface Ipredecessor_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>option_spec</b>
     */
    public interface Ioption_spec
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>optionList</b>
     */
    public interface Ioption_list
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>option</b>
     */
    public interface Ioption
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>option_value0
     *<li>option_value1
     *</ul>
     *</b>
     */
    public interface Ioption_value
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>SYMBOLList</b>
     */
    public interface Isymbol_list
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>aliasSpec0
     *<li>aliasSpec1
     *<li>aliasSpec2
     *<li>aliasSpec3
     *<li>aliasSpec4
     *<li>aliasSpec5
     *</ul>
     *</b>
     */
    public interface IaliasSpec
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>produces0
     *<li>produces1
     *<li>produces2
     *<li>produces3
     *</ul>
     *</b>
     */
    public interface Iproduces extends IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>alias_rhs0
     *<li>alias_rhs1
     *<li>alias_rhs2
     *<li>alias_rhs3
     *<li>alias_rhs4
     *<li>alias_rhs5
     *<li>alias_rhs6
     *</ul>
     *</b>
     */
    public interface Ialias_rhs extends IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>alias_lhs_macro_name</b>
     */
    public interface Ialias_lhs_macro_name extends IASTNodeToken {}

    /**
     * is implemented by <b>action_segmentList</b>
     */
    public interface Iaction_segment_list extends Iast_segment, Iheaders_segment, Itrailers_segment {}

    /**
     * is implemented by <b>defineSpec</b>
     */
    public interface IdefineSpec
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>macro_name_symbol0
     *<li>macro_name_symbol1
     *</ul>
     *</b>
     */
    public interface Imacro_name_symbol extends IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>macro_segment</b>
     */
    public interface Imacro_segment extends IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface Iterminal_symbol extends Ieol_segment, Ieof_segment, Ierror_segment, Iidentifier_segment, IkeywordSpec, IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>action_segment</b>
     */
    public interface Iaction_segment extends Iopt_action_segment, IASTNodeToken {}

    /**
     * is implemented by <b>drop_commandList</b>
     */
    public interface Idrop_command_list
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>drop_command0
     *<li>drop_command1
     *</ul>
     *</b>
     */
    public interface Idrop_command
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>SYMBOLList</b>
     */
    public interface Idrop_symbols
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>drop_ruleList</b>
     */
    public interface Idrop_rules
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>drop_rule</b>
     */
    public interface Idrop_rule
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>optMacroName</b>
     */
    public interface IoptMacroName extends IASTNodeToken {}

    /**
     * is implemented by <b>ruleList</b>
     */
    public interface IruleList
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>keywordSpec
     *<li>terminal_symbol0
     *<li>terminal_symbol1
     *</ul>
     *</b>
     */
    public interface IkeywordSpec
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>name0
     *<li>name1
     *<li>name2
     *<li>name3
     *<li>name4
     *<li>name5
     *</ul>
     *</b>
     */
    public interface Iname extends IASTNodeToken {}

    /**
     * is implemented by <b>nameSpec</b>
     */
    public interface InameSpec
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>nonTermList</b>
     */
    public interface InonTermList
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>nonTerm</b>
     */
    public interface InonTerm
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>RuleName</b>
     */
    public interface IruleNameWithAttributes extends IASTNodeToken {}

    /**
     * is implemented by <b>rule</b>
     */
    public interface Irule
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>symWithAttrsList</b>
     */
    public interface IsymWithAttrsList
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>action_segment</b>
     */
    public interface Iopt_action_segment
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by:
     *<b>
     *<ul>
     *<li>symWithAttrs0
     *<li>symWithAttrs1
     *</ul>
     *</b>
     */
    public interface IsymWithAttrs extends IASTNodeToken {}

    /**
     * is implemented by <b>symAttrs</b>
     */
    public interface IoptAttrList extends IASTNodeToken {}

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by:
     *<b>
     *<ul>
     *<li>start_symbol0
     *<li>start_symbol1
     *</ul>
     *</b>
     */
    public interface Istart_symbol extends IASTNodeToken {}

    /**
     * is implemented by <b>terminal</b>
     */
    public interface Iterminal
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>optTerminalAlias</b>
     */
    public interface IoptTerminalAlias
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>type_declarations</b>
     */
    public interface Itype_declarations
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>SYMBOLList</b>
     */
    public interface IbarSymbolList
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is implemented by <b>symbol_pair</b>
     */
    public interface Isymbol_pair
    {
        public IToken getLeftIToken();
        public IToken getRightIToken();

        void accept(IAstVisitor v);
    }

    /**
     * is always implemented by <b>ASTNodeToken</b>. It is also implemented by <b>recover_symbol</b>
     */
    public interface Irecover_symbol extends IASTNodeToken {}

    /**
     *<b>
     *<li>Rule 1:  JikesPG ::= options_segment JikesPG_INPUT
     *</b>
     */
    static public class JikesPG extends ASTNode implements IJikesPG
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        private option_specList _options_segment;
        private JikesPG_itemList _JikesPG_INPUT;

        public option_specList getoptions_segment() { return _options_segment; }
        public JikesPG_itemList getJikesPG_INPUT() { return _JikesPG_INPUT; }

        public JikesPG(LPGParser environment, IToken leftIToken, IToken rightIToken,
                       option_specList _options_segment,
                       JikesPG_itemList _JikesPG_INPUT)
        {
            super(leftIToken, rightIToken);

            this.environment = environment;
            this._options_segment = _options_segment;
            ((ASTNode) _options_segment).setParent(this);
            this._JikesPG_INPUT = _JikesPG_INPUT;
            ((ASTNode) _JikesPG_INPUT).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_options_segment);
            list.add(_JikesPG_INPUT);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof JikesPG)) return false;
            JikesPG other = (JikesPG) o;
            if (! _options_segment.equals(other._options_segment)) return false;
            if (! _JikesPG_INPUT.equals(other._JikesPG_INPUT)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_options_segment.hashCode());
            hash = hash * 31 + (_JikesPG_INPUT.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _options_segment.accept(v);
                _JikesPG_INPUT.accept(v);
            }
            v.endVisit(this);
        }

    public SymbolTable symbolTable;
    void initialize() { symbolTable = symtab; }
     }

    /**
     *<b>
     *<li>Rule 2:  JikesPG_INPUT ::= $Empty
     *<li>Rule 3:  JikesPG_INPUT ::= JikesPG_INPUT JikesPG_item
     *</b>
     */
    static public class JikesPG_itemList extends AbstractASTNodeList implements IJikesPG_INPUT
    {
        public IJikesPG_item getJikesPG_itemAt(int i) { return (IJikesPG_item) getElementAt(i); }

        public JikesPG_itemList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public JikesPG_itemList(IJikesPG_item _JikesPG_item, boolean leftRecursive)
        {
            super((ASTNode) _JikesPG_item, leftRecursive);
            ((ASTNode) _JikesPG_item).setParent(this);
        }

        public void add(IJikesPG_item _JikesPG_item)
        {
            super.add((ASTNode) _JikesPG_item);
            ((ASTNode) _JikesPG_item).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof JikesPG_itemList)) return false;
            JikesPG_itemList other = (JikesPG_itemList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                IJikesPG_item element = getJikesPG_itemAt(i);
                    if (! element.equals(other.getJikesPG_itemAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getJikesPG_itemAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    IJikesPG_item element = getJikesPG_itemAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 4:  JikesPG_item ::= ALIAS_KEY$ alias_segment END_KEY_OPT$
     *</b>
     */
    static public class AliasSeg extends ASTNode implements IJikesPG_item
    {
        private aliasSpecList _alias_segment;

        public aliasSpecList getalias_segment() { return _alias_segment; }

        public AliasSeg(IToken leftIToken, IToken rightIToken,
                        aliasSpecList _alias_segment)
        {
            super(leftIToken, rightIToken);

            this._alias_segment = _alias_segment;
            ((ASTNode) _alias_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_alias_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof AliasSeg)) return false;
            AliasSeg other = (AliasSeg) o;
            if (! _alias_segment.equals(other._alias_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_alias_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _alias_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 5:  JikesPG_item ::= AST_KEY$ ast_segment END_KEY_OPT$
     *</b>
     */
    static public class AstSeg extends ASTNode implements IJikesPG_item
    {
        private action_segmentList _ast_segment;

        public action_segmentList getast_segment() { return _ast_segment; }

        public AstSeg(IToken leftIToken, IToken rightIToken,
                      action_segmentList _ast_segment)
        {
            super(leftIToken, rightIToken);

            this._ast_segment = _ast_segment;
            ((ASTNode) _ast_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_ast_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof AstSeg)) return false;
            AstSeg other = (AstSeg) o;
            if (! _ast_segment.equals(other._ast_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_ast_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _ast_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 6:  JikesPG_item ::= DEFINE_KEY$ define_segment END_KEY_OPT$
     *</b>
     */
    static public class DefineSeg extends ASTNode implements IJikesPG_item
    {
        private defineSpecList _define_segment;

        public defineSpecList getdefine_segment() { return _define_segment; }

        public DefineSeg(IToken leftIToken, IToken rightIToken,
                         defineSpecList _define_segment)
        {
            super(leftIToken, rightIToken);

            this._define_segment = _define_segment;
            ((ASTNode) _define_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_define_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof DefineSeg)) return false;
            DefineSeg other = (DefineSeg) o;
            if (! _define_segment.equals(other._define_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_define_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _define_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 7:  JikesPG_item ::= EOF_KEY$ eof_segment END_KEY_OPT$
     *</b>
     */
    static public class EofSeg extends ASTNode implements IJikesPG_item
    {
        private Ieof_segment _eof_segment;

        public Ieof_segment geteof_segment() { return _eof_segment; }

        public EofSeg(IToken leftIToken, IToken rightIToken,
                      Ieof_segment _eof_segment)
        {
            super(leftIToken, rightIToken);

            this._eof_segment = _eof_segment;
            ((ASTNode) _eof_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_eof_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof EofSeg)) return false;
            EofSeg other = (EofSeg) o;
            if (! _eof_segment.equals(other._eof_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_eof_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _eof_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 8:  JikesPG_item ::= EOL_KEY$ eol_segment END_KEY_OPT$
     *</b>
     */
    static public class EolSeg extends ASTNode implements IJikesPG_item
    {
        private Ieol_segment _eol_segment;

        public Ieol_segment geteol_segment() { return _eol_segment; }

        public EolSeg(IToken leftIToken, IToken rightIToken,
                      Ieol_segment _eol_segment)
        {
            super(leftIToken, rightIToken);

            this._eol_segment = _eol_segment;
            ((ASTNode) _eol_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_eol_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof EolSeg)) return false;
            EolSeg other = (EolSeg) o;
            if (! _eol_segment.equals(other._eol_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_eol_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _eol_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 9:  JikesPG_item ::= ERROR_KEY$ error_segment END_KEY_OPT$
     *</b>
     */
    static public class ErrorSeg extends ASTNode implements IJikesPG_item
    {
        private Ierror_segment _error_segment;

        public Ierror_segment geterror_segment() { return _error_segment; }

        public ErrorSeg(IToken leftIToken, IToken rightIToken,
                        Ierror_segment _error_segment)
        {
            super(leftIToken, rightIToken);

            this._error_segment = _error_segment;
            ((ASTNode) _error_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_error_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof ErrorSeg)) return false;
            ErrorSeg other = (ErrorSeg) o;
            if (! _error_segment.equals(other._error_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_error_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _error_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 10:  JikesPG_item ::= EXPORT_KEY$ export_segment END_KEY_OPT$
     *</b>
     */
    static public class ExportSeg extends ASTNode implements IJikesPG_item
    {
        private terminal_symbolList _export_segment;

        public terminal_symbolList getexport_segment() { return _export_segment; }

        public ExportSeg(IToken leftIToken, IToken rightIToken,
                         terminal_symbolList _export_segment)
        {
            super(leftIToken, rightIToken);

            this._export_segment = _export_segment;
            ((ASTNode) _export_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_export_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof ExportSeg)) return false;
            ExportSeg other = (ExportSeg) o;
            if (! _export_segment.equals(other._export_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_export_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _export_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 11:  JikesPG_item ::= GLOBALS_KEY$ globals_segment END_KEY_OPT$
     *</b>
     */
    static public class GlobalsSeg extends ASTNode implements IJikesPG_item
    {
        private action_segmentList _globals_segment;

        public action_segmentList getglobals_segment() { return _globals_segment; }

        public GlobalsSeg(IToken leftIToken, IToken rightIToken,
                          action_segmentList _globals_segment)
        {
            super(leftIToken, rightIToken);

            this._globals_segment = _globals_segment;
            ((ASTNode) _globals_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_globals_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof GlobalsSeg)) return false;
            GlobalsSeg other = (GlobalsSeg) o;
            if (! _globals_segment.equals(other._globals_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_globals_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _globals_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 12:  JikesPG_item ::= HEADERS_KEY$ headers_segment END_KEY_OPT$
     *</b>
     */
    static public class HeadersSeg extends ASTNode implements IJikesPG_item
    {
        private action_segmentList _headers_segment;

        public action_segmentList getheaders_segment() { return _headers_segment; }

        public HeadersSeg(IToken leftIToken, IToken rightIToken,
                          action_segmentList _headers_segment)
        {
            super(leftIToken, rightIToken);

            this._headers_segment = _headers_segment;
            ((ASTNode) _headers_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_headers_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof HeadersSeg)) return false;
            HeadersSeg other = (HeadersSeg) o;
            if (! _headers_segment.equals(other._headers_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_headers_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _headers_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 13:  JikesPG_item ::= IDENTIFIER_KEY$ identifier_segment END_KEY_OPT$
     *</b>
     */
    static public class IdentifierSeg extends ASTNode implements IJikesPG_item
    {
        private Iidentifier_segment _identifier_segment;

        public Iidentifier_segment getidentifier_segment() { return _identifier_segment; }

        public IdentifierSeg(IToken leftIToken, IToken rightIToken,
                             Iidentifier_segment _identifier_segment)
        {
            super(leftIToken, rightIToken);

            this._identifier_segment = _identifier_segment;
            ((ASTNode) _identifier_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_identifier_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof IdentifierSeg)) return false;
            IdentifierSeg other = (IdentifierSeg) o;
            if (! _identifier_segment.equals(other._identifier_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_identifier_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _identifier_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 14:  JikesPG_item ::= IMPORT_KEY$ import_segment END_KEY_OPT$
     *</b>
     */
    static public class ImportSeg extends ASTNode implements IJikesPG_item
    {
        private import_segment _import_segment;

        public import_segment getimport_segment() { return _import_segment; }

        public ImportSeg(IToken leftIToken, IToken rightIToken,
                         import_segment _import_segment)
        {
            super(leftIToken, rightIToken);

            this._import_segment = _import_segment;
            ((ASTNode) _import_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_import_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof ImportSeg)) return false;
            ImportSeg other = (ImportSeg) o;
            if (! _import_segment.equals(other._import_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_import_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _import_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 15:  JikesPG_item ::= INCLUDE_KEY$ include_segment END_KEY_OPT$
     *</b>
     */
    static public class IncludeSeg extends ASTNode implements IJikesPG_item
    {
        private include_segment _include_segment;

        public include_segment getinclude_segment() { return _include_segment; }

        public IncludeSeg(IToken leftIToken, IToken rightIToken,
                          include_segment _include_segment)
        {
            super(leftIToken, rightIToken);

            this._include_segment = _include_segment;
            ((ASTNode) _include_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_include_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof IncludeSeg)) return false;
            IncludeSeg other = (IncludeSeg) o;
            if (! _include_segment.equals(other._include_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_include_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _include_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 16:  JikesPG_item ::= KEYWORDS_KEY$ keywords_segment END_KEY_OPT$
     *</b>
     */
    static public class KeywordsSeg extends ASTNode implements IJikesPG_item
    {
        private keywordSpecList _keywords_segment;

        public keywordSpecList getkeywords_segment() { return _keywords_segment; }

        public KeywordsSeg(IToken leftIToken, IToken rightIToken,
                           keywordSpecList _keywords_segment)
        {
            super(leftIToken, rightIToken);

            this._keywords_segment = _keywords_segment;
            ((ASTNode) _keywords_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_keywords_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof KeywordsSeg)) return false;
            KeywordsSeg other = (KeywordsSeg) o;
            if (! _keywords_segment.equals(other._keywords_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_keywords_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _keywords_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 17:  JikesPG_item ::= NAMES_KEY$ names_segment END_KEY_OPT$
     *</b>
     */
    static public class NamesSeg extends ASTNode implements IJikesPG_item
    {
        private nameSpecList _names_segment;

        public nameSpecList getnames_segment() { return _names_segment; }

        public NamesSeg(IToken leftIToken, IToken rightIToken,
                        nameSpecList _names_segment)
        {
            super(leftIToken, rightIToken);

            this._names_segment = _names_segment;
            ((ASTNode) _names_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_names_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof NamesSeg)) return false;
            NamesSeg other = (NamesSeg) o;
            if (! _names_segment.equals(other._names_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_names_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _names_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 18:  JikesPG_item ::= NOTICE_KEY$ notice_segment END_KEY_OPT$
     *</b>
     */
    static public class NoticeSeg extends ASTNode implements IJikesPG_item
    {
        private action_segmentList _notice_segment;

        public action_segmentList getnotice_segment() { return _notice_segment; }

        public NoticeSeg(IToken leftIToken, IToken rightIToken,
                         action_segmentList _notice_segment)
        {
            super(leftIToken, rightIToken);

            this._notice_segment = _notice_segment;
            ((ASTNode) _notice_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_notice_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof NoticeSeg)) return false;
            NoticeSeg other = (NoticeSeg) o;
            if (! _notice_segment.equals(other._notice_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_notice_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _notice_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 19:  JikesPG_item ::= RULES_KEY$ rules_segment END_KEY_OPT$
     *</b>
     */
    static public class RulesSeg extends ASTNode implements IJikesPG_item
    {
        private rules_segment _rules_segment;

        public rules_segment getrules_segment() { return _rules_segment; }

        public RulesSeg(IToken leftIToken, IToken rightIToken,
                        rules_segment _rules_segment)
        {
            super(leftIToken, rightIToken);

            this._rules_segment = _rules_segment;
            ((ASTNode) _rules_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_rules_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof RulesSeg)) return false;
            RulesSeg other = (RulesSeg) o;
            if (! _rules_segment.equals(other._rules_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_rules_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _rules_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 20:  JikesPG_item ::= START_KEY$ start_segment END_KEY_OPT$
     *</b>
     */
    static public class StartSeg extends ASTNode implements IJikesPG_item
    {
        private start_symbolList _start_segment;

        public start_symbolList getstart_segment() { return _start_segment; }

        public StartSeg(IToken leftIToken, IToken rightIToken,
                        start_symbolList _start_segment)
        {
            super(leftIToken, rightIToken);

            this._start_segment = _start_segment;
            ((ASTNode) _start_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_start_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof StartSeg)) return false;
            StartSeg other = (StartSeg) o;
            if (! _start_segment.equals(other._start_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_start_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _start_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 21:  JikesPG_item ::= TERMINALS_KEY$ terminals_segment END_KEY_OPT$
     *</b>
     */
    static public class TerminalsSeg extends ASTNode implements IJikesPG_item
    {
        private terminalList _terminals_segment;

        public terminalList getterminals_segment() { return _terminals_segment; }

        public TerminalsSeg(IToken leftIToken, IToken rightIToken,
                            terminalList _terminals_segment)
        {
            super(leftIToken, rightIToken);

            this._terminals_segment = _terminals_segment;
            ((ASTNode) _terminals_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_terminals_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof TerminalsSeg)) return false;
            TerminalsSeg other = (TerminalsSeg) o;
            if (! _terminals_segment.equals(other._terminals_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_terminals_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _terminals_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 22:  JikesPG_item ::= TRAILERS_KEY$ trailers_segment END_KEY_OPT$
     *</b>
     */
    static public class TrailersSeg extends ASTNode implements IJikesPG_item
    {
        private action_segmentList _trailers_segment;

        public action_segmentList gettrailers_segment() { return _trailers_segment; }

        public TrailersSeg(IToken leftIToken, IToken rightIToken,
                           action_segmentList _trailers_segment)
        {
            super(leftIToken, rightIToken);

            this._trailers_segment = _trailers_segment;
            ((ASTNode) _trailers_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_trailers_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof TrailersSeg)) return false;
            TrailersSeg other = (TrailersSeg) o;
            if (! _trailers_segment.equals(other._trailers_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_trailers_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _trailers_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 23:  JikesPG_item ::= TYPES_KEY$ types_segment END_KEY_OPT$
     *</b>
     */
    static public class TypesSeg extends ASTNode implements IJikesPG_item
    {
        private type_declarationsList _types_segment;

        public type_declarationsList gettypes_segment() { return _types_segment; }

        public TypesSeg(IToken leftIToken, IToken rightIToken,
                        type_declarationsList _types_segment)
        {
            super(leftIToken, rightIToken);

            this._types_segment = _types_segment;
            ((ASTNode) _types_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_types_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof TypesSeg)) return false;
            TypesSeg other = (TypesSeg) o;
            if (! _types_segment.equals(other._types_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_types_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _types_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 24:  JikesPG_item ::= RECOVER_KEY$ recover_segment END_KEY_OPT$
     *</b>
     */
    static public class RecoverSeg extends ASTNode implements IJikesPG_item
    {
        private SYMBOLList _recover_segment;

        public SYMBOLList getrecover_segment() { return _recover_segment; }

        public RecoverSeg(IToken leftIToken, IToken rightIToken,
                          SYMBOLList _recover_segment)
        {
            super(leftIToken, rightIToken);

            this._recover_segment = _recover_segment;
            ((ASTNode) _recover_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_recover_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof RecoverSeg)) return false;
            RecoverSeg other = (RecoverSeg) o;
            if (! _recover_segment.equals(other._recover_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_recover_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _recover_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 25:  JikesPG_item ::= DISJOINTPREDECESSORSETS_KEY$ predecessor_segment END_KEY_OPT$
     *</b>
     */
    static public class PredecessorSeg extends ASTNode implements IJikesPG_item
    {
        private symbol_pairList _predecessor_segment;

        public symbol_pairList getpredecessor_segment() { return _predecessor_segment; }

        public PredecessorSeg(IToken leftIToken, IToken rightIToken,
                              symbol_pairList _predecessor_segment)
        {
            super(leftIToken, rightIToken);

            this._predecessor_segment = _predecessor_segment;
            ((ASTNode) _predecessor_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_predecessor_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof PredecessorSeg)) return false;
            PredecessorSeg other = (PredecessorSeg) o;
            if (! _predecessor_segment.equals(other._predecessor_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_predecessor_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _predecessor_segment.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 26:  options_segment ::= $Empty
     *<li>Rule 27:  options_segment ::= options_segment option_spec
     *</b>
     */
    static public class option_specList extends AbstractASTNodeList implements Ioptions_segment
    {
        public option_spec getoption_specAt(int i) { return (option_spec) getElementAt(i); }

        public option_specList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public option_specList(option_spec _option_spec, boolean leftRecursive)
        {
            super((ASTNode) _option_spec, leftRecursive);
            ((ASTNode) _option_spec).setParent(this);
        }

        public void add(option_spec _option_spec)
        {
            super.add((ASTNode) _option_spec);
            ((ASTNode) _option_spec).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof option_specList)) return false;
            option_specList other = (option_specList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                option_spec element = getoption_specAt(i);
                    if (! element.equals(other.getoption_specAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getoption_specAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    option_spec element = getoption_specAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 28:  option_spec ::= OPTIONS_KEY$ option_list
     *</b>
     */
    static public class option_spec extends ASTNode implements Ioption_spec
    {
        private optionList _option_list;

        public optionList getoption_list() { return _option_list; }

        public option_spec(IToken leftIToken, IToken rightIToken,
                           optionList _option_list)
        {
            super(leftIToken, rightIToken);

            this._option_list = _option_list;
            ((ASTNode) _option_list).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_option_list);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof option_spec)) return false;
            option_spec other = (option_spec) o;
            if (! _option_list.equals(other._option_list)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_option_list.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _option_list.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 29:  option_list ::= option
     *<li>Rule 30:  option_list ::= option_list ,$ option
     *</b>
     */
    static public class optionList extends AbstractASTNodeList implements Ioption_list
    {
        public option getoptionAt(int i) { return (option) getElementAt(i); }

        public optionList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public optionList(option _option, boolean leftRecursive)
        {
            super((ASTNode) _option, leftRecursive);
            ((ASTNode) _option).setParent(this);
        }

        public void add(option _option)
        {
            super.add((ASTNode) _option);
            ((ASTNode) _option).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof optionList)) return false;
            optionList other = (optionList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                option element = getoptionAt(i);
                    if (! element.equals(other.getoptionAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getoptionAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    option element = getoptionAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 31:  option ::= SYMBOL option_value
     *</b>
     */
    static public class option extends ASTNode implements Ioption
    {
        private ASTNodeToken _SYMBOL;
        private Ioption_value _option_value;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        /**
         * The value returned by <b>getoption_value</b> may be <b>null</b>
         */
        public Ioption_value getoption_value() { return _option_value; }

        public option(IToken leftIToken, IToken rightIToken,
                      ASTNodeToken _SYMBOL,
                      Ioption_value _option_value)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._option_value = _option_value;
            if (_option_value != null) ((ASTNode) _option_value).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_option_value);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof option)) return false;
            option other = (option) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (_option_value == null)
                if (other._option_value != null) return false;
                else; // continue
            else if (! _option_value.equals(other._option_value)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_option_value == null ? 0 : _option_value.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                if (_option_value != null) _option_value.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 35:  symbol_list ::= SYMBOL
     *<li>Rule 36:  symbol_list ::= symbol_list ,$ SYMBOL
     *<li>Rule 74:  drop_symbols ::= SYMBOL
     *<li>Rule 75:  drop_symbols ::= drop_symbols SYMBOL
     *<li>Rule 135:  barSymbolList ::= SYMBOL
     *<li>Rule 136:  barSymbolList ::= barSymbolList |$ SYMBOL
     *<li>Rule 140:  recover_segment ::= $Empty
     *<li>Rule 141:  recover_segment ::= recover_segment recover_symbol
     *</b>
     */
    static public class SYMBOLList extends AbstractASTNodeList implements Isymbol_list, Idrop_symbols, IbarSymbolList, Irecover_segment
    {
        public ASTNodeToken getSYMBOLAt(int i) { return (ASTNodeToken) getElementAt(i); }

        public SYMBOLList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public SYMBOLList(ASTNodeToken _SYMBOL, boolean leftRecursive)
        {
            super((ASTNode) _SYMBOL, leftRecursive);
            ((ASTNode) _SYMBOL).setParent(this);
        }

        public void add(ASTNodeToken _SYMBOL)
        {
            super.add((ASTNode) _SYMBOL);
            ((ASTNode) _SYMBOL).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof SYMBOLList)) return false;
            SYMBOLList other = (SYMBOLList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                ASTNodeToken element = getSYMBOLAt(i);
                    if (! element.equals(other.getSYMBOLAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getSYMBOLAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    ASTNodeToken element = getSYMBOLAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 37:  alias_segment ::= aliasSpec
     *<li>Rule 38:  alias_segment ::= alias_segment aliasSpec
     *</b>
     */
    static public class aliasSpecList extends AbstractASTNodeList implements Ialias_segment
    {
        public IaliasSpec getaliasSpecAt(int i) { return (IaliasSpec) getElementAt(i); }

        public aliasSpecList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public aliasSpecList(IaliasSpec _aliasSpec, boolean leftRecursive)
        {
            super((ASTNode) _aliasSpec, leftRecursive);
            ((ASTNode) _aliasSpec).setParent(this);
        }

        public void add(IaliasSpec _aliasSpec)
        {
            super.add((ASTNode) _aliasSpec);
            ((ASTNode) _aliasSpec).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof aliasSpecList)) return false;
            aliasSpecList other = (aliasSpecList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                IaliasSpec element = getaliasSpecAt(i);
                    if (! element.equals(other.getaliasSpecAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getaliasSpecAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    IaliasSpec element = getaliasSpecAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 45:  alias_lhs_macro_name ::= MACRO_NAME
     *</b>
     */
    static public class alias_lhs_macro_name extends ASTNodeToken implements Ialias_lhs_macro_name
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public alias_lhs_macro_name(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 54:  define_segment ::= defineSpec
     *<li>Rule 55:  define_segment ::= define_segment defineSpec
     *</b>
     */
    static public class defineSpecList extends AbstractASTNodeList implements Idefine_segment
    {
        public defineSpec getdefineSpecAt(int i) { return (defineSpec) getElementAt(i); }

        public defineSpecList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public defineSpecList(defineSpec _defineSpec, boolean leftRecursive)
        {
            super((ASTNode) _defineSpec, leftRecursive);
            ((ASTNode) _defineSpec).setParent(this);
        }

        public void add(defineSpec _defineSpec)
        {
            super.add((ASTNode) _defineSpec);
            ((ASTNode) _defineSpec).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof defineSpecList)) return false;
            defineSpecList other = (defineSpecList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                defineSpec element = getdefineSpecAt(i);
                    if (! element.equals(other.getdefineSpecAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getdefineSpecAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    defineSpec element = getdefineSpecAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 56:  defineSpec ::= macro_name_symbol macro_segment
     *</b>
     */
    static public class defineSpec extends ASTNode implements IdefineSpec
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        private Imacro_name_symbol _macro_name_symbol;
        private macro_segment _macro_segment;

        public Imacro_name_symbol getmacro_name_symbol() { return _macro_name_symbol; }
        public macro_segment getmacro_segment() { return _macro_segment; }

        public defineSpec(LPGParser environment, IToken leftIToken, IToken rightIToken,
                          Imacro_name_symbol _macro_name_symbol,
                          macro_segment _macro_segment)
        {
            super(leftIToken, rightIToken);

            this.environment = environment;
            this._macro_name_symbol = _macro_name_symbol;
            ((ASTNode) _macro_name_symbol).setParent(this);
            this._macro_segment = _macro_segment;
            ((ASTNode) _macro_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_macro_name_symbol);
            list.add(_macro_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof defineSpec)) return false;
            defineSpec other = (defineSpec) o;
            if (! _macro_name_symbol.equals(other._macro_name_symbol)) return false;
            if (! _macro_segment.equals(other._macro_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_macro_name_symbol.hashCode());
            hash = hash * 31 + (_macro_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _macro_name_symbol.accept(v);
                _macro_segment.accept(v);
            }
            v.endVisit(this);
        }

    void initialize() { symtab.addDefinition(_macro_name_symbol.toString(), this); }
     }

    /**
     *<b>
     *<li>Rule 59:  macro_segment ::= BLOCK
     *</b>
     */
    static public class macro_segment extends ASTNodeToken implements Imacro_segment
    {
        public IToken getBLOCK() { return leftIToken; }

        public macro_segment(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 63:  export_segment ::= terminal_symbol
     *<li>Rule 64:  export_segment ::= export_segment terminal_symbol
     *</b>
     */
    static public class terminal_symbolList extends AbstractASTNodeList implements Iexport_segment
    {
        public Iterminal_symbol getterminal_symbolAt(int i) { return (Iterminal_symbol) getElementAt(i); }

        public terminal_symbolList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public terminal_symbolList(Iterminal_symbol _terminal_symbol, boolean leftRecursive)
        {
            super((ASTNode) _terminal_symbol, leftRecursive);
            ((ASTNode) _terminal_symbol).setParent(this);
        }

        public void add(Iterminal_symbol _terminal_symbol)
        {
            super.add((ASTNode) _terminal_symbol);
            ((ASTNode) _terminal_symbol).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof terminal_symbolList)) return false;
            terminal_symbolList other = (terminal_symbolList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                Iterminal_symbol element = getterminal_symbolAt(i);
                    if (! element.equals(other.getterminal_symbolAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getterminal_symbolAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    Iterminal_symbol element = getterminal_symbolAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 65:  globals_segment ::= action_segment
     *<li>Rule 66:  globals_segment ::= globals_segment action_segment
     *<li>Rule 95:  notice_segment ::= action_segment
     *<li>Rule 96:  notice_segment ::= notice_segment action_segment
     *<li>Rule 145:  action_segment_list ::= $Empty
     *<li>Rule 146:  action_segment_list ::= action_segment_list action_segment
     *</b>
     */
    static public class action_segmentList extends AbstractASTNodeList implements Iglobals_segment, Inotice_segment, Iaction_segment_list
    {
        public action_segment getaction_segmentAt(int i) { return (action_segment) getElementAt(i); }

        public action_segmentList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public action_segmentList(action_segment _action_segment, boolean leftRecursive)
        {
            super((ASTNode) _action_segment, leftRecursive);
            ((ASTNode) _action_segment).setParent(this);
        }

        public void add(action_segment _action_segment)
        {
            super.add((ASTNode) _action_segment);
            ((ASTNode) _action_segment).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof action_segmentList)) return false;
            action_segmentList other = (action_segmentList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                action_segment element = getaction_segmentAt(i);
                    if (! element.equals(other.getaction_segmentAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getaction_segmentAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    action_segment element = getaction_segmentAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 69:  import_segment ::= SYMBOL drop_command_list
     *</b>
     */
    static public class import_segment extends ASTNode implements Iimport_segment
    {
        private ASTNodeToken _SYMBOL;
        private drop_commandList _drop_command_list;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        public drop_commandList getdrop_command_list() { return _drop_command_list; }

        public import_segment(IToken leftIToken, IToken rightIToken,
                              ASTNodeToken _SYMBOL,
                              drop_commandList _drop_command_list)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._drop_command_list = _drop_command_list;
            ((ASTNode) _drop_command_list).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_drop_command_list);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof import_segment)) return false;
            import_segment other = (import_segment) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (! _drop_command_list.equals(other._drop_command_list)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_drop_command_list.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                _drop_command_list.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 70:  drop_command_list ::= $Empty
     *<li>Rule 71:  drop_command_list ::= drop_command_list drop_command
     *</b>
     */
    static public class drop_commandList extends AbstractASTNodeList implements Idrop_command_list
    {
        public Idrop_command getdrop_commandAt(int i) { return (Idrop_command) getElementAt(i); }

        public drop_commandList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public drop_commandList(Idrop_command _drop_command, boolean leftRecursive)
        {
            super((ASTNode) _drop_command, leftRecursive);
            ((ASTNode) _drop_command).setParent(this);
        }

        public void add(Idrop_command _drop_command)
        {
            super.add((ASTNode) _drop_command);
            ((ASTNode) _drop_command).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof drop_commandList)) return false;
            drop_commandList other = (drop_commandList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                Idrop_command element = getdrop_commandAt(i);
                    if (! element.equals(other.getdrop_commandAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getdrop_commandAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    Idrop_command element = getdrop_commandAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 76:  drop_rules ::= drop_rule
     *<li>Rule 77:  drop_rules ::= drop_rules drop_rule
     *</b>
     */
    static public class drop_ruleList extends AbstractASTNodeList implements Idrop_rules
    {
        public drop_rule getdrop_ruleAt(int i) { return (drop_rule) getElementAt(i); }

        public drop_ruleList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public drop_ruleList(drop_rule _drop_rule, boolean leftRecursive)
        {
            super((ASTNode) _drop_rule, leftRecursive);
            ((ASTNode) _drop_rule).setParent(this);
        }

        public void add(drop_rule _drop_rule)
        {
            super.add((ASTNode) _drop_rule);
            ((ASTNode) _drop_rule).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof drop_ruleList)) return false;
            drop_ruleList other = (drop_ruleList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                drop_rule element = getdrop_ruleAt(i);
                    if (! element.equals(other.getdrop_ruleAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getdrop_ruleAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    drop_rule element = getdrop_ruleAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 78:  drop_rule ::= SYMBOL optMacroName produces ruleList
     *</b>
     */
    static public class drop_rule extends ASTNode implements Idrop_rule
    {
        private ASTNodeToken _SYMBOL;
        private optMacroName _optMacroName;
        private Iproduces _produces;
        private ruleList _ruleList;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        /**
         * The value returned by <b>getoptMacroName</b> may be <b>null</b>
         */
        public optMacroName getoptMacroName() { return _optMacroName; }
        public Iproduces getproduces() { return _produces; }
        public ruleList getruleList() { return _ruleList; }

        public drop_rule(IToken leftIToken, IToken rightIToken,
                         ASTNodeToken _SYMBOL,
                         optMacroName _optMacroName,
                         Iproduces _produces,
                         ruleList _ruleList)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._optMacroName = _optMacroName;
            if (_optMacroName != null) ((ASTNode) _optMacroName).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._ruleList = _ruleList;
            ((ASTNode) _ruleList).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_optMacroName);
            list.add(_produces);
            list.add(_ruleList);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof drop_rule)) return false;
            drop_rule other = (drop_rule) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (_optMacroName == null)
                if (other._optMacroName != null) return false;
                else; // continue
            else if (! _optMacroName.equals(other._optMacroName)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _ruleList.equals(other._ruleList)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_optMacroName == null ? 0 : _optMacroName.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_ruleList.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                if (_optMacroName != null) _optMacroName.accept(v);
                _produces.accept(v);
                _ruleList.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<em>
     *<li>Rule 79:  optMacroName ::= $Empty
     *</em>
     *<p>
     *<b>
     *<li>Rule 80:  optMacroName ::= MACRO_NAME
     *</b>
     */
    static public class optMacroName extends ASTNodeToken implements IoptMacroName
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public optMacroName(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 81:  include_segment ::= SYMBOL
     *</b>
     */
    static public class include_segment extends ASTNodeToken implements Iinclude_segment
    {
        public IToken getSYMBOL() { return leftIToken; }

        public include_segment(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 82:  keywords_segment ::= keywordSpec
     *<li>Rule 83:  keywords_segment ::= keywords_segment keywordSpec
     *</b>
     */
    static public class keywordSpecList extends AbstractASTNodeList implements Ikeywords_segment
    {
        public IkeywordSpec getkeywordSpecAt(int i) { return (IkeywordSpec) getElementAt(i); }

        public keywordSpecList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public keywordSpecList(IkeywordSpec _keywordSpec, boolean leftRecursive)
        {
            super((ASTNode) _keywordSpec, leftRecursive);
            ((ASTNode) _keywordSpec).setParent(this);
        }

        public void add(IkeywordSpec _keywordSpec)
        {
            super.add((ASTNode) _keywordSpec);
            ((ASTNode) _keywordSpec).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof keywordSpecList)) return false;
            keywordSpecList other = (keywordSpecList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                IkeywordSpec element = getkeywordSpecAt(i);
                    if (! element.equals(other.getkeywordSpecAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getkeywordSpecAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    IkeywordSpec element = getkeywordSpecAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<em>
     *<li>Rule 84:  keywordSpec ::= terminal_symbol
     *</em>
     *<p>
     *<b>
     *<li>Rule 85:  keywordSpec ::= terminal_symbol produces name
     *</b>
     */
    static public class keywordSpec extends ASTNode implements IkeywordSpec
    {
        private Iterminal_symbol _terminal_symbol;
        private Iproduces _produces;
        private Iname _name;

        public Iterminal_symbol getterminal_symbol() { return _terminal_symbol; }
        public Iproduces getproduces() { return _produces; }
        public Iname getname() { return _name; }

        public keywordSpec(IToken leftIToken, IToken rightIToken,
                           Iterminal_symbol _terminal_symbol,
                           Iproduces _produces,
                           Iname _name)
        {
            super(leftIToken, rightIToken);

            this._terminal_symbol = _terminal_symbol;
            ((ASTNode) _terminal_symbol).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._name = _name;
            ((ASTNode) _name).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_terminal_symbol);
            list.add(_produces);
            list.add(_name);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof keywordSpec)) return false;
            keywordSpec other = (keywordSpec) o;
            if (! _terminal_symbol.equals(other._terminal_symbol)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _name.equals(other._name)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_terminal_symbol.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_name.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _terminal_symbol.accept(v);
                _produces.accept(v);
                _name.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 86:  names_segment ::= nameSpec
     *<li>Rule 87:  names_segment ::= names_segment nameSpec
     *</b>
     */
    static public class nameSpecList extends AbstractASTNodeList implements Inames_segment
    {
        public nameSpec getnameSpecAt(int i) { return (nameSpec) getElementAt(i); }

        public nameSpecList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public nameSpecList(nameSpec _nameSpec, boolean leftRecursive)
        {
            super((ASTNode) _nameSpec, leftRecursive);
            ((ASTNode) _nameSpec).setParent(this);
        }

        public void add(nameSpec _nameSpec)
        {
            super.add((ASTNode) _nameSpec);
            ((ASTNode) _nameSpec).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof nameSpecList)) return false;
            nameSpecList other = (nameSpecList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                nameSpec element = getnameSpecAt(i);
                    if (! element.equals(other.getnameSpecAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getnameSpecAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    nameSpec element = getnameSpecAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 88:  nameSpec ::= name produces name
     *</b>
     */
    static public class nameSpec extends ASTNode implements InameSpec
    {
        private Iname _name;
        private Iproduces _produces;
        private Iname _name3;

        public Iname getname() { return _name; }
        public Iproduces getproduces() { return _produces; }
        public Iname getname3() { return _name3; }

        public nameSpec(IToken leftIToken, IToken rightIToken,
                        Iname _name,
                        Iproduces _produces,
                        Iname _name3)
        {
            super(leftIToken, rightIToken);

            this._name = _name;
            ((ASTNode) _name).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._name3 = _name3;
            ((ASTNode) _name3).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_name);
            list.add(_produces);
            list.add(_name3);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof nameSpec)) return false;
            nameSpec other = (nameSpec) o;
            if (! _name.equals(other._name)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _name3.equals(other._name3)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_name.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_name3.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _name.accept(v);
                _produces.accept(v);
                _name3.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 97:  rules_segment ::= action_segment_list nonTermList
     *</b>
     */
    static public class rules_segment extends ASTNode implements Irules_segment
    {
        private action_segmentList _action_segment_list;
        private nonTermList _nonTermList;

        public action_segmentList getaction_segment_list() { return _action_segment_list; }
        public nonTermList getnonTermList() { return _nonTermList; }

        public rules_segment(IToken leftIToken, IToken rightIToken,
                             action_segmentList _action_segment_list,
                             nonTermList _nonTermList)
        {
            super(leftIToken, rightIToken);

            this._action_segment_list = _action_segment_list;
            ((ASTNode) _action_segment_list).setParent(this);
            this._nonTermList = _nonTermList;
            ((ASTNode) _nonTermList).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_action_segment_list);
            list.add(_nonTermList);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof rules_segment)) return false;
            rules_segment other = (rules_segment) o;
            if (! _action_segment_list.equals(other._action_segment_list)) return false;
            if (! _nonTermList.equals(other._nonTermList)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_action_segment_list.hashCode());
            hash = hash * 31 + (_nonTermList.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _action_segment_list.accept(v);
                _nonTermList.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 98:  nonTermList ::= $Empty
     *<li>Rule 99:  nonTermList ::= nonTermList nonTerm
     *</b>
     */
    static public class nonTermList extends AbstractASTNodeList implements InonTermList
    {
        public nonTerm getnonTermAt(int i) { return (nonTerm) getElementAt(i); }

        public nonTermList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public nonTermList(nonTerm _nonTerm, boolean leftRecursive)
        {
            super((ASTNode) _nonTerm, leftRecursive);
            ((ASTNode) _nonTerm).setParent(this);
        }

        public void add(nonTerm _nonTerm)
        {
            super.add((ASTNode) _nonTerm);
            ((ASTNode) _nonTerm).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof nonTermList)) return false;
            nonTermList other = (nonTermList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                nonTerm element = getnonTermAt(i);
                    if (! element.equals(other.getnonTermAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getnonTermAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    nonTerm element = getnonTermAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 100:  nonTerm ::= ruleNameWithAttributes produces ruleList
     *</b>
     */
    static public class nonTerm extends ASTNode implements InonTerm
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        private RuleName _ruleNameWithAttributes;
        private Iproduces _produces;
        private ruleList _ruleList;

        public RuleName getruleNameWithAttributes() { return _ruleNameWithAttributes; }
        public Iproduces getproduces() { return _produces; }
        public ruleList getruleList() { return _ruleList; }

        public nonTerm(LPGParser environment, IToken leftIToken, IToken rightIToken,
                       RuleName _ruleNameWithAttributes,
                       Iproduces _produces,
                       ruleList _ruleList)
        {
            super(leftIToken, rightIToken);

            this.environment = environment;
            this._ruleNameWithAttributes = _ruleNameWithAttributes;
            ((ASTNode) _ruleNameWithAttributes).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._ruleList = _ruleList;
            ((ASTNode) _ruleList).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_ruleNameWithAttributes);
            list.add(_produces);
            list.add(_ruleList);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof nonTerm)) return false;
            nonTerm other = (nonTerm) o;
            if (! _ruleNameWithAttributes.equals(other._ruleNameWithAttributes)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _ruleList.equals(other._ruleList)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_ruleNameWithAttributes.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_ruleList.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _ruleNameWithAttributes.accept(v);
                _produces.accept(v);
                _ruleList.accept(v);
            }
            v.endVisit(this);
        }

    void initialize() { symtab.addDefinition(_ruleNameWithAttributes.getSYMBOL().toString(), this); }
     }

    /**
     *<b>
     *<li>Rule 101:  ruleNameWithAttributes ::= SYMBOL
     *<li>Rule 102:  ruleNameWithAttributes ::= SYMBOL MACRO_NAME$className
     *<li>Rule 103:  ruleNameWithAttributes ::= SYMBOL MACRO_NAME$className MACRO_NAME$arrayElement
     *</b>
     */
    static public class RuleName extends ASTNode implements IruleNameWithAttributes
    {
        private ASTNodeToken _SYMBOL;
        private ASTNodeToken _className;
        private ASTNodeToken _arrayElement;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        /**
         * The value returned by <b>getclassName</b> may be <b>null</b>
         */
        public ASTNodeToken getclassName() { return _className; }
        /**
         * The value returned by <b>getarrayElement</b> may be <b>null</b>
         */
        public ASTNodeToken getarrayElement() { return _arrayElement; }

        public RuleName(IToken leftIToken, IToken rightIToken,
                        ASTNodeToken _SYMBOL,
                        ASTNodeToken _className,
                        ASTNodeToken _arrayElement)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._className = _className;
            if (_className != null) ((ASTNode) _className).setParent(this);
            this._arrayElement = _arrayElement;
            if (_arrayElement != null) ((ASTNode) _arrayElement).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_className);
            list.add(_arrayElement);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof RuleName)) return false;
            RuleName other = (RuleName) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (_className == null)
                if (other._className != null) return false;
                else; // continue
            else if (! _className.equals(other._className)) return false;
            if (_arrayElement == null)
                if (other._arrayElement != null) return false;
                else; // continue
            else if (! _arrayElement.equals(other._arrayElement)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_className == null ? 0 : _className.hashCode());
            hash = hash * 31 + (_arrayElement == null ? 0 : _arrayElement.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                if (_className != null) _className.accept(v);
                if (_arrayElement != null) _arrayElement.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 104:  ruleList ::= rule
     *<li>Rule 105:  ruleList ::= ruleList |$ rule
     *</b>
     */
    static public class ruleList extends AbstractASTNodeList implements IruleList
    {
        public rule getruleAt(int i) { return (rule) getElementAt(i); }

        public ruleList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public ruleList(rule _rule, boolean leftRecursive)
        {
            super((ASTNode) _rule, leftRecursive);
            ((ASTNode) _rule).setParent(this);
        }

        public void add(rule _rule)
        {
            super.add((ASTNode) _rule);
            ((ASTNode) _rule).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof ruleList)) return false;
            ruleList other = (ruleList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                rule element = getruleAt(i);
                    if (! element.equals(other.getruleAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getruleAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    rule element = getruleAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 110:  rule ::= symWithAttrsList opt_action_segment
     *</b>
     */
    static public class rule extends ASTNode implements Irule
    {
        private symWithAttrsList _symWithAttrsList;
        private action_segment _opt_action_segment;

        public symWithAttrsList getsymWithAttrsList() { return _symWithAttrsList; }
        /**
         * The value returned by <b>getopt_action_segment</b> may be <b>null</b>
         */
        public action_segment getopt_action_segment() { return _opt_action_segment; }

        public rule(IToken leftIToken, IToken rightIToken,
                    symWithAttrsList _symWithAttrsList,
                    action_segment _opt_action_segment)
        {
            super(leftIToken, rightIToken);

            this._symWithAttrsList = _symWithAttrsList;
            ((ASTNode) _symWithAttrsList).setParent(this);
            this._opt_action_segment = _opt_action_segment;
            if (_opt_action_segment != null) ((ASTNode) _opt_action_segment).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_symWithAttrsList);
            list.add(_opt_action_segment);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof rule)) return false;
            rule other = (rule) o;
            if (! _symWithAttrsList.equals(other._symWithAttrsList)) return false;
            if (_opt_action_segment == null)
                if (other._opt_action_segment != null) return false;
                else; // continue
            else if (! _opt_action_segment.equals(other._opt_action_segment)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_symWithAttrsList.hashCode());
            hash = hash * 31 + (_opt_action_segment == null ? 0 : _opt_action_segment.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _symWithAttrsList.accept(v);
                if (_opt_action_segment != null) _opt_action_segment.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 111:  symWithAttrsList ::= $Empty
     *<li>Rule 112:  symWithAttrsList ::= symWithAttrsList symWithAttrs
     *</b>
     */
    static public class symWithAttrsList extends AbstractASTNodeList implements IsymWithAttrsList
    {
        public IsymWithAttrs getsymWithAttrsAt(int i) { return (IsymWithAttrs) getElementAt(i); }

        public symWithAttrsList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public symWithAttrsList(IsymWithAttrs _symWithAttrs, boolean leftRecursive)
        {
            super((ASTNode) _symWithAttrs, leftRecursive);
            ((ASTNode) _symWithAttrs).setParent(this);
        }

        public void add(IsymWithAttrs _symWithAttrs)
        {
            super.add((ASTNode) _symWithAttrs);
            ((ASTNode) _symWithAttrs).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof symWithAttrsList)) return false;
            symWithAttrsList other = (symWithAttrsList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                IsymWithAttrs element = getsymWithAttrsAt(i);
                    if (! element.equals(other.getsymWithAttrsAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getsymWithAttrsAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    IsymWithAttrs element = getsymWithAttrsAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 115:  optAttrList ::= $Empty
     *<li>Rule 116:  optAttrList ::= MACRO_NAME
     *</b>
     */
    static public class symAttrs extends ASTNode implements IoptAttrList
    {
        private ASTNodeToken _MACRO_NAME;

        /**
         * The value returned by <b>getMACRO_NAME</b> may be <b>null</b>
         */
        public ASTNodeToken getMACRO_NAME() { return _MACRO_NAME; }

        public symAttrs(IToken leftIToken, IToken rightIToken,
                        ASTNodeToken _MACRO_NAME)
        {
            super(leftIToken, rightIToken);

            this._MACRO_NAME = _MACRO_NAME;
            if (_MACRO_NAME != null) ((ASTNode) _MACRO_NAME).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_MACRO_NAME);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof symAttrs)) return false;
            symAttrs other = (symAttrs) o;
            if (_MACRO_NAME == null)
                if (other._MACRO_NAME != null) return false;
                else; // continue
            else if (! _MACRO_NAME.equals(other._MACRO_NAME)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_MACRO_NAME == null ? 0 : _MACRO_NAME.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                if (_MACRO_NAME != null) _MACRO_NAME.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 119:  action_segment ::= BLOCK
     *</b>
     */
    static public class action_segment extends ASTNodeToken implements Iaction_segment
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        public IToken getBLOCK() { return leftIToken; }

        public action_segment(LPGParser environment, IToken token)        {
            super(token);
            this.environment = environment;
            initialize();
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }

        private JavaParser.Ast ast;
        public JavaParser.Ast getAst() { return ast; }
        public void setAst(JavaParser.Ast ast) { this.ast = ast; }
    }

    /**
     *<b>
     *<li>Rule 120:  start_segment ::= start_symbol
     *<li>Rule 121:  start_segment ::= start_segment start_symbol
     *</b>
     */
    static public class start_symbolList extends AbstractASTNodeList implements Istart_segment
    {
        public Istart_symbol getstart_symbolAt(int i) { return (Istart_symbol) getElementAt(i); }

        public start_symbolList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public start_symbolList(Istart_symbol _start_symbol, boolean leftRecursive)
        {
            super((ASTNode) _start_symbol, leftRecursive);
            ((ASTNode) _start_symbol).setParent(this);
        }

        public void add(Istart_symbol _start_symbol)
        {
            super.add((ASTNode) _start_symbol);
            ((ASTNode) _start_symbol).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof start_symbolList)) return false;
            start_symbolList other = (start_symbolList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                Istart_symbol element = getstart_symbolAt(i);
                    if (! element.equals(other.getstart_symbolAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getstart_symbolAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    Istart_symbol element = getstart_symbolAt(i);
                    element.accept(v);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 124:  terminals_segment ::= terminal
     *<li>Rule 125:  terminals_segment ::= terminals_segment terminal
     *</b>
     */
    static public class terminalList extends AbstractASTNodeList implements Iterminals_segment
    {
        public terminal getterminalAt(int i) { return (terminal) getElementAt(i); }

        public terminalList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public terminalList(terminal _terminal, boolean leftRecursive)
        {
            super((ASTNode) _terminal, leftRecursive);
            ((ASTNode) _terminal).setParent(this);
        }

        public void add(terminal _terminal)
        {
            super.add((ASTNode) _terminal);
            ((ASTNode) _terminal).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof terminalList)) return false;
            terminalList other = (terminalList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                terminal element = getterminalAt(i);
                    if (! element.equals(other.getterminalAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getterminalAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    terminal element = getterminalAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 126:  terminal ::= terminal_symbol optTerminalAlias
     *</b>
     */
    static public class terminal extends ASTNode implements Iterminal
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        private Iterminal_symbol _terminal_symbol;
        private optTerminalAlias _optTerminalAlias;

        public Iterminal_symbol getterminal_symbol() { return _terminal_symbol; }
        /**
         * The value returned by <b>getoptTerminalAlias</b> may be <b>null</b>
         */
        public optTerminalAlias getoptTerminalAlias() { return _optTerminalAlias; }

        public terminal(LPGParser environment, IToken leftIToken, IToken rightIToken,
                        Iterminal_symbol _terminal_symbol,
                        optTerminalAlias _optTerminalAlias)
        {
            super(leftIToken, rightIToken);

            this.environment = environment;
            this._terminal_symbol = _terminal_symbol;
            ((ASTNode) _terminal_symbol).setParent(this);
            this._optTerminalAlias = _optTerminalAlias;
            if (_optTerminalAlias != null) ((ASTNode) _optTerminalAlias).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_terminal_symbol);
            list.add(_optTerminalAlias);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof terminal)) return false;
            terminal other = (terminal) o;
            if (! _terminal_symbol.equals(other._terminal_symbol)) return false;
            if (_optTerminalAlias == null)
                if (other._optTerminalAlias != null) return false;
                else; // continue
            else if (! _optTerminalAlias.equals(other._optTerminalAlias)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_terminal_symbol.hashCode());
            hash = hash * 31 + (_optTerminalAlias == null ? 0 : _optTerminalAlias.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _terminal_symbol.accept(v);
                if (_optTerminalAlias != null) _optTerminalAlias.accept(v);
            }
            v.endVisit(this);
        }

    void initialize() { symtab.addDefinition(_terminal_symbol.toString(), this); }
     }

    /**
     *<em>
     *<li>Rule 127:  optTerminalAlias ::= $Empty
     *</em>
     *<p>
     *<b>
     *<li>Rule 128:  optTerminalAlias ::= produces name
     *</b>
     */
    static public class optTerminalAlias extends ASTNode implements IoptTerminalAlias
    {
        private Iproduces _produces;
        private Iname _name;

        public Iproduces getproduces() { return _produces; }
        public Iname getname() { return _name; }

        public optTerminalAlias(IToken leftIToken, IToken rightIToken,
                                Iproduces _produces,
                                Iname _name)
        {
            super(leftIToken, rightIToken);

            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._name = _name;
            ((ASTNode) _name).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_produces);
            list.add(_name);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof optTerminalAlias)) return false;
            optTerminalAlias other = (optTerminalAlias) o;
            if (! _produces.equals(other._produces)) return false;
            if (! _name.equals(other._name)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_name.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _produces.accept(v);
                _name.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 132:  types_segment ::= type_declarations
     *<li>Rule 133:  types_segment ::= types_segment type_declarations
     *</b>
     */
    static public class type_declarationsList extends AbstractASTNodeList implements Itypes_segment
    {
        public type_declarations gettype_declarationsAt(int i) { return (type_declarations) getElementAt(i); }

        public type_declarationsList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public type_declarationsList(type_declarations _type_declarations, boolean leftRecursive)
        {
            super((ASTNode) _type_declarations, leftRecursive);
            ((ASTNode) _type_declarations).setParent(this);
        }

        public void add(type_declarations _type_declarations)
        {
            super.add((ASTNode) _type_declarations);
            ((ASTNode) _type_declarations).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof type_declarationsList)) return false;
            type_declarationsList other = (type_declarationsList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                type_declarations element = gettype_declarationsAt(i);
                    if (! element.equals(other.gettype_declarationsAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (gettype_declarationsAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    type_declarations element = gettype_declarationsAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 134:  type_declarations ::= SYMBOL produces barSymbolList
     *</b>
     */
    static public class type_declarations extends ASTNode implements Itype_declarations
    {
        private ASTNodeToken _SYMBOL;
        private Iproduces _produces;
        private SYMBOLList _barSymbolList;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        public Iproduces getproduces() { return _produces; }
        public SYMBOLList getbarSymbolList() { return _barSymbolList; }

        public type_declarations(IToken leftIToken, IToken rightIToken,
                                 ASTNodeToken _SYMBOL,
                                 Iproduces _produces,
                                 SYMBOLList _barSymbolList)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._barSymbolList = _barSymbolList;
            ((ASTNode) _barSymbolList).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_produces);
            list.add(_barSymbolList);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof type_declarations)) return false;
            type_declarations other = (type_declarations) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _barSymbolList.equals(other._barSymbolList)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_barSymbolList.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                _produces.accept(v);
                _barSymbolList.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 137:  predecessor_segment ::= $Empty
     *<li>Rule 138:  predecessor_segment ::= predecessor_segment symbol_pair
     *</b>
     */
    static public class symbol_pairList extends AbstractASTNodeList implements Ipredecessor_segment
    {
        public symbol_pair getsymbol_pairAt(int i) { return (symbol_pair) getElementAt(i); }

        public symbol_pairList(IToken leftIToken, IToken rightIToken, boolean leftRecursive)
        {
            super(leftIToken, rightIToken, leftRecursive);
        }

        public symbol_pairList(symbol_pair _symbol_pair, boolean leftRecursive)
        {
            super((ASTNode) _symbol_pair, leftRecursive);
            ((ASTNode) _symbol_pair).setParent(this);
        }

        public void add(symbol_pair _symbol_pair)
        {
            super.add((ASTNode) _symbol_pair);
            ((ASTNode) _symbol_pair).setParent(this);
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            if (! (o instanceof symbol_pairList)) return false;
            symbol_pairList other = (symbol_pairList    ) o;
            if (size() != other.size()) return false;
            for (int i = 0; i < size(); i++)
            {
                symbol_pair element = getsymbol_pairAt(i);
                    if (! element.equals(other.getsymbol_pairAt(i))) return false;
            }
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            for (int i = 0; i < size(); i++)
                hash = hash * 31 + (getsymbol_pairAt(i).hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }
        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                for (int i = 0; i < size(); i++)
                {
                    symbol_pair element = getsymbol_pairAt(i);
                    if (! v.preVisit(element)) continue;
                    element.enter(v);
                    v.postVisit(element);
                }
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 139:  symbol_pair ::= SYMBOL SYMBOL
     *</b>
     */
    static public class symbol_pair extends ASTNode implements Isymbol_pair
    {
        private ASTNodeToken _SYMBOL;
        private ASTNodeToken _SYMBOL2;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        public ASTNodeToken getSYMBOL2() { return _SYMBOL2; }

        public symbol_pair(IToken leftIToken, IToken rightIToken,
                           ASTNodeToken _SYMBOL,
                           ASTNodeToken _SYMBOL2)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._SYMBOL2 = _SYMBOL2;
            ((ASTNode) _SYMBOL2).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_SYMBOL2);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof symbol_pair)) return false;
            symbol_pair other = (symbol_pair) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (! _SYMBOL2.equals(other._SYMBOL2)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_SYMBOL2.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                _SYMBOL2.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 142:  recover_symbol ::= SYMBOL
     *</b>
     */
    static public class recover_symbol extends ASTNodeToken implements Irecover_symbol
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        public IToken getSYMBOL() { return leftIToken; }

        public recover_symbol(LPGParser environment, IToken token)        {
            super(token);
            this.environment = environment;
            initialize();
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }

    void initialize() {
       symtab.addDefinition(getSYMBOL().toString(), this);
    }
     }

    /**
     *<em>
     *<li>Rule 143:  END_KEY_OPT ::= $Empty
     *</em>
     *<p>
     *<b>
     *<li>Rule 144:  END_KEY_OPT ::= END_KEY
     *</b>
     */
    static public class END_KEY_OPT extends ASTNodeToken implements IEND_KEY_OPT
    {
        public IToken getEND_KEY() { return leftIToken; }

        public END_KEY_OPT(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 33:  option_value ::= =$ SYMBOL
     *</b>
     */
    static public class option_value0 extends ASTNode implements Ioption_value
    {
        private ASTNodeToken _SYMBOL;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }

        public option_value0(IToken leftIToken, IToken rightIToken,
                             ASTNodeToken _SYMBOL)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof option_value0)) return false;
            option_value0 other = (option_value0) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _SYMBOL.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 34:  option_value ::= =$ ($ symbol_list )$
     *</b>
     */
    static public class option_value1 extends ASTNode implements Ioption_value
    {
        private SYMBOLList _symbol_list;

        public SYMBOLList getsymbol_list() { return _symbol_list; }

        public option_value1(IToken leftIToken, IToken rightIToken,
                             SYMBOLList _symbol_list)
        {
            super(leftIToken, rightIToken);

            this._symbol_list = _symbol_list;
            ((ASTNode) _symbol_list).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_symbol_list);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof option_value1)) return false;
            option_value1 other = (option_value1) o;
            if (! _symbol_list.equals(other._symbol_list)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_symbol_list.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
                _symbol_list.accept(v);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 39:  aliasSpec ::= ERROR_KEY produces alias_rhs
     *</b>
     */
    static public class aliasSpec0 extends ASTNode implements IaliasSpec
    {
        private ASTNodeToken _ERROR_KEY;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public ASTNodeToken getERROR_KEY() { return _ERROR_KEY; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec0(IToken leftIToken, IToken rightIToken,
                          ASTNodeToken _ERROR_KEY,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._ERROR_KEY = _ERROR_KEY;
            ((ASTNode) _ERROR_KEY).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_ERROR_KEY);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec0)) return false;
            aliasSpec0 other = (aliasSpec0) o;
            if (! _ERROR_KEY.equals(other._ERROR_KEY)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_ERROR_KEY.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _ERROR_KEY.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 40:  aliasSpec ::= EOL_KEY produces alias_rhs
     *</b>
     */
    static public class aliasSpec1 extends ASTNode implements IaliasSpec
    {
        private ASTNodeToken _EOL_KEY;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public ASTNodeToken getEOL_KEY() { return _EOL_KEY; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec1(IToken leftIToken, IToken rightIToken,
                          ASTNodeToken _EOL_KEY,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._EOL_KEY = _EOL_KEY;
            ((ASTNode) _EOL_KEY).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_EOL_KEY);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec1)) return false;
            aliasSpec1 other = (aliasSpec1) o;
            if (! _EOL_KEY.equals(other._EOL_KEY)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_EOL_KEY.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _EOL_KEY.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 41:  aliasSpec ::= EOF_KEY produces alias_rhs
     *</b>
     */
    static public class aliasSpec2 extends ASTNode implements IaliasSpec
    {
        private ASTNodeToken _EOF_KEY;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public ASTNodeToken getEOF_KEY() { return _EOF_KEY; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec2(IToken leftIToken, IToken rightIToken,
                          ASTNodeToken _EOF_KEY,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._EOF_KEY = _EOF_KEY;
            ((ASTNode) _EOF_KEY).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_EOF_KEY);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec2)) return false;
            aliasSpec2 other = (aliasSpec2) o;
            if (! _EOF_KEY.equals(other._EOF_KEY)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_EOF_KEY.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _EOF_KEY.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 42:  aliasSpec ::= IDENTIFIER_KEY produces alias_rhs
     *</b>
     */
    static public class aliasSpec3 extends ASTNode implements IaliasSpec
    {
        private ASTNodeToken _IDENTIFIER_KEY;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public ASTNodeToken getIDENTIFIER_KEY() { return _IDENTIFIER_KEY; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec3(IToken leftIToken, IToken rightIToken,
                          ASTNodeToken _IDENTIFIER_KEY,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._IDENTIFIER_KEY = _IDENTIFIER_KEY;
            ((ASTNode) _IDENTIFIER_KEY).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_IDENTIFIER_KEY);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec3)) return false;
            aliasSpec3 other = (aliasSpec3) o;
            if (! _IDENTIFIER_KEY.equals(other._IDENTIFIER_KEY)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_IDENTIFIER_KEY.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _IDENTIFIER_KEY.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 43:  aliasSpec ::= SYMBOL produces alias_rhs
     *</b>
     */
    static public class aliasSpec4 extends ASTNode implements IaliasSpec
    {
        private ASTNodeToken _SYMBOL;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec4(IToken leftIToken, IToken rightIToken,
                          ASTNodeToken _SYMBOL,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec4)) return false;
            aliasSpec4 other = (aliasSpec4) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 44:  aliasSpec ::= alias_lhs_macro_name produces alias_rhs
     *</b>
     */
    static public class aliasSpec5 extends ASTNode implements IaliasSpec
    {
        private alias_lhs_macro_name _alias_lhs_macro_name;
        private Iproduces _produces;
        private Ialias_rhs _alias_rhs;

        public alias_lhs_macro_name getalias_lhs_macro_name() { return _alias_lhs_macro_name; }
        public Iproduces getproduces() { return _produces; }
        public Ialias_rhs getalias_rhs() { return _alias_rhs; }

        public aliasSpec5(IToken leftIToken, IToken rightIToken,
                          alias_lhs_macro_name _alias_lhs_macro_name,
                          Iproduces _produces,
                          Ialias_rhs _alias_rhs)
        {
            super(leftIToken, rightIToken);

            this._alias_lhs_macro_name = _alias_lhs_macro_name;
            ((ASTNode) _alias_lhs_macro_name).setParent(this);
            this._produces = _produces;
            ((ASTNode) _produces).setParent(this);
            this._alias_rhs = _alias_rhs;
            ((ASTNode) _alias_rhs).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_alias_lhs_macro_name);
            list.add(_produces);
            list.add(_alias_rhs);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof aliasSpec5)) return false;
            aliasSpec5 other = (aliasSpec5) o;
            if (! _alias_lhs_macro_name.equals(other._alias_lhs_macro_name)) return false;
            if (! _produces.equals(other._produces)) return false;
            if (! _alias_rhs.equals(other._alias_rhs)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_alias_lhs_macro_name.hashCode());
            hash = hash * 31 + (_produces.hashCode());
            hash = hash * 31 + (_alias_rhs.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _alias_lhs_macro_name.accept(v);
                _produces.accept(v);
                _alias_rhs.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 46:  alias_rhs ::= SYMBOL
     *</b>
     */
    static public class alias_rhs0 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getSYMBOL() { return leftIToken; }

        public alias_rhs0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 47:  alias_rhs ::= MACRO_NAME
     *</b>
     */
    static public class alias_rhs1 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public alias_rhs1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 48:  alias_rhs ::= ERROR_KEY
     *</b>
     */
    static public class alias_rhs2 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getERROR_KEY() { return leftIToken; }

        public alias_rhs2(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 49:  alias_rhs ::= EOL_KEY
     *</b>
     */
    static public class alias_rhs3 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getEOL_KEY() { return leftIToken; }

        public alias_rhs3(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 50:  alias_rhs ::= EOF_KEY
     *</b>
     */
    static public class alias_rhs4 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getEOF_KEY() { return leftIToken; }

        public alias_rhs4(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 51:  alias_rhs ::= EMPTY_KEY
     *</b>
     */
    static public class alias_rhs5 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getEMPTY_KEY() { return leftIToken; }

        public alias_rhs5(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 52:  alias_rhs ::= IDENTIFIER_KEY
     *</b>
     */
    static public class alias_rhs6 extends ASTNodeToken implements Ialias_rhs
    {
        public IToken getIDENTIFIER_KEY() { return leftIToken; }

        public alias_rhs6(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 57:  macro_name_symbol ::= MACRO_NAME
     *</b>
     */
    static public class macro_name_symbol0 extends ASTNodeToken implements Imacro_name_symbol
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public macro_name_symbol0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 58:  macro_name_symbol ::= SYMBOL
     *</b>
     */
    static public class macro_name_symbol1 extends ASTNodeToken implements Imacro_name_symbol
    {
        public IToken getSYMBOL() { return leftIToken; }

        public macro_name_symbol1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 72:  drop_command ::= DROPSYMBOLS_KEY drop_symbols
     *</b>
     */
    static public class drop_command0 extends ASTNode implements Idrop_command
    {
        private ASTNodeToken _DROPSYMBOLS_KEY;
        private SYMBOLList _drop_symbols;

        public ASTNodeToken getDROPSYMBOLS_KEY() { return _DROPSYMBOLS_KEY; }
        public SYMBOLList getdrop_symbols() { return _drop_symbols; }

        public drop_command0(IToken leftIToken, IToken rightIToken,
                             ASTNodeToken _DROPSYMBOLS_KEY,
                             SYMBOLList _drop_symbols)
        {
            super(leftIToken, rightIToken);

            this._DROPSYMBOLS_KEY = _DROPSYMBOLS_KEY;
            ((ASTNode) _DROPSYMBOLS_KEY).setParent(this);
            this._drop_symbols = _drop_symbols;
            ((ASTNode) _drop_symbols).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_DROPSYMBOLS_KEY);
            list.add(_drop_symbols);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof drop_command0)) return false;
            drop_command0 other = (drop_command0) o;
            if (! _DROPSYMBOLS_KEY.equals(other._DROPSYMBOLS_KEY)) return false;
            if (! _drop_symbols.equals(other._drop_symbols)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_DROPSYMBOLS_KEY.hashCode());
            hash = hash * 31 + (_drop_symbols.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _DROPSYMBOLS_KEY.accept(v);
                _drop_symbols.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 73:  drop_command ::= DROPRULES_KEY drop_rules
     *</b>
     */
    static public class drop_command1 extends ASTNode implements Idrop_command
    {
        private ASTNodeToken _DROPRULES_KEY;
        private drop_ruleList _drop_rules;

        public ASTNodeToken getDROPRULES_KEY() { return _DROPRULES_KEY; }
        public drop_ruleList getdrop_rules() { return _drop_rules; }

        public drop_command1(IToken leftIToken, IToken rightIToken,
                             ASTNodeToken _DROPRULES_KEY,
                             drop_ruleList _drop_rules)
        {
            super(leftIToken, rightIToken);

            this._DROPRULES_KEY = _DROPRULES_KEY;
            ((ASTNode) _DROPRULES_KEY).setParent(this);
            this._drop_rules = _drop_rules;
            ((ASTNode) _drop_rules).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_DROPRULES_KEY);
            list.add(_drop_rules);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof drop_command1)) return false;
            drop_command1 other = (drop_command1) o;
            if (! _DROPRULES_KEY.equals(other._DROPRULES_KEY)) return false;
            if (! _drop_rules.equals(other._drop_rules)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_DROPRULES_KEY.hashCode());
            hash = hash * 31 + (_drop_rules.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _DROPRULES_KEY.accept(v);
                _drop_rules.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 89:  name ::= SYMBOL
     *</b>
     */
    static public class name0 extends ASTNodeToken implements Iname
    {
        public IToken getSYMBOL() { return leftIToken; }

        public name0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 90:  name ::= MACRO_NAME
     *</b>
     */
    static public class name1 extends ASTNodeToken implements Iname
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public name1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 91:  name ::= EMPTY_KEY
     *</b>
     */
    static public class name2 extends ASTNodeToken implements Iname
    {
        public IToken getEMPTY_KEY() { return leftIToken; }

        public name2(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 92:  name ::= ERROR_KEY
     *</b>
     */
    static public class name3 extends ASTNodeToken implements Iname
    {
        public IToken getERROR_KEY() { return leftIToken; }

        public name3(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 93:  name ::= EOL_KEY
     *</b>
     */
    static public class name4 extends ASTNodeToken implements Iname
    {
        public IToken getEOL_KEY() { return leftIToken; }

        public name4(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 94:  name ::= IDENTIFIER_KEY
     *</b>
     */
    static public class name5 extends ASTNodeToken implements Iname
    {
        public IToken getIDENTIFIER_KEY() { return leftIToken; }

        public name5(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 106:  produces ::= ::=
     *</b>
     */
    static public class produces0 extends ASTNodeToken implements Iproduces
    {
        public IToken getEQUIVALENCE() { return leftIToken; }

        public produces0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 107:  produces ::= ::=?
     *</b>
     */
    static public class produces1 extends ASTNodeToken implements Iproduces
    {
        public IToken getPRIORITY_EQUIVALENCE() { return leftIToken; }

        public produces1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 108:  produces ::= ->
     *</b>
     */
    static public class produces2 extends ASTNodeToken implements Iproduces
    {
        public IToken getARROW() { return leftIToken; }

        public produces2(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 109:  produces ::= ->?
     *</b>
     */
    static public class produces3 extends ASTNodeToken implements Iproduces
    {
        public IToken getPRIORITY_ARROW() { return leftIToken; }

        public produces3(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 113:  symWithAttrs ::= EMPTY_KEY
     *</b>
     */
    static public class symWithAttrs0 extends ASTNodeToken implements IsymWithAttrs
    {
        public IToken getEMPTY_KEY() { return leftIToken; }

        public symWithAttrs0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 114:  symWithAttrs ::= SYMBOL optAttrList
     *</b>
     */
    static public class symWithAttrs1 extends ASTNode implements IsymWithAttrs
    {
        private ASTNodeToken _SYMBOL;
        private symAttrs _optAttrList;

        public ASTNodeToken getSYMBOL() { return _SYMBOL; }
        /**
         * The value returned by <b>getoptAttrList</b> may be <b>null</b>
         */
        public symAttrs getoptAttrList() { return _optAttrList; }

        public symWithAttrs1(IToken leftIToken, IToken rightIToken,
                             ASTNodeToken _SYMBOL,
                             symAttrs _optAttrList)
        {
            super(leftIToken, rightIToken);

            this._SYMBOL = _SYMBOL;
            ((ASTNode) _SYMBOL).setParent(this);
            this._optAttrList = _optAttrList;
            if (_optAttrList != null) ((ASTNode) _optAttrList).setParent(this);
            initialize();
        }

        /**
         * A list of all children of this node, including the null ones.
         */
        public java.util.ArrayList getAllChildren()
        {
            java.util.ArrayList list = new java.util.ArrayList();
            list.add(_SYMBOL);
            list.add(_optAttrList);
            return list;
        }

        public boolean equals(Object o)
        {
            if (o == this) return true;
            //
            // The super call test is not required for now because an Ast node
            // can only extend the root Ast, AstToken and AstList and none of
            // these nodes contain additional children.
            //
            // if (! super.equals(o)) return false;
            //
            if (! (o instanceof symWithAttrs1)) return false;
            symWithAttrs1 other = (symWithAttrs1) o;
            if (! _SYMBOL.equals(other._SYMBOL)) return false;
            if (_optAttrList == null)
                if (other._optAttrList != null) return false;
                else; // continue
            else if (! _optAttrList.equals(other._optAttrList)) return false;
            return true;
        }

        public int hashCode()
        {
            int hash = 7;
            hash = hash * 31 + (_SYMBOL.hashCode());
            hash = hash * 31 + (_optAttrList == null ? 0 : _optAttrList.hashCode());
            return hash;
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            boolean checkChildren = v.visit(this);
            if (checkChildren)
            {
                _SYMBOL.accept(v);
                if (_optAttrList != null) _optAttrList.accept(v);
            }
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 122:  start_symbol ::= SYMBOL
     *</b>
     */
    static public class start_symbol0 extends ASTNodeToken implements Istart_symbol
    {
        public IToken getSYMBOL() { return leftIToken; }

        public start_symbol0(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 123:  start_symbol ::= MACRO_NAME
     *</b>
     */
    static public class start_symbol1 extends ASTNodeToken implements Istart_symbol
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public start_symbol1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    /**
     *<b>
     *<li>Rule 129:  terminal_symbol ::= SYMBOL
     *</b>
     */
    static public class terminal_symbol0 extends ASTNodeToken implements Iterminal_symbol
    {
        private LPGParser environment;
        public LPGParser getEnvironment() { return environment; }

        public IToken getSYMBOL() { return leftIToken; }

        public terminal_symbol0(LPGParser environment, IToken token)        {
            super(token);
            this.environment = environment;
            initialize();
        }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }

    void initialize() { symtab.addDefinition(getSYMBOL().toString(), this); }
     }

    /**
     *<b>
     *<li>Rule 130:  terminal_symbol ::= MACRO_NAME
     *</b>
     */
    static public class terminal_symbol1 extends ASTNodeToken implements Iterminal_symbol
    {
        public IToken getMACRO_NAME() { return leftIToken; }

        public terminal_symbol1(IToken token) { super(token); initialize(); }

        public void accept(IAstVisitor v)
        {
            if (! v.preVisit(this)) return;
            enter((Visitor) v);
            v.postVisit(this);
        }

        public void enter(Visitor v)
        {
            v.visit(this);
            v.endVisit(this);
        }
    }

    public interface Visitor extends IAstVisitor
    {
        boolean visit(ASTNode n);
        void endVisit(ASTNode n);

        boolean visit(ASTNodeToken n);
        void endVisit(ASTNodeToken n);

        boolean visit(JikesPG n);
        void endVisit(JikesPG n);

        boolean visit(JikesPG_itemList n);
        void endVisit(JikesPG_itemList n);

        boolean visit(AliasSeg n);
        void endVisit(AliasSeg n);

        boolean visit(AstSeg n);
        void endVisit(AstSeg n);

        boolean visit(DefineSeg n);
        void endVisit(DefineSeg n);

        boolean visit(EofSeg n);
        void endVisit(EofSeg n);

        boolean visit(EolSeg n);
        void endVisit(EolSeg n);

        boolean visit(ErrorSeg n);
        void endVisit(ErrorSeg n);

        boolean visit(ExportSeg n);
        void endVisit(ExportSeg n);

        boolean visit(GlobalsSeg n);
        void endVisit(GlobalsSeg n);

        boolean visit(HeadersSeg n);
        void endVisit(HeadersSeg n);

        boolean visit(IdentifierSeg n);
        void endVisit(IdentifierSeg n);

        boolean visit(ImportSeg n);
        void endVisit(ImportSeg n);

        boolean visit(IncludeSeg n);
        void endVisit(IncludeSeg n);

        boolean visit(KeywordsSeg n);
        void endVisit(KeywordsSeg n);

        boolean visit(NamesSeg n);
        void endVisit(NamesSeg n);

        boolean visit(NoticeSeg n);
        void endVisit(NoticeSeg n);

        boolean visit(RulesSeg n);
        void endVisit(RulesSeg n);

        boolean visit(StartSeg n);
        void endVisit(StartSeg n);

        boolean visit(TerminalsSeg n);
        void endVisit(TerminalsSeg n);

        boolean visit(TrailersSeg n);
        void endVisit(TrailersSeg n);

        boolean visit(TypesSeg n);
        void endVisit(TypesSeg n);

        boolean visit(RecoverSeg n);
        void endVisit(RecoverSeg n);

        boolean visit(PredecessorSeg n);
        void endVisit(PredecessorSeg n);

        boolean visit(option_specList n);
        void endVisit(option_specList n);

        boolean visit(option_spec n);
        void endVisit(option_spec n);

        boolean visit(optionList n);
        void endVisit(optionList n);

        boolean visit(option n);
        void endVisit(option n);

        boolean visit(SYMBOLList n);
        void endVisit(SYMBOLList n);

        boolean visit(aliasSpecList n);
        void endVisit(aliasSpecList n);

        boolean visit(alias_lhs_macro_name n);
        void endVisit(alias_lhs_macro_name n);

        boolean visit(defineSpecList n);
        void endVisit(defineSpecList n);

        boolean visit(defineSpec n);
        void endVisit(defineSpec n);

        boolean visit(macro_segment n);
        void endVisit(macro_segment n);

        boolean visit(terminal_symbolList n);
        void endVisit(terminal_symbolList n);

        boolean visit(action_segmentList n);
        void endVisit(action_segmentList n);

        boolean visit(import_segment n);
        void endVisit(import_segment n);

        boolean visit(drop_commandList n);
        void endVisit(drop_commandList n);

        boolean visit(drop_ruleList n);
        void endVisit(drop_ruleList n);

        boolean visit(drop_rule n);
        void endVisit(drop_rule n);

        boolean visit(optMacroName n);
        void endVisit(optMacroName n);

        boolean visit(include_segment n);
        void endVisit(include_segment n);

        boolean visit(keywordSpecList n);
        void endVisit(keywordSpecList n);

        boolean visit(keywordSpec n);
        void endVisit(keywordSpec n);

        boolean visit(nameSpecList n);
        void endVisit(nameSpecList n);

        boolean visit(nameSpec n);
        void endVisit(nameSpec n);

        boolean visit(rules_segment n);
        void endVisit(rules_segment n);

        boolean visit(nonTermList n);
        void endVisit(nonTermList n);

        boolean visit(nonTerm n);
        void endVisit(nonTerm n);

        boolean visit(RuleName n);
        void endVisit(RuleName n);

        boolean visit(ruleList n);
        void endVisit(ruleList n);

        boolean visit(rule n);
        void endVisit(rule n);

        boolean visit(symWithAttrsList n);
        void endVisit(symWithAttrsList n);

        boolean visit(symAttrs n);
        void endVisit(symAttrs n);

        boolean visit(action_segment n);
        void endVisit(action_segment n);

        boolean visit(start_symbolList n);
        void endVisit(start_symbolList n);

        boolean visit(terminalList n);
        void endVisit(terminalList n);

        boolean visit(terminal n);
        void endVisit(terminal n);

        boolean visit(optTerminalAlias n);
        void endVisit(optTerminalAlias n);

        boolean visit(type_declarationsList n);
        void endVisit(type_declarationsList n);

        boolean visit(type_declarations n);
        void endVisit(type_declarations n);

        boolean visit(symbol_pairList n);
        void endVisit(symbol_pairList n);

        boolean visit(symbol_pair n);
        void endVisit(symbol_pair n);

        boolean visit(recover_symbol n);
        void endVisit(recover_symbol n);

        boolean visit(END_KEY_OPT n);
        void endVisit(END_KEY_OPT n);

        boolean visit(option_value0 n);
        void endVisit(option_value0 n);

        boolean visit(option_value1 n);
        void endVisit(option_value1 n);

        boolean visit(aliasSpec0 n);
        void endVisit(aliasSpec0 n);

        boolean visit(aliasSpec1 n);
        void endVisit(aliasSpec1 n);

        boolean visit(aliasSpec2 n);
        void endVisit(aliasSpec2 n);

        boolean visit(aliasSpec3 n);
        void endVisit(aliasSpec3 n);

        boolean visit(aliasSpec4 n);
        void endVisit(aliasSpec4 n);

        boolean visit(aliasSpec5 n);
        void endVisit(aliasSpec5 n);

        boolean visit(alias_rhs0 n);
        void endVisit(alias_rhs0 n);

        boolean visit(alias_rhs1 n);
        void endVisit(alias_rhs1 n);

        boolean visit(alias_rhs2 n);
        void endVisit(alias_rhs2 n);

        boolean visit(alias_rhs3 n);
        void endVisit(alias_rhs3 n);

        boolean visit(alias_rhs4 n);
        void endVisit(alias_rhs4 n);

        boolean visit(alias_rhs5 n);
        void endVisit(alias_rhs5 n);

        boolean visit(alias_rhs6 n);
        void endVisit(alias_rhs6 n);

        boolean visit(macro_name_symbol0 n);
        void endVisit(macro_name_symbol0 n);

        boolean visit(macro_name_symbol1 n);
        void endVisit(macro_name_symbol1 n);

        boolean visit(drop_command0 n);
        void endVisit(drop_command0 n);

        boolean visit(drop_command1 n);
        void endVisit(drop_command1 n);

        boolean visit(name0 n);
        void endVisit(name0 n);

        boolean visit(name1 n);
        void endVisit(name1 n);

        boolean visit(name2 n);
        void endVisit(name2 n);

        boolean visit(name3 n);
        void endVisit(name3 n);

        boolean visit(name4 n);
        void endVisit(name4 n);

        boolean visit(name5 n);
        void endVisit(name5 n);

        boolean visit(produces0 n);
        void endVisit(produces0 n);

        boolean visit(produces1 n);
        void endVisit(produces1 n);

        boolean visit(produces2 n);
        void endVisit(produces2 n);

        boolean visit(produces3 n);
        void endVisit(produces3 n);

        boolean visit(symWithAttrs0 n);
        void endVisit(symWithAttrs0 n);

        boolean visit(symWithAttrs1 n);
        void endVisit(symWithAttrs1 n);

        boolean visit(start_symbol0 n);
        void endVisit(start_symbol0 n);

        boolean visit(start_symbol1 n);
        void endVisit(start_symbol1 n);

        boolean visit(terminal_symbol0 n);
        void endVisit(terminal_symbol0 n);

        boolean visit(terminal_symbol1 n);
        void endVisit(terminal_symbol1 n);

    }

    static public abstract class AbstractVisitor implements Visitor
    {
        public abstract void unimplementedVisitor(String s);

        public boolean preVisit(IAst element) { return true; }

        public void postVisit(IAst element) {}

        public boolean visit(ASTNodeToken n) { unimplementedVisitor("visit(ASTNodeToken)"); return true; }
        public void endVisit(ASTNodeToken n) { unimplementedVisitor("endVisit(ASTNodeToken)"); }

        public boolean visit(JikesPG n) { unimplementedVisitor("visit(JikesPG)"); return true; }
        public void endVisit(JikesPG n) { unimplementedVisitor("endVisit(JikesPG)"); }

        public boolean visit(JikesPG_itemList n) { unimplementedVisitor("visit(JikesPG_itemList)"); return true; }
        public void endVisit(JikesPG_itemList n) { unimplementedVisitor("endVisit(JikesPG_itemList)"); }

        public boolean visit(AliasSeg n) { unimplementedVisitor("visit(AliasSeg)"); return true; }
        public void endVisit(AliasSeg n) { unimplementedVisitor("endVisit(AliasSeg)"); }

        public boolean visit(AstSeg n) { unimplementedVisitor("visit(AstSeg)"); return true; }
        public void endVisit(AstSeg n) { unimplementedVisitor("endVisit(AstSeg)"); }

        public boolean visit(DefineSeg n) { unimplementedVisitor("visit(DefineSeg)"); return true; }
        public void endVisit(DefineSeg n) { unimplementedVisitor("endVisit(DefineSeg)"); }

        public boolean visit(EofSeg n) { unimplementedVisitor("visit(EofSeg)"); return true; }
        public void endVisit(EofSeg n) { unimplementedVisitor("endVisit(EofSeg)"); }

        public boolean visit(EolSeg n) { unimplementedVisitor("visit(EolSeg)"); return true; }
        public void endVisit(EolSeg n) { unimplementedVisitor("endVisit(EolSeg)"); }

        public boolean visit(ErrorSeg n) { unimplementedVisitor("visit(ErrorSeg)"); return true; }
        public void endVisit(ErrorSeg n) { unimplementedVisitor("endVisit(ErrorSeg)"); }

        public boolean visit(ExportSeg n) { unimplementedVisitor("visit(ExportSeg)"); return true; }
        public void endVisit(ExportSeg n) { unimplementedVisitor("endVisit(ExportSeg)"); }

        public boolean visit(GlobalsSeg n) { unimplementedVisitor("visit(GlobalsSeg)"); return true; }
        public void endVisit(GlobalsSeg n) { unimplementedVisitor("endVisit(GlobalsSeg)"); }

        public boolean visit(HeadersSeg n) { unimplementedVisitor("visit(HeadersSeg)"); return true; }
        public void endVisit(HeadersSeg n) { unimplementedVisitor("endVisit(HeadersSeg)"); }

        public boolean visit(IdentifierSeg n) { unimplementedVisitor("visit(IdentifierSeg)"); return true; }
        public void endVisit(IdentifierSeg n) { unimplementedVisitor("endVisit(IdentifierSeg)"); }

        public boolean visit(ImportSeg n) { unimplementedVisitor("visit(ImportSeg)"); return true; }
        public void endVisit(ImportSeg n) { unimplementedVisitor("endVisit(ImportSeg)"); }

        public boolean visit(IncludeSeg n) { unimplementedVisitor("visit(IncludeSeg)"); return true; }
        public void endVisit(IncludeSeg n) { unimplementedVisitor("endVisit(IncludeSeg)"); }

        public boolean visit(KeywordsSeg n) { unimplementedVisitor("visit(KeywordsSeg)"); return true; }
        public void endVisit(KeywordsSeg n) { unimplementedVisitor("endVisit(KeywordsSeg)"); }

        public boolean visit(NamesSeg n) { unimplementedVisitor("visit(NamesSeg)"); return true; }
        public void endVisit(NamesSeg n) { unimplementedVisitor("endVisit(NamesSeg)"); }

        public boolean visit(NoticeSeg n) { unimplementedVisitor("visit(NoticeSeg)"); return true; }
        public void endVisit(NoticeSeg n) { unimplementedVisitor("endVisit(NoticeSeg)"); }

        public boolean visit(RulesSeg n) { unimplementedVisitor("visit(RulesSeg)"); return true; }
        public void endVisit(RulesSeg n) { unimplementedVisitor("endVisit(RulesSeg)"); }

        public boolean visit(StartSeg n) { unimplementedVisitor("visit(StartSeg)"); return true; }
        public void endVisit(StartSeg n) { unimplementedVisitor("endVisit(StartSeg)"); }

        public boolean visit(TerminalsSeg n) { unimplementedVisitor("visit(TerminalsSeg)"); return true; }
        public void endVisit(TerminalsSeg n) { unimplementedVisitor("endVisit(TerminalsSeg)"); }

        public boolean visit(TrailersSeg n) { unimplementedVisitor("visit(TrailersSeg)"); return true; }
        public void endVisit(TrailersSeg n) { unimplementedVisitor("endVisit(TrailersSeg)"); }

        public boolean visit(TypesSeg n) { unimplementedVisitor("visit(TypesSeg)"); return true; }
        public void endVisit(TypesSeg n) { unimplementedVisitor("endVisit(TypesSeg)"); }

        public boolean visit(RecoverSeg n) { unimplementedVisitor("visit(RecoverSeg)"); return true; }
        public void endVisit(RecoverSeg n) { unimplementedVisitor("endVisit(RecoverSeg)"); }

        public boolean visit(PredecessorSeg n) { unimplementedVisitor("visit(PredecessorSeg)"); return true; }
        public void endVisit(PredecessorSeg n) { unimplementedVisitor("endVisit(PredecessorSeg)"); }

        public boolean visit(option_specList n) { unimplementedVisitor("visit(option_specList)"); return true; }
        public void endVisit(option_specList n) { unimplementedVisitor("endVisit(option_specList)"); }

        public boolean visit(option_spec n) { unimplementedVisitor("visit(option_spec)"); return true; }
        public void endVisit(option_spec n) { unimplementedVisitor("endVisit(option_spec)"); }

        public boolean visit(optionList n) { unimplementedVisitor("visit(optionList)"); return true; }
        public void endVisit(optionList n) { unimplementedVisitor("endVisit(optionList)"); }

        public boolean visit(option n) { unimplementedVisitor("visit(option)"); return true; }
        public void endVisit(option n) { unimplementedVisitor("endVisit(option)"); }

        public boolean visit(SYMBOLList n) { unimplementedVisitor("visit(SYMBOLList)"); return true; }
        public void endVisit(SYMBOLList n) { unimplementedVisitor("endVisit(SYMBOLList)"); }

        public boolean visit(aliasSpecList n) { unimplementedVisitor("visit(aliasSpecList)"); return true; }
        public void endVisit(aliasSpecList n) { unimplementedVisitor("endVisit(aliasSpecList)"); }

        public boolean visit(alias_lhs_macro_name n) { unimplementedVisitor("visit(alias_lhs_macro_name)"); return true; }
        public void endVisit(alias_lhs_macro_name n) { unimplementedVisitor("endVisit(alias_lhs_macro_name)"); }

        public boolean visit(defineSpecList n) { unimplementedVisitor("visit(defineSpecList)"); return true; }
        public void endVisit(defineSpecList n) { unimplementedVisitor("endVisit(defineSpecList)"); }

        public boolean visit(defineSpec n) { unimplementedVisitor("visit(defineSpec)"); return true; }
        public void endVisit(defineSpec n) { unimplementedVisitor("endVisit(defineSpec)"); }

        public boolean visit(macro_segment n) { unimplementedVisitor("visit(macro_segment)"); return true; }
        public void endVisit(macro_segment n) { unimplementedVisitor("endVisit(macro_segment)"); }

        public boolean visit(terminal_symbolList n) { unimplementedVisitor("visit(terminal_symbolList)"); return true; }
        public void endVisit(terminal_symbolList n) { unimplementedVisitor("endVisit(terminal_symbolList)"); }

        public boolean visit(action_segmentList n) { unimplementedVisitor("visit(action_segmentList)"); return true; }
        public void endVisit(action_segmentList n) { unimplementedVisitor("endVisit(action_segmentList)"); }

        public boolean visit(import_segment n) { unimplementedVisitor("visit(import_segment)"); return true; }
        public void endVisit(import_segment n) { unimplementedVisitor("endVisit(import_segment)"); }

        public boolean visit(drop_commandList n) { unimplementedVisitor("visit(drop_commandList)"); return true; }
        public void endVisit(drop_commandList n) { unimplementedVisitor("endVisit(drop_commandList)"); }

        public boolean visit(drop_ruleList n) { unimplementedVisitor("visit(drop_ruleList)"); return true; }
        public void endVisit(drop_ruleList n) { unimplementedVisitor("endVisit(drop_ruleList)"); }

        public boolean visit(drop_rule n) { unimplementedVisitor("visit(drop_rule)"); return true; }
        public void endVisit(drop_rule n) { unimplementedVisitor("endVisit(drop_rule)"); }

        public boolean visit(optMacroName n) { unimplementedVisitor("visit(optMacroName)"); return true; }
        public void endVisit(optMacroName n) { unimplementedVisitor("endVisit(optMacroName)"); }

        public boolean visit(include_segment n) { unimplementedVisitor("visit(include_segment)"); return true; }
        public void endVisit(include_segment n) { unimplementedVisitor("endVisit(include_segment)"); }

        public boolean visit(keywordSpecList n) { unimplementedVisitor("visit(keywordSpecList)"); return true; }
        public void endVisit(keywordSpecList n) { unimplementedVisitor("endVisit(keywordSpecList)"); }

        public boolean visit(keywordSpec n) { unimplementedVisitor("visit(keywordSpec)"); return true; }
        public void endVisit(keywordSpec n) { unimplementedVisitor("endVisit(keywordSpec)"); }

        public boolean visit(nameSpecList n) { unimplementedVisitor("visit(nameSpecList)"); return true; }
        public void endVisit(nameSpecList n) { unimplementedVisitor("endVisit(nameSpecList)"); }

        public boolean visit(nameSpec n) { unimplementedVisitor("visit(nameSpec)"); return true; }
        public void endVisit(nameSpec n) { unimplementedVisitor("endVisit(nameSpec)"); }

        public boolean visit(rules_segment n) { unimplementedVisitor("visit(rules_segment)"); return true; }
        public void endVisit(rules_segment n) { unimplementedVisitor("endVisit(rules_segment)"); }

        public boolean visit(nonTermList n) { unimplementedVisitor("visit(nonTermList)"); return true; }
        public void endVisit(nonTermList n) { unimplementedVisitor("endVisit(nonTermList)"); }

        public boolean visit(nonTerm n) { unimplementedVisitor("visit(nonTerm)"); return true; }
        public void endVisit(nonTerm n) { unimplementedVisitor("endVisit(nonTerm)"); }

        public boolean visit(RuleName n) { unimplementedVisitor("visit(RuleName)"); return true; }
        public void endVisit(RuleName n) { unimplementedVisitor("endVisit(RuleName)"); }

        public boolean visit(ruleList n) { unimplementedVisitor("visit(ruleList)"); return true; }
        public void endVisit(ruleList n) { unimplementedVisitor("endVisit(ruleList)"); }

        public boolean visit(rule n) { unimplementedVisitor("visit(rule)"); return true; }
        public void endVisit(rule n) { unimplementedVisitor("endVisit(rule)"); }

        public boolean visit(symWithAttrsList n) { unimplementedVisitor("visit(symWithAttrsList)"); return true; }
        public void endVisit(symWithAttrsList n) { unimplementedVisitor("endVisit(symWithAttrsList)"); }

        public boolean visit(symAttrs n) { unimplementedVisitor("visit(symAttrs)"); return true; }
        public void endVisit(symAttrs n) { unimplementedVisitor("endVisit(symAttrs)"); }

        public boolean visit(action_segment n) { unimplementedVisitor("visit(action_segment)"); return true; }
        public void endVisit(action_segment n) { unimplementedVisitor("endVisit(action_segment)"); }

        public boolean visit(start_symbolList n) { unimplementedVisitor("visit(start_symbolList)"); return true; }
        public void endVisit(start_symbolList n) { unimplementedVisitor("endVisit(start_symbolList)"); }

        public boolean visit(terminalList n) { unimplementedVisitor("visit(terminalList)"); return true; }
        public void endVisit(terminalList n) { unimplementedVisitor("endVisit(terminalList)"); }

        public boolean visit(terminal n) { unimplementedVisitor("visit(terminal)"); return true; }
        public void endVisit(terminal n) { unimplementedVisitor("endVisit(terminal)"); }

        public boolean visit(optTerminalAlias n) { unimplementedVisitor("visit(optTerminalAlias)"); return true; }
        public void endVisit(optTerminalAlias n) { unimplementedVisitor("endVisit(optTerminalAlias)"); }

        public boolean visit(type_declarationsList n) { unimplementedVisitor("visit(type_declarationsList)"); return true; }
        public void endVisit(type_declarationsList n) { unimplementedVisitor("endVisit(type_declarationsList)"); }

        public boolean visit(type_declarations n) { unimplementedVisitor("visit(type_declarations)"); return true; }
        public void endVisit(type_declarations n) { unimplementedVisitor("endVisit(type_declarations)"); }

        public boolean visit(symbol_pairList n) { unimplementedVisitor("visit(symbol_pairList)"); return true; }
        public void endVisit(symbol_pairList n) { unimplementedVisitor("endVisit(symbol_pairList)"); }

        public boolean visit(symbol_pair n) { unimplementedVisitor("visit(symbol_pair)"); return true; }
        public void endVisit(symbol_pair n) { unimplementedVisitor("endVisit(symbol_pair)"); }

        public boolean visit(recover_symbol n) { unimplementedVisitor("visit(recover_symbol)"); return true; }
        public void endVisit(recover_symbol n) { unimplementedVisitor("endVisit(recover_symbol)"); }

        public boolean visit(END_KEY_OPT n) { unimplementedVisitor("visit(END_KEY_OPT)"); return true; }
        public void endVisit(END_KEY_OPT n) { unimplementedVisitor("endVisit(END_KEY_OPT)"); }

        public boolean visit(option_value0 n) { unimplementedVisitor("visit(option_value0)"); return true; }
        public void endVisit(option_value0 n) { unimplementedVisitor("endVisit(option_value0)"); }

        public boolean visit(option_value1 n) { unimplementedVisitor("visit(option_value1)"); return true; }
        public void endVisit(option_value1 n) { unimplementedVisitor("endVisit(option_value1)"); }

        public boolean visit(aliasSpec0 n) { unimplementedVisitor("visit(aliasSpec0)"); return true; }
        public void endVisit(aliasSpec0 n) { unimplementedVisitor("endVisit(aliasSpec0)"); }

        public boolean visit(aliasSpec1 n) { unimplementedVisitor("visit(aliasSpec1)"); return true; }
        public void endVisit(aliasSpec1 n) { unimplementedVisitor("endVisit(aliasSpec1)"); }

        public boolean visit(aliasSpec2 n) { unimplementedVisitor("visit(aliasSpec2)"); return true; }
        public void endVisit(aliasSpec2 n) { unimplementedVisitor("endVisit(aliasSpec2)"); }

        public boolean visit(aliasSpec3 n) { unimplementedVisitor("visit(aliasSpec3)"); return true; }
        public void endVisit(aliasSpec3 n) { unimplementedVisitor("endVisit(aliasSpec3)"); }

        public boolean visit(aliasSpec4 n) { unimplementedVisitor("visit(aliasSpec4)"); return true; }
        public void endVisit(aliasSpec4 n) { unimplementedVisitor("endVisit(aliasSpec4)"); }

        public boolean visit(aliasSpec5 n) { unimplementedVisitor("visit(aliasSpec5)"); return true; }
        public void endVisit(aliasSpec5 n) { unimplementedVisitor("endVisit(aliasSpec5)"); }

        public boolean visit(alias_rhs0 n) { unimplementedVisitor("visit(alias_rhs0)"); return true; }
        public void endVisit(alias_rhs0 n) { unimplementedVisitor("endVisit(alias_rhs0)"); }

        public boolean visit(alias_rhs1 n) { unimplementedVisitor("visit(alias_rhs1)"); return true; }
        public void endVisit(alias_rhs1 n) { unimplementedVisitor("endVisit(alias_rhs1)"); }

        public boolean visit(alias_rhs2 n) { unimplementedVisitor("visit(alias_rhs2)"); return true; }
        public void endVisit(alias_rhs2 n) { unimplementedVisitor("endVisit(alias_rhs2)"); }

        public boolean visit(alias_rhs3 n) { unimplementedVisitor("visit(alias_rhs3)"); return true; }
        public void endVisit(alias_rhs3 n) { unimplementedVisitor("endVisit(alias_rhs3)"); }

        public boolean visit(alias_rhs4 n) { unimplementedVisitor("visit(alias_rhs4)"); return true; }
        public void endVisit(alias_rhs4 n) { unimplementedVisitor("endVisit(alias_rhs4)"); }

        public boolean visit(alias_rhs5 n) { unimplementedVisitor("visit(alias_rhs5)"); return true; }
        public void endVisit(alias_rhs5 n) { unimplementedVisitor("endVisit(alias_rhs5)"); }

        public boolean visit(alias_rhs6 n) { unimplementedVisitor("visit(alias_rhs6)"); return true; }
        public void endVisit(alias_rhs6 n) { unimplementedVisitor("endVisit(alias_rhs6)"); }

        public boolean visit(macro_name_symbol0 n) { unimplementedVisitor("visit(macro_name_symbol0)"); return true; }
        public void endVisit(macro_name_symbol0 n) { unimplementedVisitor("endVisit(macro_name_symbol0)"); }

        public boolean visit(macro_name_symbol1 n) { unimplementedVisitor("visit(macro_name_symbol1)"); return true; }
        public void endVisit(macro_name_symbol1 n) { unimplementedVisitor("endVisit(macro_name_symbol1)"); }

        public boolean visit(drop_command0 n) { unimplementedVisitor("visit(drop_command0)"); return true; }
        public void endVisit(drop_command0 n) { unimplementedVisitor("endVisit(drop_command0)"); }

        public boolean visit(drop_command1 n) { unimplementedVisitor("visit(drop_command1)"); return true; }
        public void endVisit(drop_command1 n) { unimplementedVisitor("endVisit(drop_command1)"); }

        public boolean visit(name0 n) { unimplementedVisitor("visit(name0)"); return true; }
        public void endVisit(name0 n) { unimplementedVisitor("endVisit(name0)"); }

        public boolean visit(name1 n) { unimplementedVisitor("visit(name1)"); return true; }
        public void endVisit(name1 n) { unimplementedVisitor("endVisit(name1)"); }

        public boolean visit(name2 n) { unimplementedVisitor("visit(name2)"); return true; }
        public void endVisit(name2 n) { unimplementedVisitor("endVisit(name2)"); }

        public boolean visit(name3 n) { unimplementedVisitor("visit(name3)"); return true; }
        public void endVisit(name3 n) { unimplementedVisitor("endVisit(name3)"); }

        public boolean visit(name4 n) { unimplementedVisitor("visit(name4)"); return true; }
        public void endVisit(name4 n) { unimplementedVisitor("endVisit(name4)"); }

        public boolean visit(name5 n) { unimplementedVisitor("visit(name5)"); return true; }
        public void endVisit(name5 n) { unimplementedVisitor("endVisit(name5)"); }

        public boolean visit(produces0 n) { unimplementedVisitor("visit(produces0)"); return true; }
        public void endVisit(produces0 n) { unimplementedVisitor("endVisit(produces0)"); }

        public boolean visit(produces1 n) { unimplementedVisitor("visit(produces1)"); return true; }
        public void endVisit(produces1 n) { unimplementedVisitor("endVisit(produces1)"); }

        public boolean visit(produces2 n) { unimplementedVisitor("visit(produces2)"); return true; }
        public void endVisit(produces2 n) { unimplementedVisitor("endVisit(produces2)"); }

        public boolean visit(produces3 n) { unimplementedVisitor("visit(produces3)"); return true; }
        public void endVisit(produces3 n) { unimplementedVisitor("endVisit(produces3)"); }

        public boolean visit(symWithAttrs0 n) { unimplementedVisitor("visit(symWithAttrs0)"); return true; }
        public void endVisit(symWithAttrs0 n) { unimplementedVisitor("endVisit(symWithAttrs0)"); }

        public boolean visit(symWithAttrs1 n) { unimplementedVisitor("visit(symWithAttrs1)"); return true; }
        public void endVisit(symWithAttrs1 n) { unimplementedVisitor("endVisit(symWithAttrs1)"); }

        public boolean visit(start_symbol0 n) { unimplementedVisitor("visit(start_symbol0)"); return true; }
        public void endVisit(start_symbol0 n) { unimplementedVisitor("endVisit(start_symbol0)"); }

        public boolean visit(start_symbol1 n) { unimplementedVisitor("visit(start_symbol1)"); return true; }
        public void endVisit(start_symbol1 n) { unimplementedVisitor("endVisit(start_symbol1)"); }

        public boolean visit(terminal_symbol0 n) { unimplementedVisitor("visit(terminal_symbol0)"); return true; }
        public void endVisit(terminal_symbol0 n) { unimplementedVisitor("endVisit(terminal_symbol0)"); }

        public boolean visit(terminal_symbol1 n) { unimplementedVisitor("visit(terminal_symbol1)"); return true; }
        public void endVisit(terminal_symbol1 n) { unimplementedVisitor("endVisit(terminal_symbol1)"); }


        public boolean visit(ASTNode n)
        {
            if (n instanceof ASTNodeToken) return visit((ASTNodeToken) n);
            else if (n instanceof JikesPG) return visit((JikesPG) n);
            else if (n instanceof JikesPG_itemList) return visit((JikesPG_itemList) n);
            else if (n instanceof AliasSeg) return visit((AliasSeg) n);
            else if (n instanceof AstSeg) return visit((AstSeg) n);
            else if (n instanceof DefineSeg) return visit((DefineSeg) n);
            else if (n instanceof EofSeg) return visit((EofSeg) n);
            else if (n instanceof EolSeg) return visit((EolSeg) n);
            else if (n instanceof ErrorSeg) return visit((ErrorSeg) n);
            else if (n instanceof ExportSeg) return visit((ExportSeg) n);
            else if (n instanceof GlobalsSeg) return visit((GlobalsSeg) n);
            else if (n instanceof HeadersSeg) return visit((HeadersSeg) n);
            else if (n instanceof IdentifierSeg) return visit((IdentifierSeg) n);
            else if (n instanceof ImportSeg) return visit((ImportSeg) n);
            else if (n instanceof IncludeSeg) return visit((IncludeSeg) n);
            else if (n instanceof KeywordsSeg) return visit((KeywordsSeg) n);
            else if (n instanceof NamesSeg) return visit((NamesSeg) n);
            else if (n instanceof NoticeSeg) return visit((NoticeSeg) n);
            else if (n instanceof RulesSeg) return visit((RulesSeg) n);
            else if (n instanceof StartSeg) return visit((StartSeg) n);
            else if (n instanceof TerminalsSeg) return visit((TerminalsSeg) n);
            else if (n instanceof TrailersSeg) return visit((TrailersSeg) n);
            else if (n instanceof TypesSeg) return visit((TypesSeg) n);
            else if (n instanceof RecoverSeg) return visit((RecoverSeg) n);
            else if (n instanceof PredecessorSeg) return visit((PredecessorSeg) n);
            else if (n instanceof option_specList) return visit((option_specList) n);
            else if (n instanceof option_spec) return visit((option_spec) n);
            else if (n instanceof optionList) return visit((optionList) n);
            else if (n instanceof option) return visit((option) n);
            else if (n instanceof SYMBOLList) return visit((SYMBOLList) n);
            else if (n instanceof aliasSpecList) return visit((aliasSpecList) n);
            else if (n instanceof alias_lhs_macro_name) return visit((alias_lhs_macro_name) n);
            else if (n instanceof defineSpecList) return visit((defineSpecList) n);
            else if (n instanceof defineSpec) return visit((defineSpec) n);
            else if (n instanceof macro_segment) return visit((macro_segment) n);
            else if (n instanceof terminal_symbolList) return visit((terminal_symbolList) n);
            else if (n instanceof action_segmentList) return visit((action_segmentList) n);
            else if (n instanceof import_segment) return visit((import_segment) n);
            else if (n instanceof drop_commandList) return visit((drop_commandList) n);
            else if (n instanceof drop_ruleList) return visit((drop_ruleList) n);
            else if (n instanceof drop_rule) return visit((drop_rule) n);
            else if (n instanceof optMacroName) return visit((optMacroName) n);
            else if (n instanceof include_segment) return visit((include_segment) n);
            else if (n instanceof keywordSpecList) return visit((keywordSpecList) n);
            else if (n instanceof keywordSpec) return visit((keywordSpec) n);
            else if (n instanceof nameSpecList) return visit((nameSpecList) n);
            else if (n instanceof nameSpec) return visit((nameSpec) n);
            else if (n instanceof rules_segment) return visit((rules_segment) n);
            else if (n instanceof nonTermList) return visit((nonTermList) n);
            else if (n instanceof nonTerm) return visit((nonTerm) n);
            else if (n instanceof RuleName) return visit((RuleName) n);
            else if (n instanceof ruleList) return visit((ruleList) n);
            else if (n instanceof rule) return visit((rule) n);
            else if (n instanceof symWithAttrsList) return visit((symWithAttrsList) n);
            else if (n instanceof symAttrs) return visit((symAttrs) n);
            else if (n instanceof action_segment) return visit((action_segment) n);
            else if (n instanceof start_symbolList) return visit((start_symbolList) n);
            else if (n instanceof terminalList) return visit((terminalList) n);
            else if (n instanceof terminal) return visit((terminal) n);
            else if (n instanceof optTerminalAlias) return visit((optTerminalAlias) n);
            else if (n instanceof type_declarationsList) return visit((type_declarationsList) n);
            else if (n instanceof type_declarations) return visit((type_declarations) n);
            else if (n instanceof symbol_pairList) return visit((symbol_pairList) n);
            else if (n instanceof symbol_pair) return visit((symbol_pair) n);
            else if (n instanceof recover_symbol) return visit((recover_symbol) n);
            else if (n instanceof END_KEY_OPT) return visit((END_KEY_OPT) n);
            else if (n instanceof option_value0) return visit((option_value0) n);
            else if (n instanceof option_value1) return visit((option_value1) n);
            else if (n instanceof aliasSpec0) return visit((aliasSpec0) n);
            else if (n instanceof aliasSpec1) return visit((aliasSpec1) n);
            else if (n instanceof aliasSpec2) return visit((aliasSpec2) n);
            else if (n instanceof aliasSpec3) return visit((aliasSpec3) n);
            else if (n instanceof aliasSpec4) return visit((aliasSpec4) n);
            else if (n instanceof aliasSpec5) return visit((aliasSpec5) n);
            else if (n instanceof alias_rhs0) return visit((alias_rhs0) n);
            else if (n instanceof alias_rhs1) return visit((alias_rhs1) n);
            else if (n instanceof alias_rhs2) return visit((alias_rhs2) n);
            else if (n instanceof alias_rhs3) return visit((alias_rhs3) n);
            else if (n instanceof alias_rhs4) return visit((alias_rhs4) n);
            else if (n instanceof alias_rhs5) return visit((alias_rhs5) n);
            else if (n instanceof alias_rhs6) return visit((alias_rhs6) n);
            else if (n instanceof macro_name_symbol0) return visit((macro_name_symbol0) n);
            else if (n instanceof macro_name_symbol1) return visit((macro_name_symbol1) n);
            else if (n instanceof drop_command0) return visit((drop_command0) n);
            else if (n instanceof drop_command1) return visit((drop_command1) n);
            else if (n instanceof name0) return visit((name0) n);
            else if (n instanceof name1) return visit((name1) n);
            else if (n instanceof name2) return visit((name2) n);
            else if (n instanceof name3) return visit((name3) n);
            else if (n instanceof name4) return visit((name4) n);
            else if (n instanceof name5) return visit((name5) n);
            else if (n instanceof produces0) return visit((produces0) n);
            else if (n instanceof produces1) return visit((produces1) n);
            else if (n instanceof produces2) return visit((produces2) n);
            else if (n instanceof produces3) return visit((produces3) n);
            else if (n instanceof symWithAttrs0) return visit((symWithAttrs0) n);
            else if (n instanceof symWithAttrs1) return visit((symWithAttrs1) n);
            else if (n instanceof start_symbol0) return visit((start_symbol0) n);
            else if (n instanceof start_symbol1) return visit((start_symbol1) n);
            else if (n instanceof terminal_symbol0) return visit((terminal_symbol0) n);
            else if (n instanceof terminal_symbol1) return visit((terminal_symbol1) n);
            throw new UnsupportedOperationException("visit(" + n.getClass().toString() + ")");
        }
        public void endVisit(ASTNode n)
        {
            if (n instanceof ASTNodeToken) endVisit((ASTNodeToken) n);
            else if (n instanceof JikesPG) endVisit((JikesPG) n);
            else if (n instanceof JikesPG_itemList) endVisit((JikesPG_itemList) n);
            else if (n instanceof AliasSeg) endVisit((AliasSeg) n);
            else if (n instanceof AstSeg) endVisit((AstSeg) n);
            else if (n instanceof DefineSeg) endVisit((DefineSeg) n);
            else if (n instanceof EofSeg) endVisit((EofSeg) n);
            else if (n instanceof EolSeg) endVisit((EolSeg) n);
            else if (n instanceof ErrorSeg) endVisit((ErrorSeg) n);
            else if (n instanceof ExportSeg) endVisit((ExportSeg) n);
            else if (n instanceof GlobalsSeg) endVisit((GlobalsSeg) n);
            else if (n instanceof HeadersSeg) endVisit((HeadersSeg) n);
            else if (n instanceof IdentifierSeg) endVisit((IdentifierSeg) n);
            else if (n instanceof ImportSeg) endVisit((ImportSeg) n);
            else if (n instanceof IncludeSeg) endVisit((IncludeSeg) n);
            else if (n instanceof KeywordsSeg) endVisit((KeywordsSeg) n);
            else if (n instanceof NamesSeg) endVisit((NamesSeg) n);
            else if (n instanceof NoticeSeg) endVisit((NoticeSeg) n);
            else if (n instanceof RulesSeg) endVisit((RulesSeg) n);
            else if (n instanceof StartSeg) endVisit((StartSeg) n);
            else if (n instanceof TerminalsSeg) endVisit((TerminalsSeg) n);
            else if (n instanceof TrailersSeg) endVisit((TrailersSeg) n);
            else if (n instanceof TypesSeg) endVisit((TypesSeg) n);
            else if (n instanceof RecoverSeg) endVisit((RecoverSeg) n);
            else if (n instanceof PredecessorSeg) endVisit((PredecessorSeg) n);
            else if (n instanceof option_specList) endVisit((option_specList) n);
            else if (n instanceof option_spec) endVisit((option_spec) n);
            else if (n instanceof optionList) endVisit((optionList) n);
            else if (n instanceof option) endVisit((option) n);
            else if (n instanceof SYMBOLList) endVisit((SYMBOLList) n);
            else if (n instanceof aliasSpecList) endVisit((aliasSpecList) n);
            else if (n instanceof alias_lhs_macro_name) endVisit((alias_lhs_macro_name) n);
            else if (n instanceof defineSpecList) endVisit((defineSpecList) n);
            else if (n instanceof defineSpec) endVisit((defineSpec) n);
            else if (n instanceof macro_segment) endVisit((macro_segment) n);
            else if (n instanceof terminal_symbolList) endVisit((terminal_symbolList) n);
            else if (n instanceof action_segmentList) endVisit((action_segmentList) n);
            else if (n instanceof import_segment) endVisit((import_segment) n);
            else if (n instanceof drop_commandList) endVisit((drop_commandList) n);
            else if (n instanceof drop_ruleList) endVisit((drop_ruleList) n);
            else if (n instanceof drop_rule) endVisit((drop_rule) n);
            else if (n instanceof optMacroName) endVisit((optMacroName) n);
            else if (n instanceof include_segment) endVisit((include_segment) n);
            else if (n instanceof keywordSpecList) endVisit((keywordSpecList) n);
            else if (n instanceof keywordSpec) endVisit((keywordSpec) n);
            else if (n instanceof nameSpecList) endVisit((nameSpecList) n);
            else if (n instanceof nameSpec) endVisit((nameSpec) n);
            else if (n instanceof rules_segment) endVisit((rules_segment) n);
            else if (n instanceof nonTermList) endVisit((nonTermList) n);
            else if (n instanceof nonTerm) endVisit((nonTerm) n);
            else if (n instanceof RuleName) endVisit((RuleName) n);
            else if (n instanceof ruleList) endVisit((ruleList) n);
            else if (n instanceof rule) endVisit((rule) n);
            else if (n instanceof symWithAttrsList) endVisit((symWithAttrsList) n);
            else if (n instanceof symAttrs) endVisit((symAttrs) n);
            else if (n instanceof action_segment) endVisit((action_segment) n);
            else if (n instanceof start_symbolList) endVisit((start_symbolList) n);
            else if (n instanceof terminalList) endVisit((terminalList) n);
            else if (n instanceof terminal) endVisit((terminal) n);
            else if (n instanceof optTerminalAlias) endVisit((optTerminalAlias) n);
            else if (n instanceof type_declarationsList) endVisit((type_declarationsList) n);
            else if (n instanceof type_declarations) endVisit((type_declarations) n);
            else if (n instanceof symbol_pairList) endVisit((symbol_pairList) n);
            else if (n instanceof symbol_pair) endVisit((symbol_pair) n);
            else if (n instanceof recover_symbol) endVisit((recover_symbol) n);
            else if (n instanceof END_KEY_OPT) endVisit((END_KEY_OPT) n);
            else if (n instanceof option_value0) endVisit((option_value0) n);
            else if (n instanceof option_value1) endVisit((option_value1) n);
            else if (n instanceof aliasSpec0) endVisit((aliasSpec0) n);
            else if (n instanceof aliasSpec1) endVisit((aliasSpec1) n);
            else if (n instanceof aliasSpec2) endVisit((aliasSpec2) n);
            else if (n instanceof aliasSpec3) endVisit((aliasSpec3) n);
            else if (n instanceof aliasSpec4) endVisit((aliasSpec4) n);
            else if (n instanceof aliasSpec5) endVisit((aliasSpec5) n);
            else if (n instanceof alias_rhs0) endVisit((alias_rhs0) n);
            else if (n instanceof alias_rhs1) endVisit((alias_rhs1) n);
            else if (n instanceof alias_rhs2) endVisit((alias_rhs2) n);
            else if (n instanceof alias_rhs3) endVisit((alias_rhs3) n);
            else if (n instanceof alias_rhs4) endVisit((alias_rhs4) n);
            else if (n instanceof alias_rhs5) endVisit((alias_rhs5) n);
            else if (n instanceof alias_rhs6) endVisit((alias_rhs6) n);
            else if (n instanceof macro_name_symbol0) endVisit((macro_name_symbol0) n);
            else if (n instanceof macro_name_symbol1) endVisit((macro_name_symbol1) n);
            else if (n instanceof drop_command0) endVisit((drop_command0) n);
            else if (n instanceof drop_command1) endVisit((drop_command1) n);
            else if (n instanceof name0) endVisit((name0) n);
            else if (n instanceof name1) endVisit((name1) n);
            else if (n instanceof name2) endVisit((name2) n);
            else if (n instanceof name3) endVisit((name3) n);
            else if (n instanceof name4) endVisit((name4) n);
            else if (n instanceof name5) endVisit((name5) n);
            else if (n instanceof produces0) endVisit((produces0) n);
            else if (n instanceof produces1) endVisit((produces1) n);
            else if (n instanceof produces2) endVisit((produces2) n);
            else if (n instanceof produces3) endVisit((produces3) n);
            else if (n instanceof symWithAttrs0) endVisit((symWithAttrs0) n);
            else if (n instanceof symWithAttrs1) endVisit((symWithAttrs1) n);
            else if (n instanceof start_symbol0) endVisit((start_symbol0) n);
            else if (n instanceof start_symbol1) endVisit((start_symbol1) n);
            else if (n instanceof terminal_symbol0) endVisit((terminal_symbol0) n);
            else if (n instanceof terminal_symbol1) endVisit((terminal_symbol1) n);
            throw new UnsupportedOperationException("visit(" + n.getClass().toString() + ")");
        }
    }

    public void ruleAction(int ruleNumber)
    {
        switch (ruleNumber)
        {

            //
            // Rule 1:  JikesPG ::= options_segment JikesPG_INPUT
            //
            case 1: {
                setResult(
                    new JikesPG(LPGParser.this, getLeftIToken(), getRightIToken(),
                                (option_specList)getRhsSym(1),
                                (JikesPG_itemList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 2:  JikesPG_INPUT ::= $Empty
            //
            case 2: {
                setResult(
                    new JikesPG_itemList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 3:  JikesPG_INPUT ::= JikesPG_INPUT JikesPG_item
            //
            case 3: {
                ((JikesPG_itemList)getRhsSym(1)).add((IJikesPG_item)getRhsSym(2));
                break;
            }
            //
            // Rule 4:  JikesPG_item ::= ALIAS_KEY$ alias_segment END_KEY_OPT$
            //
            case 4: {
                setResult(
                    new AliasSeg(getLeftIToken(), getRightIToken(),
                                 (aliasSpecList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 5:  JikesPG_item ::= AST_KEY$ ast_segment END_KEY_OPT$
            //
            case 5: {
                setResult(
                    new AstSeg(getLeftIToken(), getRightIToken(),
                               (action_segmentList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 6:  JikesPG_item ::= DEFINE_KEY$ define_segment END_KEY_OPT$
            //
            case 6: {
                setResult(
                    new DefineSeg(getLeftIToken(), getRightIToken(),
                                  (defineSpecList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 7:  JikesPG_item ::= EOF_KEY$ eof_segment END_KEY_OPT$
            //
            case 7: {
                setResult(
                    new EofSeg(getLeftIToken(), getRightIToken(),
                               (Ieof_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 8:  JikesPG_item ::= EOL_KEY$ eol_segment END_KEY_OPT$
            //
            case 8: {
                setResult(
                    new EolSeg(getLeftIToken(), getRightIToken(),
                               (Ieol_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 9:  JikesPG_item ::= ERROR_KEY$ error_segment END_KEY_OPT$
            //
            case 9: {
                setResult(
                    new ErrorSeg(getLeftIToken(), getRightIToken(),
                                 (Ierror_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 10:  JikesPG_item ::= EXPORT_KEY$ export_segment END_KEY_OPT$
            //
            case 10: {
                setResult(
                    new ExportSeg(getLeftIToken(), getRightIToken(),
                                  (terminal_symbolList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 11:  JikesPG_item ::= GLOBALS_KEY$ globals_segment END_KEY_OPT$
            //
            case 11: {
                setResult(
                    new GlobalsSeg(getLeftIToken(), getRightIToken(),
                                   (action_segmentList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 12:  JikesPG_item ::= HEADERS_KEY$ headers_segment END_KEY_OPT$
            //
            case 12: {
                setResult(
                    new HeadersSeg(getLeftIToken(), getRightIToken(),
                                   (action_segmentList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 13:  JikesPG_item ::= IDENTIFIER_KEY$ identifier_segment END_KEY_OPT$
            //
            case 13: {
                setResult(
                    new IdentifierSeg(getLeftIToken(), getRightIToken(),
                                      (Iidentifier_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 14:  JikesPG_item ::= IMPORT_KEY$ import_segment END_KEY_OPT$
            //
            case 14: {
                setResult(
                    new ImportSeg(getLeftIToken(), getRightIToken(),
                                  (import_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 15:  JikesPG_item ::= INCLUDE_KEY$ include_segment END_KEY_OPT$
            //
            case 15: {
                setResult(
                    new IncludeSeg(getLeftIToken(), getRightIToken(),
                                   (include_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 16:  JikesPG_item ::= KEYWORDS_KEY$ keywords_segment END_KEY_OPT$
            //
            case 16: {
                setResult(
                    new KeywordsSeg(getLeftIToken(), getRightIToken(),
                                    (keywordSpecList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 17:  JikesPG_item ::= NAMES_KEY$ names_segment END_KEY_OPT$
            //
            case 17: {
                setResult(
                    new NamesSeg(getLeftIToken(), getRightIToken(),
                                 (nameSpecList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 18:  JikesPG_item ::= NOTICE_KEY$ notice_segment END_KEY_OPT$
            //
            case 18: {
                setResult(
                    new NoticeSeg(getLeftIToken(), getRightIToken(),
                                  (action_segmentList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 19:  JikesPG_item ::= RULES_KEY$ rules_segment END_KEY_OPT$
            //
            case 19: {
                setResult(
                    new RulesSeg(getLeftIToken(), getRightIToken(),
                                 (rules_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 20:  JikesPG_item ::= START_KEY$ start_segment END_KEY_OPT$
            //
            case 20: {
                setResult(
                    new StartSeg(getLeftIToken(), getRightIToken(),
                                 (start_symbolList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 21:  JikesPG_item ::= TERMINALS_KEY$ terminals_segment END_KEY_OPT$
            //
            case 21: {
                setResult(
                    new TerminalsSeg(getLeftIToken(), getRightIToken(),
                                     (terminalList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 22:  JikesPG_item ::= TRAILERS_KEY$ trailers_segment END_KEY_OPT$
            //
            case 22: {
                setResult(
                    new TrailersSeg(getLeftIToken(), getRightIToken(),
                                    (action_segmentList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 23:  JikesPG_item ::= TYPES_KEY$ types_segment END_KEY_OPT$
            //
            case 23: {
                setResult(
                    new TypesSeg(getLeftIToken(), getRightIToken(),
                                 (type_declarationsList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 24:  JikesPG_item ::= RECOVER_KEY$ recover_segment END_KEY_OPT$
            //
            case 24: {
                setResult(
                    new RecoverSeg(getLeftIToken(), getRightIToken(),
                                   (SYMBOLList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 25:  JikesPG_item ::= DISJOINTPREDECESSORSETS_KEY$ predecessor_segment END_KEY_OPT$
            //
            case 25: {
                setResult(
                    new PredecessorSeg(getLeftIToken(), getRightIToken(),
                                       (symbol_pairList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 26:  options_segment ::= $Empty
            //
            case 26: {
                setResult(
                    new option_specList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 27:  options_segment ::= options_segment option_spec
            //
            case 27: {
                ((option_specList)getRhsSym(1)).add((option_spec)getRhsSym(2));
                break;
            }
            //
            // Rule 28:  option_spec ::= OPTIONS_KEY$ option_list
            //
            case 28: {
                setResult(
                    new option_spec(getLeftIToken(), getRightIToken(),
                                    (optionList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 29:  option_list ::= option
            //
            case 29: {
                setResult(
                    new optionList((option)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 30:  option_list ::= option_list ,$ option
            //
            case 30: {
                ((optionList)getRhsSym(1)).add((option)getRhsSym(3));
                break;
            }
            //
            // Rule 31:  option ::= SYMBOL option_value
            //
            case 31: {
                setResult(
                    new option(getLeftIToken(), getRightIToken(),
                               new ASTNodeToken(getRhsIToken(1)),
                               (Ioption_value)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 32:  option_value ::= $Empty
            //
            case 32: {
                setResult(null);
                break;
            }
            //
            // Rule 33:  option_value ::= =$ SYMBOL
            //
            case 33: {
                setResult(
                    new option_value0(getLeftIToken(), getRightIToken(),
                                      new ASTNodeToken(getRhsIToken(2)))
                );
                break;
            }
            //
            // Rule 34:  option_value ::= =$ ($ symbol_list )$
            //
            case 34: {
                setResult(
                    new option_value1(getLeftIToken(), getRightIToken(),
                                      (SYMBOLList)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 35:  symbol_list ::= SYMBOL
            //
            case 35: {
                setResult(
                    new SYMBOLList(new ASTNodeToken(getRhsIToken(1)), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 36:  symbol_list ::= symbol_list ,$ SYMBOL
            //
            case 36: {
                ((SYMBOLList)getRhsSym(1)).add(new ASTNodeToken(getRhsIToken(3)));
                break;
            }
            //
            // Rule 37:  alias_segment ::= aliasSpec
            //
            case 37: {
                setResult(
                    new aliasSpecList((IaliasSpec)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 38:  alias_segment ::= alias_segment aliasSpec
            //
            case 38: {
                ((aliasSpecList)getRhsSym(1)).add((IaliasSpec)getRhsSym(2));
                break;
            }
            //
            // Rule 39:  aliasSpec ::= ERROR_KEY produces alias_rhs
            //
            case 39: {
                setResult(
                    new aliasSpec0(getLeftIToken(), getRightIToken(),
                                   new ASTNodeToken(getRhsIToken(1)),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 40:  aliasSpec ::= EOL_KEY produces alias_rhs
            //
            case 40: {
                setResult(
                    new aliasSpec1(getLeftIToken(), getRightIToken(),
                                   new ASTNodeToken(getRhsIToken(1)),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 41:  aliasSpec ::= EOF_KEY produces alias_rhs
            //
            case 41: {
                setResult(
                    new aliasSpec2(getLeftIToken(), getRightIToken(),
                                   new ASTNodeToken(getRhsIToken(1)),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 42:  aliasSpec ::= IDENTIFIER_KEY produces alias_rhs
            //
            case 42: {
                setResult(
                    new aliasSpec3(getLeftIToken(), getRightIToken(),
                                   new ASTNodeToken(getRhsIToken(1)),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 43:  aliasSpec ::= SYMBOL produces alias_rhs
            //
            case 43: {
                setResult(
                    new aliasSpec4(getLeftIToken(), getRightIToken(),
                                   new ASTNodeToken(getRhsIToken(1)),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 44:  aliasSpec ::= alias_lhs_macro_name produces alias_rhs
            //
            case 44: {
                setResult(
                    new aliasSpec5(getLeftIToken(), getRightIToken(),
                                   (alias_lhs_macro_name)getRhsSym(1),
                                   (Iproduces)getRhsSym(2),
                                   (Ialias_rhs)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 45:  alias_lhs_macro_name ::= MACRO_NAME
            //
            case 45: {
                setResult(
                    new alias_lhs_macro_name(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 46:  alias_rhs ::= SYMBOL
            //
            case 46: {
                setResult(
                    new alias_rhs0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 47:  alias_rhs ::= MACRO_NAME
            //
            case 47: {
                setResult(
                    new alias_rhs1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 48:  alias_rhs ::= ERROR_KEY
            //
            case 48: {
                setResult(
                    new alias_rhs2(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 49:  alias_rhs ::= EOL_KEY
            //
            case 49: {
                setResult(
                    new alias_rhs3(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 50:  alias_rhs ::= EOF_KEY
            //
            case 50: {
                setResult(
                    new alias_rhs4(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 51:  alias_rhs ::= EMPTY_KEY
            //
            case 51: {
                setResult(
                    new alias_rhs5(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 52:  alias_rhs ::= IDENTIFIER_KEY
            //
            case 52: {
                setResult(
                    new alias_rhs6(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 53:  ast_segment ::= action_segment_list
            //
            case 53:
                break;
            //
            // Rule 54:  define_segment ::= defineSpec
            //
            case 54: {
                setResult(
                    new defineSpecList((defineSpec)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 55:  define_segment ::= define_segment defineSpec
            //
            case 55: {
                ((defineSpecList)getRhsSym(1)).add((defineSpec)getRhsSym(2));
                break;
            }
            //
            // Rule 56:  defineSpec ::= macro_name_symbol macro_segment
            //
            case 56: {
                setResult(
                    new defineSpec(LPGParser.this, getLeftIToken(), getRightIToken(),
                                   (Imacro_name_symbol)getRhsSym(1),
                                   (macro_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 57:  macro_name_symbol ::= MACRO_NAME
            //
            case 57: {
                setResult(
                    new macro_name_symbol0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 58:  macro_name_symbol ::= SYMBOL
            //
            case 58: {
                setResult(
                    new macro_name_symbol1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 59:  macro_segment ::= BLOCK
            //
            case 59: {
                setResult(
                    new macro_segment(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 60:  eol_segment ::= terminal_symbol
            //
            case 60:
                break;
            //
            // Rule 61:  eof_segment ::= terminal_symbol
            //
            case 61:
                break;
            //
            // Rule 62:  error_segment ::= terminal_symbol
            //
            case 62:
                break;
            //
            // Rule 63:  export_segment ::= terminal_symbol
            //
            case 63: {
                setResult(
                    new terminal_symbolList((Iterminal_symbol)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 64:  export_segment ::= export_segment terminal_symbol
            //
            case 64: {
                ((terminal_symbolList)getRhsSym(1)).add((Iterminal_symbol)getRhsSym(2));
                break;
            }
            //
            // Rule 65:  globals_segment ::= action_segment
            //
            case 65: {
                setResult(
                    new action_segmentList((action_segment)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 66:  globals_segment ::= globals_segment action_segment
            //
            case 66: {
                ((action_segmentList)getRhsSym(1)).add((action_segment)getRhsSym(2));
                break;
            }
            //
            // Rule 67:  headers_segment ::= action_segment_list
            //
            case 67:
                break;
            //
            // Rule 68:  identifier_segment ::= terminal_symbol
            //
            case 68:
                break;
            //
            // Rule 69:  import_segment ::= SYMBOL drop_command_list
            //
            case 69: {
                setResult(
                    new import_segment(getLeftIToken(), getRightIToken(),
                                       new ASTNodeToken(getRhsIToken(1)),
                                       (drop_commandList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 70:  drop_command_list ::= $Empty
            //
            case 70: {
                setResult(
                    new drop_commandList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 71:  drop_command_list ::= drop_command_list drop_command
            //
            case 71: {
                ((drop_commandList)getRhsSym(1)).add((Idrop_command)getRhsSym(2));
                break;
            }
            //
            // Rule 72:  drop_command ::= DROPSYMBOLS_KEY drop_symbols
            //
            case 72: {
                setResult(
                    new drop_command0(getLeftIToken(), getRightIToken(),
                                      new ASTNodeToken(getRhsIToken(1)),
                                      (SYMBOLList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 73:  drop_command ::= DROPRULES_KEY drop_rules
            //
            case 73: {
                setResult(
                    new drop_command1(getLeftIToken(), getRightIToken(),
                                      new ASTNodeToken(getRhsIToken(1)),
                                      (drop_ruleList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 74:  drop_symbols ::= SYMBOL
            //
            case 74: {
                setResult(
                    new SYMBOLList(new ASTNodeToken(getRhsIToken(1)), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 75:  drop_symbols ::= drop_symbols SYMBOL
            //
            case 75: {
                ((SYMBOLList)getRhsSym(1)).add(new ASTNodeToken(getRhsIToken(2)));
                break;
            }
            //
            // Rule 76:  drop_rules ::= drop_rule
            //
            case 76: {
                setResult(
                    new drop_ruleList((drop_rule)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 77:  drop_rules ::= drop_rules drop_rule
            //
            case 77: {
                ((drop_ruleList)getRhsSym(1)).add((drop_rule)getRhsSym(2));
                break;
            }
            //
            // Rule 78:  drop_rule ::= SYMBOL optMacroName produces ruleList
            //
            case 78: {
                setResult(
                    new drop_rule(getLeftIToken(), getRightIToken(),
                                  new ASTNodeToken(getRhsIToken(1)),
                                  (optMacroName)getRhsSym(2),
                                  (Iproduces)getRhsSym(3),
                                  (ruleList)getRhsSym(4))
                );
                break;
            }
            //
            // Rule 79:  optMacroName ::= $Empty
            //
            case 79: {
                setResult(null);
                break;
            }
            //
            // Rule 80:  optMacroName ::= MACRO_NAME
            //
            case 80: {
                setResult(
                    new optMacroName(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 81:  include_segment ::= SYMBOL
            //
            case 81: {
                setResult(
                    new include_segment(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 82:  keywords_segment ::= keywordSpec
            //
            case 82: {
                setResult(
                    new keywordSpecList((IkeywordSpec)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 83:  keywords_segment ::= keywords_segment keywordSpec
            //
            case 83: {
                ((keywordSpecList)getRhsSym(1)).add((IkeywordSpec)getRhsSym(2));
                break;
            }
            //
            // Rule 84:  keywordSpec ::= terminal_symbol
            //
            case 84:
                break;
            //
            // Rule 85:  keywordSpec ::= terminal_symbol produces name
            //
            case 85: {
                setResult(
                    new keywordSpec(getLeftIToken(), getRightIToken(),
                                    (Iterminal_symbol)getRhsSym(1),
                                    (Iproduces)getRhsSym(2),
                                    (Iname)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 86:  names_segment ::= nameSpec
            //
            case 86: {
                setResult(
                    new nameSpecList((nameSpec)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 87:  names_segment ::= names_segment nameSpec
            //
            case 87: {
                ((nameSpecList)getRhsSym(1)).add((nameSpec)getRhsSym(2));
                break;
            }
            //
            // Rule 88:  nameSpec ::= name produces name
            //
            case 88: {
                setResult(
                    new nameSpec(getLeftIToken(), getRightIToken(),
                                 (Iname)getRhsSym(1),
                                 (Iproduces)getRhsSym(2),
                                 (Iname)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 89:  name ::= SYMBOL
            //
            case 89: {
                setResult(
                    new name0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 90:  name ::= MACRO_NAME
            //
            case 90: {
                setResult(
                    new name1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 91:  name ::= EMPTY_KEY
            //
            case 91: {
                setResult(
                    new name2(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 92:  name ::= ERROR_KEY
            //
            case 92: {
                setResult(
                    new name3(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 93:  name ::= EOL_KEY
            //
            case 93: {
                setResult(
                    new name4(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 94:  name ::= IDENTIFIER_KEY
            //
            case 94: {
                setResult(
                    new name5(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 95:  notice_segment ::= action_segment
            //
            case 95: {
                setResult(
                    new action_segmentList((action_segment)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 96:  notice_segment ::= notice_segment action_segment
            //
            case 96: {
                ((action_segmentList)getRhsSym(1)).add((action_segment)getRhsSym(2));
                break;
            }
            //
            // Rule 97:  rules_segment ::= action_segment_list nonTermList
            //
            case 97: {
                setResult(
                    new rules_segment(getLeftIToken(), getRightIToken(),
                                      (action_segmentList)getRhsSym(1),
                                      (nonTermList)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 98:  nonTermList ::= $Empty
            //
            case 98: {
                setResult(
                    new nonTermList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 99:  nonTermList ::= nonTermList nonTerm
            //
            case 99: {
                ((nonTermList)getRhsSym(1)).add((nonTerm)getRhsSym(2));
                break;
            }
            //
            // Rule 100:  nonTerm ::= ruleNameWithAttributes produces ruleList
            //
            case 100: {
                setResult(
                    new nonTerm(LPGParser.this, getLeftIToken(), getRightIToken(),
                                (RuleName)getRhsSym(1),
                                (Iproduces)getRhsSym(2),
                                (ruleList)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 101:  ruleNameWithAttributes ::= SYMBOL
            //
            case 101: {
                setResult(
                    new RuleName(getLeftIToken(), getRightIToken(),
                                 new ASTNodeToken(getRhsIToken(1)),
                                 (ASTNodeToken)null,
                                 (ASTNodeToken)null)
                );
                break;
            }
            //
            // Rule 102:  ruleNameWithAttributes ::= SYMBOL MACRO_NAME$className
            //
            case 102: {
                setResult(
                    new RuleName(getLeftIToken(), getRightIToken(),
                                 new ASTNodeToken(getRhsIToken(1)),
                                 new ASTNodeToken(getRhsIToken(2)),
                                 (ASTNodeToken)null)
                );
                break;
            }
            //
            // Rule 103:  ruleNameWithAttributes ::= SYMBOL MACRO_NAME$className MACRO_NAME$arrayElement
            //
            case 103: {
                setResult(
                    new RuleName(getLeftIToken(), getRightIToken(),
                                 new ASTNodeToken(getRhsIToken(1)),
                                 new ASTNodeToken(getRhsIToken(2)),
                                 new ASTNodeToken(getRhsIToken(3)))
                );
                break;
            }
            //
            // Rule 104:  ruleList ::= rule
            //
            case 104: {
                setResult(
                    new ruleList((rule)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 105:  ruleList ::= ruleList |$ rule
            //
            case 105: {
                ((ruleList)getRhsSym(1)).add((rule)getRhsSym(3));
                break;
            }
            //
            // Rule 106:  produces ::= ::=
            //
            case 106: {
                setResult(
                    new produces0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 107:  produces ::= ::=?
            //
            case 107: {
                setResult(
                    new produces1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 108:  produces ::= ->
            //
            case 108: {
                setResult(
                    new produces2(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 109:  produces ::= ->?
            //
            case 109: {
                setResult(
                    new produces3(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 110:  rule ::= symWithAttrsList opt_action_segment
            //
            case 110: {
                setResult(
                    new rule(getLeftIToken(), getRightIToken(),
                             (symWithAttrsList)getRhsSym(1),
                             (action_segment)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 111:  symWithAttrsList ::= $Empty
            //
            case 111: {
                setResult(
                    new symWithAttrsList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 112:  symWithAttrsList ::= symWithAttrsList symWithAttrs
            //
            case 112: {
                ((symWithAttrsList)getRhsSym(1)).add((IsymWithAttrs)getRhsSym(2));
                break;
            }
            //
            // Rule 113:  symWithAttrs ::= EMPTY_KEY
            //
            case 113: {
                setResult(
                    new symWithAttrs0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 114:  symWithAttrs ::= SYMBOL optAttrList
            //
            case 114: {
                setResult(
                    new symWithAttrs1(getLeftIToken(), getRightIToken(),
                                      new ASTNodeToken(getRhsIToken(1)),
                                      (symAttrs)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 115:  optAttrList ::= $Empty
            //
            case 115: {
                setResult(
                    new symAttrs(getLeftIToken(), getRightIToken(),
                                 (ASTNodeToken)null)
                );
                break;
            }
            //
            // Rule 116:  optAttrList ::= MACRO_NAME
            //
            case 116: {
                setResult(
                    new symAttrs(getLeftIToken(), getRightIToken(),
                                 new ASTNodeToken(getRhsIToken(1)))
                );
                break;
            }
            //
            // Rule 117:  opt_action_segment ::= $Empty
            //
            case 117: {
                setResult(null);
                break;
            }
            //
            // Rule 118:  opt_action_segment ::= action_segment
            //
            case 118:
                break;
            //
            // Rule 119:  action_segment ::= BLOCK
            //
            case 119: {
                setResult(
                    new action_segment(LPGParser.this, getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 120:  start_segment ::= start_symbol
            //
            case 120: {
                setResult(
                    new start_symbolList((Istart_symbol)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 121:  start_segment ::= start_segment start_symbol
            //
            case 121: {
                ((start_symbolList)getRhsSym(1)).add((Istart_symbol)getRhsSym(2));
                break;
            }
            //
            // Rule 122:  start_symbol ::= SYMBOL
            //
            case 122: {
                setResult(
                    new start_symbol0(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 123:  start_symbol ::= MACRO_NAME
            //
            case 123: {
                setResult(
                    new start_symbol1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 124:  terminals_segment ::= terminal
            //
            case 124: {
                setResult(
                    new terminalList((terminal)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 125:  terminals_segment ::= terminals_segment terminal
            //
            case 125: {
                ((terminalList)getRhsSym(1)).add((terminal)getRhsSym(2));
                break;
            }
            //
            // Rule 126:  terminal ::= terminal_symbol optTerminalAlias
            //
            case 126: {
                setResult(
                    new terminal(LPGParser.this, getLeftIToken(), getRightIToken(),
                                 (Iterminal_symbol)getRhsSym(1),
                                 (optTerminalAlias)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 127:  optTerminalAlias ::= $Empty
            //
            case 127: {
                setResult(null);
                break;
            }
            //
            // Rule 128:  optTerminalAlias ::= produces name
            //
            case 128: {
                setResult(
                    new optTerminalAlias(getLeftIToken(), getRightIToken(),
                                         (Iproduces)getRhsSym(1),
                                         (Iname)getRhsSym(2))
                );
                break;
            }
            //
            // Rule 129:  terminal_symbol ::= SYMBOL
            //
            case 129: {
                setResult(
                    new terminal_symbol0(LPGParser.this, getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 130:  terminal_symbol ::= MACRO_NAME
            //
            case 130: {
                setResult(
                    new terminal_symbol1(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 131:  trailers_segment ::= action_segment_list
            //
            case 131:
                break;
            //
            // Rule 132:  types_segment ::= type_declarations
            //
            case 132: {
                setResult(
                    new type_declarationsList((type_declarations)getRhsSym(1), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 133:  types_segment ::= types_segment type_declarations
            //
            case 133: {
                ((type_declarationsList)getRhsSym(1)).add((type_declarations)getRhsSym(2));
                break;
            }
            //
            // Rule 134:  type_declarations ::= SYMBOL produces barSymbolList
            //
            case 134: {
                setResult(
                    new type_declarations(getLeftIToken(), getRightIToken(),
                                          new ASTNodeToken(getRhsIToken(1)),
                                          (Iproduces)getRhsSym(2),
                                          (SYMBOLList)getRhsSym(3))
                );
                break;
            }
            //
            // Rule 135:  barSymbolList ::= SYMBOL
            //
            case 135: {
                setResult(
                    new SYMBOLList(new ASTNodeToken(getRhsIToken(1)), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 136:  barSymbolList ::= barSymbolList |$ SYMBOL
            //
            case 136: {
                ((SYMBOLList)getRhsSym(1)).add(new ASTNodeToken(getRhsIToken(3)));
                break;
            }
            //
            // Rule 137:  predecessor_segment ::= $Empty
            //
            case 137: {
                setResult(
                    new symbol_pairList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 138:  predecessor_segment ::= predecessor_segment symbol_pair
            //
            case 138: {
                ((symbol_pairList)getRhsSym(1)).add((symbol_pair)getRhsSym(2));
                break;
            }
            //
            // Rule 139:  symbol_pair ::= SYMBOL SYMBOL
            //
            case 139: {
                setResult(
                    new symbol_pair(getLeftIToken(), getRightIToken(),
                                    new ASTNodeToken(getRhsIToken(1)),
                                    new ASTNodeToken(getRhsIToken(2)))
                );
                break;
            }
            //
            // Rule 140:  recover_segment ::= $Empty
            //
            case 140: {
                setResult(
                    new SYMBOLList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 141:  recover_segment ::= recover_segment recover_symbol
            //
            case 141:
                break;
            //
            // Rule 142:  recover_symbol ::= SYMBOL
            //
            case 142: {
                setResult(
                    new recover_symbol(LPGParser.this, getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 143:  END_KEY_OPT ::= $Empty
            //
            case 143: {
                setResult(null);
                break;
            }
            //
            // Rule 144:  END_KEY_OPT ::= END_KEY
            //
            case 144: {
                setResult(
                    new END_KEY_OPT(getRhsIToken(1))
                );
                break;
            }
            //
            // Rule 145:  action_segment_list ::= $Empty
            //
            case 145: {
                setResult(
                    new action_segmentList(getLeftIToken(), getRightIToken(), true /* left recursive */)
                );
                break;
            }
            //
            // Rule 146:  action_segment_list ::= action_segment_list action_segment
            //
            case 146: {
                ((action_segmentList)getRhsSym(1)).add((action_segment)getRhsSym(2));
                break;
            }
    
            default:
                break;
        }
        return;
    }
}

