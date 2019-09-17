package com.devfactory.codefix.web;

import static java.util.stream.Collectors.toList;

import java.util.List;

public abstract class Mapper<T, U> {

    public abstract T toDto(U from);

    public List<T> toDtoList(List<U> issueList) {
        return issueList.stream()
                .map(this::toDto)
                .collect(toList());
    }
}
