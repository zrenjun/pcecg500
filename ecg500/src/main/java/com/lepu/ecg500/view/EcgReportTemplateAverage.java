package com.lepu.ecg500.view;

import android.content.Context;

import com.lepu.ecg500.entity.MacureResultBean;
import com.lepu.ecg500.entity.PatientInfoBean;
import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.entity.ConfigBean;

import java.util.List;

public class EcgReportTemplateAverage extends BaseEcgReportTemplate {

    public EcgReportTemplateAverage(Context context, boolean isVertical, boolean drawGridBg, ConfigBean configBean){
        super(context,isVertical,drawGridBg,configBean);

        patientInfoHeight = textHeight * 2 + smartGrid * 2;
        bottomInfoHeight = textHeight * 2 + smartGrid;

        //画四周表格
        drawRoundTable();
    }

    @Override
    public void drawTitleInfo(String titleInfo) {
        drawTitleInfoBase(titleInfo);
    }

    @Override
    public void drawPatientInfoTop(PatientInfoBean patientInfoBean) {
        ptText.setFakeBoldText(false);
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        //画患者信息
        drawPatientInfoTopLandscapeSimple(patientInfoBean);
    }

    @Override
    public void drawMacureResult(MacureResultBean macureResultBean) {

    }

    @Override
    public void drawEcgImage(float[] gainArray,short[][] ecgDataArray,boolean averageTemplate,List<String> valueList) {
        int left = rect.left + smartGrid;
        int right = rect.right - smartGrid;
        int top = currentBottomPosition + ONE_PIXEL;
        int bottom = rect.bottom - bottomInfoHeight;
        int gridWidth = (right - left);
        int gridHeight = (bottom - top) + smartGrid*3;

        BaseEcgPreviewTemplate baseEcgPreviewTemplate = CustomTool.getBaseEcgPreviewTemplate(context, PreviewPageEnum.PAGE_REPORT,smartGrid,gridWidth,gridHeight,
                configBean, gainArray,drawGridBg,false);

        baseEcgPreviewTemplate.setAverageTemplateMode(averageTemplate);
        baseEcgPreviewTemplate.setEcgMode(EcgShowModeEnum.MODE_SCROLL);
        baseEcgPreviewTemplate.initParams();
        if(ecgDataArray != null){
            baseEcgPreviewTemplate.addEcgData(ecgDataArray);
        }
        baseEcgPreviewTemplate.drawEcgReport();
        ecgCanvas.drawBitmap(baseEcgPreviewTemplate.getBgBitmap(),left,top,ptLine);
    }

    @Override
    public void drawBottomEcgParamInfo(String ecgParamInfo) {
        drawBottomEcgParamInfoBase(ecgParamInfo);
    }

    @Override
    public void drawBottomOtherInfo(String infoTip,String infoPage){
        drawBottomOtherInfoBase(infoTip,infoPage);
    }
}
