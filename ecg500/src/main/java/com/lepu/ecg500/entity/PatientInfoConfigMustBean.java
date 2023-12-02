package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class PatientInfoConfigMustBean implements Parcelable, Serializable {
    //是否选中
    private boolean value;
    private PatientSettingConfigEnum.PatientInfoConfigMustType patientInfoConfigMustType;

    public PatientInfoConfigMustBean(boolean value, PatientSettingConfigEnum.PatientInfoConfigMustType patientInfoConfigMustType) {
       this.value = value;
       this.patientInfoConfigMustType = patientInfoConfigMustType;
    }

    protected PatientInfoConfigMustBean(Parcel in) {
        value = in.readByte() != 0;
        int index = in.readInt();
        PatientSettingConfigEnum.PatientInfoConfigMustType[] values = PatientSettingConfigEnum.PatientInfoConfigMustType.values();
        if (index >= 0 && index < values.length){
            patientInfoConfigMustType = values[index];
        }else {
            patientInfoConfigMustType = values[0];
        }
    }

    public static final Creator<PatientInfoConfigMustBean> CREATOR = new Creator<PatientInfoConfigMustBean>() {
        @Override
        public PatientInfoConfigMustBean createFromParcel(Parcel in) {
            return new PatientInfoConfigMustBean(in);
        }

        @Override
        public PatientInfoConfigMustBean[] newArray(int size) {
            return new PatientInfoConfigMustBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (value ? 1 : 0));
        dest.writeInt(patientInfoConfigMustType.ordinal());
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public PatientSettingConfigEnum.PatientInfoConfigMustType getPatientInfoConfigMustType() {
        return patientInfoConfigMustType;
    }

    public void setPatientInfoConfigMustType(PatientSettingConfigEnum.PatientInfoConfigMustType patientInfoConfigMustType) {
        this.patientInfoConfigMustType = patientInfoConfigMustType;
    }
}
