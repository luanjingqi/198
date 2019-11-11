package cn.exrick.xboot.modules.file.controller;

import cn.exrick.xboot.common.constant.CommonConstant;
import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.common.utils.*;
import cn.exrick.xboot.modules.base.service.SettingService;
import cn.exrick.xboot.modules.base.vo.OssSetting;
import cn.exrick.xboot.common.vo.PageVo;
import cn.exrick.xboot.common.vo.Result;
import cn.exrick.xboot.common.vo.SearchVo;
import cn.exrick.xboot.modules.file.entity.File;
import cn.exrick.xboot.modules.file.manage.FileManageFactory;
import cn.exrick.xboot.modules.file.manage.impl.LocalFileManage;
import cn.exrick.xboot.modules.file.service.FileService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;


/**
 * @author Exrick
 */
@Slf4j
@Controller
@Api(description = "文件管理管理接口")
@RequestMapping("/xboot/file")
@Transactional
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private FileManageFactory fileManageFactory;

    @Autowired
    private SettingService settingService;

    @PersistenceContext
    private EntityManager entityManager;

    @RequestMapping(value = "/getByCondition", method = RequestMethod.GET)
    @ApiOperation(value = "多条件分页获取")
    @ResponseBody
    public Result<Page<File>> getFileList(@ModelAttribute File file,
                                          @ModelAttribute SearchVo searchVo,
                                          @ModelAttribute PageVo pageVo){

        Page<File> page = fileService.findByCondition(file, searchVo, PageUtil.initPage(pageVo));
        page.getContent().forEach(e -> {
            if(e.getLocation()!=null&&CommonConstant.OSS_LOCAL.equals(e.getLocation())){
                OssSetting os = new Gson().fromJson(settingService.get(SettingConstant.LOCAL_OSS).getValue(), OssSetting.class);
                String url = os.getHttp() + os.getEndpoint() + "/";
                entityManager.clear();
                e.setUrl(url + e.getId());
            }
        });
        return new ResultUtil<Page<File>>().setData(page);
    }

    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    @ApiOperation(value = "文件复制")
    @ResponseBody
    public Result<Object> copy(@RequestParam String id,
                               @RequestParam String key) throws Exception {

        File file = fileService.get(id);
        String toKey = "副本_" + key;
        if(file.getLocation()==null){
            return new ResultUtil<Object>().setErrorMsg("存储位置未知");
        }

        // 特殊处理本地服务器
        if(CommonConstant.OSS_LOCAL.equals(file.getLocation())){
            key = file.getUrl();
        }
        String newUrl = fileManageFactory.getFileManage(file.getLocation()).copyFile(key, toKey);

        File newFile = new File();
        newFile.setName(file.getName());
        newFile.setFKey(toKey);
        newFile.setSize(file.getSize());
        newFile.setType(file.getType());
        newFile.setLocation(file.getLocation());
        newFile.setUrl(newUrl);
        fileService.save(newFile);
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    @ApiOperation(value = "文件重命名")
    @ResponseBody
    public Result<Object> upload(@RequestParam String id,
                                 @RequestParam String key,
                                 @RequestParam String newKey,
                                 @RequestParam String newName) throws Exception {

        File file = fileService.get(id);
        if(file.getLocation()==null){
            return new ResultUtil<Object>().setErrorMsg("存储位置未知");
        }
        String newUrl = "";
        if(!key.equals(newKey)){
            // 特殊处理本地服务器
            if(CommonConstant.OSS_LOCAL.equals(file.getLocation())){
                key = file.getUrl();
            }
            newUrl = fileManageFactory.getFileManage(file.getLocation()).renameFile(key, newKey);
        }
        file.setName(newName);
        file.setFKey(newKey);
        if(!key.equals(newKey)) {
            file.setUrl(newUrl);
        }
        fileService.update(file);
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/delete/{ids}", method = RequestMethod.DELETE)
    @ApiOperation(value = "文件删除")
    @ResponseBody
    public Result<Object> delete(@PathVariable String[] ids) {

        for(String id : ids){
            File file = fileService.get(id);
            if(file.getLocation()==null){
                return new ResultUtil<Object>().setErrorMsg("存储位置未知");
            }
            // 特殊处理本地服务器
            String key = file.getFKey();
            if(CommonConstant.OSS_LOCAL.equals(file.getLocation())){
                key = file.getUrl();
            }
            fileManageFactory.getFileManage(file.getLocation()).deleteFile(key);
            fileService.delete(id);
        }
        return new ResultUtil<Object>().setData(null);
    }

    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "本地存储预览文件")
    public void view(@PathVariable String id, HttpServletResponse response) throws IOException {

        File file = fileService.get(id);
        if(file==null){
            throw new XbootException("文件ID:"+id+"不存在");
        }
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getFKey(), "UTF-8"));
        response.setContentLengthLong(file.getSize());
        response.setContentType(file.getType());
        response.addHeader("Accept-Ranges", "bytes");
        if(file.getSize()!=null&&file.getSize()>0){
            response.addHeader("Content-Range", "bytes " + 0 + "-" + (file.getSize()-1) + "/" + file.getSize());
        }
        LocalFileManage.view(file.getUrl(), response);
    }
}
