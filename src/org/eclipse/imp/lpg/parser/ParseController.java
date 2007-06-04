/*
 * Created on Oct 28, 2005
 */
package org.eclipse.safari.jikespg.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lpg.runtime.IMessageHandler;
import lpg.runtime.IToken;
import lpg.runtime.Monitor;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.uide.model.ISourceProject;
import org.eclipse.uide.parser.IASTNodeLocator;
import org.eclipse.uide.parser.ILexer;
import org.eclipse.uide.parser.IParseController;
import org.eclipse.uide.parser.IParser;
import org.eclipse.uide.parser.ParseError;
import org.eclipse.safari.jikespg.parser.JikesPGParser.ASTNode;

public class ParseController implements IParseController {
    private IPath fFilePath;
    private ISourceProject fProject;
    private JikesPGParser fParser;
    private JikesPGLexer fLexer;
    private ASTNode fCurrentAst;
    private char fKeywords[][];
    private boolean fIsKeyword[];

    public void initialize(IPath filePath, ISourceProject project, IMessageHandler handler) {
	fFilePath= filePath;
	fProject= project;
	fParser.setMessageHandler(handler);
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
	fLexer= new JikesPGLexer(); // Create the lexer
	fParser= new JikesPGParser(fLexer.getLexStream()); // Create the parser
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
	fLexer.lexer(my_monitor, fParser.getParseStream()); // Lex the stream to produce the token stream
	if (my_monitor.isCancelled())
	    return fCurrentAst; // TODO fCurrentAst might (probably will) be inconsistent wrt the lex stream now
	fCurrentAst= (ASTNode) fParser.parser(my_monitor, 0);
	if (fCurrentAst == null)
	    fParser.dumpTokens();
	if (fKeywords == null)
	    initKeywords();
	return fCurrentAst;
    }

    private void initKeywords() {
	String tokenKindNames[]= fParser.getParseStream().orderedTerminalSymbols();

	this.fIsKeyword= new boolean[tokenKindNames.length];
	this.fKeywords= new char[tokenKindNames.length][];

	int[] keywordKinds= fLexer.getKeywordKinds();

	for(int i= 1; i < keywordKinds.length; i++) {
	    int index= fParser.getParseStream().mapKind(keywordKinds[i]);
	    fIsKeyword[index]= true;
	    fKeywords[index]= fParser.getParseStream().orderedTerminalSymbols()[index].toCharArray();
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
