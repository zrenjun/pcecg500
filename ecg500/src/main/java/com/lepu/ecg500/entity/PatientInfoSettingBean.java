package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.List;

/**
 * 患者信息设置
 */
public class PatientInfoSettingBean implements Parcelable, Serializable {

    /**
     *必选患者信息配置
     */
    private List<PatientInfoConfigMustBean> patientInfoConfigMustBeanList;
    /**
     *患者信息配置
     */
    private List<PatientInfoConfigBean> patientInfoConfigBeanList;

    /**
     * 唯一识别码
     */
    private PatientSettingConfigEnum.UniqueCodeType uniqueCodeType;



    /**
     * 身高/体重
     */
    private PatientSettingConfigEnum.HeightWeightUnit heightWeightUnit;
    /**
     * 血压单位
     */
    private PatientSettingConfigEnum.BloodPressureUnit bloodPressureUnit;
    /**
     * id生成方式
     */
    private PatientSettingConfigEnum.IdMakeType idMakeType;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.patientInfoConfigMustBeanList);
        dest.writeTypedList(this.patientInfoConfigBeanList);
        dest.writeInt(this.uniqueCodeType == null ? -1 : this.uniqueCodeType.ordinal());
        dest.writeInt(this.heightWeightUnit == null ? -1 : this.heightWeightUnit.ordinal());
        dest.writeInt(this.bloodPressureUnit == null ? -1 : this.bloodPressureUnit.ordinal());
        dest.writeInt(this.idMakeType == null ? -1 : this.idMakeType.ordinal());
    }

    public PatientInfoSettingBean() {
    }

    protected PatientInfoSettingBean(Parcel in) {
        this.patientInfoConfigMustBeanList = in.createTypedArrayList(PatientInfoConfigMustBean.CREATOR);
        this.patientInfoConfigBeanList = in.createTypedArrayList(PatientInfoConfigBean.CREATOR);
        int tmpUniqueCodeType = in.readInt();
        this.uniqueCodeType = tmpUniqueCodeType == -1 ? null : PatientSettingConfigEnum.UniqueCodeType.values()[tmpUniqueCodeType];
        int tmpHeightWeightUnit = in.readInt();
        this.heightWeightUnit = tmpHeightWeightUnit == -1 ? null : PatientSettingConfigEnum.HeightWeightUnit.values()[tmpHeightWeightUnit];
        int tmpBloodPressureUnit = in.readInt();
        this.bloodPressureUnit = tmpBloodPressureUnit == -1 ? null : PatientSettingConfigEnum.BloodPressureUnit.values()[tmpBloodPressureUnit];
        int tmpIdMakeType = in.readInt();
        this.idMakeType = tmpIdMakeType == -1 ? null : PatientSettingConfigEnum.IdMakeType.values()[tmpIdMakeType];
    }

    public static final Creator<PatientInfoSettingBean> CREATOR = new Creator<PatientInfoSettingBean>() {
        @Override
        public PatientInfoSettingBean createFromParcel(Parcel source) {
            return new PatientInfoSettingBean(source);
        }

        @Override
        public PatientInfoSettingBean[] newArray(int size) {
            return new PatientInfoSettingBean[size];
        }
    };

    public List<PatientInfoConfigMustBean> getPatientInfoConfigMustBeanList() {
        return patientInfoConfigMustBeanList;
    }

    public void setPatientInfoConfigMustBeanList(List<PatientInfoConfigMustBean> patientInfoConfigMustBeanList) {
        this.patientInfoConfigMustBeanList = patientInfoConfigMustBeanList;
    }

    public List<PatientInfoConfigBean> getPatientInfoConfigBeanList() {
        return patientInfoConfigBeanList;
    }

    public void setPatientInfoConfigBeanList(List<PatientInfoConfigBean> patientInfoConfigBeanList) {
        this.patientInfoConfigBeanList = patientInfoConfigBeanList;
    }

    public PatientSettingConfigEnum.UniqueCodeType getUniqueCodeType() {
        return uniqueCodeType;
    }

    public void setUniqueCodeType(PatientSettingConfigEnum.UniqueCodeType uniqueCodeType) {
        this.uniqueCodeType = uniqueCodeType;
    }

    public PatientSettingConfigEnum.HeightWeightUnit getHeightWeightUnit() {
        return heightWeightUnit;
    }

    public void setHeightWeightUnit(PatientSettingConfigEnum.HeightWeightUnit heightWeightUnit) {
        this.heightWeightUnit = heightWeightUnit;
    }

    public PatientSettingConfigEnum.BloodPressureUnit getBloodPressureUnit() {
        return bloodPressureUnit;
    }

    public void setBloodPressureUnit(PatientSettingConfigEnum.BloodPressureUnit bloodPressureUnit) {
        this.bloodPressureUnit = bloodPressureUnit;
    }

    public PatientSettingConfigEnum.IdMakeType getIdMakeType() {
        return idMakeType;
    }

    public void setIdMakeType(PatientSettingConfigEnum.IdMakeType idMakeType) {
        this.idMakeType = idMakeType;
    }
}
