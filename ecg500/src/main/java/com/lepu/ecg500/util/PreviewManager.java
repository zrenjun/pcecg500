package com.lepu.ecg500.util;

import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.view.BaseEcgPreviewTemplate;

public class PreviewManager {

    private static PreviewManager instance = null;

    private ConfigBean configBean;
    private BaseEcgPreviewTemplate baseEcgPreviewTemplate;
   // private PatientCaseManager patientCaseManager;
    //增益
    private float[] gainArray = new float[]{1.0F, 1.0F};
    private PreviewManager() {

    }

    public static PreviewManager getInstance() {
        if (instance == null) {
            instance = new PreviewManager();
        }
        return instance;
    }

    public void init() {

    }

    public void destroy() {
        instance = null;
    }

    //======================================

//    /**
//     * 跳到预览
//     */
//    @Deprecated
//    public static void jumpPreview(Activity activity, PreviewPageEnum previewPageEnum, PatientInfoBean patientInfoBean,
//                                   MacureResultBean macureResultBeanPreview, String ecgDataFilePath,
//                                   EcgSettingConfigEnum.LeadWorkModeType leadWorkModeType,
//                                   EcgSettingConfigEnum.LeadType leadType, int previewDataDbId, String drugTestPoints,
//                                   EcgSettingConfigEnum.LeadNameType leadRhythm1, EcgSettingConfigEnum.LeadNameType leadRhythm2,
//                                   EcgSettingConfigEnum.LeadNameType leadRhythm3, EcgSettingConfigEnum.LeadShowStyleType leadShowStyleType,
//                                   long checkTimeStamp, String analysisResultPath, String uploadId, float[] gainArray) {
//        Bundle data = new Bundle();
//        data.putInt("previewPageEnum", previewPageEnum.ordinal());
//        data.putParcelable("patientInfoBean", patientInfoBean);
//        data.putInt("leadType", leadType.ordinal());
//        data.putInt("previewDataDbId", previewDataDbId);
//        data.putString("drugTestPoints", drugTestPoints);
//        data.putInt("leadRhythm1", leadRhythm1 != null ? leadRhythm1.ordinal() : -1);
//        data.putInt("leadRhythm2", leadRhythm2 != null ? leadRhythm2.ordinal() : -1);
//        data.putInt("leadRhythm3", leadRhythm3 != null ? leadRhythm3.ordinal() : -1);
//        if (leadShowStyleType == null) {
//            EcgSettingBean ecgSettingBean = new EcgSettingBean();
//            ecgSettingBean.setLeadWorkModeType(leadWorkModeType);
//            ecgSettingBean.setLeadType(leadType);
//            leadShowStyleType = MainEcgManager.getLeadShowStyleTypeDefaultValue(PreviewPageEnum.PAGE_PREVIEW, ecgSettingBean);
//        }
//
//        if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW) {
//            data.putParcelable("macureResultBeanPreview", macureResultBeanPreview);
//            data.putSerializable("ecgDataFilePath", ecgDataFilePath);
//            data.putInt("leadWorkModeType", leadWorkModeType.ordinal());
//            data.putInt("leadShowStyleType", leadShowStyleType.ordinal());
//            data.putString("uploadId", uploadId);
//        } else {
//            //冻结
//            boolean frankMode = leadWorkModeType == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_FRANK;
//            data.putInt("leadWorkModeType", frankMode ? leadWorkModeType.ordinal() : EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_NO.ordinal());
//            data.putInt("leadShowStyleType", MainEcgManager.getLeadShowStyleTypeDefaultValue(
//                    previewPageEnum, MainEcgManager.getInstance().getConfigBeanTemp().getEcgSettingBean()).ordinal());
//        }
//
//        data.putLong("checkTimeStamp", checkTimeStamp);
//        data.putString("analysisResultPath", analysisResultPath);
//
//        CommonActivity.startCommonActivity(activity, PageEnum.PREVIEW, data);
//    }

