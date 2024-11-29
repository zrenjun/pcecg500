package com.lepu.ecg500.util

import com.Carewell.OmniEcg.jni.JniFilterNew
import com.ecg.entity.NotifyFilterBean


class ECGBytesToShort {

    fun bytesToLeadData(isOldDevice: Boolean, readBuffer: ByteArray, actualNum: Int):  Array<ShortArray> {
        return if (isOldDevice) {
            oldBytesToLeadData(readBuffer, actualNum)
        } else {
            newBytesToLeadData(readBuffer, actualNum)
        }
    }

    /**
     * 7F(头) 81(Type 8导联) 0A(加密+循环(0-F判断是否丢包)) 00 00 06 00 06 00 FA FF 07 00 04 00 06 00 07 00(16byte=8x2) 00(导联脱落位) 00(起搏信号) 27
     * 循环获取，22个字节一帧，  最后一个字节为前21个字节和的后8位。
     */
   private fun newBytesToLeadData(readBuffer: ByteArray, actualNum: Int): Array<ShortArray> {
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
     * 循环获取，16个字节一帧，16个字节中第一个字节值为0x0f，第二个字节告诉8个导联那几个脱落
     * 后面的12位依次代表8导联的数值，然后后面的一个字节值0xff，最后一个字节为前15个字节和的后8位。
     * 1+1+12+1+1
     */
    private  fun oldBytesToLeadData(readBuffer: ByteArray, actualNum: Int): Array<ShortArray> {
        val shortBuffer =
            Array(CommConst.CUEERNT_LEAD_COUNT) { ShortArray(actualNum / 16) }
        var one_frame_buffer = ByteArray(16)
        var isStart = false
        var index = 0
        var last: Short = 0
        var LEAD_GROUP_INDEX = 0 //每次调用都需要清零
        for (i in 0 until actualNum) {
            if (i < actualNum - 1 && readBuffer[i].toInt() == 0x0f) {
                isStart = true
            }
            if (isStart) {
                one_frame_buffer[index] = readBuffer[i]
                if (index < 16 - 1) {
                    last = (last + (readBuffer[i].toInt() and 0xff)).toShort()
                    index++
                } else {
                    isStart = false
                    val calLast = short8ToByte(last)
                    last = 0
                    index = 0
                    if (calLast == readBuffer[i]) {
                        oldParseWaveform(one_frame_buffer, shortBuffer, LEAD_GROUP_INDEX)
                        LEAD_GROUP_INDEX++
                    } else {
                        continue
                    }
                    one_frame_buffer = ByteArray(16)
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
        leadsData[CommConst.LEAD_COUNT + 1][i] = (-(leadsData[1][i] + leadsData[0][i]) / 2).toShort()
        //aVF = (I - III) / 2;
        leadsData[CommConst.LEAD_COUNT + 2][i] = ((leadsData[0][i] - leadsData[CommConst.LEAD_COUNT][i]) / 2).toShort()
        //aVL = (II + III) / 2;
        leadsData[CommConst.LEAD_COUNT + 3][i] = ((leadsData[1][i] + leadsData[CommConst.LEAD_COUNT][i]) / 2).toShort()
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
        if (ac == CommConst.FILTER_HUM_50) {//50hz 工频
            val notifyFilter = NotifyFilterBean()
            notifyFilter.outDataLen = dataArray[0].size
            notifyFilter.dataArray = switchEcgDataArray(dataArray)
            JniFilterNew.getInstance().powerFrequency50(
                leadsData.map { it.map { it.toInt() }.toIntArray() }.toTypedArray(),
                notifyFilter.dataArray.size,
                notifyFilter,
                12
            )
            dataArray = switchEcgDataArray(notifyFilter.dataArray)
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


    /**
     * 波形数据解析函数
     */
    private fun oldParseWaveform(one_frame_buffer: ByteArray, leads_data: Array<ShortArray>, i: Int) {
        //原始波形数据的放大倍数。
        val avm = 2.556

        //原始波形数据的基础值。
        val waveform_base_value = 0x800 //计算正负100000000000(二进制)

        // one_frame_buffer 正好保存了一帧的数据。
        val leads_data_begin = 2

        //一次计算两个导联的数据
        for (j in 0 until CommConst.LEAD_COUNT / 2) {
            val k = j * 2
            val m = j * 3

            //循环计算I、V1、V3、V5导联。
            val low1 = one_frame_buffer[leads_data_begin + m].toInt() and 0xff
            var high1 = one_frame_buffer[leads_data_begin + m + 1].toInt() and 0xff
            high1 = high1 and 0x0F
            high1 = high1 shl 8
            leads_data[k][i] = (low1 + high1).toShort()
            leads_data[k][i] = (leads_data[k][i] - waveform_base_value).toShort()
            leads_data[k][i] = (leads_data[k][i] * avm.toInt()).toShort()

            //循环计算II、V2、V4、V6导联。
            var low2 = one_frame_buffer[leads_data_begin + m + 1].toInt() and 0xff
            var high2 = one_frame_buffer[leads_data_begin + m + 2].toInt() and 0xff
            low2 = low2 and 0xF0
            low2 = low2 shr 4
            high2 = high2 shl 4
            leads_data[k + 1][i] = (low2 + high2).toShort()
            leads_data[k + 1][i] = (leads_data[k + 1][i] - waveform_base_value).toShort()
            leads_data[k + 1][i] = (leads_data[k + 1][i] * avm.toInt()).toShort()
        }
        parseWaveOther4(leads_data, i)
    }
}
