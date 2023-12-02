package com.lepu.ecg500.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import com.lepu.ecg500.R;
import com.lepu.ecg500.util.Util;
import com.lepu.ecg500.util.Const;
import com.lepu.ecg500.entity.ConfigBean;
import com.lepu.ecg500.entity.PatientSettingConfigEnum;
import com.lepu.ecg500.entity.MacureResultBean;
import com.lepu.ecg500.entity.PatientInfoBean;
import java.util.List;

public abstract class BaseEcgReportTemplate {

    public static final float TEXT_SIZE_TITLE_BIG = 45.0F;
    public static final float TEXT_SIZE_NORMAL = 35.0F;
    public static final float TEXT_SIZE_BOTTOM_INFO = 30.0F;
    public static final int ONE_PIXEL = 2;

    /**
     * 打印模式默认为横向,纵向
     */
    boolean isVertical = false;
    Context context;

    int smartGrid;
    int largeGrid;
    Rect rect;
    int textHeight;
    //左右间距
    int leftMargin;
    //上下间距
    int topMargin;
    Bitmap bgEcg;
    Canvas ecgCanvas;
    Paint ptText;
    Paint ptLine;
    int currentBottomPosition;
    boolean drawGridBg = true;
    ConfigBean configBean;

    int patientInfoHeight;
    int bottomInfoHeight;

    int drawWidth;
    int drawHeight;

    public BaseEcgReportTemplate(Context context, boolean isVertical, boolean drawGridBg, ConfigBean configBean) {
        this.context = context;
        this.isVertical = isVertical;
        this.drawGridBg = drawGridBg;
        this.configBean = configBean;

        //old
        smartGrid = 5 * ONE_PIXEL;
        largeGrid = 5 * smartGrid;
        textHeight = largeGrid;
        leftMargin = largeGrid;
        topMargin = largeGrid;

        if (!isVertical) {
            //横版
            drawWidth = Const.A4_HEIGHT - 2*leftMargin;
            drawHeight = Const.A4_WIDTH - 2*topMargin;

            bgEcg = Bitmap.createBitmap(Const.A4_HEIGHT, Const.A4_WIDTH, Bitmap.Config.RGB_565);
            rect = new Rect(leftMargin, topMargin, drawWidth+leftMargin, drawHeight+topMargin);
        }else{
            //竖版
            drawWidth = Const.A4_WIDTH - 2*leftMargin;
            drawHeight = Const.A4_HEIGHT - 2*topMargin;

            bgEcg = Bitmap.createBitmap(Const.A4_WIDTH, Const.A4_HEIGHT, Bitmap.Config.RGB_565);
            rect = new Rect(leftMargin, topMargin, drawWidth+leftMargin, drawHeight+topMargin);
        }

        ecgCanvas = new Canvas(bgEcg);
        ecgCanvas.drawColor(Color.WHITE);

        ptText = new Paint();
        ptText.setColor(Color.BLACK);
        ptText.setTextAlign(Paint.Align.LEFT);
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        ptLine = new Paint();
        ptLine.setColor(Color.BLACK);
        ptLine.setStyle(Paint.Style.STROKE);
        ptLine.setStrokeWidth(ONE_PIXEL);
        ptLine.setAntiAlias(true);
    }

    /**
     * 画最顶部信息
     * @param titleInfo
     */
    public abstract void drawTitleInfo(String titleInfo);

    /**
     * 画顶部通用患者信息
     * @param patientInfoBean
     */
    public abstract void drawPatientInfoTop(PatientInfoBean patientInfoBean);

    /**
     * 画测量值 诊断结果
     * @param macureResultBean
     */
    public abstract void drawMacureResult(MacureResultBean macureResultBean);

    /**
     * 画心电图
     */
    public abstract void drawEcgImage(float[] gainArray, short[][] ecgDataArray,
                                      boolean averageTemplate,List<String> macureValueList);

    /**
     * 画心电参数信息
     */
    public abstract void drawBottomEcgParamInfo(String ecgParamInfo);

    /**
     * 画心电参数信息
     */
    public abstract void drawBottomOtherInfo(String infoTip,String infoPage);

    public Bitmap getBgEcg() {
        return bgEcg;
    }

