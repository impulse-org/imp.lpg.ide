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

package org.eclipse.imp.lpg.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.imp.builder.BuilderBase;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.parser.LPGLexer;
import org.eclipse.imp.lpg.parser.LPGParser;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.import_segment;
import org.eclipse.imp.lpg.parser.LPGParser.include_segment;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.option_value0;
import org.eclipse.imp.lpg.preferences.LPGConstants;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.ui.console.MessageConsoleStream;
import org.osgi.framework.Bundle;

/**
 * @author rfuhrer@watson.ibm.com
 * @author CLaffra
 */
public class LPGBuilder extends BuilderBase {
    private static final String LPG_BUILDER_CONSOLE= "LPG Build";

    /**
     * Extension ID of the LPG builder. Must match the ID in the corresponding
     * extension definition in plugin.xml.
     */
    public static final String BUILDER_ID= LPGRuntimePlugin.kPluginID + ".LPGBuilder";

    public static final String PROBLEM_MARKER_ID= LPGRuntimePlugin.kPluginID + ".problem";

    /**
     * ID of the LPG runtime plugin, which houses the LPG runtime library
     */
    // SMS 22 Feb 2007  lpg -> lpg.runtime
    public static final String LPG_RUNTIME_PLUGIN_ID= "lpg.runtime";

    /**
     * ID of the LPG generator plugin, which houses the LPG executable and the templates.
     */
    public static final String LPG_GENERATOR_PLUGIN_ID= "lpg.generator";

    private static final String SYNTAX_MSG_REGEXP= "(.*):([0-9]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+): (Informative|Warning|Error): (.*)";

    private static final Pattern SYNTAX_MSG_PATTERN= Pattern.compile(SYNTAX_MSG_REGEXP);

    private static final String SYNTAX_MSG_NOSEV_REGEXP= "(.*):([0-9]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+): (.*)";
    private static final Pattern SYNTAX_MSG_NOSEV_PATTERN= Pattern.compile(SYNTAX_MSG_NOSEV_REGEXP);

    private static final String MISSING_MSG_REGEXP= " file \"([^\"]+)\" could not be read";
    private static final Pattern MISSING_MSG_PATTERN= Pattern.compile(MISSING_MSG_REGEXP);

    private boolean fEmitDiagnostics; // Set by compile() to current pref value

    protected PluginBase getPlugin() {
        return LPGRuntimePlugin.getInstance();
    }

    protected String getErrorMarkerID() {
        return PROBLEM_MARKER_ID;
    }

    protected String getWarningMarkerID() {
        return PROBLEM_MARKER_ID;
    }

    protected String getInfoMarkerID() {
        return PROBLEM_MARKER_ID;
    }

    protected boolean isSourceFile(IFile file) {
    	// SMS 8 Sep 2006
    	//return !file.isDerived() && LPGPreferenceCache.rootExtensionList.contains(file.getFileExtension());

	    String extensListed = fPrefService.getStringPreference(LPGConstants.P_SOURCEFILEEXTENSIONS);
	    String[] extens = extensListed.split(",");
	    HashSet<String> rootExtensionsSet = new HashSet<String>();
	    for(int i= 0; i < extens.length; i++) { rootExtensionsSet.add(extens[i]); }
    	return !file.isDerived() && rootExtensionsSet.contains(file.getFileExtension());
    }

    protected boolean isNonRootSourceFile(IFile file) {
    	// SMS 8 Sep 2006
        //return !file.isDerived() && LPGPreferenceCache.nonRootExtensionList.contains(file.getFileExtension());
        String extensListed = fPrefService.getStringPreference(LPGConstants.P_INCLUDEFILEEXTENSIONS);
        String[] extens = extensListed.split(",");
        HashSet<String> nonrootExtensionsSet = new HashSet<String>();
        for(int i= 0; i < extens.length; i++) {
            nonrootExtensionsSet.add(extens[i]);
        }
    	return !file.isDerived() && nonrootExtensionsSet.contains(file.getFileExtension());
    }

    protected boolean isOutputFolder(IResource resource) {
        return resource.getFullPath().lastSegment().equals("bin");
    }

