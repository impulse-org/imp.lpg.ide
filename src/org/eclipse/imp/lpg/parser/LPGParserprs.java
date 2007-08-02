package org.eclipse.imp.lpg.parser;

public class LPGParserprs implements lpg.runtime.ParseTable, LPGParsersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,1,0,0,
            0,0,0,1,1,1,0,0,0,0,
            0,0,1,0,0,0,0,1,1,1,
            0,0,0,0,0,0,0,0,0,1,
            0,0,0,0,0,0,1,0,0,1,
            0,1,1,0,0,1,0,0,1,0,
            0,0,1,1,0,0,1,0,1,0,
            1,0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            7,35,42,38,36,52,43,57,58,31,
            34,37,39,40,48,50,51,53,63,65,
            2,3,4,5,6,8,9,10,11,12,
            13,14,15,16,17,18,19,20,21,22,
            23,24,25,26,27,28,29,30,32,33,
            41,44,45,46,47,49,54,55,56,59,
            60,61,62,64,66,67,68,1
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            2,0,2,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,0,2,2,1,3,
            2,0,2,4,1,3,1,2,3,3,
            3,3,3,3,1,1,1,1,1,1,
            1,1,1,1,2,2,1,1,1,1,
            1,1,1,2,1,2,1,1,2,0,
            2,2,2,1,2,1,2,4,0,1,
            1,1,2,1,3,1,2,3,1,1,
            1,1,1,1,1,2,2,0,2,3,
            1,2,3,1,3,1,1,1,1,2,
            0,2,1,2,0,1,0,1,1,1,
            1,1,1,2,2,0,2,1,1,1,
            1,2,3,1,3,0,2,2,0,2,
            1,0,1,0,2,-10,-15,-14,3,3,
            -27,-42,1,6,-25,-36,1,6,3,-57,
            1,11,12,17,19,18,-97,13,14,18,
            4,-2,13,14,19,25,-44,1,-3,3,
            -11,27,-90,37,-1,38,-101,42,-60,1,
            8,9,-8,17,8,9,-9,-5,16,11,
            12,23,16,7,-12,21,22,10,-28,-13,
            -20,7,20,4,4,-18,-21,-19,3,3,
            -72,41,-30,1,7,47,60,61,-22,-24,
            -23,3,3,3,-26,-37,44,2,4,43,
            -31,1,32,63,40,48,-6,31,39,34,
            33,-32,1,-53,1,-86,3,28,30,29,
            26,-40,-41,1,-7,4,4,-99,-52,1,
            15,20,4,-4,-29,8,9,-16,58,59,
            -17,-33,-35,2,-34,1,4,-48,66,-38,
            1,-51,46,-39,1,4,-43,24,2,64,
            -45,-58,2,-46,1,-47,1,67,-70,45,
            55,-49,1,36,-75,35,-50,1,57,-54,
            1,-55,1,49,-56,1,-59,1,-61,-62,
            2,2,-63,-64,2,2,-65,-67,2,52,
            -66,-76,2,-71,-73,-74,5,10,-77,6,
            6,6,51,5,-78,-79,-80,-81,-82,5,
            5,5,5,-84,-87,2,-92,53,-93,-98,
            -100,2,-68,65,-69,-83,-85,-88,-89,-91,
            -94,15,-95,-96,-102,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,50,0,
            0,0,0,0,0,0,0,0,54,0,
            0,0,0,0,56,0,0,0,0,0,
            0,0,62,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            21,21,23,23,24,24,24,24,24,24,
            24,24,24,24,24,24,24,24,24,24,
            24,24,24,24,24,24,22,22,47,48,
            48,10,49,49,49,50,50,25,25,11,
            11,11,11,11,11,12,5,5,5,5,
            5,5,5,26,27,27,13,14,14,51,
            29,28,30,31,31,32,32,33,34,35,
            52,52,53,53,54,54,55,55,15,56,
            56,36,37,37,17,17,38,38,18,6,
            6,6,6,6,6,39,39,40,57,57,
            58,59,59,59,16,16,2,2,2,2,
            8,9,9,61,61,62,62,60,60,4,
            41,63,63,42,42,19,64,64,3,3,
            43,44,44,20,65,65,46,46,66,45,
            45,67,1,1,7,7,75,75,131,236,
            301,144,108,17,297,150,5,21,297,236,
            41,6,37,329,82,123,86,221,54,302,
            87,118,124,55,302,124,189,5,16,134,
            301,147,160,199,177,39,152,199,156,112,
            4,104,167,181,83,104,167,158,165,384,
            38,329,274,385,283,158,172,179,29,214,
            167,167,262,131,95,65,75,75,158,68,
            63,259,290,231,25,292,27,110,112,75,
            75,75,62,61,60,167,156,252,344,53,
            285,235,24,269,120,294,209,116,254,263,
            312,317,239,23,5,10,265,64,325,320,
            322,327,205,241,18,129,145,96,199,241,
            11,76,132,66,73,215,105,167,189,99,
            364,197,74,183,309,251,22,145,173,137,
            251,20,237,223,251,19,145,74,3,345,
            125,164,253,346,251,15,251,14,140,257,
            241,367,251,13,304,228,306,251,12,221,
            251,9,251,8,31,251,7,251,5,74,
            74,342,349,74,74,355,356,74,165,357,
            315,74,126,358,131,131,131,44,30,126,
            127,88,85,56,43,126,126,126,126,262,
            42,41,40,39,74,267,183,274,71,277,
            74,285,187,92,376,255,264,85,207,269,
            271,280,77,282,272,276,436,436,436,436,
            436,436,436,436,436,436,436,436,436,436,
            436,436,436,436,436,436,436,436,436,378,
            436,436,436,436,436,436,436,436,436,381,
            436,436,436,436,436,370,436,436,436,436,
            436,436,436,114,436,0
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,0,1,2,3,8,9,
            10,11,12,13,14,15,16,17,18,19,
            20,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,0,1,0,3,
            0,1,2,3,8,9,10,11,12,13,
            14,15,16,17,18,19,20,21,22,23,
            24,25,26,27,28,29,30,31,32,33,
            34,35,0,0,0,1,2,4,5,6,
            7,9,10,11,0,13,2,15,16,17,
            18,0,1,21,22,23,24,25,26,27,
            28,29,30,31,32,33,34,0,1,2,
            3,0,1,2,3,0,9,10,11,12,
            9,10,11,0,13,0,1,2,0,38,
            0,1,2,0,9,10,11,12,13,9,
            10,11,12,0,1,2,0,1,2,0,
            1,2,9,10,11,0,13,0,35,4,
            5,6,7,0,0,1,0,4,5,6,
            7,0,0,40,8,4,5,6,7,0,
            0,1,0,4,5,6,7,0,0,1,
            8,4,5,6,7,0,0,1,0,4,
            5,6,7,0,0,0,0,4,5,6,
            7,0,8,0,0,4,5,6,7,0,
            0,1,0,4,5,6,7,0,8,0,
            0,1,12,3,0,1,0,3,0,1,
            0,3,36,3,8,39,19,20,8,36,
            0,37,0,3,0,1,0,1,0,1,
            8,0,1,0,0,1,0,1,0,1,
            0,0,2,0,1,0,0,14,2,0,
            1,0,1,0,0,14,2,0,0,14,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            117,371,618,371,142,564,565,579,371,371,
            371,371,371,371,371,371,371,371,371,371,
            371,371,371,371,371,371,371,371,371,371,
            371,371,371,371,371,371,117,371,26,371,
            142,494,493,579,371,371,371,371,371,371,
            371,371,371,371,371,371,371,371,371,371,
            371,371,371,371,371,371,371,371,371,371,
            371,371,1,436,436,564,565,542,543,544,
            545,229,231,216,101,230,380,151,235,155,
            247,436,469,217,211,218,281,278,147,148,
            210,265,146,205,181,197,193,142,525,526,
            579,142,330,481,579,136,769,761,753,527,
            801,793,777,436,785,436,482,483,139,359,
            436,525,526,2,484,485,488,487,486,528,
            529,530,527,436,330,481,436,557,558,436,
            494,493,341,337,333,126,334,144,435,542,
            543,544,545,84,436,275,436,542,543,544,
            545,142,70,198,555,530,530,530,530,142,
            436,282,130,529,529,529,529,142,436,517,
            555,528,528,528,528,142,436,288,111,333,
            333,333,333,142,98,436,436,334,334,334,
            334,142,555,28,32,337,337,337,337,142,
            117,582,436,341,341,341,341,69,555,436,
            142,375,549,579,142,577,67,579,142,282,
            142,579,383,579,555,470,256,365,555,338,
            142,373,436,579,436,574,436,570,97,377,
            495,436,471,133,436,369,436,510,436,571,
            102,100,539,73,369,78,79,379,516,72,
            511,436,472,436,115,268,552,436,436,268
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }

    public interface Asb {
        public final static char asb[] = {0,
            125,180,125,158,121,97,97,121,66,33,
            33,65,90,1,33,121,121,33,66,90,
            33,33,33,33,33,90,29,124,123,97,
            97,97,186,157,66,96,92,157,157,65,
            66,8,186,96,92,157,157,155,157,157,
            66,66,96,157,157,157,96,90,157,96,
            186,186,186,186,186,186,121,182,121,121,
            1,97,1,1,155,28,28,28,28,28,
            28,121,38,186,185,121,121,191,121,36,
            185,154,185,154,121,38,151,186,151,150,
            151,153
        };
    };
    public final static char asb[] = Asb.asb;
    public final int asb(int index) { return asb[index]; }

    public interface Asr {
        public final static byte asr[] = {0,
            1,2,12,9,10,11,0,15,16,17,
            18,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,3,12,9,10,
            13,11,1,2,0,12,8,15,16,17,
            18,3,9,10,13,21,22,23,11,24,
            25,26,27,28,29,30,31,32,33,34,
            35,1,14,0,1,15,16,17,18,3,
            9,10,13,21,22,23,11,24,25,26,
            27,28,29,30,31,32,33,34,35,8,
            0,4,5,6,7,2,15,16,17,18,
            3,9,10,13,21,22,23,11,24,25,
            26,27,28,29,30,31,32,33,34,35,
            1,0,37,36,15,16,17,18,9,10,
            13,21,22,23,11,24,25,26,27,28,
            40,29,30,31,32,33,34,35,0,2,
            12,8,14,1,19,20,3,15,16,17,
            13,10,9,21,22,23,11,24,25,26,
            27,28,31,32,30,33,34,29,18,35,
            0,1,38,0,2,4,5,6,7,0,
            39,36,0
        };
    };
    public final static byte asr[] = Asr.asr;
    public final int asr(int index) { return asr[index]; }

    public interface Nasb {
        public final static byte nasb[] = {0,
            31,36,24,86,41,71,84,37,39,15,
            27,39,43,1,15,90,92,47,39,45,
            49,57,61,59,11,63,4,36,88,54,
            68,73,66,35,82,14,65,35,35,78,
            81,7,66,14,66,35,35,94,35,35,
            82,81,14,35,35,35,17,96,35,33,
            66,66,66,66,66,66,102,36,36,98,
            9,51,9,9,100,104,104,104,104,104,
            104,106,36,66,36,76,108,36,36,29,
            36,110,112,36,36,36,21,66,29,114,
            29,36
        };
    };
    public final static byte nasb[] = Nasb.nasb;
    public final int nasb(int index) { return nasb[index]; }

    public interface Nasr {
        public final static byte nasr[] = {0,
            6,38,0,12,25,0,1,18,6,0,
            14,27,0,1,3,0,14,1,13,0,
            60,61,0,23,47,0,41,0,9,0,
            22,0,12,11,1,0,44,0,7,0,
            48,0,39,0,32,0,34,0,31,0,
            59,58,0,1,66,0,30,0,28,0,
            29,0,26,0,64,2,0,1,67,0,
            46,0,1,20,0,55,0,57,4,0,
            1,4,0,45,0,24,0,49,0,36,
            0,35,0,52,0,51,0,65,0,53,
            0,10,0,5,0,50,0,54,0,15,
            0,56,0,62,0
        };
    };
    public final static byte nasr[] = Nasr.nasr;
    public final int nasr(int index) { return nasr[index]; }

    public interface TerminalIndex {
        public final static byte terminalIndex[] = {0,
            44,43,21,1,2,3,4,45,22,23,
            28,20,24,5,14,15,16,17,18,19,
            25,26,27,29,30,31,32,33,35,36,
            37,38,39,40,41,7,6,8,9,34,
            10,11,12,42,46,47,48
        };
    };
    public final static byte terminalIndex[] = TerminalIndex.terminalIndex;
    public final int terminalIndex(int index) { return terminalIndex[index]; }

    public interface NonterminalIndex {
        public final static byte nonterminalIndex[] = {0,
            0,72,79,75,73,85,0,0,0,69,
            71,74,76,77,83,0,84,86,90,91,
            0,0,0,49,50,51,52,53,54,55,
            56,57,0,58,59,60,61,62,63,0,
            64,65,0,66,0,0,67,68,0,70,
            78,0,80,81,82,0,0,87,88,0,
            89,0,0,0,92,93,94,0
        };
    };
    public final static byte nonterminalIndex[] = NonterminalIndex.nonterminalIndex;
    public final int nonterminalIndex(int index) { return nonterminalIndex[index]; }
    public final static int scopePrefix[] = null;
    public final int scopePrefix(int index) { return 0;}

    public final static int scopeSuffix[] = null;
    public final int scopeSuffix(int index) { return 0;}

    public final static int scopeLhs[] = null;
    public final int scopeLhs(int index) { return 0;}

    public final static int scopeLa[] = null;
    public final int scopeLa(int index) { return 0;}

    public final static int scopeStateSet[] = null;
    public final int scopeStateSet(int index) { return 0;}

    public final static int scopeRhs[] = null;
    public final int scopeRhs(int index) { return 0;}

    public final static int scopeState[] = null;
    public final int scopeState(int index) { return 0;}

    public final static int inSymb[] = null;
    public final int inSymb(int index) { return 0;}


    public interface Name {
        public final static String name[] = {
            "",
            "::=",
            "::=?",
            "->",
            "->?",
            "|",
            "=",
            ",",
            "(",
            ")",
            "[",
            "]",
            "#",
            "$empty",
            "ALIAS_KEY",
            "AST_KEY",
            "DEFINE_KEY",
            "DISJOINTPREDECESSORSETS_KEY",
            "DROPRULES_KEY",
            "DROPSYMBOLS_KEY",
            "EMPTY_KEY",
            "END_KEY",
            "ERROR_KEY",
            "EOL_KEY",
            "EOF_KEY",
            "EXPORT_KEY",
            "GLOBALS_KEY",
            "HEADERS_KEY",
            "IDENTIFIER_KEY",
            "IMPORT_KEY",
            "INCLUDE_KEY",
            "KEYWORDS_KEY",
            "NAMES_KEY",
            "NOTICE_KEY",
            "OPTIONS_KEY",
            "RECOVER_KEY",
            "TERMINALS_KEY",
            "RULES_KEY",
            "START_KEY",
            "TRAILERS_KEY",
            "TYPES_KEY",
            "EOF_TOKEN",
            "SINGLE_LINE_COMMENT",
            "MACRO_NAME",
            "SYMBOL",
            "BLOCK",
            "VBAR",
            "$empty",
            "ERROR_TOKEN",
            "JikesPG_item",
            "alias_segment",
            "ast_segment",
            "define_segment",
            "eof_segment",
            "eol_segment",
            "error_segment",
            "export_segment",
            "globals_segment",
            "identifier_segment",
            "import_segment",
            "include_segment",
            "keywords_segment",
            "names_segment",
            "notice_segment",
            "start_segment",
            "terminals_segment",
            "types_segment",
            "option_spec",
            "option_list",
            "option",
            "symbol_list",
            "aliasSpec",
            "produces",
            "alias_rhs",
            "alias_lhs_macro_name",
            "action_segment",
            "defineSpec",
            "macro_name_symbol",
            "macro_segment",
            "terminal_symbol",
            "drop_command",
            "drop_symbols",
            "drop_rules",
            "drop_rule",
            "keywordSpec",
            "name",
            "nameSpec",
            "nonTerm",
            "ruleNameWithAttributes",
            "symWithAttrs",
            "terminal",
            "type_declarations",
            "barSymbolList",
            "symbol_pair",
            "recover_symbol"
        };
    };
    public final static String name[] = Name.name;
    public final String name(int index) { return name[index]; }

    public final static int
           ERROR_SYMBOL      = 47,
           SCOPE_UBOUND      = -1,
           SCOPE_SIZE        = 0,
           MAX_NAME_LENGTH   = 27;

    public final int getErrorSymbol() { return ERROR_SYMBOL; }
    public final int getScopeUbound() { return SCOPE_UBOUND; }
    public final int getScopeSize() { return SCOPE_SIZE; }
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int
           NUM_STATES        = 102,
           NT_OFFSET         = 47,
           LA_STATE_OFFSET   = 581,
           MAX_LA            = 3,
           NUM_RULES         = 145,
           NUM_NONTERMINALS  = 68,
           NUM_SYMBOLS       = 115,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 185,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 35,
           EOLT_SYMBOL       = 35,
           ACCEPT_ACTION     = 435,
           ERROR_ACTION      = 436;

    public final static boolean BACKTRACK = true;

    public final int getNumStates() { return NUM_STATES; }
    public final int getNtOffset() { return NT_OFFSET; }
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }
    public final int getMaxLa() { return MAX_LA; }
    public final int getNumRules() { return NUM_RULES; }
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }
    public final int getNumSymbols() { return NUM_SYMBOLS; }
    public final int getSegmentSize() { return SEGMENT_SIZE; }
    public final int getStartState() { return START_STATE; }
    public final int getStartSymbol() { return lhs[0]; }
    public final int getIdentifierSymbol() { return IDENTIFIER_SYMBOL; }
    public final int getEoftSymbol() { return EOFT_SYMBOL; }
    public final int getEoltSymbol() { return EOLT_SYMBOL; }
    public final int getAcceptAction() { return ACCEPT_ACTION; }
    public final int getErrorAction() { return ERROR_ACTION; }
    public final boolean isValidForParser() { return isValidForParser; }
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int originalState(int state) {
        return -baseCheck[state];
    }
    public final int asi(int state) {
        return asb[originalState(state)];
    }
    public final int nasi(int state) {
        return nasb[originalState(state)];
    }
    public final int inSymbol(int state) {
        return inSymb[originalState(state)];
    }

    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    public final int tAction(int state, int sym) {
        int i = baseAction[state],
            k = i + sym;
        return termAction[termCheck[k] == sym ? k : i];
    }
    public final int lookAhead(int la_state, int sym) {
        int k = la_state + sym;
        return termAction[termCheck[k] == sym ? k : la_state];
    }
}
