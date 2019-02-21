package com.agile.mvc.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Basic;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.agile.common.annotation.Remark;
import java.util.Date;
import org.hibernate.sql.Delete;
import org.hibernate.sql.Update;
import org.hibernate.sql.Insert;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotEmpty;
import javax.persistence.Id;

/**

 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "sys_login")
public class SysLoginEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String sysLoginId;
    @Remark("账号主键")
    private String sysUserId;
    @Remark("登陆时间")
    private Date loginTime;
    @Remark("退出时间")
    private Date logoutTime;
    @Remark("登陆IP地址")
    private String loginIp;
    @Remark("口令")
    private String token;

    @Column(name = "sys_login_id", nullable = false, length = 18)
    @NotEmpty(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysLoginId() {
        return sysLoginId;
    }

    @Column(name = "sys_user_id", length = 18)
    @Basic
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getSysUserId() {
        return sysUserId;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @Column(name = "login_time", length = 26)
    @Basic
    public Date getLoginTime() {
        return loginTime;
    }

    @Length(max = 26, message = "最长为26个字符", groups = {Insert.class, Update.class})
    @Column(name = "logout_time", length = 26)
    @Basic
    public Date getLogoutTime() {
        return logoutTime;
    }

    @Column(name = "login_ip", length = 15)
    @Length(max = 15, message = "最长为15个字符", groups = {Insert.class, Update.class})
    @Basic
    public String getLoginIp() {
        return loginIp;
    }

    @Basic
    @Column(name = "token", length = 8)
    @Length(max = 8, message = "最长为8个字符", groups = {Insert.class, Update.class})
    public String getToken() {
        return token;
    }


    @Override
    public SysLoginEntity clone() {
        try {
            return (SysLoginEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
