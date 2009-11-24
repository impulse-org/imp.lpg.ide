package org.eclipse.imp.lpg.parser;

public class LPGParserprs implements lpg.runtime.ParseTable, LPGParsersym {
    public final static int ERROR_SYMBOL = 47;
    public final int getErrorSymbol() { return ERROR_SYMBOL; }

    public final static int SCOPE_UBOUND = -1;
    public final int getScopeUbound() { return SCOPE_UBOUND; }

    public final static int SCOPE_SIZE = 0;
    public final int getScopeSize() { return SCOPE_SIZE; }

    public final static int MAX_NAME_LENGTH = 27;
    public final int getMaxNameLength() { return MAX_NAME_LENGTH; }

    public final static int NUM_STATES = 105;
    public final int getNumStates() { return NUM_STATES; }

    public final static int NT_OFFSET = 47;
    public final int getNtOffset() { return NT_OFFSET; }

    public final static int LA_STATE_OFFSET = 597;
    public final int getLaStateOffset() { return LA_STATE_OFFSET; }

    public final static int MAX_LA = 3;
    public final int getMaxLa() { return MAX_LA; }

    public final static int NUM_RULES = 147;
    public final int getNumRules() { return NUM_RULES; }

    public final static int NUM_NONTERMINALS = 68;
    public final int getNumNonterminals() { return NUM_NONTERMINALS; }

    public final static int NUM_SYMBOLS = 115;
    public final int getNumSymbols() { return NUM_SYMBOLS; }

    public final static int SEGMENT_SIZE = 8192;
    public final int getSegmentSize() { return SEGMENT_SIZE; }

    public final static int START_STATE = 201;
    public final int getStartState() { return START_STATE; }

    public final static int IDENTIFIER_SYMBOL = 0;
    public final int getIdentifier_SYMBOL() { return IDENTIFIER_SYMBOL; }

    public final static int EOFT_SYMBOL = 36;
    public final int getEoftSymbol() { return EOFT_SYMBOL; }

    public final static int EOLT_SYMBOL = 36;
    public final int getEoltSymbol() { return EOLT_SYMBOL; }

    public final static int ACCEPT_ACTION = 449;
    public final int getAcceptAction() { return ACCEPT_ACTION; }