    public void initConfigBean(ConfigBean configBeanOld) {
        configBean = (ConfigBean) SerializableUtils.copy(configBeanOld);
    }

//    /**
//     * 获取主页需要的数据
//     *
//     * @return
//     */
//    public static PreviewDataBean getPreviewData(Context context, PreviewPageEnum previewPageEnum, ConfigBean configBean) {
//
//        List<PopItemBean> sensitivityList = new ArrayList<>();
//        List<PopItemBean> speedList = new ArrayList<>();
//        List<PopItemBean> ecgShowStyleList = new ArrayList<>();
//
//        int sensitivityIndex = -1;
//        int speedIndex = -1;
//        int ecgShowStyleIndex = 0;
//
//        int beginType;
//        int endType;
//
//        //gain
//        beginType = EcgSettingConfigEnum.LeadGainType.GAIN_AUTO.ordinal();
//        endType = EcgSettingConfigEnum.LeadGainType.GAIN_40.ordinal();
//        for (int i = beginType; i <= endType; i++) {
//            EcgSettingConfigEnum.LeadGainType item = EcgSettingConfigEnum.LeadGainType.values()[i];
//            sensitivityList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//        }
//        //定位
//        for (int i = 0; i < sensitivityList.size(); i++) {
//            PopItemBean item = sensitivityList.get(i);
//            if (item.getEnumType() == configBean.getEcgSettingBean().getLeadGainType().ordinal()) {
//                sensitivityIndex = i;
//                break;
//            }
//        }
//        if (sensitivityIndex < 0) {
//            //没有找到增益，设置默认增益
//            configBean.getEcgSettingBean().setLeadGainType(EcgSettingConfigEnum.LeadGainType.GAIN_10);
//            //定位
//            for (int i = 0; i < sensitivityList.size(); i++) {
//                PopItemBean item = sensitivityList.get(i);
//                if (item.getEnumType() == configBean.getEcgSettingBean().getLeadGainType().ordinal()) {
//                    sensitivityIndex = i;
//                    break;
//                }
//            }
//        }
//
//        EcgSettingConfigEnum.LeadWorkModeType leadWorkModeTypeCurrent = configBean.getEcgSettingBean().getLeadWorkModeType();
//        //speed
//        if (SettingManager.getInstance().isThermalPrintMode()) {
//            if (leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_RR) {
//                //rr 支持12.5mm/s,25mm/s.
//                beginType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_12_P_5.ordinal();
//                endType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25.ordinal();
//            } else {
//                //其它 25/50
//                beginType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25.ordinal();
//                endType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_50.ordinal();
//            }
//        } else {
//            if (leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_RR) {
//                //rr 支持12.5mm/s,25mm/s.
//                beginType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_12_P_5.ordinal();
//                endType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25.ordinal();
//            } else {
//                //其它 全支持
//                beginType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_5.ordinal();
//                endType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_50.ordinal();
//            }
//        }
//
//        for (int i = beginType; i <= endType; i++) {
//            EcgSettingConfigEnum.LeadSpeedType item = EcgSettingConfigEnum.LeadSpeedType.values()[i];
//            speedList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//        }
//        //定位
//        for (int i = 0; i < speedList.size(); i++) {
//            PopItemBean item = speedList.get(i);
//            if (item.getEnumType() == configBean.getEcgSettingBean().getLeadSpeedType().ordinal()) {
//                speedIndex = i;
//                break;
//            }
//        }
//        if (speedIndex < 0) {
//            //没有找到走速，设置默认走速
//            configBean.getEcgSettingBean().setLeadSpeedType(EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25);
//            //定位
//            for (int i = 0; i < speedList.size(); i++) {
//                PopItemBean item = speedList.get(i);
//                if (item.getEnumType() == configBean.getEcgSettingBean().getLeadSpeedType().ordinal()) {
//                    speedIndex = i;
//                    break;
//                }
//            }
//        }
//
//        //ecg show style
//        switch (configBean.getEcgSettingBean().getLeadType()) {
//            case LEAD_9:
//                beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_9_9_1.ordinal();
//                endType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_9_6_3.ordinal();
//                break;
//            case LEAD_15_STANDARD_RIGHT:
//            case LEAD_15_STANDARD_AFTER:
//            case LEAD_15_CHILD:
//                beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_15_1.ordinal();
//                endType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_3_5_3R.ordinal();
//                break;
//            case LEAD_18:
//                //beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_18_18_1.ordinal();
//                beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_18_12_1_6_1.ordinal();
//                endType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_18_6_2_6_1_2R.ordinal();
//                break;
//            case LEAD_12:
//            default:
//                beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_12_12_1.ordinal();
//                endType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_12_3_4_3R.ordinal();
//                break;
//        }
//
//        boolean freezeMode = previewPageEnum == PreviewPageEnum.PAGE_FREEZE;
//
//        //非RR模式，支持全导联显示
//        if (freezeMode || (leadWorkModeTypeCurrent != EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_RR &&
//                leadWorkModeTypeCurrent != EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_FRANK &&
//                leadWorkModeTypeCurrent != EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_HRV)) {
//            for (int i = beginType; i <= endType; i++) {
//                EcgSettingConfigEnum.LeadShowStyleType item = EcgSettingConfigEnum.LeadShowStyleType.values()[i];
//                ecgShowStyleList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//            }
//        }
//
//        //添加节律
//        if (leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_DRUG_TEST) {
//            //药物试验，单节律
//            EcgSettingConfigEnum.LeadShowStyleType item = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_RHYTHM_ONE_DRUG_TEST;
//            ecgShowStyleList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//        } else if (!freezeMode && leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_RR) {
//            //RR 单节律，三节律
//            beginType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_RHYTHM_ONE_RR.ordinal();
//            endType = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_RHYTHM_THREE_RR.ordinal();
//            for (int i = beginType; i <= endType; i++) {
//                EcgSettingConfigEnum.LeadShowStyleType item = EcgSettingConfigEnum.LeadShowStyleType.values()[i];
//                ecgShowStyleList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//            }
//        } else if (!freezeMode && leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_FRANK) {
//            //frank
//            EcgSettingConfigEnum.LeadShowStyleType item = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_FRANK;
//            ecgShowStyleList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//        } else if (!freezeMode && leadWorkModeTypeCurrent == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_HRV) {
//            //hrv
//            EcgSettingConfigEnum.LeadShowStyleType item = EcgSettingConfigEnum.LeadShowStyleType.FORMAT_HRV_3R;
//            ecgShowStyleList.add(new PopItemBean(item.getName(), item.getValue(), item.ordinal()));
//        }
//
//        //定位
//        for (int i = 0; i < ecgShowStyleList.size(); i++) {
//            PopItemBean item = ecgShowStyleList.get(i);
//            if (item.getEnumType() == configBean.getEcgSettingBean().getLeadShowStyleType().ordinal()) {
//                ecgShowStyleIndex = i;
//                break;
//            }
//        }
//
//        if (ecgShowStyleIndex < 0) {
//            configBean.getEcgSettingBean().setLeadShowStyleType(MainEcgManager.getLeadShowStyleTypeDefaultValue(previewPageEnum, configBean.getEcgSettingBean()));
//
//            //定位
//            for (int i = 0; i < ecgShowStyleList.size(); i++) {
//                PopItemBean item = ecgShowStyleList.get(i);
//                if (item.getEnumType() == configBean.getEcgSettingBean().getLeadShowStyleType().ordinal()) {
//                    ecgShowStyleIndex = i;
//                    break;
//                }
//            }
//        }
//
//        //
//        PreviewDataBean previewDataBean = new PreviewDataBean();
//
//        //gain
//        previewDataBean.setSensitivityList(sensitivityList);
//        previewDataBean.setSensitivityIndex(sensitivityIndex);
//        //speed
//        previewDataBean.setSpeedList(speedList);
//        previewDataBean.setSpeedIndex(speedIndex);
//        //ecg show style
//        previewDataBean.setEcgShowStyleList(ecgShowStyleList);
//        previewDataBean.setEcgShowStyleIndex(ecgShowStyleIndex);
//        return previewDataBean;
//    }

