package com.lepu.ecg500.entity;

import android.content.Context;

import androidx.annotation.StringRes;

import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.R;


public class SystemSettingConfigEnum {

    public enum AutoLockScreenMode {

        CLOSE(0),
        MINUTE_5(5),
        MINUTE_10(10),
        MINUTE_20(20),
        MINUTE_30(30),
        MINUTE_60(60);

        private int value;

        AutoLockScreenMode(int value){
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
            Context context = CustomTool.application;
            if (value == 0){
                return context.getString(R.string.setting_text_system_set_auto_close_device_close);
            }else {
                String format = value + " %s";
                return String.format(format,context.getString(R.string.setting_text_detect_setting_unit_min));
            }
        }

        public int getValue() {
            return value;
        }

    }

    public enum BrightnessMode {
        /**
         * 亮度低
         */
        BRIGHTNESS_LOW("",1),
        /**
         * 亮度中
         */
        BRIGHTNESS_MID("",2),
        /**
         * 亮度高
         */
        BRIGHTNESS_HIGH("",3);

        private String name;
        private Object value;

        BrightnessMode(String name, Object value){
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

    public enum DateFormatMode {
        YEAR_MONTH_DAY(R.string.setting_text_sys_date_format_ymd,"yyyy-MM-dd"),
        MONTH_DAY_YEAR(R.string.setting_text_sys_date_format_mdy,"MM-dd-yyyy"),
        DAY_MONTH_YEAR(R.string.setting_text_sys_date_format_dmy,"dd-MM-yyyy");

        private @StringRes
        int nameRes;
        private Object value;

        DateFormatMode(@StringRes int name, Object value){
            this.nameRes = name;
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }

            return CustomTool.application.getString(nameRes);
        }

        public Object getValue() {
            return value;
        }

    }

    public enum DemoMode{
        DEMOMODE_CLOSE (R.string.setting_text_sys_demo_mode_close,""),
        DEMOMODE_NORMALE (R.string.setting_text_sys_demo_mode_normal,"normal"),
        DEMOMODE_EXCEPTION (R.string.setting_text_sys_demo_mode_exception,"exception");
        //        DEMOMODE_SINE_HIGH (R.string.setting_text_sys_demo_mode_high_sine,"3mv40hzSine");

        private @StringRes int nameRes;
        private Object value;

        DemoMode(@StringRes int nameRes, Object value){
            this.nameRes = nameRes;
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
//            return context.getString(nameRes);
            return CustomTool.application.getString(nameRes);
        }

        public Object getValue() {
            return value;
        }
    }

    public enum DiagnosticMode {
        LOCAL_MODE("",""),//本地诊断
        NET_MODE("","");//云诊断

        private String name;
        private Object value;

        DiagnosticMode(String name, Object value){
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

    public enum SoundMode {
        CLOSE(R.string.setting_text_off,0),
        /**
         * 声音低
         */
        VOICE_LOW(R.string.setting_text_sys_volume_low,10),
        /**
         * 声音中
         */
        VOICE_MID(R.string.setting_text_sys_volume_mid,20),
        /**
         * 声音高
         */
        VOICE_HIGH(R.string.setting_text_sys_volume_high,30);

        private @StringRes int nameRes;
        private Object value;

        SoundMode(@StringRes int name, Object value){
            this.nameRes = name;
            this.value = value;
        }

        public String getName() {
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
//            return context.getString(nameRes);
            return CustomTool.application.getString(nameRes);
        }

        public Object getValue() {
            return value;
        }

    }

    public enum TimeFormatMode {
        FORMAT_12("",12),
        FORMAT_24("",24);

        private String name;
        private Object value;

        TimeFormatMode(String name, Object value){
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

    public enum LanguageMode {
        //使用这个国际化，不工作
        //        CHINESE (BaseApplication.getInstance().getString(R.string.setting_text_sys_language_ch),"zh-cn"),
        //        ENGLISH(BaseApplication.getInstance().getString(R.string.setting_text_sys_language_en),"en-us"),
        //        SPAIN(BaseApplication.getInstance().getString(R.string.setting_text_sys_language_spain),"es-rES");

        //使用这个才可以
        CHINESE (R.string.setting_text_sys_language_ch,"zh"),
        ENGLISH(R.string.setting_text_sys_language_en,"en"),
        SPAIN(R.string.setting_text_sys_language_spain,"es"),
        PORTUGUESE(R.string.setting_text_sys_language_portuguese,"pt"),
        FRENCH(R.string.setting_text_sys_language_french,"fr"),
        RUSSIAN(R.string.setting_text_sys_language_russian,"ru");

        private @StringRes int nameResId;
        private Object value;

        LanguageMode(@StringRes int name, Object value){
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

        public static LanguageMode getLanguageByNickName(String systemLanguageValue){
            LanguageMode languageMode = LanguageMode.CHINESE;
            for(LanguageMode item : LanguageMode.values()){
                if(systemLanguageValue.contains(String.valueOf(item.getValue()))){
                    languageMode = item;
                    break;
                }
            }
            return languageMode;
        }
    }
    public enum ExportFormat {
        PDF ("PDF","PDF"),
        BMP ("BMP","BMP"),
        SCP ("SCP","SCP"),
        HL7("HL7","HL7"),
        CAREWELL("CAREWELL","CAREWELL"),
        DICOM("DICOM","DICOM");
        private String name;
        private Object value;

        ExportFormat(String name, Object value){
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
}
