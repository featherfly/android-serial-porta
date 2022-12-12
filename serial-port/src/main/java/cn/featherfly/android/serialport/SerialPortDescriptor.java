package cn.featherfly.android.serialport;

/**
 * The type Serial port descriptor.
 */
public class SerialPortDescriptor {

    /**
     * The constant DEFAULT_BAUD.
     */
    public static final int DEFAULT_BAUD = 9600;

    private String name;

    private int baudrate = DEFAULT_BAUD;

    private int flags = 0;

    /**
     * Instantiates a new Serial port descriptor.
     *
     * @param name the name
     */
    public SerialPortDescriptor(String name) {
        this(name, DEFAULT_BAUD);
    }

    /**
     * Instantiates a new Serial port descriptor.
     *
     * @param name     the name
     * @param baudrate the baudrate
     */
    public SerialPortDescriptor(String name, int baudrate) {
        this(name, baudrate, 0);
    }

    /**
     * Instantiates a new Serial port descriptor.
     *
     * @param name     the name
     * @param baudrate the baudrate
     * @param flags    the flags
     */
    public SerialPortDescriptor(String name, int baudrate, int flags) {
        this.name = name;
        this.baudrate = baudrate;
        this.flags = flags;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets baudrate.
     *
     * @return the baudrate
     */
    public int getBaudrate() {
        return baudrate;
    }

    /**
     * Gets flags.
     *
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    @Override
    public String toString() {
        return "SerialPortDescriptor{" +
                "name='" + name + '\'' +
                ", baudrate=" + baudrate +
                ", flags=" + flags +
                '}';
    }
}
