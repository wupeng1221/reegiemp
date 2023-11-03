package com.wup;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wup.domain.Employee;
import com.wup.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
@Slf4j
@SpringBootTest
class ReegiempApplicationTests {
	@Autowired
	private EmployeeMapper employeeMapper;

	@Test
	void testSelecte() {
		LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		lambdaQueryWrapper.like(Employee::getName, "心");
		employeeMapper.selectList(lambdaQueryWrapper).forEach(employee ->
				log.info(employee.toString())
		);

	}

}
