package com.lepu.ecg500.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

//import com.lib.common.util.log.KLog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: <BitmapUtil><br>
 * <ul>
 * <h1>功能列表：</h1>
 * <li>1.将scrollView转化为bitmap</li>
 * <li>2.将View转化为bitmap</li>
 * <li>3.将文本和view合拼生成一个bitmap</li>
 * <li>4.将一个bitmap转化为一个字节数组</li>
 * <li>5.将一个字节数组转化为一个bitmap</li>
 * <li>6.等比压缩Bitmap到720P内</li>
 * <li>7.图片压缩到 100 * 100 像素</li>
 * <li>8.图片压缩到指定的size</li>
 * <li>9.bitmap缩放到指定的宽和高</li>
 * <li>10.网络url获取Btimep,非异步操作,会有延迟</li>
 * <li>11.读取图片的旋转的角度</li>
 * <li>12.将图片按照某个角度进行旋转</li>
 * </ul>
 * Author: mxdl<br>
 * Date: 2018/6/11<br>
 * Version: V1.0.0<br>
 * Update: <br>
 */
public class BitmapUtil {
    /**
     * 截取竖scrollview的屏幕
     * <p>
     * 因为部分手机屏幕分辨率大,导致生成的bitmap会出现OOM异常,所以将view的bitmap设置为720p
     *
     * @param scrollView
     * @return
     */
    public static Bitmap convertViewToBitmap(ScrollView scrollView) {
        int h = 0;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(Color.WHITE); // 透明色背景会出现黑色,设置为白色,应该考虑父级背景色
        }
        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return scaled720Bitmap(bitmap);
    }

    /**
     * 将View生成bitmap
     * <p>
     * 因为部分手机屏幕分辨率大,导致生成的bitmap会出现OOM异常,所以将view的bitmap设置为720p
     *
     * @param view 要生成bitmap的View
     * @return
     */
    public static Bitmap convertViewToBitmap(View view) {
        if (view == null) return null;
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(scaled720Bitmap(view.getDrawingCache()));
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 合并Bitmap,该方法主要用于微信分享,添加标题栏的合并
     *
     * @return
     */
//    public static Bitmap combineBitmap_Title(Context context, String titleStr, View view) {
//        if (context == null || view == null) return null;
//
//        Bitmap bitmap = view instanceof ScrollView ? convertViewToBitmap((ScrollView) view) : convertViewToBitmap(view);
//
//        Paint textPaint = new Paint();
//        textPaint.setAntiAlias(true);
//        textPaint.setColor(Color.WHITE);
//        textPaint.setTextSize(DisplayUtil.sp2px(16));
//        // 文本的宽高
//        Paint.FontMetrics fm = textPaint.getFontMetrics();
//        float textWidth = textPaint.measureText(titleStr);
//        float textHeight = (float) Math.ceil(fm.descent - fm.ascent);
//
//        // 标题栏的高度
//        int titleHeight = DisplayUtil.dip2px(40);
//        // 先生成标题栏的bitmap,因为根据当前设备制作的标题栏要进行720P的压缩
//        Bitmap titleBitmap = Bitmap.createBitmap(bitmap.getWidth(), titleHeight, Bitmap.Config.RGB_565);
//        Canvas titleCanvas = new Canvas(titleBitmap);
//        // 绘制标题栏背景色
//        Paint titlePaint = new Paint();
//        titlePaint.setAntiAlias(true);
//        // ******** 5.0标题颜色待确定 ********************
//        // titlePaint.setColor(context.getResources().getColor(R.color.title_bg));
//        titlePaint.setStrokeWidth(titleHeight);
//        titleCanvas.drawLine(0, titleHeight / 2, bitmap.getWidth(), titleHeight / 2, titlePaint);
//        // 绘制标题文字
//        titleCanvas.drawText(titleStr, bitmap.getWidth() / 2 - textWidth / 2, titleHeight / 2 + textHeight / 3, textPaint);
//        //??? titleCanvas.save(Canvas.ALL_SAVE_FLAG);
//        titleCanvas.restore();
//        titleBitmap = Bitmap.createScaledBitmap(titleBitmap, 720, (int) ((720f / titleBitmap.getWidth()) * titleBitmap.getHeight()), true);
//        // 合成两个bitmap
//        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight() + titleBitmap.getHeight(), Bitmap.Config.RGB_565);
//        Canvas canvas = new Canvas(newBitmap);
//        canvas.drawBitmap(titleBitmap, 0, 0, null);
//        canvas.drawBitmap(bitmap, 0, titleBitmap.getHeight(), null);
//        //????canvas.save(Canvas.ALL_SAVE_FLAG);
//        canvas.restore();
//        bitmap.recycle();
//        titleBitmap.recycle();
//        return newBitmap;
//    }

    public static byte[] bmpToByteArray(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap bytesToBimap(byte[] b) {
        if (b == null || b.length == 0) return null;

        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static Bitmap bytesToBimap_alpha8(byte[] b) {
        if (b == null || b.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            options.outConfig = Bitmap.Config.ALPHA_8;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length, options);
    }
    public static Bitmap resizeBitmap(Bitmap bitmap ,float xScale,float yScale){
        Matrix matrix = new Matrix();
        matrix.postScale(xScale,yScale); //长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }
    /***//**
     * 图片去色,返回灰度图片
     * @param bmpOriginal 传入的图片
     * @return 去色后的图片
     */
    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    //把白色转换成透明
    public static Bitmap makeBitmapBgToTranslate(Bitmap mBitmap) {
        Bitmap createBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ALPHA_8);
        if (mBitmap != null) {
            int mWidth = mBitmap.getWidth();
            int mHeight = mBitmap.getHeight();
            for (int i = 0; i < mHeight; i++) {
                for (int j = 0; j < mWidth; j++) {
                    int color = mBitmap.getPixel(j, i);
                    int g = Color.green(color);
                    int r = Color.red(color);
                    int b = Color.blue(color);
                    int a = Color.alpha(color);
                    if(g>=250&&r>=250&&b>=250){
                        a = 0;
                    }
                    color = Color.argb(a, r, g, b);
                    createBitmap.setPixel(j, i, color);
                }
            }
        }
        return createBitmap;
    }
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

        if (bmp == null) return null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 等比压缩Bitmap到720P内
     *
     * @param bitmap
     * @return
     */
    private static Bitmap scaled720Bitmap(Bitmap bitmap) {
        if (bitmap == null) return null;
        int width, height;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            if (bitmap.getHeight() <= 720) return bitmap;
            height = 720;
            width = (int) ((720f / bitmap.getHeight()) * bitmap.getWidth());
        } else {
            if (bitmap.getWidth() <= 720) return bitmap;
            width = 720;
            height = (int) ((720f / bitmap.getWidth()) * bitmap.getHeight());
        }
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    /**
     * 图片压缩到 100 * 100 像素
     *
     * @param bitmap
     * @return
     */
    public static Bitmap scaleTo100Bitmap(Bitmap bitmap) {
        return scaleTo(bitmap, 100, 100);
    }

    public static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        // ByteArrayInputStream isBm = new
        // ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        // Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
        // 把ByteArrayInputStream数据生成图片
        return baos.toByteArray();
    }

    public static Bitmap compressImage(Bitmap image, int targetSize) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        Log.d("bitmapUtil", "要压缩的bitmap大小 : " + baos.toByteArray().length / 1024);
        int options = 100;
        while (baos.toByteArray().length / 1024 > targetSize) { // 循环判断如果压缩后图片是否大于指定的kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        Log.d("bitmapUtil", "压缩后的bitmap大小 : " + baos.toByteArray().length / 1024);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
        // 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap scaleTo(Bitmap bitmap, int width, int height) {

        if (bitmap == null) return null;
        int w, h;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            if (bitmap.getHeight() <= height) return bitmap;
            h = height;
            w = (int) ((100f / bitmap.getHeight()) * bitmap.getWidth());
        } else {
            if (bitmap.getWidth() <= width) return bitmap;
            w = width;
            h = (int) ((100f / bitmap.getWidth()) * bitmap.getHeight());
        }
        return Bitmap.createScaledBitmap(bitmap, w, h, true);
    }

    /**
     * 网络url获取Btimep,非异步操作,会有延迟
     *
     * @param strUrl
     * @return
     */
    public static Bitmap getBitMap(String strUrl) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            URL url = new URL(strUrl);
            URLConnection conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    /**
     * 将bitmap压缩至目标大小
     *
     * @param maxImageSize
     * @param filter
     * @return
     */
    public static Bitmap scaleDown(Uri imageUri, float maxImageSize, boolean filter) {
        Bitmap result = null;
        try {
            File file = new File(new URI(imageUri.toString()));
            Bitmap realImage = BitmapFactory.decodeFile(imageUri.getPath());
            long size = file.length();
            float ratio = size / maxImageSize;
            int width = Math.round((float) realImage.getWidth() / ratio);
            int height = Math.round((float) realImage.getHeight() / ratio);

            result = Bitmap.createScaledBitmap(realImage, width, height, filter);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Bitmap decodeSampledBitmapFromResource(Uri imageUri, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageUri.getPath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imageUri.getPath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;


            // final int halfHeight = height / 2;
            // final int halfWidth = width / 2;
            //
            // while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
            // {
            // inSampleSize *= 2;
            // }
        }

        return inSampleSize;
    }

    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;

        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    /**
     * 获取压缩后的图片
     *
     * @return
     */
    public static Bitmap decodeBitmapFromFile(String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }
    public static boolean isImageFile(String url){
        if(TextUtils.isEmpty(url)){
            return false;
        }
        String reg = ".+(\\.jpeg|\\.jpg|\\.gif|\\.bmp|\\.png).*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.find();
    }
    /**
     * 获取图片原始的宽、高
     *
     * @param url
     * @return
     */
    public static int[] getImageSize(String url) {
        int[] size = new int[]{0, 0};
        // TODO: 2021/5/12 mwj 抽取FileUtil的方法
       // if (FileUtil.isImageFile(url)) {
          if(isImageFile(url)){
            BitmapFactory.Options options = new BitmapFactory.Options();
            // 设置为true,表示解析Bitmap对象，该对象不占内存
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // 图片宽高
            switch (getBitmapDegree(url)) {
                case 90:
                case 270:
                    return new int[]{options.outHeight, options.outWidth};
                default:
                    return new int[]{options.outWidth, options.outHeight};
            }
        }
        return size;
    }

    public static int[] getAdjustImageSize(Context context, int sourceWidth, int sourceHeight) {
        int screenWidth = ScreenUtils.getScreenWidth(context);
        int screenHeight = ScreenUtils.getScreenHeight(context);
        //默认的宽和高
        int coverWidth = 0;// - DisplayUtil.dip2px(16) * 2;
        int coverHeight = 0;//

        if (sourceWidth > 0 && sourceHeight > 0) {
            //如果是横屏图
            if (sourceWidth > sourceHeight) {
                //图比屏幕还宽则以屏幕的宽为基准进行缩放
                if (sourceWidth > screenWidth) {
                    coverWidth = screenWidth;//屏幕的宽就是图片的宽
                    float scaleRate = (float) screenWidth / sourceWidth;//缩放比例
                    coverHeight = (int) (scaleRate * sourceHeight);//图片的宽高等比例的缩小
                } else {
                    //否则图片是多大的就显示多大的
                    coverWidth = sourceWidth;
                    coverHeight = sourceHeight;
                }
            } else {
                //如果是竖屏的图，且比屏幕还高则以屏幕的高为基准进行等比例缩放
                if (sourceHeight > screenHeight) {
                    coverHeight = screenHeight;//屏幕的高就是图片的高
                    float scaleRate = (float) screenHeight / sourceHeight;//缩放比例
                    coverWidth = (int) (scaleRate * sourceWidth);//图片的宽等比例的缩小
                } else {
                    //否则图片是多大的就显示多大的
                    coverWidth = sourceWidth;
                    coverHeight = sourceHeight;
                }
            }
        }
        if (coverWidth == 0 && coverHeight == 0) {
            return null;
        }
        return new int[]{coverWidth, coverHeight};
    }

    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static final int IMAGE_QUALITY_SMALL = 60;
    public static final int IMAGE_QUALITY_MEDIUM = 80;// 一般使用这个,适中
    public static final int IMAGE_QUALITY_BIG = 90;

    public static Bitmap convertToBitmap(String filePath, int w, int h) {

        Bitmap bitmap = getBitmap(filePath);
        if (bitmap != null) {
            bitmap = zoomBitmap(bitmap, w, h);
        }

        return bitmap;
    }

    public static Bitmap getBitmap(String filePath, int newWidth, int newHeight) {
        Bitmap bitmap = null;

        File file = new File(filePath);
        if (file.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, newWidth
                    * newHeight);
            opts.inJustDecodeBounds = false;
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
            try {
                bitmap = BitmapFactory.decodeFile(filePath, opts);
            } catch (OutOfMemoryError err) {
                err.printStackTrace();
            }
        }

        return bitmap;
    }

    public static Bitmap getBitmap(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        FileInputStream fis = null;
        Bitmap bitmap = null;

        File file = new File(filePath);
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                bitmap = BitmapFactory.decodeStream(fis);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    @SuppressWarnings("deprecation")
    public static Bitmap getBitmapSmall(String filePath) {
        FileInputStream fis = null;
        Bitmap bitmap = null;

        File file = new File(filePath);
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;// 宽是原始图片1/2,高是原始图片1/2.整体图片是原始图片的1/4
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inPurgeable = true;
                options.inInputShareable = true;
                bitmap = BitmapFactory.decodeStream(fis, null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    @SuppressWarnings("deprecation")
    public static Bitmap getBitmap(String filePath, Bitmap.Config bitmapConfig) {

        Bitmap bitmap = null;
        File file = new File(filePath);
        if (file.exists()) {
            try {
                FileInputStream inputStream = new FileInputStream(file);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = bitmapConfig;
                options.inPurgeable = true;
                options.inInputShareable = true;
                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /*
     * 从流中，取原始图片
     */
    public static Bitmap getBitmap(InputStream inputStream) {

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
     * 从流中，图片大小变为原来的1/4，图片色差为 ARGB_4444
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getBitmapSmall(InputStream inputStream) {

        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;// 宽是原始图片1/2,高是原始图片1/2.整体图片是原始图片的1/4
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /*
     * 从流中读取图片，可以设置 bitmapConfig
     */
    @SuppressWarnings("deprecation")
    public static Bitmap getBitmap(InputStream inputStream,
                                   Bitmap.Config bitmapConfig) {

        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = bitmapConfig;
            options.inPurgeable = true;
            options.inInputShareable = true;
            bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {

        }
        return bitmap;
    }

    // ===========================================================================================

    /**
     * write file /data/data/PACKAGE_NAME/files 目录下
     */
    public static void saveImageToFile(Context context, String fileName,
                                       Bitmap bitmap) {
        saveImageToFile(context, fileName, bitmap, IMAGE_QUALITY_MEDIUM);
    }

    public static void saveImageToFile(Context context, String fileName,
                                       Bitmap bitmap, int quality) {
        if (bitmap == null || fileName == null || context == null) {
            return;
        }

        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
            byte[] bytes = stream.toByteArray();
            fos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveImageToSd(Bitmap bitmap, String filePath) {
        saveImageToSd(bitmap, filePath, IMAGE_QUALITY_BIG);
    }

    public static void saveImageToSd(Bitmap.CompressFormat compressFormat,
                                     Bitmap bitmap, String filePath) {
        saveImageToSd(compressFormat, bitmap, filePath, IMAGE_QUALITY_BIG);
    }

    /*
     * write file to SD
     */
    public static void saveImageToSd(Bitmap bitmap, String filePath, int quality) {

        if (bitmap == null || filePath == null) {
            return;
        }
        boolean bCompress = false;
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filePath);
            bCompress = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);//quality/3, fout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.flush();
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveImageToSd(Bitmap.CompressFormat compressFormat,
                                     Bitmap bitmap, String filePath, int quality) {

        if (bitmap == null || filePath == null) {
            return;
        }

        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream(filePath);
            bitmap.compress(compressFormat, quality, fout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null) {
                    fout.flush();
                    fout.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // =============================================================================================
    /*
     * 给图片添加水印 水印图片和水印文字
     */
    public static Bitmap createPrintBitmap(Bitmap src, Bitmap waterMark,
                                           String title) {

        if (src == null) {
            return null;
        }

        int w = src.getWidth();
        int h = src.getHeight();

        // 需要处理图片太大造成的内存超过的问题,这里我的图片很小所以不写相应代码了
        Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(newb);
        cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
        Paint paint = new Paint();

        // 加入图片
        if (waterMark != null) {
            int ww = waterMark.getWidth();
            int hh = waterMark.getHeight();
            paint.setAlpha(50);
            cv.drawBitmap(waterMark, (w - ww) / 2, (h - hh) / 2, paint);// 屏幕的中间画图
        }

        // 加入文字
        if (title != null) {
            String familyName = "宋体";
            Typeface font = Typeface.create(familyName, Typeface.BOLD);
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.GRAY);
            textPaint.setTypeface(font);
            textPaint.setTextSize(50);
            // 这里是自动换行的
            StaticLayout layout = new StaticLayout(title, textPaint, w,
                    Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
            layout.draw(cv);
            // cv.drawText(title, w / 2, h / 2, textPaint);
        }
        cv.save();// 保存Canvas.ALL_SAVE_FLAG
        cv.restore();// 存储
        return newb;
    }

    /*
     * 获取activity的截图
     */
    @SuppressWarnings("deprecation")
    public static Bitmap takeScreenShot(Activity activity) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        System.out.println(statusBarHeight);

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        // int height = activity.getWindowManager().getDefaultDisplay()
        // .getHeight();
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width,
                b1.getHeight() - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    /*
     * 获取view的截图
     */
    public static Bitmap getViewBitmap(View v) {

        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_4444);
        v.draw(new Canvas(bitmap));

        return bitmap;
    }

    /*
     * 获取ListView的截图
     */
    public static Bitmap getListViewBitmap(ListView listView) {

        int h = 0;
        for (int i = 0; i < listView.getChildCount(); i++) {
            h += listView.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(listView.getWidth(), h,
                Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        listView.draw(canvas);

        return bitmap;
    }

    /*
     * 合并成一张图片
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, 0, 0, null);
        return bitmap;
    }

    /*
     * 合并成一张图片
     */
    public static Bitmap mergeBitmap(Bitmap firstBitmap, Bitmap secondBitmap,
                                     float left, float top) {
        Bitmap bitmap = Bitmap.createBitmap(firstBitmap.getWidth(),
                firstBitmap.getHeight(), firstBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(firstBitmap, new Matrix(), null);
        canvas.drawBitmap(secondBitmap, left, top, null);
        return bitmap;
    }

    public static Bitmap getBitmapByResId(Context context, int resId) {
        return BitmapFactory.decodeResource(context.getResources(), resId);
    }

    public static Bitmap getBitmapByByte(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private static int computeSampleSize(BitmapFactory.Options options,
                                         int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    // /**
    // * 通过uri获取文件的绝对路径
    // *
    // * @param uri
    // * @return
    // */
    // @SuppressWarnings("deprecation")
    // public static String getAbsoluteImagePath(Activity context, Uri uri) {
    // String imagePath = "";
    // String[] proj = { MediaStore.Images.Media.DATA };
    // Cursor cursor = context.managedQuery(uri, proj, // Which columns to
    // // return
    // null, // WHERE clause; which rows to return (all rows)
    // null, // WHERE clause selection arguments (none)
    // null); // Order-by clause (ascending by name)
    //
    // if (cursor != null) {
    // int column_index = cursor
    // .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    // if (cursor.getCount() > 0 && cursor.moveToFirst()) {
    // imagePath = cursor.getString(column_index);
    // }
    // }
    //
    // return imagePath;
    // }

    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_4444
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    /**
     * 获得圆角图片的方法
     *
     * @param bitmap
     * @param roundPx 一般设成14
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 获得带倒影的图片方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
                width, height / 2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
                (height + height / 2), Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
                + reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 获取图片类型
     *
     * @param file
     * @return
     */
    public static String getImageType(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            String type = getImageType(in);
            return type;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取图片的类型信息
     *
     * @param in
     * @return
     * @see #getImageType(byte[])
     */
    public static String getImageType(InputStream in) {
        if (in == null) {
            return null;
        }
        try {
            byte[] bytes = new byte[8];
            in.read(bytes);
            return getImageType(bytes);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 获取图片的类型信息
     *
     * @param bytes 2~8 byte at beginning of the image file
     * @return image mimetype or null if the file is not image
     */
    public static String getImageType(byte[] bytes) {
        if (isJPEG(bytes)) {
            return "image/jpeg";
        }
        if (isGIF(bytes)) {
            return "image/gif";
        }
        if (isPNG(bytes)) {
            return "image/png";
        }
        if (isBMP(bytes)) {
            return "application/x-bmp";
        }
        return null;
    }

    private static boolean isJPEG(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == (byte) 0xFF) && (b[1] == (byte) 0xD8);
    }

    private static boolean isGIF(byte[] b) {
        if (b.length < 6) {
            return false;
        }
        return b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isPNG(byte[] b) {
        if (b.length < 8) {
            return false;
        }
        return (b[0] == (byte) 137 && b[1] == (byte) 80 && b[2] == (byte) 78
                && b[3] == (byte) 71 && b[4] == (byte) 13 && b[5] == (byte) 10
                && b[6] == (byte) 26 && b[7] == (byte) 10);
    }

    private static boolean isBMP(byte[] b) {
        if (b.length < 2) {
            return false;
        }
        return (b[0] == 0x42) && (b[1] == 0x4d);
    }

    /**
     * 让Gallery上能马上看到该图片
     */
    public static void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
        Bitmap newbmp = null;
        if (bitmap != null) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            float scaleWidht = ((float) w / width);
            float scaleHeight = ((float) h / height);
            matrix.postScale(scaleWidht, scaleHeight);
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
                    true);
        }
        return newbmp;
    }

    /**
     * 根据原图和变长绘制圆形图片
     *
     * @param source
     * @param min
     * @return
     */
    public static Bitmap createCircleImage(Bitmap source, int min) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_4444);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    public static String getFileImageUrl(String fileUrl) {
        String filePath = String.format("file://%s", fileUrl);
        return filePath;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /*
     * 旋转图片
     *
     * @param angle
     *
     * @param bitmap
     *
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {

        if (bitmap == null) {
            return null;
        }

        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /*
     * 通过filepath 获取uri 这个是最准的，有些取不到照相的uri
     */

    // @SuppressLint("SdCardPath")
    // public static String getImagePathFromUri(Context context, Uri fileUrl) {
    // String fileName = null;
    // Uri filePathUri = fileUrl;
    // if (fileUrl != null) {
    // if (fileUrl.getScheme().toString().compareTo("content") == 0) {
    // // content://开头的uri
    // Cursor cursor = context.getContentResolver().query(fileUrl,
    // null, null, null, null);
    // if (cursor != null && cursor.moveToFirst()) {
    // int column_index = cursor
    // .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    // fileName = cursor.getString(column_index); // 取出文件路径
    //
    // // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
    // if (!fileName.startsWith("/storage")
    // && !fileName.startsWith("/mnt")) {
    // if (!fileName.startsWith("/system")) {
    // // 检查是否有”/mnt“前缀
    // if (Build.VERSION.SDK_INT > 14) {
    // fileName = "/storage" + fileName;
    // } else {
    // fileName = "/mnt" + fileName;
    // }
    // }
    // }
    // cursor.close();
    // }
    // } else if (fileUrl.getScheme().compareTo("file") == 0) // file:///开头的uri
    // {
    // fileName = filePathUri.toString();// 替换file://
    // fileName = filePathUri.toString().replace("file://", "");
    //
    // int index = fileName.indexOf("/sdcard");
    // fileName = (index == -1) ? fileName : fileName.substring(index);
    //
    // if (!fileName.startsWith("/mnt")
    // && !fileName.startsWith("/storage")) {
    // if (!fileName.startsWith("/system")) {
    // // 检查是否有”/mnt“前缀
    // if (Build.VERSION.SDK_INT > 14) {
    // fileName = "/storage" + fileName;
    // } else {
    // fileName = "/mnt" + fileName;
    // }
    //
    // }
    // }
    // }
    // }
    // return fileName;
    // }

    /**
     * 获取图片文件的信息，是否旋转了90度，如果是则反转
     *
     * @param bitmap 需要旋转的图片
     * @param path   图片的路径
     */
    public static Bitmap reviewPicRotate(Bitmap bitmap, String path) {

        if (bitmap == null) {
            return null;
        }

        int degree = getPicRotate(path);
        if (degree != 0) {
            Matrix m = new Matrix();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            // m.setRotate(degree); // 旋转angle度
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// ä»Žæ–°ç”Ÿæˆ�å›¾ç‰‡
        }
        return bitmap;
    }

    /**
     * 读取图片文件旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片旋转的角度
     */
    public static int getPicRotate(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param imageUri
     */
    @SuppressLint("NewApi")
    public static String getImageAbsolutePath(Activity context, Uri imageUri) {
        if (context == null || imageUri == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri)) {
                return imageUri.getLastPathSegment();
            }
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @SuppressWarnings("deprecation")
    public static void setBackgroundResource(Context context, int resId,
                                             ImageView imageView) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory
                    .decodeResource(context.getResources(), resId);
        } catch (OutOfMemoryError oom) {
            System.gc();
            System.runFinalization();
        } finally {

        }
        if (bitmap != null && !bitmap.isRecycled()) {
            BitmapDrawable bd = new BitmapDrawable(context.getResources(),
                    bitmap);
            imageView.setBackgroundDrawable(bd);
        }
    }

    public static void destroyBackgroundResource(Context context,
                                                 ImageView imageView) {

        try {
            BitmapDrawable bd = (BitmapDrawable) imageView.getBackground();
            imageView.setBackgroundResource(0);// 别忘了把背景设为null，避免onDraw刷新背景时候出现used
            // a recycled bitmap错误
            bd.setCallback(null);
            bd.getBitmap().recycle();
        } catch (Exception e) {
        }

    }

    public static void setImageResource(Context context, int resId,
                                        ImageView imageView) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory
                    .decodeResource(context.getResources(), resId);
        } catch (OutOfMemoryError oom) {
            System.gc();
            System.runFinalization();
        } finally {

        }
        if (bitmap != null && !bitmap.isRecycled()) {
            imageView.setImageBitmap(bitmap);
        }
    }

    public static void destroyImageResource(Context context, ImageView imageView) {

        try {
            BitmapDrawable bd = (BitmapDrawable) imageView.getBackground();
            imageView.setImageResource(0);// 别忘了把背景设为null，避免onDraw刷新背景时候出现used
            // a recycled bitmap错误
            bd.setCallback(null);
            bd.getBitmap().recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static int[] getImageWidthHeight(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();

        /**
         * 最关键在此，把options.inJustDecodeBounds = true;
         * 这里再decodeFile()，返回的bitmap为空，但此时调用options.outHeight时，已经包含了图片的高了
         */
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回的bitmap为null
        /**
         * options.outHeight为原始图片的高
         */
        return new int[]{options.outWidth, options.outHeight};
    }

    /**
     * @param bitmap     原图
     * @param edgeLength 希望得到的正方形部分的边长
     * @return 缩放截取正中部分后的位图。
     */
    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }
        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            // 压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (edgeLength * Math.max(widthOrg, heightOrg) / Math
                    .min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
                        scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            // 从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft,
                        edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }

    /**
     * 将Bitmap存为 .bmp格式图片
     *
     * @param bitmap 原图片
     */
    public static void saveBmp(Bitmap bitmap, String path) {
        if (bitmap == null) {
            return;
        }

        byte[] bmpData;
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        // 图像数据大小
        int bufferSize = nBmpHeight * nBmpWidth * 4;
        try {
            File file = new File(path);
            File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fileos = new FileOutputStream(path);
            // bmp文件头
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // 保存bmp文件头
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp信息头
            long biSize = 40L;
            int biPlanes = 1;
            int biBitCount = 32;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // 保存bmp信息头
            writeDword(fileos, biSize);
            writeLong(fileos, (long) nBmpWidth);
            writeLong(fileos, (long) nBmpHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // 像素扫描
            bmpData = new byte[bufferSize];
            int wWidth = (nBmpWidth * 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol) {
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 4) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 3] = (byte) Color.alpha(0xff);
                }
            }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    private static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    private static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    /**
     * 保存图片
     *
     * @param filePath
     * @param bitmap
     * @param quality
     * @return
     */
    public static boolean saveBitmap(String filePath, Bitmap bitmap, int quality) {
        boolean flag = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
            fos.flush();
            flag = true;
        } catch (Exception e) {
            CustomTool.e(Log.getStackTraceString(e));
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    CustomTool.e(Log.getStackTraceString(e));
                }
            }
        }
        return flag;
    }
}
