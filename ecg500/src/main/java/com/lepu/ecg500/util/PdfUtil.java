package com.lepu.ecg500.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.util.Log;

//import com.lib.common.util.log.KLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfUtil {

    /**
     * 生成pdf
     * @param bitmaps
     * @param outfilePath
     * @param isVertical
     * @return
     */
    public static File saveBitmapForPdf(List<Bitmap> bitmaps, String outfilePath, boolean isVertical) {
        PdfDocument doc = new PdfDocument();
        int pageWidth;
        int pageHeight;

        if(isVertical){
            pageWidth = PrintAttributes.MediaSize.ISO_A4.getWidthMils() * 72 / 1000;
        }else{
            pageWidth = PrintAttributes.MediaSize.ISO_A4.getHeightMils() * 72 / 1000;
        }

        float scale = (float) pageWidth / (float) bitmaps.get(0).getWidth();
        pageHeight = (int) (bitmaps.get(0).getHeight() * scale);

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        File file = new File(outfilePath);
        FileOutputStream outputStream = null;
        try {
            for (int i = 0; i < bitmaps.size(); i++) {
                //KLog.d(String.format("开始生成第%d页图片",i+1));

                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create();
                PdfDocument.Page page = doc.startPage(newPage);
                Canvas canvas = page.getCanvas();
                Bitmap bitmap = bitmaps.get(i);
                canvas.drawBitmap(bitmap, matrix, paint);
                doc.finishPage(page);

                if(bitmap != null){
                    bitmap.recycle();
                    bitmap = null;
                }
                //KLog.d(String.format("生成第%d页图片完成",i+1));
            }

            outputStream = new FileOutputStream(file);
            doc.writeTo(outputStream);
        }catch (Exception e) {
            CustomTool.e(Log.getStackTraceString(e));
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                CustomTool.e(Log.getStackTraceString(e));
            }
            doc.close();
        }
        return file;
    }

    /**
     *
     * @param context
     * @param pdfSrcFile
     * @param currentPageIndex
     * @return
     */
    public static Bitmap pdfReportToImage(Context context, File pdfSrcFile, int currentPageIndex) {

        PdfRenderer pdfRenderer = null;
        ParcelFileDescriptor fileDescriptor = null;
        PdfRenderer.Page currentPage = null;
        Bitmap converBitmap = null;

        try {
            fileDescriptor = ParcelFileDescriptor.open(pdfSrcFile, ParcelFileDescriptor.MODE_READ_ONLY);
            // This is the PdfRenderer we use to render the PDF.
            pdfRenderer = new PdfRenderer(fileDescriptor);
            currentPage = pdfRenderer.openPage(currentPageIndex);
            // Important: the destination bitmap must be ARGB (not RGB).
            //横屏，宽高对调
            converBitmap = Bitmap.createBitmap(Const.A4_HEIGHT, Const.A4_WIDTH,Bitmap.Config.ARGB_8888);
            // Here, we render the page onto the Bitmap.
            // To render a portion of the page, use the second and third parameter. Pass nulls to get
            // the default result.
            // Pass either RENDER_MODE_FOR_DISPLAY or RENDER_MODE_FOR_PRINT for the last parameter.
            currentPage.render(converBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        }catch (Exception e){
            CustomTool.e(Log.getStackTraceString(e));
        }finally {
            if(currentPage != null){
                currentPage.close();
            }
            if(pdfRenderer != null){
                pdfRenderer.close();
            }
            if(fileDescriptor != null){
                try {
                    fileDescriptor.close();
                } catch (IOException e) {
                    CustomTool.e(Log.getStackTraceString(e));
                }
            }
        }

        return converBitmap;
    }

}
