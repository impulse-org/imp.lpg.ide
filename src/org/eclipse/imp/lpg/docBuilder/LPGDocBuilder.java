package org.eclipse.imp.lpg.docBuilder;

import java.io.StringBufferInputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import lpg.runtime.IPrsStream;
import lpg.runtime.IToken;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.builder.BuilderBase;
import org.eclipse.imp.builder.BuilderUtils;
import org.eclipse.imp.builder.MarkerCreator;
import org.eclipse.imp.language.Language;
import org.eclipse.imp.language.LanguageRegistry;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.parser.ASTUtils;
import org.eclipse.imp.lpg.parser.ParseController;
import org.eclipse.imp.lpg.parser.LPGParser.IsymWithAttrs;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.nonTerm;
import org.eclipse.imp.lpg.parser.LPGParser.ruleList;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrsList;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__EMPTY_KEY;
import org.eclipse.imp.lpg.parser.LPGParser.symWithAttrs__SYMBOL_optAttrList;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.runtime.PluginBase;

/**
 * A builder may be activated on a file containing LPG code every time it
 * has changed (when "Build automatically" is on), or when the programmer
 * chooses to "Build" a project.
 */
public class LPGDocBuilder extends BuilderBase {
    /**
     * Extension ID of the LPG builder, which matches the ID in
     * the corresponding extension definition in plugin.xml..
     */
    public static final String BUILDER_ID= LPGRuntimePlugin.kPluginID + ".docBuilder";

    /**
     * A marker ID that identifies problems detected by the builder
     */
    public static final String PROBLEM_MARKER_ID= LPGRuntimePlugin.kPluginID + ".docBuilder.problem";

    public static final String LANGUAGE_NAME= "LPG";

    public static final Language LANGUAGE= LanguageRegistry.findLanguage(LANGUAGE_NAME);

    private LPG fASTRoot;

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

    /**
     * Decide whether a file needs to be build using this builder. Note that
     * <code>isNonRootSourceFile()</code> and <code>isSourceFile()</code>
     * should never return true for the same file.
     * 
     * @return true iff an arbitrary file is a LPG source file.
     */
    protected boolean isSourceFile(IFile file) {
        IPath path= file.getRawLocation();
        if (path == null)
            return false;

        String pathString= path.toString();
        if (pathString.indexOf("/bin/") != -1)
            return false;

        return LANGUAGE.hasExtension(path.getFileExtension());
    }

    /**
     * Decide whether or not to scan a file for dependencies. Note:
     * <code>isNonRootSourceFile()</code> and <code>isSourceFile()</code>
     * should never return true for the same file.
     * 
     * @return true iff the given file is a source file that this builder should
     *         scan for dependencies, but not compile as a top-level compilation
     *         unit.
     * 
     */
    protected boolean isNonRootSourceFile(IFile resource) {
        return false;
    }

    /**
     * Collects compilation-unit dependencies for the given file, and records
     * them via calls to <code>fDependency.addDependency()</code>.
     */
    protected void collectDependencies(IFile file) {
        String fromPath= file.getFullPath().toString();

        getPlugin().writeInfoMsg("Collecting dependencies from ${LANG_NAME} file: " + file.getName());

        // TODO: implement dependency collector
        // E.g. for each dependency:
        // fDependencyInfo.addDependency(fromPath, uponPath);
    }

    /**
     * @return true iff this resource identifies the output folder
     */
    protected boolean isOutputFolder(IResource resource) {
        return resource.getFullPath().lastSegment().equals("bin");
    }

    private final String NL= System.getProperty("line.separator");

