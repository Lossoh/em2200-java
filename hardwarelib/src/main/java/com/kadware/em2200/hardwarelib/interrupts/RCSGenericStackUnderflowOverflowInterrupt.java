/*
 * Copyright (c) 2018 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.em2200.hardwarelib.interrupts;

import com.kadware.em2200.baselib.Word36;

/**
 * Represents a particular machine interrupt class
 * Occurs when a stack underflow or overflow condition is detected.
 * Generally during BUY or SELL, but possibly also during LMJ, LIJ, LBJ.
 */
public class RCSGenericStackUnderflowOverflowInterrupt extends MachineInterrupt {

    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Nested enumerations
    //  ----------------------------------------------------------------------------------------------------------------------------

    public enum Reason {
        Overflow(0),
        Underflow(1);

        private final short _code;

        Reason(
            final int code
        ) {
            _code = (short)code;
        }

        public short getCode(
        ) {
            return _code;
        }
    };


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Class attributes
    //  ----------------------------------------------------------------------------------------------------------------------------

    private final byte _baseRegister;       //  5 bits significant
    private final Reason _reason;
    private final int _relativeAddress;     //  24 bits significant


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Constructors
    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Constructor
     * <p>
     * @param reason
     * @param baseRegister
     * @param relativeAddress
     */
    public RCSGenericStackUnderflowOverflowInterrupt(
        final Reason reason,
        final int baseRegister,
        final int relativeAddress
    ) {
        super(InterruptClass.Diagnostic, ConditionCategory.Fault, Synchrony.Pended, Deferrability.Exigent, InterruptPoint.MidExecution);
        _reason = reason;
        _baseRegister = (byte)(baseRegister & 037);
        _relativeAddress = relativeAddress & 077777777;
    }


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Accessors
    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Getter
     * <p>
     * @return
     */
    public byte getBaseRegister(
    ) {
        return _baseRegister;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public Reason getReason(
    ) {
        return _reason;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public int getRelativeAddress(
    ) {
        return _relativeAddress;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    @Override
    public Word36 getInterruptStatusWord1(
    ) {
        Word36 result = new Word36(_relativeAddress);
        result.setS1(_baseRegister);
        return result;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    @Override
    public byte getShortStatusField(
    ) {
        return (byte)_reason.getCode();
    }


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Instance methods
    //  ----------------------------------------------------------------------------------------------------------------------------


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Static methods
    //  ----------------------------------------------------------------------------------------------------------------------------
}
