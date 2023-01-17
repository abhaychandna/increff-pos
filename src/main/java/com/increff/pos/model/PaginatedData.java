package com.increff.pos.model;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginatedData<T> {
    List<T> data;
    Integer recordsTotal;
    Integer recordsFiltered;
    Integer draw;
}
