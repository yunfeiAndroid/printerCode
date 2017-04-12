package com.yun.testprinter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yun.printer.BitmapUtil1;
import com.yun.printer.BmpFile;
import com.yun.printer.PcxFile;


public class CustomTicketPhotoView {

    private Paint mPaint;
    private Paint mPaint0;
    Context context;
    TickInfo info;
    private Rect mSrcRect;
    private Rect mDestRect;

    public CustomTicketPhotoView(Context context, TickInfo info) {
        this.context = context;
        this.info = info;

        init();
        saveCustomViewBitmap();
    }

    public void init() {
        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/FZLTZH_GBK_GBK.TTF");

         //  18号字体
        mPaint0 = new Paint(); //  标题以及副券
        mPaint0.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint0.setStrokeWidth(0.6f);
        mPaint0.setAntiAlias(true);
        mPaint0.setTextSize(38);
        mPaint0.setTypeface(font);
        mPaint0.setColor(Color.BLACK);
         //  16号字体
        mPaint = new Paint();  //  普通绘制
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTypeface(font);
        mPaint.setColor(Color.BLACK);
    }

    /**
     * 截屏当前视图保存自定义view的截图 在View内部截屏生成图片
     */
    @SuppressLint("WrongCall")
    public void saveCustomViewBitmap() {
         //  获取自定义view图片的大小
        temBitmap = Bitmap.createBitmap(1570, 397, Bitmap.Config.ARGB_8888);

         //  使用Canvas，调用自定义view控件的onDraw方法，绘制图片
        canvas = new Canvas(temBitmap);
         //  canvas加抗锯齿
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        canvas.drawBitmap(temBitmap, 0, 0, mPaint);

        drawTickets();
        if (temBitmap == null) {
            return;
        }

           createSignFile(temBitmap);
    }
    
