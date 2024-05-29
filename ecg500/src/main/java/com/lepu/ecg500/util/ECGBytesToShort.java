package com.lepu.ecg500.util;


import com.Carewell.OmniEcg.jni.NotifyFilter;
import com.Carewell.OmniEcg.jni.JniFilter;

public class ECGBytesToShort {

    public static final float SHORT_MV_GAIN = 1;

    private final com.Carewell.Data.Filter filter;
    private int index = 0;

    private boolean powerFrequencyChange = true;

    public ECGBytesToShort() {
        filter = new com.Carewell.Data.Filter();
    }

    /**
     * //7F(头) 81(Type 8导联) 0A(加密+循环(0-F判断是否丢包)) 00 00 06 00 06 00 FA FF 07 00 04 00 06 00 07 00(16byte=8x2) 00(导联脱落位) 00(起搏信号) 27
     * 循环获取，22个字节一帧，  最后一个字节为前21个字节和的后8位。
     */
    public short[][] bytesToLeadData(byte[] readBuffer, int actualNum) {
        short[][] shortBuffer = new short[CommConst.CUEERNT_LEAD_COUNT]
                [actualNum / CommConst.FRAME_COUNT];
        byte[] one_frame_buffer = new byte[CommConst.FRAME_COUNT];
        boolean isStart = false;
        int index = 0;
        short last = 0;
        int LEAD_GROUP_INDEX = 0;  //每次调用都需要清零
        for (int i = 0; i < actualNum; i++) {
            if (i < actualNum - 1 && readBuffer[i] == 0x7f) {
                isStart = true;
            }
            if (isStart) {
                one_frame_buffer[index] = readBuffer[i];
                if (index < CommConst.FRAME_COUNT - 1) {
                    last += (short) (readBuffer[i] & 0xff);
                    index++;
                } else {
                    isStart = false;
                    byte calLast = short8ToByte(last);
                    last = 0;
                    index = 0;
                    if (calLast == readBuffer[i]) {
                        parseWaveform(one_frame_buffer, shortBuffer, LEAD_GROUP_INDEX);
                        LEAD_GROUP_INDEX++;
                    } else {
                        continue;
                    }
                    one_frame_buffer = new byte[CommConst.FRAME_COUNT];
                }
            }
        }
        return shortBuffer;
    }

    /**
     * 清除过滤计数缓冲
     */
    public void clearFilterWave() {
        index = 0;
        filter.LowPass75HzFilterFree();
        filter.LowPass90HzFilterFree();
        filter.LowPass100HzFilterFree();
        filter.LowPass165HzFilterFree();
        //肌电滤波
        filter.EMG25FilterFree();
        filter.EMG35FilterFree();
        filter.EMG45FilterFree();
        //工频滤波
        filter.HUMFilterFree();
    }

    /**
     * 波形数据解析函数
     */
    private void parseWaveform(byte[] one_frame_buffer, short[][] leads_data, int i) {
        int leads_data_begin = 3;
        for (int j = 0; j < CommConst.LEAD_COUNT ; j++) {
            int low = one_frame_buffer[leads_data_begin + j * 2] & 0xff;
            int high = one_frame_buffer[leads_data_begin + j * 2 + 1] & 0xff << 8;
            leads_data[j][i] = (short) (low + high);
        }
        parseWaveOther4(leads_data, i);
    }

    /**
     * 其他4个派生导联计算方式
     */
    public void parseWaveOther4(short[][] leads_data, int i) {
        final short value = 2;
        //III = II - I
        leads_data[CommConst.LEAD_COUNT][i] = (short) (leads_data[1][i] - leads_data[0][i]);
        //aVR = -(I + II) / 2;
        leads_data[CommConst.LEAD_COUNT + 1][i] = (short) (-(leads_data[1][i] + leads_data[0][i]) / value);
        //aVF = (I - III) / 2;
        leads_data[CommConst.LEAD_COUNT + 2][i] = (short) ((leads_data[0][i] - leads_data[CommConst.LEAD_COUNT][i]) / value);
        //aVL = (II + III) / 2;
        leads_data[CommConst.LEAD_COUNT + 3][i] = (short) ((leads_data[1][i] + leads_data[CommConst.LEAD_COUNT][i]) / value);

    }

