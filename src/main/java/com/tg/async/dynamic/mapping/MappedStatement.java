package com.tg.async.dynamic.mapping;


import com.tg.async.exception.BuilderException;
import lombok.Getter;

/**
 * Created by twogoods on 2018/4/19.
 */
@Getter
public class MappedStatement {

    private String resultType;
    private String resultMap;
    private SqlType sqlType;
    private String id;
    private String useGeneratedKeys;
    private String keyProperty;

    private SqlSource sqlSource;

    private MappedStatement() {
    }

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(String id, SqlSource sqlSource, String mode) {
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            try {
                mappedStatement.sqlType = SqlType.valueOf(mode.toUpperCase());
            } catch (IllegalArgumentException e) {
                new BuilderException(String.format("sql mode {} not support", mode));
            }
        }

        public Builder resultType(String resultType) {
            mappedStatement.resultType = resultType;
            return this;
        }

        public Builder resultMap(String resultMap) {
            mappedStatement.resultMap = resultMap;

            return this;
        }

        public Builder keyGenerator(String useGeneratedKeys) {
            mappedStatement.useGeneratedKeys = useGeneratedKeys;
            return this;
        }

        public Builder keyProperty(String keyProperty) {
            mappedStatement.keyProperty = keyProperty;
            return this;
        }

        public MappedStatement build() {
            return mappedStatement;
        }
    }
}
