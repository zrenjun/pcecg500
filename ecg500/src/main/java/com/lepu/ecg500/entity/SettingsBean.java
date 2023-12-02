package com.lepu.ecg500.entity;

public class SettingsBean {
	/**
	 * 服务器地址(默认值为：http://124.172.177.141:10405/)
	 */
	private String serverUrl = null;

	/**
	 * 获取患者信息url
	 */
	private String serverUrlGetPatient = null;

	/**
	 * 采集时间-------常规心电(int)
	 * 默认值为10：当时间为10时，需要多采集2s(即时间为12s)
	 */
	private int colTimeNormal = 10;   //

	/**
	 * 首页首列显示的是pid(0) or wearNumber(1)
	 */
	private int mainId = 0;   //

	/**
	 * 采集数据是否多采集2秒
	 */
	private boolean moreSecond = true;   //

	/**
	 * 采集模式
	 */
	private boolean detectMode = true;   //

	/**
	 * 心率变异采集时间(int 为分钟)
	 */
	private int colTimeHRV = 1;
	/**
	 * 向量心电采集时间(int 为分钟)
	 */
	private int colTimeVCG = 1;
	/**
	 * 保存的最近最近病例数量(int)
	 */
	private int reportCount = 150;
	/**
	 * 保存的最近检查的本地心电数据数量(int)
	 */
	private int dataCount = 150;
	
	/**
	 * 是否选中常规心电(boolean)
	 */
	private boolean studyECG = true;
	
	/**
	 * 是否选中QT离散度(boolean)
	 */
	private boolean studyQTD = false;
	
	/**
	 * 是否选中频谱心电(boolean)
	 */
	private boolean studyFCG = false;
	
	/**
	 * 是否选中高频心电(boolean)
	 */
	private boolean studyHF;
	
	/**
	 * 是否选中心率变异(boolean)
	 */
	private boolean studyHRV;
	
	/**
	 * 是否选中向量心电(boolean)
	 */
	private boolean studyVCG;
	
	/**
	 * 是否选中时间向量(boolean)
	 */
	private boolean studyTVCG;
	/**
	 * 是否选中晚电位(boolean)
	 */
	private boolean studyVLP;
	/**
	 * 是否选中起搏检测(boolean)
	 */
	private boolean studyUserPacemaker;
	
	/**
	 * 增益（int）
	 */
	private int gain = 5;
	
	/**
	 * 波速（float）
	 */
	private float waveSpeed = 25;
	
	/**
	 * 低通滤波Hz(int)
	 */
	private int lowPassHz = 100;
	
	/**
	 * 肌电滤波Hz(int)
	 */
	private int EMGHz = 35;
	
	/**
	 * 工频滤波(Hz)
	 * 0:未被选中
	 */
	private int HUMHz = 0;

	/**
	 * 基漂滤波(Hz)
	 */
	private int HpHz = 5;

	public int getFilterSelect() {
		return filterSelect;
	}

	public void setFilterSelect(int filterSelect) {
		this.filterSelect = filterSelect;
	}

	/**
	 * 滤波选择
	 * 0 无
	 * 1 40hz
	 * 2 50hz
	 */
	private int filterSelect = 0;

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerUrlGetPatient() {
		return serverUrlGetPatient;
	}

	public void setServerUrlGetPatient(String serverUrlGetPatient) {
		this.serverUrlGetPatient = serverUrlGetPatient;
	}

	public int getColTimeNormal() {
		return colTimeNormal;
	}

	public void setColTimeNormal(int colTimeNormal) {
		this.colTimeNormal = colTimeNormal;
	}

	public int getMainId() {
		return mainId;
	}

	public void setMainId(int mainId) {
		this.mainId = mainId;
	}

	public boolean isMoreSecond() {
		return moreSecond;
	}

	public void setMoreSecond(boolean moreSecond) {
		this.moreSecond = moreSecond;
	}

	public boolean isDetectMode() {
		return detectMode;
	}

	public void setDetectMode(boolean detectMode) {
		this.detectMode = detectMode;
	}

	public int getColTimeHRV() {
		return colTimeHRV;
	}

	public void setColTimeHRV(int colTimeHRV) {
		this.colTimeHRV = colTimeHRV;
	}

	public int getColTimeVCG() {
		return colTimeVCG;
	}

	public void setColTimeVCG(int colTimeVCG) {
		this.colTimeVCG = colTimeVCG;
	}

	public int getReportCount() {
		return reportCount;
	}

	public void setReportCount(int reportCount) {
		this.reportCount = reportCount;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	public boolean isStudyECG() {
		return studyECG;
	}

	public void setStudyECG(boolean studyECG) {
		this.studyECG = studyECG;
	}

	public boolean isStudyQTD() {
		return studyQTD;
	}

	public void setStudyQTD(boolean studyQTD) {
		this.studyQTD = studyQTD;
	}

	public boolean isStudyFCG() {
		return studyFCG;
	}

	public void setStudyFCG(boolean studyFCG) {
		this.studyFCG = studyFCG;
	}

	public boolean isStudyHF() {
		return studyHF;
	}

	public void setStudyHF(boolean studyHF) {
		this.studyHF = studyHF;
	}

	public boolean isStudyHRV() {
		return studyHRV;
	}

	public void setStudyHRV(boolean studyHRV) {
		this.studyHRV = studyHRV;
	}

	public boolean isStudyVCG() {
		return studyVCG;
	}

	public void setStudyVCG(boolean studyVCG) {
		this.studyVCG = studyVCG;
	}

	public boolean isStudyTVCG() {
		return studyTVCG;
	}

	public void setStudyTVCG(boolean studyTVCG) {
		this.studyTVCG = studyTVCG;
	}

	public boolean isStudyVLP() {
		return studyVLP;
	}

	public void setStudyVLP(boolean studyVLP) {
		this.studyVLP = studyVLP;
	}

	public boolean isStudyUserPacemaker() {
		return studyUserPacemaker;
	}

	public void setStudyUserPacemaker(boolean studyUserPacemaker) {
		this.studyUserPacemaker = studyUserPacemaker;
	}

	public int getGain() {
		return gain;
	}

	public void setGain(int gain) {
		this.gain = gain;
	}

	public float getWaveSpeed() {
		return waveSpeed;
	}

	public void setWaveSpeed(float waveSpeed) {
		this.waveSpeed = waveSpeed;
	}

	public int getLowPassHz() {
		return lowPassHz;
	}

	public void setLowPassHz(int lowPassHz) {
		this.lowPassHz = lowPassHz;
	}

	public int getEMGHz() {
		return EMGHz;
	}

	public void setEMGHz(int eMGHz) {
		EMGHz = eMGHz;
	}

	public int getHUMHz() {
		return HUMHz;
	}

	public void setHUMHz(int hUMHz) {
		HUMHz = hUMHz;
	}

	public int getHpHz() {
		return HpHz;
	}

	public void setHpHz(int hpHz) {
		HpHz = hpHz;
	}
}
