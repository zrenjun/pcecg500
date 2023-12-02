package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class ReportSettingBean implements Parcelable, Serializable {
    //是否选中
    private boolean value;
    private RecordSettingConfigEnum.ReportSetting reportSetting;

    public ReportSettingBean(boolean value, RecordSettingConfigEnum.ReportSetting reportSetting) {
        this.value = value;
        this.reportSetting = reportSetting;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.value ? (byte) 1 : (byte) 0);
        dest.writeInt(this.reportSetting == null ? -1 : this.reportSetting.ordinal());
    }

    protected ReportSettingBean(Parcel in) {
        this.value = in.readByte() != 0;
        int tmpReportSetting = in.readInt();
        this.reportSetting = tmpReportSetting == -1 ? null : RecordSettingConfigEnum.ReportSetting.values()[tmpReportSetting];
    }

    public static final Creator<ReportSettingBean> CREATOR = new Creator<ReportSettingBean>() {
        @Override
        public ReportSettingBean createFromParcel(Parcel source) {
            return new ReportSettingBean(source);
        }

        @Override
        public ReportSettingBean[] newArray(int size) {
            return new ReportSettingBean[size];
        }
    };

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public RecordSettingConfigEnum.ReportSetting getReportSetting() {
        return reportSetting;
    }

    public void setReportSetting(RecordSettingConfigEnum.ReportSetting reportSetting) {
        this.reportSetting = reportSetting;
    }

}