    /**
     * 画图，更新数据
     *
     * @param ecgDataArray
     */
    //    public void updateEcgImage(short[][] ecgDataArray) {
    //        if (baseEcgPreviewTemplate != null) {
    //            baseEcgPreviewTemplate.addEcgData(ecgDataArray);
    //            baseEcgPreviewTemplate.drawEcgPathPreview();
    //        }
    //    }

//    /**
//     * 重新绘图
//     */
//    public void resetDrawEcg(Context context, int bigGridCount, int imageWidth, int imageHeight) {
//        float smallGridSpace = 0;
//
//        if (Const.USE_HEIGHT_CALC_ECG_GRID) {
//            smallGridSpace = (imageHeight / ((float) bigGridCount * EcgConfig.SMALL_GRID_COUNT));
//        } else {
//            smallGridSpace = (imageWidth / ((float) bigGridCount * EcgConfig.SMALL_GRID_COUNT));//10秒數據+1个定标符号=51个大格
//        }
//
//        baseEcgPreviewTemplate = MainEcgManager.getBaseEcgPreviewTemplate(context, PreviewPageEnum.PAGE_PREVIEW, smallGridSpace,
//                imageWidth, imageHeight, configBean, gainArray, true,false);
//        baseEcgPreviewTemplate.initParams();
//        baseEcgPreviewTemplate.setEcgMode(EcgShowModeEnum.MODE_SCROLL);
//    }

//    /**
//     * 更新导联模板展示格式，若当前增益为自动档位，则重新计算自动增益值。
//     */
//    public void updateEcgShowStyle(int enumType, short[][] ecgDataAll, int ecgImageWidth, int ecgImageHeight) {
//        EcgSettingConfigEnum.LeadShowStyleType[] values = EcgSettingConfigEnum.LeadShowStyleType.values();
//        if (enumType >= 0 && enumType < values.length){
//            EcgSettingConfigEnum.LeadShowStyleType leadShowType = values[enumType];
//            configBean.getEcgSettingBean().setLeadShowStyleType(leadShowType);
//            EcgSettingConfigEnum.LeadGainType gainType = configBean.getEcgSettingBean().getLeadGainType();
//            if (gainType == EcgSettingConfigEnum.LeadGainType.GAIN_AUTO) {
//                int leadLines = leadShowType.getOriginalLeadLines();
//                EcgSettingConfigEnum.LeadWorkModeType leadWorkType = configBean.getEcgSettingBean().getLeadWorkModeType();
//                short[][] ecgDataArray = getCurrentScrrenDrawData(leadWorkType,ecgDataAll,ecgImageWidth,ecgImageHeight,0,false);
//                gainArray = MainEcgManager.getInstance().calculateAutoSensitivity(getCalculateAutoSensitivityData(ecgDataArray), leadLines);
//            }
//        }
//    }

//    /**
//     * 设置病历管理manager
//     */
//    public void setPatientCaseManager(PatientCaseManager patientCaseManager) {
//        this.patientCaseManager = patientCaseManager;
//    }

//    /**
//     * 更新增益
//     *
//     * @param enumType
//     * @param calculateAutoGainArray
//     */
//    public void updateGain(int enumType, short[][] calculateAutoGainArray) {
//        configBean.getEcgSettingBean().setLeadGainType(EcgSettingConfigEnum.LeadGainType.values()[enumType]);
//        if (enumType == EcgSettingConfigEnum.LeadGainType.GAIN_AUTO.ordinal()) {
//            int leadLines = baseEcgPreviewTemplate.getLeadLines();
//            gainArray = MainEcgManager.getInstance().calculateAutoSensitivity(getCalculateAutoSensitivityData(calculateAutoGainArray), leadLines);
//        } else {
//            gainArray = MainEcgManager.initGain(configBean.getEcgSettingBean().getLeadGainType());
//        }
//    }

//    /**
//     * 更新走速
//     */
//    public void updateSpeed(int enumType) {
//        EcgSettingConfigEnum.LeadSpeedType leadSpeedType = EcgSettingConfigEnum.LeadSpeedType.values()[enumType];
//        configBean.getEcgSettingBean().setLeadSpeedType(leadSpeedType);
//        baseEcgPreviewTemplate.setSpeed(leadSpeedType);
//    }

    /**
     * 获取计算自动增益的数据
     *
     * @return
     */
    public short[][] getCalculateAutoSensitivityData(short[][] ecgDataArrayCache) {
        int beginPos = ecgDataArrayCache[0].length - 1;
        //先拿几秒钟数据
        int needDataLen = Const.SAMPLE_RATE * 2;
        if (beginPos >= needDataLen) {
            short[][] dataArray = new short[ecgDataArrayCache.length][needDataLen];
            for (int i = 0; i < dataArray.length; i++) {
                System.arraycopy(ecgDataArrayCache[i], beginPos - needDataLen, dataArray[i], 0, needDataLen);
            }
            return dataArray;
        }
        return null;
    }

    public float[] getGainArray() {
        return gainArray;
    }

    public void setGainArray(float[] gainArray) {
        this.gainArray = gainArray;
    }

//    /**
//     * @param content
//     * @param dataList
//     * @return group g，child p
//     */
//    public Map<String, Integer> getSearchMinicodePosition(String content, LinkedList<MinnesotaCodeBean> dataList) {
//        Map<String, Integer> maps = new HashMap<>();
//
//        for (int i = 0; i < dataList.size(); i++) {
//            ArrayList<MinnesotaCodeItemBean> itemList = dataList.get(i).getDataList();
//            if (itemList != null && itemList.size() > 0) {
//                for (int j = 0; j < itemList.size(); j++) {
//                    MinnesotaCodeItemBean item = itemList.get(j);
//                    if (item.getTitle().contains(content)) {
//                        String key = String.format("%d_%d", i, j);
//                        maps.put(key, j);
//                    }
//                }
//            }
//        }
//
//        return maps;
//    }

    /**
     * 获取当前区域数据
     *
     * @return
     */
    public short[][] getCurrentAreaData(short[][] ecgDataArrayAll) {
        if (configBean.getEcgSettingBean().getLeadWorkModeType() == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_RR) {

        } else {

        }
        return null;
    }

