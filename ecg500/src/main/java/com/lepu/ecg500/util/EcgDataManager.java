package com.lepu.ecg500.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.lepu.ecg500.R;
import com.lepu.ecg500.entity.AiResultBean;
import com.lepu.ecg500.entity.AiResultDiagnosisBean;
import com.lepu.ecg500.entity.EcgDataInfoBean;
import com.lepu.ecg500.entity.EcgMacureResultEnum;
import com.lepu.ecg500.entity.MacureResultBean;
import com.lepu.ecg500.entity.MinnesotaCodeBean;
import com.lepu.ecg500.entity.MinnesotaCodeItemBean;
import com.lepu.ecg500.entity.PatientInfoBean;
import com.lepu.ecg500.view.EcgReportTemplateMacureValue;
import com.lepu.ecg500.view.EcgReportTemplateRoutine;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 心电数据管理
 */
public class EcgDataManager {
    private static EcgDataManager instance = null;

    private Map<String, String> minnesotaCodeMap = new LinkedHashMap<>();
    private Map<String, String> warnCodeMaps = new HashMap<>();

    private EcgDataManager() {

    }

    public static EcgDataManager getInstance() {
        if (instance == null) {
            instance = new EcgDataManager();
        }
        return instance;
    }

    /**
     * 检查是否是起搏器
     *
     * @param aiResultBean
     * @return
     */
    public static boolean checkIfPacemaker(AiResultBean aiResultBean) {
        boolean pacemaker = false;
        AiResultDiagnosisBean aiResultDiagnosisBean = aiResultBean.getAiResultDiagnosisBean();
        if (aiResultDiagnosisBean != null) {
            List<MinnesotaCodeItemBean> minnesotaCodeItemBeanList = aiResultDiagnosisBean.getDiagnosis();
            if (minnesotaCodeItemBeanList != null && minnesotaCodeItemBeanList.size() > 0) {
                for (MinnesotaCodeItemBean item : minnesotaCodeItemBeanList) {
                    if (!TextUtils.isEmpty(item.getCode())) {
                        if ("421".equals(item.getCode())) {
                            pacemaker = true;
                            break;
                        }
                    }
                }
            }
        }

        return pacemaker;
    }

    public void init(Context context) {
        updateMinnesotaCodeMap(context);
        initWarnCodesDefault();
    }

    public void destroy() {
        instance = null;
    }

    //======================================
    public void updateMinnesotaCodeMap(Context context) {
        if (minnesotaCodeMap != null) {
            minnesotaCodeMap.clear();
        }

        LinkedList<MinnesotaCodeBean> codeList = getMinnesotaCodeList();
        if (codeList != null && codeList.size() > 0) {
            for (MinnesotaCodeBean codeBean : codeList) {
                List<MinnesotaCodeItemBean> list = codeBean.getDataList();
                for (MinnesotaCodeItemBean item : list) {
                    minnesotaCodeMap.put(item.getCode(), item.getTitle());
                }
            }
        }
    }

    public LinkedList<MinnesotaCodeBean> getMinnesotaCodeList() {
        LinkedList<MinnesotaCodeBean> dataArrayList = new LinkedList<MinnesotaCodeBean>();
        ArrayList<MinnesotaCodeItemBean> diagnosisBuiltInGroup = DiagnosisBuiltInGroupDao.getDiagnosisBuiltInGroup();
        ArrayList<MinnesotaCodeItemBean> diagnosisBuiltInChild = DiagnosisBuiltInChildDao.getDiagnosisBuiltInChild();
        for (MinnesotaCodeItemBean group : diagnosisBuiltInGroup) {
            MinnesotaCodeBean bean = new MinnesotaCodeBean();
            bean.setGroupName(group.getTitle());
            ArrayList<MinnesotaCodeItemBean> list = new ArrayList<>();
            for (MinnesotaCodeItemBean child : diagnosisBuiltInChild) {
                if (group.getId() == child.getId()) {
                    list.add(child);
                }
            }
            bean.setDataList(list);
            dataArrayList.add(bean);
        }
        return dataArrayList;
    }

    public Map<String, String> getMinnesotaCodeMap() {
        return minnesotaCodeMap;
    }

    public void addWarnCode(MinnesotaCodeItemBean minnesotaCodeItemBean) {
        warnCodeMaps.put(minnesotaCodeItemBean.getCode(), minnesotaCodeItemBean.getTitle());
    }

    private void initWarnCodesDefault() {
        List<MinnesotaCodeItemBean> dataList = makeWarnCodeList();
        if (dataList.size() > 0) {
            for (MinnesotaCodeItemBean item : dataList) {
                addWarnCode(item);
            }
        }
    }

