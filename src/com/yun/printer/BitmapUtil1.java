package com.yun.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapUtil1 {

    public static final BitmapUtil1 INSTANCE = new BitmapUtil1();

    public static final BitmapUtil1 getInstance() {
        return INSTANCE;
    }

    private BitmapUtil1() {

    }

    /**
     * 将bitmap存为.bmp 1bit格式图片
     * 
     * @param bitmap
     */
    public byte[] getbitBmp(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
         //  位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
         //  图像数据大小
        int widthBytes = (int) Math.ceil(nBmpWidth / 8d);
        int realWidth = (int) Math.ceil(widthBytes / 4d) * 4;
        int bufferSize = nBmpHeight * realWidth;
        try {
             //  bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + 8 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40 + 8;
             //  保存bmp文件头
            byte[] totalBytes = writeWord(null, bfType);
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, bfSize));
            totalBytes = ByteUtil.catByte(totalBytes, writeWord(null, bfReserved1));
            totalBytes = ByteUtil.catByte(totalBytes, writeWord(null, bfReserved2));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, bfOffBits));
             //  bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
             //  int biBitCount = 24;
            int biBitCount = 1;
            long biCompression = 0L;
            long biSizeImage = bufferSize;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
             //  保存bmp信息头
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, biSize));
            totalBytes = ByteUtil.catByte(totalBytes, writeLong(null, biWidth));
            totalBytes = ByteUtil.catByte(totalBytes, writeLong(null, biHeight));
            totalBytes = ByteUtil.catByte(totalBytes, writeWord(null, biPlanes));
            totalBytes = ByteUtil.catByte(totalBytes, writeWord(null, biBitCount));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, biCompression));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, biSizeImage));
            totalBytes = ByteUtil.catByte(totalBytes, writeLong(null, biXpelsPerMeter));
            totalBytes = ByteUtil.catByte(totalBytes, writeLong(null, biYPelsPerMeter));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, biClrUsed));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, biClrImportant));
             //  调色板
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, 0x00000000));
            totalBytes = ByteUtil.catByte(totalBytes, writeDword(null, 0x00ffffff));
             //  像素数据
            byte[] bmpData = new byte[bufferSize];
             //  int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
             //  for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight;
             //  ++nCol, --nRealCol)
             //  for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++,
             //  wByteIdex += 3) {
             //  int clr = bitmap.getPixel(wRow, nCol);
             //  bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
             //  bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte)
             //  Color.green(clr);
             //  bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte)
             //  Color.red(clr);
             //  }

            for (int row = 0; row < nBmpHeight; row++) {
                for (int col = 0; col < realWidth; col++) {
                    int[] byteColor = new int[8];
                    for (int i = 0; i < 8; i++) {
                        int origColor = 0;
                        int x = col * 8 + i;
                        if (x < bitmap.getWidth()) {
                            try {
                                int y = nBmpHeight - row - 1;
                                int bitmapColor = bitmap.getPixel(x, y);
                                origColor = (bitmapColor & 0xff) | ((bitmapColor & 0xff00) >> 8)
                                        | ((bitmapColor & 0xff0000) >> 16);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (origColor < 0x80) {
                            byteColor[i] = 0;
                        } else {
                            byteColor[i] = 1;
                        }
                    }
                    bmpData[row * realWidth + col] = arrayToByte(byteColor);
                }
            }
            totalBytes = ByteUtil.catByte(totalBytes, bmpData);
            return totalBytes;
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 将bitmap存为.bmp 1bit格式图片
     * 
     * @param bitmap
     */
    public void save1bitBmp(Bitmap bitmap, String filename) {
        if (bitmap == null || filename == null) {
            return;
        }
        try {
             //  存储文件名
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(filename);
            fileos.write(getbitBmp(bitmap));
            fileos.flush();
            fileos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 将Bitmap存为 .bmp格式图片
     * 
     * @param bitmap
     */
    public byte[] saveBmp(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
         //  位图大小
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
         //  图像数据大小
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
             //  存储文件名
            String filename = "/sdcard/test.bmp";
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileos = new FileOutputStream(filename);
             //  bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
             //  保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
             //  bmp信息头
            long biSize = 40L;
            long biWidth = nBmpWidth;
            long biHeight = nBmpHeight;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
             //  保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, biWidth);
            writeLong(fileos, biHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
             //  像素扫描
            byte[] bmpData = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            }

            fileos.write(bmpData);
            fileos.flush();
            fileos.close();

            return bmpData;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        if (stream != null) {
            stream.write(b);
        }
        return b;
    }

    protected byte[] writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        if (stream != null) {
            stream.write(b);
        }
        return b;
    }

    protected byte[] writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        if (stream != null) {
            stream.write(b);
        }
        return b;
    }

    private byte arrayToByte(int[] array) {
        byte b = 0;
        if (array == null || array.length != 8) {
            return b;
        }
        for (int i = 0; i < 8; i++) {
            b |= (array[i] & 0x1) << (7 - i);
        }
        return b;
    }

}
