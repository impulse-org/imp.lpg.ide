package org.eclipse.imp.lpg.editor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.imp.lpg.LPGRuntimePlugin;
import org.eclipse.imp.lpg.preferences.LPGConstants;
import org.eclipse.imp.preferences.IPreferencesService;
import org.eclipse.imp.preferences.PreferenceConstants;
import org.eclipse.imp.preferences.PreferencesService;
import org.eclipse.imp.services.IAutoEditStrategy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;

public class LPGAutoEditStrategy implements IAutoEditStrategy {
    private static final char[] SPACES= "                                                                    ".toCharArray();

    private IPreferencesService fPrefsService= new PreferencesService(null, LPGRuntimePlugin.getInstance().getLanguageID());

    private static final Pattern NEXT_SEGMENT_PATTERN= Pattern.compile("%([^eE]|([eE][^nN])|([eE][nN][^dD]))");

    /**
     * Cache of the current preference setting
     */
    private int fIndentWidth;

    /**
     * Cache of the current preference setting
     */
    private boolean fSpacesForTabs;

    /**
     * Cache of the current preference setting
     */
    private int fTabWidth;

    public void customizeDocumentCommand(IDocument doc, DocumentCommand cmd) {
        if (cmd.doit == false)
            return;
        if (cmd.length == 0 && cmd.text != null && isLineDelimiter(doc, cmd.text)) {
            smartIndentAfterNewline(doc, cmd);
        } else if (cmd.text.length() == 1 && cmd.text.charAt(0) == '\t') {
            smartIndentOnTab(doc, cmd);
        }
    }

    private void smartIndentAfterNewline(IDocument doc, DocumentCommand cmd) {
        try {
            fIndentWidth= fPrefsService.getIntPreference(LPGConstants.P_INDENTWIDTH);
            fSpacesForTabs= fPrefsService.getBooleanPreference(PreferenceConstants.P_SPACES_FOR_TABS);
            fTabWidth= fPrefsService.getIntPreference(PreferenceConstants.P_TAB_WIDTH);

            IRegion r= doc.getLineInformation(doc.getLineOfOffset(cmd.offset));
            String thisLine= doc.get(r.getOffset(), r.getLength());
            int thisLineNum= doc.getLineOfOffset(cmd.offset);
            int thisLineStart= doc.getLineOffset(thisLineNum);

            if (thisLine.startsWith("%")) {
                // Close a segment if not already closed and indent
                String contents= doc.get();
                int endPos= contents.indexOf("%End", cmd.offset);
                Matcher nextSegMatcher= NEXT_SEGMENT_PATTERN.matcher(contents);
                boolean hasNextSeg= nextSegMatcher.find(cmd.offset);
                int nextSegOffset= (hasNextSeg ? nextSegMatcher.start() : doc.getLength());
                boolean segClosed= endPos < nextSegOffset;
                cmd.text= "\n" + leadingSpace(fIndentWidth);
                if (!segClosed) {
                    cmd.text= cmd.text + "\n%End";
                }
                cmd.caretOffset= cmd.offset;
                cmd.shiftsCaret= true;
            } else if (startsWithIgnoreLeadingWhitespace(thisLine, "--")) {
                int indent= calculateLinePrefixTo(doc, thisLineNum, "-- ");
                cmd.text= "\n" + leadingSpace(indent) + "-- ";
            } else if (thisLine.contains("/.")) {
                boolean blockClosed= blockOpenAt(cmd.offset, doc.get());
                int indent= calculateLinePrefixTo(doc, doc.getLineOfOffset(r.getOffset()), "/.");

                cmd.text= "\n" + leadingSpace(indent+2);
                if (!blockClosed) {
                    cmd.text= cmd.text + "\n" + leadingSpace(indent+1) + "./";
                }
                cmd.caretOffset= cmd.offset;
                cmd.shiftsCaret= true;
            } else if (startsWithIgnoreLeadingWhitespace(thisLine, "|")) {
                int indent= calculateLeadingWhitespaceAtOffset(doc, thisLineStart);
                int followingOffset= skipWhite(indent+1, thisLine);
                cmd.text= "\n" + leadingSpace(indent) + "| " + leadingSpace(followingOffset - indent - 2);
            } else {
                // Just copy indentation of previous non-blank line
                int prevNonBlankLineStart= findOffsetOfPrevNonBlankLine(doc, thisLineStart);
                int indent= calculateLeadingWhitespaceAtOffset(doc, prevNonBlankLineStart);
                cmd.text= "\n" + leadingSpace(indent);
            }
        } catch (BadLocationException e) {
        }
    }

