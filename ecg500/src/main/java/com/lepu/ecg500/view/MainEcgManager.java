package com.lepu.ecg500.view;

import android.content.Context;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;

import java.util.LinkedList;
import java.util.List;

public class MainEcgManager {
    private static MainEcgManager instance = null;
    private static LeadType leadType;

    private DrawEcgRealView drawEcgRealView;
    private Context context;
    //走速
    private EcgSettingConfigEnum.LeadSpeedType leadSpeedType = EcgSettingConfigEnum.LeadSpeedType.FORMFEED_25;
    //增益
    private EcgSettingConfigEnum.LeadGainType leadGainType = EcgSettingConfigEnum.LeadGainType.GAIN_10;
    private float[] gainArray = new float[]{1.0F, 1.0F};
    //顺序类型
    private RecordSettingConfigEnum.RecordOrderType recordOrderType = RecordSettingConfigEnum.RecordOrderType.ORDER_SYNC;
    //重置绘图
    private boolean resetDraw = false;

    private MainEcgManager() {

    }

    public static MainEcgManager getInstance() {
        if (instance == null) {
            instance = new MainEcgManager();
        }
        return instance;
    }

    //=================================
    public void init(Context context){
        this.context = context;
        gainArray = updateGain();
    }

    public EcgSettingConfigEnum.LeadSpeedType getLeadSpeedType() {
        return leadSpeedType;
    }

    public void setLeadSpeedType(EcgSettingConfigEnum.LeadSpeedType leadSpeedType) {
        this.leadSpeedType = leadSpeedType;
    }

    public RecordSettingConfigEnum.RecordOrderType getRecordOrderType() {
        return recordOrderType;
    }

    public void setRecordOrderType(RecordSettingConfigEnum.RecordOrderType recordOrderType) {
        this.recordOrderType = recordOrderType;
    }

    public DrawEcgRealView getDrawEcgRealView() {
        return drawEcgRealView;
    }

    public void setDrawEcgRealView(DrawEcgRealView drawEcgRealView) {
        this.drawEcgRealView = drawEcgRealView;
    }

    public float[] getGainArray() {
        return gainArray;
    }

    public void setGainArray(float[] gainArray) {
        this.gainArray = gainArray;
    }

    /**
     * 初始化增益
     */
    private float[] updateGain() {
        float[] gainArrayTemp = new float[]{1.0F, 1.0F};
        switch (leadGainType) {
            case GAIN_10:
                gainArrayTemp[0] = 1.0F;
                gainArrayTemp[1] = 1.0F;
                break;
            case GAIN_2_P_5:
                gainArrayTemp[0] = 0.25F;
                gainArrayTemp[1] = 0.25F;
                break;
            case GAIN_5:
                gainArrayTemp[0] = 0.5F;
                gainArrayTemp[1] = 0.5F;
                break;
            case GAIN_20:
                gainArrayTemp[0] = 2.0F;
                gainArrayTemp[1] = 2.0F;
                break;
            case GAIN_40:
                gainArrayTemp[0] = 4.0F;
                gainArrayTemp[1] = 4.0F;
                break;
            default:
                break;
        }
        return gainArrayTemp;
    }

    /**
     * 重新绘制波形
     */
    public void resetDrawEcg() {
        if (drawEcgRealView != null) {
            resetDraw = true;
            drawEcgRealView.resetDrawEcg();
            resetDraw = false;
        }
    }

    /**
     * 获取画图模板
     * @param context
     * @param previewPageEnum
     * @param smallGridSpace
     * @param drawWidth
     * @param drawHeight
     * @param leadSpeedType
     * @param gainArray
     * @param drawReportGridBg
     * @param recordOrderType
     * @return
     */
    public static BaseEcgPreviewTemplate getBaseEcgPreviewTemplate(Context context, PreviewPageEnum previewPageEnum,
                                                                   float smallGridSpace, int drawWidth, int drawHeight,
                                                                   EcgSettingConfigEnum.LeadSpeedType leadSpeedType, float[] gainArray,
                                                                   boolean drawReportGridBg, RecordSettingConfigEnum.RecordOrderType recordOrderType) {
        String[] leadNameArray = new String[]{"I","II","III","aVR","aVL","aVF","V1","V2","V3","V4","V5","V6"};
        List<String> leadNameList = new LinkedList<String>();
        for(int i=0;i<leadNameArray.length;i++){
            leadNameList.add(leadNameArray[i]);
        }
        BaseEcgPreviewTemplate baseEcgPreviewTemplate  = null;
        switch (leadType){
            case LEAD_12:{//6*2
                baseEcgPreviewTemplate = new EcgPreviewTemplate12Lead6X2(context, drawWidth, drawHeight, drawReportGridBg,
                        leadNameList, gainArray, leadSpeedType);
            }
            break;
            case LEAD_6:{//6*1
                baseEcgPreviewTemplate = new EcgPreviewTemplate12Lead6X1(context, drawWidth, drawHeight, drawReportGridBg,
                        leadNameList, gainArray, leadSpeedType);
            }
            break;
            case LEAD_I:{//L*1
                baseEcgPreviewTemplate = new EcgPreviewTemplate1leadL(context, drawWidth, drawHeight, drawReportGridBg,
                        leadNameList, gainArray, leadSpeedType);
            }
            break;
            case LEAD_II:{//F*1
                baseEcgPreviewTemplate = new EcgPreviewTemplate1leadF(context, drawWidth, drawHeight, drawReportGridBg,
                        leadNameList, gainArray, leadSpeedType);
            }
            break;
            default:
                break;
        }
        baseEcgPreviewTemplate.init(previewPageEnum, smallGridSpace, recordOrderType);

        return baseEcgPreviewTemplate;
    }

