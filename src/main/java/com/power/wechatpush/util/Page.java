package com.power.wechatpush.util;

import java.util.List;

/**
 * Created by tanzhiming on 2018/4/20.
 */
public class Page<T> {

    private int total = 0;
    private List<T> rows = null;

    public Page() {
    }

    protected Page(int total, List<T> rows) {
        this.rows = rows;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public static <E> Page<E> create(int total, List<E> rows) {
        return new Page<E>(total, rows);
    }
}