    public BaseEcgPreviewTemplate getBaseEcgPreviewTemplate() {
        return baseEcgPreviewTemplate;
    }

//    /**
//     * @param context
//     * @param patientInfoBean
//     * @param macureResultBean
//     * @param previewDataDbId
//     * @param ecgDataInfoBean
//     * @param workModeFrank
//     * @return
//     */
//    public boolean saveEcgDataRecord(Context context, PatientInfoBean patientInfoBean, MacureResultBean macureResultBean, int previewDataDbId, EcgDataInfoBean ecgDataInfoBean, boolean workModeFrank) {
//
//        String dateString = DateUtil.stringFromDate(new Date(System.currentTimeMillis()), "yyyyMMddHHmmss");
//        String pid = patientInfoBean.getPatientNumber();
//        String fileName = String.format("%s_%s", pid, dateString);
//
//        //保存测量结果
//        String resultFilePath = "";
//        if (macureResultBean != null) {
//            resultFilePath = SdLocal.getDataDatPath(context, String.format("%s_result", fileName));
//            EcgDataManager.getInstance().writeConfigMacureResultBean(macureResultBean, resultFilePath);
//        }
//
//        if (previewDataDbId < 0) {
//
//            //冻结来的数据，不需要覆盖数据。生成数据库新记录
//            //保存心电图数据文件
//            String ecgDataPath = SdLocal.getDataDatPath(context, String.format("%s", fileName));
//            EcgDataManager.saveEcgDataFileAll(context, ecgDataPath, ecgDataInfoBean, patientInfoBean);
//
//            PatientCaseBean patientCaseBean = new PatientCaseBean();
//            patientCaseBean.setArchivesBean(patientInfoBean);
//            patientCaseBean.setUrgent(MainEcgManager.getInstance().isAddBusyMode());
//            patientCaseBean.setUploadStateEnum(UploadStateEnum.UPLOAD_NONE);
//            patientCaseBean.setCheckTime(System.currentTimeMillis());
//            patientCaseBean.setDiagnosticStateEnum(macureResultBean == null ? DiagnosticStateEnum.ANALYSIS_NO : DiagnosticStateEnum.ANALYSIS_YES);
//            patientCaseBean.setUnReadStateReportRemote(false);
//            patientCaseBean.setUploadId("-1");
//            patientCaseBean.setEcgDataResultPath(resultFilePath);
//            patientCaseBean.setEcgDataPath(ecgDataPath);
//            patientCaseBean.setDiagnosticMode(configBean.getSystemSettingBean().getDiagnosticMode());
//            patientCaseBean.setCheckCaseLeadType(configBean.getEcgSettingBean().getLeadType());
//            patientCaseBean.setCheckCaseWorkMode(workModeFrank ? EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_FRANK : EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_NO);
//            if (configBean.getEcgSettingBean().getLeadWorkModeType() == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_DRUG_TEST) {
//                List<SampleDrugTestTimePointBean> drugTestPointList = configBean.getSampleSettingBean().getDrugTestPointList();
//                StringBuilder points = new StringBuilder();
//                for (SampleDrugTestTimePointBean bean : drugTestPointList) {
//                    if (bean.isValue())
//                        points.append(bean.getDrugTestSamplePointsMode().ordinal()).append(",");
//                }
//                patientCaseBean.setDrugTestPoints(points.length() > 0 ? points.substring(0, points.length() - 1) : "");
//            }
//            patientCaseBean.setLeadShowStyle(configBean.getEcgSettingBean().getLeadShowStyleType());
//            patientCaseBean.setLeadRhythm1(configBean.getEcgSettingBean().getRhythmLead1());
//            patientCaseBean.setLeadRhythm2(configBean.getEcgSettingBean().getRhythmLead2());
//            patientCaseBean.setLeadRhythm3(configBean.getEcgSettingBean().getRhythmLead3());
//            patientCaseBean.setDemoMode(configBean.getSystemSettingBean().getDemoMode());
//            if (configBean.getSystemSettingBean().getDiagnosticMode() == SystemSettingConfigEnum.DiagnosticMode.NET_MODE) {
//                patientCaseBean.setUserID(LoginManager.getInstance().getUserID());
//            }
//            //            if (patientCaseManager == null) {
//            //                patientCaseManager = PatientCaseManagerLocalImpl.getInstance();
//            //            }
//            //            patientCaseManager.saveData(patientCaseBean);
//            PatientCaseDaoLocal.insertItem(patientCaseBean, false);
//
//            if (SettingManager.getInstance().getConfigBeanTemp().getPatientInfoSettingBean().getIdMakeType() == PatientSettingConfigEnum.IdMakeType.ID_SUMMATION) {
//                patientInfoBean = PatientInfoManager.getArchivesSerialNumberAuto();
//            } else {
//                patientInfoBean = PatientInfoManager.getInstance().getPatientInfoBeanCurrent();
//                patientInfoBean.setPatientNumber("");
//                PatientInfoManager.writeConfigArchives(BaseApplication.getInstance(), patientInfoBean);
//            }
//            ObserverManager.getInstance().notifyAsync(Const.Observer.NOTIFY_MAIN_ARCHIVES_UPDATE, null, patientInfoBean);
//        } else {
//            //数据库修改记录
//            //删除旧的测量结果文件，关联新的结果文件
//            PatientCaseBean item = PatientCaseDaoLocal.getItem(String.valueOf(previewDataDbId));
//            if (item != null) {
//                String resultPath = item.getEcgDataResultPath();
//                FileUtil.deleteFile(BaseApplication.getInstance(), new File(resultPath));
//                item.setEcgDataResultPath(resultFilePath);
//                PatientCaseDaoLocal.updateEcgDataResultPath(item);
//                ObserverManager.getInstance().notifyAsync(Const.Observer.NOTIFY_PATIENT_CASE_MODIFY_DIAGNOSIS, null, resultFilePath);
//            }
//        }
//
//        return true;
//    }

//    /**
//     * 计算心率
//     *
//     * @param ecgDataArray
//     * @return
//     */
//    public int calculateTopbarRate(short[][] ecgDataArray, int rthIndex1) {
//        //        NotifyEcgDataBean notifyEcgDataBean = new NotifyEcgDataBean();
//        //        notifyEcgDataBean.setLeadEcgBuf(ecgDataArray);
//        //        notifyEcgDataBean.setLeadEcgLen(ecgDataArray[0].length);
//        //        JniSample.getInstance().getDataHeartRate(notifyEcgDataBean, rthIndex1);
//        //        int heartRateValue = notifyEcgDataBean.getHeartRate();
//
//        JniHeartRateDetect.getInstance().initHeartRateDetectTemp(Const.SAMPLE_RATE, Const.HEART_RATE_DETECT_VALUE);
//        int heartRateValue = 0;
//        short[] ecgDataArraySrc = ecgDataArray[rthIndex1];
//        int totalLen = ecgDataArraySrc.length;
//        int needLen = 5000;
//        if (totalLen > needLen) {
//            short[] ecgDataArrayDest = new short[needLen];
//            System.arraycopy(ecgDataArraySrc, totalLen - needLen, ecgDataArrayDest, 0, needLen);
//            heartRateValue = JniHeartRateDetect.getInstance().getDataHeartRateTemp(ecgDataArrayDest);
//        } else {
//            heartRateValue = JniHeartRateDetect.getInstance().getDataHeartRateTemp(ecgDataArraySrc);
//        }
//        JniHeartRateDetect.getInstance().closeHeartRateDetectTemp();
//
//        return heartRateValue;
//    }

//    /**
//     * 获取当前屏幕开始的10s数据
//     * 分析，保存，打印操作用。返回原始采集数据。8导联，或14导联数据
//     */
//    public short[][] getCurrentNeedDoWithData(short[][] ecgDataArrayAll) {
//        // TODO: 2020-05-15
//        //待实现，先用test数据
//        short[][] dataArray = new short[ecgDataArrayAll.length][Const.SAMPLE_RATE * 10];
//        //当前屏幕的数据不足10s且之后的数据也不足10s
//        if (ecgDataArrayAll[0].length - firstIndex < dataArray[0].length) {
//            for (int i = 0; i < dataArray.length; i++) {
//                System.arraycopy(ecgDataArrayAll[i], ecgDataArrayAll[0].length - dataArray[0].length, dataArray[i], 0, dataArray[0].length);
//            }
//        } else {
//            for (int i = 0; i < dataArray.length; i++) {
//                System.arraycopy(ecgDataArrayAll[i], firstIndex, dataArray[i], 0, dataArray[0].length);
//            }
//        }
//
//        return dataArray;
//    }

//    /**
//     * HRV模式节律导联数据
//     *
//     * @param data
//     * @return
//     */
//    public short[][] initHrvData(short[][] data) {
//        //RR模式分割数据
//        EcgSettingBean ecgSettingBean = PreviewManager.getInstance().getConfigBean().getEcgSettingBean();
//        int rth1DataIndex = MainEcgManager.getRthDataIndex(ecgSettingBean.getRhythmLead1(), ecgSettingBean.getLeadType());
//        short[] rth1DataArray = data[rth1DataIndex];
//        int rth2DataIndex = MainEcgManager.getRthDataIndex(ecgSettingBean.getRhythmLead2(), ecgSettingBean.getLeadType());
//        short[] rth2DataArray = data[rth2DataIndex];
//        int rth3DataIndex = MainEcgManager.getRthDataIndex(ecgSettingBean.getRhythmLead3(), ecgSettingBean.getLeadType());
//        short[] rth3DataArray = data[rth3DataIndex];
//
//        short[][] dataArrayTemp = new short[3][rth1DataArray.length];
//        dataArrayTemp[0] = rth1DataArray;
//        dataArrayTemp[1] = rth2DataArray;
//        dataArrayTemp[2] = rth3DataArray;
//        return dataArrayTemp;
//    }

//    /**
//     * RR模式数据分割
//     *
//     * @param data
//     * @return
//     */
//    public short[][] initRRData(ConfigBean configBean, short[][] data) {
//        short[][] result;
//        //RR模式分割数据
//        EcgSettingBean ecgSettingBean = configBean.getEcgSettingBean();
//        int rth1DataIndex = MainEcgManager.getRthDataIndex(ecgSettingBean.getRhythmLead1(), ecgSettingBean.getLeadType());
//        short[] rthDataArray = data[rth1DataIndex];
//        if (ecgSettingBean.getLeadShowStyleType() == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_RHYTHM_ONE_RR) {
//            int leadLines = 6;
//
//            int simpleLeadDataLen = rthDataArray.length / leadLines;
//            short[][] dataArrayTemp = new short[leadLines][simpleLeadDataLen];
//            for (int i = 0; i < leadLines; i++) {
//                System.arraycopy(rthDataArray, i * simpleLeadDataLen, dataArrayTemp[i], 0, simpleLeadDataLen);
//            }
//            result = dataArrayTemp;
//        } else if (ecgSettingBean.getLeadShowStyleType() == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_RHYTHM_THREE_RR) {
//            int rth2DataIndex = MainEcgManager.getInstance().getRthDataIndex(ecgSettingBean.getRhythmLead2());
//            short[] rth2DataArray = data[rth2DataIndex];
//            int rth3DataIndex = MainEcgManager.getInstance().getRthDataIndex(ecgSettingBean.getRhythmLead3());
//            short[] rth3DataArray = data[rth3DataIndex];
//            int leadLines = 6;
//
//            int simpleLeadDataLen = rthDataArray.length / leadLines * 3;
//            short[][] dataArrayTemp = new short[leadLines][simpleLeadDataLen];
//            System.arraycopy(rthDataArray, 0, dataArrayTemp[0], 0, simpleLeadDataLen);
//            System.arraycopy(rth2DataArray, 0, dataArrayTemp[1], 0, simpleLeadDataLen);
//            System.arraycopy(rth3DataArray, 0, dataArrayTemp[2], 0, simpleLeadDataLen);
//            System.arraycopy(rthDataArray, simpleLeadDataLen, dataArrayTemp[3], 0, simpleLeadDataLen);
//            System.arraycopy(rth2DataArray, simpleLeadDataLen, dataArrayTemp[4], 0, simpleLeadDataLen);
//            System.arraycopy(rth3DataArray, simpleLeadDataLen, dataArrayTemp[5], 0, simpleLeadDataLen);
//            result = dataArrayTemp;
//        } else {
//            result = data;
//        }
//        return result;
//    }

//    public float getGridSpace(EcgSettingConfigEnum.LeadWorkModeType leadWorkModeType, int ecgImageHeight, int ecgImageWidth) {
//        float bigGridCount = 0;
//        float gridSpace = 0;
//
//        if (Const.USE_HEIGHT_CALC_ECG_GRID) {
//            bigGridCount = EcgConfig.BIG_GRID_COUNT_H;
//            //HRV 波形区域变小网格数量也缩小
//            if (leadWorkModeType == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_HRV)
//                bigGridCount = bigGridCount / 3;
//
//            gridSpace = ecgImageHeight / ((float) bigGridCount * EcgConfig.SMALL_GRID_COUNT);
//        } else {
//            bigGridCount = EcgConfig.BIG_GRID_COUNT_W;
//            gridSpace = ecgImageWidth / ((float) bigGridCount * EcgConfig.SMALL_GRID_COUNT);
//        }
//        return gridSpace;
//    }

//    /**
//     * 获取当前屏幕可画的数据
//     *
//     * @return
//     */
//    public synchronized short[][] getCurrentScrrenDrawData(EcgSettingConfigEnum.LeadWorkModeType leadWorkModeType, short[][] ecgDataArrayAll, int ecgImageWidth, int ecgImageHeight, float length, boolean refreshData) {
//        mAllDrawEcgDataLen = ecgDataArrayAll[0].length;
//
//        float gridSpace = getGridSpace(leadWorkModeType, ecgImageHeight, ecgImageWidth);
//        float speed = (float) configBean.getEcgSettingBean().getLeadSpeedType().getValue();
//        float screenCanDrawSecond = (ecgImageWidth - gridSpace * 5) / baseEcgPreviewTemplate.getLeadColumes() / gridSpace / speed;
//        int needData = (int) (Const.SAMPLE_RATE * screenCanDrawSecond);
//        short[][] dataArray;
//        //内存中的数据不够，屏幕画的数据
//        if (ecgDataArrayAll[0].length <= needData) {
//
//            needData = ecgDataArrayAll[0].length;
//            dataArray = new short[ecgDataArrayAll.length][needData];
//
//            if(refreshData){
//                refreshData(needData / (float) Const.SAMPLE_RATE * speed, true, ecgDataArrayAll, speed);
//                refreshRthData(needData / (float) Const.SAMPLE_RATE * speed, isFirst, ecgDataArrayAll, speed);
//            }
//
//            isFirst = false;
//            for (int i = 0; i < dataArray.length; i++) {
//                System.arraycopy(ecgDataArrayAll[i], ecgDataArrayAll[0].length - dataArray[0].length, dataArray[i], 0, dataArray[0].length);
//            }
//        } else {
//            if (isFirst) {
//                length = (ecgImageWidth - gridSpace * 5) / baseEcgPreviewTemplate.getLeadColumes() / gridSpace;
//                mScreenDrawEcgDataLen = (int) (ecgDataArrayAll[0].length * (ecgImageWidth - gridSpace * 5) / gridSpace / (speed * ecgDataArrayAll[0].length / Const.SAMPLE_RATE));
//            } else
//                length = length / gridSpace;
//            dataArray = new short[ecgDataArrayAll.length][(int) (ecgDataArrayAll[0].length * ((ecgImageWidth - gridSpace * 5) / baseEcgPreviewTemplate.getLeadColumes() / gridSpace) / (speed * ecgDataArrayAll[0].length / Const.SAMPLE_RATE))];
//
//            if(refreshData){
//                refreshData(length, isFirst, ecgDataArrayAll, speed);
//                refreshRthData(length, isFirst, ecgDataArrayAll, speed);
//            }
//
//            isFirst = false;
//            if (ecgDataArrayAll[0].length - dataArray[0].length < firstIndex) {
//                firstIndex = ecgDataArrayAll[0].length - dataArray[0].length;
//            }
//
//            for (int i = 0; i < dataArray.length; i++) {
//                System.arraycopy(ecgDataArrayAll[i], firstIndex, dataArray[i], 0, dataArray[0].length);
//            }
//        }
//
//        //需要转换成对应导联类型的数据
//        //        dataArray = WaveEncodeUtil.leadDataSwitch(dataArray,
//        //                SettingManager.getInstance().checkLeadSortCabrera(),leadType);
//        return dataArray;
//    }

