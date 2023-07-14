package com.jaychou.jaybi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jaychou.jaybi.model.entity.Chart;
import com.jaychou.jaybi.service.ChartService;
import com.jaychou.jaybi.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author JayChou
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-07-07 13:54:47
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




