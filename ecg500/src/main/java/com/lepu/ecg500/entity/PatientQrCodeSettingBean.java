package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class PatientQrCodeSettingBean implements Parcelable,Serializable  {
    private PatientSettingConfigEnum.PatientInfoConfigType patientInfoConfigType;
    private PatientSettingConfigEnum.PatientInfoConfigMustType patientInfoConfigMustType;
    /**
     * 如果是出生日期 以,隔开 年,月,日
     */
    private String startPos;
    private String endPos;

    public PatientSettingConfigEnum.PatientInfoConfigType getPatientInfoConfigType() {
        return patientInfoConfigType;
    }

    public void setPatientInfoConfigType(PatientSettingConfigEnum.PatientInfoConfigType patientInfoConfigType) {
        this.patientInfoConfigType = patientInfoConfigType;
    }

    public PatientSettingConfigEnum.PatientInfoConfigMustType getPatientInfoConfigMustType() {
        return patientInfoConfigMustType;
    }

    public void setPatientInfoConfigMustType(PatientSettingConfigEnum.PatientInfoConfigMustType patientInfoConfigMustType) {
        this.patientInfoConfigMustType = patientInfoConfigMustType;
    }

    public String getStartPos() {
        return startPos;
    }

    public void setStartPos(String startPos) {
        this.startPos = startPos;
    }

    public String getEndPos() {
        return endPos;
    }

    public void setEndPos(String endPos) {
        this.endPos = endPos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.patientInfoConfigType == null ? -1 : this.patientInfoConfigType.ordinal());
        dest.writeInt(this.patientInfoConfigMustType == null ? -1 : this.patientInfoConfigMustType.ordinal());
        dest.writeString(this.startPos);
        dest.writeString(this.endPos);
    }

    public PatientQrCodeSettingBean() {
    }

    protected PatientQrCodeSettingBean(Parcel in) {
        int tmpPatientInfoConfigType = in.readInt();
        this.patientInfoConfigType = tmpPatientInfoConfigType == -1 ? null : PatientSettingConfigEnum.PatientInfoConfigType.values()[tmpPatientInfoConfigType];
        int tmpPatientInfoConfigMustType = in.readInt();
        this.patientInfoConfigMustType = tmpPatientInfoConfigMustType == -1 ? null : PatientSettingConfigEnum.PatientInfoConfigMustType.values()[tmpPatientInfoConfigMustType];
        this.startPos = in.readString();
        this.endPos = in.readString();
    }

    public static final Creator<PatientQrCodeSettingBean> CREATOR = new Creator<PatientQrCodeSettingBean>() {
        @Override
        public PatientQrCodeSettingBean createFromParcel(Parcel source) {
            return new PatientQrCodeSettingBean(source);
        }

        @Override
        public PatientQrCodeSettingBean[] newArray(int size) {
            return new PatientQrCodeSettingBean[size];
        }
    };
}
