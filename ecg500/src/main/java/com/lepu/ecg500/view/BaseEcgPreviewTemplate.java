package com.lepu.ecg500.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;


import com.lepu.ecg500.R;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;
import com.lepu.ecg500.util.Const;
import com.lepu.ecg500.util.CustomTool;

import java.util.List;

public abstract class BaseEcgPreviewTemplate {

    /**
     * 添加心电数据
     *
     * @param dataArray
     */
    public abstract void addEcgData(short[][] dataArray);

    /**
     * 添加预览节律心电数据
     *
     * @param dataArray
     */
    public void addPreviewRthEcgData(short[][] dataArray) {
    }

    /**
     * 初始化参数
     */
    public abstract void initParams();

    /**
     * 绘制导联信息 定标，名称
     */
    public abstract void drawLeadInfo();

    Context context;
    int drawWidth;
    int drawHeight;
    float[] gainArray;
    EcgSettingConfigEnum.LeadSpeedType leadSpeedType;
    LeadManager leadManager;
    Bitmap bgBitmap;
    Canvas canvasBg;
    Paint wavePaint;
    Paint fontPaint;
    Paint timeRulerPaint;
    Path timeRulerPath;
    Paint leadStandardPaint;
    //小网格
    float gridSpace;
    float largeGridSpace;
    //int standardLen;
    RectF gridRect;
    //子类赋值
    float leadWidth;
    float leadHeight;
    int leadLines;
    int leadColumes;
    Bitmap gridCellBitmap;
    //定标符号
    ScaleBean scaleBean;
    PreviewPageEnum previewPageEnum;
    List<String> leadNameList;
    //数据多列显示时的顺序
    RecordSettingConfigEnum.RecordOrderType recordOrderType;
    boolean drawReportGridBg = true;
    boolean averageTemplateMode = false;
    float leadNameYOffset = 0;//导联名称 y 偏移量
    int timeRulerHeight;
    int reportWaveOffsetLeft = 0;

    public BaseEcgPreviewTemplate() {

    }

    public void init(PreviewPageEnum previewPageEnum, float smallGridSpace, RecordSettingConfigEnum.RecordOrderType recordOrderType){
        this.previewPageEnum = previewPageEnum;
        this.recordOrderType = recordOrderType;

        gridSpace = smallGridSpace;
        largeGridSpace = gridSpace * 5;
        bgBitmap = Bitmap.createBitmap(drawWidth, drawHeight, Bitmap.Config.RGB_565);

        wavePaint = new Paint();
        wavePaint.setStyle(Paint.Style.STROKE);
        wavePaint.setAntiAlias(true);
        wavePaint.setFilterBitmap(true);

        leadStandardPaint = new Paint();
        leadStandardPaint.setStyle(Paint.Style.STROKE);

        fontPaint = new Paint();
        fontPaint.setStyle(Paint.Style.FILL);

        leadManager = new LeadManager(context);
        leadManager.setRangeLen(gridSpace);
        leadManager.setGridSpace(gridSpace);
        leadManager.setEcgMode(EcgShowModeEnum.MODE_SWEEP);
        leadManager.setSpeed(leadSpeedType);
        leadManager.setSensitivity(gainArray);

        float density = context.getResources().getDisplayMetrics().density;
        //0.25cm
        //standardLen = (int) ((0.25f / EcgConfig.IN_CM * 160 * density) + 0.5);

        canvasBg = new Canvas();
        canvasBg.setBitmap(bgBitmap);

        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            canvasBg.drawColor(EcgConfig.pdfBgColor);

            wavePaint.setColor(EcgConfig.pdfWaveColor);
            wavePaint.setStrokeWidth(BaseEcgReportTemplate.ONE_PIXEL);

            fontPaint.setTextSize(BaseEcgReportTemplate.TEXT_SIZE_NORMAL);
            fontPaint.setColor(EcgConfig.pdfWaveColor);

            leadStandardPaint.setColor(EcgConfig.pdfWaveColor);
            leadStandardPaint.setStrokeWidth(BaseEcgReportTemplate.ONE_PIXEL);

            reportWaveOffsetLeft = (int) (largeGridSpace * 3);
        } else {
            canvasBg.drawColor(EcgConfig.screenBgColor);

            wavePaint.setColor(EcgConfig.screenWaveColor);
            wavePaint.setStrokeWidth(2.0F);

            float textSize = context.getResources().getDimension(R.dimen.text_medium);
            fontPaint.setTextSize(textSize);
            fontPaint.setColor(EcgConfig.fontColorStandard);

            leadStandardPaint.setColor(EcgConfig.fontColorStandard);
            leadStandardPaint.setStrokeWidth(1.1F);
        }

