/*
 * Copyright (c) 2018 by Kurt Duncan - All Rights Reserved
 */

package com.kadware.em2200.baselib.types;

/**
 * Wraps an integer in a thin wrapper to help enhance type checking of things which are really just ints or longs
 */
public class PrepFactor extends Size {

    /**
     * Default constructor
     */
    public PrepFactor(
    ) {
    }

    /**
     * Constructor
     * <p>
     * @param value
     */
    public PrepFactor(
        final int value
    ) {
        super(value);
    }

    /**
     * Determines how many 36-bit blocks, of the size indicated by the value of this object,
     * will be required to make up one track (which is comprised of 1792 36-bit words).
     * <p>
     * @return
     */
    public int getBlocksPerTrack(
    ) {
        return 1792 / getValue();
    }

    /**
     * Indicates the required size of a byte buffer to contain a 36-bit block sized according to the
     * value of this object, presuming said 36-bit words are packed 2 words -> 9 bytes.
     * This presumes the byte buffer size is required to be a power of two; the resulting value will contain some unused bytes.
     * <p>
     * @return
     */
    public int getContainingByteBlockSize(
    ) {
        switch (getValue()) {
            case 28:    return 128;
            case 56:    return 256;
            case 112:   return 512;
            case 224:   return 1024;
            case 448:   return 2048;
            case 896:   return 4096;
            case 1792:  return 8192;
        }

        return 0;
    }

    /**
     * In consideration of the commentary for getContaingByteBlockSize(), this method indicates how many unused
     * bytes there are at the end of the containing byte buffer.
     * <p>
     * @return
     */
    public int getContainerByteBlockSlop(
    ) {
        switch (getValue()) {
            case 28:    return 2;
            case 56:    return 4;
            case 112:   return 8;
            case 224:   return 16;
            case 448:   return 32;
            case 896:   return 64;
            case 1792:  return 128;
        }

        return 0;
    }

    /**
     * Converts a disk pack byte block size to a prep factor
     * <p>
     * @param blockSize
     * <p>
     * @return
     */
    public static PrepFactor getPrepFactorFromBlockSize(
        final BlockSize blockSize
    ) {
        switch (blockSize.getValue()) {
            case 128:       return new PrepFactor(28);
            case 256:       return new PrepFactor(56);
            case 512:       return new PrepFactor(112);
            case 1024:      return new PrepFactor(224);
            case 2048:      return new PrepFactor(448);
            case 4096:      return new PrepFactor(896);
            case 8192:      return new PrepFactor(1792);
        }

        return new PrepFactor(0);
    }

    /**
     * Indicates whether the value of this object is a valid prep factor
     * <p>
     * @return
     */
    public boolean isValid(
    ) {
        switch (getValue()) {
            case 28:
            case 56:
            case 112:
            case 224:
            case 448:
            case 896:   //  non-standard, but allowed
            case 1792:
                return true;
        }

        return false;
    }
}
