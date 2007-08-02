package org.eclipse.imp.lpg.parser;

public class LPGKWLexerprs implements lpg.runtime.ParseTable, LPGKWLexersym {

    public interface IsNullable {
        public final static byte isNullable[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0
        };
    };
    public final static byte isNullable[] = IsNullable.isNullable;
    public final boolean isNullable(int index) { return isNullable[index] != 0; }

    public interface ProsthesesIndex {
        public final static byte prosthesesIndex[] = {0,
            2,1
        };
    };
    public final static byte prosthesesIndex[] = ProsthesesIndex.prosthesesIndex;
    public final int prosthesesIndex(int index) { return prosthesesIndex[index]; }

    public interface IsKeyword {
        public final static byte isKeyword[] = {0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0
        };
    };
    public final static byte isKeyword[] = IsKeyword.isKeyword;
    public final boolean isKeyword(int index) { return isKeyword[index] != 0; }

    public interface BaseCheck {
        public final static byte baseCheck[] = {0,
            6,4,7,24,10,12,6,4,6,4,
            4,7,8,8,11,7,8,9,6,7,
            10,8,6,6,9,6
        };
    };
    public final static byte baseCheck[] = BaseCheck.baseCheck;
    public final int baseCheck(int index) { return baseCheck[index]; }
    public final static byte rhs[] = baseCheck;
    public final int rhs(int index) { return rhs[index]; };

    public interface BaseAction {
        public final static char baseAction[] = {
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,1,1,1,
            1,1,1,1,1,1,1,36,29,27,
            1,8,17,25,34,55,31,57,59,12,
            29,44,61,63,62,65,64,68,14,67,
            73,77,74,80,78,84,82,45,89,91,
            83,95,96,97,101,102,104,109,111,113,
            116,105,118,119,103,121,125,123,126,130,
            132,134,137,139,131,140,141,143,151,144,
            154,155,157,145,159,160,165,166,168,170,
            172,174,175,176,47,179,182,184,187,193,
            185,196,198,191,199,202,203,205,209,207,
            210,211,215,219,220,224,218,228,225,231,
            234,230,237,235,239,243,246,247,238,251,
            249,256,259,257,262,263,265,266,267,269,
            275,272,277,279,282,284,285,289,288,294,
            297,290,299,165,165
        };
    };
    public final static char baseAction[] = BaseAction.baseAction;
    public final int baseAction(int index) { return baseAction[index]; }
    public final static char lhs[] = baseAction;
    public final int lhs(int index) { return lhs[index]; };

    public interface TermCheck {
        public final static byte termCheck[] = {0,
            0,1,2,3,0,5,6,0,8,9,
            10,0,5,0,3,4,0,1,5,19,
            20,10,22,12,0,1,0,3,0,1,
            0,3,16,0,6,0,25,4,14,9,
            10,8,12,0,0,2,0,0,2,3,
            7,7,26,18,0,1,0,1,0,15,
            0,0,0,0,0,7,0,0,8,7,
            3,8,0,0,13,11,0,0,12,0,
            1,0,0,0,11,8,14,4,0,13,
            0,3,11,11,0,0,0,2,4,9,
            0,0,0,0,0,5,3,6,0,1,
            0,15,0,1,4,0,12,0,0,1,
            0,6,0,6,0,0,24,7,4,0,
            0,0,10,0,9,4,0,4,0,0,
            0,5,0,0,0,6,17,5,8,11,
            0,21,2,0,0,2,0,13,0,0,
            2,7,6,4,0,0,23,0,3,0,
            1,0,5,0,0,0,3,3,0,8,
            16,0,4,0,0,2,0,1,0,14,
            0,10,0,1,10,0,1,0,0,9,
            3,0,0,5,0,3,0,6,0,0,
            0,7,0,5,0,1,6,0,0,0,
            14,3,3,0,0,16,9,0,1,0,
            0,8,2,0,0,2,0,0,0,15,
            2,12,0,7,10,0,0,2,0,7,
            0,1,6,5,17,0,0,2,0,1,
            4,0,0,2,0,0,0,3,0,3,
            2,0,7,11,0,1,0,1,0,0,
            9,0,1,0,0,2,2,0,0,0,
            3,13,4,0,5,2,0,1,0,0,
            2,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0,0,0,
            0,0,0,0,0,0,0,0
        };
    };
    public final static byte termCheck[] = TermCheck.termCheck;
    public final int termCheck(int index) { return termCheck[index]; }