    /**
     * 将12导的顺序由 I/II/V1/V2/V3/V4/V5/V6/III/aVR/aVL/aVF转换为
     * I/II/III/aVR/aVL/aVF/V1/V2/V3/V4/V5/V6
     */
    public short[][] leadSortThe12(short[][] leadsData) {
        if (leadsData.length == CommConst.CUEERNT_LEAD_COUNT) {
            short[][] tempData = new short[CommConst.CUEERNT_LEAD_COUNT][];
            for (int i = 0; i < leadsData.length; i++) {
                if (i <= 1) {
                    tempData[i] = leadsData[i];
                } else if (i + 4 < leadsData.length) {
                    tempData[i + 4] = leadsData[i];
                } else {
                    tempData[i - 6] = leadsData[i];   //前面两导保持原样，充第三导开始转换
                }
            }
            return tempData;
        }
        return leadsData;
    }


    /**
     * 循环数组，过滤信号
     */
    public short[][] filterLeadsData(short[][] leadsData, int lp,  int ac)   {
        for (int i = 0; i < leadsData[0].length; i++) {
            for (int j = 0; j < leadsData.length; j++) {
                //低通滤波
                if (lp == CommConst.FILTER_LOW_PASS_75) {
                    leadsData[j][i] = filter.LowPass75HzFilter(j, leadsData[j][i], index);
                } else if (lp == CommConst.FILTER_LOW_PASS_90) {
                    leadsData[j][i] = filter.LowPass90HzFilter(j, leadsData[j][i], index);
                } else if (lp == CommConst.FILTER_LOW_PASS_100) {
                    leadsData[j][i] = filter.LowPass100HzFilter(j, leadsData[j][i], index);
                } else if (lp == CommConst.FILTER_LOW_PASS_165) {
                    leadsData[j][i] = filter.LowPass165HzFilter(j, leadsData[j][i], index);
                }
                //肌电滤波
//                if (setting.getEMGHz() == CommConst.FILTER_EMG_25) {
//                    leadsData[j][i] = filter.EMG25Filter(j, leadsData[j][i], index);
//                } else if (setting.getEMGHz() == CommConst.FILTER_EMG_35) {
//                    leadsData[j][i] = filter.EMG35Filter(j, leadsData[j][i], index);
//                } else if (setting.getEMGHz() == CommConst.FILTER_EMG_45) {
//                    leadsData[j][i] = filter.EMG45Filter(j, leadsData[j][i], index);
//                }
            }
            index++;
        }
        //新的工频滤波
        if (ac == CommConst.FILTER_HUM_50) {
            int inputDataCount = leadsData[0].length;
            JniFilter jniFilter = JniFilter.getInstance();
            NotifyFilter notifyFilter = new NotifyFilter();
            notifyFilter.setOutDataLen(inputDataCount);
            float[][] mvDataArray = switchEcgDataArray(leadsData);

            notifyFilter.setDataArray(mvDataArray);
            boolean powerF = isPowerFrequencyChange();

            //50hz 工频
            jniFilter.powerFrequency50attenuation(mvDataArray,mvDataArray[0].length,1.0f,powerF,notifyFilter);
            mvDataArray = notifyFilter.getDataArray();

            leadsData = switchEcgDataArray(mvDataArray);
            setPowerFrequencyChange(false);
        }
        return leadsData;
    }



    /**
     * 将16位的short的低八位转换成byte
     */
    public byte short8ToByte(short s) {
        return (byte) (s & 0xff);
    }

    public boolean isPowerFrequencyChange() {
        return powerFrequencyChange;
    }

    public void setPowerFrequencyChange(boolean powerFrequencyChange) {
        this.powerFrequencyChange = powerFrequencyChange;
    }

    public static float[][] switchEcgDataArray(short[][] dataArray) {
        if (dataArray == null) {
            return null;
        }
        float[][] valueDataArray = new float[dataArray.length][dataArray[0].length];
        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[0].length; j++) {
                short value = dataArray[i][j];
                valueDataArray[i][j] = value * SHORT_MV_GAIN;
            }
        }
        return valueDataArray;
    }

    public static short[][] switchEcgDataArray(float[][] dataArray) {
        if (dataArray == null) {
            return null;
        }

        short[][] valueDataArray = new short[dataArray.length][dataArray[0].length];
        for (int i = 0; i < dataArray.length; i++) {
            for (int j = 0; j < dataArray[0].length; j++) {
                float value = dataArray[i][j];
                valueDataArray[i][j] = (short) (value / SHORT_MV_GAIN);
            }
        }
        return valueDataArray;
    }
}
