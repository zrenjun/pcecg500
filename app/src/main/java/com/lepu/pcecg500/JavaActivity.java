package com.lepu.pcecg500;

import static org.koin.java.KoinJavaComponent.inject;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.Carewell.OmniEcg.jni.JniFilterNew;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.lepu.ecg500.ecg12.LeadType;
import com.lepu.ecg500.ecg12.MainEcgManager;
import com.lepu.ecg500.entity.PatientInfoBean;
import com.lepu.ecg500.usb.UsbData;
import com.lepu.ecg500.usb.CollectIndex;
import com.lepu.ecg500.util.ECGBytesToShort;
import com.net.bean.Patient;
import com.net.util.Constant;
import com.net.util.FileUtil;
import com.net.vm.GetPDFViewModel;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import kotlin.Lazy;

/**
 * 说明: demo java
 * zrj 2022/3/24 15:16
 */
public class JavaActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, SerialInputOutputManager.Listener {
    private enum UsbPermission {Unknown, Requested, Granted, Denied}

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private final Lazy<GetPDFViewModel> viewViewModel = inject(GetPDFViewModel.class);
    private int uiReadBufferSize = 3200;//每次读取的字节数量
    private byte[] readBuffer;//将数据从缓冲数组中读取出来存放的数组
    private final int updateTaskPeriodTime = 200;//定时时间
    private final int normalTime = 15 * 1000;//采集总时长 10秒
    private final List<short[][]> allDetectInfo = new ArrayList<>(); //采集的全部的数据
    private int currentDetectTime = 0;//当前采集时间
    private final CollectIndex collInd = new CollectIndex();
    private ECGBytesToShort ecgDataUtil; //滤波处理工具
    private Timer updateTimer = new Timer();
    private final LeadType leadType = LeadType.LEAD_12; //当前导联模式
    private Boolean isStart = false; //是否正在采集数据
    private TextView tvStart;
    private Handler mainLooper;
    private BroadcastReceiver broadcastReceiver;
    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort usbSerialPort;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private boolean connected = false;
    private boolean isOldDevice = true;
    private UsbData usbData;
    private Long checkTimeStamp = 0L;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ecgDataUtil = new ECGBytesToShort();
        //波形控件初始化
        MainEcgManager.getInstance().init(this);
        //设置布局显示方式
        MainEcgManager.getInstance().updateMainEcgShowStyle(leadType);
        MainEcgManager.getInstance().setDrawEcgRealView(findViewById(R.id.ecgView));
        //控制按钮
        tvStart = findViewById(R.id.tv_start);
        tvStart.setOnClickListener(this);
        //4个设置选项
        Spinner spinnerGain = findViewById(R.id.spinnerGain);
        Spinner spinnerSpeed = findViewById(R.id.spinnerSpeed);
        spinnerGain.setSelection(2, true);
        spinnerSpeed.setSelection(2, true);
        spinnerSpeed.setOnItemSelectedListener(this);
        spinnerGain.setOnItemSelectedListener(this);

        viewViewModel.getValue().getMECGPdf().observe(this, s -> {
            dismissProgressDialog();
            if (s.contains("ecg500")) {
                FileUtil.INSTANCE.shareFile(this, new File(s), viewViewModel.getValue().isPdf() ? "application/pdf" : "application/xml");
            }
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        });
        viewViewModel.getValue().getMLocalResult().observe(this, s -> {
            Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show();
        });

        viewViewModel.getValue().getMException().observe(this, s -> {
            dismissProgressDialog();
            Toast.makeText(this, s.getMessage(), Toast.LENGTH_SHORT).show();
        });