    public final static int ERROR_ACTION = 450;
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
            0,0,0,0,0,0,0,1,0,0,
            0,0,0,1,0,1,1,0,0,0,
            0,0,0,0,1,0,0,0,0,1,
            1,1,0,0,1,0,0,0,0,0,
            0,1,0,0,0,0,0,1,0,0,
            1,0,1,1,0,0,1,0,0,1,
            0,0,0,1,1,0,0,1,0,1,
            1,0,0,0,0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            7,35,42,43,36,52,38,51,57,58,
            19,31,34,37,39,40,48,50,53,62,
            63,65,2,3,4,5,6,8,9,10,
            11,12,13,14,15,16,17,18,20,21,
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
            0,0,0,0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            2,0,2,3,3,3,3,3,3,3,
            3,3,3,3,3,3,3,3,3,3,
            3,3,3,3,3,3,0,2,2,1,
            3,2,0,2,4,1,3,1,2,3,
            3,3,3,3,3,1,1,1,1,1,
            1,1,1,1,1,2,2,1,1,1,
            1,1,1,1,2,1,2,1,1,2,
            0,2,2,2,1,2,1,2,4,0,
            1,1,1,2,1,3,1,2,3,1,
            1,1,1,1,1,1,2,2,0,2,
            3,1,2,3,1,3,1,1,1,1,
            2,0,2,1,2,0,1,0,1,1,
            1,2,1,1,1,2,2,0,2,1,
            1,1,1,2,3,1,3,0,2,2,
            0,2,1,0,1,0,2,-10,-12,-74,
            0,0,-15,-28,-16,0,0,0,0,0,
            -26,-34,0,0,-2,0,0,0,0,-40,
            0,0,0,-32,0,0,0,0,-37,0,
            0,0,-45,0,-47,0,-93,0,0,0,
            0,0,0,-59,0,0,0,-63,0,0,
            -1,0,-100,-104,0,-3,0,-5,0,0,
            0,0,0,0,-8,-11,-31,0,-9,0,
            -13,0,-29,0,0,0,-23,0,-14,0,
            0,-19,0,-71,0,0,0,-20,-22,-21,
            0,0,-27,0,0,-24,-76,-25,0,0,
            0,-43,-38,0,0,0,0,0,0,0,
            -6,0,0,0,0,-33,0,0,0,-89,
            0,0,0,0,-39,0,0,0,-4,-44,
            0,-7,0,0,-54,0,0,0,0,-55,
            0,-102,0,-17,0,-18,-30,-35,0,-36,
            0,0,-50,0,0,0,0,0,-41,0,
            0,-42,0,-46,-60,0,0,-48,0,-49,
            0,-51,0,-52,0,0,-53,0,-56,0,
            0,0,0,-57,0,-58,0,-61,0,-62,
            -64,-70,0,0,-73,-65,0,0,-66,-67,
            0,0,-68,0,0,0,-69,-75,0,-77,
            -78,-85,-79,0,-80,0,0,0,-81,0,
            -82,-83,-84,0,-90,0,0,0,-87,-95,
            0,-96,-101,-103,0,-72,-86,-88,-91,-92,
            -94,-97,-98,-99,-105,0,0,0,0,0,
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
            23,23,25,25,26,26,26,26,26,26,
            26,26,26,26,26,26,26,26,26,26,
            26,26,26,26,26,26,26,24,24,48,
            49,49,12,50,50,50,51,51,27,27,
            13,13,13,13,13,13,14,5,5,5,
            5,5,5,5,28,29,29,15,16,16,
            52,31,30,32,33,33,34,34,35,36,
            37,53,53,54,54,55,55,56,56,17,
            57,57,38,11,11,8,8,39,39,19,
            6,6,6,6,6,6,40,40,41,58,
            58,59,60,60,60,18,18,2,2,2,
            2,9,10,10,62,62,63,63,61,61,
            4,42,42,20,20,43,43,21,64,64,
            3,3,44,45,45,22,65,65,47,47,
            66,46,46,67,1,1,7,7,77,77,
            134,253,309,134,147,77,129,83,309,314,
            170,176,76,83,345,119,185,38,341,125,
            5,20,87,309,238,25,55,315,84,5,
            22,198,253,111,17,5,16,233,309,314,
            194,179,183,84,150,6,105,203,115,4,
            126,40,88,224,233,394,161,119,184,56,
            315,39,341,105,203,192,168,234,26,137,
            30,137,395,218,165,206,300,77,252,154,
            63,279,77,96,94,69,121,133,137,77,
            154,142,64,137,66,327,77,267,77,61,
            340,62,240,159,28,150,147,223,275,329,
            266,87,312,298,111,113,242,24,322,280,
            271,338,290,324,285,42,21,334,336,75,
            244,18,132,139,97,244,11,77,134,67,
            5,10,233,65,200,122,216,89,253,23,
            186,106,203,226,147,3,100,379,217,167,
            247,358,253,19,76,257,360,127,253,15,
            253,14,253,13,253,12,380,254,174,253,
            9,147,318,320,253,8,253,7,253,5,
            258,76,184,363,147,263,76,32,365,76,
            76,369,371,76,31,372,361,76,134,373,
            134,231,269,129,86,129,89,57,45,129,
            44,129,129,129,43,273,42,41,40,76,
            281,187,283,76,290,204,259,261,208,209,
            276,278,286,288,279,284,450,78,450,450,
            450,450,450,450,450,450,450,450,450,450,
            387,450,450,389,450,72,450,450,450,450,
            450,450,450,450,450,450,450,450,450,450,
            392,450,450,450,450,450,450,450,450,383,
            450,450,450,450,450,450,450,115,450,450
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
            30,31,32,33,34,35,36,0,1,0,
            3,0,1,2,3,8,9,10,11,12,
            13,14,15,16,17,18,19,20,21,22,
            23,24,25,26,27,28,29,30,31,32,
            33,34,35,36,0,0,0,1,2,4,
            5,6,7,9,10,11,0,13,0,15,
            16,17,18,0,1,21,22,23,24,25,
            26,27,28,29,30,31,32,33,34,35,
            0,1,2,3,0,1,2,3,0,9,
            10,11,12,9,10,11,38,13,0,1,
            2,0,39,0,1,2,0,9,10,11,
            12,13,9,10,11,12,0,1,2,0,
            1,2,3,0,36,9,10,11,0,13,
            0,8,4,5,6,7,0,0,1,2,
            4,5,6,7,0,0,1,2,4,5,
            6,7,0,0,1,0,4,5,6,7,
            0,0,1,8,4,5,6,7,0,0,
            1,41,4,5,6,7,0,0,0,2,
            4,5,6,7,0,0,1,0,4,5,
            6,7,0,0,1,0,4,5,6,7,
            0,8,0,0,1,12,3,0,1,0,
            3,0,1,0,3,37,3,8,40,19,
            20,8,0,0,37,3,0,0,0,1,
            0,8,0,1,8,8,0,1,0,1,
            0,1,0,1,14,0,1,0,0,2,
            0,1,0,0,2,0,1,0,1,0,
            0,2,14,0,0,0,0,14,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            118,384,635,384,144,580,581,595,384,384,
            384,384,384,384,384,384,384,384,384,384,
            384,384,384,384,384,384,384,384,384,384,
            384,384,384,384,384,384,384,118,384,27,
            384,144,573,574,595,384,384,384,384,384,
            384,384,384,384,384,384,384,384,384,384,
            384,384,384,384,384,384,384,384,384,384,
            384,384,384,384,1,450,450,580,581,557,
            558,559,560,227,246,232,138,248,33,154,
            243,161,261,450,484,239,240,238,296,294,
            155,153,229,282,221,149,216,148,219,215,
            144,540,541,595,144,346,496,595,450,788,
            780,772,542,820,812,796,234,804,450,497,
            498,141,362,450,540,541,146,499,500,503,
            502,501,543,544,545,542,450,346,496,144,
            509,508,595,450,449,357,353,349,128,350,
            2,570,557,558,559,560,85,450,573,574,
            557,558,559,560,144,450,509,508,545,545,
            545,545,144,450,297,132,544,544,544,544,
            144,450,162,570,543,543,543,543,144,450,
            532,208,349,349,349,349,144,102,450,391,
            350,350,350,350,144,450,303,29,353,353,
            353,353,144,118,598,71,357,357,357,357,
            70,570,112,144,386,564,595,144,593,99,
            595,144,162,144,595,393,595,570,485,270,
            375,570,144,68,342,595,450,54,450,590,
            135,570,450,586,510,570,98,388,450,486,
            450,382,450,525,390,450,587,103,101,554,
            74,382,80,79,531,73,526,450,487,116,
            450,567,292,450,450,450,450,292
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }

    public interface Asb {
        public final static char asb[] = {0,
            132,189,132,166,128,103,103,128,68,34,
            34,34,67,93,1,34,128,128,34,68,
            93,34,34,34,34,34,68,30,131,130,
            103,103,103,192,165,68,102,98,102,102,
            98,165,67,68,8,192,102,165,165,163,
            165,165,68,68,102,165,165,165,102,93,
            165,68,102,192,192,192,192,192,192,128,
            95,128,128,1,1,103,1,163,29,29,
            29,29,29,29,128,39,192,191,128,128,
            197,128,37,191,162,191,162,128,39,159,
            192,159,158,159,161
        };
    };
    public final static char asb[] = Asb.asb;
    public final int asb(int index) { return asb[index]; }

    public interface Asr {
        public final static byte asr[] = {0,
            1,2,12,9,10,11,0,15,16,17,
            18,21,22,23,24,25,26,27,28,29,
            30,31,32,33,34,35,36,3,12,9,
            10,13,11,1,2,0,12,8,15,16,
            17,18,3,9,10,13,21,22,23,11,
            24,25,26,27,28,29,30,31,32,33,
            34,35,36,1,14,0,1,15,16,17,
            18,3,9,10,13,21,22,23,11,24,
            25,26,27,28,29,30,31,32,33,34,
            35,36,8,0,1,39,0,4,5,6,
            7,2,15,16,17,18,3,9,10,13,
            21,22,23,11,24,25,26,27,28,29,
            30,31,32,33,34,35,36,1,0,38,
            37,15,16,17,18,9,10,13,21,22,
            23,11,24,25,26,27,28,41,29,30,
            31,32,33,34,35,36,0,2,12,8,
            14,1,19,20,3,15,16,17,13,10,
            9,21,22,23,11,24,25,26,27,28,
            30,31,32,33,34,35,29,18,36,0,
            2,4,5,6,7,0,40,37,0
        };
    };
    public final static byte asr[] = Asr.asr;
    public final int asr(int index) { return asr[index]; }

    public interface Nasb {
        public final static byte nasb[] = {0,
            30,29,35,82,38,72,87,40,47,14,
            42,14,47,51,1,14,89,91,53,47,
            57,55,49,59,64,7,47,4,29,93,
            44,10,74,70,28,85,13,69,79,13,
            70,28,66,84,16,70,13,28,28,95,
            28,28,85,84,13,28,28,28,22,97,
            28,85,26,70,70,70,70,70,70,99,
            29,29,101,18,18,61,18,103,107,107,
            107,107,107,107,105,29,70,29,77,109,
            29,29,20,29,111,113,29,29,29,32,
            70,20,115,20,29
        };
    };
    public final static byte nasb[] = Nasb.nasb;
    public final int nasb(int index) { return nasb[index]; }

    public interface Nasr {
        public final static byte nasr[] = {0,
            6,39,0,14,27,0,16,29,0,1,
            67,0,1,3,0,1,19,6,0,10,
            0,16,1,15,0,14,13,1,0,24,
            0,61,62,0,25,48,0,49,0,45,
            0,42,0,1,66,0,7,0,32,0,
            40,0,36,0,33,0,34,0,31,0,
            60,59,0,30,0,58,4,0,64,2,
            0,47,0,1,22,0,56,0,1,20,
            0,26,0,1,4,0,46,0,38,0,
            37,0,50,0,53,0,52,0,12,0,
            65,0,54,0,51,0,5,0,55,0,
            17,0,57,0,63,0
        };
    };
    public final static byte nasr[] = Nasr.nasr;
    public final int nasr(int index) { return nasr[index]; }

    public interface TerminalIndex {
        public final static byte terminalIndex[] = {0,
            45,44,21,1,2,3,4,46,22,23,
            28,20,24,5,14,15,16,17,18,19,
            25,26,27,29,30,31,32,33,35,36,
            37,38,39,40,41,42,7,6,8,9,
            34,10,11,12,43,47,48
        };
    };
    public final static byte terminalIndex[] = TerminalIndex.terminalIndex;
    public final int terminalIndex(int index) { return terminalIndex[index]; }

    public interface NonterminalIndex {
        public final static byte nonterminalIndex[] = {0,
            0,71,77,78,72,84,0,83,0,0,
            60,68,70,73,74,75,82,0,85,89,
            90,91,0,0,0,49,50,0,51,52,
            53,54,55,56,0,57,58,59,61,62,
            0,63,64,0,65,0,0,66,67,0,
            69,76,0,79,80,81,0,0,86,87,
            0,88,0,0,92,93,94,0
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
            "RULES_KEY",
            "SOFT_KEYWORDS_KEY",
            "START_KEY",
            "TERMINALS_KEY",
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
