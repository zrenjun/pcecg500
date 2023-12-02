package com.lepu.ecg500.util;

import android.content.Context;
import android.text.TextUtils;
import com.lepu.ecg500.R;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.EcgSettingBean;
import com.lepu.ecg500.entity.ReportSettingBean;
import com.lepu.ecg500.entity.EcgSettingConfigEnum;
import com.lepu.ecg500.entity.RecordSettingConfigEnum;
import java.util.List;

/**
 * @author wxd 打印
 */
public class PrintManager {
	private static PrintManager instance = null;

	private PrintManager() {

	}

	public static PrintManager getInstance() {
		if (instance == null) {
			instance = new PrintManager();
		}
		return instance;
	}

	/**
	 * 打印底部信息
	 */
	public String getPrintBottomInfo(Context context, ConfigBean configBean, boolean isOutPrint, long checkTimeStamp, int heartRateValue, boolean isAiAnalysis) {

		EcgSettingBean ecgSettingBean = configBean.getEcgSettingBean();
		String space = "\t \t";
		StringBuilder sb = new StringBuilder();

		String modeContent = "";
		modeContent = context.getString(R.string.main_text_ecg_mode_demo_mode);
		if (!TextUtils.isEmpty(modeContent)) {
			sb.append(modeContent).append(space);
		}

		//走速 增益
		sb.append(ecgSettingBean.getLeadSpeedType().getName()).append(space);
		sb.append(ecgSettingBean.getLeadGainType().getName()).append(space);

		//滤波
		if (ecgSettingBean.getFilterAc() == EcgSettingConfigEnum.LeadFilterType.FILTER_AC_OPEN) {
			if (configBean.getSystemSettingBean().getLeadFilterTypeAc() == EcgSettingConfigEnum.LeadFilterType.FILTER_AC_50_HZ ||
					configBean.getSystemSettingBean().getLeadFilterTypeAc() == EcgSettingConfigEnum.LeadFilterType.FILTER_AC_60_HZ) {
				sb.append(String.format("AC %s", configBean.getSystemSettingBean().getLeadFilterTypeAc().getName())).append(space);
			}
		}
		if (ecgSettingBean.getFilterEmg().ordinal() >= EcgSettingConfigEnum.LeadFilterType.FILTER_EMG_25.ordinal() &&
				ecgSettingBean.getFilterEmg().ordinal() <= EcgSettingConfigEnum.LeadFilterType.FILTER_EMG_45.ordinal()) {
			sb.append(String.format("EMG %s", ecgSettingBean.getFilterEmg().getName())).append(space);
		} else if (ecgSettingBean.getFilterLowpass().ordinal() >= EcgSettingConfigEnum.LeadFilterType.FILTER_LOWPASS_75.ordinal() &&
				ecgSettingBean.getFilterLowpass().ordinal() <= EcgSettingConfigEnum.LeadFilterType.FILTER_LOWPASS_300.ordinal()) {
			sb.append(String.format("LP %s", ecgSettingBean.getFilterLowpass().getName())).append(space);
		}
		sb.append(String.format("ADS %s", ecgSettingBean.getFilterBaseline().getName())).append(space);

		//HR
		if (configBean.getRecordSettingBean().getPrintDeviceType() == RecordSettingConfigEnum.PrintDeviceType.THERMAL) {
			if (heartRateValue >= 0) {
				sb.append(String.format("HR: %d", heartRateValue)).append(space);
			} else {
				sb.append(String.format("HR: %s", "- -")).append(space);
			}
		}

		sb.append(String.format("%s", "1")).append(space);
		sb.append(String.format("%s",  "1")).append(space);

		//机构名称
		String organizationName = configBean.getSystemSettingBean().getOrganizationName();
		if (!TextUtils.isEmpty(organizationName)) {
			sb.append(String.format("%s", organizationName)).append(space);
		}

		//检查时间
		if (checkTimeStamp > 0) {
			sb.append(String.format("%s %s",
					context.getString(R.string.print_export_bottom_info_checktime),
					CustomTool.formatDateString(context, checkTimeStamp))).append(space);
		}

		//打印时间
		if (isOutPrint) {
			List<ReportSettingBean> reportSettingBeanList = configBean.getRecordSettingBean().getReportSettingBeanList();
			boolean printTimeFlag = reportSettingBeanList.get(RecordSettingConfigEnum.ReportSetting.PRINT_TIME.ordinal()).isValue();
			if (printTimeFlag) {
				sb.append(String.format("%s %s",
						context.getString(R.string.print_export_bottom_info_printtime),
						CustomTool.formatDateString(context, System.currentTimeMillis()))).append(space);
			}
		}

		return sb.toString();
	}

	/**
	 * 打印底部信息
	 */
	public String getPrintBottomInfo(Context context, ConfigBean configBean, boolean isOutPrint, long checkTimeStamp, boolean isAiAnalysis) {
		return getPrintBottomInfo(context, configBean, isOutPrint, checkTimeStamp, -1, isAiAnalysis);
	}
}
