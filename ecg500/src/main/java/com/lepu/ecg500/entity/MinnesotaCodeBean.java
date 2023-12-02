package com.lepu.ecg500.entity;

import java.util.ArrayList;

/**
 * Created by wxd on 2019-04-29.
 */

public class MinnesotaCodeBean {
    private String groupName;
    private ArrayList<MinnesotaCodeItemBean> dataList;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<MinnesotaCodeItemBean> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<MinnesotaCodeItemBean> dataList) {
        this.dataList = dataList;
    }





}
