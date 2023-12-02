package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class PatientInfoConfigBean implements Parcelable, Serializable {
    //是否选中
    private boolean value;
    private PatientSettingConfigEnum.PatientInfoConfigType patientInfoConfigType;

    public PatientInfoConfigBean(boolean value, PatientSettingConfigEnum.PatientInfoConfigType patientInfoConfigType) {
       this.value = value;
       this.patientInfoConfigType = patientInfoConfigType;
    }

    protected PatientInfoConfigBean(Parcel in) {
        value = in.readByte() != 0;
        int index = in.readInt();
        PatientSettingConfigEnum.PatientInfoConfigType[] values = PatientSettingConfigEnum.PatientInfoConfigType.values();
        if (index >= 0 && index < values.length){
            patientInfoConfigType = values[index];
        }else {
            patientInfoConfigType = values[0];
        }
    }

    public static final Creator<PatientInfoConfigBean> CREATOR = new Creator<PatientInfoConfigBean>() {
        @Override
        public PatientInfoConfigBean createFromParcel(Parcel in) {
            return new PatientInfoConfigBean(in);
        }

        @Override
        public PatientInfoConfigBean[] newArray(int size) {
            return new PatientInfoConfigBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (value ? 1 : 0));
        dest.writeInt(patientInfoConfigType.ordinal());
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public PatientSettingConfigEnum.PatientInfoConfigType getPatientInfoConfigType() {
        return patientInfoConfigType;
    }

    public void setPatientInfoConfigType(PatientSettingConfigEnum.PatientInfoConfigType patientInfoConfigType) {
        this.patientInfoConfigType = patientInfoConfigType;
    }
}
