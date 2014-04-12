package com.kk.teachme.db;

import com.kk.teachme.checker.Checker;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.RowMapper;

public class CheckerDepot extends AbstractCachedDepot<Checker> implements ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    protected RowMapper<Checker> getRowMapper() {
        return (resultSet, i) -> {
            Checker checker = (Checker) applicationContext.getBean(resultSet.getString("bean_name"));
            checker.setId(resultSet.getInt("id"));
            return checker;
        };
    }

    @Override
    protected String getTableName() {
        return "checker";
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
