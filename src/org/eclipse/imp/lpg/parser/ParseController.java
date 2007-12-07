/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Oct 28, 2005
 */
package org.eclipse.imp.lpg.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lpg.runtime.IMessageHandler;
import lpg.runtime.IToken;
import lpg.runtime.Monitor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.imp.lpg.builder.LPGBuilder;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.Ioption_value;
import org.eclipse.imp.lpg.parser.LPGParser.JikesPG;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value0;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.parser.IASTNodeLocator;
import org.eclipse.imp.parser.ILexer;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.IParser;
import org.eclipse.imp.parser.ParseError;

public class ParseController implements IParseController {
    private IPath fFilePath;
    private ISourceProject fProject;
    private IMessageHandler fHandler;
    private LPGParser fParser;
    private LPGLexer fLexer;
    private ASTNode fCurrentAst;
    private JavaActionBlockVisitor actionVisitor;
    private char fKeywords[][];
    private boolean fIsKeyword[];

    public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
	fFilePath= filePath;
	fProject= project;
	fHandler= handler;
    }

    public ISourceProject getProject() {
	return fProject;
    }

    public IPath getPath() {
	return fFilePath;
    }

    public IParser getParser() {
	return fParser;
    }

    public ILexer getLexer() {
	return fLexer;
    }

    public Object getCurrentAst() {
	return fCurrentAst;
    }

    public char[][] getKeywords() {
	return fKeywords;
    }

    public boolean isKeyword(int kind) {
	return fIsKeyword[kind];
    }

    public String getSingleLineCommentPrefix() {
    	return "--";
    }
    
    public int getTokenIndexAtCharacter(int offset) {
	int index= fParser.getParseStream().getTokenIndexAtCharacter(offset);
	return (index < 0 ? -index : index);
    }

    public IToken getTokenAtCharacter(int offset) {
	return fParser.getParseStream().getTokenAtCharacter(offset);
    }

    public IASTNodeLocator getNodeLocator() {
	return new NodeLocator(this);
    }

    public boolean hasErrors() {
	return fCurrentAst == null;
    }

    public List getErrors() {
	return Collections.singletonList(new ParseError("parse error", null));
    }

    public ParseController() {
	fLexer= new LPGLexer(); // Create the lexer
	fParser= new LPGParser();
    }

    class MyMonitor implements Monitor {
	IProgressMonitor monitor;
	boolean wasCancelled= false;

	MyMonitor(IProgressMonitor monitor) {
	    this.monitor= monitor;
	}

	public boolean isCancelled() {
	    if (!wasCancelled)
		wasCancelled= monitor.isCanceled();
	    return wasCancelled;
	}
    }

    public static List<option> getOptions(JikesPG root) {
        List<option> result = new ArrayList<option>();
        String template_file = null;
        option_specList optSeg = root.getoptions_segment();
        for(int i = 0; i < optSeg.size(); i++) {
            option_spec optSpec = optSeg.getoption_specAt(i);
            optionList optList = optSpec.getoption_list();
            for(int o = 0; o < optList.size(); o++) {
                option opt = optList.getoptionAt(o);
                result.add(opt);
                IASTNodeToken sym = opt.getSYMBOL();
                String optName = sym.toString();
                if (optName.equals("template")) {
                    Ioption_value optValue = opt.getoption_value();
                    if (optValue instanceof option_value0)
                        template_file = ((option_value0) optValue).getSYMBOL().toString();
                }
            }
        }
        
        if (template_file != null) {
            String include_str = LPGBuilder.getDefaultIncludePath();
            int offset,
                i = -1;
            do {
                offset = i + 1;
                i = include_str.indexOf(';', offset);
                String filename = include_str.substring(offset, i == -1 ? include_str.length() : i) + template_file;
                File f = new File(filename);
                if (f.exists()) {
                    try {
                        LPGLexer lex = new LPGLexer(filename);
                        LPGParser prs = new LPGParser(lex.getLexStream()); // Create the parser
                        lex.lexer(prs.getParseStream()); // Lex the stream to produce the token stream
                        JikesPG template_root = (JikesPG) prs.parser(); // Parse the token stream to produce an AST
                        if (template_root != null) {
                            result.addAll(getOptions(template_root));
                            break;
                        }
                    }
                    catch (java.io.IOException e) {
                            // skip this file            		
                    }
                } 
            } while (i != -1);
        }
        
        return result;
    }

    public Object parse(String contents, boolean scanOnly, IProgressMonitor monitor) {
	MyMonitor my_monitor= new MyMonitor(monitor);
	char[] contentsArray= contents.toCharArray();

	fLexer.reset(contentsArray, fFilePath.toOSString());
	fParser.reset(fLexer.getLexStream());
	fParser.getParseStream().setMessageHandler(fHandler);
	fLexer.lexer(my_monitor, fParser.getParseStream()); // Lex the stream to produce the token stream
	if (my_monitor.isCancelled())
	    return fCurrentAst; // TODO fCurrentAst might (probably will) be inconsistent wrt the lex stream now
	fCurrentAst= (ASTNode) fParser.parser(my_monitor, 0);
	if (fCurrentAst == null)
	    fParser.getParseStream().dumpTokens();
	else {
        boolean is_java = false,
                automatic_ast = false;
        for(option opt : getOptions((JikesPG) fCurrentAst)) {
            IASTNodeToken sym = opt.getSYMBOL();
            String optName= sym.toString();
            if (optName.equalsIgnoreCase("programming-language") ||
                optName.equalsIgnoreCase("programming_language") ||
                optName.equalsIgnoreCase("programminglanguage") ||
                optName.equalsIgnoreCase("table")) {
                Ioption_value optValue = opt.getoption_value();
                if (optValue instanceof option_value0)
                    is_java = ((option_value0) optValue).getSYMBOL().toString().equalsIgnoreCase("java");
            }
            else if (optName.equalsIgnoreCase("automatic-ast") ||
                     optName.equalsIgnoreCase("automatic_ast") ||
                     optName.equalsIgnoreCase("automaticast"))
                automatic_ast = true;
            else if (optName.equalsIgnoreCase("noautomatic-ast") ||
                     optName.equalsIgnoreCase("noautomatic_ast") ||
                     optName.equalsIgnoreCase("noautomaticast"))
            automatic_ast = false;
        }
    
        if (is_java)
        {        
            actionVisitor = (automatic_ast ? new JavaActionBlockAutomaticVisitor()
            		                       : new JavaActionBlockUserDefinedVisitor());
            actionVisitor.reset(fParser);
            fCurrentAst.accept(actionVisitor);
        }
    }
	if (fKeywords == null)
	    initKeywords();
	return fCurrentAst;
    }

    private void initKeywords() {
	String tokenKindNames[]= fParser.orderedTerminalSymbols();

	this.fIsKeyword= new boolean[tokenKindNames.length];
	this.fKeywords= new char[tokenKindNames.length][];

	int[] keywordKinds= fLexer.getKeywordKinds();

	for(int i= 1; i < keywordKinds.length; i++) {
	    int index= fParser.getParseStream().mapKind(keywordKinds[i]);
	    fIsKeyword[index]= true;
	    fKeywords[index]= fParser.orderedTerminalSymbols()[index].toCharArray();
	}
    }
    
    /*
     * For the management of associated problem-marker types
     */
    
    private static List problemMarkerTypes = new ArrayList();
    
    public List getProblemMarkerTypes() {
    	return problemMarkerTypes;
    }
    
    public void addProblemMarkerType(String problemMarkerType) {
    	problemMarkerTypes.add(problemMarkerType);
    }
    
	public void removeProblemMarkerType(String problemMarkerType) {
		problemMarkerTypes.remove(problemMarkerType);
	}
}
