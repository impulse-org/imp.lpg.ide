package org.eclipse.imp.lpg.parser;

import lpg.runtime.*;
import java.util.*;
import org.eclipse.imp.parser.ILexer;
public class LPGLexer extends LpgLexStream implements LPGParsersym, LPGLexersym, RuleAction, ILexer
{
    private static ParseTable prs = new LPGLexerprs();
    private LexParser lexParser = new LexParser(this, prs, this);

    public int getToken(int i) { return lexParser.getToken(i); }
    public int getRhsFirstTokenIndex(int i) { return lexParser.getFirstToken(i); }
    public int getRhsLastTokenIndex(int i) { return lexParser.getLastToken(i); }

    public int getLeftSpan() { return lexParser.getToken(1); }
    public int getRightSpan() { return lexParser.getLastToken(); }

    public LPGLexer(String filename, int tab) throws java.io.IOException 
    {
        super(filename, tab);
    }

    public LPGLexer(char[] input_chars, String filename, int tab)
    {
        super(input_chars, filename, tab);
    }

    public LPGLexer(char[] input_chars, String filename)
    {
        this(input_chars, filename, 1);
    }

    public LPGLexer() {}

    public String[] orderedExportedSymbols() { return LPGParsersym.orderedTerminalSymbols; }
    public LexStream getLexStream() { return (LexStream) this; }

    public void lexer(IPrsStream prsStream)
    {
        lexer(null, prsStream);
    }
    
    public void lexer(Monitor monitor, IPrsStream prsStream)
    {
        if (getInputChars() == null)
            throw new NullPointerException("LexStream was not initialized");

        setPrsStream(prsStream);

        prsStream.makeToken(0, -1, 0); // Token list must start with a bad token
            
        lexParser.parseCharacters(monitor);  // Lex the input characters
            
        int i = getStreamIndex();
        prsStream.makeToken(i, i, TK_EOF_TOKEN); // and end with the end of file token
        prsStream.setStreamLength(prsStream.getSize());
            
        return;
    }

    //
    // The Lexer contains an array of characters as the input stream to be parsed.
    // There are methods to retrieve and classify characters.
    // The lexparser "token" is implemented simply as the index of the next character in the array.
    // The Lexer extends the abstract class LpgLexStream with an implementation of the abstract
    // method getKind.  The template defines the Lexer class and the lexer() method.
    // A driver creates the action class, "Lexer", passing an Option object to the constructor.
    //
    LPGKWLexer kwLexer;
    boolean printTokens;
    private final static int ECLIPSE_TAB_VALUE = 4;

    public int [] getKeywordKinds() { return kwLexer.getKeywordKinds(); }

    public LPGLexer(String filename) throws java.io.IOException
    {
        this(filename, ECLIPSE_TAB_VALUE);
        this.kwLexer = new LPGKWLexer(getInputChars(), TK_MACRO_NAME);
    }

    public void initialize(char [] content, String filename)
    {
        super.initialize(content, filename);
        if (this.kwLexer == null)
             this.kwLexer = new LPGKWLexer(getInputChars(), TK_MACRO_NAME);
        else this.kwLexer.setInputChars(getInputChars());
    }
    
