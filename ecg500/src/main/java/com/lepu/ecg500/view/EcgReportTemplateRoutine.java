package com.lepu.ecg500.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import com.lepu.ecg500.entity.AiResultBean;
import com.lepu.ecg500.entity.MacureResultBean;
import com.lepu.ecg500.entity.PatientInfoBean;
import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.util.BitmapUtil;
import com.lepu.ecg500.util.Const;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.ReportSettingBean;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;
import com.lepu.ecg500.util.EcgDataManager;
import java.util.List;
import com.lepu.ecg500.R;
public class EcgReportTemplateRoutine extends BaseEcgReportTemplate {

    //显示的药物试验的文字
    private String drugTestTimePointText;
    private boolean ifHaveReportRth;

    public EcgReportTemplateRoutine(Context context, boolean isVertical, boolean drawGridBg, ConfigBean configBean, boolean ifHaveReportRth) {
        super(context, isVertical, drawGridBg, configBean);

        patientInfoHeight = textHeight * 10;
        bottomInfoHeight = textHeight * 2 + smartGrid;

        this.ifHaveReportRth = ifHaveReportRth;
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
        drawPatientInfoTopLandscapeMulColume(patientInfoBean);
    }

    @Override
    public void drawMacureResult(MacureResultBean macureResultBean) {
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        int left = rect.left;
        int right = rect.right;
        int top = currentBottomPosition + smartGrid;
        int bottom = top + patientInfoHeight + ONE_PIXEL;

        currentBottomPosition = bottom;

        //画表格线
        int perRectWidth = (drawWidth - largeGrid) / 3;
        int macureValueDividerX = perRectWidth;
        int macureResultDividerX = perRectWidth * 2;
        //middle line
        ecgCanvas.drawLine(macureValueDividerX, top, macureValueDividerX + ONE_PIXEL, bottom, ptLine);
        ecgCanvas.drawLine(macureResultDividerX, top, macureResultDividerX + ONE_PIXEL, bottom, ptLine);
        //top line
        ecgCanvas.drawLine(left, top - ONE_PIXEL, right, top, ptLine);
        //bottom line
        ecgCanvas.drawLine(left, bottom - ONE_PIXEL, right, bottom, ptLine);

        int marginX = perRectWidth + smartGrid;
        int topY = top;

        //================测量值 ====================
        List<ReportSettingBean> reportSettingBeanList = configBean.getRecordSettingBean().getReportSettingBeanList();
        boolean macureValueFlag = reportSettingBeanList.get(RecordSettingConfigEnum.ReportSetting.MEASURE_RESULT.ordinal()).isValue();
        if (macureValueFlag) {

            boolean aiResultShow = true;
            AiResultBean aiResultBean = null;
            if (macureResultBean != null && macureResultBean.getAiResultBean() != null) {
                aiResultBean = macureResultBean.getAiResultBean();
                aiResultShow = true;
            }else{
                aiResultShow = false;
            }

            float marginValue = largeGrid * 3F;
            float marginUnit = largeGrid * 14F;

            ecgCanvas.drawText(context.getString(R.string.print_measure_hr), marginX, topY + largeGrid, ptText);
            ecgCanvas.drawText("P", marginX, topY + 2 * largeGrid, ptText);
            ecgCanvas.drawText("PR", marginX, topY + 3 * largeGrid, ptText);
            ecgCanvas.drawText("QRS", marginX, topY + 4 * largeGrid, ptText);
            ecgCanvas.drawText("QT/QTC", marginX, topY + 5 * largeGrid, ptText);
            ecgCanvas.drawText("P/QRS/T", marginX, topY + 6 * largeGrid, ptText);
            ecgCanvas.drawText("RV5/SV1", marginX, topY + 7 * largeGrid, ptText);
            ecgCanvas.drawText("RV5+SV1", marginX, topY + 8 * largeGrid, ptText);
            ecgCanvas.drawText("RV6/SV2", marginX, topY + 9 * largeGrid, ptText);

            if (aiResultShow) {
                boolean isQBQ = EcgDataManager.checkIfPacemaker(aiResultBean);
                if (isQBQ) {
                    ecgCanvas.drawText("       : --", (marginX + marginValue), topY + largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : --", context.getString(R.string.print_measure_time_limit)),
                            (marginX + marginValue), topY + 2 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : --", context.getString(R.string.print_measure_time_interval)),
                            (marginX + marginValue), topY + 3 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : --", context.getString(R.string.print_measure_time_limit)),
                            (marginX + marginValue), topY + 4 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : -- / --", context.getString(R.string.print_measure_time_limit)),
                            (marginX + marginValue), topY + 5 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : --/ --/ --", context.getString(R.string.print_measure_electric_axis)),
                            (marginX + marginValue), topY + 6 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : -- / --", context.getString(R.string.print_measure_amplitude)),
                            (marginX + marginValue), topY + 7 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : --", context.getString(R.string.print_measure_amplitude)),
                            (marginX + marginValue), topY + 8 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format("%s : -- / --", context.getString(R.string.print_measure_amplitude)),
                            (marginX + marginValue), topY + 9 * largeGrid, ptText);
                } else {
                    ecgCanvas.drawText(String.format("      : %d", aiResultBean.getHR()), (marginX + marginValue), topY + largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_time_limit) + " : %d", aiResultBean.getPd()),
                            (marginX + marginValue), topY + 2 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_time_interval) + " : %d", aiResultBean.getPR()),
                            (marginX + marginValue), topY + 3 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_time_limit) + " : %d", aiResultBean.getQRS()),
                            (marginX + marginValue), topY + 4 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_time_limit) + " : %d / %d", aiResultBean.getQT(), aiResultBean.getQTc()),
                            (marginX + marginValue), topY + 5 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_electric_axis) + " : %d/ %d/ %d", aiResultBean.getPAxis(), aiResultBean.getQRSAxis(), aiResultBean.getTAxis()),
                            (marginX + marginValue), topY + 6 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_amplitude) + " : %.3f / %.3f", aiResultBean.getRV5() / 1000f, aiResultBean.getSV1() / 1000f),
                            (marginX + marginValue), topY + 7 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_amplitude) + " : %.3f", aiResultBean.getRV5() / 1000f + aiResultBean.getSV1() / 1000f),
                            (marginX + marginValue), topY + 8 * largeGrid, ptText);
                    ecgCanvas.drawText(String.format(context.getString(R.string.print_measure_amplitude) + " : %.3f / %.3f", aiResultBean.getRV6() / 1000f, aiResultBean.getSV2() / 1000f),
                            (marginX + marginValue), topY + 9 * largeGrid, ptText);
                }
            } else {
                ecgCanvas.drawText("       : ", (marginX + marginValue), topY + largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_time_limit)),
                        (marginX + marginValue), topY + 2 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_time_interval)),
                        (marginX + marginValue), topY + 3 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_time_limit)),
                        (marginX + marginValue), topY + 4 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_time_limit)),
                        (marginX + marginValue), topY + 5 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_electric_axis)),
                        (marginX + marginValue), topY + 6 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_amplitude)),
                        (marginX + marginValue), topY + 7 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_amplitude)),
                        (marginX + marginValue), topY + 8 * largeGrid, ptText);
                ecgCanvas.drawText(String.format("%s : ", context.getString(R.string.print_measure_amplitude)),
                        (marginX + marginValue), topY + 9 * largeGrid, ptText);
            }
            ecgCanvas.drawText("bpm", (marginX + marginUnit), topY + largeGrid, ptText);
            ecgCanvas.drawText("ms", (marginX + marginUnit), topY + 2 * largeGrid, ptText);
            ecgCanvas.drawText("ms", (marginX + marginUnit), topY + 3 * largeGrid, ptText);
            ecgCanvas.drawText("ms", (marginX + marginUnit), topY + 4 * largeGrid, ptText);
            ecgCanvas.drawText("ms", (marginX + marginUnit), topY + 5 * largeGrid, ptText);
            ecgCanvas.drawText("°", (marginX + marginUnit), topY + 6 * largeGrid, ptText);
            ecgCanvas.drawText("mV", (marginX + marginUnit), topY + 7 * largeGrid, ptText);
            ecgCanvas.drawText("mV", (marginX + marginUnit), topY + 8 * largeGrid, ptText);
            ecgCanvas.drawText("mV", (marginX + marginUnit), topY + 9 * largeGrid, ptText);
        }

        //===============画 右边 诊断结果=============================
        marginX = (marginX + perRectWidth);

        //明尼苏达码
        boolean mingnisudaCodeFlag = reportSettingBeanList.get(RecordSettingConfigEnum.ReportSetting.MINNESOTA.ordinal()).isValue();
        if (mingnisudaCodeFlag) {
            StringBuffer codeSb = new StringBuffer();
            if (macureResultBean != null && macureResultBean.getAiResultBean() != null) {
                AiResultBean aiResultBean = macureResultBean.getAiResultBean();
                if (aiResultBean.getAiResultDiagnosisBean() != null) {
                    String[] codes = aiResultBean.getAiResultDiagnosisBean().getMinnesotacodes();
                    if (codes != null && codes.length > 0) {
                        for (int i = 0; i < codes.length; i++) {
                            codeSb.append(codes[i]).append("\t \t");
                        }
                    }
                }
            }

            ecgCanvas.save();
            ecgCanvas.translate(marginX, topY + smartGrid * 2);
            String contentCode = String.format("%s : %s", context.getString(R.string.print_minnesota_code), codeSb.toString());
            StaticLayout staticLayoutCode = new StaticLayout(contentCode, new TextPaint(ptText), (largeGrid * 19),
                    Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            staticLayoutCode.draw(ecgCanvas);
            ecgCanvas.restore();
        }

        //诊断结果.紧急使用，不显示诊断结果
        boolean diaResultFlag = reportSettingBeanList.get(RecordSettingConfigEnum.ReportSetting.DIAGNOSIS.ordinal()).isValue();
        if (diaResultFlag) {
            StringBuilder diaResultSb = new StringBuilder();
            if (macureResultBean != null && macureResultBean.getAiResultBean() != null) {
                AiResultBean aiResultBean = macureResultBean.getAiResultBean();
                List<String> diagnosisResult = EcgDataManager.getInstance().getDiagnosisResult(aiResultBean);
                if (diagnosisResult != null && diagnosisResult.size() > 0) {
                    for (int i = 0; i < diagnosisResult.size(); i++) {
                        diaResultSb.append(String.format("%d . ", i + 1));
                        diaResultSb.append(diagnosisResult.get(i)).append("\t \t ");
                    }
                }
            }

            ecgCanvas.save();
            ecgCanvas.translate(marginX, topY + largeGrid * 2);
            String contentResult = String.format("%s : %s", context.getString(R.string.print_analysis_result), diaResultSb.toString());
            StaticLayout staticLayoutResult = new StaticLayout(contentResult, new TextPaint(ptText), (largeGrid * 19),
                    Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
            staticLayoutResult.draw(ecgCanvas);
            ecgCanvas.restore();
        }

        //医生确认
        ecgCanvas.drawText(String.format("%s : %s", context.getString(R.string.print_report_to_confirm), ""),
                (marginX), topY + patientInfoHeight - smartGrid * 2, ptText);
        if (macureResultBean != null && macureResultBean.getAiResultBean() != null && macureResultBean.getAiResultBean().getSignPicture() != null) {
            float textWidth = ptText.measureText(String.format("%s : %s", context.getString(R.string.print_report_to_confirm), ""));
            Bitmap sign = BitmapUtil.bytesToBimap(macureResultBean.getAiResultBean().getSignPicture());
            Bitmap scaleBitmap = BitmapUtil.scaleTo(sign, 200, 100);
            if (scaleBitmap != null){
                ecgCanvas.drawBitmap(scaleBitmap, marginX + textWidth + 20, bottom-105, ptText);
                sign.recycle();
            }
        }
    }


    @Override
    public void drawEcgImage(float[] gainArray, short[][] ecgDataArray, boolean averageTemplate, List<String> valueList) {
        int left = rect.left + smartGrid;
        int right = rect.right - smartGrid;
        int top = currentBottomPosition + ONE_PIXEL;
        int bottom = rect.bottom - bottomInfoHeight;
        int gridWidth = (right - left);
        int gridHeight = (bottom - top) + smartGrid * 3;
        BaseEcgPreviewTemplate baseEcgPreviewTemplate = CustomTool.getBaseEcgPreviewTemplate(context, PreviewPageEnum.PAGE_REPORT, smartGrid, gridWidth, gridHeight,
                configBean, gainArray, drawGridBg,ifHaveReportRth);
        
        baseEcgPreviewTemplate.initParams();
        baseEcgPreviewTemplate.setEcgMode(EcgShowModeEnum.MODE_SCROLL);
        baseEcgPreviewTemplate.addEcgData(ecgDataArray);
        baseEcgPreviewTemplate.drawEcgReport();
        baseEcgPreviewTemplate.drawTimeRuler(ecgDataArray[0].length / Const.SAMPLE_RATE, (Float) configBean.getEcgSettingBean().getLeadSpeedType().getValue(), 0,ecgDataArray[0].length);

        ecgCanvas.drawBitmap(baseEcgPreviewTemplate.getBgBitmap(), left, top, ptLine);


        //
        if (!TextUtils.isEmpty(drugTestTimePointText)) {
            ecgCanvas.drawText(drugTestTimePointText, left + largeGrid + smartGrid * 2, top + textHeight, ptText);
        }
    }

    @Override
    public void drawBottomEcgParamInfo(String ecgParamInfo) {
        drawBottomEcgParamInfoBase(ecgParamInfo);
    }

    @Override
    public void drawBottomOtherInfo(String infoTip, String infoPage) {
        drawBottomOtherInfoBase(infoTip, infoPage);
    }

    public void setDrugTestTimePointText(String drugTestTimePointText) {
        this.drugTestTimePointText = drugTestTimePointText;
    }
}
