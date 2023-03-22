package com.hdekker.modbuserror;

public class Util {

    public static String byteArray2String(byte[] ba) {
        StringBuilder msgStr = new StringBuilder();
        for (byte b: ba) {
            msgStr.append(String.format("%02x ", b));
        }
        return msgStr.toString();
    }

    public static int toInt(byte msb, byte lsb) {
        return ((msb << 8) + (0xff & lsb)) & 0x0000ffff;
    }

    public static long toLong(byte[] msbfirst) {
        long l = 0L;
        for(int i = 0; i < msbfirst.length; i++) {
        	l = l << 8;
        	l |= (msbfirst[i] & 0xFF);
        }
        return l;
    }
 

}
