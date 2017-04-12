package com.yun.printer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by hancj on 16/7/28.
 */
public class PcxFile {
    /**
     * 生成的Pcx图像,以byte数组形式返回
     * 
     * @param bmpFile
     * @return
     * @throws IOException
     */
    public static byte[] convert(BmpFile bmpFile) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(32 * 1024);  //  32K
        convert(bmpFile, outputStream);

        return outputStream.toByteArray();
    }

    /**
     * @param bmpFile
     * @param stream
     * @throws IOException
     */
    public static void convert(BmpFile bmpFile, OutputStream stream) throws IOException {
        PcxFileHeader header = new PcxFileHeader(bmpFile);
        LEDataOutputStream outputStream = new LEDataOutputStream(stream);

         //  生成pcx头
        header.write(outputStream);
         //  正文
        int j = header.colorPlanes * header.height;
        for (int i = 0; i < j; ++i) {
            int bmpPos = bmpFile.linePos(i);
            writeLine(outputStream, bmpFile.data, bmpPos, bmpFile.pixelLineSize());
        }
         //  后置调色板
        if (header.postPalette != null) {
            outputStream.writeByte(0x0c);
            outputStream.write(header.postPalette);
        }
    }

    /**
     * 向Pcx中写入一行数据
     * 
     * @param outputStream
     * @param b
     *            数据组数
     * @param offset
     *            行数据在数组中的起始位置
     * @param len
     *            行数据的长度
     * @return
     * @throws IOException
     */
    private static int writeLine(LEDataOutputStream outputStream, byte[] b, int offset, int len) throws IOException {
        int j = 0;
        int pos = offset;
        int end = offset + len;

        do {
            int i = 1;
            while ((pos + i < end) && (i < 63) && (b[pos] == b[pos + i])) {
                ++i;
            }
            byte val = b[pos];
            pos += i;

            if (i > 1) {
                outputStream.writeByte(i | 0xc0);
                outputStream.writeByte(val);
                j += 2;
            } else {
                if ((val & 0xc0) == 0xc0) {
                    outputStream.writeByte(0xc1);
                    j++;
                }
                outputStream.writeByte(val);
                j++;
            }
        } while (pos < end);

        return j;
    }
}
