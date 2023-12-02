package com.lepu.ecg500.util;


import com.Carewell.OmniEcg.jni.NotifyFilter;
import com.lepu.ecg500.entity.SettingsBean;
import com.Carewell.OmniEcg.jni.JniFilter;

public class ECGBytesToShort {

    public static final float SHORT_MV_GAIN = 5 / 2048F;

    private com.Carewell.Data.Filter filter = null;
    private int index = 0;

    private boolean powerFrequencyChange = true;

    public ECGBytesToShort() {
        filter = new com.Carewell.Data.Filter();
    }

    /**
     * 循环获取，16个字节一帧，16个字节中第一个字节值为0x0f，第二个字节告诉8个导联那几个脱落
     * 后面的12位依次代表8导联的数值，然后后面的一个字节值0xff，最后一个字节为前15个字节和的后8位。
     * <p>
     * 1+1+12+1+1
     *
     * @param readBuffer
     */
    public short[][] bytesToLeadData(byte[] readBuffer, int actualNum, SettingsBean setting) {
        short[][] shortBuffer = new short[CommConst.CUEERNT_LEAD_COUNT]
                [actualNum / CommConst.FRAME_COUNT];
        byte[] one_frame_buffer = new byte[CommConst.FRAME_COUNT];
        boolean isStart = false;
        int index = 0;
        short last = 0;
        int LEAD_GROUP_INDEX = 0;  //每次调用都需要清零
        for (int i = 0; i < actualNum; i++) {
            if (i < actualNum - 1 && readBuffer[i] == 0x0f/* && readBuffer[i + 1] == 0x01*/) {
                isStart = true;
            }
            if (isStart) {
                one_frame_buffer[index] = readBuffer[i];
                if (index < CommConst.FRAME_COUNT - 1) {
                    last += (readBuffer[i] & 0xff);
                    index++;
                } else {
                    isStart = false;
                    byte calLast = short8ToByte(last);
                    last = 0;
                    index = 0;
                    if (calLast == readBuffer[i]) {
                        ParseWaveform(one_frame_buffer, shortBuffer, LEAD_GROUP_INDEX, setting);
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
    public void ClearFilterWave() {
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
     *
     * @param one_frame_buffer
     * @param leads_data
     * @param i
     */
    private void ParseWaveform(byte[] one_frame_buffer, short[][] leads_data, int i, SettingsBean setting) {
        //原始波形数据的放大倍数。
        double avm = 2.556;

        //原始波形数据的基础值。
        int waveform_base_value = 0x800;    //计算正负100000000000(二进制)

        // one_frame_buffer 正好保存了一帧的数据。
        int leads_data_begin = 2;

        //一次计算两个导联的数据
        for (int j = 0; j < CommConst.LEAD_COUNT / 2; j++) {
            int k = j * 2;
            int m = j * 3;

            //循环计算I、V1、V3、V5导联。
            int low1 = one_frame_buffer[leads_data_begin + m] & 0xff;
            int high1 = one_frame_buffer[leads_data_begin + m + 1] & 0xff;
            high1 = high1 & 0x0F;
            high1 = high1 << 8;
            leads_data[k][i] = (short) (low1 + high1);
            leads_data[k][i] = (short) (leads_data[k][i] - waveform_base_value);
            leads_data[k][i] = new Double(leads_data[k][i] * avm).shortValue();

            //循环计算II、V2、V4、V6导联。
            int low2 = one_frame_buffer[leads_data_begin + m + 1] & 0xff;
            int high2 = one_frame_buffer[leads_data_begin + m + 2] & 0xff;
            low2 = low2 & 0xF0;
            low2 = low2 >> 4;
            high2 = high2 << 4;
            leads_data[k + 1][i] = (short) (low2 + high2);
            leads_data[k + 1][i] = (short) (leads_data[k + 1][i] - waveform_base_value);
            leads_data[k + 1][i] = new Double(leads_data[k + 1][i] * avm).shortValue();

        }
        ParseWaveOther4(leads_data, i);
    }

    /**
     * 其他4个派生导联计算方式
     *
     * @param leads_data
     * @param i
     */
    public void ParseWaveOther4(short[][] leads_data, int i) {
        final short value = 2;
        //III = II - I
        leads_data[CommConst.LEAD_COUNT][i] = (short) (leads_data[1][i] - leads_data[0][i]);
        //aVR = -(I + II) / 2;
        leads_data[CommConst.LEAD_COUNT + 1][i] = (short) (0 - (leads_data[1][i] + leads_data[0][i]) / value);
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
     *
     * @param leadsData
     * @param setting
     * @return
     */
    public short[][] filterLeadsData(short[][] leadsData, SettingsBean setting) {
        for (int i = 0; i < leadsData[0].length; i++) {
            for (int j = 0; j < leadsData.length; j++) {
                //低通滤波
                if (setting.getLowPassHz() == CommConst.FILTER_LOW_PASS_75) {
                    leadsData[j][i] = filter.LowPass75HzFilter(j, leadsData[j][i], index);
                } else if (setting.getLowPassHz() == CommConst.FILTER_LOW_PASS_90) {
                    leadsData[j][i] = filter.LowPass90HzFilter(j, leadsData[j][i], index);
                } else if (setting.getLowPassHz() == CommConst.FILTER_LOW_PASS_100) {
                    leadsData[j][i] = filter.LowPass100HzFilter(j, leadsData[j][i], index);
                } else if (setting.getLowPassHz() == CommConst.FILTER_LOW_PASS_165) {
                    leadsData[j][i] = filter.LowPass165HzFilter(j, leadsData[j][i], index);
                }
                //肌电滤波
                if (setting.getEMGHz() == CommConst.FILTER_EMG_25) {
                    leadsData[j][i] = filter.EMG25Filter(j, leadsData[j][i], index);
                } else if (setting.getEMGHz() == CommConst.FILTER_EMG_35) {
                    leadsData[j][i] = filter.EMG35Filter(j, leadsData[j][i], index);
                } else if (setting.getEMGHz() == CommConst.FILTER_EMG_45) {
                    leadsData[j][i] = filter.EMG45Filter(j, leadsData[j][i], index);
                }
            }
            index++;
        }
        //新的工频滤波
        if (setting.getHUMHz() == CommConst.FILTER_HUM_50) {
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


    /// <summary>
    /// FRANK导联转换法（8通道转换为X、Y、Z三通道数据）
    /// </summary>
    /// <param name="buffers"></param>
    /// <returns></returns>
    public short[][] FrankLeadData(short[][] buffers) {
        try {
            short[][] newBuffers = new short[3][];

            float Va, Vc, Vi, Vf, Vm, Vh, Ve, temp;

            int count = buffers[0].length;

            for (int i = 0; i < 3; i++) {
                newBuffers[i] = new short[count];
            }

            for (int i = 0; i < count; i++) {
                temp = (float) ((buffers[0][i] - 2 * buffers[1][i]) / 3.0);
                Va = buffers[5][i] + temp;
                Vc = buffers[4][i] + temp;
                Vi = buffers[2][i] + temp;
                Vf = 0;
                Vm = buffers[6][i] + temp;
                Vh = buffers[7][i] + temp;
                Ve = buffers[3][i] + temp;

                newBuffers[0][i] = (short) (0.61 * Va + 0.171 * Vc - 0.781 * Vi); //对应Ｘ导联数据
                newBuffers[1][i] = (short) (0.655 * Vf + 0.345 * Vm - 1.0 * Vh);//对应Ｙ导联数据
                newBuffers[2][i] = (short) (-(0.133 * Va + 0.736 * Vm - 0.264 * Vi - 0.374 * Ve - 0.231 * Vc));//对应Z导联数据
            }
            return newBuffers;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将16位的short的低八位转换成byte
     *
     * @param s short
     * @return byte[] 长度为2
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
