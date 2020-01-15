package com.sogou.speech.vad;

import java.lang.reflect.Array;



public class RingBuffer {
    private final int mMaxLength;
    private final Object mBuffer;
    private int mR;
    private int mW;

    public RingBuffer(int maxLength, Short none) {
        mMaxLength = maxLength;
        mBuffer = new short[maxLength + 1];
    }

    public RingBuffer(int maxLength, Byte none) {
        mMaxLength = maxLength;
        mBuffer = new byte[maxLength + 1];
    }

    public <T> RingBuffer(int maxLength, T none) {
        mMaxLength = maxLength;
        mBuffer = Array.newInstance(none.getClass(), maxLength + 1);
    }

    public int getLength() {
        return mW >= mR ? mW - mR : mW + mMaxLength + 1 - mR;
    }

    public int getFreeLength() {
        return mMaxLength - getLength();
    }

    private void doWrite(Object input, int pos, int room) {
        if (mW < mR) {
            System.arraycopy(input, pos, mBuffer, mW, room);
            mW += room;
        } else {
            int backRoom = Math.min(room, mMaxLength + 1 - mW);
            System.arraycopy(input, pos, mBuffer, mW, backRoom);
            mW = (mW + backRoom) % (mMaxLength + 1);
            final int frontRoom = room - backRoom;
            if (frontRoom > 0) {
                System.arraycopy(input, pos + backRoom, mBuffer, 0, frontRoom);
                mW = frontRoom;
            }
        }
    }

    public void skipRead(int maxSkipLength) {
        int skip = Math.min(maxSkipLength, getLength());
        mR = (mR + skip) % (mMaxLength + 1);
    }

    /*
    public int writeFront(Object input , int pos, int maxWriteLength){
        final int room = Math.min(maxWriteLength, getFreeLength());
        if (mR - room >= 0){
            System.arraycopy(input, pos, mBuffer, mR - room, room);
            mR -= room;
        }else {
            final int backRoom = room - mR;
            System.arraycopy(input, pos, mBuffer, mMaxLength + 1 - backRoom, backRoom);
            System.arraycopy(input, pos + backRoom, mBuffer, 0, mR);
            mR = mMaxLength + 1 - backRoom;
        }
        return room;
    }*/

    public int write(Object input, int pos, int maxWriteLength) {
        final int room = Math.min(maxWriteLength, getFreeLength());
        doWrite(input, pos, room);
        return room;
    }

    public void overWrite(Object input, int pos, int maxWriteLength) {
        final int freeLength = getFreeLength();
        if (maxWriteLength <= freeLength) {
            doWrite(input, pos, maxWriteLength);
        } else {
            if (maxWriteLength < mMaxLength) {
                skipRead(maxWriteLength - freeLength);
                doWrite(input, pos, maxWriteLength);
            } else {
                System.arraycopy(input, pos + maxWriteLength - mMaxLength, mBuffer, 0, mMaxLength);
                mR = 0;
                mW = mMaxLength;
            }
        }
    }

    public int read(Object output, int pos, int maxReadLength) {
        final int available = Math.min(maxReadLength, getLength());
        if (mR <= mW) {
            System.arraycopy(mBuffer, mR, output, pos, available);
            mR += available;
        } else {
            final int backAvailable = Math.min(available, mMaxLength + 1 - mR);
            System.arraycopy(mBuffer, mR, output, pos, backAvailable);
            mR = (mR + backAvailable) % (mMaxLength + 1);
            final int frontAvailable = available - backAvailable;
            if (frontAvailable > 0) {
                System.arraycopy(mBuffer, 0, output, pos + backAvailable, frontAvailable);
                mR = frontAvailable;
            }
        }
        if (mR == mW) {
            clear();
        }
        return available;
    }

    //read content from buffer , and not change the index
    public int peek(Object output, int pos, int maxReadLength) {
        int oldR = mR;
        int oldW = mW;
        int count = read(output, pos, maxReadLength);
        mR = oldR;
        mW = oldW;
        return count;
    }

    public void clear() {
        mR = mW = 0;
    }
}