        usbData = new UsbData();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (INTENT_ACTION_GRANT_USB.equals(intent.getAction())) {
                    usbPermission = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                            ? UsbPermission.Granted : UsbPermission.Denied;
                    connect();
                }
            }
        };
        mainLooper = new Handler(Looper.getMainLooper());

        if (isStart) {
            isStart = false;
            clickTvStart();
            Toast.makeText(this, R.string.detect_interrupt_re_detect, Toast.LENGTH_LONG).show();
        }

        collInd.iReadIndex = 0;
        collInd.actualNumBytes = 0;
        JniFilterNew.getInstance().InitDCRecover(0);
    }

    private byte leadOffState = 0x00;

    @SuppressLint("SetTextI18n")
    public void runTask() {
        List<Byte> statusList;
        if (usbData.getTotalBytes() > uiReadBufferSize * 5) {
            statusList = usbData.collectReadData(uiReadBufferSize * 2, readBuffer, collInd);
        } else {
            statusList = usbData.collectReadData(uiReadBufferSize, readBuffer, collInd);
        }
        int actualNumBytes = collInd.actualNumBytes;  //一次从缓冲数组中读取字节的数量
        if (0x00 == statusList.get(0)) {
            short[][] tempEcgDataArray = ecgDataUtil.bytesToLeadData(isOldDevice, readBuffer, actualNumBytes);//返回的是8导数据
            short[][] waveFormData = ecgDataUtil.leadSortThe12(tempEcgDataArray);
            runOnUiThread(() -> {
                if (actualNumBytes > 0) {
                    if (isStart) {
                        if (statusList.get(1) != 0) {
                            if (leadOffState != statusList.get(1)) {
                                status("导联脱落");
                                leadOffState = statusList.get(1);
                            }
                        }
                        allDetectInfo.add(waveFormData);
                        currentDetectTime += updateTaskPeriodTime;
                        tvStart.setText((currentDetectTime + updateTaskPeriodTime) / 1000 + "s");
                        if (currentDetectTime >= normalTime) {
                            currentDetectTime = 0;
                            isStart = false;
                            clickTvStart();
                            List<List<Short>> ecgDataArray = new ArrayList<>();
                            for (int i = 0; i < allDetectInfo.size(); i++) {
                                short[][] temp = allDetectInfo.get(i);
                                for (int j = 0; j < temp.length; j++) {
                                    List<Short> shortList = new ArrayList<>();
                                    for (Short x : temp[j]) {
                                        shortList.add(x);
                                    }
                                    if (ecgDataArray.size() - 1 < j) {
                                        ecgDataArray.add(shortList);
                                    } else {
                                        ecgDataArray.get(j).addAll(shortList);
                                    }
                                }
                            }
                            if (isAI) {
                                getAIPdf(ecgDataArray);
                            } else {
                                getLocalPdf(ecgDataArray);
                            }
                            leadOffState = 0x00;
                        }
                    }

                    short[][] finalWaveFormData = ecgDataUtil.filterLeadsData(waveFormData); //是否使用滤波算法;
                    MainEcgManager.getInstance().addEcgData(finalWaveFormData); //绘制心电波形图
                }
            });
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(INTENT_ACTION_GRANT_USB);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(broadcastReceiver, filter);
        }

        if (usbPermission == UsbPermission.Unknown || usbPermission == UsbPermission.Granted)
            mainLooper.post(this::connect);
    }

    @Override
    public void onNewData(byte[] data) {
        usbData.add(data);
    }

    @Override
    public void onRunError(Exception e) {
        mainLooper.post(() -> {
            status("connection lost: " + e.getMessage());
            disconnect();
            connect();
        });
    }

    @SuppressLint("SimpleDateFormat")
    private void getAIPdf(List<List<Short>> ecgDataArray) {
        Patient patient = new Patient();//根据自己的bean修改下面的字段
        patient.setIdcard("430124198701116863");
        patient.setDuns("58693");
        patient.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        patient.setCreatedBy("lepu");
        patient.setName("路菲");
        patient.setUserId(Constant.INSTANCE.getUserId());//todo  替换客户自己管理平台账号
        patient.setSex("女");
        patient.setTel("18612461315");
        patient.setAge("33");
        patient.setSmachineCode("gorlroq");
        patient.setDiagnosisMemo("胸痛");
        showProgressDialog();
        viewViewModel.getValue().getAIPdf(ecgDataArray, patient, null, null, checkTimeStamp,
                checkTimeStamp + normalTime);
    }

    //本地分析
    @SuppressLint("SimpleDateFormat")
    private void getLocalPdf(List<List<Short>> ecgDataArray) {
        //病人信息设置, 请写真实用户信息和医生信息
        PatientInfoBean patientInfoBean = new PatientInfoBean();
        patientInfoBean.setPatientNumber("430124198701116863");
        patientInfoBean.setArchivesName("moArchivesName");
        patientInfoBean.setFirstName("moFirstName");
        patientInfoBean.setLastName("moLastName");
        patientInfoBean.setMiddleName("moMiddleName");
        patientInfoBean.setIdNumber("102");
        patientInfoBean.setPatientNumber("111");
        patientInfoBean.setAge("20");
        patientInfoBean.setBirthdate("2003-09-08");
        patientInfoBean.setLeadoffstate(leadOffState);
        viewViewModel.getValue().getLocalPdf(ecgDataArray, patientInfoBean, null, null, checkTimeStamp,
                checkTimeStamp + normalTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateTimer.cancel();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_start) {
            if (!connected) {
                Toast.makeText(this, R.string.detect_not_connection_usb, Toast.LENGTH_LONG).show();
            } else {
                if (System.currentTimeMillis() - checkTimeStamp > 500) {
                    checkTimeStamp = System.currentTimeMillis();
                    allDetectInfo.clear();
                    isStart = !isStart;
                    clickTvStart();
                }
            }
        }
    }

    private final Boolean isAI = false;

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.spinnerGain) {
            MainEcgManager.getInstance().updateMainGain(position);//设置增益
        } else if (adapterView.getId() == R.id.spinnerSpeed) {
            MainEcgManager.getInstance().updateMainSpeed(position); //设置走速
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private ProgressDialog progressDialog;

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.ecg_data_uploading_please_wait));
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    //修改控件
    private void clickTvStart() {
        Drawable drawable;
        if (isStart) {
            drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_pause_24);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.ic_baseline_play_arrow_24);
            tvStart.setText(getString(R.string.collection));
            currentDetectTime = 0;
            MainEcgManager.getInstance().resetDrawEcg();
        }
        assert drawable != null;
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvStart.setCompoundDrawables(drawable, null, null, null);
    }

    private void connect() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        UsbDevice device = null;
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        for (UsbDevice v : usbManager.getDeviceList().values())
            device = v;

        if (device == null) {
            status("connection failed: device not found");
            return;
        }
        isOldDevice = device.getVendorId() == 1027;
        uiReadBufferSize = isOldDevice ? 3200 : 4400;
        readBuffer = new byte[uiReadBufferSize * 2];
        UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
        if (driver == null) {
            status("connection failed: no driver for device");
            return;
        }
        usbSerialPort = driver.getPorts().get(0);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
            usbPermission = UsbPermission.Requested;
            int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0;
            PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
            usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
            return;
        }
        if (usbConnection == null) {
            if (!usbManager.hasPermission(driver.getDevice()))
                status("connection failed: permission denied");
            else
                status("connection failed: open failed");
            return;
        }
        try {
            usbData.initVal(isOldDevice);
            int baudRate = isOldDevice ? 200000 : 460800;  // 串口设定波特率，
            usbSerialPort.open(usbConnection);
            usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
            usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
            usbIoManager.start();
            status("connected");
            connected = true;
            if (!isOldDevice) {
                usbIoManager.writeAsync(usbData.startCmd()); //开始采集命令
            }
            updateTimer = new Timer();
            updateTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runTask();
                }
            }, 200, updateTaskPeriodTime);
        } catch (Exception e) {
            status("connection exception: " + e.getMessage());
            disconnect();
        }
    }

    private void disconnect() {
        connected = false;
        try {
            if (usbIoManager != null) {
                usbIoManager.setListener(null);
                usbIoManager.stop();
            }
            usbIoManager = null;
            usbSerialPort.close();
        } catch (IOException ignored) {
        }
        usbSerialPort = null;

    }

    private void status(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
