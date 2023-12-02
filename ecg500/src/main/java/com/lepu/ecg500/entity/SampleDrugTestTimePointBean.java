package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class SampleDrugTestTimePointBean implements Parcelable, Serializable {
    //是否选中
    private boolean value;
    private SampleSettingConfigEnum.DrugTestSamplePointsMode drugTestSamplePointsMode;

    public SampleDrugTestTimePointBean(boolean value, SampleSettingConfigEnum.DrugTestSamplePointsMode drugTestSamplePointsMode){
        this.value = value;
        this.drugTestSamplePointsMode = drugTestSamplePointsMode;
    }

    protected SampleDrugTestTimePointBean(Parcel in) {
        value = in.readByte() != 0;
        drugTestSamplePointsMode = SampleSettingConfigEnum.DrugTestSamplePointsMode.values()[in.readInt()];
    }

    public static final Creator<SampleDrugTestTimePointBean> CREATOR = new Creator<SampleDrugTestTimePointBean>() {
        @Override
        public SampleDrugTestTimePointBean createFromParcel(Parcel in) {
            return new SampleDrugTestTimePointBean(in);
        }

        @Override
        public SampleDrugTestTimePointBean[] newArray(int size) {
            return new SampleDrugTestTimePointBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (value ? 1 : 0));
        dest.writeInt(drugTestSamplePointsMode.ordinal());
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public SampleSettingConfigEnum.DrugTestSamplePointsMode getDrugTestSamplePointsMode() {
        return drugTestSamplePointsMode;
    }

    public void setDrugTestSamplePointsMode(SampleSettingConfigEnum.DrugTestSamplePointsMode drugTestSamplePointsMode) {
        this.drugTestSamplePointsMode = drugTestSamplePointsMode;
    }
}
