package com.xzl.linux;

import com.sun.jna.NativeLibrary;
import com.sun.jna.Platform;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

//方法库
public class TestClientDemo {

    static {
        //loadLib();
    }

    private static void loadLib(){
        try {
            File tmpDir = Files.createTempDirectory(null).toFile();
            //如果平台是windows平台
            //如果平台是windows平台
                //可能是lib64 也可能是lib文件夹下
                tmpDir =new File("/usr/lib64");
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        loadLib();
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
                System.out.println("复制到的目录名称："+ tmpDir + parentFileName+"/" +dllName);
                System.out.println("获取资源名称："+platformPrefix + parentFileName+"/" + dllName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