    public void setBgEcg(Bitmap bgEcg) {
        this.bgEcg = bgEcg;
    }



    /**
     * 画四周表格
     */
    public void drawRoundTable(){
        int left = rect.left;
        int right = rect.right;
        int top = rect.top + ONE_PIXEL;
        int bottom = rect.bottom ;

        //top line
        ecgCanvas.drawLine(left,top - ONE_PIXEL, right, top, ptLine);
        //bottom
        ecgCanvas.drawLine(left,bottom-ONE_PIXEL, right, bottom, ptLine);
        //left
        ecgCanvas.drawLine(left,top, left+ONE_PIXEL, bottom, ptLine);
        //right
        ecgCanvas.drawLine(right-ONE_PIXEL,top, right, bottom, ptLine);

        currentBottomPosition = top;
    }

    /***
     * 绘制title信息
     * @param titleInfo
     */
    public void drawTitleInfoBase(String titleInfo) {
        ptText.setFakeBoldText(true);
        ptText.setTextSize(TEXT_SIZE_TITLE_BIG);

        int left = rect.left;
        int right = rect.right;
        int top = currentBottomPosition;
        int topY = top + largeGrid;

        //ecgCanvas.drawText(titleInfo, (right - titleInfo.length()*textHeight) / 2, topY, ptText);
        ecgCanvas.save();
        ecgCanvas.translate(left, topY-largeGrid);
        String contentResult = String.format("%s",titleInfo);
        StaticLayout staticLayoutResult = new StaticLayout(contentResult, new TextPaint(ptText), right - largeGrid,
                Layout.Alignment.ALIGN_CENTER, 0, 0, false);
        staticLayoutResult.draw(ecgCanvas);
        ecgCanvas.restore();

        currentBottomPosition = topY;
    }
    /**
     * 获取年龄
     *
     * @param patientInfoBean
     * @return
     */
    public String getarchivesAge(PatientInfoBean patientInfoBean) {
        String archivesage = "";
        if (!TextUtils.isEmpty(patientInfoBean.getAge()) && patientInfoBean.getAgeUnit() != null) {
            PatientSettingConfigEnum.AgeUnit ageUnit = patientInfoBean.getAgeUnit();
            Log.i("MyTag", "age:" + patientInfoBean.getAge() + " unit:" + ageUnit.getName());
            archivesage = patientInfoBean.getAge() + ageUnit.getName();
          /*  switch (ageUnit) {
                case YEAR:
                    age = patientInfoBean.getAge();
                    break;
                case MONTH:
                    age = String.valueOf(Util.parseInt(patientInfoBean.getAge()) / 12);
                    break;
                case DAY:
                    age = String.valueOf(Util.parseInt(patientInfoBean.getAge()) / 365);
                    break;
                default:
                    break;
            }
        }
        if (age.equals("0"))
            age = "1";*/

        }
        return archivesage;
    }

    /**
     * 获取年龄
     *
     * @param patientInfoBean
     * @return
     */
    public String getAge(PatientInfoBean patientInfoBean) {
        String age = "";
        if (!TextUtils.isEmpty(patientInfoBean.getAge()) && patientInfoBean.getAgeUnit() != null) {
            PatientSettingConfigEnum.AgeUnit ageUnit = patientInfoBean.getAgeUnit();
            Log.i("MyTag","age:"+patientInfoBean.getAge()+" unit:"+ageUnit.getName());
            switch (ageUnit) {
                case YEAR:
                    age = patientInfoBean.getAge();
                    break;
                case MONTH:
                    age = String.valueOf(Util.parseInt(patientInfoBean.getAge()) / 12);
                    break;
                case DAY:
                    age = String.valueOf(Util.parseInt(patientInfoBean.getAge()) / 365);
                    break;
                default:
                    break;
            }
        }
        if (age.equals("0"))
            age = "1";
        return age;
    }

    /**
     * 获取性别
     *
     * @param patientInfoBean
     * @return
     */
    public String getSex(PatientInfoBean patientInfoBean) {
        String sex = "";
        if (!TextUtils.isEmpty(patientInfoBean.getSex())) {
            sex = PatientSettingConfigEnum.Sex.values()[Integer.parseInt(patientInfoBean.getSex())].getName();
        }
        return sex;
    }

