package com.lepu.ecg500.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;


import java.io.Serializable;

/**
 * 通讯设置
 */
public class CommunSettingBean implements Parcelable, Serializable {
    /**
     * 服务器IP
     */
    private String serverIP;
    /**
     * 服务器端口
     */
    private String serverPort;
    /**
     * FTP服务器IP
     */
    private String ftpServerIP;
    /**
     * FTP服务器端口
     */
    private String ftpServerPort;
    /**
     * FTP 用户名
     */
    private String ftpUserName;

    /**
     * FTP密码
     */
    private String ftpPassword;
    /**
     * 网卡ip类型
     */
    private CommunSettingEnum.EthernetIpConfig ipConfig;

    /**
     * ai分析平台
     */
    private CommunSettingEnum.AiServer aiServer;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serverIP);
        dest.writeString(this.serverPort);
        dest.writeString(this.ftpServerIP);
        dest.writeString(this.ftpServerPort);
        dest.writeString(this.ftpUserName);
        dest.writeString(this.ftpPassword);
        dest.writeInt(this.ipConfig == null ? -1 : this.ipConfig.ordinal());
        dest.writeInt(this.aiServer == null ? -1 : this.aiServer.ordinal());
    }

    public CommunSettingBean() {
    }

    protected CommunSettingBean(Parcel in) {
        this.serverIP = in.readString();
        String port = in.readString();
        this.serverPort = TextUtils.isEmpty(port) ? "" : port;
        this.ftpServerIP = in.readString();
        this.ftpServerPort = in.readString();
        this.ftpUserName = in.readString();
        this.ftpPassword = in.readString();
        int tmpIpConfig = in.readInt();
        this.ipConfig = tmpIpConfig == -1 ? null : CommunSettingEnum.EthernetIpConfig.values()[tmpIpConfig];
        int tmpAiServer = in.readInt();
        this.aiServer = tmpAiServer == -1 ? null : CommunSettingEnum.AiServer.values()[tmpAiServer];
    }

    public static final Creator<CommunSettingBean> CREATOR = new Creator<CommunSettingBean>() {
        @Override
        public CommunSettingBean createFromParcel(Parcel source) {
            return new CommunSettingBean(source);
        }

        @Override
        public CommunSettingBean[] newArray(int size) {
            return new CommunSettingBean[size];
        }
    };

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getFtpServerIP() {
        return ftpServerIP;
    }

    public void setFtpServerIP(String ftpServerIP) {
        this.ftpServerIP = ftpServerIP;
    }

    public String getFtpServerPort() {
        return ftpServerPort;
    }

    public void setFtpServerPort(String ftpServerPort) {
        this.ftpServerPort = ftpServerPort;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        this.ftpPassword = ftpPassword;
    }

    public CommunSettingEnum.EthernetIpConfig getIpConfig() {
        return ipConfig;
    }

    public void setIpConfig(CommunSettingEnum.EthernetIpConfig ipConfig) {
        this.ipConfig = ipConfig;
    }

    public CommunSettingEnum.AiServer getAiServer() {
        return aiServer;
    }

    public void setAiServer(CommunSettingEnum.AiServer aiServer) {
        this.aiServer = aiServer;
    }
}
