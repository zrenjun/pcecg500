package com.lepu.ecg500.entity;

import androidx.annotation.StringRes;
import com.lepu.ecg500.R;
import com.lepu.ecg500.util.CustomTool;


public class RecordSettingConfigEnum {
    public enum ChannelType {
        /**
         * 三通道
         */
        CHANNEL_3("",""),
        /**
         * 六通道
         */
        CHANNEL_6("",""),

        /**
         * 全通道
         */
        CHANNEL_ALL("","");

        private String name;
        private Object value;

        ChannelType(String name, Object value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public enum PrintDeviceType {
        /**
         * 热敏打印机
         */
        THERMAL (R.string.setting_text_report_setting_print_device_thermal,""),
        /**
         * 激光网络打印机
         */
        LASER_NET(R.string.setting_text_report_setting_print_device_net,""),
        /**
         * 激光 usb打印
         */
        LASER_USB (R.string.setting_text_report_setting_print_device_usb,"");

        private @StringRes
        int nameResId;
        private Object value;

        PrintDeviceType(@StringRes int name, Object value){
            this.nameResId = name;
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
//            return context.getString(nameResId);
            return CustomTool.application.getString(nameResId);
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public enum RecordOrderType {
        /**
         * 顺序记录
         */
        ORDER_INORDER ("",""),
        /**
         * 同步记录
         */
        ORDER_SYNC("","");

        private String name;
        private Object value;

        RecordOrderType(String name, Object value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public enum RecordMode {
        /**
         * 快速模式
         */
        TYPE_FAST("",""),
        /**
         * 省纸模式
         */
        TYPE_SAVEPAPER("","");

        private String name;
        private Object value;

        RecordMode(String name, Object value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
    public enum ReportSetting {
        /**
         * 测量参数
         */
        MEASURE_RESULT(R.string.setting_text_report_setting_report_measure_result,""),
        /**
         * 平均模板
         */
        AVERAGE_TEMPLATE(R.string.setting_text_report_setting_report_average_templatate,""),
        /**
         * 诊断结果
         */
        DIAGNOSIS(R.string.setting_text_report_setting_report_diagnosis,""),
        /**
         * 明尼苏达码
         */
        MINNESOTA(R.string.setting_text_report_setting_report_minnesota,""),
        /**
         * 打印时间
         */
        PRINT_TIME(R.string.setting_text_report_setting_report_print_time,""),
        /**
         * 测量矩阵
         */
        MEASURE_MATRIX(R.string.setting_text_report_setting_report_measure_matrix,""),
        /**
         * 时间标尺
         */
        TIME_RULER(R.string.setting_text_report_setting_report_measure_time,"");

        private @StringRes int nameResId;
        private Object value;

        ReportSetting(@StringRes int name, Object value){
            this.nameResId = name;
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
//            return context.getString(nameResId);
            return CustomTool.application.getString(nameResId);
        }

        public Object getValue() {
            return value;
        }

    }
}