    /**
     * Compile one LPG file.
     */
    protected void compile(final IFile file, IProgressMonitor monitor) {
        try {
            String fileName= file.getName();
            getPlugin().writeInfoMsg("Building documentation for LPG file: " + fileName);

            parseSourceFile(file, monitor);

            // visit AST and generate the HTML doc file...
            List<nonTerm> nonTerms= ASTUtils.getNonTerminals(fASTRoot);
            StringBuilder sb= new StringBuilder();
            IPrsStream parseStream= fASTRoot.getEnvironment().getIPrsStream();

            Map<String,nonTerm> nonTermMap= new HashMap<String, nonTerm>();
            for(nonTerm nt : nonTerms) {
                nonTermMap.put(nt.getruleNameWithAttributes().getSYMBOL().toString(), nt);
            }
            TreeSet<nonTerm> sortedNonTerms= new TreeSet<nonTerm>(new Comparator<nonTerm>() {
                public int compare(nonTerm o1, nonTerm o2) {
                    String n1= o1.getruleNameWithAttributes().getSYMBOL().toString();
                    String n2= o2.getruleNameWithAttributes().getSYMBOL().toString();
                    return n1.compareTo(n2);
                } });
            sortedNonTerms.addAll(nonTerms);

            generateDocHeader(sb, fileName);
            for(nonTerm nt: sortedNonTerms) {
                generateNonTermDoc(nt, sb, nonTermMap, parseStream);
            }
            generateDocFooter(sb);

            String htmlFileName= fileName.substring(0, fileName.lastIndexOf('.')) + ".html";
            IFile htmlFile= file.getParent().getFile(new Path(htmlFileName));
            StringBufferInputStream sbis= new StringBufferInputStream(sb.toString());

            if (htmlFile.exists()) {
                htmlFile.setContents(sbis, IResource.FORCE, monitor);
            } else {
                htmlFile.create(sbis, true, monitor);
            }

            doRefresh(file.getParent());
        } catch (Exception e) {
            // catch Exception, because any exception could break the
            // builder infrastructure.
            getPlugin().logException(e.getMessage(), e);
        }
    }

    private void generateDocHeader(StringBuilder sb, String fileName) {
        sb.append("<html>"); sb.append(NL);
        sb.append("<body>"); sb.append(NL);
        sb.append("<h1>Syntax for ");
        sb.append(fileName);
        sb.append(" Language</h1>"); sb.append(NL);
        sb.append("<hr>"); sb.append(NL);

        IToken[] adjAfterOptions= fASTRoot.getoptions_segment().getRightIToken().getFollowingAdjuncts();

        for(int i= 0; i < adjAfterOptions.length; i++) {
            String adjStr= adjAfterOptions[i].toString();
            if (adjStr.startsWith("--**")) {
                String text= adjStr.substring(4);
                if (text.trim().length() == 0) {
                    sb.append("<p>"); sb.append(NL);
                } else {
                    sb.append(text);
                }
            }
        }
        sb.append("<hr>"); sb.append(NL);
        sb.append("<table border=1 width=\"100%\" cellpadding=\"3\" cellspacing=\"0\">"); sb.append(NL);
        String[] cols= { "Non-terminal", "Description", "Rules" };
        sb.append("    <thead><tr bgcolor=\"#ccccff\">");
        for(int i= 0; i < cols.length; i++) {
            sb.append("<td><b>");
            sb.append(cols[i]);
            sb.append("</b></td>");
        }
        sb.append("</tr></thead>"); sb.append(NL);
        sb.append("  <tbody>"); sb.append(NL);
    }

    private void generateDocFooter(StringBuilder sb) {
        sb.append("  </tbody>"); sb.append(NL);
        sb.append("</table>"); sb.append(NL);
        sb.append("</body>"); sb.append(NL);
        sb.append("</html>"); sb.append(NL);
    }