    /**
     * 所画数据的总长度
     */
    private int mAllDrawEcgDataLen;
    /**
     * 整个屏幕能画的数据
     */
    private int mScreenDrawEcgDataLen;
    private int firstIndex_onePlus;
    private int firstIndex_rth, lastIndex_rth;
    private int firstIndex, lastIndex;
    private float dataRatio = 0;
    private float inorderDataRation = 0;
    private int mScreenShowCount;
    private boolean isFirst = true;
    private short[][] onePointData;

    public void clearCurrentScreenData() {
        firstIndex = 0;
        lastIndex = 0;
        isFirst = true;
        mAllDrawEcgDataLen = 0;
        firstIndex_onePlus = 0;
        firstIndex_rth = 0;
        lastIndex_rth = 0;
        //        dataRatio = 0;
    }

    public float getDataRatio() {
        return dataRatio;
    }

    public float getInorderDataRation() {
        return inorderDataRation;
    }

    /**
     * 数据位置 是否剩余一屏数据
     *
     * @return
     */
    public boolean lessOneScreenData() {
        //        KLog.e("========remainOneScreenData=====firstIndex_onePlus"+firstIndex_onePlus+"=========mScreenDrawEcgDataLen"+mScreenDrawEcgDataLen+"============mAllDrawEcgDataLen"+mAllDrawEcgDataLen);

        return mAllDrawEcgDataLen - firstIndex_onePlus <= mScreenDrawEcgDataLen && mAllDrawEcgDataLen - firstIndex_onePlus > mScreenShowCount;
    }

