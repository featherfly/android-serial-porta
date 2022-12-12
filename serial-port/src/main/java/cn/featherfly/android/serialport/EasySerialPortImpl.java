package cn.featherfly.android.serialport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Easy serial port.
 */
public class EasySerialPortImpl implements EasySerialPort {

    private ExecutorService executorService;

    private static final String TAG_FORMAT = "SerialPort[%s]";

    private SerialPortDescriptor descriptor;

    private SerialPort serialPort;

    private InputStream in;

    private OutputStream out;

//    private DataInputStream in;
//    private DataOutputStream out;

    private String tag;

    private boolean connected;

    private List<SerialReadListener> serialReadListeners = new ArrayList<>();

    private byte[] buffer;

    private int bufferSize = 512;


    private EasySerialPortImpl(SerialPortDescriptor descriptor) {
        this(descriptor, 512);
    }

    private EasySerialPortImpl(SerialPortDescriptor descriptor, int bufferSize) {
        this.descriptor = descriptor;
        this.bufferSize = bufferSize;

        buffer = new byte[bufferSize];

        tag = String.format(TAG_FORMAT, descriptor.getName());
    }

    /**
     * open easy serial port.
     *
     * @param portName the port name
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public static EasySerialPortImpl open(String portName) throws IOException {
        return open(portName, SerialPortDescriptor.DEFAULT_BAUD);
    }

    /**
     * open easy serial port.
     *
     * @param portName the port name
     * @param portBaud the port baud
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public static EasySerialPortImpl open(String portName, int portBaud) throws IOException {
        return new EasySerialPortImpl(new SerialPortDescriptor(portName, portBaud)).open();
    }

    /**
     * open easy serial port.
     *
     * @param portName   the port name
     * @param portBaud   the port baud
     * @param bufferSize the buffer size
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public static EasySerialPortImpl open(String portName, int portBaud, int bufferSize) throws IOException {
        return new EasySerialPortImpl(new SerialPortDescriptor(portName, portBaud), bufferSize).open();
    }

    /**
     * Open easy serial port.
     *
     * @param descriptor the descriptor
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public static EasySerialPortImpl open(SerialPortDescriptor descriptor) throws IOException {
        return new EasySerialPortImpl(descriptor).open();
    }

    /**
     * Open easy serial port.
     *
     * @param descriptor the descriptor
     * @param bufferSize the buffer size
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public static EasySerialPortImpl open(SerialPortDescriptor descriptor, int bufferSize) throws IOException {
        return new EasySerialPortImpl(descriptor, bufferSize).open();
    }

    /**
     * Is connected boolean.
     *
     * @return the boolean
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * open easy serial port.
     *
     * @return the easy serial port
     * @throws IOException the io exception
     */
    public EasySerialPortImpl open() throws IOException {
        Log.i(tag, "open");
        serialPort = new SerialPort(new File(descriptor.getName()), descriptor.getBaudrate(), descriptor.getFlags());
        in = serialPort.getInputStream();
        out = serialPort.getOutputStream();
//        in = new DataInputStream(serialPort.getInputStream());
//        out = new DataOutputStream(serialPort.getOutputStream());
//        serialPort.notifyOnDataAvailable(true);
        connected = true;
//        for (SerialReadListener serialReadListener : serialReadListeners) {
//            serialReadListener.onSerialOpenSuccess(new SerialPortEvent(name));
//        }
        return this;
    }

//    public EasySerialPort addSerialReader(SerialReader serialReader) {
//        serialReader.setInStream(in);
//        serialReader.setPort(name);
////            serialPort.addEventListener(serialReader);
//        return this;
//    }

    /**
     * Start receive easy serial port.
     *
     * @return the easy serial port
     */
    public synchronized EasySerialPortImpl startReceive() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
//            receiveThread = new ReceiveThread();
            executorService.execute(new ReceiveThread());
        }
        return this;
    }

    /**
     * Stop receive easy serial port.
     *
     * @return the easy serial port
     */
    public synchronized EasySerialPortImpl stopReceive() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();
            executorService = null;
        }
        return this;
    }

    /**
     * Disconnect easy serial port.
     *
     * @return the easy serial port
     */
    public EasySerialPortImpl close() {
        Log.i(tag, "close");
        closeInput();
        closeOutput();
        serialPort.close();
        return this;
    }

    private void closeInput() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
        }
    }

    private void closeOutput() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (IOException e) {
            Log.e(tag, e.getMessage());
        }
    }

    /**
     * Add listener easy serial port.
     *
     * @param serialReadListener the serial read listener
     * @return the easy serial port
     */
    public EasySerialPortImpl addListener(SerialReadListener serialReadListener) {
        serialReadListeners.add(serialReadListener);
        return this;
    }

    /**
     * Write.
     *
     * @param b the b
     * @throws IOException the io exception
     */
    public void write(int b) throws IOException {
        Log.i(tag, "write -> " + Integer.toHexString(b));
        out.write(b);
    }

    /**
     * Write.
     *
     * @param b   the b
     * @param off the off
     * @param len the len
     * @throws IOException the io exception
     */
    public void write(byte[] b, int off, int len) throws IOException {
        Log.i(tag, "write -> " + toHexString(Arrays.copyOfRange(b, off, len), len - off));
        out.write(b, off, len);
    }

    /**
     * Write.
     *
     * @param b the b
     * @throws IOException the io exception
     */
    public void write(byte[] b) throws IOException {
        Log.i(tag, "write -> " + toHexString(b, b.length));
        out.write(b);
    }

    /**
     * Flush.
     *
     * @throws IOException the io exception
     */
    public void flush() throws IOException {
        out.flush();
    }

    /**
     * Gets buffer size.
     *
     * @return the buffer size
     */
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public String getName() {
        return descriptor.getName();
    }

    @Override
    public int getBaudrate() {
        return descriptor.getBaudrate();
    }

    /**
     * 接收串口数据的线程
     */
    private class ReceiveThread implements Runnable {
        @Override
        public void run() {
            while (connected) {
                if (in != null) {
                    try {
//                        while (in.available() > 0) {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//
//                        }
                        int size = in.read(buffer);
                        if (size > 0) {
                            Log.i(tag, "read -> " + toHexString(buffer, size));
                            int[] receive = new int[size];
                            for (int i = 0; i < size; i++) {
                                receive[i] = toUnsignedInt(buffer[i]);
                            }
                            for (SerialReadListener serialReadListener : serialReadListeners) {
                                serialReadListener.onReadComplete(new SerialPortReadEvent(descriptor.getName(), receive));
                            }
                        }
//                        }
                    } catch (IOException e) {
                        Log.e(tag, e.getMessage());
                    }
                }
            }
        }
    }

    private int toUnsignedInt(byte x) {
        return ((int) x) & 0xff;
    }

    private String toHexString(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte b = bytes[i];
            sb.append(Integer.toHexString(b));
            sb.append(" ");
        }
        return sb.toString();
    }
}
