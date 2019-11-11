package cn.exrick.xboot.generator;

import cn.exrick.xboot.generator.bean.Entity;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.ClasspathResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 代码生成器 JPA
 * @author Exrickx
 */
@Slf4j
public class XbootGenerator {

    /**
     * 实体类名
     * 建议仅需修改
     */
    private static final String className = "XBoot";

    /**
     * 类说明描述
     * 建议仅需修改
     */
    private static final String description = "测试";

    /**
     * 作者名
     * 建议仅需修改
     */
    private static final String author = "Exrick";

    /**
     * 是否生成树形结构相关接口
     * 建议仅需修改
     */
    private static final Boolean isTree = false;

    /**
     * 是否生成关联工作流相关接口 isTree为false时生效
     * 建议仅需修改
     */
    private static final Boolean activiti = false;

    /**
     * 数据库表名前缀
     * 下方请根据需要修改
     */
    private static final String tablePre = "t_";

    /**
     * 主键类型
     */
    private static final String primaryKeyType = "String";

    /**
     * 实体类对应包
     * (文件自动生成至该包下)
     */
    private static final String entityPackage = "cn.exrick.xboot.modules.your.entity";

    /**
     * dao对应包
     * (文件自动生成至该包下)
     */
    private static final String daoPackage = "cn.exrick.xboot.modules.your.dao";

    /**
     * service对应包
     * (文件自动生成至该包下)
     */
    private static final String servicePackage = "cn.exrick.xboot.modules.your.service";

    /**
     * serviceImpl对应包
     * (文件自动生成至该包下)
     */
    private static final String serviceImplPackage = "cn.exrick.xboot.modules.your.serviceimpl";

    /**
     * controller对应包
     * (文件自动生成至该包下)
     */
    private static final String controllerPackage = "cn.exrick.xboot.modules.your.controller";

    /**
     * 运行该主函数即可生成代码
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws Exception {

        // 模板路径
        ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader("/btl/");
        Configuration cfg = Configuration.defaultConfiguration();
        GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
        // 生成代码
        generateCode(gt);

        // 根据类名删除生成的代码
        //deleteCode(className);
    }

    /**
     * 生成代码
     * @param gt
     * @throws IOException
     */
    private static void generateCode(GroupTemplate gt) throws Exception{

        Template entityTemplate = gt.getTemplate("entity.btl");
        Template daoTemplate = gt.getTemplate("dao.btl");
        Template serviceTemplate = gt.getTemplate("service.btl");
        Template serviceImplTemplate = gt.getTemplate("serviceImpl.btl");
        Template controllerTemplate = gt.getTemplate("controller.btl");
        if(isTree){
            controllerTemplate = gt.getTemplate("treeController.btl");
        }

        Entity entity = new Entity();
        entity.setEntityPackage(entityPackage);
        entity.setDaoPackage(daoPackage);
        entity.setServicePackage(servicePackage);
        entity.setServiceImplPackage(serviceImplPackage);
        entity.setControllerPackage(controllerPackage);
        entity.setAuthor(author);
        entity.setClassName(className);
        entity.setTableName(tablePre+camel2Underline(className));
        entity.setClassNameLowerCase(first2LowerCase(className));
        entity.setDescription(description);
        entity.setPrimaryKeyType(primaryKeyType);
        entity.setActiviti(activiti);
        entity.setIsTree(isTree);

        OutputStream out = null;

        //生成实体类代码
        entityTemplate.binding("entity", entity);
        String entityResult = entityTemplate.render();
        log.info(entityResult);
        //创建文件
        String entityFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(entityPackage) + "/" + className + ".java";
        File entityFile = new File(entityFileUrl);
        File entityDir = entityFile.getParentFile();
        if (!entityDir.exists()) {
            entityDir.mkdirs();
        }
        if(!entityFile.exists()) {
            // 若文件存在则不重新生成
            entityFile.createNewFile();
            out = new FileOutputStream(entityFile);
            entityTemplate.renderTo(out);
        }

        //生成dao代码
        daoTemplate.binding("entity",entity);
        String daoResult = daoTemplate.render();
        log.info(daoResult);
        //创建文件
        String daoFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(daoPackage) + "/" +className + "Dao.java";
        File daoFile = new File(daoFileUrl);
        File daoDir = daoFile.getParentFile();
        if (!daoDir.exists()) {
            daoDir.mkdirs();
        }
        if(!daoFile.exists()) {
            // 若文件存在则不重新生成
            daoFile.createNewFile();
            out = new FileOutputStream(daoFile);
            daoTemplate.renderTo(out);
        }

        //生成service代码
        serviceTemplate.binding("entity",entity);
        String serviceResult = serviceTemplate.render();
        log.info(serviceResult);
        //创建文件
        String serviceFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(servicePackage) + "/" + className + "Service.java";
        File serviceFile = new File(serviceFileUrl);
        File serviceDir = serviceFile.getParentFile();
        if (!serviceDir.exists()) {
            serviceDir.mkdirs();
        }
        if(!serviceFile.exists()) {
            // 若文件存在则不重新生成
            serviceFile.createNewFile();
            out = new FileOutputStream(serviceFile);
            serviceTemplate.renderTo(out);
        }

        //生成serviceImpl代码
        serviceImplTemplate.binding("entity",entity);
        String serviceImplResult = serviceImplTemplate.render();
        log.info(serviceImplResult);
        //创建文件
        String serviceImplFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(serviceImplPackage) + "/" + className + "ServiceImpl.java";
        File serviceImplFile = new File(serviceImplFileUrl);
        File serviceImplDir = serviceImplFile.getParentFile();
        if (!serviceImplDir.exists()) {
            serviceImplDir.mkdirs();
        }
        if(!serviceImplFile.exists()) {
            // 若文件存在则不重新生成
            serviceImplFile.createNewFile();
            out = new FileOutputStream(serviceImplFile);
            serviceImplTemplate.renderTo(out);
        }

        //生成controller代码
        controllerTemplate.binding("entity",entity);
        String controllerResult = controllerTemplate.render();
        log.info(controllerResult);
        //创建文件
        String controllerFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(controllerPackage) + "/" + className + "Controller.java";
        File controllerFile = new File(controllerFileUrl);
        File controllerDir = controllerFile.getParentFile();
        if (!controllerDir.exists()) {
            controllerDir.mkdirs();
        }
        if(!controllerFile.exists()) {
            // 若文件存在则不重新生成
            controllerFile.createNewFile();
            out = new FileOutputStream(controllerFile);
            controllerTemplate.renderTo(out);
        }

        if(out!=null){
            out.close();
        }
        log.info("生成代码成功！");
    }

