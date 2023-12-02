package com.lepu.ecg500.util;


/**
 * Created by wuxd
 * @author wxd
 */
public class Const {
    public static final int SAMPLE_RATE = 1000;//采样率
    public static final float MV_1_SHORT = 1048.576F;
    public static final float SHORT_MV_GAIN = 0.0009536F;//mv
    public static final float SHORT_EXTRA_VALUE = 1.035F;//采集的原始数据幅值少，补充 1035/1000f.SmartEcg 不用补充
    public static final int PER_SCREEN_DATA_SECOND = 10;
    public static final int ANALYSIS_DIAGNOSIS_MODE = 0;
    public  static final int A4_WIDTH = 2100;
    public static final int A4_HEIGHT = 2970;

}
