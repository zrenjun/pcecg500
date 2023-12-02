package com.lepu.ecg500.entity;


import androidx.annotation.StringRes;
import com.lepu.ecg500.util.CustomTool;
import com.lepu.ecg500.R;

public class SampleSettingConfigEnum {

    public enum RealtimeSampleTimeMode {
        SECOND_10(10),
        SECOND_20(20),
        SECOND_30(30),
        SECOND_60(60);

        private int value;

        RealtimeSampleTimeMode(int value){
            this.value = value;
        }

        public String getName() {
            String format = value +" %s";
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
            return String.format(format, CustomTool.application.getString(R.string.setting_text_detect_setting_unit_second));
        }

        public int getValue() {
            return value;
        }

    }

    public enum RRSampleTimeMode {
        SECOND_60("60",60),
        SECOND_180("180",180);

        private String name;
        private Object value;

        RRSampleTimeMode(String name, Object value){
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

    public enum HrvSampleTimeMode {
        MINUTE_5(5),
        MINUTE_10(10),
        MINUTE_15(15),
        MINUTE_20(20),
        MINUTE_25(25),
        MINUTE_30(30);

        private int value;

        HrvSampleTimeMode(int value){
            this.value = value;
        }

        public String getName() {
            String format = value + " %s";
//            Context context = MyAppProxy.getInstance().getResumeAct();
//            if (context == null){
//                context = BaseApplication.getInstance();
//            }
            return String.format(format,CustomTool.application.getString(R.string.setting_text_detect_setting_unit_min));
        }

        public int getValue() {
            return value * 60;
        }

    }

    public enum DrugTestSamplePointsMode {
        START (R.string.setting_text_detect_setting_drug_point_0,0),
        MINUTE_1 (R.string.setting_text_detect_setting_drug_point_1,60),
        MINUTE_2 (R.string.setting_text_detect_setting_drug_point_2,60*2),
        MINUTE_3 (R.string.setting_text_detect_setting_drug_point_3,60*3),
        MINUTE_5 (R.string.setting_text_detect_setting_drug_point_5,60*5),
        MINUTE_7 (R.string.setting_text_detect_setting_drug_point_7,60*7),
        MINUTE_10 (R.string.setting_text_detect_setting_drug_point_10,60*10),
        MINUTE_15 (R.string.setting_text_detect_setting_drug_point_15,60*15),
        MINUTE_20 (R.string.setting_text_detect_setting_drug_point_20,60*20),
        MINUTE_30 (R.string.setting_text_detect_setting_drug_point_30,60*30);

        private @StringRes
        int nameResId;
        private Object value;

        DrugTestSamplePointsMode(@StringRes int name, Object value){
            this.nameResId = name;
            this.value = value;
        }

        public String getName(){
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

}
