package com.lepu.ecg500.usb

import com.lepu.ecg500.util.CommConst
import com.net.util.LogUtil
import java.util.concurrent.atomic.AtomicInteger

class UsbData {
    private val readDataBuffer: ByteArray
    private val iTotalBytes = AtomicInteger(0) //总共读取心电数据字节长度
    private val MAX_NUM_BYTES = 65536 * 2 //缓冲字节数组长度

    val totalBytes: Int
        get() = iTotalBytes.get()

    fun startCmd(): ByteArray {
        val cmd = ByteArray(12)
        cmd[0] = 0x7f.toByte()
        cmd[1] = 0xc1.toByte()
        cmd[2] = 0x00.toByte()
        cmd[3] = 0x01.toByte()
        cmd[4] = 0x00.toByte()
        cmd[5] = 0x00.toByte()
        cmd[6] = 0x00.toByte()
        cmd[7] = 0x00.toByte()
        cmd[8] = 0x00.toByte()
        cmd[9] = 0x00.toByte()
        cmd[10] = 0x00.toByte()
        cmd[11] = 0x41.toByte()
        return cmd
    }

    fun stopCmd(): ByteArray {
        val cmd = ByteArray(12)
        cmd[0] = 0x7f.toByte()
        cmd[1] = 0xc1.toByte()
        cmd[2] = 0x00.toByte()
        cmd[3] = 0x02.toByte()
        cmd[4] = 0x00.toByte()
        cmd[5] = 0x00.toByte()
        cmd[6] = 0x00.toByte()
        cmd[7] = 0x00.toByte()
        cmd[8] = 0x00.toByte()
        cmd[9] = 0x00.toByte()
        cmd[10] = 0x00.toByte()
        cmd[11] = 0x42.toByte()
        return cmd
    }

    //7F(头) 81(Type 8导联) 0A(加密+循环(0-F判断是否丢包)) 00 00 06 00 06 00 FA FF 07 00 04 00 06 00 07 00(16byte=8x2) 00(导联脱落位) 00(起搏信号) 27
    fun collectReadData(numBytes: Int, buffer: ByteArray, collectInd: CollectIndex): List<Byte> {
        var num = numBytes
        var status: Byte = 0x00
        var leadOffState: Byte = 0x00
        val statusList: MutableList<Byte> = ArrayList()
        if (num < 1 || 0 == iTotalBytes.get()) {
            collectInd.actualNumBytes = 0
            status = 0x01
            statusList.add(status)
            return statusList
        }
        val totalByteNum = iTotalBytes.get()
        if (num > totalByteNum) {
            num = totalByteNum
            if (num % CommConst.FRAME_COUNT != 0) {
                num = CommConst.FRAME_COUNT * (num / CommConst.FRAME_COUNT)
            }
        }
        if (num + 2 <= totalByteNum) {
            num += 2 //处理一下帧的开头信息
        }
        var copyInd = 0
        var readNum = 0
        collectInd.skipNum = 0
        while (readNum + 22 <= num) {
            val f1Ind = collectInd.iReadIndex
            val f2Ind = (f1Ind + 1) % MAX_NUM_BYTES
            val f19Ind = (f1Ind + 19) % MAX_NUM_BYTES
            if (readDataBuffer[f1Ind].toInt() == 0x7f && readDataBuffer[f2Ind] == 0x81.toByte()) {
                for (i in 0 until CommConst.FRAME_COUNT) {
                    buffer[copyInd] = readDataBuffer[collectInd.iReadIndex]
                    collectInd.iReadIndex++
                    collectInd.iReadIndex %= MAX_NUM_BYTES
                    copyInd++
                    readNum++
                }
                val b = readDataBuffer[f19Ind]
                if (b.toInt() != 0) {
                    leadOffState = b
                }
            } else {
                collectInd.iReadIndex++
                collectInd.iReadIndex %= MAX_NUM_BYTES
                readNum++
                collectInd.skipNum++
            }
        }
        iTotalBytes.addAndGet(-readNum)
        collectInd.actualNumBytes = copyInd
        collectInd.totalUIReadNum += collectInd.actualNumBytes
        statusList.add(status)
        statusList.add(leadOffState)
        return statusList
    }

    @Volatile
    private var iWriteIndex = 0 //已经写入缓冲数组的下标
    init {
        readDataBuffer = ByteArray(MAX_NUM_BYTES)
    }
    @Synchronized
    fun initVal() {
        iWriteIndex = 0
        iTotalBytes.set(0)
    }

    @Synchronized
    fun add(data: ByteArray) {
        // 7f, c2, 00, 00, 00, 81, 08, 01, 00, 56, 31, 2e, 30, 2e, 31, 2e, 31, 5f, 31, 00, 00, fe, //回复帧
        // 7f, 81, 08, 8d, f8, ff, 7f, 00, 80, 00, 80, 00, 80, 00, 80, 00, 80, 00, 80, 00, 00, 0b,  //数据
//        LogUtil.v(bytesToHexString(data))
        if (data[0] == 0x7f.toByte() && data[ 1] == 0x81.toByte()){
            var writeCount = 0
            for (count in data.indices) {
                readDataBuffer[iWriteIndex] = data[count]
                iWriteIndex++
                iWriteIndex %= MAX_NUM_BYTES
                writeCount++
            }
            iTotalBytes.addAndGet(writeCount)
        }
    }

}
 fun bytesToHexString(src: ByteArray?): String {
    val stringBuilder = StringBuilder("")
    if (src == null || src.isEmpty()) {
        return ""
    }
    for (i in src.indices) {
        val v = src[i].toInt() and 0xFF
        val hv = Integer.toHexString(v)
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
        stringBuilder.append(", ")
    }
    return stringBuilder.toString()
}
