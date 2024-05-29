package com.lepu.ecg500.util

import com.hoho.android.usbserial.driver.UsbSerialPort


object CommConst {
    const val FILTER_LOW_PASS_75: Int = 75 //低通75
    const val FILTER_LOW_PASS_90: Int = 90 //低通90
    const val FILTER_LOW_PASS_100: Int = 100 //低通100
    const val FILTER_LOW_PASS_165: Int = 165 //低通165
    const val FILTER_EMG_25: Int = 25 //肌电25
    const val FILTER_EMG_35: Int = 35 //肌电25
    const val FILTER_EMG_45: Int = 45 //肌电25
    const val FILTER_HUM_50: Int = 50 //工频25

    /**
     * 波特率setup the baud rate
     */
    const val FT230X_BAUD_RATE: Int = 460800

    /**
     * 停止标签值 stop bits
     */
    const val FT230X_STOP_BIT: Byte = 1

    /**
     * 数据字节数量 data bits
     */
    const val FT230X_DATA_BIT: Byte = 8

    /**
     * parity
     */
    const val FT230X_PARITY: Byte = UsbSerialPort.PARITY_NONE.toByte()

    /**
     * 一帧包含的字节数量
     */
    const val FRAME_COUNT: Int = 22

    /**
     * 导联数量
     */
    const val LEAD_COUNT: Int = 8 //导联数量

    /**
     * 需要显示的总共导联数量
     */
    const val CUEERNT_LEAD_COUNT: Int = 12
}