    /**
     * 删除指定类代码
     * @param className
     * @throws IOException
     */
    private static void deleteCode(String className) throws IOException{

        String entityFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(entityPackage) + "/" +className+".java";
        File entityFile = new File(entityFileUrl);
        if(entityFile.exists()){
            entityFile.delete();
        }
        String daoFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(daoPackage) + "/" +className+"Dao.java";
        File daoFile = new File(daoFileUrl);
        if(daoFile.exists()){
            daoFile.delete();
        }

        String serviceFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(servicePackage) + "/" +className+"Service.java";
        File serviceFile = new File(serviceFileUrl);
        if(serviceFile.exists()){
            serviceFile.delete();
        }

        String serviceImplFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(serviceImplPackage) + "/" +className+"ServiceImpl.java";
        File serviceImplFile = new File(serviceImplFileUrl);
        if(serviceImplFile.exists()){
            serviceImplFile.delete();
        }

        String controllerFileUrl = System.getProperty("user.dir")+"/src/main/java/"+ dotToLine(controllerPackage) + "/" +className+"Controller.java";
        File controllerFile = new File(controllerFileUrl);
        if(controllerFile.exists()){
            controllerFile.delete();
        }

        log.info("删除代码完毕！");
    }

    /**
     * 点转斜线
     * @param str
     * @return
     */
    public static String dotToLine(String str){
        return str.replace(".", "/");
    }

    /**
     * 驼峰法转下划线
     */
    public static String camel2Underline(String str) {

        if (StrUtil.isBlank(str)) {
            return "";
        }
        if(str.length()==1){
            return str.toLowerCase();
        }
        StringBuffer sb = new StringBuffer();
        for(int i=1;i<str.length();i++){
            if(Character.isUpperCase(str.charAt(i))){
                sb.append("_"+Character.toLowerCase(str.charAt(i)));
            }else{
                sb.append(str.charAt(i));
            }
        }
        return (str.charAt(0)+sb.toString()).toLowerCase();
    }

    /**
     * 首字母小写
     */
    public static String first2LowerCase(String str) {

        if (StrUtil.isBlank(str)) {
            return "";
        }
        if(str.length()==1){
            return str.toLowerCase();
        }
        StringBuffer sb = new StringBuffer();
        sb.append(Character.toLowerCase(str.charAt(0)));
        sb.append(str.substring(1,str.length()));
        return sb.toString();
    }
}