    private void createSignFile(Bitmap mSignBitmap) {  
        ByteArrayOutputStream baos = null;  
        FileOutputStream fos = null;  
        String path = null;    
        File file = null;  
        try {    
            path = Environment.getExternalStorageDirectory() + File.separator + "yuan.bmp";   
            file = new File(path);  
            fos = new FileOutputStream(file);  
            baos = new ByteArrayOutputStream();  
            //������ó�Bitmap.compress(CompressFormat.JPEG, 100, fos) ͼƬ�ı������Ǻ�ɫ��  
            mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos); 
            
            Log.d("saveOK", "保存成功！");
            byte[] b = baos.toByteArray();    
            if (b != null) {    
                fos.write(b);   
            }    
        } catch (IOException e) {    
            e.printStackTrace();    
        } finally {    
            try {    
                if (fos != null) {  
                    fos.close();  
                }  
                if (baos != null) {  
                    baos.close();  
                }  
            } catch (IOException e) {    
                e.printStackTrace();    
            }    
        }    
    }  

    public byte[] getPhotoBytes() {
        try {
             //  InputStream imageFile = getImageStream(temBitmap);
             //  读取Bmp文件内容
            byte[] bmpBuf = BitmapUtil1.getInstance().getbitBmp(temBitmap);
            Log.e("getPhotoBytes", "数据长度：" + bmpBuf.length);
             //  Pcx需要使用BmpFile对象来做预分析
            BmpFile bmpFile = new BmpFile(bmpBuf);
            byte[] pcx = PcxFile.convert(bmpFile);

            return pcx;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

     //  调整偏移量
    private float xOff = 0;
    private float yOff = 0;
    private Bitmap temBitmap;
    private Canvas canvas;
    private String mSeat;
    private String fuPrice;

    public int getStringNeededIndex(String str, Paint pt, int max) {
        int stringWidth = (int) pt.measureText(str);
        if (stringWidth > max) {
            return getStringNeededIndex(str.substring(0, str.length() - 1), pt, max);
        }
        return str.length();
    }

    @SuppressLint({ "DrawAllocation", "SimpleDateFormat" })
    public void drawTickets() {
        canvas.drawColor(Color.WHITE); //  给画布设置背景

         //  判断showName的长度，进行换行1
        String name = info.getProductName();
        if (TextUtils.isEmpty(name)) {
            Log.d("debug", "showName is null。。。");
            return;
        } else {

            int stringWidth = (int) mPaint0.measureText(name);
            if (stringWidth < 1112) {
                yOff = 0;
                canvas.drawText(name, 0, 60, mPaint0);

                yOff = 10;
            } else {
                int stringWrapIndex = getStringNeededIndex(name, mPaint0, 1112);
                canvas.drawText(name.substring(0, stringWrapIndex), 0, 35, mPaint0);

                int stringWrapIndex2 = getStringNeededIndex(name.substring(stringWrapIndex, name.length()), mPaint0,
                        1112);
                canvas.drawText(name.substring(stringWrapIndex, stringWrapIndex + stringWrapIndex2), 0, 94, mPaint0);

                yOff = 35;
            }

             //  二维码
            Bitmap bm = QRCodeUtil.createQRImage00(info.getTicketNo(), 300, 300);
            mSrcRect = new Rect(0, 0, bm.getWidth(), bm.getHeight());
            mDestRect = new Rect(-50, (int) (64 + yOff), 254, (int) (364 + yOff));
            canvas.drawBitmap(bm, mSrcRect, mDestRect, mPaint);

            mPaint.setTextSize(33);
            String dateChinese = null;
             //  设定时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
            long showDate = info.getShowStartTime();
            Date date = new Date(showDate);
            dateChinese = sdf.format(date);
            if (!TextUtils.isEmpty(info.getArea())) {
                mSeat = info.getArea() + "  " + info.getSeat();
            } else {
                mSeat = info.getSeat();
            }

            String printPrice = null;
            String price = null;
            
            if (!TextUtils.isEmpty(info.getRemark())) {
                price += "元(" + info.getRemark() + ")";
            } else {
                price += "元";
            }

            canvas.drawText("日期/Date    ：" + dateChinese, 245 + xOff, 129 + yOff, mPaint); //  普通绘制
            /**
             * 处理场馆信息
             */
            String venue = info.getVenueName();
            int stringvenueWidth = (int) mPaint.measureText(venue);
            if (stringvenueWidth < 650) {
                canvas.drawText("场馆/Venue ：" + venue, 242 + xOff, 191 + yOff, mPaint);
            } else {
                int stringWrapIndex = getStringNeededIndex(venue, mPaint, 650);
                canvas.drawText("场馆/Venue ：" + venue.substring(0, stringWrapIndex), 242 + xOff, 191 + yOff, mPaint);
            }

            canvas.drawText("座位/Seat    ：", 245 + xOff, 253 + yOff, mPaint);

            mPaint0.setTextSize(34);
            canvas.drawText(mSeat, 472 + xOff, 253 + yOff, mPaint0);
            canvas.drawText("票价/Price   ：", 245 + xOff, 315 + yOff, mPaint);
            canvas.drawText(price, 472 + xOff, 315 + yOff, mPaint0);

            mPaint.setTextSize(22);
            canvas.drawText("NO." + info.getOrderId() + "     Ticket NO." + info.getTicketNo(), 245 + xOff, 360 + yOff,
                    mPaint);
            mPaint.setTextSize(18);
            canvas.drawText("此处请勿玷污！", 50 + xOff, 360 + yOff, mPaint);

            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(33);
            mPaint0.setTextAlign(Paint.Align.CENTER);
            mPaint0.setTextSize(38);

             //  副券
            canvas.drawText(fuPrice + "元", 1405, 260 + yOff, mPaint0);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date2 = new Date(showDate);
            dateChinese = sdf2.format(date2);
            canvas.drawText(dateChinese, 1405, 343 + yOff, mPaint);
             //  判断换行
            if (!TextUtils.isEmpty(info.getArea())) { //  两行
                canvas.drawText("" + info.getArea(), 1405, 80 + yOff, mPaint);
                canvas.drawText("" + info.getSeat(), 1405, 130 + yOff, mPaint);
            } else {
                canvas.drawText("" + info.getSeat(), 1405, 90 + yOff, mPaint);
            }
        }
    }

}