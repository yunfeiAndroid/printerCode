package com.yun.printer;

import java.io.DataOutput;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by hancj on 16/7/28.
 */
public class LEDataOutputStream extends FilterOutputStream implements DataOutput {
    protected int written = 0;

    public LEDataOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    public void flush() throws IOException {
        super.out.flush();
    }

    public final int size() {
        return this.written;
    }

    public synchronized void write(int val) throws IOException {
        super.out.write(val);
        ++this.written;
    }

    public synchronized void write(byte[] b, int off, int len) throws IOException {
        super.out.write(b, off, len);
        this.written += len;
    }

    public final void writeBoolean(boolean b) throws IOException {
        super.out.write(b ? 1 : 0);
        ++this.written;
    }

    public final void writeByte(int v) throws IOException {
        super.out.write(v);
        ++this.written;
    }

    public final void writeBytes(String s) throws IOException {
        OutputStream outputStream = super.out;
        int len = s.length();

        for (int i = 0; i < len; ++i) {
            outputStream.write((byte) s.charAt(i));
        }

        this.written += len;
    }

    public final void writeChar(int c) throws IOException {
        OutputStream var2 = super.out;
        var2.write(c & 255);
        var2.write(c >>> 8 & 255);
        this.written += 2;
    }

    public final void writeChars(String s) throws IOException {
        OutputStream outputStream = super.out;
        int len = s.length();

        for (int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            outputStream.write(c >>> 8 & 255);
            outputStream.write(c & 255);
        }

        this.written += len * 2;
    }

    public final void writeDouble(double d) throws IOException {
        this.writeLong(Double.doubleToLongBits(d));
    }

    public final void writeFloat(float f) throws IOException {
        this.writeInt(Float.floatToIntBits(f));
    }

    public final void writeInt(int n) throws IOException {
        OutputStream outputStream = super.out;
        outputStream.write(n & 255);
        outputStream.write(n >>> 8 & 255);
        outputStream.write(n >>> 16 & 255);
        outputStream.write(n >>> 24 & 255);
        this.written += 4;
    }

    public final void writeLong(long v) throws IOException {
        OutputStream outputStream = super.out;
        outputStream.write((int) v & 255);
        outputStream.write((int) (v >>> 8) & 255);
        outputStream.write((int) (v >>> 16) & 255);
        outputStream.write((int) (v >>> 24) & 255);
        outputStream.write((int) (v >>> 32) & 255);
        outputStream.write((int) (v >>> 40) & 255);
        outputStream.write((int) (v >>> 48) & 255);
        outputStream.write((int) (v >>> 56) & 255);
        this.written += 8;
    }

    public final void writeShort(int v) throws IOException {
        OutputStream outputStream = super.out;
        outputStream.write(v & 255);
        outputStream.write(v >>> 8 & 255);
        this.written += 2;
    }

    public final void writeUTF(String s) throws IOException {
    }
}
