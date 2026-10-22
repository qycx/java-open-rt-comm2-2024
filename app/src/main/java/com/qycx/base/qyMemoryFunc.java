
package com.qycx.base;

import java.nio.ByteBuffer;

public class qyMemoryFunc {
    public static class QY_MEMORY {
        public int iAllocType;
        public int uiBufSize;             //  2011/12/04
        //  int			iIndex_tmpMemory;		//  2010/09/18
        public byte[] m_pVirtualData;
        public byte[] m_pBuf;
        // fixme to change...
        public ByteBuffer buffer_m_pBuf;
        public Object m_obj;

        public byte[] toByteArray1() {
            int bufferSize = Integer.BYTES * 2;
            if(m_pVirtualData !=null){
                bufferSize += m_pVirtualData.length;
            }

            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            buffer.putInt(iAllocType);
            buffer.putInt(uiBufSize);
            if(m_pVirtualData!=null){
                buffer.put(m_pVirtualData);
            }

            return buffer.array();
        }

    }


    public static class qyFuncs {

        public static byte[] mymemcpy(byte[] destin, byte[] source, int n) {
            if (destin == null || source == null || n == 0) {
                return new byte[0];
            }

            int remaining = Math.min(Math.min(destin.length, source.length), n);

            int block = remaining >> 3;

            for (int i = 0; i < block; i++) {
                long srcValue = ByteBuffer.wrap(source, i * 8, 8).getLong();
                ByteBuffer.wrap(destin, i * 8, 8).putLong(srcValue);
            }

            remaining = remaining - (block << 3);

            if (remaining > 0) {
                for (int i = 0; i < remaining; i++) {
                    destin[block * 8 + i] = source[block * 8 + i];
                }
            }

            return destin;
        }

    }
}
