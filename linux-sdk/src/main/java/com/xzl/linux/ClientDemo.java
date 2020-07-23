package com.xzl.linux;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

//方法库
public class ClientDemo {
    private HCNetSDK.FMSGCallBack fMSFCallBack;// alarm callback
    private static final Map<String,Integer> userMap =new HashMap<>();
    private static final Map<String,Integer> alarmMap =new HashMap<>();

    static HCNetSDK hCNetSDK;
    static {
        loadLib();
        hCNetSDK=HCNetSDK.INSTANCE;
    }

    private static void loadLib(){
        try {
            File tmpDir =new File("/usr/lib64");
            String platformPrefix = "linux-x86-64";
            String[] so1 = new String[] {
                    "libAudioRender","libcrypto","libHCCore","libhcnetsdk","libhpr","libNPQos","libPlayCtrl","libssl","libSuperRender"
            };
            //HCNetSDKCom文件夹下的文件
            String[] so2 =new String[] {
                    "libanalyzedata","libHCAlarm","libHCCoreDevCfg","libHCDisplay","libHCGeneralCfgMgr","libHCIndustry","libHCPlayBack","libHCPreview","libHCVoiceTalk","libiconv2","libStreamTransClient","libSystemTransform"
            };
            loadFolder(so1,null,tmpDir,".so",platformPrefix);
            loadFolder(so2,"HCNetSDKCom",tmpDir,".so",platformPrefix);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadFolder(String[] arr,String parentFileName,File tmpDir,String suffix,String platformPrefix){
        try {
            if(parentFileName ==null){
                parentFileName = "";
            }else {
                parentFileName = "/" +parentFileName;
            }
            int count;
            byte[] buf = new byte[1024];
            for (String fileName : arr) {
                String dllName = fileName + suffix;
                File parentFilePath = new File(tmpDir + parentFileName);
                if(!parentFilePath.exists()){
                    parentFilePath.mkdir();
                }
                File fdll = new File(tmpDir , dllName);
                try (FileOutputStream fos = new FileOutputStream(fdll)) {
                    try (InputStream is = ClientDemo.class.getClassLoader()
                            .getResourceAsStream(platformPrefix + parentFileName+"/" + dllName)) {
                        while ((count = is.read(buf, 0, buf.length)) > 0) {
                            fos.write(buf, 0, count);
                        }
                    }
                }
                fdll.deleteOnExit();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public ClientDemo(HCNetSDK.FMSGCallBack callBack) {
        fMSFCallBack = callBack;
    }
    //
    public void CameraInit(){
    	//初始化
        boolean initSuc = hCNetSDK.NET_DVR_Init();
        if (!initSuc){
            System.out.println("初始化失败");
            throw new RuntimeException("设备初始化失败");
        }else{
        	System.out.println("初始化成功");
        }
    }
    //注册
    public int register(String username,String password,String sDeviceIP,short port){
        Integer userId = userMap.get(sDeviceIP);
        if(userId!=null){
            System.out.println("用户已注册注销登录");
            return userId;
        }
        try {
            Thread.sleep(1000);
            NativeLong nlUserID = hCNetSDK.NET_DVR_Login_V30(sDeviceIP, port, username, password, new HCNetSDK.NET_DVR_DEVICEINFO_V30());
            System.out.println("NET_DVR_Login_V40 after");
            long lUserID = nlUserID.longValue();
            if (lUserID == -1) {
                int ierr = hCNetSDK.NET_DVR_GetLastError();
                System.out.println("login error,error code：" + ierr);
            } else {
                System.out.println("注册是否成功"+hCNetSDK.NET_DVR_GetLastError());
                userMap.put(sDeviceIP,nlUserID.intValue());
                return nlUserID.intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //布防
    public void SetupAlarmChan(String deviceIp) {
        if (!hCNetSDK.NET_DVR_SetDVRMessageCallBack_V30(this.fMSFCallBack, (Pointer)null)) {
            System.out.println("设置回调函数失败，失败code:" + hCNetSDK.NET_DVR_GetLastError());
            throw new RuntimeException("设置回调函数失败，失败code:" + hCNetSDK.NET_DVR_GetLastError());
        } else {
            HCNetSDK.NET_DVR_SETUPALARM_PARAM m_strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            m_strAlarmInfo.dwSize = m_strAlarmInfo.size();
            m_strAlarmInfo.byLevel = 1;
            m_strAlarmInfo.byAlarmInfoType = 1;
            m_strAlarmInfo.byDeployType = 1;
            m_strAlarmInfo.write();
            int lAlarmHandle = hCNetSDK.NET_DVR_SetupAlarmChan_V41((Integer)userMap.get(deviceIp), m_strAlarmInfo);
            if (lAlarmHandle == -1) {
                System.out.println("布防失败，错误号:" + hCNetSDK.NET_DVR_GetLastError());
                throw new RuntimeException("布防失败，错误号:+  hCNetSDK.NET_DVR_GetLastError()");
            } else {
                System.out.println("布防成功，失败code:" + hCNetSDK.NET_DVR_GetLastError());
                System.out.println("布防成功");
                alarmMap.put(deviceIp, lAlarmHandle);
            }
        }
    }
    /**
     * 撤防
     */
    public boolean Logout(String deviceIp) {
        //报警撤防
        Integer alarmHandle = alarmMap.get(deviceIp);
        if (alarmHandle!=null&&alarmHandle > -1) {
            if(!hCNetSDK.NET_DVR_CloseAlarmChan_V30(new NativeLong(alarmHandle))) {
                System.out.println("撤防失败");
                return false;
            }else {
                alarmMap.remove(deviceIp);
            }
        }
        //注销
        Integer userId = userMap.get(deviceIp);
        if (userId!=null&&userId > -1) {
            if(hCNetSDK.NET_DVR_Logout(new NativeLong(userId))) {
                System.out.println("注销成功");
                userMap.remove(deviceIp);
            }
        }
        return true;
    }
}
