package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.io.Serializable;
import java.util.Date;

/**
 * 描述：[系统管理]日志表
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "log_main")
@Remark("[系统管理]日志表")
public class LogMainEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("唯一标识")
    private String logMainId;
    @Remark("业务编码")
    private String businessCode;
    @Remark("业务对象类型")
    private String targetType;
    @Remark("业务对象标识")
    private String targetCode;
    @Remark("操作人")
    private String userId;
    @Remark("操作时间")
    private Date createTime;

    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    @Column(name = "log_main_id", nullable = false, length = 18)
    public String getLogMainId() {
        return logMainId;
    }

    @Column(name = "business_code", nullable = false, length = 6)
    @Basic
    @NotBlank(message = "业务编码不能为空", groups = {Insert.class, Update.class})
    @Length(max = 6, message = "最长为6个字符", groups = {Insert.class, Update.class})
    public String getBusinessCode() {
        return businessCode;
    }

    @Column(name = "target_type", length = 4)
    @Basic
    @Length(max = 4, message = "最长为4个字符", groups = {Insert.class, Update.class})
    public String getTargetType() {
        return targetType;
    }

    @Length(max = 100, message = "最长为100个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "target_code", length = 100)
    public String getTargetCode() {
        return targetCode;
    }

    @NotBlank(message = "操作人不能为空", groups = {Insert.class, Update.class})
    @Basic
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    @Column(name = "user_id", nullable = false, length = 8)
    public String getUserId() {
        return userId;
    }

    @Basic
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "操作时间不能为空", groups = {Insert.class, Update.class})
    @Past
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, length = 26, updatable = false)
    public Date getCreateTime() {
        return createTime;
    }


    @Override
    public LogMainEntity clone() {
        try {
            return (LogMainEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