    public List<MinnesotaCodeItemBean> makeWarnCodeList() {
        List<MinnesotaCodeItemBean> dataList = new ArrayList<>();
        Map<String, String> maps = getMinnesotaCodeMap();
        if (maps != null && maps.size() > 0) {
            MinnesotaCodeItemBean item = new MinnesotaCodeItemBean();
            //1
            String code = "413";
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //2
            code = "731";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "741";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "751";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "761";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "771";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "781";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "791";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //3
            code = "734";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "744";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "754";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "764";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //4
            code = "732";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "742";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "752";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "762";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "772";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //5
            code = "733";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "743";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "753";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "763";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "773";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //6
            code = "415";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //7
            code = "805";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "863";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //8
            code = "622";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "623";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //9
            code = "662";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "663";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            //10
            code = "672";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);

            code = "673";
            item = new MinnesotaCodeItemBean();
            item.setCode(code);
            item.setTitle(maps.get(code));
            dataList.add(item);
        }

        return dataList;
    }


    /**
     * 导出图片 走速最大支持25mm/s
     */
    public List<Bitmap> exportBmp(Context context, boolean isOutPrint, ConfigBean configBeanTemp,
                                  PatientInfoBean patientInfoBean, MacureResultBean macureResultBean,
                                  EcgDataInfoBean ecgDataInfoBean, long checkTimeStamp) {


        short[][] ecgDataArraySrc = ecgDataInfoBean.getEcgDataArray();
        short[][] ecgDataArrayAll = null;
        float[] gainArray = ecgDataInfoBean.getGainArray();

        //其它模式，截取最后10秒数据
        int ecgSrcDataLen = ecgDataArraySrc[0].length;
        int needDataLen = Const.PER_SCREEN_DATA_SECOND * Const.SAMPLE_RATE;
        int beginPos = ecgSrcDataLen - needDataLen;

        short[][] ecgDataArrayTemp = new short[ecgDataArraySrc.length][needDataLen];
        for (int i = 0; i < ecgDataArrayTemp.length; i++) {
            System.arraycopy(ecgDataArraySrc[i], beginPos, ecgDataArrayTemp[i], 0, needDataLen);
        }

        ecgDataArrayAll = ecgDataArrayTemp;

        ecgDataInfoBean.setEcgDataArray(ecgDataArrayAll);


        return makeReportRoutine(context, isOutPrint, configBeanTemp, patientInfoBean, macureResultBean,
                ecgDataInfoBean, checkTimeStamp, gainArray, ecgDataArrayAll,
                false, null,
                false, 1, 1);
    }

    /**
     * 导出pdf
     */
    public File exportPdf(Context context, boolean isOutPrint, ConfigBean configBean,
                          PatientInfoBean patientInfoBean, MacureResultBean macureResultBean,
                          EcgDataInfoBean ecgDataInfoBean, long checkTimeStamp, String resultFilePath) {

        List<Bitmap> imageList = exportBmp(context, isOutPrint, configBean, patientInfoBean, macureResultBean, ecgDataInfoBean, checkTimeStamp);
        if (imageList == null || imageList.size() == 0) {
            return null;
        }
        Log.d("pc700", "save pdf path = " + resultFilePath);
        PdfUtil.saveBitmapForPdf(imageList, resultFilePath, false);

        File outFile = new File(resultFilePath);
        if (outFile.exists()) {
            return outFile;
        } else {
            return null;
        }
    }

    /**
     * @param aiResultBean
     * @return
     */
    public List<String> getDiagnosisResult(AiResultBean aiResultBean) {
        List<String> resultDataList = new ArrayList<>();

        if (aiResultBean.getAiResultDiagnosisBean().getDiagnosis().size() > 0) {
            for (MinnesotaCodeItemBean item : aiResultBean.getAiResultDiagnosisBean().getDiagnosis()) {
                resultDataList.add(getDiagnosisTitle(item));
            }
        }

        return resultDataList;
    }

    /**
     * @param minnesotaCodeItemBean
     * @return
     */
    public String getDiagnosisTitle(MinnesotaCodeItemBean minnesotaCodeItemBean) {
        String title = minnesotaCodeItemBean.getTitle();
        String code = minnesotaCodeItemBean.getCode();
        if (!TextUtils.isEmpty(code)) {
            title = minnesotaCodeMap.get(code);
        }
        String content = "";
        content = String.format("%s", title);
        Log.e(code, content);
        return content;
    }

    private List<Bitmap> makeReportRoutine(Context context, boolean isOutPrint, ConfigBean configBean,
                                           PatientInfoBean patientInfoBean, MacureResultBean macureResultBean,
                                           EcgDataInfoBean ecgDataInfoBean, long reportTimeStamp,
                                           float[] gainArray, short[][] ecgDataArrayAll,
                                           boolean drugTest, String drugTestTimePointText, boolean needAverageTemplate, int currentPage, int maxPage) {

        List<Bitmap> imageList = new ArrayList<>();
        int extraPage = 1;
        currentPage = 0;
        boolean ifHaveReportRth = false;
        for (int i = 0; i < extraPage; i++) {
            //心电图报告
            currentPage++;

            EcgReportTemplateRoutine ecgReportTemplateRoutine = new EcgReportTemplateRoutine(context, false, true, configBean, ifHaveReportRth);
            String organizationName = configBean.getSystemSettingBean().getOrganizationName() == null ? "" : configBean.getSystemSettingBean().getOrganizationName();
            if (drugTest) {
                ecgReportTemplateRoutine.drawTitleInfo(String.format("%s %s", organizationName, context.getString(R.string.print_export_title_drug_test)));
                ecgReportTemplateRoutine.setDrugTestTimePointText(drugTestTimePointText);
            } else if (configBean.getEcgSettingBean().getLeadWorkModeType() == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_ECG_EVENT) {
                ecgReportTemplateRoutine.drawTitleInfo(String.format("%s %s", organizationName, context.getString(R.string.print_export_title_ecgevent)));
            } else {
                ecgReportTemplateRoutine.drawTitleInfo(String.format("%s %s", organizationName, context.getString(R.string.print_export_title)));
            }
            ecgReportTemplateRoutine.drawPatientInfoTop(patientInfoBean);
            ecgReportTemplateRoutine.drawMacureResult(macureResultBean);
            ecgReportTemplateRoutine.drawEcgImage(gainArray, ecgDataArrayAll, false, null);

            boolean isAiAnalysis = false;
            if (macureResultBean != null && macureResultBean.getEcgMacureResultEnum() != null) {
                isAiAnalysis = macureResultBean.getEcgMacureResultEnum() == EcgMacureResultEnum.TYPE_LOCAL_AI ||
                        macureResultBean.getEcgMacureResultEnum() == EcgMacureResultEnum.TYPE_NET_AI;
            }

            ecgReportTemplateRoutine.drawBottomEcgParamInfo(PrintManager.getInstance().getPrintBottomInfo(context, configBean, isOutPrint, reportTimeStamp, isAiAnalysis));
            ecgReportTemplateRoutine.drawBottomOtherInfo(context.getString(R.string.print_export_bottom_info),
                    String.format(context.getString(R.string.print_export_bottom_info_page), currentPage, maxPage));

            Bitmap reportTemplateBgEcg = ecgReportTemplateRoutine.getBgEcg();
            if (reportTemplateBgEcg != null) {
                imageList.add(reportTemplateBgEcg);
            }
        }

        //测量结果
        if (macureResultBean != null) {
            // 测量矩阵
            boolean macureValueFlag = configBean.getRecordSettingBean().getReportSettingBeanList().get(RecordSettingConfigEnum.ReportSetting.MEASURE_MATRIX.ordinal()).isValue();
            if (macureValueFlag) {
                currentPage += 1;
                Bitmap reportTemplateBgEcg = addMacureValueTemplate(context, isOutPrint, configBean, patientInfoBean, macureResultBean,
                        reportTimeStamp, currentPage, maxPage);
                if (reportTemplateBgEcg != null) {
                    imageList.add(reportTemplateBgEcg);
                }
            }
        }

        return imageList;
    }

    /**
     * 添加测量值模板
     */
    private Bitmap addMacureValueTemplate(Context context, boolean isOutPrint, ConfigBean configBeanTemp,
                                          PatientInfoBean patientInfoBean, MacureResultBean macureResultBean,
                                          long reportTimeStamp, int currentPage, int maxPage) {
        //平均模板，会修改对象的值。所以需要拷贝一份，单独使用


        EcgReportTemplateMacureValue ecgReportTemplateMacureValue = new EcgReportTemplateMacureValue(context, false, true, configBeanTemp);
        ecgReportTemplateMacureValue.drawTitleInfo(String.format("%s", context.getString(R.string.print_macure_value_template_title)));
        //平均模板不画一行患者信息了，都输入了，一行也显示不下。
        ecgReportTemplateMacureValue.drawPatientInfoTop(patientInfoBean);
        ecgReportTemplateMacureValue.drawMacureResult(macureResultBean);

        boolean isAiAnalysis = false;
        if (macureResultBean != null && macureResultBean.getEcgMacureResultEnum() != null) {
            isAiAnalysis = macureResultBean.getEcgMacureResultEnum() == EcgMacureResultEnum.TYPE_LOCAL_AI ||
                    macureResultBean.getEcgMacureResultEnum() == EcgMacureResultEnum.TYPE_NET_AI;
        }
        ecgReportTemplateMacureValue.drawBottomEcgParamInfo(PrintManager.getInstance().getPrintBottomInfo(context, configBeanTemp, isOutPrint, reportTimeStamp, isAiAnalysis));
        ecgReportTemplateMacureValue.drawBottomOtherInfo(context.getString(R.string.print_export_bottom_info),
                String.format(context.getString(R.string.print_export_bottom_info_page), currentPage, maxPage));

        return ecgReportTemplateMacureValue.getBgEcg();
    }
}
