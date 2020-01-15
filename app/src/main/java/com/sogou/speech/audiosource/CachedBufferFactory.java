package com.sogou.speech.audiosource;



public class CachedBufferFactory {
    public static abstract class BufferFactoryBase implements IBufferFactory {
        private Object mCachedBuffer;
        private int mCurrentLength;

        public BufferFactoryBase(int initialSize){
            mCurrentLength = initialSize;
            mCachedBuffer = createBufferArray(initialSize);
        }

        @Override
        public Object newBuffer(int length) {
            if (mCurrentLength >= length){
                return mCachedBuffer;
            }
            mCachedBuffer = createBufferArray(length);
            mCurrentLength = length;
            return mCachedBuffer;
        }

        protected abstract Object createBufferArray(int length);
    }

    public static class ByteBufferFactory extends BufferFactoryBase {

        public ByteBufferFactory(int initialSize) {
            super(initialSize);
        }

        @Override
        protected Object createBufferArray(int length) {
            return new byte[length];
        }
    }

    public static class ShortBufferFactory extends BufferFactoryBase {

        public ShortBufferFactory(int initialSize) {
            super(initialSize);
        }

        @Override
        protected Object createBufferArray(int length) {
            return new short[length];
        }
    }
}