    public interface TermAction {
        public final static char termAction[] = {0,
            165,39,31,32,165,33,36,165,41,40,
            34,165,42,165,58,57,165,44,72,38,
            37,59,35,60,165,47,165,46,165,63,
            165,61,43,165,62,165,56,48,45,53,
            51,49,52,165,165,64,165,165,119,120,
            65,175,164,30,165,50,165,54,165,176,
            165,165,165,165,165,55,165,165,66,67,
            71,70,165,165,68,69,165,165,73,165,
            77,165,165,165,76,78,74,79,165,75,
            165,81,80,82,165,165,165,84,83,173,
            165,165,165,165,165,167,87,86,165,88,
            165,85,165,90,89,165,92,165,165,94,
            165,91,165,93,165,165,95,96,97,165,
            165,165,98,165,99,101,165,102,165,165,
            165,103,165,165,165,106,100,189,107,104,
            165,105,188,165,165,191,165,111,165,165,
            184,109,110,112,165,165,108,165,114,165,
            116,165,115,165,165,165,118,174,165,117,
            113,165,121,165,165,166,165,123,165,172,
            165,122,165,124,125,165,185,165,165,127,
            126,165,165,181,165,129,165,128,165,165,
            165,130,165,177,165,168,133,165,165,165,
            131,187,134,165,165,132,136,165,182,165,
            165,135,179,165,165,178,165,165,165,137,
            190,138,165,139,140,165,165,183,165,141,
            165,144,142,145,143,165,165,186,165,146,
            147,165,165,170,165,165,165,180,165,150,
            171,165,149,148,165,151,165,153,165,165,
            152,165,155,165,165,156,157,165,165,165,
            159,154,158,165,162,160,165,161,165,165,
            169
        };
    };
    public final static char termAction[] = TermAction.termAction;
    public final int termAction(int index) { return termAction[index]; }
    public final int asb(int index) { return 0; }
    public final int asr(int index) { return 0; }
    public final int nasb(int index) { return 0; }
    public final int nasr(int index) { return 0; }
    public final int terminalIndex(int index) { return 0; }
    public final int nonterminalIndex(int index) { return 0; }
    public final int scopePrefix(int index) { return 0;}
    public final int scopeSuffix(int index) { return 0;}
    public final int scopeLhs(int index) { return 0;}
    public final int scopeLa(int index) { return 0;}
    public final int scopeStateSet(int index) { return 0;}
    public final int scopeRhs(int index) { return 0;}
    public final int scopeState(int index) { return 0;}
    public final int inSymb(int index) { return 0;}
    public final String name(int index) { return null; }
    public final int getErrorSymbol() { return 0; }
    public final int getScopeUbound() { return 0; }
    public final int getScopeSize() { return 0; }
    public final int getMaxNameLength() { return 0; }

    public final static int
           NUM_STATES        = 135,
           NT_OFFSET         = 29,
           LA_STATE_OFFSET   = 191,
           MAX_LA            = 0,
           NUM_RULES         = 26,
           NUM_NONTERMINALS  = 2,
           NUM_SYMBOLS       = 31,
           SEGMENT_SIZE      = 8192,
           START_STATE       = 27,
           IDENTIFIER_SYMBOL = 0,
           EOFT_SYMBOL       = 26,
           EOLT_SYMBOL       = 30,
           ACCEPT_ACTION     = 164,
           ERROR_ACTION      = 165;

    public final static boolean BACKTRACK = false;

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

    public final int originalState(int state) { return 0; }
    public final int asi(int state) { return 0; }
    public final int nasi(int state) { return 0; }
    public final int inSymbol(int state) { return 0; }

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
