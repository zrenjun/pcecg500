package com.lepu.ecg500.util;


public class CommConst {

    public static final int FILTER_LOW_PASS_75 = 75; //低通75
    public static final int FILTER_LOW_PASS_90 = 90; //低通90
    public static final int FILTER_LOW_PASS_100 = 100; //低通100
    public static final int FILTER_LOW_PASS_165 = 165; //低通165
    public static final int FILTER_EMG_25 = 25; //肌电25
    public static final int FILTER_EMG_35 = 35; //肌电25
    public static final int FILTER_EMG_45 = 45; //肌电25
    public static final int FILTER_HUM_50 = 50; //工频25
    public static final int FILTER_HP_005 = 5; //基漂 0.05
    public static final int FILTER_HP_032 = 32; //基漂0.32
    public static final int FILTER_HP_067 = 67; //基漂0.67



    /*************************************芯片参数设置值********************************************/
    /**
     * 波特率setup the baud rate
     */
    public static final int FT230X_BAUD_RATE = 200000;
    /**
     * 停止标签值 stop bits
     */
    public static final byte FT230X_STOP_BIT = 1;
    /**
     * 数据字节数量 data bits
     */
    public static final byte FT230X_DATA_BIT = 8;

    /**
     * parity
     */
    public static final byte FT230X_PARITY = 0;

    /**
     * flow control
     */
    public static final byte FT230X_FLOW_CONTROL = 0;


    /**
     * 一帧包含的字节数量
     */
    public static final int FRAME_COUNT = 16;
    /**
     * 导联数量
     */
    public static final int LEAD_COUNT = 8;  //导联数量

    /**
     * 需要显示的总共导联数量
     */
    public static final int CUEERNT_LEAD_COUNT = 12;


}
