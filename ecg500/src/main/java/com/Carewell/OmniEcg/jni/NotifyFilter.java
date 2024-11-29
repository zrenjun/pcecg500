package com.Carewell.OmniEcg.jni;

/**
 * Created by wxd on 2019-05-24.
 */

public class NotifyFilter {

    private boolean filterSuccess;//滤波成功，使用滤波的数据。否则使用原始的数据。目前 工频滤波使用。低通 肌电，不用这个返回值了
    private float[][] dataArray;//mv数据
    private int outDataLen;//输出数据长度

    public boolean isFilterSuccess() {
        return filterSuccess;
    }

    public void setFilterSuccess(boolean filterSuccess) {
        this.filterSuccess = filterSuccess;
    }

    public float[][] getDataArray() {
        return dataArray;
    }

    public void setDataArray(float[][] dataArray) {
        this.dataArray = dataArray;
    }

    public int getOutDataLen() {
        return outDataLen;
    }

    public void setOutDataLen(int outDataLen) {
        this.outDataLen = outDataLen;
    }

}
