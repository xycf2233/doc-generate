package com.xycf.generate.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.xycf.generate.config.DocConfig;
import com.xycf.generate.config.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @Author ztc
 * @Date 2023/2/1 10:24
 */
@Component
@Slf4j
public class FileUtil {

    @Resource
    private DocConfig docConfig;
    private static String[] SUFFIX_NAMES = {".zip", ".rar", ".7z"};
    private static String ZIP = ".zip";
    private static String RAR = ".rar";
    private static String SEVEN_Z = ".7z";

    /**
     * 使用MD5编码成32位字符串
     *
     * @param inputStr
     * @return String
     */
    public static String encodeInMD5(final String inputStr) {
        if (StringUtils.isEmpty(inputStr)) {
            return inputStr;
        }
        return DigestUtils.md5DigestAsHex(inputStr.getBytes());
    }

    /**
     * 生成存放文件路径
     *
     * @return path
     */
    public static String generateRelativeDir() {
        // 当前日期
        final Date date = new Date();
        // 格式化并转换String类型
        final String path = new SimpleDateFormat("yyyy/MM/dd").format(date);
        return path;
    }

    /**
     * 生成FileId   例如:201806181701001+7位随机数
     *
     * @return fileId
     */
    public static String createFileId() {
        StringBuffer path = new StringBuffer();
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String time = format.format(date);

        path.append(time);

        Random random = new Random();
        int numCount = 7;
        for (int i = 0; i < numCount; i++) {
            path.append(String.valueOf(random.nextInt(10)));
        }
        return path.toString();
    }

