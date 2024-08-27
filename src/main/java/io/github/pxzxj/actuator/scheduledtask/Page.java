package io.github.pxzxj.actuator.scheduledtask;

import java.util.Collections;
import java.util.List;

public class Page<T> {

    private int total;

    private int page;

    private int size;

    private List<T> data;

    Page(int total, int page, int size, List<T> data) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.data = data;
    }

    public Page() {

    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    static <T> Page<T> of(List<T> list, int page, int size) {
        int total = list.size();
        int start = page * size;
        if (start >= total) {
            return new Page<>(total, page, size, Collections.emptyList());
        }
        int end = start + size;
        if (end > total) {
            end = total;
        }
        return new Page<>(total, page, size, list.subList(start, end));
    }
}
