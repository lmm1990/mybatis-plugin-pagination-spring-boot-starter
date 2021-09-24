package com.github.lmm1990.mybatis.plugin.pagination.demo;

import com.alibaba.fastjson.JSONArray;
import com.github.lmm1990.mybatis.plugin.pagination.demo.entity.TestInfo;
import com.github.lmm1990.mybatis.plugin.pagination.demo.mapper.TestMapper;
import io.github.lmm1990.spring.boot.starter.entity.Page;
import io.github.lmm1990.spring.boot.starter.utils.PaginationHelper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动类
 *
 * @author liumingming
 * @since 2021-08-19 9:32
 */
@Component
public class Runner implements CommandLineRunner {

    @Resource
    private TestMapper testMapper;

    @Override
    public void run(String... args) {
//        System.out.println(testMapper.list());

        PaginationHelper.init(1,5);
        Page<TestInfo> list1 = testMapper.listByStatus(1);
        System.out.println(JSONArray.toJSONString(list1));

        PaginationHelper.init(2,10,"select 999;");
        Page<TestInfo> list2 = testMapper.listByStatus(2);
        System.out.println(JSONArray.toJSONString(list2));
    }
}