    /**
     * 删除指定文件
     *
     * @param path
     */
    public static void deleteFile(final String path) {
        final File file = new File(path);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 删除文件夹及其下的所有子文件夹和文件
     * @param folderPath
     */
    public static void deleteFolder(String folderPath) {
        try {
            File baseFolder = new File(folderPath);
            if (baseFolder.exists()) {
                deleteAllFileInFolder(folderPath);
                baseFolder.delete();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public static boolean deleteAllFileInFolder(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                deleteAllFileInFolder(path + File.separator + tempList[i]);
                deleteFolder(path + File.separator + tempList[i]);
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 移动文件（强制覆盖）
     *
     * @param srcFileName      源文件完整路径
     * @param destDirName      目的目录完整路径
     * @param originalFileName 保存的文件名称
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFile(final String srcFileName, final String destDirName, String originalFileName) {

        //源文件
        final File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        //目标文件夹
        final File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        // 固定文件存放路径
        return srcFile.renameTo(new File(destDirName + File.separator + originalFileName));
    }

    /**
     * 移动文件（强制覆盖）
     *
     * @param srcFileName      源文件完整路径
     * @param destDirName      目的目录完整路径
     * @param originalFileName 保存的文件名称
     * @return 文件移动成功返回true，否则返回false
     */
    public static boolean moveFileForce(final String srcFileName, final String destDirName, String originalFileName) {

        //源文件
        final File srcFile = new File(srcFileName);
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        //目标文件夹
        final File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        //目标文件
        File desFile = new File(destDirName + File.separator + originalFileName);
        //目标文件存在，且不与源文件在同一目录，删除目标文件
        if (desFile.exists() && !srcFile.getPath().equals(desFile.getPath())) {
            desFile.delete();
        }
        // 固定文件存放路径
        return srcFile.renameTo(desFile);
    }

    /**
     * 复制文件
     *
     * @param srcFileName
     * @param destDirName
     * @param originalFileName
     */
    public static void copyFile(final String srcFileName, String destDirName, String originalFileName) {
        final File destDir = new File(destDirName);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(srcFileName).getChannel();
            outputChannel = new FileOutputStream(destDirName + File.separator + originalFileName).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 从http Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static boolean downLoadFromUrl(String urlStr, String fileName, String savePath) {
        URL url = null;
        HttpURLConnection conn = null;
        FileOutputStream fos = null;
        InputStream is = null;
        try {
            url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(30 * 1000);
            is = conn.getInputStream();
            byte[] getData;
            byte[] buffer = new byte[1024];
            int len = 0;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            getData = bos.toByteArray();
            bos.close();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdir();
            }
            File file = new File(saveDir + File.separator + fileName);
            fos = new FileOutputStream(file);
            fos.write(getData);
            return true;
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * 解压缩zip包
     *
     * @param zipFilePath zip文件的全路径
     * @param targetPath  解压后的文件保存的路径
     * @param isInSameDir  是否将压缩包内的所有文件解压到同一个文件夹下
     * @return
     */
    public static String unzip(String zipFilePath, String targetPath, boolean isInSameDir) {
        StringBuffer msg = new StringBuffer();
        OutputStream os = null;
        InputStream is = null;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath, Charset.forName("GBK"));
            File directoryFile = new File(targetPath);
            if (!directoryFile.exists()) {
                directoryFile.mkdir();
            }
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                ZipEntry zipEntry = null;
                int len = 4096;
                String zipFileName = "";
                while (entryEnum.hasMoreElements()) {
                    zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (zipEntry.getSize() > 0) {
                        zipFileName = zipEntry.getName();
                        // 处理解压文件保存到同一目录
                        if (isInSameDir) {
                            if (zipFileName.indexOf("/") > -1) {
                                zipFileName = zipFileName.substring(zipFileName.lastIndexOf("/") + 1, zipFileName.length());
                                if (zipFileName.startsWith("~$")) {
                                    continue;
                                }
                            }
                        }
                        //判断文件是否已经存在。
                        if (checkFileExistence(targetPath, zipFileName)) {
                            //msg.setLength(0);
                            msg.append("存在重复的文件名：" + zipFileName);
                        }
                        // 文件
                        File targetFile = new File(targetPath + File.separator + zipFileName);
                        os = new BufferedOutputStream(new FileOutputStream(targetFile));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, len)) >= 0) {
                            os.write(buffer, 0, readLen);
                            os.flush();
                        }
//                        if (zipFileName.lastIndexOf(".") > -1) {
//                            String suffix = zipFileName.substring(zipFileName.lastIndexOf("."), zipFileName.length()).toLowerCase();
//                            if (zipFileName.lastIndexOf(".") > -1 && Arrays.asList(SUFFIX_NAMES).contains(suffix)) {
//                                String ret = unCompressedFilesToSameDir(targetPath + File.separator + zipFileName, targetPath, suffix);
//                                if (!StringUtils.isEmpty(ret)) {
//                                    //msg.setLength(0);
//                                    msg.append(ret);
//                                }
//                            }
//                        }
                    }
                    if (zipEntry.isDirectory()) {
                        File file = new File(targetPath + File.separator + zipEntry.getName());
                        if (!file.exists()) {
                            file.mkdir();
                        }
                    }
                    if (null != is) {
                        is.close();
                    }
                    if (null != os) {
                        os.close();
                    }
                }
            }

            closeZipStream(zipFile, is, os);
            //如果解压的文件在在目标文件夹中，将其删除
            if (zipFilePath.contains(targetPath)) {
                new File(zipFilePath).delete();
            }

            if (isInSameDir) {
                String ret = moveToRootDir(targetPath);
                if (!CharSequenceUtil.isEmpty(ret)) {
                    msg.append(ret);
                }
            }

            if (null != directoryFile) {
                StringBuffer stringBuffer = unFile(directoryFile, targetPath, isInSameDir);
                msg.append(stringBuffer);
            }


            return msg.toString();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            closeZipStream(zipFile, is, os);
        }
        return msg.toString();
    }


    public static StringBuffer unFile(File sourceFile, String targetPath, boolean isInSameDir) {
        StringBuffer msg = new StringBuffer();
        File[] sourceFiles = sourceFile.listFiles();
        for (File sf : sourceFiles) {
            if (sf.isFile() && sf.length() > 0) {
                String filePath = sf.getPath();
                if (filePath.lastIndexOf(".") > -1) {
                    //获取文件的后缀
                    String suffix = filePath.substring(filePath.lastIndexOf("."), filePath.length()).toLowerCase();
                    //如果是压缩包，继续解压缩，也就是解压到目标目录下没有rar文件位置
                    if (filePath.lastIndexOf(".") > -1 && Arrays.asList(SUFFIX_NAMES).contains(suffix)) {
                        String ret = unCompressedFilesToSameDir(filePath, targetPath, suffix);
                        if (!StringUtils.isEmpty(ret)) {
                            msg.append(ret);
                        }
                    }
                }
            }

            if (sf.isDirectory()) {
                if (isInSameDir) {
                    unFile(sf, targetPath, isInSameDir);
                } else {
                    unFile(sf, sf.getAbsolutePath(), isInSameDir);
                }
            }
        }
        return msg;
    }

    /**
     * 判断解压的文件在解压目录下是否存在
     *
     * @param targetPath
     * @param zipFileName
     * @return
     */
    private static boolean checkFileExistence(String targetPath, String zipFileName) {
        boolean exist = false;
        File target = new File(targetPath);
        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (File file : files) {
                if (!file.isDirectory()) {
                    String fileName = file.getName();
                    if (fileName.equals(zipFileName)) {
                        exist = true;
                        break;
                    }
                }
            }
        }
        return exist;
    }

