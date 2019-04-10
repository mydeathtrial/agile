package com.agile.mvc.entity;

import com.agile.common.annotation.Remark;
import com.agile.common.base.Constant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：[系统管理]字典数据表
 *
 * @author agile gennerator
 */
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "dictionary_data")
@Remark("[系统管理]字典数据表")
@Cacheable()
public class DictionaryDataEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;
    @Remark("主键")
    private String dictionaryDataId;
    @Remark("父节点主键")
    private String parentId;
    @Remark("显示名称")
    private String code;
    @Remark("代表值")
    private String name;
    @Remark("字典值是否固定")
    private Boolean isFixed;


    private List<DictionaryDataEntity> children = new ArrayList<>();

    @Transient
    public List<DictionaryDataEntity> getChildren() {
        return children;
    }


    private Map<String, DictionaryDataEntity> codeCache = new HashMap<>();

    @Transient
    public Map<String, DictionaryDataEntity> getCodeCache() {
        return codeCache;
    }

    @Transient
    public DictionaryDataEntity getCodeCache(String code) {
        DictionaryDataEntity entity;
        String parentCode = code.trim();
        if (code.contains(Constant.RegularAbout.SPOT)) {
            String[] codes = code.split("[.]");
            parentCode = codes[Constant.NumberAbout.ZERO].trim();
            if (codes.length > Constant.NumberAbout.ONE) {
                DictionaryDataEntity re = codeCache.get(parentCode);
                if (re == null) {
                    return null;
                }
                entity = re.getCodeCache(code.replaceFirst(parentCode + Constant.RegularAbout.SPOT, Constant.RegularAbout.BLANK));
            } else {
                entity = codeCache.get(parentCode);
            }
        } else {
            entity = codeCache.get(parentCode);
        }
        return entity;
    }

    /**
     * 判断是否包含子字典实例
     * @param code 子字典码
     * @return 是否
     */
    public boolean containsKey(String code) {
        return codeCache.containsKey(code);
    }

    public void addCodeCache(String key, DictionaryDataEntity value) {
        codeCache.put(key, value);
    }

    @Column(name = "dictionary_data_id", nullable = false, length = 18)
    @NotBlank(message = "唯一标识不能为空", groups = {Update.class, Delete.class})
    @Id
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getDictionaryDataId() {
        return dictionaryDataId;
    }

    @Basic
    @Column(name = "parent_id", length = 18)
    @Length(max = 18, message = "最长为18个字符", groups = {Insert.class, Update.class})
    public String getParentId() {
        return parentId;
    }

    @NotBlank(message = "显示名称不能为空", groups = {Insert.class, Update.class})
    @Length(max = 50, message = "最长为50个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "code", nullable = false, length = 50)
    public String getCode() {
        return code;
    }

    @NotBlank(message = "代表值不能为空", groups = {Insert.class, Update.class})
    @Length(max = 50, message = "最长为50个字符", groups = {Insert.class, Update.class})
    @Basic
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    @Basic
    @NotNull(message = "字典值是否固定不能为空", groups = {Insert.class, Update.class})
    @Column(name = "is_fixed", nullable = false, length = 1)
    public Boolean getIsFixed() {
        return isFixed;
    }


    @Override
    public DictionaryDataEntity clone() {
        try {
            return (DictionaryDataEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}
