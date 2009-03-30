package org.eclipse.imp.lpg.parser;

public class LPGParserprs implements lpg.runtime.ParseTable, LPGParsersym {
    public final static int ERROR_SYMBOL = 46;
    public final int getErrorSymbol() { return ERROR_SYMBOL; }

    public final static int SCOPE_UBOUND = -1;
    public final int getScopeUbound() { return SCOPE_UBOUND; }

    public final static int SCOPE_SIZE = 0;
    public final int getScopeSize() { return SCOPE_SIZE; }

    public final static int MAX_NAME_LENGTH = 27;
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int NUM_STATES = 103;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 46;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 586;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 3;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 146;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 68;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 114;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 194;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 35;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 35;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 439;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 440;
    public final int getErrorAction() { return ERROR_ACTION; }

    public final static boolean BACKTRACK = false;
    public final boolean getBacktrack() { return BACKTRACK; }

    public final int getStartSymbol() { return lhs(0); }
    public final boolean isValidForParser() { return LPGParsersym.isValidForParser; }


    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,1,0,0,0,
            0,0,1,1,1,0,0,0,0,0,
            0,1,0,0,0,0,0,1,1,1,
            0,0,1,0,0,0,0,0,0,1,
            0,0,0,0,0,0,1,0,0,1,
            0,1,1,0,0,1,0,0,1,0,
            0,0,1,1,0,0,1,0,1,1,
            0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            7,35,42,43,36,52,38,57,58,31,
            34,37,39,40,48,50,51,53,62,63,
            65,2,3,4,5,6,8,9,10,11,
            12,13,14,15,16,17,18,19,20,21,
            22,23,24,25,26,27,28,29,30,32,
            33,41,44,45,46,47,49,54,55,56,
            59,60,61,64,66,67,68,1
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
            0,0,0,0,0,0
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
            2,1,1,1,2,2,0,2,1,1,
            1,1,2,3,1,3,0,2,2,0,
            2,1,0,1,0,2,-10,-14,-25,0,
            -57,0,-15,0,-77,0,-27,-36,0,0,
            0,0,0,0,0,0,0,0,0,0,
            -2,-98,-16,-42,0,0,0,0,-5,0,
            -44,0,0,0,-61,0,0,-3,0,0,
            0,0,-91,-1,-102,0,0,0,-8,-11,
            0,0,0,0,-18,-9,-12,0,0,0,
            0,0,0,0,-17,0,0,-13,0,0,
            -20,0,-19,-21,0,-73,0,0,-22,0,
            -28,0,0,0,-23,0,-37,0,0,0,
            -40,0,-24,0,0,0,-6,0,-7,0,
            0,-26,-87,0,-29,0,0,0,0,0,
            -30,0,-31,0,0,-32,0,0,-38,0,
            -4,0,-41,0,-53,0,0,0,0,-52,
            0,-100,-48,0,0,0,0,0,-58,0,
            0,-34,0,0,0,0,-68,-33,0,0,
            0,-35,-39,0,0,0,0,-43,0,0,
            -45,-71,0,-46,0,-47,0,-49,0,-50,
            0,-51,-54,0,-76,0,0,-55,0,0,
            -56,0,-59,0,-60,0,-83,-62,0,0,
            0,-63,-64,0,0,-65,-66,0,0,-67,
            -72,0,-74,-75,-88,-78,0,-79,0,0,
            0,-80,0,-81,-82,-85,0,0,0,0,
            -93,-94,-99,-101,0,-69,0,-70,0,-84,
            -86,-89,-90,-92,-95,0,-96,0,-97,-103,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            22,22,24,24,25,25,25,25,25,25,
            25,25,25,25,25,25,25,25,25,25,
            25,25,25,25,25,25,23,23,48,49,
            49,10,50,50,50,51,51,26,26,11,
            11,11,11,11,11,12,5,5,5,5,
            5,5,5,27,28,28,13,14,14,52,
            30,29,31,32,32,33,33,34,35,36,
            53,53,54,54,55,55,56,56,15,57,
            57,37,38,38,17,17,39,39,18,6,
            6,6,6,6,6,40,40,41,58,58,
            59,60,60,60,16,16,2,2,2,2,
            8,9,9,62,62,63,63,61,61,4,
            42,42,19,19,43,43,20,64,64,3,
            3,44,45,45,21,65,65,47,47,66,
            46,46,67,1,1,7,7,75,131,173,
            237,147,6,75,308,126,311,144,5,21,
            44,237,54,289,55,289,86,124,37,338,
            82,116,221,205,108,17,118,151,125,181,
            308,5,16,185,311,112,4,174,158,29,
            158,181,87,159,39,159,38,338,83,189,
            165,104,172,104,172,75,134,134,68,389,
            314,390,271,302,241,213,171,188,183,120,
            132,183,95,134,75,65,267,63,231,75,
            322,215,62,110,112,75,27,156,60,351,
            318,252,269,75,266,146,61,85,303,129,
            292,316,134,271,280,87,275,320,273,335,
            323,231,25,235,24,328,239,23,76,41,
            20,73,331,241,18,5,10,96,64,333,
            241,11,159,152,66,99,366,133,121,255,
            105,172,250,22,261,263,3,181,74,226,
            312,126,237,250,19,31,146,30,74,371,
            353,164,265,354,250,15,250,14,250,13,
            250,12,254,250,9,228,146,138,250,8,
            141,250,7,250,5,256,325,269,74,146,
            155,56,74,74,356,358,74,74,362,364,
            74,131,365,131,131,274,126,128,126,88,
            85,43,126,42,126,126,74,41,193,40,
            39,281,283,74,290,195,92,380,257,71,
            259,197,207,276,278,286,77,288,382,279,
            284,440,440,440,440,440,440,440,440,440,
            440,440,440,440,440,440,440,440,440,440,
            385,440,440,440,440,440,440,440,440,440,
            440,440,440,440,440,440,440,440,440,373,
            440,440,440,440,440,440,440,114,440,440
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
            7,9,10,11,0,13,0,15,16,17,
            18,0,1,21,22,23,24,25,26,27,
            28,29,30,31,32,33,34,0,1,2,
            3,0,1,2,3,0,9,10,11,12,
            9,10,11,37,13,0,1,2,0,38,
            0,1,2,0,9,10,11,12,13,9,
            10,11,12,0,1,2,0,1,2,3,
            35,0,9,10,11,0,13,0,0,4,
            5,6,7,0,0,1,2,4,5,6,
            7,0,0,1,2,4,5,6,7,0,
            0,1,0,4,5,6,7,0,0,1,
            8,4,5,6,7,0,0,40,2,4,
            5,6,7,0,0,1,0,4,5,6,
            7,0,0,1,0,4,5,6,7,0,
            0,1,0,4,5,6,7,0,8,0,
            0,1,12,3,0,1,0,3,0,1,
            0,3,36,3,8,39,19,20,8,0,
            36,0,3,0,0,0,0,1,0,8,
            0,8,8,8,0,1,0,1,0,1,
            0,1,14,0,1,0,1,0,0,2,
            0,1,0,0,2,0,1,0,1,0,
            0,2,14,0,0,0,0,14,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            117,374,623,374,143,569,570,584,374,374,
            374,374,374,374,374,374,374,374,374,374,
            374,374,374,374,374,374,374,374,374,374,
            374,374,374,374,374,374,117,374,26,374,
            143,562,563,584,374,374,374,374,374,374,
            374,374,374,374,374,374,374,374,374,374,
            374,374,374,374,374,374,374,374,374,374,
            374,374,1,440,440,569,570,546,547,548,
            549,229,235,205,137,243,32,157,252,149,
            247,440,473,224,221,223,215,173,153,148,
            218,249,147,207,200,206,199,143,529,530,
            584,143,342,485,584,440,774,766,758,531,
            806,798,782,376,790,440,486,487,140,337,
            440,529,530,145,488,489,492,491,490,532,
            533,534,531,440,342,485,143,498,497,584,
            439,70,350,347,343,127,346,2,111,546,
            547,548,549,84,440,562,563,546,547,548,
            549,143,440,498,497,534,534,534,534,143,
            440,255,440,533,533,533,533,143,440,298,
            559,532,532,532,532,143,101,179,384,343,
            343,343,343,143,440,521,440,346,346,346,
            346,143,440,283,28,347,347,347,347,143,
            117,587,440,350,350,350,350,69,559,440,
            143,378,553,584,143,582,131,584,143,298,
            143,584,387,584,559,474,253,355,559,143,
            297,98,584,67,440,53,440,579,134,559,
            440,559,499,559,440,575,97,381,440,475,
            440,372,383,440,514,440,576,102,100,543,
            73,372,79,78,520,72,515,440,476,115,
            440,556,282,440,440,440,440,282
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }

    public interface Asb {
        public final static char asb[] = {0,
            125,180,125,158,121,97,97,121,66,33,
            33,65,90,1,33,121,121,33,66,90,
            33,33,33,33,33,66,29,124,123,97,
            97,97,186,157,66,96,92,96,157,65,
            66,8,186,96,92,157,157,155,157,157,
            66,66,96,157,157,157,96,90,157,66,
            96,186,186,186,186,186,186,121,182,121,
            121,1,97,1,1,155,28,28,28,28,
            28,28,121,38,186,185,121,121,191,121,
            36,185,154,185,154,121,38,151,186,151,
            150,151,153
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
            39,33,34,92,28,72,74,41,47,17,
            43,47,51,1,17,22,49,45,47,53,
            55,60,62,70,4,47,13,33,78,80,
            83,86,65,32,95,16,64,89,32,67,
            94,24,65,16,65,32,32,97,32,32,
            95,94,16,32,32,32,7,99,32,95,
            30,65,65,65,65,65,65,101,33,33,
            103,26,57,26,26,105,11,11,11,11,
            11,11,107,33,65,33,76,109,33,33,
            37,33,111,113,33,33,33,19,65,37,
            115,37,33
        };
    };
    public final static byte nasb[] = Nasb.nasb;
    public final int nasb(int index) { return nasb[index]; }

    public interface Nasr {
        public final static byte nasr[] = {0,
            6,39,0,14,28,0,14,1,13,0,
            5,0,12,26,0,1,3,0,61,62,
            0,37,0,1,18,6,0,49,0,12,
            11,1,0,24,48,0,9,0,23,0,
            45,0,42,0,35,0,7,0,36,0,
            40,0,33,0,32,0,60,59,0,31,
            0,30,0,64,2,0,58,4,0,29,
            0,47,0,46,0,56,0,50,0,1,
            66,0,1,67,0,1,21,0,1,19,
            0,25,0,1,4,0,53,0,52,0,
            10,0,65,0,54,0,51,0,55,0,
            15,0,57,0,63,0
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
            10,11,12,42,46,47
        };
    };
    public final static byte terminalIndex[] = TerminalIndex.terminalIndex;
    public final int terminalIndex(int index) { return terminalIndex[index]; }

    public interface NonterminalIndex {
        public final static byte nonterminalIndex[] = {0,
            0,70,76,77,71,83,0,0,0,67,
            69,72,73,74,81,0,82,84,88,89,
            90,0,0,0,48,49,0,50,51,52,
            53,54,55,0,56,57,58,59,60,61,
            0,62,63,0,64,0,0,65,66,0,
            68,75,0,78,79,80,0,0,85,86,
            0,87,0,0,91,92,93,0
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
            "ERROR_TOKEN",
            "LPG_item",
            "alias_segment",
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
            "defineSpec",
            "macro_name_symbol",
            "macro_segment",
            "terminal_symbol",
            "action_segment",
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
            "start_symbol",
            "terminal",
            "type_declarations",
            "barSymbolList",
            "symbol_pair",
            "recover_symbol"
        };
    };
    public final static String name[] = Name.name;
    public final String name(int index) { return name[index]; }

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

    /**
     * assert(! goto_default);
     */
    public final int ntAction(int state, int sym) {
        return baseAction[state + sym];
    }

    /**
     * assert(! shift_default);
     */
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
