package com.lepu.ecg500.util;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.lepu.ecg500.R;
import com.lepu.ecg500.entity.CommunSettingBean;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.EcgSettingBean;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.entity.PatientInfoSettingBean;
import com.lepu.ecg500.entity.RecordSettingBean;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;
import com.lepu.ecg500.entity.ReportSettingBean;
import com.lepu.ecg500.entity.SampleSettingBean;
import com.lepu.ecg500.entity.SystemSettingBean;
import com.lepu.ecg500.entity.SystemSettingConfigEnum;
import com.lepu.ecg500.view.BaseEcgPreviewTemplate;
import com.lepu.ecg500.view.EcgPreviewTemplate12Lead12X1;
import com.lepu.ecg500.view.PreviewPageEnum;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CustomTool {

    private static String TAG = "CustomTool";

    public static Application application;

    public static void init(Application app) {
        application = app;
        String dbPath = getDatabasePath(app, DBHelper.DATABASE_NAME_MAIN);
        DBHelper.checkDatabase(app, DBHelper.DATABASE_NAME_MAIN, 1, R.raw.main, dbPath);
        EcgDataManager.getInstance().init(app);
    }

    //获取当前系统语言 -- by frf 2021/5/17
    public static String getCurLanguage() {
        Locale locale = application.getApplicationContext().getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    public static String getDatabasePath(Context context, String dbName) {
        return context.getDatabasePath(dbName).getPath();
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * 通过节律导联，获取数据index
     *
     * @param leadNameType
     * @return
     */
    public static int getRthDataIndex(EcgSettingConfigEnum.LeadNameType leadNameType) {
        // TODO: 2021/5/12 默认使用12导联
        return getRthDataIndex(leadNameType, EcgSettingConfigEnum.LeadType.LEAD_12);
    }

    /**
     * @param leadNameType
     * @param leadType
     * @return
     */
    public static int getRthDataIndex(EcgSettingConfigEnum.LeadNameType leadNameType, EcgSettingConfigEnum.LeadType leadType) {
        int index = 0;

        // TODO: 2021/5/12 mwj 暂时先默认LeadModeType 是标准的模式
        if (leadNameType.ordinal() <= EcgSettingConfigEnum.LeadNameType.V1.ordinal()) {
            index = leadNameType.ordinal();
//            if (SettingManager.getInstance().getConfigBeanTemp().getEcgSettingBean().getLeadModeType() == EcgSettingConfigEnum.LeadModeType.LEADMODE_CABREREA){
//                switch (leadNameType){
//                    case I:
//                        index=1;
//                        break;
//                    case II:
//                        index=3;
//                        break;
//                    case III:
//                        index=5;
//                        break;
//                    case aVF:
//                        index=4;
//                        break;
//                    case aVL:
//                        index=0;
//                        break;
//                    case aVR:
//                        index=2;
//                        break;
//                }
//            }
            return index;
        }

        switch (leadType) {
            case LEAD_9:
                if (leadNameType.ordinal() == EcgSettingConfigEnum.LeadNameType.V3.ordinal()) {
                    index = 7;
                } else if (leadNameType.ordinal() == EcgSettingConfigEnum.LeadNameType.V5.ordinal()) {
                    index = 8;
                }
                break;
            case LEAD_12:
                index = leadNameType.ordinal();
                break;
            case LEAD_15_STANDARD_RIGHT:
                //v3r v4r v5r
                index = leadNameType.ordinal();
                break;
            case LEAD_15_STANDARD_AFTER:
                //v7 v8 v9
                if (leadNameType.ordinal() >= EcgSettingConfigEnum.LeadNameType.V7.ordinal()) {
                    index = leadNameType.ordinal() - 3;
                }
                break;
            case LEAD_15_CHILD:
                //v3r 4r v7
                if (leadNameType.ordinal() <= EcgSettingConfigEnum.LeadNameType.V4R.ordinal()) {
                    index = leadNameType.ordinal();
                } else if (leadNameType.ordinal() == EcgSettingConfigEnum.LeadNameType.V7.ordinal()) {
                    index = leadNameType.ordinal() - 1;
                }
                break;
            case LEAD_18:
                index = leadNameType.ordinal();
                break;
            default:
                break;
        }
        return index;
    }


    /**
     * 获取当前导联模式的名称
     *
     * @param context
     * @param leadType
     * @return
     */
    public static LinkedList<String> getLeadNameArrayCurrentLeadModeList(Context context, EcgSettingConfigEnum.LeadType leadType, boolean leadCabreraMode) {

        String[] leadNameArray;
        //美标
        if (leadCabreraMode) {
            leadNameArray = context.getResources().getStringArray(R.array.array_lead_name_aha_cabrera);
        } else {
            leadNameArray = context.getResources().getStringArray(R.array.array_lead_name_aha_standard);
        }

        LinkedList<String> leadNameList = new LinkedList<>();
        switch (leadType) {
            case LEAD_9:
                for (int i = 0; i < 6; i++) {
                    leadNameList.add(leadNameArray[i]);
                }
                //v1 v3 v5
                leadNameList.add(leadNameArray[6]);
                leadNameList.add(leadNameArray[8]);
                leadNameList.add(leadNameArray[10]);
                break;
            case LEAD_12:
                for (int i = 0; i < 12; i++) {
                    leadNameList.add(leadNameArray[i]);
                }
                break;
            case LEAD_15_STANDARD_RIGHT:
            case LEAD_15_STANDARD_AFTER:
            case LEAD_15_CHILD:
                for (int i = 0; i < 12; i++) {
                    leadNameList.add(leadNameArray[i]);
                }

                if (leadType == EcgSettingConfigEnum.LeadType.LEAD_15_STANDARD_RIGHT) {
                    //v3r v4r v5r
                    leadNameList.add(leadNameArray[12]);
                    leadNameList.add(leadNameArray[13]);
                    leadNameList.add(leadNameArray[14]);
                } else if (leadType == EcgSettingConfigEnum.LeadType.LEAD_15_STANDARD_AFTER) {
                    //v7 v8 v9
                    leadNameList.add(leadNameArray[15]);
                    leadNameList.add(leadNameArray[16]);
                    leadNameList.add(leadNameArray[17]);
                } else {
                    //v3r v4r v7
                    leadNameList.add(leadNameArray[12]);
                    leadNameList.add(leadNameArray[13]);
                    leadNameList.add(leadNameArray[15]);
                }
                break;
            case LEAD_18:
                for (int i = 0; i < 18; i++) {
                    leadNameList.add(leadNameArray[i]);
                }
                break;
            default:
                break;
        }

        return leadNameList;
    }


    /**
     * 获取画图模板
     *
     * @param context
     * @param drawWidth
     * @param drawHeight
     * @return
     */
    public static BaseEcgPreviewTemplate getBaseEcgPreviewTemplate(Context context, PreviewPageEnum previewPageEnum, float smallGridSpace, int drawWidth, int drawHeight,
                                                                   ConfigBean configBean, float[] gainArray, boolean drawReportGridBg, boolean ifHaveReportRth) {
        BaseEcgPreviewTemplate baseEcgPreviewTemplate = null;
        EcgSettingBean ecgSettingBean = configBean.getEcgSettingBean();

        EcgSettingConfigEnum.LeadSpeedType leadSpeedType = ecgSettingBean.getLeadSpeedType();
        EcgSettingConfigEnum.LeadNameType rth1 = ecgSettingBean.getRhythmLead1();
        EcgSettingConfigEnum.LeadNameType rth2 = ecgSettingBean.getRhythmLead2();
        EcgSettingConfigEnum.LeadNameType rth3 = ecgSettingBean.getRhythmLead3();
        LinkedList<String> leadNameList = getLeadNameArrayCurrentLeadModeList(context, ecgSettingBean.getLeadType(),
                false);
        String rth1Value;
        String rth2Value;
        String rth3Value;

        switch (ecgSettingBean.getLeadShowStyleType()) {
            case FORMAT_RHYTHM_ONE_RR:
            case FORMAT_RHYTHM_ONE_DRUG_TEST:
                leadNameList.clear();

                rth1Value = (String) rth1.getValue();
                for (int i = 0; i < 6; i++) {
                    leadNameList.add(rth1Value);
                }
                break;
            case FORMAT_RHYTHM_THREE_RR:
                leadNameList.clear();

                rth1Value = (String) rth1.getValue();
                rth2Value = (String) rth2.getValue();
                rth3Value = (String) rth3.getValue();

                for (int i = 0; i < 2; i++) {
                    leadNameList.add(rth1Value);
                    leadNameList.add(rth2Value);
                    leadNameList.add(rth3Value);
                }
                break;
            case FORMAT_HRV_3R:
                leadNameList.clear();

                rth1Value = (String) rth1.getValue();
                rth2Value = (String) rth2.getValue();
                rth3Value = (String) rth3.getValue();

                leadNameList.add(rth1Value);
                leadNameList.add(rth2Value);
                leadNameList.add(rth3Value);
                break;
            default:
                break;
        }

        baseEcgPreviewTemplate = new EcgPreviewTemplate12Lead12X1(context, drawWidth, drawHeight, drawReportGridBg,
                leadNameList, gainArray, leadSpeedType);
        baseEcgPreviewTemplate.init(previewPageEnum, smallGridSpace, configBean.getRecordSettingBean().getRecordOrderType());

        return baseEcgPreviewTemplate;
    }


    public static String formatDateString(Context context, long timeStamp) {
        String dateString = "";
        try {
            String formatDate = (String) SystemSettingConfigEnum.DateFormatMode.YEAR_MONTH_DAY.getValue();
            boolean is24hour = true;

            StringBuilder sb = new StringBuilder();
            if (is24hour) {
                String format = String.format("%s %s", formatDate, DateUtil.DATE_HOUR_MINUTE);
                String[] dateTime = DateUtil.stringFromDate(new Date(timeStamp), format).split(" ");
                sb.append(dateTime[0]).append("\n").append(dateTime[1]);
            } else {
                String format = String.format("%s %s", formatDate, DateUtil.DATE_HOUR_MINUTE_12);
                String[] dateTime = DateUtil.stringFromDate(new Date(timeStamp), format).split(" ");

                sb.append(dateTime[0]).append("\n");
                sb.append(DateUtil.getPeroidText(context)).append(" ");
                sb.append(dateTime[1]);
            }
            dateString = sb.toString();
        } catch (Exception e) {
            CustomTool.d(Log.getStackTraceString(e));
        }
        return dateString;
    }


    /***
    返回默认配置信息
    *****/
    public static ConfigBean getDefaultConfigBean() {
        ConfigBean configBean = new ConfigBean();
        configBean.setSystemSettingBean(getDefaultSystemSettingBean());
        configBean.setCommunSettingBean(new CommunSettingBean());
        configBean.setEcgSettingBean(getDefaultEcgSettingBean());
        configBean.setPatientInfoSettingBean(new PatientInfoSettingBean());
        configBean.setRecordSettingBean(getDefaultRecordSettingBean());//报告设置
        configBean.setSampleSettingBean(new SampleSettingBean());
        return configBean;
    }

    /***
    返回默认报告设置信息
    *****/
    public static RecordSettingBean getDefaultRecordSettingBean(){
        RecordSettingBean bean = new RecordSettingBean();
        bean.setChannelType(RecordSettingConfigEnum.ChannelType.CHANNEL_ALL);
        bean.setPrintGrid(true);
        bean.setPrintOutput(true);
        bean.setPrintDeviceType(RecordSettingConfigEnum.PrintDeviceType.THERMAL);
        bean.setPrintPreview(true);
        List<ReportSettingBean> recordSettingBeans = new ArrayList<>();

        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.MEASURE_RESULT));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.AVERAGE_TEMPLATE));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.DIAGNOSIS));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.MINNESOTA));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.PRINT_TIME));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.MEASURE_MATRIX));
        recordSettingBeans.add(new ReportSettingBean(true, RecordSettingConfigEnum.ReportSetting.TIME_RULER));
        bean.setReportSettingBeanList(recordSettingBeans);
      //  bean.set
        return bean;
    }

    /***
    返回默认心电设置信息
    *****/
    public static EcgSettingBean getDefaultEcgSettingBean() {
        EcgSettingBean bean = new EcgSettingBean();
        bean.setAutoSave(true);
        bean.setFilterAc(EcgSettingConfigEnum.LeadFilterType.FILTER_AC_50_HZ);//交流滤波器
        bean.setLeadWorkModeType(EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_SAMPLE_REALTIME);//实时采样
        bean.setLeadGainType(EcgSettingConfigEnum.LeadGainType.GAIN_10);//增益
        bean.setLeadModeType(EcgSettingConfigEnum.LeadModeType.LEADMODE_STANDARD);//标准
        bean.setLeadType(EcgSettingConfigEnum.LeadType.LEAD_12);//12导联
        bean.setLeadShowStyleType(EcgSettingConfigEnum.LeadShowStyleType.FORMAT_12_3_4);//展示样式
        bean.setLeadSpeedType(EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25);//走速
        bean.setFilterEmg(EcgSettingConfigEnum.LeadFilterType.FILTER_EMG_25);
        bean.setFilterBaseline(EcgSettingConfigEnum.LeadFilterType.FILTER_BASELINE_067);
        return bean;
    }

    public static SystemSettingBean getDefaultSystemSettingBean() {
        SystemSettingBean systemSettingBean = new SystemSettingBean();
        systemSettingBean.setUseGlasgow(false);
        systemSettingBean.setAutoLockScreenMode(SystemSettingConfigEnum.AutoLockScreenMode.MINUTE_5);
        systemSettingBean.setBrightness(10);
        systemSettingBean.setCodeSampleContent("");
        systemSettingBean.setDateFormatMode(SystemSettingConfigEnum.DateFormatMode.DAY_MONTH_YEAR);
        systemSettingBean.setDemoMode(SystemSettingConfigEnum.DemoMode.DEMOMODE_NORMALE);
        systemSettingBean.setDiagnosticMode(SystemSettingConfigEnum.DiagnosticMode.LOCAL_MODE);
        //systemSettingBean.set
        return systemSettingBean;
    }

    /***
     获取存储地址，fileName需要包含后缀名
     *****/
    public static String getFilePath(String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getPath() + "/TestECG";
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return dirPath + "/" + fileName;
    }


    /***
     返回PDF文件地址，pdfFileName不包括后缀名
     *****/
    public static String getPDFPath(String pdfFileName) {
        return getFilePath(pdfFileName + ".pdf");
    }

    /***
     返回xml文件地址，xmlFileName不包括后缀名
     *****/
    public static String getXMLPath(String xmlFileName) {
        return getFilePath(xmlFileName + ".xml");
    }

    /***
     读取文件内容,返回String
     *****/
    public static String readFileContentStr(String fullFilename) {
        String readOutStr = null;

        try {
//          //BufferedReader bufReader = new BufferedReader(new FileReader(fullFilename));
//          InputStreamReader isr = new InputStreamReader(new FileInputStream(fullFilename), "UTF-8");
//          BufferedReader bufReader = new BufferedReader(isr);
//
//          String lineSeparator = System.getProperty("line.separator");
//
//          String line = "";
//          while( ( line = bufReader.readLine() ) != null)
//          {
//          	readOutStr += line + lineSeparator;
//          }
//          bufReader.close();

            DataInputStream dis = new DataInputStream(new FileInputStream(fullFilename));
            try {
                long len = new File(fullFilename).length();
                if (len > Integer.MAX_VALUE)
                    throw new IOException("File " + fullFilename + " too large, was " + len + " bytes.");
                byte[] bytes = new byte[(int) len];
                dis.readFully(bytes);
                readOutStr = new String(bytes, "UTF-8");
            } finally {
                dis.close();
            }

            Log.d("readFileContentStr", "Successfully to read out string from file " + fullFilename);
        } catch (IOException e) {
            readOutStr = null;

            //e.printStackTrace();
            Log.d("readFileContentStr", "Fail to read out string from file " + fullFilename);
        }

        return readOutStr;
    }

}