    private String leadingSpace(int indentAmount) {
        StringBuilder sb= new StringBuilder();
        if (fSpacesForTabs) {
            for(int i=0; i < indentAmount; i++) {
                sb.append(' ');
            }
        } else {
            for(int i=0; i < indentAmount / fTabWidth; i++) {
                sb.append('\t');
            }
            for(int i=0; i < indentAmount % fTabWidth; i++) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

    private boolean blockOpenAt(int offset, String contents) {
        int nextBlockOpen= contents.indexOf("/.", offset);
        int nextBlockClosed= contents.indexOf("./", offset);
        boolean blockClosed= nextBlockClosed < nextBlockOpen;

        return blockClosed;
    }

    private int findOffsetOfPrevNonBlankLine(IDocument doc, int offset) throws BadLocationException {
        int lineNum= doc.getLineOfOffset(offset) + 1;
        do {
            IRegion r= doc.getLineInformation(--lineNum);
            String line= doc.get(r.getOffset(), r.getLength());
            if (line.trim().length() > 0)
                break;
        } while (lineNum > 0);
        return doc.getLineOffset(lineNum);
    }

    private int skipWhite(int idx, String s) {
        while (idx < s.length() && Character.isWhitespace(s.charAt(idx))) {
            idx++;
        }
        return idx;
    }

    private boolean startsWithIgnoreLeadingWhitespace(String line, String prefix) {
        int afterWhite= 0;
        while (afterWhite < line.length() && Character.isWhitespace(line.charAt(afterWhite))) {
            afterWhite++;
        }
        return line.substring(afterWhite).startsWith(prefix);
    }

    private String getLineContaining(int offset, IDocument doc) throws BadLocationException {
        int lineNum= doc.getLineOfOffset(offset);
        return doc.get(doc.getLineOffset(lineNum), doc.getLineLength(lineNum));
    }

    private String getLine(int lineNum, IDocument doc) throws BadLocationException {
        return doc.get(doc.getLineOffset(lineNum), doc.getLineLength(lineNum));
    }

    private void smartIndentOnTab(IDocument doc, DocumentCommand cmd) {
        try {
            int lineNum= doc.getLineOfOffset(cmd.offset);
            IRegion r= doc.getLineInformation(lineNum);
            int lineStart= doc.getLineOffset(lineNum);
            String line= doc.get(r.getOffset(), r.getLength());
            int lineToHereLen= cmd.offset - lineStart;

            fIndentWidth= fPrefsService.getIntPreference(LPGConstants.P_INDENTWIDTH);
            fTabWidth= fPrefsService.getIntPreference(PreferenceConstants.P_TAB_WIDTH);
            fSpacesForTabs= fPrefsService.getBooleanPreference(PreferenceConstants.P_SPACES_FOR_TABS);

            if (line.substring(0, lineToHereLen).trim().length() == 0 && !Character.isWhitespace(doc.getChar(cmd.offset))) {
                // we're at the first non-whitespace character on the line - push the indent in one more level
                cmd.text= leadingSpace(fIndentWidth);
            } else {
                int spacesToInsert= 0;
                int lenToReplace= calculateLeadingWhitespaceCharsAtOffset(doc, lineStart);

                if (lineNum > 0 && isSegmentStart(getLine(lineNum-1, doc))) {
                    // segment start
                    int prevLineLeadingWhitespace= calculateLeadingWhitespaceOfLine(doc, lineNum-1);
                    spacesToInsert= prevLineLeadingWhitespace + fIndentWidth;
                } else if (lineNum > 0 && isBlockStart(getLine(lineNum-1, doc))) {
                    // open action block
                    int prevLineBlockStart= calculateLinePrefixTo(doc, lineNum-1, "/.");
                    spacesToInsert= prevLineBlockStart + 2;
                } else if (lineNum > 0 && blockOpenAt(cmd.offset, doc.get()) && line.trim().startsWith("./")) {
                    // close action block
                    int prevLineLeadingWhitespace= calculateLeadingWhitespaceOfLine(doc, lineNum-1);
                    spacesToInsert= prevLineLeadingWhitespace - 1;
                } else if (lineNum > 0 && isNonTermDef(getLine(lineNum-1, doc)) && line.trim().startsWith("|")) {
                    // new rule right after non-terminal def line
                    spacesToInsert= calculateLinePrefixTo(doc, lineNum-1, "::=");
                } else if (lineNum > 0 && line.substring(0, lineToHereLen).trim().length() == 0) {
                    // the caret is surrounded by the leading whitespace of this line -
                    // indent to whatever degree makes sense
                    int prevNonBlankLineStart= findOffsetOfPrevNonBlankLine(doc, lineStart-1);
                    spacesToInsert= calculateLeadingWhitespaceAtOffset(doc, prevNonBlankLineStart);
                }
                if (spacesToInsert != 0) {
                    cmd.offset= lineStart;
                    cmd.length= lenToReplace;
                    cmd.text= leadingSpace(spacesToInsert);
                }
            }
        } catch (BadLocationException e) {
        }
    }

    private boolean isNonTermDef(String line) {
        return line.contains("::=");
    }

    private boolean isSegmentStart(String line) {
        final Pattern SEGMENT_START_PATTERN= Pattern.compile("%[a-zA-Z]+");
        Matcher m= SEGMENT_START_PATTERN.matcher(line.trim());
        return m.matches();
    }

    private boolean isBlockStart(String line) {
        return line.contains("/.");
    }

    /**
     * @param doc the document
     * @param lineNum the number of the given line
     * @param suffix the text to search for in the given line
     * @return the number of space-equivalent characters leading up to the given suffix in the given line
     * @throws BadLocationException
     */
    private int calculateLinePrefixTo(IDocument doc, int lineNum, String suffix) throws BadLocationException {
        IRegion r= doc.getLineInformation(lineNum);
        String prevLine= doc.get(r.getOffset(), r.getLength());
        int prefixLen= prevLine.indexOf(suffix);
        int result= prefixLen;
        for(int i=0; i < prefixLen; i++) {
            if (prevLine.charAt(i) == '\t') {
                result += (fTabWidth - 1);
            }
        }
        return result;
    }

    /**
     * @return the number of whitespace characters at the beginning of the given line,
     * i.e., <strong>without</strong> taking tab expansion into account
     * @param lineStart the offset of the beginning of the line
     * @param doc the document
     * @throws BadLocationException
     */
    private int calculateLeadingWhitespaceCharsAtOffset(IDocument doc, int lineStart) throws BadLocationException {
        int idx= lineStart;
        int chars= 0;
        while (idx < doc.getLength() && Character.isWhitespace(doc.getChar(idx)) && doc.getChar(idx) != '\n') {
            chars++;
            idx++;
        }
        return chars;
    }

    /**
     * @return the number of spaces equivalent to the whitespace characters at the beginning of the given line,
     * i.e., taking into account tab expansion (if enabled by the corresponding preference)
     * @param lineStart the offset of the beginning of the line
     * @param doc the document
     * @throws BadLocationException
     */
    private int calculateLeadingWhitespaceAtOffset(IDocument doc, int lineStart) throws BadLocationException {
        int idx= lineStart;
        int white= 0;
        while (idx < doc.getLength() && Character.isWhitespace(doc.getChar(idx)) && doc.getChar(idx) != '\n') {
            if (doc.getChar(idx) == '\t') {
                white += fTabWidth;
            } else {
                white++;
            }
            idx++;
        }
        return white;
    }

    /**
     * @param doc
     * @param lineNum the number of the given line
     * @return the number of spaces equivalent to the whitespace characters at the beginning of the given line,
     * i.e., taking into account tab expansion (if enabled by the corresponding preference)
     * @throws BadLocationException
     */
    private int calculateLeadingWhitespaceOfLine(IDocument doc, int lineNum) throws BadLocationException {
        if (lineNum < 0) { return 0; }
        int lineStart= doc.getLineOffset(lineNum);

        return calculateLeadingWhitespaceAtOffset(doc, lineStart);
    }

    /**
     * @return true if the given text is one of the legal line delimiters, under the configuration
     * of the given document
     */
    private boolean isLineDelimiter(IDocument doc, String text) {
        String[] delimiters= doc.getLegalLineDelimiters();
        if (delimiters != null) { return TextUtilities.equals(delimiters, text) > -1; }
        return false;
    }
}
