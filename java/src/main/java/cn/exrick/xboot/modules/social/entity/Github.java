package cn.exrick.xboot.modules.social.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Exrick
 */
@Data
@Entity
@Table(name = "t_github")
@TableName("t_github")
@ApiModel(value = "Github用户")
public class Github extends BaseSocialEntity {

    private static final long serialVersionUID = 1L;
}