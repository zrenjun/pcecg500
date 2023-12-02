package com.lepu.ecg500.view;

import android.content.Context;
import android.graphics.RectF;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;

import java.util.List;

public class EcgPreviewTemplate12Lead12X1 extends BaseEcgPreviewTemplate {

    public EcgPreviewTemplate12Lead12X1(Context context, int width, int height,
                                        boolean isDrawGrid, List<String> leadNameList,
                                        float[] gainArray, EcgSettingConfigEnum.LeadSpeedType leadSpeedType) {
        this.context = context;
        this.drawWidth = width;
        this.drawHeight = height;
        this.gainArray = gainArray;
        this.leadSpeedType = leadSpeedType;
        this.leadLines = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_12_12_1.getOriginalLeadLines();
        this.leadColumes = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_12_12_1.getOriginalLeadColumns();
        this.leadNameList = leadNameList;
        this.drawReportGridBg = isDrawGrid;
    }

    /**
     * 添加心电数据
     *
     * @param dataArray
     */
    @Override
    public void addEcgData(short[][] dataArray) {
        int leadNum = dataArray.length;
        short value = 0;
        //是否是胸导联
        boolean chestLead = false;
        int dataLen = dataArray[0].length;


        if (previewPageEnum == PreviewPageEnum.PAGE_REPORT && leadSpeedType.ordinal() > EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25.ordinal()) {
            //走速大于25时，需要特殊处理
            float speed = (float) leadSpeedType.getValue();
            dataLen = (int) (dataLen * 25F / speed);
        }

        for (int i = 0; i < leadNum; i++) {
            for (int j = 0; j < dataLen; j++) {
                value = dataArray[i][j];

                if (i > 5) {
                    chestLead = true;
                } else {
                    chestLead = false;
                }
                leadManager.getleadList().get(i).addFilterPoint(value, chestLead);
            }
        }
    }

    /**
     * 初始化画布
     */
    @Override
    public void initParams() {
        initDrawWave();
        drawGridBg(canvasBg);
        drawBase();
        drawLeadInfo();
    }

    /**
     * 初始化导联布局
     */
    private void initDrawWave() {

        float topMargin = largeGridSpace;
        float left = gridRect.left + largeGridSpace + reportWaveOffsetLeft;
        float right = gridRect.right;
        float top = (gridRect.top + topMargin);

        for (int i = 0; i < leadLines; i++) {
            leadManager.addLead(leadManager.new Lead(new RectF(left, top, right, top += (leadHeight))));
        }
    }

    /**
     * 绘制导联信息 定标，名称
     */
    public void drawLeadInfo() {

        int num = 0;
        boolean needScaleMove;

        for (int i = (int) (gridRect.top + leadHeight); i <= gridRect.bottom - timeRulerHeight; i += leadHeight) {
            needScaleMove = num <= 3;
            if (num < leadLines) {
                drawLeadStandard(canvasBg, leadStandardPaint, gridRect.left + gridSpace * 3 + reportWaveOffsetLeft, i,
                        num > 5, false, false, false, needScaleMove, leadLines);
            }

            updateFontPaintColor(num, false);
            canvasBg.drawText(leadNameList.get(num), gridRect.left + largeGridSpace + reportWaveOffsetLeft, i - leadNameYOffset, fontPaint);
            num++;
        }
    }
}
