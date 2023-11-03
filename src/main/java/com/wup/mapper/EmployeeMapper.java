package com.wup.mapper;

import com.wup.domain.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;

/**
* @author brainwu
* @description 针对表【employee(员工信息)】的数据库操作Mapper
* @createDate 2023-09-28 17:51:14
* @Entity com.wup.domain.Employee
*/
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}




