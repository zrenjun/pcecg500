#  demo地址：https://github.com/zrenjun/pcecg500



## 1.引入依赖

> ```
> implementation project(path: ':ecg500')
> ```



## 2.申请使用权限（https://developer.android.com/guide/topics/connectivity/usb/host?hl=zh-cn）

> ```
> <manifest ...>
>   <uses-feature android:name="android.hardware.usb.host" /> <!-- usb权限 -->
>   <uses-sdk android:minSdkVersion="12" />
>   ...
>   <application>
>       <!-- 适配7.0 -->
>        <provider
>             android:name="androidx.core.content.FileProvider"
>             android:authorities="${applicationId}.fileprovider"
>             android:exported="false"
>             android:grantUriPermissions="true">
>             <meta-data
>                 android:name="android.support.FILE_PROVIDER_PATHS"
>                 android:resource="@xml/provider_paths" />
>         </provider>
>    
>       <activity ...>
>          ...
>            <!-- usb连接 -->
>           <intent-filter>
>               <action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
>           </intent-filter>
>    
>           <meta-data android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
>            android:resource="@xml/device_filter" />
>       </activity>
>    </application>
>    </manifest>
>   
> ```

> ```
> <?xml version="1.0" encoding="utf-8"?>
> 
> <resources>
>    <usb-device vendor-id="1027" product-id="24597" /> <!-- FT230X -->
> </resources>
> ```



## 3.设备通信流程提示，详例参见demo

> 1. 连接      
>
>    ```
>    usbConnManager.connectFunction()
>    ```
>
> 2. 获取数据 
>
>    ```
>    usbConnManager.collectReadData()
>    ```



## 4.生成Hl7Xml文件上传获取心电报告

> 具体参见GetPDFViewModel

1. 在管理平台http://www.lepuaicloud.com（这是测试环境，生产环境https://wisemedical.creative-sz.com）创建统一客户账号
2. 通过账号密码获取接口请求令牌，具体请参考示例及接口文档
3. 注意：在上传用户信息patient.setUserId中一定要与客户账号一致



## 5.切换波形展示

> ```
> MainEcgManager.getInstance().updateMainEcgShowStyle(LeadType.LEAD_6)
> ```



## 6.本地心电传统分析

> ```
> 具体参见：viewViewModel.getValue().getLocalPdf(ecgDataArray)
> 结论：viewViewModel.getValue().getMLocalResult().observe()
> 全部分析数据：viewViewModel.getValue().getMLocalResultBean().observe()
> ```
> 

