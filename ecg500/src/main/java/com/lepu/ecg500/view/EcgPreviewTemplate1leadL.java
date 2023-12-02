package com.lepu.ecg500.view;

import android.content.Context;
import android.graphics.RectF;

import com.lepu.ecg500.entity.EcgSettingConfigEnum;

import java.util.List;

public class EcgPreviewTemplate1leadL extends BaseEcgPreviewTemplate {

    public EcgPreviewTemplate1leadL(Context context, int width, int height,
                                    boolean isDrawGrid, List<String> leadNameList,
                                    float[] gainArray, EcgSettingConfigEnum.LeadSpeedType leadSpeedType) {
        this.context = context;
        this.drawWidth = width;
        this.drawHeight = height;
        this.gainArray = gainArray;
        this.leadSpeedType = leadSpeedType;
        this.leadLines = 1;
        this.leadColumes = 1;
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
        int leadNum = 1;
        short value = 0;
        //是否是胸导联
        boolean chestLead = false;
        int dataLen = dataArray[0].length;

        //定标数据
        if (scaleBean != null) {
            dataLen = scaleBean.getDataArray().length;
        }

        for (int i = 0; i < leadNum; i++) {//单导联L只处理第一导联的数据
            for (int j = 0; j < dataLen; j++) {
                if (scaleBean != null) {
                    value = scaleBean.getDataArray()[j];
                } else {
                    value = dataArray[i][j];
                }

                if (i > 5) {
                    chestLead = true;
                } else {
                    chestLead = false;
                }
                leadManager.getleadList().get(i).addFilterPoint(value, chestLead);
            }
        }

        scaleBean = null;
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
        float left = gridRect.left + largeGridSpace;
        float right = (gridRect.left + largeGridSpace + leadWidth);
        float top = (gridRect.top + topMargin);

        leadManager.addLead(leadManager.new Lead(new RectF(left, top, right, top += (leadHeight))));
    }

    /**
     * 绘制导联信息 定标，名称
     */
    public void drawLeadInfo() {
        int num = 0;
        boolean needScaleMove;

        for (float i = (gridRect.top + leadHeight / 2 + largeGridSpace); i < gridRect.bottom; i += leadHeight / 2) {
            needScaleMove = num <= 1;
            if (num < leadLines) {
                drawLeadStandard(canvasBg, leadStandardPaint, gridRect.left + gridSpace * 3, i,
                        num > 5, false, false, false, needScaleMove, leadLines);
            }

            updateFontPaintColor(num, false);
            canvasBg.drawText(leadNameList.get(0), gridRect.left + largeGridSpace, i - leadNameYOffset, fontPaint);
            num++;
            break;
        }
    }
}