        float leftSpacing = largeGridSpace;
        float nWidth = leftSpacing * (drawWidth / leftSpacing);
        float nHeight = leftSpacing * (drawHeight / leftSpacing);

        float left = (drawWidth - nWidth) / 2;
        float right = left + nWidth;
        float top = (drawHeight - nHeight) / 2;
        float bottom = top + nHeight;
        gridRect = new RectF(left, top, right, bottom);
        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            timeRulerHeight = 50;
            leadWidth = (50 * largeGridSpace) / leadColumes;
        } else if (previewPageEnum == PreviewPageEnum.PAGE_REAL_DRAW) {
            leadWidth = (gridRect.right - gridRect.left - largeGridSpace) / leadColumes;
        } else if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW || previewPageEnum == PreviewPageEnum.PAGE_FREEZE) {
            timeRulerHeight = 30;
            leadWidth = (gridRect.right - gridRect.left - largeGridSpace) / leadColumes;
        }
        leadHeight = (gridRect.bottom - timeRulerHeight - gridRect.top) / (leadLines);

        leadNameYOffset = largeGridSpace;
    }

    public void drawBase() {
        drawModeTip(canvasBg);
    }

    /**
     * 绘制时间标尺
     *
     * @param timeLen
     * @param speed
     */
    public void drawTimeRuler(float timeLen, float speed, float startTime,float dataLen) {
        if (timeRulerHeight == 0)
            return;

        if(previewPageEnum == PreviewPageEnum.PAGE_REPORT){

            if(leadSpeedType.ordinal() > EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25.ordinal()){
                //走速大于25时，需要特殊处理
                timeLen = timeLen * 25F / speed;
            }
        }

        float nTimeScaleBitmapW = timeLen * gridSpace * speed;
        float len = nTimeScaleBitmapW - 2;
        float segmentNum = (timeLen);
        float baseY = gridRect.bottom - timeRulerHeight;
        float baseX = gridRect.left + largeGridSpace + reportWaveOffsetLeft;
        float xOffset = 0f, yOffset = 0f, verticalLineHeight = 0;
        if (timeRulerPaint == null) {
            timeRulerPaint = new Paint();
            timeRulerPath = new Path();
            if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
                PathEffect effects = new DashPathEffect(new float[]{10, 10}, 10);
                timeRulerPaint.setPathEffect(effects);
                timeRulerPaint.setColor(Color.BLACK);
                timeRulerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                timeRulerPaint.setTextSize(25);
                timeRulerPaint.setStrokeWidth(4.0f);
            } else if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW || previewPageEnum == PreviewPageEnum.PAGE_FREEZE) {
                PathEffect effects = new DashPathEffect(new float[]{3, 3}, 0);
                timeRulerPaint.setPathEffect(effects);
                timeRulerPaint.setColor(Color.WHITE);
                timeRulerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                timeRulerPaint.setTextSize(15);
                timeRulerPaint.setStrokeWidth(2.0f);
                yOffset = 20;
            }
        }

        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            yOffset = 20;
            xOffset = 0;
            verticalLineHeight = 40;
        } else if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW || previewPageEnum == PreviewPageEnum.PAGE_FREEZE) {
            yOffset = 20;
            xOffset = 30;
            verticalLineHeight = 10;
        }
