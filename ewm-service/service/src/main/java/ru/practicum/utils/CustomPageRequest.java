package ru.practicum.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {

    private final int offset;

    protected CustomPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.offset = from;
    }

    public static PageRequest pageRequestOf(int from, int size) {
        return new CustomPageRequest(from, size, Sort.unsorted());
    }

    public static PageRequest pageRequestOf(int from, int size, Sort sort) {
        return new CustomPageRequest(from, size, sort);
    }

    @Override
    public long getOffset() {
        return this.offset;
    }

}