    /**
     * 绘制1行 患者信息
     * @param patientInfoBean
     */
    public void drawPatientInfoTopLandscapeSimple(PatientInfoBean patientInfoBean){
        int left = rect.left;
        int right = rect.right;
        int top = currentBottomPosition + smartGrid;
        int bottom = top + patientInfoHeight + ONE_PIXEL;

        currentBottomPosition = bottom;

        //top line
        ecgCanvas.drawLine(left,top-ONE_PIXEL, right, top, ptLine);
        //bottom line
        ecgCanvas.drawLine(left,bottom-ONE_PIXEL, right, bottom, ptLine);

        //画患者信息
        String valueTabLong = "\t \t \t \t \t \t \t \t \t \t \t \t";
        String marginTab = "\t \t";

        String patientNumber = String.format("%s:%s",context.getString(R.string.print_export_patient_number),
                patientInfoBean.getPatientNumber() == null ? "" : patientInfoBean.getPatientNumber());
        String patientName = String.format("%s:%s",context.getString(R.string.print_export_patient_name),
                patientInfoBean.getArchivesName() == null ? "" : patientInfoBean.getArchivesName());
        String patientSex = String.format("%s:%s",context.getString(R.string.print_export_patient_sex),
                 getSex(patientInfoBean));
        String age =  getarchivesAge(patientInfoBean);
        String patientAge = String.format("%s:%s ",context.getString(R.string.print_export_patient_age),age);

        String patientApplyDepartment = String.format("%s:%s",context.getString(R.string.print_export_patient_apply_department),
                patientInfoBean.getApplyDepartment() == null ? "" : patientInfoBean.getApplyDepartment());
        String patientApplyDoctor = String.format("%s:%s",context.getString(R.string.print_export_patient_apply_doctor),
                patientInfoBean.getApplyDoctor() == null ? "" : patientInfoBean.getApplyDoctor());
        String patientCheckDepartment = String.format("%s:%s",context.getString(R.string.print_export_patient_check_department),
                patientInfoBean.getCheckDepartment() == null ? "" : patientInfoBean.getCheckDepartment());
        String patientCheckDoctor = String.format("%s:%s",context.getString(R.string.print_export_patient_check_doctor),
                patientInfoBean.getCheckTechnician() == null ? "" : patientInfoBean.getCheckTechnician());
        String patientBedNumber = String.format("%s:%s",context.getString(R.string.print_export_patient_bed_number),
                patientInfoBean.getBedNo() == null ? "" : patientInfoBean.getBedNo());

        StringBuilder sbInfo = new StringBuilder();
        sbInfo.append(patientNumber).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getPatientNumber())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientName).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getArchivesName())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientSex).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getSex())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientAge).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getAge())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientApplyDepartment).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getApplyDepartment())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientApplyDoctor).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getApplyDoctor())){
            sbInfo.append(valueTabLong);
        }

        sbInfo.append(patientCheckDepartment).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getCheckDepartment())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientCheckDoctor).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getCheckTechnician())){
            sbInfo.append(valueTabLong);
        }
        sbInfo.append(patientBedNumber).append(marginTab);
        if(TextUtils.isEmpty(patientInfoBean.getBedNo())){
            sbInfo.append(valueTabLong);
        }

        left += smartGrid;

        //ecgCanvas.drawText(sbInfo.toString(), left, top + textHeight, ptText);

        ecgCanvas.save();
        ecgCanvas.translate(left, top + smartGrid*2);
        String contentResult = String.format("%s",sbInfo.toString());
        StaticLayout staticLayoutResult = new StaticLayout(contentResult, new TextPaint(ptText), right-largeGrid,
                Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        staticLayoutResult.draw(ecgCanvas);
        ecgCanvas.restore();
    }

    /**
     * 绘制多列，患者信息
     * @param patientInfoBean
     */
    public void drawPatientInfoTopLandscapeMulColume(PatientInfoBean patientInfoBean){
        int left = rect.left + smartGrid;
        int right = rect.right;
        int top = currentBottomPosition + smartGrid;
        int bottom = top + patientInfoHeight + ONE_PIXEL;

        int perRectWidth = (drawWidth-largeGrid) / 3;

        //画患者信息
        String patientNumber = String.format("%s:%s",context.getString(R.string.print_export_patient_number),
                patientInfoBean.getPatientNumber() == null ? "" : patientInfoBean.getPatientNumber());
        String patientName = String.format("%s:%s",context.getString(R.string.print_export_patient_name),
                patientInfoBean.getArchivesName() == null ? "" : patientInfoBean.getArchivesName());

        String patientSex = String.format("%s:%s",context.getString(R.string.print_export_patient_sex), patientInfoBean.getSex());
        String age =  patientInfoBean.getAge();
        String patientAge = String.format("%s:%s",context.getString(R.string.print_export_patient_age),age);

        String patientApplyDepartment = String.format("%s:%s",context.getString(R.string.print_export_patient_apply_department),
                patientInfoBean.getApplyDepartment() == null ? "" : patientInfoBean.getApplyDepartment());
        String patientApplyDoctor = String.format("%s:%s",context.getString(R.string.print_export_patient_apply_doctor),
                patientInfoBean.getApplyDoctor() == null ? "" : patientInfoBean.getApplyDoctor());
        String patientCheckDepartment = String.format("%s:%s",context.getString(R.string.print_export_patient_check_department),
                patientInfoBean.getCheckDepartment() == null ? "" : patientInfoBean.getCheckDepartment());
        String patientCheckDoctor = String.format("%s:%s",context.getString(R.string.print_export_patient_check_doctor),
                patientInfoBean.getCheckTechnician() == null ? "" : patientInfoBean.getCheckTechnician());
        String patientBedNumber = String.format("%s:%s",context.getString(R.string.print_export_patient_bed_number),
                patientInfoBean.getBedNo() == null ? "" : patientInfoBean.getBedNo());

        int dividerLeftX = left;
        int dividerRightX = dividerLeftX + perRectWidth / 2;
        ecgCanvas.drawText(patientNumber, dividerLeftX, top + textHeight, ptText);

        ecgCanvas.drawText(patientName, dividerLeftX, top + textHeight*2, ptText);
        ecgCanvas.drawText(patientSex, dividerLeftX, top + textHeight * 3, ptText);

        ecgCanvas.drawText(patientAge, dividerLeftX, top + textHeight * 4, ptText);
        ecgCanvas.drawText(patientApplyDepartment, dividerLeftX, top + textHeight * 5, ptText);

        ecgCanvas.drawText(patientApplyDoctor, dividerLeftX, top + textHeight * 6, ptText);
        ecgCanvas.drawText(patientCheckDepartment, dividerLeftX, top + textHeight * 7, ptText);

        ecgCanvas.drawText(patientCheckDoctor, dividerLeftX, top + textHeight * 8, ptText);
        ecgCanvas.drawText(patientBedNumber, dividerLeftX, top + textHeight * 9, ptText);
    }

    public void drawBottomEcgParamInfoBase(String ecgParamInfo) {
        ptText.setFakeBoldText(false);
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        int left = rect.left + smartGrid;
        int topY = rect.bottom - textHeight;// - smartGrid*2

        ecgCanvas.drawText(ecgParamInfo, left, topY, ptText);
    }

    public void drawBottomOtherInfoBase(String infoTip,String infoPage){
        ptText.setFakeBoldText(false);
        ptText.setTextSize(TEXT_SIZE_NORMAL);

        int left = rect.left + smartGrid;
        int right = rect.right;
        int topY = rect.bottom - smartGrid;//*3

        ecgCanvas.drawText(infoTip, left, topY, ptText);
        //ecgCanvas.drawText(infoPage,right - textHeight*infoPage.length() - smartGrid, topY, ptText);

        ecgCanvas.save();
        ecgCanvas.translate(left, topY - smartGrid*3);
        String contentResult = String.format("%s",infoPage);

        StaticLayout staticLayoutResult = new StaticLayout(contentResult, new TextPaint(ptText), right - largeGrid - smartGrid*2,
                Layout.Alignment.ALIGN_OPPOSITE, 1, 0, false);
        staticLayoutResult.draw(ecgCanvas);
        ecgCanvas.restore();
    }
}
