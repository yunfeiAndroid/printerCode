package com.yun.printer;

import java.io.DataInput;
import java.io.IOException;

/**
 * Created by hancj on 16/7/28.
 */
public class RGBQuad {
    public byte red;
    public byte green;
    public byte blue;
    public byte alpha;

    public void read(DataInput input) throws IOException {
        this.red = (byte) input.readUnsignedByte();
        this.green = (byte) input.readUnsignedByte();
        this.blue = (byte) input.readUnsignedByte();
        this.alpha = (byte) input.readUnsignedByte();
    }

    /**
     * 是否灰度点
     * 
     * @return
     */
    public boolean isGray() {
        return red == green && green == blue;
    }
}