    private void generateNonTermDoc(nonTerm nonTerm, StringBuilder sb, Map<String, nonTerm> nonTermMap, IPrsStream parseStream) {
        String ntName= nonTerm.getruleNameWithAttributes().getSYMBOL().toString();
        String ntDoc= findNonTermDocComment(nonTerm, parseStream);

        if (ntDoc == SUPPRESS_DOC) {
        	return;
        }

        sb.append("  <tr>"); sb.append(NL);
        sb.append("    <td width=\"10%\">");
        emitAnchor(ntName, "<b>" + ntName + "</b>", sb); sb.append(NL);
        sb.append("    </td>"); sb.append(NL);
        sb.append("    <td width=\"50%\">"); sb.append(NL);

        if (ntDoc != null) {
            sb.append("      ");
            sb.append(ntDoc);
            sb.append(NL);
        }
        sb.append("    </td>"); sb.append(NL);
        sb.append("    <td width=\"40%\">"); sb.append(NL);

        ruleList rules= nonTerm.getruleList();

        for(int i= 0; i < rules.size(); i++) {
            // emit 1 paragraph per rule
            symWithAttrsList rhsSyms= rules.getruleAt(i).getsymWithAttrsList();
            sb.append("      <p>");
            for(int j=0; j < rhsSyms.size(); j++) {
                IsymWithAttrs sym= rhsSyms.getsymWithAttrsAt(j);
                if (j > 0) { sb.append(' '); }
                if (sym instanceof symWithAttrs__EMPTY_KEY) {
                    sb.append("%empty");
                } else {
                    String symName= ((symWithAttrs__SYMBOL_optAttrList) sym).getSYMBOL().toString();
                    if (nonTermMap.containsKey(symName)) {
                        emitRef(symName, symName, sb); // assumes no HTML-inappropriate chars in non terminal/terminal names
                    } else {
                        emitEscaped(symName, sb);
                    }
                }
            }
            sb.append("</p>");
            sb.append(NL);
        }
        sb.append("    </td>"); sb.append(NL);
        sb.append("  </tr>"); sb.append(NL);
    }

    private void emitEscaped(String text, StringBuilder sb) {
        // Technically, an apostrophe is also a reserved character in HTML, but apparently browsers
        // like IE and Safari don't handle the quoted version properly, so don't bother replacing it.
        sb.append(text.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;"));
    }

    private void emitRef(String text, String ref, StringBuilder sb) {
        sb.append("<a href=\"#");
        sb.append(ref);
        sb.append("\">");
        sb.append(text);
        sb.append("</a>");
    }

    private void emitAnchor(String anchorName, String text, StringBuilder sb) {
        sb.append("<a name=\"");
        sb.append(anchorName);
        sb.append("\">");
        sb.append(text);
        sb.append("</a>");
    }

    private static final String SUPPRESS_DOC= "!!SUPPRESS!!";

    private String findNonTermDocComment(nonTerm nonTerm, IPrsStream parseStream) {
        IToken[] precAdjuncts= parseStream.getPrecedingAdjuncts(nonTerm.getLeftIToken().getTokenIndex());
        for(int i= 0; i < precAdjuncts.length; i++) {
            String adjStr= precAdjuncts[i].toString();
            if (adjStr.startsWith("--*!")) {
            	return SUPPRESS_DOC;
            }
            if (adjStr.startsWith("--**")) {
                return adjStr.substring(4);
            }
        }
        return null;
    }

    /**
     * @param file    input source file
     * @param monitor progress monitor
     */
    protected void parseSourceFile(final IFile file, IProgressMonitor monitor) {
        try {
            IParseController parseController= new ParseController();

            MarkerCreator markerCreator= new MarkerCreator(file, PROBLEM_MARKER_ID);
            parseController.getAnnotationTypeInfo().addProblemMarkerType(getErrorMarkerID());

            ISourceProject sourceProject= ModelFactory.open(file.getProject());
            parseController.initialize(file.getProjectRelativePath(), sourceProject, markerCreator);

            String contents= BuilderUtils.getFileContents(file);
            fASTRoot= (LPG) parseController.parse(contents, monitor);
        } catch (ModelException e) {
            getPlugin().logException("Example builder returns without parsing due to a ModelException", e);
        }
    }
}
