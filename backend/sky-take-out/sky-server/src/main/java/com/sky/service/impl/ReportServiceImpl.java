package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 营业额统计 应该是查询订单表 已完成的订单
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 营业额统计VO
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate) {
        // 算一下从begin到end之间的日期
        List<LocalDate> dateList = new ArrayList();

        // 添加参数校验
        if (beginDate == null || endDate == null) {
            log.error("日期参数不能为空: beginDate={}, endDate={}", beginDate, endDate);
            throw new IllegalArgumentException("日期参数不能为空");
        }

        if (beginDate.isAfter(endDate)) {
            log.error("开始日期不能晚于结束日期: beginDate={}, endDate={}", beginDate, endDate);
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }

        // 遍历从begin到end之间的日期
        for (LocalDate date = beginDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 把dateList转换为逗号分隔的字符串
        String dateListStr = StringUtils.join(dateList, ",");

        // =======================
        // 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            // 计算date这一天的营业额
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover =  orderMapper.sumByMap(map);

            // 如果营业额为null，默认值为0.0
            if(turnover == null){
                turnover = 0.0;
            }

            turnoverList.add(turnover);
        }
        // 把turnoverList转换为逗号分隔的字符串
        String turnoverListStr = StringUtils.join(turnoverList, ",");

        // =======================

        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();

    }

}
