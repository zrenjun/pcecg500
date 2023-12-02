package com.lepu.ecg500.util;

import com.lepu.ecg500.entity.MinnesotaCodeItemBean;
import com.lepu.ecg500.entity.SystemSettingConfigEnum;

import java.util.ArrayList;
import java.util.HashMap;

public class DiagnosisBuiltInChildDao {
    private static final String TABLE_NAME = "TemplateStandard";

    public static ArrayList<MinnesotaCodeItemBean> getDiagnosisBuiltInChild() {
        // TODO: 2021/5/13 数据库版本写死
        //DBHelper dbHelp = DBHelper.getInstance(DBHelper.DATABASE_NAME_MAIN, Setting.DATABASE_MAIN_VERSION);
        DBHelper dbHelp = DBHelper.getInstance(DBHelper.DATABASE_NAME_MAIN, 1);

        String sql = String.format("select * from %s order by TId desc", TABLE_NAME);
        ArrayList<HashMap<String, Object>> queryResult = dbHelp.query(sql,
                null);
        ArrayList<MinnesotaCodeItemBean> dataList = new ArrayList<MinnesotaCodeItemBean>();

        if (queryResult == null || queryResult.size() == 0) {
            return dataList;
        }

        for (int arrayIndex = 0; arrayIndex < queryResult.size(); arrayIndex++) {
            HashMap<String, Object> dictionary = queryResult.get(arrayIndex);
            MinnesotaCodeItemBean item = parseFromDatabase(dictionary);
            dataList.add(item);
        }
        return dataList;
    }

    private static MinnesotaCodeItemBean parseFromDatabase(
            HashMap<String, Object> dictionary) {

        if (dictionary == null || dictionary.size() == 0) {
            return null;
        }
        MinnesotaCodeItemBean item = new MinnesotaCodeItemBean();
        // TODO: 2021/5/13 mwj 语言版本写死
       // String language = LanguageSettingUtil.getInstance(BaseApplication.getInstance()).getLanguage();
        String language =  String.valueOf(SystemSettingConfigEnum.LanguageMode.CHINESE.getValue());
        if("en".equals(CustomTool.getCurLanguage())){
            language = String.valueOf(SystemSettingConfigEnum.LanguageMode.ENGLISH.getValue());
        }

        item.setId(Util.parseInt((String) dictionary.get("GId")));
        item.setCode((String) dictionary.get("ItemCode"));

        if (language.contains(String.valueOf(SystemSettingConfigEnum.LanguageMode.CHINESE.getValue())))
            item.setTitle((String) dictionary.get("ItemName"));
        else if (language.contains(String.valueOf(SystemSettingConfigEnum.LanguageMode.SPAIN.getValue())))
            item.setTitle((String) dictionary.get("ItemNameEs"));
        else if (language.contains(String.valueOf(SystemSettingConfigEnum.LanguageMode.PORTUGUESE.getValue())))
            item.setTitle((String) dictionary.get("ItemNamePt"));
        else if (language.contains(String.valueOf(SystemSettingConfigEnum.LanguageMode.FRENCH.getValue())))
            item.setTitle((String) dictionary.get("ItemNameFr"));
        else if (language.contains(String.valueOf(SystemSettingConfigEnum.LanguageMode.RUSSIAN.getValue())))
            item.setTitle((String) dictionary.get("ItemNameRu"));
        else
            item.setTitle((String) dictionary.get("ItemNameEn"));

        item.setLevel((String) dictionary.get("TLevel"));
        item.setMinnesotaCode((String) dictionary.get("MinnesotaCode"));
        return item;
    }
}