    @Override
    protected String getConsoleName() {
        return LPG_BUILDER_CONSOLE;
    }

    protected void compile(final IFile file, IProgressMonitor monitor) {
        fEmitDiagnostics= fPrefService.getBooleanPreference(LPGConstants.P_EMITDIAGNOSTICS);

        String fileName= file.getLocation().toOSString();
        try {
            String executablePath= getLPGExecutable();

            if (executablePath.length() == 0)
                return;

            File parentDir= new File(fileName).getParentFile();
            String includePath= getIncludePath();
            String[] cmd= buildCmdlineArgs(fileName, executablePath, includePath);
            MessageConsoleStream diag= getConsoleStream();

            if (fEmitDiagnostics) {
                diag.println("Running generator on grammar file '" + fileName + "'.");
                diag.println("Using executable at '" + executablePath + "'.");
                diag.println("Using template path '" + includePath + "'.");
            }

            Process process= Runtime.getRuntime().exec(cmd, new String[0], parentDir);

            processLPGOutput(file, process, diag);
            processLPGErrors(file, process, diag);
            doRefresh(file);
            collectDependencies(file);
            diag.println("Generator exit code == " + process.waitFor());
        } catch (Exception e) {
            LPGRuntimePlugin.getInstance().logException(e.getMessage() == null ? "<no information>" : e.getMessage(), e);
        }
    }

    private String[] buildCmdlineArgs(String fileName, String executablePath, String includePath) {
        String includeSearchPath= "-include-directory='" + includePath + "'";

        String cmd[]= new String[] {
                executablePath,
                "-quiet",
                (fPrefService.getBooleanPreference(LPGConstants.P_GENERATELISTINGS) ? "-list" : "-nolist"),
                // In order for Windows to treat the following template path argument as
                // a single argument, despite any embedded spaces, it has to be completely
                // enclosed in double quotes. It does not suffice to quote only the path
                // part. However, for lpg to treat the path properly, the path itself
                // must also be quoted, since the outer quotes will be stripped by the
                // Windows shell (command/cmd.exe). As an added twist, if we used the same
                // kind of quote for both the inner and outer quoting, and the outer quotes
                // survived, the part that actually needed quoting would be "bare"! Hence
                // we use double quotes for the outer level and single quotes inside.
                (fIsWin32 ? "\"" + includeSearchPath + "\"" : includeSearchPath),
                // TODO RMF 7/21/05 -- Don't specify -dat-directory; causes performance issues with Eclipse.
                // Lexer tables can get quite large, so large that Java as spec'ed can't swallow them
                // when translated to a switch statement, or even an array initializer. As a result,
                // LPG supports the "-dat-directory" option to spill the tables into external data
                // files loaded by the lexer at runtime. HOWEVER, loading these external data tables is
                // very slow when performed using the standard Eclipse/plugin classloader.
                // So: don't enable it by default.
                // "-dat-directory=" + getOutputDirectory(resource.getProject()),
                fileName};
        return cmd;
    }

    protected void collectDependencies(IFile file) {
        if (fEmitDiagnostics) {
            getConsoleStream().println("Collecting dependencies from file '" + file.getLocation().toOSString() + "'.");
        }

        LPGLexer lexer= new LPGLexer(); // Create the lexer        
        // SMS 12 Feb 2008
        // Previously attempted to get lex stream before it has been set,
        // so added a call to reset(..) here.  This call to reset(..)
        // obviates the need to initialize the lexer later on in the
        // method (so code related to that can be removed)
        try {
        	lexer.reset(file.getLocation().toString(), 8);
        } catch (IOException e) {
        	
        }
        LPGParser parser= new LPGParser(lexer.getILexStream()); // Create the parser

        lexer.lexer(null, parser.getIPrsStream());
        ASTNode ast= (ASTNode) parser.parser();

        if (ast != null)
        	findDependencies(ast, file.getFullPath().toString());
    }

