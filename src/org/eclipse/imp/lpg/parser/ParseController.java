/*
 * (C) Copyright IBM Corporation 2007
 * 
 * This file is part of the Eclipse IMP.
 */
/*
 * Created on Oct 28, 2005
 */
package org.eclipse.imp.lpg.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lpg.runtime.IMessageHandler;
import lpg.runtime.IToken;
import lpg.runtime.Monitor;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.model.ISourceProject;
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
	fParser= new LPGParser(fLexer); //fLexer.getLexStream()); // Create the parser
	actionVisitor = new JavaActionBlockVisitor();
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

    public Object parse(String contents, boolean scanOnly, IProgressMonitor monitor) {
	MyMonitor my_monitor= new MyMonitor(monitor);
	char[] contentsArray= contents.toCharArray();

	fLexer.initialize(contentsArray, fFilePath.toOSString());
	fParser.getParseStream().resetTokenStream();
	fParser.getParseStream().setMessageHandler(fHandler);
	fLexer.lexer(my_monitor, fParser.getParseStream()); // Lex the stream to produce the token stream
	if (my_monitor.isCancelled())
	    return fCurrentAst; // TODO fCurrentAst might (probably will) be inconsistent wrt the lex stream now
	fCurrentAst= (ASTNode) fParser.parser(my_monitor, 0);
	if (fCurrentAst == null)
	    fParser.getParseStream().dumpTokens();
	else { // TODO: only do this when automatic-ast is requested
        actionVisitor.initialize(fParser);
        fCurrentAst.accept(actionVisitor);
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