//        timeRulerPath.reset();
//        Bitmap timeScaleBitmap = Bitmap.createBitmap(nTimeScaleBitmapW, timeRulerHeight, Bitmap.Config.ALPHA_8);
//        Canvas canvas = new Canvas(timeScaleBitmap);
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Canvas canvas = canvasBg;
        float horizontalLineY = baseY + yOffset;
        timeRulerPath.moveTo(baseX + 1, horizontalLineY - verticalLineHeight / 2);
        timeRulerPath.lineTo(baseX + 1, horizontalLineY + verticalLineHeight / 2);
        for (int i = 1; i < segmentNum; i++) {
            timeRulerPath.moveTo(baseX + gridSpace * speed * i, horizontalLineY - verticalLineHeight / 2);
            timeRulerPath.lineTo(baseX + gridSpace * speed * i, horizontalLineY + verticalLineHeight / 2);
        }
//        timeRulerPath.moveTo(baseX + (len) * 1.0f, baseY+yOffset);
//        timeRulerPath.lineTo(baseX + (len) * 1.0f, baseY + timeRulerHeight);
        timeRulerPath.moveTo(baseX + len, horizontalLineY - verticalLineHeight / 2);
        timeRulerPath.lineTo(baseX + len, horizontalLineY + verticalLineHeight / 2);
        timeRulerPaint.setStyle(Paint.Style.STROKE);

//        canvas.drawPath(timeRulerPath, timeRulerPaint);
        timeRulerPath.moveTo(baseX + 1, horizontalLineY);
        timeRulerPath.lineTo(baseX + len, horizontalLineY);
        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT)
            timeRulerPaint.setStrokeWidth(2.0f);
        else if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW || previewPageEnum == PreviewPageEnum.PAGE_FREEZE)
            timeRulerPaint.setStrokeWidth(1.0f);
        canvas.drawPath(timeRulerPath, timeRulerPaint);

        timeRulerPaint.setStyle(Paint.Style.FILL);
        timeRulerPaint.setTextAlign(Paint.Align.LEFT);
        String formatStr = previewPageEnum == PreviewPageEnum.PAGE_REPORT ? "%.1f" : "%.2f";
        if((previewPageEnum == PreviewPageEnum.PAGE_FREEZE || previewPageEnum == PreviewPageEnum.PAGE_PREVIEW) && recordOrderType== RecordSettingConfigEnum.RecordOrderType.ORDER_INORDER){
            float unitTime = timeLen/leadColumes;
            startTime = startTime-unitTime*(leadColumes-1);
            if(startTime<0){
                startTime = 0;
                unitTime = dataLen/leadColumes/ Const.SAMPLE_RATE;
            }
            for(int i=0;i<=leadColumes-1;i++){
                canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime), baseX + nTimeScaleBitmapW / leadColumes * i + 2, baseY + yOffset - 5, timeRulerPaint);
                startTime+=unitTime;
            }
            startTime = startTime-unitTime+timeLen/leadColumes;
            timeRulerPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime), baseX + nTimeScaleBitmapW, baseY + yOffset - 5, timeRulerPaint);
            return;
        }
        canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), 0 + startTime), baseX + 3, horizontalLineY - 5, timeRulerPaint);
        if (previewPageEnum == PreviewPageEnum.PAGE_FREEZE||previewPageEnum== PreviewPageEnum.PAGE_PREVIEW) {
            for (int i = 1; i < leadColumes; i++) {
                canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime), baseX + nTimeScaleBitmapW / leadColumes * i + 2, horizontalLineY - 5, timeRulerPaint);
            }
            timeRulerPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime + timeLen / leadColumes), baseX + nTimeScaleBitmapW, horizontalLineY - 5, timeRulerPaint);

        }else {
            if (recordOrderType == RecordSettingConfigEnum.RecordOrderType.ORDER_INORDER || leadColumes == 1) {
                for (int i = 1; i <= leadColumes; i++) {
                    if (i == leadColumes)
                        timeRulerPaint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime + timeLen / leadColumes * i), baseX + nTimeScaleBitmapW / leadColumes * i + 2, horizontalLineY - 5, timeRulerPaint);
//                    canvas.drawText(String.format(formatStr + BaseApplication.getInstance().getString(R.string.print_time_second), startTime + timeLen), baseX + nTimeScaleBitmapW, horizontalLineY-5, timeRulerPaint);
                }

            } else {
                for (int i = 1; i < leadColumes; i++) {
                    canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime), baseX + nTimeScaleBitmapW / leadColumes * i + 2, horizontalLineY - 5, timeRulerPaint);
                }
                timeRulerPaint.setTextAlign(Paint.Align.RIGHT);
                if (timeLen % leadColumes == 0) {
                    canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime + timeLen / leadColumes), baseX + nTimeScaleBitmapW, horizontalLineY - 5, timeRulerPaint);
                } else {
                    canvas.drawText(String.format(formatStr + CustomTool.application.getString(R.string.print_time_second), startTime + timeLen * 1.0f / leadColumes), baseX + nTimeScaleBitmapW, horizontalLineY - 5, timeRulerPaint);
                }
            }
        }