    public boolean lessOneScreenRthData() {

        boolean result = mAllDrawEcgDataLen - firstIndex_onePlus <= mScreenDrawEcgDataLen && mAllDrawEcgDataLen - firstIndex_onePlus > rhythmCount;
        return result;
    }


    /**
     * 刷新正常导联数据
     *
     * @param length
     * @param isFirst
     * @param data
     * @param speed
     * @return
     */
    private boolean isInorder;
    private int leadColumes;
    private EcgSettingConfigEnum.LeadShowStyleType leadShowStyleType;

//    private void refreshData(float length, boolean isFirst, short[][] data, float speed) {
//
//        int count = (int) (data[0].length * length / (speed * data[0].length / Const.SAMPLE_RATE) + 0.5f);
//        if (count == 0)
//            return;
//        if (baseEcgPreviewTemplate == null || baseEcgPreviewTemplate.getLeadManager() == null) {
//            return;
//        }
//        if ((count >= 0 && lastIndex >= data[0].length) || (count <= 0 && firstIndex <= 0)) {
//            return;
//        }
//
//        if (count < 0 && isInorder && firstIndex - mScreenShowCount * (leadColumes - 1) <= 0)
//            return;
//
//        //        Log.e("=============", "================count=" + count);
//
//        //        ThreadPool.execute(() -> {
//
//
//        if (isFirst) {
//            //            lastIndex = firstIndex + count;
//            //            firstIndex = 0;
//            leadShowStyleType = PreviewManager.getInstance().getConfigBean().getEcgSettingBean().getLeadShowStyleType();
//            isInorder = MainEcgManager.getInstance().getConfigBeanTemp().getRecordSettingBean().getRecordOrderType() == RecordSettingConfigEnum.RecordOrderType.ORDER_INORDER;
//            leadColumes = baseEcgPreviewTemplate.getLeadColumes();
//            onePointData = new short[data.length][1];
//            lastIndex = data[0].length;
//            firstIndex = data[0].length - count;
//            if (isInorder&&count * leadColumes > data[0].length) {
//                count = mScreenShowCount = data[0].length / leadColumes;
//                firstIndex = data[0].length - count;
//            } else {
//                mScreenShowCount = count;
//            }
//
//        } else {
//            firstIndex += count;
//            lastIndex += count;
//        }
//
//        if (count > 0 && lastIndex > data[0].length) {
//            count = data[0].length - lastIndex + count;
//            lastIndex = data[0].length;
//            firstIndex = lastIndex - mScreenShowCount;
//        }
//
//        if (isInorder) {
//            if (count < 0) {
//                int start = firstIndex - mScreenShowCount * (leadColumes - 1);
//                if (start < 0) {
//                    count = firstIndex - count - mScreenShowCount * (leadColumes - 1);
//                    firstIndex = mScreenShowCount * (leadColumes - 1);
//                    lastIndex = firstIndex + mScreenShowCount;
//                }
//            }
//        } else if (count < 0 && firstIndex < 0) {
//            //            count = -(firstIndex - count);
//            count = firstIndex - count;
//            firstIndex = 0;
//            lastIndex = firstIndex + mScreenShowCount;
//        }
//
//        if (isInorder) {
//            int columesIndex;
//            if (count < 0) {
//                for (int i = lastIndex-1; i >= firstIndex; i--) {
//                    for (int j = 0; j < data.length; j++) {
//                        if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_9_6_3
//                                || leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_6_3
//                                || leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_6_3_1R) {
//                            columesIndex = j / 6;
//                        } else if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_9) {
//                            columesIndex = j / 6;
//                            if (columesIndex > 1) {
//                                columesIndex = 1;
//                            }
//                        } else if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_18_12_1_6_1) {
//                            columesIndex = j / 12;
//                        } else {
//                            columesIndex = j / (data.length / leadColumes);
//                        }
//                        int dataIndex = i - (leadColumes - columesIndex - 1) * mScreenShowCount;
//                        onePointData[j][0] = data[j][dataIndex];
//                    }
//                    baseEcgPreviewTemplate.addEcgData(onePointData);
//                }
//            } else {
//                for (int i = firstIndex; i < lastIndex; i++) {
//                    for (int j = 0; j < data.length; j++) {
//                        if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_9_6_3
//                                || leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_6_3
//                                || leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_6_3_1R) {
//                            columesIndex = j / 6;
//                        } else if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_15_6_9) {
//                            columesIndex = j / 6;
//                            if (columesIndex > 1) {
//                                columesIndex = 1;
//                            }
//                        } else if (leadShowStyleType == EcgSettingConfigEnum.LeadShowStyleType.FORMAT_18_12_1_6_1) {
//                            columesIndex = j / 12;
//                        } else {
//                            columesIndex = j / (data.length / leadColumes);
//                        }
//                        int dataIndex = i - (leadColumes - columesIndex - 1) * mScreenShowCount;
//                        onePointData[j][0] = data[j][dataIndex];
//                    }
//                    baseEcgPreviewTemplate.addEcgData(onePointData);
//                }
//            }
//        } else {
//            if (count < 0) {
//
//                //            for (int i = firstIndex - count; i > firstIndex; i--) {
//                //                for (int j = 0; j < data.length; j++) {
//                //                    onePointData[j][0] = data[j][i];
//                //                }
//                //                baseEcgPreviewTemplate.addEcgData(onePointData);
//                //            }
//                for (int i = lastIndex - 1; i >= firstIndex; i--) {
//                    for (int j = 0; j < data.length; j++) {
//                        onePointData[j][0] = data[j][i];
//                    }
//                    baseEcgPreviewTemplate.addEcgData(onePointData);
//                }
//            } else {
//                //            for (int i = lastIndex - count; i < lastIndex; i++) {
//                //
//                //                for (int j = 0; j < data.length; j++) {
//                //                    onePointData[j][0] = data[j][i];
//                //                }
//                //                baseEcgPreviewTemplate.addEcgData(onePointData);
//                //            }
//
//                for (int i = firstIndex; i < lastIndex; i++) {
//
//                    for (int j = 0; j < data.length; j++) {
//                        onePointData[j][0] = data[j][i];
//                    }
//                    baseEcgPreviewTemplate.addEcgData(onePointData);
//                }
//            }
//        }
//
//
//        dataRatio = firstIndex / ((float) data[0].length);
//        inorderDataRation = (firstIndex - mScreenShowCount * (leadColumes - 1)) / (float) data[0].length;
//        Log.d("----", "========firstIndex" + firstIndex + "=====count" + count);
//    }

