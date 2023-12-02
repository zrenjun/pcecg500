package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;


import java.io.Serializable;
import java.util.List;

public class RecordSettingBean implements Parcelable, Serializable {

    /**
     * 手动记录格式
     */
    private RecordSettingConfigEnum.ChannelType channelType;

    /**
     * 记录模式
     */
    private RecordSettingConfigEnum.RecordMode recordMode;
    /**
     * 记录顺序
     */
    private RecordSettingConfigEnum.RecordOrderType recordOrderType;
    /**
     * 打印设备
     */
    private RecordSettingConfigEnum.PrintDeviceType printDeviceType;

    /**
     * 网络打印机IP
     */
    private String netWorkPrinterIP;
    /**
     * 网络打印机端口
     */
    private String netWorkPrinterPort;
    /**
     * 是否打印网格
     */
    private boolean printGrid;
    /**
     * 打印预览
     */
    private boolean printPreview;

    /**
     * 打印输出
     */
    private boolean printOutput;
    /**
     * 报告设置
     */

    private List<ReportSettingBean> reportSettingBeanList;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.channelType == null ? -1 : this.channelType.ordinal());
        dest.writeInt(this.recordMode == null ? -1 : this.recordMode.ordinal());
        dest.writeInt(this.recordOrderType == null ? -1 : this.recordOrderType.ordinal());
        dest.writeInt(this.printDeviceType == null ? -1 : this.printDeviceType.ordinal());
        dest.writeString(this.netWorkPrinterIP);
        dest.writeString(this.netWorkPrinterPort);
        dest.writeByte(this.printGrid ? (byte) 1 : (byte) 0);
        dest.writeByte(this.printPreview ? (byte) 1 : (byte) 0);
        dest.writeByte(this.printOutput ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.reportSettingBeanList);
    }

    public RecordSettingBean() {
    }

    protected RecordSettingBean(Parcel in) {
        int tmpChannelType = in.readInt();
        this.channelType = tmpChannelType == -1 ? null : RecordSettingConfigEnum.ChannelType.values()[tmpChannelType];
        int tmpRecordMode = in.readInt();
        this.recordMode = tmpRecordMode == -1 ? null : RecordSettingConfigEnum.RecordMode.values()[tmpRecordMode];
        int tmpRecordOrderType = in.readInt();
        this.recordOrderType = tmpRecordOrderType == -1 ? null : RecordSettingConfigEnum.RecordOrderType.values()[tmpRecordOrderType];
        int tmpPrintDeviceType = in.readInt();
        this.printDeviceType = tmpPrintDeviceType == -1 ? null : RecordSettingConfigEnum.PrintDeviceType.values()[tmpPrintDeviceType];
        this.netWorkPrinterIP = in.readString();
        this.netWorkPrinterPort = in.readString();
        this.printGrid = in.readByte() != 0;
        this.printPreview = in.readByte() != 0;
        this.printOutput = in.readByte() != 0;
        this.reportSettingBeanList = in.createTypedArrayList(ReportSettingBean.CREATOR);
    }

    public static final Creator<RecordSettingBean> CREATOR = new Creator<RecordSettingBean>() {
        @Override
        public RecordSettingBean createFromParcel(Parcel source) {
            return new RecordSettingBean(source);
        }

        @Override
        public RecordSettingBean[] newArray(int size) {
            return new RecordSettingBean[size];
        }
    };

    public RecordSettingConfigEnum.ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(RecordSettingConfigEnum.ChannelType channelType) {
        this.channelType = channelType;
    }

    public RecordSettingConfigEnum.RecordMode getRecordMode() {
        return recordMode;
    }

    public void setRecordMode(RecordSettingConfigEnum.RecordMode recordMode) {
        this.recordMode = recordMode;
    }

    public RecordSettingConfigEnum.RecordOrderType getRecordOrderType() {
        return recordOrderType;
    }

    public void setRecordOrderType(RecordSettingConfigEnum.RecordOrderType recordOrderType) {
        this.recordOrderType = recordOrderType;
    }

    public RecordSettingConfigEnum.PrintDeviceType getPrintDeviceType() {
        return printDeviceType;
    }

    public void setPrintDeviceType(RecordSettingConfigEnum.PrintDeviceType printDeviceType) {
        this.printDeviceType = printDeviceType;
    }

    public String getNetWorkPrinterIP() {
        return netWorkPrinterIP;
    }

    public void setNetWorkPrinterIP(String netWorkPrinterIP) {
        this.netWorkPrinterIP = netWorkPrinterIP;
    }

    public String getNetWorkPrinterPort() {
        return netWorkPrinterPort;
    }

    public void setNetWorkPrinterPort(String netWorkPrinterPort) {
        this.netWorkPrinterPort = netWorkPrinterPort;
    }

    public boolean isPrintGrid() {
        return printGrid;
    }

    public void setPrintGrid(boolean printGrid) {
        this.printGrid = printGrid;
    }

    public boolean isPrintPreview() {
        return printPreview;
    }

    public void setPrintPreview(boolean printPreview) {
        this.printPreview = printPreview;
    }

    public boolean isPrintOutput() {
        return printOutput;
    }

    public void setPrintOutput(boolean printOutput) {
        this.printOutput = printOutput;
    }

    public List<ReportSettingBean> getReportSettingBeanList() {
        return reportSettingBeanList;
    }

    public void setReportSettingBeanList(List<ReportSettingBean> reportSettingBeanList) {
        this.reportSettingBeanList = reportSettingBeanList;
    }
}
