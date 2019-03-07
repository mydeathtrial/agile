package com.agile.mvc.service;

import com.agile.common.annotation.Init;
import com.agile.common.annotation.Mapping;
import com.agile.common.annotation.Models;
import com.agile.common.annotation.Validate;
import com.agile.common.annotation.Validates;
import com.agile.common.base.RETURN;
import com.agile.common.exception.NoSuchIDException;
import com.agile.common.mvc.service.BusinessService;
import com.agile.common.util.CacheUtil;
import com.agile.common.util.TreeUtil;
import com.agile.mvc.entity.DictionaryDataEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author agile generator
 */
@Api(description = "[系统管理]字典数据表")
@Mapping("/api/sys")
@Service
public class DictionaryDataService extends BusinessService<DictionaryDataEntity> {
    @ApiOperation(value = "新增[系统管理]字典数据表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "DictionaryDataEntity")
    })
    @Models({DictionaryDataEntity.class})
    @Validate(beanClass = DictionaryDataEntity.class, validateGroups = Insert.class)
    @Mapping(value = "/dictionary-data", method = RequestMethod.POST)
    public RETURN customSave() throws NoSuchIDException, IllegalAccessException, NoSuchMethodException {
        return super.save();
    }

    @ApiOperation(value = "删除[系统管理]字典数据表", httpMethod = "DELETE", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Validate(beanClass = DictionaryDataEntity.class, validateGroups = Delete.class)
    @Mapping(path = "/dictionary-data/{id}", method = RequestMethod.DELETE)
    public RETURN customDelete() throws NoSuchIDException {
        return super.delete();
    }

    @ApiOperation(value = "更新[系统管理]字典数据表", httpMethod = "PUT", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "DictionaryDataEntity")
    })
    @Models({DictionaryDataEntity.class})
    @Validate(beanClass = DictionaryDataEntity.class, validateGroups = Update.class)
    @Mapping(value = "/dictionary-data", method = RequestMethod.PUT)
    public RETURN customUpdate() throws NoSuchIDException, IllegalAccessException {
        return super.update();
    }

    @ApiOperation(value = "分页查询[系统管理]字典数据表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "DictionaryDataEntity"),
            @ApiImplicitParam(name = "pageSize", required = true, value = "页大小", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "pageNum", required = true, value = "页号", paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sorts", value = "排序字段", paramType = "query", dataType = "String[]")
    })
    @Models({DictionaryDataEntity.class})
    @Mapping(path = "/dictionary-data/list/query", method = RequestMethod.POST)
    @Validates({
            @Validate(value = "pageSize", nullable = false, validateMsgKey = "页号不能为空"),
            @Validate(value = "pageNum", nullable = false, validateMsgKey = "页容量不能为空")
    })
    public RETURN customPageQuery() throws IllegalAccessException, InstantiationException {
        return super.pageQuery();
    }

    @ApiOperation(value = "查询[系统管理]字典数据表", httpMethod = "POST", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entity", value = "实体", paramType = "body", dataType = "DictionaryDataEntity")
    })
    @Models({DictionaryDataEntity.class})
    @Mapping(path = "/dictionary-data/query", method = RequestMethod.POST)
    public RETURN customQuery() throws NoSuchIDException {
        return super.query();
    }

    @ApiOperation(value = "根据主键查询[系统管理]字典数据表", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "唯一标识", paramType = "path", dataType = "String")
    })
    @Models({DictionaryDataEntity.class})
    @Mapping(path = "/dictionary-data/{id}", method = RequestMethod.GET)
    public RETURN customQueryById() {
        return super.queryById();
    }

    @Init
    public void synchronousCache() throws NoSuchFieldException, IllegalAccessException {
        List<DictionaryDataEntity> list = dao.findAll(DictionaryDataEntity.class);
        List<DictionaryDataEntity> tree = TreeUtil.createTree(list, "dictionaryDataId", "parentId", "children", "root");
        for (DictionaryDataEntity entity : tree) {
            coverCacheCode(entity);
            if ("root".equals(entity.getParentId())) {
                CacheUtil.getDicCache().put(entity.getCode(), entity);
            }
        }
    }

    /**
     * 递归处理所有字典的CacheCode
     *
     * @param entity 字典实体
     */
    private void coverCacheCode(DictionaryDataEntity entity) {
        List<DictionaryDataEntity> children = entity.getChildren();
        if (children == null || children.size() == 0) {
            return;
        }
        for (DictionaryDataEntity child : children) {
            coverCacheCode(child);
            entity.addCodeCache(child.getCode(), child);
        }
    }
}
