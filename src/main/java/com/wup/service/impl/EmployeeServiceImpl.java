package com.wup.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wup.domain.Employee;
import com.wup.service.EmployeeService;
import com.wup.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

/**
* @author brainwu
* @description 针对表【employee(员工信息)】的数据库操作Service实现
* @createDate 2023-09-28 17:51:14
*/
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee>
    implements EmployeeService{

}




