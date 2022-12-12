package cn.featherfly.android.serialport.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import cn.featherfly.android.serialport.EasySerialPort;
import cn.featherfly.android.serialport.EasySerialPortImpl;
import cn.featherfly.android.serialport.SerialPortDescriptor;
import cn.featherfly.android.service.LocalBinderService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The type Serial port service.
 */
public class SerialPortService extends LocalBinderService {

    private static final String TAG = "Service[SerialPortService]";

    private ScheduledExecutorService executorService;

    private static Map<String, EasySerialPort> serialPortMap = new HashMap<>(0);

    /**
     * The Service connection.
     */
    ServiceConnection serviceConnection;

    /**
     * bind serial port service.
     *
     * @param context      the context
     * @param connected    the connected
     * @param disconnected the disconnected
     */
    public static void bind(Context context, BiConsumer<SerialPortService, ComponentName> connected, Consumer<ComponentName> disconnected) {
        Intent intent = new Intent(context, SerialPortService.class);
        context.bindService(intent, new ServiceConnection() {
            /**
             * 与服务器端交互的接口方法 绑定服务的时候被回调，在这个方法获取绑定Service传递过来的IBinder对象， * 通过这个IBinder对象，实现宿主和Service的交互。
             */
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected " + name.getClassName());
                // 获取Binder
                SerialPortService.LocalBinder binder = (SerialPortService.LocalBinder) service;
                // 获取服务对象
                SerialPortService serialPortService = binder.getService();
                serialPortService.serviceConnection = this;

                connected.accept(serialPortService, name);
            }

            /**
             * 当取消绑定的时候被回调。但正常情况下是不被调用的，它的调用时机是当Service服务被意外销毁时， * 例如内存的资源不足时这个方法才被自动调用。
             */
            @Override
            public void onServiceDisconnected(ComponentName name) {
                disconnected.accept(name);
            }
        }, Service.BIND_AUTO_CREATE);
    }

    /**
     * bind serial port service.
     *
     * @param context      the context
     * @param connected    the connected
     * @param disconnected the disconnected
     * @param descriptors  the descriptors
     */
    public static void bind(Context context, BiConsumer<SerialPortService, ComponentName> connected, Consumer<ComponentName> disconnected
            , SerialPortDescriptor... descriptors) {
        bind(context, (s, n) -> {
            for (SerialPortDescriptor descriptor : descriptors) {
                s.openSerialPort(descriptor);
            }
            connected.accept(s, n);
        }, disconnected);
    }

    /**
     * Stop.
     *
     * @param context the context
     */
    public static void stop(Context context) {
        context.stopService(new Intent(context, SerialPortService.class));
    }

    /**
     * unbind.
     *
     * @param context the context
     */
    public void unbind(Context context) {
        context.unbindService(serviceConnection);
    }

    /**
     * Open serial port.
     *
     * @param descriptor the descriptor
     */
    public void openSerialPort(SerialPortDescriptor descriptor) {
        try {
            if (!serialPortMap.containsKey(descriptor.getName())) {
                EasySerialPortImpl serialPort = EasySerialPortImpl.open(descriptor);
                serialPort.startReceive();
                serialPortMap.put(descriptor.getName(), serialPort);
            } else {
                Log.i("SerialPortService", String.format("port[%s] already opened", descriptor.getName()));
            }
        } catch (Exception e) {
//            Log.e(this.getClass().getSimpleName(), e.getMessage());
//            throw new RuntimeException(e);
            executorService.schedule(() -> {
                openSerialPort(descriptor);
            }, 3000, TimeUnit.SECONDS);
        }
    }

    /**
     * Close serial port.
     *
     * @param portName the port name
     */
    public void closeSerialPort(String portName) {
        EasySerialPort easySerialPort = serialPortMap.get(portName);
        if (easySerialPort != null) {
            easySerialPort.close();
            serialPortMap.remove(portName);
        }
    }

    /**
     * Gets serial port.
     *
     * @param portName the port name
     * @return the serial port
     */
    public EasySerialPort getSerialPort(String portName) {
        EasySerialPort easySerialPort = serialPortMap.get(portName);
        if (easySerialPort != null) {
            return new EasySerialPortServiceProxy(this, easySerialPort);
        } else {
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
