package org.eclipse.imp.lpg.parser;

import org.eclipse.imp.lpg.parser.LPGParser.*;

public class JavaActionBlockVisitor extends AbstractVisitor
{
    private JavaLexer javaLexer = new JavaLexer(); // lexer can be shared.
    private JavaParser javaParser = new JavaParser(javaLexer);

    public void initialize(LPGParser env)
    {
        javaLexer.initialize(env.getParseStream().getInputChars(), env.getParseStream().getFileName());
        javaParser.reset(javaLexer); // allocate a new parse stream.
        javaParser.getParseStream().setMessageHandler(env.getParseStream().getMessageHandler());
    }
         
    private void parseClassBodyDeclarationsopt(action_segmentList list) {
        for (int i = 0; i < list.size(); i++)
            parseClassBodyDeclarationsopt((action_segment) list.getaction_segmentAt(i));
    }
        
    private void parseClassBodyDeclarationsopt(action_segment n) {
        int start_offset = n.getBLOCK().getStartOffset() + 2,
            end_offset   = n.getBLOCK().getEndOffset() - 2;
        javaParser.getParseStream().resetTokenStream();
        javaLexer.lexer(javaParser.getParseStream(), start_offset, end_offset);
        n.setAst(javaParser.parseClassBodyDeclarationsopt());
    }

    private void parse(action_segmentList list) {
        for (int i = 0; i < list.size(); i++)
            parse((action_segment) list.getaction_segmentAt(i));
    }
   
    private void parse(action_segment n) {
        int start_offset = n.getBLOCK().getStartOffset() + 2,
            end_offset   = n.getBLOCK().getEndOffset() - 2;
        javaParser.getParseStream().resetTokenStream();
        javaLexer.lexer(javaParser.getParseStream(), start_offset, end_offset);
        n.setAst(javaParser.parser());
    }

    //
    // The visitors
    //
    public void unimplementedVisitor(String s) {
        // Sometimes useful for debugging
        //System.out.println(s);
    }

    public boolean visit(AstSeg n) {
        parseClassBodyDeclarationsopt(n.getast_segment());
        return false;
    }

    public boolean visit(GlobalsSeg n) {
        parse(n.getglobals_segment());
        return false;
    }

    public boolean visit(HeadersSeg n) {
        parseClassBodyDeclarationsopt(n.getheaders_segment());
        return false;
    }

    public boolean visit(NoticeSeg n) {
        parse(n.getnotice_segment());
        return false;
    }

    public boolean visit(TrailersSeg n) {
         parseClassBodyDeclarationsopt(n.gettrailers_segment());
         return false;
    }

    public boolean visit(rule n) {
        action_segment a = n.getopt_action_segment();
        if (a != null)
            parseClassBodyDeclarationsopt(a);
        return false;
    }
}