    /**
     * 关闭zip流
     *
     * @param zipFile
     * @param is
     * @param os
     */
    private static void closeZipStream(ZipFile zipFile, InputStream is, OutputStream os) {
        if (null != zipFile) {
            try {
                zipFile.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (null != is) {
            try {
                is.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (null != os) {
            try {
                os.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 解压缩rar包
     * @param rarFilePath rar文件的全路径
     * @param targetPath  解压后的文件保存的路径
     * @param isInSameDir  是否将压缩包内的所有文件解压到同一个文件夹下
     * @return
     */
    public static String unrar(String rarFilePath, String targetPath, boolean isInSameDir) {
        StringBuffer msg = new StringBuffer();
        OutputStream os = null;
        InputStream is = null;
        ZipFile zipFile = null;
        try {
            File rarFile = new File(rarFilePath);
            //先解压到指定路径
            File directoryFile = new File(targetPath);
            if (!directoryFile.exists()) {
                directoryFile.mkdir();
            }
            //解压
            ShellUtil.excuete(rarFilePath,targetPath);
            // 把解压后所有子目录的文件都移入目标目录
            if (isInSameDir) {
                String ret = moveToRootDir(targetPath);
                if (!StringUtils.isEmpty(ret)) {
                    msg.setLength(0);
                    msg.append(ret);
                }
            }
            //如果解压的文件在在目标文件夹中，将其删除
            if (rarFilePath.contains(targetPath)) {
                rarFile.delete();
            }
            //目标目录的全部文件
            File folder = new File(targetPath);
            File[] files = folder.listFiles();
            if (null != files) {
                //遍历文件夹下的所有文件
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    //如果是文件
                    if (file.length() > 0) {
                        String filePath = file.getPath();
                        if (filePath.lastIndexOf(".") > -1) {
                            //获取文件的后缀
                            String suffix = filePath.substring(filePath.lastIndexOf("."), filePath.length()).toLowerCase();
                            //如果是压缩包，继续解压缩，也就是解压到目标目录下没有rar文件位置
                            if (filePath.lastIndexOf(".") > -1 && Arrays.asList(SUFFIX_NAMES).contains(suffix)) {
                                String ret = unCompressedFilesToSameDir(filePath, targetPath, suffix);
                                if (!CharSequenceUtil.isEmpty(ret)) {
                                    msg.setLength(0);
                                    msg.append(ret);
                                }
                            }
                        }
                    }
                    //如果是文件夹
                    if (file.isDirectory()) {
                        File fileFilder = new File(targetPath + File.separator + file.getName());
                        if (!fileFilder.exists()) {
                            fileFilder.mkdir();
                        }
                    }
                }
            }
            return msg.toString();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        } finally {
            closeZipStream(zipFile, is, os);
        }
        return msg.toString();
    }

//    /*
//     * 通过递归得到某一路径下所有的目录及其文件
//     */
//    private void getFiles(String filePath) {
//        try {
//            File srcfile = new File(filePath);
//            File[] files = srcfile.listFiles();
//            if (srcfile.exists()) {
//                if (files.length == 0) {
//                    throw new AppException("50000","上传文件夹是空的！");
//                } else {
//                    for (File file : files) {
//                        if (file.isDirectory()) {
//                            /*
//                             * 递归调用
//                             */
//                            getFiles(file.getAbsolutePath());
//                        } else {
//                            String name = file.getName();
//                            // File转MultipartFile
//                            FileInputStream input = new FileInputStream(file);
//                            MultipartFile multipartFile1 = new MockMultipartFile("file", name, MediaType.TEXT_PLAIN_VALUE, input);
//                            unrar(multipartFile1,name);
//                            input.close();
//                        }
//                    }
//                }
//            } else {
//                log.warn("上传文件解压失败！");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }finally {
//            //删除文件和文件压缩包
//            deleteFolder(docConfig.getUpZip()+File.separator+filePath);
//        }
//    }

    /**
     * 移动目录下的所有文件到根目录
     *
     * @param filePath
     */
    public static String moveToRootDir(String filePath) {
        String msg = "";
        List<Map<String, String>> allList = new ArrayList<>();
        File baseFile = new File(filePath);
        if (baseFile.isDirectory()) {
            for (File file : baseFile.listFiles()) {
                if (file.isDirectory()) {
                    getFiles(file.getAbsolutePath(), allList);
                }
            }
        }
        if (allList != null && allList.size() > 0) {
            for (int i = 0; i < allList.size(); i++) {
                //判断有没有重名的文件
                String fileName = allList.get(i).get("fileName");
                if (checkFileExistence(filePath, fileName)) {
                    msg = "存在重复的文件名：" + fileName;
                }
                FileUtil.moveFile(allList.get(i).get("filePath"), filePath, fileName);
            }
        }
        return msg;
    }

    /**
     * 获取一个目录下所有路径
     *
     * @param path
     * @param mapList
     * @return
     */
    public static List<Map<String, String>> getFiles(String path, List<Map<String, String>> mapList) {
        File file = new File(path);
        Map<String, String> map = new HashMap<>(5);
        // 如果这个路径是文件夹
        if (file.isDirectory()) {
            // 获取路径下的所有文件
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                // 如果还是文件夹 递归获取里面的文件 文件夹
                if (files[i].isDirectory()) {
                    getFiles(files[i].getPath(), mapList);
                } else {
                    map = new HashMap<>(5);
                    map.put("fileName", files[i].getName());
                    map.put("filePath", files[i].getAbsolutePath());
                    mapList.add(map);
                }
            }
        } else {
            System.out.println("文件：" + file.getPath());
        }
        return mapList;
    }
//
//    /**
//     * 通过linux命令解压rar4以上版本的压缩包
//     *
//     * @param rarFilePath
//     * @param targetPath
//     */
//    public static void unrarNewVersion(String rarFilePath, String targetPath) {
//
//        File saveDir = new File(targetPath);
//        if (!saveDir.exists()) {
//            saveDir.mkdir();
//        }
//        String command = "unrar X -o+  " + rarFilePath + " " + targetPath;
//        ShellUtil.excuete(command);
//    }

    /**
     * 解压缩7z包
     *
     * @param rarFilePath
     * @param targetPath
     * @param isInSameDir
     * @return
     */
    public static String unSevenZ(String rarFilePath, String targetPath, boolean isInSameDir) {
        StringBuffer msg = new StringBuffer();
        try {
            SevenZFile sevenZFile = new SevenZFile(new File(rarFilePath));
            SevenZArchiveEntry entry = sevenZFile.getNextEntry();
            while (entry != null) {
                if (entry.isDirectory()) {
                    new File(targetPath + File.separator + entry.getName()).mkdirs();
                    entry = sevenZFile.getNextEntry();
                    continue;
                }
                String sevenZFileName = entry.getName();
                if (isInSameDir) {
                    if (sevenZFileName.indexOf("/") > -1) {
                        sevenZFileName = sevenZFileName.substring(sevenZFileName.lastIndexOf("/"), sevenZFileName.length());
                        if (sevenZFileName.startsWith("~$")) {
                            entry = sevenZFile.getNextEntry();
                            continue;
                        }
                    }
                }
                if (checkFileExistence(targetPath, sevenZFileName)) {
                    msg.setLength(0);
                    msg.append("存在重复的文件名：" + sevenZFileName);
                }
                FileOutputStream out = new FileOutputStream(targetPath
                        + File.separator + sevenZFileName);
                byte[] content = new byte[(int) entry.getSize()];
                sevenZFile.read(content, 0, content.length);
                out.write(content);
                out.close();
                entry = sevenZFile.getNextEntry();
                if (sevenZFileName.lastIndexOf(".") > -1) {
                    String suffix = sevenZFileName.substring(sevenZFileName.lastIndexOf("."), sevenZFileName.length()).toLowerCase();
                    if (Arrays.asList(SUFFIX_NAMES).contains(suffix)) {
                        String ret = unCompressedFilesToSameDir(targetPath + File.separator + sevenZFileName, targetPath, suffix);
                        if (!StringUtils.isEmpty(ret)) {
                            msg.setLength(0);
                            msg.append(ret);
                        }
                    }
                }
            }
            sevenZFile.close();
            return msg.toString();
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return msg.toString();
    }

    /**
     * 构建目录
     *
     * @param outputDir 输出目录
     * @param subDir    子目录
     */
    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        // 子目录不为空
        if (!(subDir == null || "".equals(subDir.trim()))) {
            file = new File(outputDir + File.separator + subDir);
        }
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.mkdirs();
        }
    }

    /**
     * 解压缩文件到同一文件夹下
     * [.rar .zip .7z]
     *
     * @param compressedFilePath 压缩文件的路径，
     * @param targetPath         解压后保存的路径
     * @param suffix             压缩文件后缀名
     */
    public static String unCompressedFilesToSameDir(String compressedFilePath, String targetPath, String suffix) {
        String msg = "";
        if (!StringUtils.isEmpty(suffix)) {
            suffix = suffix.toLowerCase();
            if (RAR.equals(suffix)) {
                // TODO: 2023/2/2 经过测试  rar4可以解压  rar不行
                msg = FileUtil.unrar(compressedFilePath, targetPath, true);
            }
            if (ZIP.equals(suffix)) {
                msg = FileUtil.unzip(compressedFilePath, targetPath, true);
            }
            if (SEVEN_Z.equals(suffix)) {
                msg = FileUtil.unSevenZ(compressedFilePath, targetPath, true);
            }
        }
        log.info("{}解压成功",targetPath);
        return msg;
    }

    public static boolean fileToZip(String sourceFilePath, String zipFilePath, String fileName) {
        boolean flag = false;
        File sourceFile = new File(sourceFilePath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        if (sourceFile.exists() == false) {
            System.out.println("待压缩的文件目录：" + sourceFilePath + "不存在.");
        } else {
            try {
                File path = new File(zipFilePath);
                if (path.exists() == false) {
                    path.mkdirs();
                }
                File zipFile = new File(zipFilePath + "/" + fileName + ".zip");
                if (zipFile.exists()) {
                    System.out.println(zipFilePath + "目录下存在名字为:" + fileName + ".zip" + "打包文件.");
                } else {
                    fos = new FileOutputStream(zipFile);
                    zos = new ZipOutputStream(new BufferedOutputStream(fos));
                    recursion(zos, sourceFile, sourceFile, fileName);
                    flag = true;
                }
            } catch (FileNotFoundException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            } finally {
                //关闭流
                try {
                    if (null != bis) {
                        bis.close();
                    }
                    if (null != zos) {
                        zos.close();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }
        }
        return flag;
    }

    private static void recursion(ZipOutputStream zos, File sourceFile, File basePath, String fileName) throws IOException {
        File[] sourceFiles = sourceFile.listFiles();
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            if (null == sourceFiles || sourceFiles.length < 1 || null == basePath || basePath.exists() == false) {
                System.out.println("待压缩的文件目录：" + sourceFile.getPath() + "里面不存在文件，无需压缩.");
            } else {
                byte[] bufs = new byte[1024 * 10];
                for (int i = 0; i < sourceFiles.length; i++) {
                    File file = sourceFiles[i];
                    String pathName = file.getPath().substring(basePath.getPath().length() + 1);
                    ;
                    if (file.isDirectory()) {
                        zos.putNextEntry(new ZipEntry(pathName + "/"));
                        recursion(zos, file, basePath, fileName);
                    } else {
                        if (file.getName().contains(fileName)) {
                            log.info("存在重复的文件名：" + file.getName());
                            continue;
                        }
                        //创建ZIP实体，并添加进压缩包
                        ZipEntry zipEntry = new ZipEntry(pathName);
                        zos.putNextEntry(zipEntry);
                        //读取待压缩的文件并写进压缩包里
                        int size = 1024 * 10;
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis, size);
                        int read = 0;
                        while ((read = bis.read(bufs, 0, size)) != -1) {
                            zos.write(bufs, 0, read);
                        }
                    }
                    try {
                        if (null != fis) {
                            fis.close();
                        }
                        if (null != bis) {
                            bis.close();
                        }
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } finally {
            //关闭流
            try {
                if (null != fis) {
                    fis.close();
                }
                if (null != bis) {
                    bis.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
//        }
    }


    /**
     * 复制文件夹下的所有文件夹或文件到另一个文件夹
     *
     * @param oldPath
     * @param newPath
     * @throws IOException
     */
    public static void copyDir(String oldPath, String newPath) throws IOException {
        File file = new File(oldPath);
        //文件名称列表
        String[] filePath = file.list();
        if(filePath==null || filePath.length==0){

        }

        if (!(new File(newPath)).exists()) {
            (new File(newPath)).mkdir();
        }

        for (int i = 0; i < filePath.length; i++) {
            if ((new File(oldPath + File.separator + filePath[i])).isDirectory()) {
                copyDir(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }

            if (new File(oldPath + File.separator + filePath[i]).isFile()) {
                copyFile(oldPath + File.separator + filePath[i], newPath + File.separator + filePath[i]);
            }

        }
    }

    public static void copyFile(String oldPath, String newPath) throws IOException {
        File oldFile = new File(oldPath);
        File file = new File(newPath);
        FileInputStream in = new FileInputStream(oldFile);
        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[2097152];
        while ((in.read(buffer)) != -1) {
            out.write(buffer);
        }
    }

    /**
     * base64转为文件保存下来
     *
     * @param base64
     * @param fileName
     * @param savePath
     */
    public static void base64ToFile(String base64, String fileName, String savePath) {

        File file = null;
        //创建文件目录
        String filePath = savePath;
        File dir = new File(filePath);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdirs();
        }
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        try {
            byte[] bytes = new BASE64Decoder().decodeBuffer(base64.trim());
            file = new File(filePath + fileName);
            fos = new java.io.FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 文件转为base64字符串
     *
     * @param document
     * @return
     */
    public static String PDFToBase64(File document) {
        BASE64Encoder encoder = new BASE64Encoder();
        FileInputStream fin = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout = null;
        try {
            fin = new FileInputStream(document);
            bin = new BufferedInputStream(fin);
            baos = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while (len != -1) {
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }
            //刷新此输出流并强制写出所有缓冲的输出字节
            bout.flush();
            byte[] bytes = baos.toByteArray();
            return encoder.encodeBuffer(bytes).trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fin.close();
                bin.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void WriteStringToFile5(String filePath, String content) {
        try {

            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

    }

    /**
     * 生成文件夹
     * @param dir 文件夹路径
     */
    public static void mkDir(String dir) {
        File file = new File(dir);
        if(!file.exists()){
            try {
                file.mkdirs();
            } catch (Exception e) {
                throw new AppException("路径:[ "+dir+" 不是一个文件夹]");
            }
        }
    }
}