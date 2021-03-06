/*
 * Copyright (c) 2018 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.em2200.hardwarelib.interrupts;

import com.kadware.em2200.baselib.Word36;

/**
 * Abstract base class representing a Machine Interrupt.
 * Extends Exception so that the various internal methods in InstructionProcessor can throw these instead of
 * returning this or that value.  We expect only the higher (highest?) level methods to catch these and
 * invoke raiseInterrupt() for them.
 */
public abstract class MachineInterrupt extends Exception {

    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Nested enumerations
    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Defines the priority of the various interrupt classes, lowest number representing the highest priority.
     * Normally there is only one interrupt pending (if any), but the priority defines which interrupt is serviced
     * when there are multiple interrupts.
     */
    public enum InterruptClass {
        HardwareDefault(000),
        HardwareCheck(001),
        Diagnostic(002),
        ReferenceViolation(010),
        AddressingException(011),
        TerminalAddressingException(012),
        RCSGenericStackUnderflowOverflow(013),
        Signal(014),
        TestAndSet(015),
        InvalidInstruction(016),
        PageException(017),
        ArithmeticException(020),
        DataException(021),
        OperationTrap(022),
        Breakpoint(023),
        QuantumTimer(024),
        SoftwareBreak(030),
        JumpHistoryFull(031),
        Dayclock(033),
        PerformanceMonitoring(034),
        InitialProgramLoad(035),
        UPIInitial(036),
        UPINormal(037);

        private final short _code;

        /**
        * Constructor
        * <p>
        * @param code
        */
        InterruptClass(
            final int code
        ) {
            _code = (short)code;
        }

        /**
         * Getter
         * <p>
         * @return
         */
        public short getCode(
        ) {
            return _code;
        }
    };

    public enum ConditionCategory {
        None(0),
        Fault(1),
        NonFault(2);

        public final short _code;

        /**
         * Constructor
         * <p>
         * @param code
         */
        ConditionCategory(
            final int code
        ) {
            _code = (short)code;
        }

        /**
         * Getter
         * <p>
         * @return
         */
        public short getCode(
        ) {
            return _code;
        }
    };

    public enum Deferrability {
        None(0),
        Deferrable(1),
        Exigent(2);

        private final short _code;

        /**
         * Constructor
         * <p>
         * @param code
         */
        Deferrability(
            final int code
        ) {
            _code = (short)code;
        }

        /**
         * Getter
         * <p>
         * @return
         */
        public short getCode(
        ) {
            return _code;
        }
    };

    public enum InterruptPoint {
        None(0),
        BetweenInstructions(1),
        MidExecution(2),
        IndirectExecute(3);

        public final short _code;

        /**
         * Constructor
         * <p>
         * @param code
         */
        InterruptPoint(
            final int code
        ) {
            _code = (short)code;
        }

        /**
         * Getter
         * <p>
         * @return
         */
        public short getCode(
        ) {
            return _code;
        }
    };

    public enum Synchrony {
        None(0),
        Asynchronous(1),    //  taken at next interrupt point; persists until taken
        Broadcast(2),       //  same as sync, except source is external to this processor
        Pended(3),          //  can be deferred, or held until the next between-instruction interrupt point;
                            //      dropped (more or less) when pre-empted, but condition is persisted in ASP
        Synchronous(4);     //  taken at next interrupt point unless pre-empted by higher-priority interrupt,
                            //      in which case this interrupt is dropped

        public final short _code;

        /**
         * Construtor
         * <p>
         * @param code
         */
        Synchrony(
            final int code
        ) {
            _code = (short)code;
        }

        /**
         * Getter
         * <p>
         * @return
         */
        public short getCode(
        ) {
            return _code;
        }
    };


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Class attributes
    //  ----------------------------------------------------------------------------------------------------------------------------

    private final ConditionCategory _conditionCategory;
    private final Deferrability     _deferrability;
    private final InterruptClass    _interruptClass;
    private final InterruptPoint    _interruptPoint;
    private final Synchrony         _synchrony;


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Constructors
    //  ----------------------------------------------------------------------------------------------------------------------------

    public MachineInterrupt(
        final InterruptClass interruptClass,
        final ConditionCategory conditionCategory,
        final Synchrony synchrony,
        final Deferrability deferrability,
        final InterruptPoint interruptPoint
    ) {
        _interruptClass = interruptClass;
        _conditionCategory = conditionCategory;
        _synchrony = synchrony;
        _deferrability = deferrability;
        _interruptPoint = interruptPoint;
    }


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Accessors
    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Getter
     * <p>
     * @return
     */
    public ConditionCategory getConditionCategory(
    ) {
        return _conditionCategory;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public Deferrability getDeferrability(
    ) {
        return _deferrability;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public InterruptClass getInterruptClass(
    ) {
        return _interruptClass;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public InterruptPoint getInterruptPoint(
    ) {
        return _interruptPoint;
    }

    /**
     * Getter
     * <p>
     * @return
     */
    public Synchrony getSynchrony(
    ) {
        return _synchrony;
    }

    /**
     * Get a displayable description of this interrupt
     * <p>
     * @return
     */
    public String getDescription(
    ) {
        return String.format("%03o:%s", getInterruptClass().getCode(), this.getClass().getName());
    }


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Abstract methods
    //  ----------------------------------------------------------------------------------------------------------------------------


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Instance methods
    //  ----------------------------------------------------------------------------------------------------------------------------

    /**
     * Getter which may be overridden by the subclass
     * <p>
     * @return
     */
    public Word36 getInterruptStatusWord0(
    ) {
        return new Word36();
    }

    /**
     * Getter which may be overridden by the subclass
     * <p>
     * @return
     */
    public Word36 getInterruptStatusWord1(
    ) {
        return new Word36();
    }

    /**
     * Getter which may be overridden by the subclass
     * <p>
     * @return
     */
    public byte getShortStatusField(
    ) {
        return 0;
    }


    //  ----------------------------------------------------------------------------------------------------------------------------
    //  Static methods
    //  ----------------------------------------------------------------------------------------------------------------------------
}