//        canvasBg.drawBitmap(timeScaleBitmap,gridRect.left+largeGridSpace ,gridRect.bottom - timeRulerHeight ,paint);
    }

    /**
     * 绘制模式提示
     */
    public void drawModeTip(Canvas canvasCell) {

        //报告 提示文字 暂时不想在中间这个位置画。会挡住波形
        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            return;
        }

        if (averageTemplateMode) {
            return;
        }

        int colorId = 0;
        float textSize = 0;
        float x = 0;
        float y = 0;

        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            colorId = EcgConfig.pdfWaveColor;
            textSize = context.getResources().getDimension(R.dimen.main_mode_text_big) * 2;
            x = drawWidth / 2 - largeGridSpace * 7;
            y = drawHeight / 2 + largeGridSpace;
        } else {
            colorId = Color.parseColor("#899ba0");
            textSize = context.getResources().getDimension(R.dimen.main_mode_text_big);
            //x = drawWidth / 2 - largeGridSpace * 7;//11
            //y = drawHeight / 2 + largeGridSpace;

            x = 0;
            y = drawHeight / 2 - largeGridSpace * 3;
        }

        Paint modePaint = new Paint();
        modePaint.setColor(colorId);
        modePaint.setTextSize(textSize);

        String text = "";
        // TODO: 2021/5/12 mwj 默认使用 演示模式
        text = context.getString(R.string.main_text_ecg_mode_demo_mode);
