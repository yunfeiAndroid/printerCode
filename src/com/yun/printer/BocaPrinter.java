package com.yun.printer;

import java.nio.ByteBuffer;
import java.util.Map;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.util.Log;


public class BocaPrinter {
    public static final int PRINTER_VENDOR_ID = 2627;
    public static final int PRINTER_PRODUCT_ID = 513;

     //  private static final String ENCODE = "ASCII";
    private static final String ACTION_USB_PERMISSION = "ACTION_USB_PERMISSION";
    String mTag = "bocaPrint";

    private UsbManager mUsbManager;
    public UsbDevice mDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbInterface mUsbInterface;
    private UsbEndpoint mEpOut;
    private UsbEndpoint mEpIn;
    public static int ret = 0;

    Context mContext;
    byte[] sendByte = null;

    public BocaPrinter(Context mContext) {
        this.mContext = mContext;
        getDevice();
    }

    private UsbDevice getDevice() {
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        if (mUsbManager == null) {
            return null;
        }

         //  通过USB管理器找到Boca打印机设备
        Map<String, UsbDevice> deviceMap = mUsbManager.getDeviceList();
        for (String key : deviceMap.keySet()) {
            UsbDevice device = deviceMap.get(key); //  device.getVendorId()

            if (device.getVendorId() == PRINTER_VENDOR_ID && device.getProductId() == PRINTER_PRODUCT_ID) {
                mUsbManager.requestPermission(device,
                        PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0));

                mDevice = device;
                return mDevice;
            }
        }
        return null;
    }

    /**
     * 找设备接口
     */
    private void findInterface() {
        if (mDevice != null) {
            Log.d(mTag, "interfaceCounts : " + mDevice.getInterfaceCount());
            for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
                UsbInterface intf = mDevice.getInterface(i);
                 //  根据手上的设备做一些判断，其实这些信息都可以在枚举到设备时打印出来
                if (i == 0) {
                    mUsbInterface = intf;
                    Log.d(mTag, "找到我的设备接口");
                }
                break;
            }
        }
    }

    /**
     * 打开设备
     */
    private void openDevice() {
        if (mUsbInterface != null) {
            UsbDeviceConnection conn = null;
             //  在open前判断是否有连接权限；对于连接权限可以静态分配，也可以动态分配权限，可以查阅相关资料
            if (mUsbManager.hasPermission(mDevice)) {
                conn = mUsbManager.openDevice(mDevice);
            }

            if (conn == null) {
                return;
            }

            if (conn.claimInterface(mUsbInterface, true)) {
                mUsbDeviceConnection = conn;  //  到此你的android设备已经连上HID设备
                Log.d(mTag, "打开设备成功");
            } else {
                conn.close();
            }
        }
    }

    private void assignEndpoint() {
        if (mUsbInterface != null) {  //  这一句不加的话 很容易报错

             //  这里的代码替换了一下 按自己硬件属性判断吧
            for (int j = 0; j < mUsbInterface.getEndpointCount(); j++) {
                UsbEndpoint ep = mUsbInterface.getEndpoint(j);
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    mEpOut = ep;
                } else {
                    mEpIn = ep;
                }
            }
        }

    }

    private void testTransfer(byte[] fileByte) {
        Log.d(mTag, "准备传输。。。");

        if (fileByte != null) {
            Log.d(mTag, "fileByte.length : " + fileByte.length);
            byte[] pcxHeader = ("<SP 100,42><pcx><G" + fileByte.length + ">").getBytes();
            byte[] pcxFooter = ("<PS2><q>").getBytes();
            sendByte = ByteUtil.catByte(ByteUtil.catByte(pcxHeader, fileByte), pcxFooter);
        }

        if (sendByte == null) {
            Log.d(mTag, "testByte is null!");
            return;
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                 //  TODO Auto-generated method stub
                if (mEpOut != null && mEpIn != null) {
                    int maxLength = 16384;
                    int remainBytes = sendByte.length;
                    while (remainBytes > 0) {
                        byte[] sendBuffer = null;
                        if (remainBytes > maxLength) {
                            sendBuffer = new byte[maxLength];
                        } else {
                            sendBuffer = new byte[remainBytes];
                        }
                        System.arraycopy(sendByte, sendByte.length - remainBytes, sendBuffer, 0, sendBuffer.length);
                        ret = mUsbDeviceConnection.bulkTransfer(mEpOut, sendBuffer, sendBuffer.length, 1000);
                         //  LogUtil.e(mTag, "send : " + ret);
                        if (ret > 0) {
                            remainBytes -= ret;
                        } else {
                            break;
                        }
                    }
                     //  有票纸打印
                    if (ret > 0) {
                    	byte[] receiveByte = new byte[33];
                        ByteBuffer buffer = ByteBuffer.wrap(receiveByte);
                        UsbRequest request = new UsbRequest();
                        boolean isOpenRequest = request.initialize(mUsbDeviceConnection, mEpIn);

                        if (isOpenRequest) {
                            boolean isQueueOK = request.queue(buffer, receiveByte.length);

                            if (isQueueOK) {
                                Log.e(mTag, "isQueueOK " + isQueueOK);

                                if (isQueueOK && mUsbDeviceConnection.requestWait() == request) {

                                	Log.d(mTag, "打印完成！");

                                }
                            }
                        } else {
                            Log.d(mTag, "打印失败！");
                        }
                    }
                }
            }
        }).start();

    }


    public void write(final byte[] bs) {
        if (getDevice() != null) {
            findInterface();
            openDevice();
            assignEndpoint();

            testTransfer(bs);
        }
    }

}