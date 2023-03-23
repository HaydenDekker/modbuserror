package com.hdekker.modbuserror;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.time.Instant;

import org.springframework.integration.ip.tcp.serializer.AbstractByteArraySerializer;

public class ModbusClientSerializer extends AbstractByteArraySerializer {

    public byte[] deserialize(InputStream stream) {

        byte[] msg = new byte[DEFAULT_MAX_MESSAGE_SIZE];
        int k, l = 0;
        Instant start = Instant.now();

        // These should be set back to 0 if this is a modbus message
        msg[2] = 0x01;
        msg[3] = 0x01;

        try {
            for (int i = 0; i < 7; i++) {
                k = stream.read();
                if (k != -1) msg[i] = (byte) k;
                if (i == 0) start = Instant.now();
                logger.debug("Stream size is  " + stream.available() + " byte = " + k);
                if (stream.available() == 0 && Duration.between(start, Instant.now()).abs().toMillis() > 10) i = 7;  // abort the loop if nothing after 10 ms
            }

            // Get the size of the modbus message
            l = Util.toInt(msg[4], msg[5]);

            // Check if this is a modbus message
            if (msg[2] != 0x00 || msg[3] != 0x00) {
                //stream.close();
                throw new Exception("Not a Modbus packet. Protocol incorrect.");
            }

            if (l < 2) {
                 // stream.close();
                throw new Exception("Not a Modbus packet. Length " + l);
            }


            // logger.debug("Message being decerialised; length: " + l);

            if (l + 6 < DEFAULT_MAX_MESSAGE_SIZE) {
                for (int j = 7; j < l + 6; j++) {
                    k = stream.read();
                    if (k != -1) msg[j] = (byte) k;
                }
            } else {
                msg = null;
                l = 0;
            }
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

        // Compress the byte array down to the correct size.
        if (l > 0) {
            byte[] rtn = new byte[l + 6];
            for (int i = 0; i < rtn.length; i++) {
                rtn[i] = msg[i];
            }

            // Log the byte array
            StringBuilder msgStr = new StringBuilder();
            for (byte b: rtn) {
                msgStr.append(String.format("%02x ", b));
            }
            // logger.debug("Deserialized reply message: " + msgStr.toString());

            return rtn;
        }

        // reset length of array
        l = 0;

        return new byte[1];

    }

    public void serialize(byte[] msg, OutputStream stream) {

        try {
            stream.write(msg);

             StringBuilder msgStr = new StringBuilder();
             for (byte b: msg) {
                 msgStr.append(String.format("%02x ", b));
             }
             logger.debug("Serialized request message: " + msgStr.toString());

        } catch (Exception e) {
            logger.warn(e.getMessage());
        }

    }

}
