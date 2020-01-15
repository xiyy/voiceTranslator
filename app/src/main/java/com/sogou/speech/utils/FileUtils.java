package com.sogou.speech.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     * @throws Exception
     */
    public static File createDirInSDCard(String dirName) throws Exception {
        File dir = new File(dirName);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 在SD卡上创建文件
     *
     * @param dirName
     * @param fileName
     * @return
     * @throws Exception
     */
    public static File createFileInSDCard(String dirName, String fileName) throws Exception {
        File file = new File(dirName + fileName);
        if(!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 判断SD卡上的文件是否存在
     *
     * @param fileName
     * @param dirName
     * @return
     */
    public static boolean isFileExist(String dirName, String fileName) {
        File file = new File(dirName + fileName);
        return file.exists();
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /*
     * 判断SD卡上的文件是否存在并返回其路径
     */
    public static String getExistFilePath(String dirName, String fileName) {
        if (isFileExist(dirName, fileName)) {
            return dirName + fileName;
        }
        return null;
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     *
     * @param dirName
     * @param fileName
     * @param inputStream
     * @return
     */
    public static File writeInputStream2SDCard(String dirName, String fileName, InputStream inputStream) {
        File file = null;
        OutputStream outputStream = null;

        try {
            createDirInSDCard(dirName);
            file = createFileInSDCard(dirName, fileName);
            outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[8 * 1024];//一次写4KB

            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }

            outputStream.flush();//清空缓存
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    public synchronized static File writeByteArray2SDCard(String dirName, String fileName, byte[] input, boolean isAppend) {
        File file = null;
        OutputStream outputStream = null;

        try {
            createDirInSDCard(dirName);
            file = createFileInSDCard(dirName, fileName);
            outputStream = new FileOutputStream(file,isAppend);
            InputStream is = new ByteArrayInputStream(input);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
    /**
     * 将一个String对象写入SD卡中
     *
     * @param dirName
     * @param fileName
     * @param str
     * @return
     */
    public static File writeString2SDCard(String dirName, String fileName, String str) {
        File file = null;
        OutputStream outputStream = null;

        try {
            createDirInSDCard(dirName);
            file = createFileInSDCard(dirName, fileName);
            outputStream = new FileOutputStream(file);

            outputStream.write(str.getBytes());
            outputStream.flush();//清空缓存
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }

    /**
     * 从文本文件读出字符串
     *
     * @param filePath
     * @return
     */
    public static String readStringFromTxtFile(String filePath) {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        String line = null;
        StringBuffer stringBuffer = new StringBuffer();

        try {
            inputStream = new FileInputStream(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuffer.toString();
    }


    /**
     * @param context
     * @param assetpath asset下的路径
     * @param SDpath    SDpath下保存路径
     */
    public static void AssetToSD(Context context, String assetpath, String SDpath) {

        AssetManager asset = context.getAssets();
        //循环的读取asset下的文件，并且写入到SD卡
        String[] filenames = null;
        FileOutputStream out = null;
        InputStream in = null;
        try {
            filenames = asset.list(assetpath);
            if (filenames.length > 0) {//说明是目录
                //创建目录
                getDirectory(assetpath);

                for (String fileName : filenames) {
                    AssetToSD(context, assetpath + "/" + fileName, SDpath + "/" + fileName);
                }
            } else {//说明是文件，直接复制到SD卡
                File SDFlie = new File(SDpath);
                String path = SDpath.substring(0, SDpath.lastIndexOf("/"));
                getDirectory(path);

                if(!SDFlie.exists()){
                    SDFlie.getParentFile().mkdirs();
                    SDFlie.createNewFile();
                    //将内容写入到文件中
                    in=asset.open(assetpath);
                    out= new FileOutputStream(SDFlie);
                    byte[] buffer = new byte[1024];
                    int byteCount=0;
                    while((byteCount=in.read(buffer))!=-1){
                        out.write(buffer, 0, byteCount);
                    }
                }

                if(out!=null) {
                    out.flush();
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            try {
                if(out!=null){
                    out.close();
                    out=null;
                }
                if(in!=null){
                    in.close();
                    in=null;
                }
                /**
                 * 关闭报错，java.lang.RuntimeException:
                 * Unable to start activity ComponentInfo
                 * {com.example.wealth/com.example.wealth.UI.main}:
                 * java.lang.RuntimeException: Assetmanager has been closed
                 */
//              if(asset!=null){
//                  asset.close();
//                  asset=null;
//                  }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(Environment.getExternalStorageDirectory(), dstPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //分级建立文件夹
    public static void getDirectory(String path) {
        //对SDpath进行处理，分层级建立文件夹
        String[] s = path.split("/");
        String str = Environment.getExternalStorageDirectory().toString();
        for (int i = 0; i < s.length; i++) {
            str = str + "/" + s[i];
            File file = new File(str);
            if (!file.exists()) {
                file.mkdir();
            }
        }

    }

    public static String shaEncrypt(String strSrc) {
        MessageDigest md = null;
        String strDes = null;
        byte[] bt = strSrc.getBytes();
        try {
            md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(bt);
            strDes = bytes2Hex(md.digest()); // to HexString
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        return strDes;
    }

    /**
     * byte数组转换为16进制字符串
     *
     * @param bts
     *            数据源
     * @return 16进制字符串
     */
    public static String bytes2Hex(byte[] bts) {
        String des = "";
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

}
