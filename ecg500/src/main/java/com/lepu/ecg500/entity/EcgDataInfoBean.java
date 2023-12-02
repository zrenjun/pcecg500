package com.lepu.ecg500.entity;

/**
 * @author wxd
 */
public class EcgDataInfoBean {

    private short[][] ecgDataArray;
    private EcgSettingConfigEnum.LeadType leadType;
    float[] gainArray;

    private EcgSettingConfigEnum.LeadFilterType filterLowpass;
    private EcgSettingConfigEnum.LeadFilterType filterEmg;
    private EcgSettingConfigEnum.LeadFilterType filterAds;
    private EcgSettingConfigEnum.LeadFilterType filterAcOpenState;
    private EcgSettingConfigEnum.LeadFilterType filterAc;

    private EcgSettingConfigEnum.LeadSpeedType leadSpeedType;
    private EcgSettingConfigEnum.LeadGainType leadGainType;

    public EcgDataInfoBean(){

    }

    /**
     *
     * @param ecgDataArray
     * @param gainArray
     * @param configBean
     * @return
     */
    public static EcgDataInfoBean makeEcgDataInfoBean(short[][] ecgDataArray, float[] gainArray, ConfigBean configBean){
        EcgSettingBean ecgSettingBean = configBean.getEcgSettingBean();
        SystemSettingBean systemSettingBean = configBean.getSystemSettingBean();
        EcgDataInfoBean ecgDataInfoBean = new EcgDataInfoBean();

        ecgDataInfoBean.setEcgDataArray(ecgDataArray);
        ecgDataInfoBean.setGainArray(gainArray);
        ecgDataInfoBean.setLeadType(ecgSettingBean.getLeadType());
        ecgDataInfoBean.setLeadSpeedType(ecgSettingBean.getLeadSpeedType());
        ecgDataInfoBean.setLeadGainType(ecgSettingBean.getLeadGainType());

        ecgDataInfoBean.setFilterLowpass(ecgSettingBean.getFilterLowpass());
        ecgDataInfoBean.setFilterEmg(ecgSettingBean.getFilterEmg());
        ecgDataInfoBean.setFilterAds(ecgSettingBean.getFilterBaseline());
        ecgDataInfoBean.setFilterAcOpenState(systemSettingBean.getLeadFilterTypeAc());
        ecgDataInfoBean.setFilterAc(ecgSettingBean.getFilterAc());

        return ecgDataInfoBean;
    }

    public short[][] getEcgDataArray() {
        return ecgDataArray;
    }

    public void setEcgDataArray(short[][] ecgDataArray) {
        this.ecgDataArray = ecgDataArray;
    }

    public EcgSettingConfigEnum.LeadType getLeadType() {
        return leadType;
    }

    public void setLeadType(EcgSettingConfigEnum.LeadType leadType) {
        this.leadType = leadType;
    }

    public float[] getGainArray() {
        return gainArray;
    }

    public void setGainArray(float[] gainArray) {
        this.gainArray = gainArray;
    }

    public EcgSettingConfigEnum.LeadSpeedType getLeadSpeedType() {
        return leadSpeedType;
    }

    public void setLeadSpeedType(EcgSettingConfigEnum.LeadSpeedType leadSpeedType) {
        this.leadSpeedType = leadSpeedType;
    }

    public EcgSettingConfigEnum.LeadGainType getLeadGainType() {
        return leadGainType;
    }

    public void setLeadGainType(EcgSettingConfigEnum.LeadGainType leadGainType) {
        this.leadGainType = leadGainType;
    }

    public EcgSettingConfigEnum.LeadFilterType getFilterLowpass() {
        return filterLowpass;
    }

    public void setFilterLowpass(EcgSettingConfigEnum.LeadFilterType filterLowpass) {
        this.filterLowpass = filterLowpass;
    }

    public EcgSettingConfigEnum.LeadFilterType getFilterEmg() {
        return filterEmg;
    }

    public void setFilterEmg(EcgSettingConfigEnum.LeadFilterType filterEmg) {
        this.filterEmg = filterEmg;
    }

    public EcgSettingConfigEnum.LeadFilterType getFilterAds() {
        return filterAds;
    }

    public void setFilterAds(EcgSettingConfigEnum.LeadFilterType filterAds) {
        this.filterAds = filterAds;
    }

    public EcgSettingConfigEnum.LeadFilterType getFilterAcOpenState() {
        return filterAcOpenState;
    }

    public void setFilterAcOpenState(EcgSettingConfigEnum.LeadFilterType filterAcOpenState) {
        this.filterAcOpenState = filterAcOpenState;
    }

    public EcgSettingConfigEnum.LeadFilterType getFilterAc() {
        return filterAc;
    }

    public void setFilterAc(EcgSettingConfigEnum.LeadFilterType filterAc) {
        this.filterAc = filterAc;
    }

}
