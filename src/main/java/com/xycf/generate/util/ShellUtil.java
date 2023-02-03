package com.xycf.generate.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @Author ztc
 * @Description 命令行工具类
 * @Date 2023/2/3 10:44
 */
@Slf4j
public class ShellUtil {


    public static void excuete(String rarFilePath, String targetPath) {
        // 获取windows中 WinRAR.exe的路径
        boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
        final boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");

        // 开始调用命令行解压，参数-o+是表示覆盖的意思
        String cmd ="";
        if(isWin) {
            cmd = "rar.exe" + " X -o+ " + rarFilePath + " " + targetPath;
        }else if(isLinux){
            //如果linux做了软连接 不需要这里配置路径
            String cmdPath = "/usr/local/bin/unrar";
            cmd = "rar" + " X -o+ " + rarFilePath + " " + targetPath;
        }
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            if (proc.waitFor() != 0) {
                if (proc.exitValue() == 0) {
                    log.warn("解压失败");
                }
            } else {
                log.warn("解压成功");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