//        if (LoginManager.getInstance().isEmergencyUseMode()) {
//            //紧急使用
//            text = context.getString(R.string.main_text_ecg_mode_busy_use);
//        } else if (SettingManager.getInstance().getConfigBeanTemp().getSystemSettingBean().getDemoMode()
//                != SystemSettingConfigEnum.DemoMode.DEMOMODE_CLOSE) {
//            //演示模式
//            text = context.getString(R.string.main_text_ecg_mode_demo_mode);
//        }
        //canvasCell.drawText(text,x,y,modePaint);

        canvasCell.save();
        canvasCell.translate(x, y);
        String contentResult = String.format("%s", text);
        StaticLayout staticLayoutResult = new StaticLayout(contentResult, new TextPaint(modePaint), drawWidth,
                Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        staticLayoutResult.draw(canvasCell);
        canvasCell.restore();
    }

    /**
     * 画背景表格
     */
    public void drawGridBg(Canvas canvas) {
        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT) {
            if (drawReportGridBg) {
                drawPdfGridBg(canvas);
            }
        } else {
            drawScreenGridBg(canvas);
        }
    }

    public void drawScreenGridBg(Canvas canvas){
        Paint paintWide = new Paint();
        paintWide.setPathEffect(new DashPathEffect(new float[]{1, 0}, 0));
        paintWide.setColor(EcgConfig.screenGridWideColor);

        Paint paintThin = new Paint();
        paintThin.setPathEffect(new DashPathEffect(new float[]{0.5F, 0.5F}, 0));
        paintThin.setColor(EcgConfig.screenGridThinColor);
        int i = 0;
        float drawIndex = 0;
        while (drawIndex<=drawWidth){
            if(i%5!=0){
                canvas.drawLine(drawIndex,0,drawIndex,drawHeight,paintThin);
            }
            i++;
            drawIndex = gridSpace*i;
        }
        i = 0;
        drawIndex = 0;
        while (drawIndex<=drawHeight){
            if(i%5!=0){
                canvas.drawLine(0,drawIndex,drawWidth,drawIndex,paintThin);
            }
            i++;
            drawIndex = gridSpace*i;
        }

        i = 0;
        drawIndex = 0;
        while (drawIndex<=drawWidth){
            canvas.drawLine(drawIndex,0,drawIndex,drawHeight,paintWide);
            i+=5;
            drawIndex = gridSpace*i;
        }

        i = 0;
        drawIndex = 0;
        while (drawIndex<drawHeight){
            canvas.drawLine(0,drawIndex,drawWidth,drawIndex,paintWide);
            i+=5;
            drawIndex = gridSpace*i;
        }
    }

    public void drawScreenGridBg2(Canvas canvas) {
        Paint paintWide = new Paint();
        paintWide.setPathEffect(new DashPathEffect(new float[]{1, 0}, 0));
        paintWide.setColor(EcgConfig.screenGridWideColor);

        Paint paintThin = new Paint();
        paintThin.setPathEffect(new DashPathEffect(new float[]{0.5F, 0.5F}, 0));
        paintThin.setColor(EcgConfig.screenGridThinColor);

        float i;
        for (i = gridRect.left; i <= gridRect.right; i += gridSpace) {
            canvas.drawLine(i, gridRect.top, i, gridRect.bottom, paintThin);
        }
        for (i = gridRect.top; i <= gridRect.bottom; i += gridSpace) {
            canvas.drawLine(gridRect.left, i, gridRect.right, i, paintThin);
        }

        int numGrid = 0;
        for (i = gridRect.left; i <= gridRect.right; i += largeGridSpace) {
            if (EcgConfig.LARGE_GRID_DIVIDER) {
                if (numGrid == 0 || numGrid % 5 == 0) {
                    paintWide.setColor(Color.RED);
                } else {
                    paintWide.setColor(EcgConfig.screenGridWideColor);
                }
            }
            canvas.drawLine(i, gridRect.top, i, gridRect.bottom, paintWide);
            numGrid++;
        }

        numGrid = 0;
        for (i = gridRect.top; i <= gridRect.bottom; i += largeGridSpace) {
            if (EcgConfig.LARGE_GRID_DIVIDER) {
                if (numGrid == 0 || numGrid % 5 == 0) {
                    paintWide.setColor(Color.RED);
                } else {
                    paintWide.setColor(EcgConfig.screenGridWideColor);
                }
            }
            canvas.drawLine(gridRect.left, i, gridRect.right, i, paintWide);
            numGrid++;
        }
    }

    public void drawPdfGridBg(Canvas canvas) {

        float smallGridPx = gridSpace;
        float largeGridPx = gridSpace * 5;

        Path path = new Path();
        path.addCircle(0, 0, 1, Path.Direction.CW);

        Paint paintSmall = new Paint();
        paintSmall.setColor(EcgConfig.pdfGridWideColor);
        paintSmall.setStrokeWidth(smallGridPx / 5);
        paintSmall.setStyle(Paint.Style.STROKE);


        int numGrid = 0;
        float i = 0;
        for (i = gridRect.left; i <= gridRect.right; i += smallGridPx) {
            if (numGrid == 0 || numGrid % 5 == 0) {
                paintSmall.setPathEffect(null);
            } else {
                paintSmall.setPathEffect(new PathDashPathEffect(path, 10, 0, PathDashPathEffect.Style.ROTATE));
            }
            canvas.drawLine(i, gridRect.top, i, gridRect.bottom, paintSmall);
            numGrid++;
        }

        numGrid = 0;
        for (i = gridRect.top; i <= gridRect.bottom; i += smallGridPx) {
            if (numGrid == 0 || numGrid % 5 == 0) {
                paintSmall.setPathEffect(null);
            } else {
                paintSmall.setPathEffect(new PathDashPathEffect(path, 10, 0, PathDashPathEffect.Style.ROTATE));
            }
            canvas.drawLine(gridRect.left, i, gridRect.right, i, paintSmall);
            numGrid++;
        }

        //
        Paint paintBig = new Paint();
        paintBig.setColor(EcgConfig.pdfGridWideColor);
        paintBig.setStrokeWidth(1);
        paintBig.setStyle(Paint.Style.STROKE);

        numGrid = 0;
        for (i = gridRect.left; i <= gridRect.right; i += largeGridPx) {
            if (EcgConfig.LARGE_GRID_DIVIDER) {
                if (numGrid == 0 || numGrid % 5 == 0) {
                    paintBig.setColor(Color.RED);
                } else {
                    paintBig.setColor(EcgConfig.pdfGridWideColor);
                }
            }
            canvas.drawLine(i, gridRect.top, i, gridRect.bottom, paintBig);
            numGrid++;
        }

        numGrid = 0;
        for (i = gridRect.top; i <= gridRect.bottom; i += largeGridPx) {
            if (EcgConfig.LARGE_GRID_DIVIDER) {
                if (numGrid == 0 || numGrid % 5 == 0) {
                    paintBig.setColor(Color.RED);
                } else {
                    paintBig.setColor(EcgConfig.pdfGridWideColor);
                }
            }
            canvas.drawLine(gridRect.left, i, gridRect.right, i, paintBig);
            numGrid++;
        }
    }

    /**
     * 画预览背景表格
     */
    public void drawPerviewGridBg(Canvas canvas) {
        if (gridCellBitmap == null) {
            gridCellBitmap = Bitmap.createBitmap(drawWidth, drawHeight, Bitmap.Config.RGB_565);
            Canvas canvasCell = new Canvas(gridCellBitmap);
            canvasCell.drawColor(EcgConfig.screenBgColor);
            drawScreenGridBg(canvasCell);
            drawModeTip(canvasCell);
        }
        canvas.drawBitmap(gridCellBitmap, 0, 0, new Paint());
    }

    /**
     * 画导联定标符号
     *
     * @param canvas
     * @param paint
     * @param x
     * @param y
     * @param chestLead
     * @param drawVerticalBar
     * @param rthLead
     * @param mulColume
     * @param needScaleMove
     */
    public void drawLeadStandard(Canvas canvas, Paint paint, float x, float y,
                                 boolean chestLead, boolean drawVerticalBar, boolean rthLead, boolean mulColume, boolean needScaleMove, int leadLines) {
        //20
        float[] af = new float[40];
        //mStandardLen的4/5，在平分4份
        float xDistance = largeGridSpace * 4 / 5 / 4;

        float sensitivity = 1.0F;
        float sensitivityBody = gainArray[0];
        float sensitivityChest = gainArray[1];
        float minusHeight = gridSpace * 10;

        if (chestLead) {
            //v1-v6
            sensitivity *= sensitivityChest;
        } else {
            //I II ..
            sensitivity *= sensitivityBody;
        }

        //增益高了，定标符号会显示不全。特殊处理，往下拉
        y = dowithY(needScaleMove, leadLines, sensitivity, y);

        if (drawVerticalBar) {
            y += gridSpace;
            minusHeight *= sensitivity;

            af[0] = x - xDistance * 2;
            af[1] = y;
            af[2] = x;
            af[3] = y;

            af[4] = x - xDistance;
            af[5] = y;
            af[6] = x - xDistance;
            af[7] = y - minusHeight;

            af[8] = x - xDistance * 2;
            af[9] = y - minusHeight;
            af[10] = x;
            af[11] = y - minusHeight;
        } else {
            boolean sensitivitySame = sensitivityBody == sensitivityChest;
            if (rthLead || !mulColume || (mulColume && sensitivitySame)) {
                minusHeight *= sensitivity;

                //节律导联 || 竖排单列 || (多列 并且 增益相同)。左排用1个定标符号
                af[0] = x - xDistance * 2;
                af[1] = y;
                af[2] = x - xDistance;
                af[3] = y;

                af[4] = x - xDistance;
                af[5] = y;
                af[6] = x - xDistance;
                af[7] = y - minusHeight;

                af[8] = x - xDistance;
                af[9] = y - minusHeight;
                af[10] = x + xDistance;
                af[11] = y - minusHeight;

                af[12] = x + xDistance;
                af[13] = y - minusHeight;
                af[14] = x + xDistance;
                af[15] = y;

                af[16] = x + xDistance;
                af[17] = y;
                af[18] = x + xDistance * 2;
                af[19] = y;
            } else {
                //左排用2个定标符号
                //1定标 ======================================
                x -= gridSpace;

                af[0] = x - xDistance;
                af[1] = y;
                af[2] = x - xDistance / 2;
                af[3] = y;

                af[4] = x - xDistance / 2;
                af[5] = y;
                af[6] = x - xDistance / 2;
                af[7] = y - minusHeight * sensitivityBody;

                af[8] = x - xDistance / 2;
                af[9] = y - minusHeight * sensitivityBody;
                af[10] = x + xDistance / 2;
                af[11] = y - minusHeight * sensitivityBody;

                af[12] = x + xDistance / 2;
                af[13] = y - minusHeight * sensitivityBody;
                af[14] = x + xDistance / 2;
                af[15] = y;

                af[16] = x + xDistance / 2;
                af[17] = y;
                af[18] = x + xDistance;
                af[19] = y;

                //2定标 ==================================
                af[20] = x + xDistance / 2;
                af[21] = y;
                af[22] = x + xDistance;
                af[23] = y;

                af[24] = x + xDistance;
                af[25] = y;
                af[26] = x + xDistance;
                af[27] = y - minusHeight * sensitivityChest;

                af[28] = x + xDistance;
                af[29] = y - minusHeight * sensitivityChest;
                af[30] = x + xDistance * 2;
                af[31] = y - minusHeight * sensitivityChest;

                af[32] = x + xDistance * 2;
                af[33] = y - minusHeight * sensitivityChest;
                af[34] = x + xDistance * 2;
                af[35] = y;

                af[36] = x + xDistance * 2;
                af[37] = y;
                af[38] = x + xDistance * 2.5f;
                af[39] = y;
            }
        }

        canvas.drawLines(af, paint);
    }

    public Bitmap getBgBitmap() {
        return bgBitmap;
    }

    public void setBgBitmap(Bitmap bgBitmap) {
        this.bgBitmap = bgBitmap;
    }

    public LeadManager getLeadManager() {
        return leadManager;
    }

    public void setLeadManager(LeadManager leadManager) {
        this.leadManager = leadManager;
    }

    public Paint getWavePaint() {
        return wavePaint;
    }

    public void setWavePaint(Paint wavePaint) {
        this.wavePaint = wavePaint;
    }

    /**
     * 实时画图
     *
     * @param canvas
     */
    public void drawEcgPathRealTime(Canvas canvas) {
        if (leadManager == null) {
            return;
        }
        leadManager.calcPath();
        leadManager.drawEcgPath(canvas, wavePaint);
    }

    /**
     * 预览画图
     */
    public void drawEcgPathPreview() {
        if (leadManager == null) {
            return;
        }
        drawPerviewGridBg(canvasBg);
        leadManager.calcPath();
        leadManager.drawEcgPath(canvasBg, wavePaint);
    }

    /**
     * 报告画图
     */
    public void drawEcgReport() {
        if (leadManager == null) {
            return;
        }
        leadManager.calcPath();
        leadManager.drawEcgPath(canvasBg, wavePaint);
    }

    /**
     * 清理心电数据
     * 导联list 不清理
     */
    public void clearData() {
        if (leadManager == null) {
            return;
        }

        leadManager.clearEcgData();
    }

    /**
     * 设置增益
     *
     * @param gainArray
     */
    public void setSensitivity(float[] gainArray) {
        this.gainArray = gainArray;
        leadManager.setSensitivity(gainArray);
    }

    /**
     * 设置走速
     *
     * @param leadSpeedType
     */
    public void setSpeed(EcgSettingConfigEnum.LeadSpeedType leadSpeedType) {
        leadManager.setSpeed(leadSpeedType);
    }

    /**
     * 设置ecg显示模式
     *
     * @param ecgShowModeEnum
     */
    public void setEcgMode(EcgShowModeEnum ecgShowModeEnum) {
        leadManager.setEcgMode(ecgShowModeEnum);
    }

    public int getLeadLines() {
        return leadLines;
    }

    public void setLeadLines(int leadLines) {
        this.leadLines = leadLines;
    }

    public void updateLeadNameColorHighlight() {
        fontPaint.setColor(EcgConfig.screenWaveColor);
    }

    public void updateLeadNameColorNormal() {
        fontPaint.setColor(EcgConfig.screenWaveColor);
    }

    public ScaleBean getScaleBean() {
        return scaleBean;
    }

    public void setScaleBean(ScaleBean scaleBean) {
        this.scaleBean = scaleBean;
    }

    public int getLeadColumes() {
        return leadColumes;
    }

    public boolean isDrawReportGridBg() {
        return drawReportGridBg;
    }

    public void setDrawReportGridBg(boolean drawReportGridBg) {
        this.drawReportGridBg = drawReportGridBg;
    }

    public boolean isAverageTemplateMode() {
        return averageTemplateMode;
    }

    public void setAverageTemplateMode(boolean averageTemplateMode) {
        this.averageTemplateMode = averageTemplateMode;
    }

    public List<String> getLeadNameList() {
        return leadNameList;
    }

    public void setLeadNameList(List<String> leadNameList) {
        this.leadNameList = leadNameList;
    }

    public void updateLeadHeight(int colume) {
        leadHeight = (gridRect.bottom - gridRect.top) / (colume);
    }

    public void updateFontPaintColor(int num, boolean rth) {
        if (previewPageEnum == PreviewPageEnum.PAGE_REAL_DRAW) {
            // TODO: 2021/5/12 mwj 为了避免使用MainEcg直接固定fontPaint的颜色
            fontPaint.setColor(EcgConfig.fontColorHighlight);
//            Map<Integer, Integer> manualPrintIndexMap = MainEcgManager.getInstance().getManualPrintIndexMap();
//            boolean flag = manualPrintIndexMap.containsKey(num);
//            if (flag) {
//                fontPaint.setColor(EcgConfig.fontColorHighlight);
//            } else {
//                if (rth && manualPrintIndexMap.size() > 0) {
//                    fontPaint.setColor(EcgConfig.fontColorHighlight);
//                } else {
//                    fontPaint.setColor(EcgConfig.fontColorStandard);
//                }
//            }

        }
    }

    private float dowithY(boolean needScaleMove, int leadLines, float sensitivity, float y) {
        float newY = 0;
        if (needScaleMove) {
            if (leadLines > 12) {
                //1.0增益也需要处理
                if (sensitivity == 1.0) {
                    y += largeGridSpace * 1;
                } else if (sensitivity == 2.0) {
                    y += largeGridSpace * 3;
                } else if (sensitivity == 4.0) {
                    y += largeGridSpace * 20;
                }
            } else {
                //只处理2.0  ；  4.0增益
                if (sensitivity == 2.0) {
                    if (leadLines >= 6) {
                        y += largeGridSpace * 2;
                    } else {
                        y += largeGridSpace * 1;
                    }
                } else if (sensitivity == 4.0) {
                    if (leadLines >= 6) {
                        y += largeGridSpace * 6;
                    } else {
                        y += largeGridSpace * 5;
                    }
                }
            }
            newY = y;
        }

        if (newY == 0) {
            return y;
        } else {
            return newY;
        }
    }
}