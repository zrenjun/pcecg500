package com.lepu.ecg500.util

import com.Carewell.OmniEcg.jni.JniFilter
import com.Carewell.OmniEcg.jni.NotifyFilter


class ECGBytesToShort {
    private var isPowerFrequencyChange = true

    /**
     * 7F(头) 81(Type 8导联) 0A(加密+循环(0-F判断是否丢包)) 00 00 06 00 06 00 FA FF 07 00 04 00 06 00 07 00(16byte=8x2) 00(导联脱落位) 00(起搏信号) 27
     * 循环获取，22个字节一帧，  最后一个字节为前21个字节和的后8位。
     */
    fun bytesToLeadData(readBuffer: ByteArray, actualNum: Int): Array<ShortArray> {
        val shortBuffer =
            Array(CommConst.CUEERNT_LEAD_COUNT) { ShortArray(actualNum / CommConst.FRAME_COUNT) }
        var oneFrameBuffer = ByteArray(CommConst.FRAME_COUNT)
        var isStart = false
        var index = 0
        var last: Short = 0
        var leadIndex = 0 //每次调用都需要清零
        for (i in 0 until actualNum) {
            if (i < actualNum - 1 && readBuffer[i].toInt() == 0x7f) {
                isStart = true
            }
            if (isStart) {
                oneFrameBuffer[index] = readBuffer[i]
                if (index < CommConst.FRAME_COUNT - 1) {
                    last = (last + (readBuffer[i].toInt() and 0xff).toShort()).toShort()
                    index++
                } else {
                    isStart = false
                    val calLast = short8ToByte(last)
                    last = 0
                    index = 0
                    if (calLast == readBuffer[i]) {
                        parseWaveform(oneFrameBuffer, shortBuffer, leadIndex)
                        leadIndex++
                    } else {
                        continue
                    }
                    oneFrameBuffer = ByteArray(CommConst.FRAME_COUNT)
                }
            }
        }
        return shortBuffer
    }


    /**
     * 波形数据解析函数
     */
    private fun parseWaveform(oneFrameBuffer: ByteArray, leadsData: Array<ShortArray>, i: Int) {
        val leadsDataBegin = 3
        for (j in 0 until CommConst.LEAD_COUNT) {
            val low = oneFrameBuffer[leadsDataBegin + j * 2].toInt() and 0xff
            val high = oneFrameBuffer[leadsDataBegin + j * 2 + 1].toInt() and 0xff shl 8
            leadsData[j][i] = (low + high).toShort()
        }
        parseWaveOther4(leadsData, i)
    }

    /**
     * 其他4个派生导联计算方式
     */
    private fun parseWaveOther4(leadsData: Array<ShortArray>, i: Int) {
        //III = II - I
        leadsData[CommConst.LEAD_COUNT][i] = (leadsData[1][i] - leadsData[0][i]).toShort()
        //aVR = -(I + II) / 2;
        leadsData[CommConst.LEAD_COUNT + 1][i] =
            (-(leadsData[1][i] + leadsData[0][i]) / 2).toShort()
        //aVF = (I - III) / 2;
        leadsData[CommConst.LEAD_COUNT + 2][i] =
            ((leadsData[0][i] - leadsData[CommConst.LEAD_COUNT][i]) / 2).toShort()
        //aVL = (II + III) / 2;
        leadsData[CommConst.LEAD_COUNT + 3][i] =
            ((leadsData[1][i] + leadsData[CommConst.LEAD_COUNT][i]) / 2).toShort()
    }

    /**
     * 将12导的顺序由 I/II/V1/V2/V3/V4/V5/V6/III/aVR/aVL/aVF转换为
     * I/II/III/aVR/aVL/aVF/V1/V2/V3/V4/V5/V6
     */
    fun leadSortThe12(leadsData: Array<ShortArray?>): Array<ShortArray?> {
        if (leadsData.size == CommConst.CUEERNT_LEAD_COUNT) {
            val tempData = arrayOfNulls<ShortArray>(CommConst.CUEERNT_LEAD_COUNT)
            for (i in leadsData.indices) {
                if (i <= 1) {
                    tempData[i] = leadsData[i]
                } else if (i + 4 < leadsData.size) {
                    tempData[i + 4] = leadsData[i]
                } else {
                    tempData[i - 6] = leadsData[i] //前面两导保持原样，充第三导开始转换
                }
            }
            return tempData
        }
        return leadsData
    }


    /**
     * 循环数组，过滤信号
     */
    fun filterLeadsData(leadsData: Array<ShortArray>, lp: Int, ac: Int): Array<ShortArray> {

        var dataArray = leadsData
        //新的工频滤波
        if (ac == CommConst.FILTER_HUM_50) {
            val notifyFilter = NotifyFilter()
            notifyFilter.outDataLen = dataArray[0].size
            var mvDataArray = switchEcgDataArray(dataArray)

            notifyFilter.dataArray = mvDataArray
            val powerF = isPowerFrequencyChange

            //50hz 工频
            JniFilter.getInstance().powerFrequency50attenuation(
                mvDataArray,
                mvDataArray[0].size,
                1.0f,
                powerF,
                notifyFilter
            )
            mvDataArray = notifyFilter.dataArray

            dataArray = switchEcgDataArray(mvDataArray)
            isPowerFrequencyChange = false
        }
        return dataArray
    }


    /**
     * 将16位的short的低八位转换成byte
     */
    private fun short8ToByte(s: Short): Byte {
        return (s.toInt() and 0xff).toByte()
    }

    companion object {
        private const val SHORT_MV_GAIN: Float = 5 / 2048f

        fun switchEcgDataArray(dataArray: Array<ShortArray>): Array<FloatArray> {
            val valueDataArray = Array(dataArray.size) { FloatArray(dataArray[0].size) }
            for (i in dataArray.indices) {
                for (j in dataArray[0].indices) {
                    val value = dataArray[i][j]
                    valueDataArray[i][j] = value * SHORT_MV_GAIN
                }
            }
            return valueDataArray
        }

        fun switchEcgDataArray(dataArray: Array<FloatArray>): Array<ShortArray> {
            val valueDataArray = Array(dataArray.size) { ShortArray(dataArray[0].size) }
            for (i in dataArray.indices) {
                for (j in dataArray[0].indices) {
                    val value = dataArray[i][j]
                    valueDataArray[i][j] = (value / SHORT_MV_GAIN).toInt().toShort()
                }
            }
            return valueDataArray
        }
    }
}
