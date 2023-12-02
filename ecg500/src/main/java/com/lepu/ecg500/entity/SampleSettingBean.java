package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.util.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 采样设置
 */
public class SampleSettingBean implements Parcelable, Serializable {

    /**
     * 自动采样时间
     */
    private SampleSettingConfigEnum.RealtimeSampleTimeMode realtimeSampleTimeMode;
    /**
     * 周期采样时间
     */
    private int cycleSampleTime;
    /**
     * 周期采样间隔
     */
    private int cycleSampleInterval;

    /**
     * HRV采样时间
     */
    private SampleSettingConfigEnum.HrvSampleTimeMode hrvSampleTimeMode;

    /**
     * RR采样时间
     */
    private SampleSettingConfigEnum.RRSampleTimeMode rrSampleTimeMode;

    /**
     * 药物试验时间点
     */
    private List<SampleDrugTestTimePointBean> drugTestPointList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.realtimeSampleTimeMode == null ? -1 : this.realtimeSampleTimeMode.ordinal());
        dest.writeInt(this.cycleSampleTime);
        dest.writeInt(this.cycleSampleInterval);
        dest.writeInt(this.hrvSampleTimeMode == null ? -1 : this.hrvSampleTimeMode.ordinal());
        dest.writeInt(this.rrSampleTimeMode == null ? -1 : this.rrSampleTimeMode.ordinal());
        dest.writeTypedList(this.drugTestPointList);
    }

    public SampleSettingBean() {
    }

    protected SampleSettingBean(Parcel in) {
        int tmpRealtimeSampleTimeMode = in.readInt();
        this.realtimeSampleTimeMode = tmpRealtimeSampleTimeMode == -1 ? null : SampleSettingConfigEnum.RealtimeSampleTimeMode.values()[tmpRealtimeSampleTimeMode];
        this.cycleSampleTime = in.readInt();
        this.cycleSampleInterval = in.readInt();
        int tmpHrvSampleTimeMode = in.readInt();
        this.hrvSampleTimeMode = tmpHrvSampleTimeMode == -1 ? null : SampleSettingConfigEnum.HrvSampleTimeMode.values()[tmpHrvSampleTimeMode];
        int tmpRrSampleTimeMode = in.readInt();
        this.rrSampleTimeMode = tmpRrSampleTimeMode == -1 ? null : SampleSettingConfigEnum.RRSampleTimeMode.values()[tmpRrSampleTimeMode];
        this.drugTestPointList = in.createTypedArrayList(SampleDrugTestTimePointBean.CREATOR);
    }

    public static final Creator<SampleSettingBean> CREATOR = new Creator<SampleSettingBean>() {
        @Override
        public SampleSettingBean createFromParcel(Parcel source) {
            return new SampleSettingBean(source);
        }

        @Override
        public SampleSettingBean[] newArray(int size) {
            return new SampleSettingBean[size];
        }
    };

    public SampleSettingConfigEnum.RealtimeSampleTimeMode getRealtimeSampleTimeMode() {
        return realtimeSampleTimeMode;
    }

    public void setRealtimeSampleTimeMode(SampleSettingConfigEnum.RealtimeSampleTimeMode realtimeSampleTimeMode) {
        this.realtimeSampleTimeMode = realtimeSampleTimeMode;
    }

    public int getCycleSampleTime() {
        return cycleSampleTime;
    }

    public void setCycleSampleTime(int cycleSampleTime) {
        this.cycleSampleTime = cycleSampleTime;
    }

    public int getCycleSampleInterval() {
        return cycleSampleInterval;
    }

    public void setCycleSampleInterval(int cycleSampleInterval) {
        this.cycleSampleInterval = cycleSampleInterval;
    }

    public SampleSettingConfigEnum.HrvSampleTimeMode getHrvSampleTimeMode() {
        return hrvSampleTimeMode;
    }

    public void setHrvSampleTimeMode(SampleSettingConfigEnum.HrvSampleTimeMode hrvSampleTimeMode) {
        this.hrvSampleTimeMode = hrvSampleTimeMode;
    }

    public SampleSettingConfigEnum.RRSampleTimeMode getRrSampleTimeMode() {
        return rrSampleTimeMode;
    }

    public void setRrSampleTimeMode(SampleSettingConfigEnum.RRSampleTimeMode rrSampleTimeMode) {
        this.rrSampleTimeMode = rrSampleTimeMode;
    }

    public List<SampleDrugTestTimePointBean> getDrugTestPointList() {
        return drugTestPointList;
    }

    public void setDrugTestPointList(List<SampleDrugTestTimePointBean> drugTestPointList) {
        this.drugTestPointList = drugTestPointList;
    }

    public static List<SampleDrugTestTimePointBean> parseDrugTestPointList(String drugTestPoints) {
        if(!TextUtils.isEmpty(drugTestPoints)){
            List<SampleDrugTestTimePointBean> drugTestPointList = new ArrayList<>();
            String typeArray[] = drugTestPoints.split(",");
            for(int i=0;i<typeArray.length;i++){
                int type = Util.parseInt(typeArray[i]);
                SampleSettingConfigEnum.DrugTestSamplePointsMode drugTestSamplePointsMode = SampleSettingConfigEnum.DrugTestSamplePointsMode.values()[type];
                SampleDrugTestTimePointBean item = new SampleDrugTestTimePointBean(true,drugTestSamplePointsMode);
                drugTestPointList.add(item);
            }
            CustomTool.d(String.format("解析赋值，药物试验时间点 类型 %s",drugTestPoints));
            return drugTestPointList;
        }
        return null;
    }
}
