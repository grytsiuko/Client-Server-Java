package org.fidoshenyata.db.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
@EqualsAndHashCode
public class PagingInfo {
    @Getter
    private final Integer offset;
    @Getter
    private final Integer limit;
    @Getter
    @Setter
    private Integer total;

   public PagingInfo(Integer offset, Integer limit){
        this(offset,limit, null);
    }

    public PagingInfo(Integer offset, Integer limit,Integer total){
       if(offset == null || limit == null) throw new NullPointerException("Either offset or limit is null");
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }
}
