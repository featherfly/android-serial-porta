package cn.featherfly.android.serialport;

import java.io.IOException;

/**
 * The interface EasySerialPort.
 */
public interface EasySerialPort {

    /**
     * Open port.
     *
     * @return this port
     * @throws SecurityException the security exception
     * @throws IOException       the io exception
     */
    EasySerialPort open() throws SecurityException, IOException;

    /**
     * Close port.
     *
     * @return this port
     */
    EasySerialPort close();

    /**
     * get name.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets baud.
     *
     * @return the baud
     */
    int getBaudrate();


    /**
     * Write byte.
     *
     * @param b the b
     * @throws IOException the io exception
     */
    public void write(int b) throws IOException;

    /**
     * Write byte array.
     *
     * @param b   the b
     * @param off the off
     * @param len the len
     * @throws IOException the io exception
     */
    public void write(byte[] b, int off, int len) throws IOException;

    /**
     * Write byte array.
     *
     * @param b the b
     * @throws IOException the io exception
     */
    public void write(byte[] b) throws IOException;

    /**
     * Flush.
     *
     * @throws IOException the io exception
     */
    public void flush() throws IOException;

    /**
     * Add listener easy serial port.
     *
     * @param serialReadListener the serial read listener
     * @return the easy serial port
     */
    public EasySerialPort addListener(SerialReadListener serialReadListener);


    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------
    // ------------------------------------------------------------------------------------------------------

    /**
     * The interface Serial read listener.
     */
    public interface SerialReadListener {
        /**
         * 读取完成
         *
         * @param event the event
         */
        public void onReadComplete(SerialPortReadEvent event);

//        /**
//         * 串口打开成功
//         *
//         * @param event the event
//         */
//        default void onSerialOpenSuccess(SerialPortEvent event) {
//
//        }
//
//        /**
//         * 串口打开异常
//         *
//         * @param event the event
//         * @param e     the e
//         */
//        default void onSerialOpenException(SerialPortEvent event, Exception e) {
//
//        }
    }

    /**
     * The type Serial port event.
     */
    public class SerialPortEvent {

        private String port;

        /**
         * Instantiates a new Serial port event.
         *
         * @param port the port
         */
        public SerialPortEvent(String port) {
            this.port = port;
        }

        /**
         * Gets port.
         *
         * @return the port
         */
        public String getPort() {
            return port;
        }
    }

    /**
     * The type Serial port read event.
     */
    public class SerialPortReadEvent extends SerialPortEvent {

        private int[] buffer;

        /**
         * Instantiates a new Serial port read event.
         *
         * @param port   the port
         * @param buffer the buffer
         */
        public SerialPortReadEvent(String port, int[] buffer) {
            super(port);
            this.buffer = buffer;
        }

        /**
         * Get buffer byte [ ].
         *
         * @return the byte [ ]
         */
        public int[] getBuffer() {
            return buffer;
        }
    }
}
