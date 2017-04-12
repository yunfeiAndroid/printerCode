package com.yun.printer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hancj on 16/7/28. 基于小头的数据输入流
 */
public class LEDataInputStream extends FilterInputStream implements DataInput {
    DataInputStream dataIn;

    public LEDataInputStream(InputStream is) {
        super(is);
        this.dataIn = new DataInputStream(is);
    }

    public void close() throws IOException {
        this.dataIn.close();
    }

    public final int read(byte[] b) throws IOException {
        return this.dataIn.read(b, 0, b.length);
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        int r = this.dataIn.read(b, off, len);
        return r;
    }

    public final boolean readBoolean() throws IOException {
        int val = this.dataIn.read();
        if (val < 0) {
            throw new EOFException();
        } else {
            return val != 0;
        }
    }

    public final byte readByte() throws IOException {
        int val = this.dataIn.read();
        if (val < 0) {
            throw new EOFException();
        } else {
            return (byte) val;
        }
    }

    public final char readChar() throws IOException {
        int val1 = this.dataIn.read();
        int val2 = this.dataIn.read();
        if ((val1 | val2) < 0) {
            throw new EOFException();
        } else {
            return (char) (val1 + (val2 << 8));
        }
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    public final void readFully(byte[] var1) throws IOException {
        this.dataIn.readFully(var1, 0, var1.length);
    }

    public final void readFully(byte[] var1, int off, int len) throws IOException {
        this.dataIn.readFully(var1, off, len);
    }

    public final int readInt() throws IOException {
        int var1 = this.dataIn.read();
        int var2 = this.dataIn.read();
        int var3 = this.dataIn.read();
        int var4 = this.dataIn.read();
        if ((var1 | var2 | var3 | var4) < 0) {
            throw new EOFException();
        } else {
            return var1 + (var2 << 8) + (var3 << 16) + (var4 << 24);
        }
    }

    public final String readLine() throws IOException {
        return new String();
    }

    public final long readLong() throws IOException {
        int var1 = this.readInt();
        int var2 = this.readInt();
        return ((long) var1 & 4294967295L) + (long) (var2 << 32);
    }

    public final short readShort() throws IOException {
        int var1 = this.dataIn.read();
        int var2 = this.dataIn.read();
        if ((var1 | var2) < 0) {
            throw new EOFException();
        } else {
            return (short) (var1 + (var2 << 8));
        }
    }

    public final String readUTF() throws IOException {
        return new String();
    }

    public static final String readUTF(DataInput var0) throws IOException {
        return new String();
    }

    public final int readUnsignedByte() throws IOException {
        int var1 = this.dataIn.read();
        if (var1 < 0) {
            throw new EOFException();
        } else {
            return var1;
        }
    }

    public final int readUnsignedShort() throws IOException {
        int var1 = this.dataIn.read();
        int var2 = this.dataIn.read();
        if ((var1 | var2) < 0) {
            throw new EOFException();
        } else {
            return var1 + (var2 << 8);
        }
    }

    public final int skipBytes(int n) throws IOException {
        return this.dataIn.skipBytes(n);
    }
}
