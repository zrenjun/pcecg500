package com.Carewell.OmniEcg.jni;


/**
 * Created by wxd
 */

public class JniFilter {

    private static JniFilter instance = null;

    private int powerFrequency50attenuation = 0;//工频可以，震铃不可以。0 关闭；1 开启50hz滤波

    static {
        System.loadLibrary("ecg_filter");
    }

    private JniFilter() {

    }

    public static JniFilter getInstance() {
        if (instance == null) {
            instance = new JniFilter();
        }
        return instance;
    }

    //==================================================交流 工频

    /**
     * 工频不可以，震铃可以
     * @param ecgDataArray
     * @param dataLen
     * @param gainValue
     * @param clearStart
     * @param notifyFilter
     */
    public native void powerFrequency50(float[][] ecgDataArray, int dataLen, float gainValue, boolean clearStart, NotifyFilter notifyFilter);

    /**
     * 工频可以，震铃不可以
     * @param ecgDataArray
     * @param dataLen
     * @param gainValue
     * @param clearStart
     * @param notifyFilter
     */
    public native void powerFrequency50attenuation(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void powerFrequency60(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    //===========================肌电  这个滤波开了，就不调用低通了。肌电和低通是一种滤波   传的数据不能小于400.前后各200个点，中间取多少传多少
    public native void electromyography25(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void electromyography35(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void electromyography45(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    //===========================低通
    public native void lowPass75(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void lowPass90(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void lowPass100(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    public native void lowPass165(float[][] ecgDataArray,int dataLen,float gainValue,boolean clearStart,NotifyFilter notifyFilter);

    //==================

    public int getPowerFrequency50attenuation() {
        return powerFrequency50attenuation;
    }

    public void setPowerFrequency50attenuation(int powerFrequency50attenuation) {
        this.powerFrequency50attenuation = powerFrequency50attenuation;
    }
}
