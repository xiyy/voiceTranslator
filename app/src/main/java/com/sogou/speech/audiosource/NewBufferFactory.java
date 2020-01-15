package com.sogou.speech.audiosource;



public class NewBufferFactory {
    public static class ByteBufferFactory implements IBufferFactory {
        @Override
        public Object newBuffer(int length) {
            return new byte[length];
        }
    }

    public static class ShortBufferFactory implements IBufferFactory{
        @Override
        public Object newBuffer(int length) {
            return new short[length];
        }
    }
}