    /**
     * @param root
     * @param filePath 
     */
    private void findDependencies(ASTNode root, final String filePath) {
        root.accept(new LPGParser.AbstractVisitor() {
            public void unimplementedVisitor(String s) { }
            public boolean visit(option n) {
                if (n.getSYMBOL().toString().equals("import_terminals")) {
                    String referent= ((option_value0) n.getoption_value()).getSYMBOL().toString();
                    String referentPath= filePath.substring(0, filePath.lastIndexOf("/")+1) + referent;

                    fDependencyInfo.addDependency(filePath, referentPath);
                } else if (n.getSYMBOL().toString().equals("filter")) {
                    String referent= ((option_value0) n.getoption_value()).getSYMBOL().toString();
                    String referentPath= filePath.substring(0, filePath.lastIndexOf("/")+1) + referent;

                    fDependencyInfo.addDependency(filePath, referentPath);
                }
                return false;
            }
            /* (non-Javadoc)
             * @see org.lpg.uide.parser.LPGParser.AbstractVisitor#visit(org.lpg.uide.parser.LPGParser.ImportSeg)
             */
            public boolean visit(import_segment n) {
                fDependencyInfo.addDependency(filePath, n.getSYMBOL().toString());
                return false;
            }
            /* (non-Javadoc)
             * @see org.eclipse.imp.lpg.runtime.parser.LPGParser.AbstractVisitor#visit(org.eclipse.imp.lpg.runtime.parser.LPGParser.include_segment1)
             */
            public boolean visit(include_segment n) {
                fDependencyInfo.addDependency(filePath, n.getSYMBOL().toString());
                return false;
            }
        });
    }