    /**
     * 刷新节律导联数据
     *
     * @param length
     * @param isFirst
     * @param data
     * @param speed
     * @return
     */
    private int rhythmCount;

    private void refreshRthData(float length, boolean isFirst, short[][] data, float speed) {

        int count = (int) (data[0].length * length / (speed * data[0].length / Const.SAMPLE_RATE));
        if (count == 0)
            return;

        if (length == 0 || baseEcgPreviewTemplate == null || baseEcgPreviewTemplate.getLeadManager() == null) {
            return;
        }
        if (!isFirst && (mScreenShowCount == data[0].length || (count >= 0 && firstIndex_rth >= data[0].length - rhythmCount) || (count <= 0 && firstIndex_rth <= 0))) {
            return;
        }
        if (isFirst) {
            lastIndex_rth = data[0].length;
            firstIndex_onePlus = data[0].length - count;
            rhythmCount = Math.min(mScreenShowCount * baseEcgPreviewTemplate.getLeadColumes(), data[0].length);
            firstIndex_rth = data[0].length - rhythmCount;
            short[][] dataFirst = new short[data.length][rhythmCount];
            for (int i = 0; i < dataFirst.length; i++) {
                System.arraycopy(data[i], firstIndex_rth, dataFirst[i], 0, rhythmCount);
            }
            baseEcgPreviewTemplate.addPreviewRthEcgData(dataFirst);
            count = 0;
            /*lastIndex_rth = data[0].length;
            firstIndex_rth = data[0].length - count;
            firstIndex_onePlus = data[0].length - count;
            short[][] dataFirst = new short[data.length][mScreenShowCount];
            for (int i = 0; i < dataFirst.length; i++) {
                System.arraycopy(data[i], firstIndex_rth, dataFirst[i], 0, dataFirst[0].length);
            }
            baseEcgPreviewTemplate.addPreviewRthEcgData(dataFirst);
            count = 0;*/
        } else {
            //滑入屏幕和滑出屏幕的时候 修改一下值为正好一屏
            if (firstIndex_rth < mAllDrawEcgDataLen - mScreenDrawEcgDataLen && firstIndex_rth + count > mAllDrawEcgDataLen - mScreenDrawEcgDataLen) {
                count = mAllDrawEcgDataLen - mScreenDrawEcgDataLen - firstIndex_rth;
            }
            firstIndex_rth += count;
            //数据区间大于一屏幕
            if (mAllDrawEcgDataLen - firstIndex_rth > mScreenDrawEcgDataLen) {
                lastIndex_rth = firstIndex_rth + mScreenDrawEcgDataLen;
            } else {
                lastIndex_rth = data[0].length;
            }
        }
        //        KLog.e("========firstIndex_rth==="+firstIndex_rth+"=============lastIndex_rth==="+lastIndex_rth+"======count==="+count);
        if (count > 0 && firstIndex_rth > data[0].length - mScreenShowCount) {
            count = count - (firstIndex_rth - (data[0].length - mScreenShowCount));
            lastIndex_rth = data[0].length;
            firstIndex_rth = lastIndex - mScreenShowCount;
            firstIndex_onePlus = firstIndex_rth - count;
            //            KLog.e("========裁剪后count==="+count+"===firstIndex_rth==="+firstIndex_rth+"===data[0].length===="+data[0].length+"====mScreenShowCount===="+mScreenShowCount);
        }

        if (count < 0 && firstIndex_rth < 0) {
            count = 0 - (firstIndex_rth - count);
            firstIndex_rth = 0;
            lastIndex_rth = firstIndex_rth + mScreenDrawEcgDataLen;
            firstIndex_onePlus = firstIndex_rth - count;
            //            KLog.e("========count < 0 && firstIndex_rth < 0"+count);
        }

        if (count < 0) {
            for (int i = firstIndex_rth - count; i > firstIndex_rth; i--) {
                for (int j = 0; j < data.length; j++) {
                    onePointData[j][0] = data[j][i];
                }
                firstIndex_onePlus--;
                baseEcgPreviewTemplate.addPreviewRthEcgData(onePointData);
            }
        } else {
            for (int i = lastIndex_rth - count; i < lastIndex_rth; i++) {

                for (int j = 0; j < data.length; j++) {
                    onePointData[j][0] = data[j][i];
                }
                firstIndex_onePlus++;
                baseEcgPreviewTemplate.addPreviewRthEcgData(onePointData);
            }

        }
        //        KLog.e("========first_rth"+firstIndex_rth+"last_rth"+lastIndex_rth+"first_oneplus"+firstIndex_onePlus+"======mScreenShowCount"+mScreenShowCount);
    }

    public ConfigBean getConfigBean() {
        return configBean;
    }

