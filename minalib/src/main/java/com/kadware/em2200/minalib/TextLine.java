/*
 * Copyright (c) 2018-2019 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.em2200.minalib;

import com.kadware.em2200.minalib.diagnostics.ErrorDiagnostic;
import com.kadware.em2200.minalib.diagnostics.QuoteDiagnostic;
import com.kadware.em2200.minalib.diagnostics.Diagnostics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a line of source code
 */
class TextLine {

    public static class GeneratedWord{
        public final int _lcIndex;
        public final int _lcOffset;
        public final RelocatableWord36 _word;

        public GeneratedWord(
            final int lcIndex,
            final int lcOffset,
            final RelocatableWord36 word
        ) {
            _lcIndex = lcIndex;
            _lcOffset = lcOffset;
            _word = word;
        }
    }

    //  line number of this line of text
    final int _lineNumber;

    //  source code for this line of text
    final String _text;

    //  parsed TextField objects parsed from the line of text - may be empty
    final ArrayList<TextField> _fields = new ArrayList<>();

    //  Diagnostic objects pertaining to this line of text
    final Diagnostics _diagnostics = new Diagnostics();

    //  Words generated for this line of text
    final List<GeneratedWord> _generatedWords = new LinkedList<>();


    /**
     * Constructor
     * @param lineNumber line number of this object
     * @param text original text for this object
     */
    TextLine(
        final int lineNumber,
        final String text
    ) {
        _lineNumber = lineNumber;
        _text = text;
    }

    /**
     * Getter
     * @param index index of field
     * @return text field
     */
    TextField getField(
        final int index
    ) {
        if (index < _fields.size()) {
            return _fields.get(index);
        }
        return null;
    }

    /**
     * Add a reference to a word generated for this line of text
     * @param lcIndex location counter index
     * @param lcOffset location counter offset
     * @param word word generated (including any relocation information)
     */
    void appendWord(
        final int lcIndex,
        final int lcOffset,
        final RelocatableWord36 word
    ) {
        _generatedWords.add(new GeneratedWord(lcIndex, lcOffset, word));
    }

    /**
     * Parses the text into TextField objects
     */
    void parseFields(
    ) {
        //  We should only ever be called once, but just in case...
        _diagnostics.clear();
        _fields.clear();

        //  Create clean text string by removing all commentary and trailing whitespace.
        String cleanText = removeComments(_text);

        //  Get ready for iterating character-by-character over the line of code
        int tx = 0;

        //  If first character of the clean text is whitespace, skip all the whitespace,
        //  and generate a null entry in the TextField array list.
        if (!cleanText.isEmpty() && (cleanText.charAt(tx) == ' ')) {
            ++tx;
            while ((tx < cleanText.length()) && (cleanText.charAt(tx) == ' ')) {
                ++tx;
            }
            _fields.add(null);
        }

        //  Now we start parsing the clean text into fields, delimited by occurrences of one or more blank characters.
        //  The presumption is, at the top of the while loop, tx does NOT index a field-delimiting blank character.
        //  Note that we do NOT scan for syntax errors here - in particular, we are pretty lax with where parenthesis
        //  and especially quote delimiters are located.  As long as they are balanced properly, we are happy.
        int parenLevel = 0;
        boolean prevComma = false;
        boolean prevSign = false;
        boolean quoted = false;
        StringBuilder sb = new StringBuilder();
        Locale locale = new Locale(_lineNumber, tx + 1);
        while (tx < cleanText.length()) {
            char ch = cleanText.charAt(tx++);

            if (quoted) {
                sb.append(ch);
            } else {
                if ((parenLevel == 0) && (ch == ' ')) {
                    //  We have found an unquoted blank.  If the previous character was a comma,
                    //  this is whitespace embedded within a field, and should *not* terminate the field.
                    if (prevComma) {
                        sb.append(ch);
                        while ((tx < cleanText.length()) && (cleanText.charAt(tx) == ' ')) {
                            sb.append(' ');
                            ++tx;
                        }
                    } else if (prevSign) {
                        //  This is incidental whitespace following a unary prefix sign character.
                        //  Skip it, and any subsequent contiguous whitespace.
                        sb.append(ch);
                        while ((tx < cleanText.length()) && (cleanText.charAt(tx) == ' ')) {
                            sb.append(' ');
                            ++tx;
                        }
                    } else {
                        //  We've reached the end of the current field.
                        //  Create a TextField object, then parse the field text into subfields.
                        String fieldText = sb.toString().trim();
                        TextField field = new TextField(locale, fieldText);
                        _fields.add(field);
                        Diagnostics parseDiags = field.parseSubfields();
                        _diagnostics.append(parseDiags);

                        //  Skip field-delimiting whitespace
                        while ((tx < cleanText.length()) && (cleanText.charAt(tx) == ' ')) {
                            ++tx;
                        }
                        sb = new StringBuilder();
                        locale = new Locale(_lineNumber, tx + 1);
                    }
                } else {
                    sb.append(ch);

                    //  Is this a leading '+' or '-'?  If so, skip whitespace
                    if ((sb.length() ==1) && ((ch == '+') || (ch == '-'))) {
                        while ((tx < cleanText.length()) && (cleanText.charAt(tx) == ' ')) {
                            sb.append(" ");
                            ++tx;
                        }
                    }

                    //  Handle opening-closing parentheses
                    if (ch == '(') {
                        ++parenLevel;
                    } else if (ch == ')') {
                        if (parenLevel == 0) {
                            Locale diagLoc = new Locale(_lineNumber, tx);
                            _diagnostics.append(new ErrorDiagnostic(diagLoc, "Too many closing parentheses"));
                            return;
                        }
                        --parenLevel;
                    }
                }
            }

            if (ch == '\'') {
                quoted = !quoted;
            }

            prevComma = (ch == ',');
            prevSign = (sb.length() == 1) && ((ch == '+') || (ch == '-'));
        }

        String fieldText = sb.toString();
        fieldText = fieldText.trim();
        if (fieldText.length() > 0) {
            TextField field = new TextField(locale, fieldText);
            _fields.add(field);
            Diagnostics parseDiags = field.parseSubfields();
            _diagnostics.append(parseDiags);
        }

        Locale endloc = new Locale(_lineNumber, tx - 1);
        if (quoted) {
            _diagnostics.append(new QuoteDiagnostic(endloc, "Unterminated string"));
        }

        if (parenLevel > 0) {
            _diagnostics.append(new ErrorDiagnostic(endloc, "Unterminated parenthesized expression"));
        }
    }

    /**
     * Remove comment from assembler text, if it exists.
     * Comments are signaled by space-period-space, or a period-space in the first two columns,
     * or a space-period in the last two columns, or by a single column containing a space.
     * @param text input text to be scanned
     * @return copy of input text with commentary removed, or the original text if no comment was found.
     */
    static String removeComments(
        final String text
    ) {
        if (text.equals(".") || text.startsWith(". ")) {
            return "";
        }

        boolean quoted = false;
        boolean prevSpace = true;
        int tx = 0;
        while (tx < text.length()) {
            char ch = text.charAt(tx++);
            if (!quoted) {
                if ((ch == '.') && prevSpace) {
                    if ((tx == text.length()) || (text.charAt(tx) == ' ')) {
                        return text.substring(0, tx - 2);
                    }
                }
            }

            prevSpace = (ch == ' ');
            if (ch == '\'') {
                quoted = !quoted;
            }
        }

        //  No comment found
        return text;
    }
}