    final void makeToken(int kind)
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan();
        makeToken(startOffset, endOffset, kind);
        if (printTokens) printValue(startOffset, endOffset);
    }

    final void makeComment(int kind)
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan();
        super.getPrsStream().makeAdjunct(startOffset, endOffset, kind);
    }

    final void skipToken()
    {
        if (printTokens) printValue(getLeftSpan(), getRightSpan());
    }
    
    final void checkForKeyWord()
    {
        int startOffset = getLeftSpan(),
            endOffset = getRightSpan(),
        kwKind = kwLexer.lexer(startOffset, endOffset);
        makeToken(startOffset, endOffset, kwKind);
        if (printTokens) printValue(startOffset, endOffset);
    }
    
    final void printValue(int startOffset, int endOffset)
    {
        String s = new String(getInputChars(), startOffset, endOffset - startOffset + 1);
        System.out.print(s);
    }

    //
    //
    //
    public final static int tokenKind[] =
    {
        Char_CtlCharNotWS,    // 000    0x00
        Char_CtlCharNotWS,    // 001    0x01
        Char_CtlCharNotWS,    // 002    0x02
        Char_CtlCharNotWS,    // 003    0x03
        Char_CtlCharNotWS,    // 004    0x04
        Char_CtlCharNotWS,    // 005    0x05
        Char_CtlCharNotWS,    // 006    0x06
        Char_CtlCharNotWS,    // 007    0x07
        Char_CtlCharNotWS,    // 008    0x08
        Char_HT,              // 009    0x09
        Char_LF,              // 010    0x0A
        Char_CtlCharNotWS,    // 011    0x0B
        Char_FF,              // 012    0x0C
        Char_CR,              // 013    0x0D
        Char_CtlCharNotWS,    // 014    0x0E
        Char_CtlCharNotWS,    // 015    0x0F
        Char_CtlCharNotWS,    // 016    0x10
        Char_CtlCharNotWS,    // 017    0x11
        Char_CtlCharNotWS,    // 018    0x12
        Char_CtlCharNotWS,    // 019    0x13
        Char_CtlCharNotWS,    // 020    0x14
        Char_CtlCharNotWS,    // 021    0x15
        Char_CtlCharNotWS,    // 022    0x16
        Char_CtlCharNotWS,    // 023    0x17
        Char_CtlCharNotWS,    // 024    0x18
        Char_CtlCharNotWS,    // 025    0x19
        Char_CtlCharNotWS,    // 026    0x1A
        Char_CtlCharNotWS,    // 027    0x1B
        Char_CtlCharNotWS,    // 028    0x1C
        Char_CtlCharNotWS,    // 029    0x1D
        Char_CtlCharNotWS,    // 030    0x1E
        Char_CtlCharNotWS,    // 031    0x1F
        Char_Space,           // 032    0x20
        Char_Exclamation,     // 033    0x21
        Char_DoubleQuote,     // 034    0x22
        Char_Sharp,           // 035    0x23
        Char_DollarSign,      // 036    0x24
        Char_Percent,         // 037    0x25
        Char_Ampersand,       // 038    0x26
        Char_SingleQuote,     // 039    0x27
        Char_LeftParen,       // 040    0x28
        Char_RightParen,      // 041    0x29
        Char_Star,            // 042    0x2A
        Char_Plus,            // 043    0x2B
        Char_Comma,           // 044    0x2C
        Char_Minus,           // 045    0x2D
        Char_Dot,             // 046    0x2E
        Char_Slash,           // 047    0x2F
        Char_0,               // 048    0x30
        Char_1,               // 049    0x31
        Char_2,               // 050    0x32
        Char_3,               // 051    0x33
        Char_4,               // 052    0x34
        Char_5,               // 053    0x35
        Char_6,               // 054    0x36
        Char_7,               // 055    0x37
        Char_8,               // 056    0x38
        Char_9,               // 057    0x39
        Char_Colon,           // 058    0x3A
        Char_SemiColon,       // 059    0x3B
        Char_LessThan,        // 060    0x3C
        Char_Equal,           // 061    0x3D
        Char_GreaterThan,     // 062    0x3E
        Char_QuestionMark,    // 063    0x3F
        Char_AtSign,          // 064    0x40
        Char_A,               // 065    0x41
        Char_B,               // 066    0x42
        Char_C,               // 067    0x43
        Char_D,               // 068    0x44
        Char_E,               // 069    0x45
        Char_F,               // 070    0x46
        Char_G,               // 071    0x47
        Char_H,               // 072    0x48
        Char_I,               // 073    0x49
        Char_J,               // 074    0x4A
        Char_K,               // 075    0x4B
        Char_L,               // 076    0x4C
        Char_M,               // 077    0x4D
        Char_N,               // 078    0x4E
        Char_O,               // 079    0x4F
        Char_P,               // 080    0x50
        Char_Q,               // 081    0x51
        Char_R,               // 082    0x52
        Char_S,               // 083    0x53
        Char_T,               // 084    0x54
        Char_U,               // 085    0x55
        Char_V,               // 086    0x56
        Char_W,               // 087    0x57
        Char_X,               // 088    0x58
        Char_Y,               // 089    0x59
        Char_Z,               // 090    0x5A
        Char_LeftBracket,     // 091    0x5B
        Char_BackSlash,       // 092    0x5C
        Char_RightBracket,    // 093    0x5D
        Char_Caret,           // 094    0x5E
        Char__,               // 095    0x5F
        Char_BackQuote,       // 096    0x60
        Char_a,               // 097    0x61
        Char_b,               // 098    0x62
        Char_c,               // 099    0x63
        Char_d,               // 100    0x64
        Char_e,               // 101    0x65
        Char_f,               // 102    0x66
        Char_g,               // 103    0x67
        Char_h,               // 104    0x68
        Char_i,               // 105    0x69
        Char_j,               // 106    0x6A
        Char_k,               // 107    0x6B
        Char_l,               // 108    0x6C
        Char_m,               // 109    0x6D
        Char_n,               // 110    0x6E
        Char_o,               // 111    0x6F
        Char_p,               // 112    0x70
        Char_q,               // 113    0x71
        Char_r,               // 114    0x72
        Char_s,               // 115    0x73
        Char_t,               // 116    0x74
        Char_u,               // 117    0x75
        Char_v,               // 118    0x76
        Char_w,               // 119    0x77
        Char_x,               // 120    0x78
        Char_y,               // 121    0x79
        Char_z,               // 122    0x7A
        Char_LeftBrace,       // 123    0x7B
        Char_VerticalBar,     // 124    0x7C
        Char_RightBrace,      // 125    0x7D
        Char_Tilde,           // 126    0x7E

        Char_AfterASCII,      // for all chars in range 128..65534
        Char_EOF              // for '\uffff' or 65535 
    };
            
    public final int getKind(int i)  // Classify character at ith location
    {
        int c = (i >= getStreamLength() ? '\uffff' : getCharValue(i));
        return (c < 128 // ASCII Character
                  ? tokenKind[c]
                  : c == '\uffff'
                       ? Char_EOF
                       : Char_AfterASCII);
    }

    public void ruleAction( int ruleNumber)
    {
        switch(ruleNumber)
        {
 
            //
            // Rule 1:  Token ::= white
            //
            case 1: { 
             skipToken();           break;
            }
 
            //
            // Rule 2:  Token ::= singleLineComment
            //
            case 2: { 
             makeComment(TK_SINGLE_LINE_COMMENT);           break;
            }
 
            //
            // Rule 4:  Token ::= MacroSymbol
            //
            case 4: { 
             checkForKeyWord();          break;
            }
 
            //
            // Rule 5:  Token ::= Symbol
            //
            case 5: { 
             makeToken(TK_SYMBOL);          break;
            }
 
            //
            // Rule 6:  Token ::= Block
            //
            case 6: { 
             makeToken(TK_BLOCK);          break;
            }
 
            //
            // Rule 7:  Token ::= Equivalence
            //
            case 7: { 
             makeToken(TK_EQUIVALENCE);          break;
            }
 
            //
            // Rule 8:  Token ::= Equivalence ?
            //
            case 8: { 
             makeToken(TK_PRIORITY_EQUIVALENCE);          break;
            }
 
            //
            // Rule 9:  Token ::= #
            //
            case 9: { 
             makeToken(TK_SHARP);          break;
            }
 
            //
            // Rule 10:  Token ::= Arrow
            //
            case 10: { 
             makeToken(TK_ARROW);          break;
            }
 
            //
            // Rule 11:  Token ::= Arrow ?
            //
            case 11: { 
             makeToken(TK_PRIORITY_ARROW);          break;
            }
 
            //
            // Rule 12:  Token ::= |
            //
            case 12: { 
             makeToken(TK_OR_MARKER);          break;
            }
 
            //
            // Rule 13:  Token ::= [
            //
            case 13: { 
             makeToken(TK_LEFT_BRACKET);          break;
            }
 
            //
            // Rule 14:  Token ::= ]
            //
            case 14: { 
             makeToken(TK_RIGHT_BRACKET);          break;
            }
 
            //
            // Rule 780:  OptionLines ::= OptionLineList
            //
            case 780: { 
            
                  // What ever needs to happen after the options have been 
                  // scanned must happen here.
                  break;
            }
       
            //
            // Rule 789:  options ::= % oO pP tT iI oO nN sS
            //
            case 789: { 
            
                  makeToken(getLeftSpan(), getRightSpan(), TK_OPTIONS_KEY);
                  break;
            }
       
            //
            // Rule 790:  OptionComment ::= singleLineComment
            //
            case 790: { 
             makeComment(TK_SINGLE_LINE_COMMENT);           break;
            }
 
            //
            // Rule 814:  separator ::= ,$comma
            //
            case 814: { 
              makeToken(getLeftSpan(), getRightSpan(), TK_COMMA);           break;
            }
 
            //
            // Rule 815:  option ::= action_block$ab optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite ,$comma1 optionWhite block_begin$bb optionWhite ,$comma2 optionWhite block_end$be optionWhite )$rp optionWhite
            //
            case 815: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(11), getRhsLastTokenIndex(11), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(13), getRhsLastTokenIndex(13), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(15), getRhsLastTokenIndex(15), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(17), getRhsLastTokenIndex(17), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 821:  option ::= ast_directory$ad optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 821: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 824:  option ::= ast_type$at optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 824: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 827:  option ::= attributes$a optionWhite
            //
            case 827: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 828:  option ::= no attributes$a optionWhite
            //
            case 828: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 830:  option ::= automatic_ast$a optionWhite
            //
            case 830: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 831:  option ::= no automatic_ast$a optionWhite
            //
            case 831: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 832:  option ::= automatic_ast$aa optionWhite =$eq optionWhite automatic_ast_value$val optionWhite
            //
            case 832: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 838:  option ::= backtrack$b optionWhite
            //
            case 838: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 839:  option ::= no backtrack$b optionWhite
            //
            case 839: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 841:  option ::= byte$b optionWhite
            //
            case 841: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 842:  option ::= no byte$b optionWhite
            //
            case 842: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 844:  option ::= conflicts$c optionWhite
            //
            case 844: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 845:  option ::= no conflicts$c optionWhite
            //
            case 845: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 847:  option ::= dat_directory$dd optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 847: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 850:  option ::= dat_file$df optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 850: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 852:  option ::= dcl_file$df optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 852: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 854:  option ::= def_file$df optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 854: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 856:  option ::= debug$d optionWhite
            //
            case 856: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 857:  option ::= no debug$d optionWhite
            //
            case 857: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 859:  option ::= edit$e optionWhite
            //
            case 859: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 860:  option ::= no edit$e optionWhite
            //
            case 860: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 862:  option ::= error_maps$e optionWhite
            //
            case 862: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 863:  option ::= no error_maps$e optionWhite
            //
            case 863: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 866:  option ::= escape$e optionWhite =$eq optionWhite anyNonWhiteChar$val optionWhite
            //
            case 866: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 868:  option ::= export_terminals$et optionWhite =$eq optionWhite filename$fn optionWhite
            //
            case 868: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 869:  option ::= export_terminals$et optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite )$rp optionWhite
            //
            case 869: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 870:  option ::= export_terminals$et optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite ,$comma optionWhite export_prefix$ep optionWhite )$rp optionWhite
            //
            case 870: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(11), getRhsLastTokenIndex(11), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(13), getRhsLastTokenIndex(13), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 871:  option ::= export_terminals$et optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite ,$comma1 optionWhite export_prefix$ep optionWhite ,$comma2 optionWhite export_suffix$es optionWhite )$rp optionWhite
            //
            case 871: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(11), getRhsLastTokenIndex(11), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(13), getRhsLastTokenIndex(13), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(15), getRhsLastTokenIndex(15), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(17), getRhsLastTokenIndex(17), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 876:  option ::= extends_parsetable$e optionWhite
            //
            case 876: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 877:  option ::= no extends_parsetable$e optionWhite
            //
            case 877: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 878:  option ::= extends_parsetable$ep optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 878: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 881:  option ::= factory$f optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 881: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 883:  option ::= file_prefix$fp optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 883: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 886:  option ::= filter$f optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 886: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 888:  option ::= first$f optionWhite
            //
            case 888: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 889:  option ::= no first$f optionWhite
            //
            case 889: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 891:  option ::= follow$f optionWhite
            //
            case 891: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 892:  option ::= no follow$f optionWhite
            //
            case 892: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 894:  option ::= goto_default$g optionWhite
            //
            case 894: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 895:  option ::= no goto_default$g optionWhite
            //
            case 895: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 898:  option ::= headers$h optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite ,$comma1 optionWhite block_begin$bb optionWhite ,$comma2 optionWhite block_end$be optionWhite )$rp optionWhite
            //
            case 898: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(11), getRhsLastTokenIndex(11), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(13), getRhsLastTokenIndex(13), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(15), getRhsLastTokenIndex(15), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(17), getRhsLastTokenIndex(17), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 900:  option ::= imp_file$if optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 900: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 903:  option ::= import_terminals$it optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 903: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 906:  option ::= include_directory$id optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 906: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 910:  option ::= lalr_level$l optionWhite
            //
            case 910: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 911:  option ::= no lalr_level$l optionWhite
            //
            case 911: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 912:  option ::= lalr_level$l optionWhite =$eq optionWhite number$val optionWhite
            //
            case 912: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 917:  option ::= list$l optionWhite
            //
            case 917: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 918:  option ::= no list$l optionWhite
            //
            case 918: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 920:  option ::= margin$m optionWhite =$eq optionWhite number$val optionWhite
            //
            case 920: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 922:  option ::= max_cases$mc optionWhite =$eq optionWhite number$val optionWhite
            //
            case 922: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 925:  option ::= names$n optionWhite
            //
            case 925: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 926:  option ::= no names$n optionWhite
            //
            case 926: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 927:  option ::= names$n optionWhite =$eq optionWhite names_value$val optionWhite
            //
            case 927: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 932:  option ::= nt_check$n optionWhite
            //
            case 932: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 933:  option ::= no nt_check$n optionWhite
            //
            case 933: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 936:  option ::= or_marker$om optionWhite =$eq optionWhite anyNonWhiteChar$val optionWhite
            //
            case 936: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 939:  option ::= out_directory$dd optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 939: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 942:  option ::= parent_saved$ps optionWhite
            //
            case 942: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 943:  option ::= no parent_saved$ps optionWhite
            //
            case 943: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 946:  option ::= package$p optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 946: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 948:  option ::= parsetable_interfaces$pi optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 948: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 952:  option ::= prefix$p optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 952: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 954:  option ::= priority$p optionWhite
            //
            case 954: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 955:  option ::= no priority$p optionWhite
            //
            case 955: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 957:  option ::= programming_language$pl optionWhite =$eq optionWhite programming_language_value$val optionWhite
            //
            case 957: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 968:  option ::= prs_file$pf optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 968: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 971:  option ::= quiet$q optionWhite
            //
            case 971: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 972:  option ::= no quiet$q optionWhite
            //
            case 972: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 974:  option ::= read_reduce$r optionWhite
            //
            case 974: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 975:  option ::= no read_reduce$r optionWhite
            //
            case 975: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 978:  option ::= remap_terminals$r optionWhite
            //
            case 978: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            }
 
            //
            // Rule 979:  option ::= no remap_terminals$r optionWhite
            //
            case 979: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            }
 
            //
            // Rule 982:  option ::= scopes$s optionWhite
            //
            case 982: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 983:  option ::= no scopes$s optionWhite
            //
            case 983: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 985:  option ::= serialize$s optionWhite
            //
            case 985: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 986:  option ::= no serialize$s optionWhite
            //
            case 986: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 988:  option ::= shift_default$s optionWhite
            //
            case 988: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 989:  option ::= no shift_default$s optionWhite
            //
            case 989: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 992:  option ::= single_productions$s optionWhite
            //
            case 992: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 993:  option ::= no single_productions$s optionWhite
            //
            case 993: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 996:  option ::= slr$s optionWhite
            //
            case 996: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 997:  option ::= no slr$s optionWhite
            //
            case 997: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 999:  option ::= soft_keywords$s optionWhite
            //
            case 999: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1000:  option ::= no soft_keywords$s optionWhite
            //
            case 1000: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1004:  option ::= states$s optionWhite
            //
            case 1004: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1005:  option ::= no states$s optionWhite
            //
            case 1005: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1007:  option ::= suffix$s optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 1007: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1009:  option ::= sym_file$sf optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 1009: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1012:  option ::= tab_file$tf optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 1012: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1015:  option ::= template$t optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 1015: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1017:  option ::= trailers$t optionWhite =$eq optionWhite ($lp optionWhite filename$fn optionWhite ,$comma1 optionWhite block_begin$bb optionWhite ,$comma2 optionWhite block_end$be optionWhite )$rp optionWhite
            //
            case 1017: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_LEFT_PAREN);
                  makeToken(getRhsFirstTokenIndex(7), getRhsLastTokenIndex(7), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(9), getRhsLastTokenIndex(9), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(11), getRhsLastTokenIndex(11), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(13), getRhsLastTokenIndex(13), TK_COMMA);
                  makeToken(getRhsFirstTokenIndex(15), getRhsLastTokenIndex(15), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(17), getRhsLastTokenIndex(17), TK_RIGHT_PAREN);
                  break;
            }
       
            //
            // Rule 1019:  option ::= table$t optionWhite
            //
            case 1019: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1020:  option ::= no table$t optionWhite
            //
            case 1020: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1021:  option ::= table$t optionWhite =$eq optionWhite programming_language_value$val optionWhite
            //
            case 1021: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1023:  option ::= trace$t optionWhite
            //
            case 1023: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1024:  option ::= no trace$t optionWhite
            //
            case 1024: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1025:  option ::= trace$t optionWhite =$eq optionWhite trace_value$val optionWhite
            //
            case 1025: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1030:  option ::= variables$v optionWhite
            //
            case 1030: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1031:  option ::= no variables$v optionWhite
            //
            case 1031: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1032:  option ::= variables$v optionWhite =$eq optionWhite variables_value$val optionWhite
            //
            case 1032: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1039:  option ::= verbose$v optionWhite
            //
            case 1039: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1040:  option ::= no verbose$v optionWhite
            //
            case 1040: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1042:  option ::= visitor$v optionWhite
            //
            case 1042: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1043:  option ::= no visitor$v optionWhite
            //
            case 1043: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1044:  option ::= visitor$v optionWhite =$eq optionWhite visitor_value$val optionWhite
            //
            case 1044: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1049:  option ::= visitor_type$vt optionWhite =$eq optionWhite Value$val optionWhite
            //
            case 1049: { 
            
                  makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);
                  makeToken(getRhsFirstTokenIndex(3), getRhsLastTokenIndex(3), TK_EQUAL);
                  makeToken(getRhsFirstTokenIndex(5), getRhsLastTokenIndex(5), TK_SYMBOL);
                  break;
            }
       
            //
            // Rule 1052:  option ::= warnings$w optionWhite
            //
            case 1052: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1053:  option ::= no warnings$w optionWhite
            //
            case 1053: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1055:  option ::= xreference$x optionWhite
            //
            case 1055: { 
              makeToken(getRhsFirstTokenIndex(1), getRhsLastTokenIndex(1), TK_SYMBOL);           break;
            } 
 
            //
            // Rule 1056:  option ::= no xreference$x optionWhite
            //
            case 1056: { 
              makeToken(getRhsFirstTokenIndex(2), getRhsLastTokenIndex(2), TK_SYMBOL);           break;
            } 

    
            default:
                break;
        }
        return;
    }
}

