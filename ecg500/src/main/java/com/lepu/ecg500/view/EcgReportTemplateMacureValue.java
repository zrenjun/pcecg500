package com.lepu.ecg500.view;

import android.content.Context;
import com.lepu.ecg500.R;
import com.lepu.ecg500.entity.AiResultMeasuredValueBean;
import com.lepu.ecg500.entity.MacureResultBean;
import com.lepu.ecg500.entity.PatientInfoBean;
import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.ReportSettingBean;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;
import java.util.LinkedList;
import java.util.List;

public class EcgReportTemplateMacureValue extends BaseEcgReportTemplate{

    public EcgReportTemplateMacureValue(Context context, boolean isVertical, boolean drawGridBg, ConfigBean configBean){
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
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        int left = rect.left;
        int right = rect.right;
        int top = currentBottomPosition;
        int bottom = rect.bottom - bottomInfoHeight;

        currentBottomPosition = top + patientInfoHeight + smartGrid + ONE_PIXEL;

        int gridWidth = (right - left);
        int gridHeight = (bottom - top) + smartGrid*3;

        //================测量值 ====================
        List<ReportSettingBean> reportSettingBeanList = configBean.getRecordSettingBean().getReportSettingBeanList();
        boolean macureValueFlag = reportSettingBeanList.get(RecordSettingConfigEnum.ReportSetting.MEASURE_MATRIX.ordinal()).isValue();
        if(macureValueFlag){
            if(macureResultBean != null && macureResultBean.getAiResultBean() != null){
                String[] macureValueArray = context.getResources().getStringArray(R.array.array_test_macure_value);
                // TODO: 2021/5/12 mwj 抽取MainEcgManager的方法
                //LinkedList<String> leadNameList = MainEcgManager.getInstance().getLeadNameArrayCurrentLeadModeList(context,configBean.getEcgSettingBean().getLeadType(),false);
                LinkedList<String> leadNameList = CustomTool.getLeadNameArrayCurrentLeadModeList(context,configBean.getEcgSettingBean().getLeadType(),false);

                List<AiResultMeasuredValueBean> aiResultMeasuredValueBeanList = macureResultBean.getAiResultBean().getMeasuredValueList();

                if(aiResultMeasuredValueBeanList == null || aiResultMeasuredValueBeanList.size() == 0){
                    return;
                }

                int columes = leadNameList.size() + 1;
                int lines = macureValueArray.length;

                //draw
                int macureValueFirstColumeWidth = largeGrid * 6;
                int topContent;
                int leftContent = left + smartGrid;
                int perRectWidth = (gridWidth-macureValueFirstColumeWidth) / (columes-1);
                int perRectHeight = (gridHeight / macureValueArray.length);


                int rectTopMargin = textHeight;

                for(int i=0;i<columes;i++){
                    for(int j=0;j<macureValueArray.length;j++){
                        topContent = top + perRectHeight * (j+1) - rectTopMargin;

                        if(i == 0){
                            //竖排第一列，画测量值名称
                            ecgCanvas.drawText(macureValueArray[j],leftContent, topContent, ptText);
                        }else{
                            //画导联测量值
                            if(j == 0){
                                //画导联名称
                                ecgCanvas.drawText(leadNameList.get(i-1),leftContent, topContent, ptText);
                            }else{
                                //画测量值
                                AiResultMeasuredValueBean aiResultMeasuredValueBean = aiResultMeasuredValueBeanList.get(i-1);
                                switch (j){
                                    case 1:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getPa1()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 2:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getQa()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 3:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getRa1()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 4:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getSa1()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 5:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getTa1()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 6:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getST1()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 7:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getSTj()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 8:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getST20()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 9:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getST40()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 10:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getST60()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 11:
                                        ecgCanvas.drawText(String.format("%.03f",aiResultMeasuredValueBean.getST80()/1000F),leftContent, topContent, ptText);
                                        break;
                                    case 12:
                                        ecgCanvas.drawText(String.format("%d",aiResultMeasuredValueBean.getQd()),leftContent, topContent, ptText);
                                        break;
                                    case 13:
                                        ecgCanvas.drawText(String.format("%d",aiResultMeasuredValueBean.getRd1()),leftContent, topContent, ptText);
                                        break;
                                    case 14:
                                        ecgCanvas.drawText(String.format("%d",aiResultMeasuredValueBean.getSd1()),leftContent, topContent, ptText);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }

                    if(i == 0){
                        //第一列是标题，文字长
                        leftContent += macureValueFirstColumeWidth;
                    }else{
                        leftContent += perRectWidth;
                    }

                    //画竖线 最后1列不画线
                    if(i != columes-1){
                        ecgCanvas.drawLine(leftContent-smartGrid,top, leftContent-smartGrid+ONE_PIXEL,bottom, ptLine);
                    }
                }

                //画横线
                int topLineMargin;
                for(int j=0;j<macureValueArray.length;j++){
                    topLineMargin = top + perRectHeight*(j+1) + ONE_PIXEL;
                    ecgCanvas.drawLine(left,topLineMargin, right,topLineMargin+ONE_PIXEL, ptLine);
                }

                //画最底部横线
                ecgCanvas.drawLine(left,bottom-ONE_PIXEL, right,bottom, ptLine);
            }
        }
    }

    @Override
    public void drawEcgImage(float[] gainArray,short[][] ecgDataArray,boolean averageTemplate,List<String> valueList) {

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
