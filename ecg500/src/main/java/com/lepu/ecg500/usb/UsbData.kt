package com.lepu.ecg500.usb;

import com.lepu.ecg500.util.CommConst;
import com.net.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xinqi on 2018-07-13.
 */

public class UsbData {

    // data buffer
    private byte[] readDataBuffer; /* circular buffer */
    private AtomicInteger iTotalBytes = new AtomicInteger(0); //总共读取心电数据字节长度
    private static final int DATA_FRAME_LENGH = 16;
    final int MAX_NUM_BYTES = 65536 * 2;    //缓冲字节数组长度 65536


    public int getTotalBytes() {
        return iTotalBytes.get();
    }

    public UsbData() {
        readDataBuffer = new byte[MAX_NUM_BYTES];//MAX_NUM_BYTES
    }


    public List<Byte> collectReadData(int numBytes, byte[] buffer, CollectIndex collectInd) {
        byte status = 0x00; /* success by default */
        byte leadOffState = 0x00;
        List<Byte> statusList = new ArrayList();
        /* should be at least one byte to read */
        if ((numBytes < 1) || (0 == iTotalBytes.get())) {
            collectInd.actualNumBytes = 0;
            status = 0x01;
            statusList.add(status);
            return statusList;
        }

        int totalByteNum = iTotalBytes.get();
        if (numBytes > totalByteNum) {
            //first time
            numBytes = totalByteNum;
            if (numBytes % DATA_FRAME_LENGH != 0) {
                numBytes = DATA_FRAME_LENGH * (numBytes / DATA_FRAME_LENGH);
            }
        }

        if (numBytes + 2 <= totalByteNum) {
            numBytes = numBytes + 2; //处理一下帧的开头信息
        }

        int copyInd = 0;
        int readNum = 0;
        collectInd.skipNum = 0;
        while (readNum + 18 <= numBytes) {
            int f1Ind = collectInd.iReadIndex;
            int f2Ind = (f1Ind + 1) % MAX_NUM_BYTES;
            int f15Ind = (f1Ind + 14) % MAX_NUM_BYTES;
            int f17Ind = (f1Ind + 16) % MAX_NUM_BYTES;
            int f18Ind = (f1Ind + 17) % MAX_NUM_BYTES;


            if (readDataBuffer[f1Ind] == 0x0f &&
                    (readDataBuffer[f2Ind] == 0x01 || readDataBuffer[f2Ind] == 0x03) &&
                    readDataBuffer[f17Ind] == 0x0f &&
                    (readDataBuffer[f18Ind] == 0x01 || readDataBuffer[f18Ind] == 0x03)) {
                for (int i = 0; i < CommConst.FRAME_COUNT; i++) {
                    buffer[copyInd] = readDataBuffer[collectInd.iReadIndex];

                    collectInd.iReadIndex++;
                    collectInd.iReadIndex = collectInd.iReadIndex % MAX_NUM_BYTES;
                    copyInd++;
                    readNum++;
                }
                if (readDataBuffer[f2Ind] == 0x03) {
                    byte b = readDataBuffer[f15Ind];
                    if (b != 0) {
                        leadOffState = b;
                    }
                }
            } else {
                collectInd.iReadIndex++;
                collectInd.iReadIndex = collectInd.iReadIndex % MAX_NUM_BYTES;
                readNum++;
                collectInd.skipNum++;
            }
        }

        iTotalBytes.addAndGet(-readNum);
        collectInd.actualNumBytes = copyInd;
        collectInd.totalUIReadNum = collectInd.totalUIReadNum + collectInd.actualNumBytes;
        statusList.add(status);
        statusList.add(leadOffState);
        return statusList;
    }


    private boolean startWrite = false;
    private volatile int iWriteIndex = 0; //已经写入缓冲数组的下标

    public synchronized void initVal() {
        iWriteIndex = 0;
        iTotalBytes.set(0);
        startWrite = false;
    }

    public synchronized void add(byte[] data) {

        int writeCount = 0;
        for (int count = 0; count < data.length; count++) {
            if (!startWrite) {
                if (count < data.length - 1 && data[count] == 0x0f
                        && (data[count + 1] == 0x01 || data[count + 1] == 0x03)) {
                    startWrite = true;
                } else {
                    continue;
                }
            }
            readDataBuffer[iWriteIndex] = data[count];
            iWriteIndex++;
            iWriteIndex %= MAX_NUM_BYTES;
            writeCount++;
        }
        iTotalBytes.addAndGet(writeCount);
    }
}
