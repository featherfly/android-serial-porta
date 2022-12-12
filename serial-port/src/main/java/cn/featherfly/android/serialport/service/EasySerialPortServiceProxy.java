package cn.featherfly.android.serialport.service;

import cn.featherfly.android.serialport.EasySerialPort;

import java.io.IOException;

public class EasySerialPortServiceProxy implements EasySerialPort {

    private EasySerialPort easySerialPort;

    private SerialPortService serialPortService;

    public EasySerialPortServiceProxy(SerialPortService serialPortService, EasySerialPort easySerialPort) {
        this.easySerialPort = easySerialPort;
        this.serialPortService = serialPortService;
    }

    @Override
    public EasySerialPort open() throws IOException {
        return easySerialPort.open();
    }

    @Override
    public EasySerialPort close() {
        serialPortService.closeSerialPort(easySerialPort.getName());
        return easySerialPort;
    }

    @Override
    public String getName() {
        return easySerialPort.getName();
    }

    @Override
    public int getBaudrate() {
        return easySerialPort.getBaudrate();
    }

    @Override
    public void write(int b) throws IOException {
        easySerialPort.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        easySerialPort.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        easySerialPort.write(b);
    }

    @Override
    public void flush() throws IOException {
        easySerialPort.flush();
    }

    @Override
    public EasySerialPort addListener(SerialReadListener serialReadListener) {
        return easySerialPort.addListener(serialReadListener);
    }
}
