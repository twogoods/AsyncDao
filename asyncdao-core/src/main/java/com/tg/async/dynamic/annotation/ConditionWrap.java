package com.tg.async.dynamic.annotation;

import com.tg.async.constant.Attach;
import com.tg.async.constant.Criterions;
import lombok.Builder;
import lombok.Data;

/**
 * Created by twogoods on 2018/5/11.
 */
@Builder
@Data
public class ConditionWrap {
    private Criterions criterion;
    private String field;
    private String column;
    private Attach attach;
    private String test;
    private String ognlParam;

    public static ConditionWrap empty(String paramName) {
        return ConditionWrap.builder()
                .attach(Attach.AND)
                .column(paramName)
                .criterion(Criterions.EQUAL)
                .field(paramName)
                .ognlParam(paramName)
                .build();
    }
}
