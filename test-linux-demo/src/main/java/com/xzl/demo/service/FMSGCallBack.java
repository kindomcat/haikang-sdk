package com.xzl.demo.service;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.xzl.linux.HCNetSDK;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @desc:
 * @author: XZL
 * @createTime: 2020/7/23 14:06
 * @version: v0.0.1
 * @history:
 */
public class FMSGCallBack implements HCNetSDK.FMSGCallBack {

    @Override
    public void invoke(NativeLong lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, HCNetSDK.RECV_ALARM pAlarmInfo,
            int dwBufLen, Pointer pUser) {
        AlarmDataHandle(lCommand.intValue(), pAlarmer, pAlarmInfo.getPointer());
    }

    private void AlarmDataHandle(int lCommand, HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo) {
        Date date = new Date();
        System.out.println("收到布防消息===================================================================================");
        if (lCommand == HCNetSDK.COMM_ALARM_RULE) {
            HCNetSDK.NET_VCA_RULE_ALARM strVcaAlarm = new HCNetSDK.NET_VCA_RULE_ALARM();
            strVcaAlarm.write();
            Pointer pVcaInfo = strVcaAlarm.getPointer();
            pVcaInfo.write(0, pAlarmInfo.getByteArray(0, strVcaAlarm.size()), 0, strVcaAlarm.size());
            strVcaAlarm.read();
            String ip = new String(strVcaAlarm.struDevInfo.struDevIP.sIpV4).trim();
            int typeEx = strVcaAlarm.struRuleInfo.wEventTypeEx;
            if (strVcaAlarm.dwPicDataLen > 0) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
                String newName = sf.format(date);
                FileOutputStream fout;
                try {
                    String fileUrl = "/home/" + new String(pAlarmer.sDeviceIP).trim() + "wEventTypeEx_" + strVcaAlarm.struRuleInfo.wEventTypeEx + "_" + newName + "_vca.jpg";
                    fout = new FileOutputStream(fileUrl);
                    //将字节写入文件
                    long offset = 0;
                    ByteBuffer buffers = strVcaAlarm.pImage.getByteBuffer(offset, strVcaAlarm.dwPicDataLen);
                    byte[] bytes = new byte[strVcaAlarm.dwPicDataLen];
                    buffers.rewind();
                    buffers.get(bytes);
                    fout.write(bytes);
                    fout.close();
                } catch (Exception e) {
                    System.out.println("保存图片发生异常！");
                    e.printStackTrace();
                }
            }
        }
    }
}
