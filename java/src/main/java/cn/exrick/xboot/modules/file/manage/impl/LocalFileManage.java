package cn.exrick.xboot.modules.file.manage.impl;

import cn.exrick.xboot.common.constant.SettingConstant;
import cn.exrick.xboot.common.exception.XbootException;
import cn.exrick.xboot.modules.base.entity.Setting;
import cn.exrick.xboot.modules.base.service.SettingService;
import cn.exrick.xboot.modules.base.vo.OssSetting;
import cn.exrick.xboot.modules.file.manage.FileManage;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * @author Exrick
 */
@Slf4j
@Component
public class LocalFileManage implements FileManage {

    @Autowired
    private SettingService settingService;

    @Override
    public OssSetting getOssSetting() {

        Setting setting = settingService.get(SettingConstant.LOCAL_OSS);
        if(setting==null|| StrUtil.isBlank(setting.getValue())){
            throw new XbootException("您还未配置本地存储");
        }
        return new Gson().fromJson(setting.getValue(), OssSetting.class);
    }

    @Override
    @Deprecated
    public String pathUpload(String filePath, String key) {

        throw new XbootException("暂不支持");
    }

    /**
     * 实则为路径上传
     * @param inputStream
     * @param key
     * @param file
     * @return
     */
    @Override
    public String inputStreamUpload(InputStream inputStream, String key, MultipartFile file) {

        OssSetting os = getOssSetting();
        String day = DateUtil.format(DateUtil.date(), "yyyyMMdd");
        String path = os.getFilePath() + "/" + day;
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File f = new File(path + "/" + key);
        if(f.exists()){
            throw new XbootException("文件名已存在");
        }
        try {
            file.transferTo(f);
            return path + "/" + key;
        } catch (IOException e) {
            log.error(e.toString());
            throw new XbootException("上传文件出错");
        }
    }

    /**
     * 注意此处需传入url
     * @param url
     * @param toKey
     * @return
     */
    @Override
    public String renameFile(String url, String toKey) {

        String result = copyFile(url, toKey);
        deleteFile(url);
        return result;
    }

    /**
     * 注意此处需传入url
     * @param url
     * @param toKey
     * @return
     */
    @Override
    public String copyFile(String url, String toKey) {

        File file = new File(url);
        FileInputStream i = null;
        FileOutputStream o = null;

        try {
            i = new FileInputStream(file);
            o = new FileOutputStream(new File(file.getParentFile() + "/" + toKey));

            byte[] buf = new byte[1024];
            int bytesRead;

            while ((bytesRead = i.read(buf))>0){
                o.write(buf, 0, bytesRead);
            }

            i.close();
            o.close();
            return file.getParentFile() + "/" + toKey;
        } catch (IOException e) {
            log.error(e.toString());
            throw new XbootException("复制文件出错");
        }
    }

    /**
     * 注意此处需传入url
     * @param url
     */
    @Override
    public void deleteFile(String url) {

        File file = new File(url);
        file.delete();
    }

    /**
     * 读取文件
     * @param url
     * @param response
     */
    public static void view(String url, HttpServletResponse response){

        File file = new File(url);
        FileInputStream in = null;
        OutputStream out = null;

        try {
            if (file.exists()) {
                in = new FileInputStream(file);
                out = response.getOutputStream();

                byte[] buf = new byte[1024];
                int bytesRead;

                while ((bytesRead = in.read(buf)) > 0) {
                    out.write(buf, 0, bytesRead);
                    out.flush();
                }
            }
        } catch (IOException e) {

        } finally {
            if(out!=null){
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