    public void setConfigBean(ConfigBean configBean) {
        this.configBean = configBean;
    }
//
//    public static class IntentBuilder {
//        Bundle bundle;
//
//        boolean isCollectFinishJump = false;
//        PreviewPageEnum previewPageEnum;
//        PatientInfoBean patientInfoBean;
//        MacureResultBean macureResultBeanPreview;
//        String ecgDataFilePath;
//        int previewDataDbId;
//        String drugTestPoints;
//        String analysisResultPath = "";
//        String uploadId = "";
//        long checkTimeStamp;
//
//        EcgSettingConfigEnum.LeadWorkModeType leadWorkModeType;
//        EcgSettingConfigEnum.LeadType leadType;
//        EcgSettingConfigEnum.LeadNameType leadRhythm1;
//        EcgSettingConfigEnum.LeadNameType leadRhythm2;
//        EcgSettingConfigEnum.LeadNameType leadRhythm3;
//        EcgSettingConfigEnum.LeadShowStyleType leadShowStyleType;
//        EcgSettingConfigEnum.LeadSpeedType leadSpeedType;
//        EcgSettingConfigEnum.LeadGainType leadGainType;
//        float[] gainArray;
//
//        public IntentBuilder() {
//            this.bundle = new Bundle();
//        }
//
//        public IntentBuilder setCollectFinishJump(boolean isCollectFinishJump){
//            this.isCollectFinishJump = isCollectFinishJump;
//            bundle.putBoolean("isCollectedFinishPreview",isCollectFinishJump);
//            return this;
//        }
//
//        public IntentBuilder previewPageEnum(@NonNull PreviewPageEnum pageEnum){
//            this.previewPageEnum = pageEnum;
//            bundle.putInt("previewPageEnum", pageEnum.ordinal());
//            return this;
//        }
//
//        public IntentBuilder patientInfo(PatientInfoBean patientInfo){
//            this.patientInfoBean = patientInfo;
//            bundle.putParcelable("patientInfoBean", patientInfo);
//            return this;
//        }
//
//        public IntentBuilder macureResult(MacureResultBean macureResult){
//            this.macureResultBeanPreview = macureResult;
//            return this;
//        }
//
//        public IntentBuilder ecgDataFilePath(String ecgDataFilePath){
//            this.ecgDataFilePath = ecgDataFilePath;
//            return this;
//        }
//
//        public IntentBuilder readConfigFromEcgSetting(EcgSettingBean bean){
//            if (bean != null){
//                leadWorkModeType = bean.getLeadWorkModeType();
//                leadType = bean.getLeadType();
//                leadRhythm1 = bean.getRhythmLead1();
//                leadRhythm2 = bean.getRhythmLead2();
//                leadRhythm3 = bean.getRhythmLead3();
//                leadShowStyleType = bean.getLeadShowStyleType();
//                leadSpeedType = bean.getLeadSpeedType();
//                leadGainType = bean.getLeadGainType();
//            }
//            return this;
//        }
//
//        public IntentBuilder setLeadWorkModeType(EcgSettingConfigEnum.LeadWorkModeType workModeType){
//            this.leadWorkModeType = workModeType;
//            return this;
//        }
//
//        public IntentBuilder setLeadType(EcgSettingConfigEnum.LeadType leadType){
//            this.leadType = leadType;
//            return this;
//        }
//
//        public IntentBuilder setLeadRhythm_1(EcgSettingConfigEnum.LeadNameType leadRhythm_1){
//            this.leadRhythm1 = leadRhythm_1;
//            return this;
//        }
//        public IntentBuilder setLeadRhythm_2(EcgSettingConfigEnum.LeadNameType leadRhythm_2){
//            this.leadRhythm2 = leadRhythm_2;
//            return this;
//        }
//
//        public IntentBuilder setLeadRhythm_3(EcgSettingConfigEnum.LeadNameType leadRhythm_3){
//            this.leadRhythm3 = leadRhythm_3;
//            return this;
//        }
//
//        public IntentBuilder setLeadShowStyleType(EcgSettingConfigEnum.LeadShowStyleType leadShowStyle){
//            this.leadShowStyleType = leadShowStyle;
//            return this;
//        }
//
//        public IntentBuilder setLeadSpeedType(EcgSettingConfigEnum.LeadSpeedType speedType){
//            this.leadSpeedType = speedType;
//            return this;
//        }
//
//        public IntentBuilder setLeadGainType(EcgSettingConfigEnum.LeadGainType gainType){
//            this.leadGainType = gainType;
//            return this;
//        }
//
//        public IntentBuilder previewDataDbId(int previewDataDbId){
//            this.previewDataDbId = previewDataDbId;
//            bundle.putInt("previewDataDbId", previewDataDbId);
//            return this;
//        }
//
//        public IntentBuilder drugTestPoints(String drugTestPoints){
//            this.drugTestPoints = drugTestPoints;
//            bundle.putString("drugTestPoints", drugTestPoints);
//            return this;
//        }
//
//        public IntentBuilder analysisResultPath(String path){
//            this.analysisResultPath = path;
//            bundle.putString("analysisResultPath", path);
//            return this;
//        }
//
//        public IntentBuilder uploadId(String id){
//            this.uploadId = id;
//            return this;
//        }
//
//        public IntentBuilder checkTimeStamp(long timeStamp){
//            this.checkTimeStamp = timeStamp;
//            bundle.putLong("checkTimeStamp", timeStamp);
//            return this;
//        }
//
//        public IntentBuilder useGainArray(float[] gainArray){
//            this.gainArray = gainArray;
//            bundle.putFloatArray("gainArrayBefore",gainArray);
//            return this;
//        }
//
//        public boolean startActivity(Activity act){
//            if (act == null || act.isDestroyed() || act.isFinishing()){
//                return false;
//            }
//            if (leadSpeedType != null){
//                bundle.putInt("leadSpeedType",leadSpeedType.ordinal());
//            }
//            if (leadGainType != null){
//                bundle.putInt("leadGainType",leadGainType.ordinal());
//            }
//            if (leadType != null){
//                bundle.putInt("leadType", leadType.ordinal());
//            }
//            if (leadRhythm1 != null){
//                bundle.putInt("leadRhythm1", leadRhythm1.ordinal());
//            }
//            if (leadRhythm2 != null){
//                bundle.putInt("leadRhythm2", leadRhythm2.ordinal());
//            }
//            if (leadRhythm3 != null){
//                bundle.putInt("leadRhythm3",leadRhythm3.ordinal());
//            }
//            if (leadWorkModeType == null){
//                leadWorkModeType = MainEcgManager.getInstance().getConfigBeanTemp().getEcgSettingBean().getLeadWorkModeType();
//            }
//            if (leadShowStyleType == null) {
//                EcgSettingBean ecgSettingBean = new EcgSettingBean();
//                ecgSettingBean.setLeadWorkModeType(leadWorkModeType);
//                ecgSettingBean.setLeadType(leadType);
//                leadShowStyleType = MainEcgManager.getLeadShowStyleTypeDefaultValue(PreviewPageEnum.PAGE_PREVIEW, ecgSettingBean);
//            }
//
//            if (previewPageEnum == PreviewPageEnum.PAGE_PREVIEW) {
//                bundle.putParcelable("macureResultBeanPreview", macureResultBeanPreview);
//                bundle.putSerializable("ecgDataFilePath", ecgDataFilePath);
//                bundle.putInt("leadWorkModeType", leadWorkModeType.ordinal());
//                bundle.putInt("leadShowStyleType", leadShowStyleType.ordinal());
//                bundle.putString("uploadId",uploadId);
//            } else {
//                //冻结
//                boolean frankMode = leadWorkModeType == EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_FRANK;
//                bundle.putInt("leadWorkModeType", frankMode ? leadWorkModeType.ordinal() : EcgSettingConfigEnum.LeadWorkModeType.WORK_MODE_NO.ordinal());
//
//                bundle.putInt("leadShowStyleType", MainEcgManager.getLeadShowStyleTypeDefaultValue(
//                        previewPageEnum, MainEcgManager.getInstance().getConfigBeanTemp().getEcgSettingBean()).ordinal());
//            }
//            CommonActivity.startCommonActivity(act, PageEnum.PREVIEW, bundle);
//            return true;
//        }
//    }
}
