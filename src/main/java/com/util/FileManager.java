package com.util;

import sun.plugin2.util.SystemUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileManager {
    public static final String ERROR = "ERROR";
    public static final String NONE = "NONE";
    //根目录
    private String basePath;
    /*//自定义存储根路径
    public FileManager(String basePath){
        this.basePath = basePath;
    }*/
    //默认存储根路径
    public FileManager(){
        this.basePath = this.getClass().getResource("/").getPath();//.split("webapps")[0]+"webapps/file";
    }

    public String getBasePath(){
        return this.basePath;
    }

    /**
     * 根据hash算法，确定存放目录
     * @param filename
     * @return
     */
    private String getPath(String filename){
        try{
            //得到文件名的hashCode的值，取二进制后16位，文件夹深度为4
            int hashcode = filename.hashCode();
            String dir_suffix = "/"+(hashcode & 0xf)+"/"+((hashcode & 0xf0) >> 4)+"/"+((hashcode & 0xf00) >> 8)+"/"+((hashcode & 0xf000) >> 12);
            //构造新的保存目录
            String dir = "F:\\"+"" + dir_suffix;
            //File既可以代表文件也可以代表目录
            File file = new File(dir);
            //如果目录不存在
            if (!file.exists()) {
                //创建目录
                file.mkdirs();
            }
            return dir;
        }catch (Exception e){
            e.printStackTrace();
            return basePath+"/error";
        }

    }

    /**
     * 保存文件
     * @return 返回访问链接
     */
    public String save(String filename, String ext, String operator, InputStream in) throws IOException {
        FileOutputStream fos = null;
        try{
            String path = this.getPath(filename);
            fos = new FileOutputStream(path+"/"+filename+"."+ext);
            byte [] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer))>0){
                fos.write(buffer,0,length);
            }
            return path+"/"+filename+"."+ext;
        }catch (Exception e){
            e.printStackTrace();
            return ERROR;
        }finally {
            if(null != in){
                in.close();
            }
            if(null != fos){
                fos.close();
            }
        }
    }

    /**
     * 根据文件名及其扩展名，返回一个文件链接
     * @param filename
     * @param ext
     * @return
     */
    public String getFileURI(String filename,String ext){
        int hashcode = filename.hashCode();
        String dir_suffix = "/"+(hashcode & 0xf)+"/"+((hashcode & 0xf0) >> 4)+"/"+((hashcode & 0xf00) >> 8)+"/"+((hashcode & 0xf000) >> 12);
        String realPath =  this.getPath(filename)+"/"+filename+"."+ext;
        File file = new File(realPath);
        if(file.exists()){
            String domain = SystemUtil.getSystemProperty("domain");
            return domain.substring(0,domain.lastIndexOf("/"))+"/file"+dir_suffix+"/"+filename+"."+ext;
        }else return NONE;
    }
}