    private void processLPGErrors(IResource resource, Process process, MessageConsoleStream stream) throws IOException {
        InputStream is= process.getErrorStream();
        BufferedReader in2= new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line= in2.readLine()) != null) {
            if (fEmitDiagnostics)
                stream.println(line);

            if (parseSyntaxMessageCreateMarker(line))
                ;
            else if (line.indexOf("could not be read.") >= 0) {
                parseMissingFileMessage(line, resource);
            } else
                handleMiscMessage(line, resource);
            LPGRuntimePlugin.getInstance().writeErrorMsg(line);
        }
        is.close();
    }

    final String lineSep= System.getProperty("line.separator");
    final int lineSepBias= lineSep.length() - 1;

    private static boolean fIsWin32= Platform.getOS().equals(Platform.OS_WIN32);

    private void processLPGOutput(final IResource resource, Process process, MessageConsoleStream stream) throws IOException {
        InputStream is= process.getInputStream();
        BufferedReader in= new BufferedReader(new InputStreamReader(is));
        String line= null;

        while ((line= in.readLine()) != null) {
            if (fEmitDiagnostics)
                stream.println(line);
            if (line.length() == 0)
                continue;

            if (parseSyntaxMessageCreateMarker(line))
                ;
            else if (line.indexOf("could not be read.") >= 0) {
                parseMissingFileMessage(line, resource);
            } else
                handleMiscMessage(line, resource);
        }
    }

    private void handleMiscMessage(String msg, IResource file) {
        if (msg.length() == 0) return;
        if (msg.startsWith("Unable to open")) {
            createMarker(file, 1, -1, -1, msg, IMarker.SEVERITY_ERROR);
            return;
        }
        if (msg.startsWith("***ERROR: ")) {
            createMarker(file, 1, 0, 1, msg.substring(10), IMarker.SEVERITY_ERROR);
            return;
        }
        if (msg.indexOf("Number of ") >= 0 ||
                msg.startsWith("(C) Copyright") ||
                msg.startsWith("IBM LALR Parser"))
            return;

        Matcher matcher= SYNTAX_MSG_NOSEV_PATTERN.matcher(msg);

        if (matcher.matches()) {
            String errorFile= matcher.group(1);
            String projectLoc= getProject().getLocation().toString();

            if (errorFile.startsWith(projectLoc))
                errorFile= errorFile.substring(projectLoc.length());

            IResource errorResource= getProject().getFile(errorFile);
            int startLine= Integer.parseInt(matcher.group(2));
//	    int startCol= Integer.parseInt(matcher.group(3));
//	    int endLine= Integer.parseInt(matcher.group(4));
//	    int endCol= Integer.parseInt(matcher.group(5));
            int startChar= Integer.parseInt(matcher.group(6)) - 1;// - (startLine - 1) * lineSepBias + 1;
            int endChar= Integer.parseInt(matcher.group(7));// - (endLine - 1) * lineSepBias + 1;
            String descrip= matcher.group(8);

            if (startLine == 0) startLine= 1;
                createMarker(errorResource, startLine, startChar, endChar, descrip, IMarker.SEVERITY_WARNING);
        } else
            createMarker(file, 1, 0, 1, msg, IMarker.SEVERITY_INFO);
    }

    private void parseMissingFileMessage(String msg, IResource file) {
        Matcher matcher= MISSING_MSG_PATTERN.matcher(msg);

        if (matcher.matches()) {
            String missingFile= matcher.group(1);
            int refLine= 1; // Integer.parseInt(matcher.group(2))

            createMarker(file, refLine, -1, -1, "Non-existent file referenced: " + missingFile, IMarker.SEVERITY_ERROR);
        }
    }

    private boolean parseSyntaxMessageCreateMarker(final String msg) {
        Matcher matcher= SYNTAX_MSG_PATTERN.matcher(msg);

        if (matcher.matches()) {
            String errorFile= matcher.group(1);
            String projectLoc= getProject().getLocation().toString();

            if (errorFile.startsWith(projectLoc))
                errorFile= errorFile.substring(projectLoc.length());

            IResource errorResource= getProject().getFile(errorFile);

            if (!errorResource.exists())
                return true;

            int startLine= Integer.parseInt(matcher.group(2));
//          int startCol= Integer.parseInt(matcher.group(3));
//          int endLine= Integer.parseInt(matcher.group(4));
//          int endCol= Integer.parseInt(matcher.group(5));
            int startChar= Integer.parseInt(matcher.group(6)) - 1;// - (startLine - 1) * lineSepBias + 1;
            int endChar= Integer.parseInt(matcher.group(7));// - (endLine - 1) * lineSepBias + 1;
            String severity= matcher.group(8);
            String descrip= matcher.group(9);

            if (startLine == 0) startLine= 1;
            if (startChar < 0) { startChar= 0; endChar= 1; }
            createMarker(errorResource, startLine, startChar, endChar, descrip, mapSeverity(severity));
            return true;
        }
        return false;
    }

    private int mapSeverity(String severity) {
        return severity.equals("Informative") ? IMarker.SEVERITY_INFO :
            (severity.equals("Warning") ? IMarker.SEVERITY_WARNING : IMarker.SEVERITY_ERROR);
    }

    public String getIncludePath() {
        if (fPrefService.getBooleanPreference(LPGConstants.P_USEDEFAULTINCLUDEPATH))
            return fPrefService.getStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_INCLUDEPATHTOUSE);
        String projSpecIncPath= fPrefService.getStringPreference(LPGConstants.P_INCLUDEPATHTOUSE);
        return projSpecIncPath; //+ ";" + getDefaultIncludePath();
    }

    public static String getDefaultIncludePath() {
        Bundle bundle= Platform.getBundle(LPG_GENERATOR_PLUGIN_ID);

        try {
            // Use getEntry() rather than getResource(), since the "templates" folder is
            // no longer inside the plugin jar (which is now expanded upon installation).
            String tmplPath= FileLocator.toFileURL(bundle.getEntry("templates")).getFile();

            if (fIsWin32)
                tmplPath= tmplPath.substring(1);

            return tmplPath;
        } catch(IOException e) {
            LPGRuntimePlugin.getInstance().logException(e.getMessage() == null ?
                    "Returning null:  <no information>" : "Returning null:  " + e.getMessage(), e);
            return null;
        }
    }

    private String getLPGExecutable() throws IOException {
        String result;
        if (fPrefService.getBooleanPreference(LPGConstants.P_USEDEFAULTEXECUTABLE)) {
            result= fPrefService.getStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_EXECUTABLETOUSE);
        } else {
        	result= fPrefService.getStringPreference(LPGConstants.P_EXECUTABLETOUSE);
        }
    	if (!(new File(result)).exists()) {
    	    postMsgDialog("No LPG Executable", "The LPG executable cannot be found at the location you specified. Please change the setting in the LPG preferences page.");
            return "";
    	}
    	return result;
    }
}
