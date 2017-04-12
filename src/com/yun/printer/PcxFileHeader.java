package com.yun.printer;

import java.io.IOException;

/**
 * Created by hancj on 16/7/28.
 */
public class PcxFileHeader {
    public static byte PC_PAINTBRUSH = 10;
    public static byte V2_5 = 0;
    public static byte V2_8p = 2;
    public static byte V2_8 = 3;
    public static byte V3_0p = 5;
    public static byte RLE_ENCODING = 1;
    public static final int HEADER_SIZE = 128;

    public byte manufacturer = (byte) 0x0a;
    public byte version = 5;
    public byte encoding = 1;
    public byte bitsPerPixel;
    public int xmin = 0;
    public int ymin = 0;
    public int xmax;
    public int ymax;
    public int hres = 0;
    public int vres = 0;
    public int colorPlanes;
    public int bytesPerLine;
    public int paletteType;

    public int width;
    public int height;

     //  调色板
    public byte[] palette = new byte[48];

     //  后置调色板
    public byte[] postPalette = null;

    public PcxFileHeader(BmpFile bmpFile) {
        BmpFileHeader bmpHeader = bmpFile.header;

        this.bitsPerPixel = 1;
        this.xmax = bmpHeader.width - 1;
        this.ymax = bmpHeader.height - 1;
        this.colorPlanes = 1;
        this.bytesPerLine = bmpHeader.pixelLineSize;
        this.paletteType = (bmpFile.isGray() ? 2 : 1);
         //  this.palette[0] = 0;
         //  this.palette[1] = 0;
         //  this.palette[2] = 0;
         //  this.palette[3] = (byte)0xff;
         //  this.palette[4] = (byte)0xff;
         //  this.palette[5] = (byte)0xff;

        this.width = bmpHeader.width;
        this.height = bmpHeader.height;

        if (bmpHeader.bitsPerPixel == 1) {
            this.colorPlanes = 1;
            this.bitsPerPixel = 1;
            this.paletteType = 1;
        } else if (bmpHeader.bitsPerPixel == 4) {
            this.colorPlanes = 4;
            this.bitsPerPixel = 1;
        } else if (bmpHeader.bitsPerPixel == 8) {
            this.colorPlanes = 1;
            this.bitsPerPixel = 8;
        } else {
            this.colorPlanes = 3;
            this.bitsPerPixel = 8;
        }

         //  调色板
        if (bmpFile.palette != null) {
            byte[] pat = this.palette;
            if (bmpFile.palette.length > this.palette.length / 3) {
                this.postPalette = new byte[768];
                assert (bmpFile.palette.length <= 256);
                pat = this.postPalette;
            }

            int j = 0;
            for (int i = 0; i < bmpFile.palette.length; ++i) {
                pat[j++] = bmpFile.palette[i].red;
                pat[j++] = bmpFile.palette[i].green;
                pat[j++] = bmpFile.palette[i].blue;
            }
        }
    }

    public void write(LEDataOutputStream outputStream) throws IOException {
        outputStream.writeByte(this.manufacturer);
        outputStream.writeByte(this.version);
        outputStream.writeByte(this.encoding);
        outputStream.writeByte(this.bitsPerPixel);
        outputStream.writeShort(this.xmin);
        outputStream.writeShort(this.ymin);
        outputStream.writeShort(this.xmax);
        outputStream.writeShort(this.ymax);
        outputStream.writeShort(this.hres);
        outputStream.writeShort(this.vres);
        outputStream.write(this.palette);
        outputStream.writeByte(0);
        outputStream.writeByte(this.colorPlanes);
        outputStream.writeShort(this.bytesPerLine);
        outputStream.writeShort(this.paletteType);
        outputStream.write(new byte[58]);
    }
}
