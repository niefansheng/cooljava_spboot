package com.controller.tool;

import java.io.File;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSONObject;
import com.model.tool.FileManager;
import com.quantity.Global;
import com.service.tool.FileManagerService;
import com.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/file")
public class FManagerController {

    @Autowired
    private FileManagerService fileManagerService;

    @RequestMapping("/fileIndex")
    public String fileIndex(){
        return "views/tool/fileManager";
    }

    @ResponseBody
    @RequestMapping(value="/fUpload",method = RequestMethod.POST)
    public String upload (HttpServletRequest request, HttpServletResponse response, Integer id) throws IOException {
            response.setContentType("application/json;charset=utf-8");
            request.setCharacterEncoding("utf-8");
            String root;
            String os;
            String pPath;
            if(Global.getOS()){
                root = Global.WINFILEPATH;
                os = "Windows";
            }else{
                root = Global.LINUXFILEPATH;
                os = "Linux";
            }
            if(id!=null&&id!=0){
                FileManager pFm = new FileManager();
                pFm.setId(id);
                pPath = fileManagerService.get(pFm).getSrc()+"/";
            }else{
                pPath = root;
            }
            FileManager fm = new FileManager();
            Map<String,Object> map1 = new HashMap<>();
            Map<String,Object> map2 = new HashMap<>();
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest)request;
            List<MultipartFile> files = mRequest.getFiles("file");
            files.forEach(file -> {
                try {
                    String suffixName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);//获取后缀名
                    String tempName = UploadUtil.uploadFile(file,pPath); //此处pPath用来实际上传文件
                    fm.setName(tempName);
                    fm.setSrc(pPath+tempName);  //此处pPath用来插入文件地址入库
                    fm.setOs(os);
                    fm.setTime(new Date());
                    fm.setType(suffixName);
                    fm.setPid(id);
                    fileManagerService.insert(fm);
                    map2.put("src",root+tempName);
                    map1.put("code",1);
                    map1.put("data",map2);
                } catch (IOException e) {
                    map1.put("code",0);
                    e.printStackTrace();
                }
            });
        return JSONUtils.toJSONString(map1);
    }

    @RequestMapping("/del")
    @ResponseBody
    public String delFile(@RequestParam("ids[]")List<Integer> ids){
        int code = 0;
        FileManager fm = new FileManager();
        try{
            if(ids != null && ids.size() != 0){
                for(Integer id : ids){
                    fm.setId(id);
                    fm = fileManagerService.get(fm);
                    String filePath = fm.getSrc();
                    File file = new File(filePath);
                    recurDeleteFile(file);
                    deleteNextLevFiles(fm);
                }
            }
            code = 1;
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Integer> rtnMap = new HashMap<>();
        rtnMap.put("code",code);
        return JSONObject.toJSONString(rtnMap);
    }

    @RequestMapping(value="/getPathList",method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public String getPathList(Integer dirId, Integer limit, Integer page){
        Map<Object, Object> map = new HashMap<>();
        List<FileManager> list;
        FileManager fm = new FileManager();
        fm.setPid(dirId);
        fm.setLimit(limit);
        fm.setPage(page);
        list = fileManagerService.getListByPage(fm);
        String root;
        String os;
        if(Global.getOS()){
            root = Global.WINFILEPATH;
            os = "Windows";
        }else{
            root = Global.LINUXFILEPATH;
            os = "Linux";
        }

        list.forEach(p -> {
            String src = p.getSrc().replace(root, "");
            p.setSrc("/photo/"+src);
        });

        map.put("code",1);
        map.put("count",1);
        map.put("fileList",list);

        return JSONObject.toJSONString(map);
    }

    @RequestMapping(value="/newDir",method = RequestMethod.POST)
    @ResponseBody
    public String newDir(String newDirName, Integer dirId){
        newDirName = newDirName.trim();
        int code ;
        String root;
        String os;
        String pPath;
        if(Global.getOS()){
            root = Global.WINFILEPATH;
             os = "Windows";
        }else{
            root = Global.LINUXFILEPATH;
            os = "Linux";
        }
        if(dirId!=null&&dirId!=0){
            FileManager pFm = new FileManager();
            pFm.setId(dirId);
            pPath = fileManagerService.get(pFm).getSrc()+"/";
        }else{
            pPath = root;
        }
        Map<String,Integer> map = new HashMap<>();
        FileManager fileManager = new FileManager();
        fileManager.setName(newDirName);
        fileManager.setType("/DIR");
        fileManager.setSrc(pPath+newDirName);
        fileManager.setPath("");
        fileManager.setPid(dirId);
        fileManager.setTime(new Date());
        fileManager.setOs(os);
        fileManagerService.insert(fileManager);

        File file = new File((pPath+newDirName).trim());
        boolean isExists = file.exists();
        if(isExists){ code = 0; }
        else{
            try{
                file.mkdir();
                if("Linux".equals(os)){  //如果是linxu系统需要给文件夹赋权限
                    Runtime.getRuntime().exec("chmod 777 -R " + pPath+newDirName.trim());//赋权限
                }
                code = 1;
            }catch (Exception e){
                e.printStackTrace();
                code = 0;
            }
        }
        map.put("code",code);
        return JSONUtils.toJSONString(map);
    }

    @RequestMapping("/pageJump")
    @ResponseBody
    public String pageJump(){
        return "";
    }

    @RequestMapping("/readFile")
    @ResponseBody
    public String readFile(Integer id){ //读取对应的文件信息
        int code = 0; // 0:failed  1:success
        FileManager fm = new FileManager();
        fm.setId(id);
        fm = fileManagerService.get(fm);
        String filePath = fm.getSrc();
        File file = new File(filePath);

        Map<String, Integer> rtnMap = new HashMap<>();
        rtnMap.put("code", code);
        return JSONObject.toJSONString(rtnMap);
    }

    @RequestMapping("/rename")
    @ResponseBody
    public String rename(Integer id, String newName){
        int code = 0;
        newName = newName.trim();
        Map<String, Integer> returnMap = new HashMap<>();
        String oldPath;
        String newPath;
        FileManager fm = new FileManager();
        fm.setId(id);
        try{
            fm = fileManagerService.get(fm);
            oldPath = fm.getSrc();  //重命名之前的磁盘地址
            newPath = oldPath.substring(0,oldPath.lastIndexOf("/")+1) + newName; //重命名之后的磁盘地址
            File oldFile = new File(oldPath);
            oldFile.renameTo(new File(newPath));
            fm.setName(newName);
            fm.setSrc(newPath);
            renameSrcNextLevFiles(fm, oldPath);
            fileManagerService.update(fm);

            code =1;
        }catch(Exception e){
            e.printStackTrace();
        }
        returnMap.put("code", code);
        return JSONObject.toJSONString(returnMap);
    }

    //递归重命名文件夹名字时修改当前目录下所有文件以及文件夹
    private void renameSrcNextLevFiles(FileManager fm, String oldPath){
        //判断这个FileManager是文件还是文件夹
        if((fm.getType() != null) && (!"".equals(fm.getType())) && ("/DIR".equals(fm.getType()))){
            int pId = fm.getId();
            FileManager fmParam = new FileManager();
            fmParam.setPid(pId);
            List<FileManager> fileManagerList = fileManagerService.getAllList(fmParam);
            if(fileManagerList.size() != 0){
                fileManagerList.forEach(p -> {
                    String oldPathTemp = p.getSrc();
                    p.setSrc(fm.getSrc()+p.getSrc().substring(oldPath.length()));
                    fileManagerService.update(p);
                    renameSrcNextLevFiles(p, oldPathTemp);
                });
            }
        }
    }

    //递归删除磁盘上的目录
    private void recurDeleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            //判断子文件集是否为空
            if(files == null || files.length == 0){
                file.delete();
            }else{
                for(File file1 : files){
                    recurDeleteFile(file1);
                }
            }
            file.delete();
        }else{
            file.delete();
        }
    }

    //递归删除数据库中该目录下所有记录
    private void deleteNextLevFiles(FileManager fm){
        //判断这个FileManager是文件还是文件夹
        if((fm.getType() != null) && (!"".equals(fm.getType())) && ("/DIR".equals(fm.getType()))){
            int pId = fm.getId();
            FileManager fmParam = new FileManager();
            fmParam.setPid(pId);
            List<FileManager> fileManagerList = fileManagerService.getAllList(fmParam);
            if(fileManagerList.size() == 0){
                fileManagerService.delete(fm.getId());
            }else{
                fileManagerList.forEach(p -> {
                    deleteNextLevFiles(p);
                });
            }
            fileManagerService.delete(fm.getId());
        }else{
            fileManagerService.delete(fm.getId());
        }
    }
}