    /**
     * 添加数据
     *
     * @param ecgDataArray
     */
    public void addEcgData(short[][] ecgDataArray) {
        if (drawEcgRealView == null || drawEcgRealView.getBaseEcgPreviewTemplate() == null) {
            return;
        }

        if (drawEcgRealView.getBaseEcgPreviewTemplate().getLeadManager() == null
                || drawEcgRealView.getBaseEcgPreviewTemplate().getLeadManager().getleadList().size() <= 0) {
            return;
        }

        if(resetDraw){
            return;
        }

        DrawEcgRealView.DrawWaveThread drawWaveThread = drawEcgRealView.getDrawWaveThread();
        if(drawWaveThread != null && drawWaveThread.isRunning()){
            drawEcgRealView.getBaseEcgPreviewTemplate().addEcgData(ecgDataArray);
        }
    }

    /**
     * 清理数据
     */
    public void clearEcgData() {
        if (drawEcgRealView == null || drawEcgRealView.getBaseEcgPreviewTemplate() == null) {
            return;
        }
        drawEcgRealView.getBaseEcgPreviewTemplate().clearData();
    }


    /**
     * 更新布局显示方式
     */
    public void updateMainEcgShowStyle(LeadType Type) {
        leadType = Type;
        //重新绘图
        if (drawEcgRealView == null || drawEcgRealView.getBaseEcgPreviewTemplate() == null) {
            return;
        }
        drawEcgRealView.getBaseEcgPreviewTemplate();
    }
    /**
     * 更新走速
     * @param enumType
     */
    public void updateMainSpeed(int enumType) {
        leadSpeedType = EcgSettingConfigEnum.LeadSpeedType.values()[enumType];

        //重新绘图
        if (drawEcgRealView == null || drawEcgRealView.getBaseEcgPreviewTemplate() == null) {
            return;
        }
        drawEcgRealView.getBaseEcgPreviewTemplate().setSpeed(leadSpeedType);
    }

    //更新走速数据不画图  add by frf 2021/4/29
    public void updateMainSpeedOnlyData(int enumType) {
        leadSpeedType = EcgSettingConfigEnum.LeadSpeedType.values()[enumType];
    }

    /**
     * 更新增益
     *
     * @param enumType
     */
    public void updateMainGain(int enumType) {
        leadGainType = EcgSettingConfigEnum.LeadGainType.values()[enumType];
        gainArray = updateGain();

        resetDrawEcg();
    }

    //更新增益数据不画图  add by frf 2021/4/29
    public void updateMainGainOnlyData(int enumType) {
        leadGainType = EcgSettingConfigEnum.LeadGainType.values()[enumType];
        gainArray = updateGain();
    }


    /**
     * 预处理数据
     * @param ecgDataArray
     * @return
     */
    public static short[][] preDowithData(short[][] ecgDataArray){
//        boolean leadoffL = leadStateList.get(0);//true 脱落
//        boolean leadoffF = leadStateList.get(1);
//        boolean leadoffR = leadStateList.get(2);
//        boolean leadoffRL = leadStateList.get(3);
//
//        int dataCount = ecgDataArray[0].length;
//        short[] emptyDataArray = new short[dataCount];
//        if(leadoffR || (leadoffL && leadoffF)){//|| leadoffRL
//            //全波导联，数据都改为0
//            for(int i=0;i<ecgDataArray.length-Const.LEAD_ADD_EXTRA_COUNT;i++){
//                System.arraycopy(emptyDataArray,0,ecgDataArray[i],0,emptyDataArray.length);
//            }
//        }else if(leadoffL){
//            //L脱落 I
//            //除了导联II，数据都改为0
//            for(int i=0;i<ecgDataArray.length-Const.LEAD_ADD_EXTRA_COUNT;i++){
//                if(i == 1){
//                    continue;
//                }
//                System.arraycopy(emptyDataArray,0,ecgDataArray[i],0,emptyDataArray.length);
//            }
//        }else if(leadoffF){
//            //F脱落 II
//            //除了导联I，数据都改为0
//            for(int i=0;i<ecgDataArray.length-Const.LEAD_ADD_EXTRA_COUNT;i++){
//                if(i == 0){
//                    continue;
//                }
//                System.arraycopy(emptyDataArray,0,ecgDataArray[i],0,emptyDataArray.length);
//            }
//        }else{
//            //
//        }

        return ecgDataArray;
    }


}
