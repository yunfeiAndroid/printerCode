package com.yun.printer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hancj on 16/7/28.
 */
public class BmpFile {
     //  Bmp文件头
    public final BmpFileHeader header;
    public final RGBQuad[] palette;
    public final byte[] data;

    /**
     * 基于内存
     * 
     * @param b
     * @throws IOException
     * @throws BmpException
     */
    public BmpFile(byte[] b) throws IOException, BmpException {
        LEDataInputStream dataInputStream = new LEDataInputStream(new ByteArrayInputStream(b));
        this.header = new BmpFileHeader(dataInputStream);
        this.palette = readPalette(dataInputStream);
        this.data = b;
    }

    /**
     * 是否灰度图
     * 
     * @return
     */
    public boolean isGray() {
        if (this.palette == null) {
            return false;
        }

        for (RGBQuad quad : this.palette) {
            if (!quad.isGray()) {
                return false;
            }
        }

        return true;
    }

    /**
     * 读取文件内容
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFile(InputStream is) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = is.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }

    /**
     * 读调色板
     * 
     * @param dataInput
     * @return
     * @throws IOException
     */
    private RGBQuad[] readPalette(DataInput dataInput) throws IOException {
        RGBQuad[] rgbQuad = null;
        if (this.header.colorsUsed == 0) {
            if (this.header.bitsPerPixel <= 8) {
                int size = (1 << this.header.bitsPerPixel);
                rgbQuad = new RGBQuad[size];
            }
        } else {
            rgbQuad = new RGBQuad[this.header.colorsUsed];
        }

        if (rgbQuad != null) {
            for (int i = 0; i < rgbQuad.length; ++i) {
                rgbQuad[i] = new RGBQuad();
                rgbQuad[i].read(dataInput);
            }
        }

        return rgbQuad;
    }

    /**
     * @return
     */
    public int pixelLineSize() {
        return header.pixelLineSize;
    }

    /**
     * 指定行在data中的位置
     * 
     * @param lineNo
     *            起始行为0的行索引
     * @return
     */
    public int linePos(int lineNo) {
        return header.bitmapOffset + (header.height - (lineNo + 1)) * header.scanLineSize;
    }
}
