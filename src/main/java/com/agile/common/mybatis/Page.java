package com.agile.common.mybatis;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 描述：
 * <p>创建时间：2018/12/17<br>
 *
 * @author 佟盟
 * @version 1.0
 * @since 1.0
 * @param <T> 分页内容类型
 */
public class Page<T> extends LinkedList<T> {
    private PageRequest pageRequest;
    private long total;

    public Page(@NotNull Collection<? extends T> c, PageRequest pageRequest, long total) {
        super(c);
        this.pageRequest = pageRequest;
        this.total = total;
    }

    public org.springframework.data.domain.Page<T> getPage() {
        return new PageImpl<T>(this, pageRequest, total);
    }
}