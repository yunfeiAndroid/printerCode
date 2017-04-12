package com.yun.printer;

import java.io.DataInput;
import java.io.IOException;

/**
 * Created by hancj on 16/7/28.
 */
public class BmpFileHeader {
    static final short FILETYPE_BM = 0x4d42; //  19778;
    static final short VERSION_2X = 12;
    static final short VERSION_3X = 40;
    static final short VERSION_4X = 108;
     //  LEDataInputStream in_;
    short fileType = FILETYPE_BM; //  19778;
    int fileSize;
    short reserved1 = 0;
    short reserved2 = 0;
    int bitmapOffset;  //  文件正文,相对于流开始的偏移

    int size;
    int width;
    int height;
    int planes;
    int bitsPerPixel;
    int compression;
    int sizeOfBitmap;
    int horzResolution;
    int vertResolution;
    int colorsUsed;
    int colorsImportant;

    int redMask;
    int greenMask;
    int blueMask;
    int alphaMask;
    int csType;
    int redX;
    int redY;
    int redZ;
    int greenX;
    int greenY;
    int greenZ;
    int blueX;
    int blueY;
    int blueZ;
    int gammaRed;
    int gammaGreen;
    int gammaBlue;

    boolean topDown;
    int actualSizeOfBitmap;
    int scanLineSize;  //  Bmp单行字节数(根据bmp要求对齐到4字节边界)
    int pixelLineSize;  //  像素点单行占用的字节数
    int actualColorsUsed;
    int noOfPixels;
    int bmpVersion;

    public BmpFileHeader(LEDataInputStream in) throws IOException, BmpException {
        this.fileType = in.readShort();
        if (this.fileType != FILETYPE_BM) {
            throw new BmpException("Not a BMP file");
        }

        this.fileSize = in.readInt();
        this.reserved1 = in.readShort();
        this.reserved2 = in.readShort();
        this.bitmapOffset = in.readInt();

        this.size = in.readInt();
        if (this.size == VERSION_2X) {
            this.readVersion2x(in);
        } else if (this.size == VERSION_3X) {
            this.readVersion3x(in);
        } else {
            if (this.size != VERSION_4X) {
                throw new BmpException("Unsupported BMP version " + this.size);
            }

            this.readVersion4x(in);
        }

        if (this.topDown) {
            throw new BmpException("Unsupported topdown BMPs");
        } else {
            this.noOfPixels = this.width * this.height;
            this.pixelLineSize = ((this.width * this.bitsPerPixel) + 7) / 8;
            this.scanLineSize = (this.width * this.bitsPerPixel + 31) / 32 * 4;
            if (this.sizeOfBitmap != 0) {
                this.actualSizeOfBitmap = this.sizeOfBitmap;
            } else {
                this.actualSizeOfBitmap = this.scanLineSize * this.height;
            }

            if (this.colorsUsed != 0) {
                this.actualColorsUsed = this.colorsUsed;
            } else if (this.bitsPerPixel < 16) {
                this.actualColorsUsed = 1 << this.bitsPerPixel;
            } else {
                this.actualColorsUsed = 0;
            }
        }
    }

    void readVersion2x(DataInput in) throws IOException {
        this.width = in.readShort();
        this.height = in.readShort();
        this.planes = in.readUnsignedShort();
        this.bitsPerPixel = in.readUnsignedShort();
        this.compression = 0;
        this.bmpVersion = 2;
        this.topDown = this.height < 0;
    }

    void readVersion3x(DataInput in) throws IOException {
        this.width = in.readInt();
        this.height = in.readInt();
        this.planes = in.readUnsignedShort();
        this.bitsPerPixel = in.readUnsignedShort();
        this.compression = in.readInt();
        this.sizeOfBitmap = in.readInt();
        this.horzResolution = in.readInt();
        this.vertResolution = in.readInt();
        this.colorsUsed = in.readInt();
        this.colorsImportant = in.readInt();
        if (this.compression == 3) {
            this.redMask = in.readInt();
            this.greenMask = in.readInt();
            this.blueMask = in.readInt();
        } else if (this.bitsPerPixel == 16) {
            this.redMask = 31744;
            this.greenMask = 992;
            this.blueMask = 31;
            this.alphaMask = 0;
        }

        this.bmpVersion = 3;
        this.topDown = this.height < 0;
    }

    void readVersion4x(DataInput in) throws IOException {
        this.width = in.readInt();
        this.height = in.readInt();
        this.planes = in.readUnsignedShort();
        this.bitsPerPixel = in.readUnsignedShort();
        this.compression = in.readInt();
        this.sizeOfBitmap = in.readInt();
        this.horzResolution = in.readInt();
        this.vertResolution = in.readInt();
        this.colorsUsed = in.readInt();
        this.colorsImportant = in.readInt();
        this.redMask = in.readInt();
        this.greenMask = in.readInt();
        this.blueMask = in.readInt();
        this.alphaMask = in.readInt();
        this.csType = in.readInt();
        this.redX = in.readInt();
        this.redY = in.readInt();
        this.redZ = in.readInt();
        this.greenX = in.readInt();
        this.greenY = in.readInt();
        this.greenZ = in.readInt();
        this.blueX = in.readInt();
        this.blueY = in.readInt();
        this.blueZ = in.readInt();
        this.gammaRed = in.readInt();
        this.gammaGreen = in.readInt();
        this.gammaBlue = in.readInt();
        this.bmpVersion = 4;
        this.topDown = this.height < 0;
    }

    public String toString() {
        return "BMP Header size=" + this.size + " width=" + this.width + " height=" + this.height + " bitmapOffset="
                + this.bitmapOffset + " planes=" + this.planes + " bitsPerPixel=" + this.bitsPerPixel + " compression="
                + this.compression + " sizeOfBitmap=" + this.sizeOfBitmap + " actualSizeOfBitmap="
                + this.actualSizeOfBitmap + " pixelLineSize=" + this.pixelLineSize + " scanLineSize="
                + this.scanLineSize + " horzResolution=" + this.horzResolution + " vertResolution="
                + this.vertResolution + " colorsUsed=" + this.colorsUsed + " colorsImportant=" + this.colorsImportant;
    }
}
