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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.parser.LPGParser.ASTNode;
import org.eclipse.imp.lpg.parser.LPGParser.IASTNodeToken;
import org.eclipse.imp.lpg.parser.LPGParser.Ioption_value;
import org.eclipse.imp.lpg.parser.LPGParser.LPG;
import org.eclipse.imp.lpg.parser.LPGParser.option;
import org.eclipse.imp.lpg.parser.LPGParser.optionList;
import org.eclipse.imp.lpg.parser.LPGParser.option_spec;
import org.eclipse.imp.lpg.parser.LPGParser.option_specList;
import org.eclipse.imp.lpg.parser.LPGParser.option_value__EQUAL_SYMBOL;
import org.eclipse.imp.lpg.preferences.LPGConstants;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.parser.LPGSourcePositionLocator;
import org.eclipse.imp.parser.SimpleLPGParseController;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.services.ILanguageSyntaxProperties;

public class ParseController extends SimpleLPGParseController implements IParseController {
    private JavaActionBlockVisitor actionVisitor;

    public ParseController() {
        super(LPGRuntimePlugin.getInstance().getLanguageID());
        fLexer= new LPGLexer();
        fParser= new LPGParser();
    }

    public ISourcePositionLocator getSourcePositionLocator() {
        return new LPGSourcePositionLocator(this);
    }

    public ILanguageSyntaxProperties getSyntaxProperties() {
        return new LPGSyntaxProperties();
    }

    public List<option> getOptions(LPG root) {
        List<option> result= new ArrayList<option>();
        String template_file= null;
        option_specList optSeg= root.getoptions_segment();
        for(int i= 0; i < optSeg.size(); i++) {
            option_spec optSpec= optSeg.getoption_specAt(i);
            optionList optList= optSpec.getoption_list();
            for(int o= 0; o < optList.size(); o++) {
                option opt= optList.getoptionAt(o);
                result.add(opt);
                IASTNodeToken sym= opt.getSYMBOL();
                String optName= sym.toString();
                if (optName.equals("template")) {
                    Ioption_value optValue= opt.getoption_value();
                    if (optValue instanceof option_value__EQUAL_SYMBOL)
                        template_file= ((option_value__EQUAL_SYMBOL) optValue).getSYMBOL().toString();
                }
            }
        }

        if (template_file != null && fProject != null) {
            IPreferencesService prefSvc= new PreferencesService(fProject.getRawProject(), LPGRuntimePlugin.getInstance().getLanguageID());
            String include_str= prefSvc.getBooleanPreference(LPGConstants.P_USEDEFAULTINCLUDEPATH) ? prefSvc.getStringPreference(IPreferencesService.DEFAULT_LEVEL, LPGConstants.P_INCLUDEPATHTOUSE) : prefSvc.getStringPreference(LPGConstants.P_INCLUDEPATHTOUSE);
            int offset, i= -1;
            do {
                offset= i + 1;
                i= include_str.indexOf(';', offset);
                String filename= include_str.substring(offset, i == -1 ? include_str.length() : i) + template_file;
                File f= new File(filename);
                if (f.exists()) {
                    try {
                        LPGLexer lex= new LPGLexer(filename);
                        LPGParser prs= new LPGParser(lex.getILexStream()); // Create the parser
                        lex.lexer(prs.getIPrsStream()); // Lex the stream to produce the token stream
                        LPG template_root= (LPG) prs.parser(); // Parse the token stream to produce an AST
                        if (template_root != null) {
                            result.addAll(getOptions(template_root));
                            break;
                        }
                    } catch (java.io.IOException e) {
                        // skip this file
                    }
                }
            } while (i != -1);
        }

        return result;
    }

    public Object parse(String contents, IProgressMonitor monitor) {
        super.parse(contents, monitor);

        if (fCurrentAst == null)
            fParser.getIPrsStream().dumpTokens();
        else {
            boolean is_java= false, automatic_ast= false;
            for(option opt : getOptions((LPG) fCurrentAst)) {
                IASTNodeToken sym= opt.getSYMBOL();
                String optName= sym.toString();
                if (optName.equalsIgnoreCase("programming-language") || optName.equalsIgnoreCase("programming_language")
                        || optName.equalsIgnoreCase("programminglanguage") || optName.equalsIgnoreCase("table")) {
                    Ioption_value optValue= opt.getoption_value();
                    if (optValue instanceof option_value__EQUAL_SYMBOL)
                        is_java= ((option_value__EQUAL_SYMBOL) optValue).getSYMBOL().toString().equalsIgnoreCase("java");
                } else if (optName.equalsIgnoreCase("automatic-ast") || optName.equalsIgnoreCase("automatic_ast") || optName.equalsIgnoreCase("automaticast"))
                    automatic_ast= true;
                else if (optName.equalsIgnoreCase("noautomatic-ast") || optName.equalsIgnoreCase("noautomatic_ast")
                        || optName.equalsIgnoreCase("noautomaticast"))
                    automatic_ast= false;
            }

            if (is_java) {
                actionVisitor= (automatic_ast ? new JavaActionBlockAutomaticVisitor() : new JavaActionBlockUserDefinedVisitor());
                actionVisitor.reset(fParser);
                ((ASTNode) fCurrentAst).accept(actionVisitor);
            }
        }
        return fCurrentAst;
    }
}
