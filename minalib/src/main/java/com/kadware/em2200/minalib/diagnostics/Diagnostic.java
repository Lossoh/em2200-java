/*
 * Copyright (c) 2018 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.em2200.minalib.diagnostics;

import com.kadware.em2200.minalib.Locale;

/**
 * Base class for diagnostic messages
 */
public abstract class Diagnostic {

    public enum Level {
        Directive,
        Duplicate,
        Error,
        Fatal,
        Quote,
        Relocation,
        Truncation,
        UndefinedReference,
        Value,
    }

    public final Locale _locale;
    public final String _message;

    /**
     * Constructor
     * @param locale locale associated with this diagnostic
     * @param message message associated with this diagnostic
     */
    public Diagnostic(
        final Locale locale,
        final String message
    ) {
        _locale = locale;
        _message = message;
    }

    /**
     * Get the level associated with this instance
     * @return value
     */
    public abstract Level getLevel(
    );

    /**
     * Private getter, retrieve severity level as a displayable character
     * @return value
     */
    public final char getLevelIndicator(
    ) {
        return getLevelIndicator(getLevel());
    };

    /**
     * Construct and return a message to be displayed, describing this diagnostic
     * <p>
     * @return
     */
    public final String getMessage(
    ) {
        return String.format("%c%s:%s", getLevelIndicator(), _locale.toString(), _message);
    }

    /**
     * Retrieves the single-character level indicator for this particular Diagnostic Level
     * <p>
     * @param level
     * <p>
     * @return
     */
    public static char getLevelIndicator(
        final Level level
    ) {
        switch (level) {
            case Directive:             return 'I';
            case Duplicate:             return 'D';
            case Error:                 return 'E';
            case Fatal:                 return 'F';
            case Quote:                 return 'Q';
            case Relocation:            return 'R';
            case Truncation:            return 'T';
            case UndefinedReference:    return 'U';
            case Value:                 return 'V';
        }
        return '?';
    }
}
