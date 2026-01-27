package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    private UserMapper userMapper;

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

    /**
     * 用户统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 用户统计VO
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 算一下从begin到end之间的日期
        List<LocalDate> dateList = new ArrayList();

        // 遍历从begin到end之间的日期
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }

        // 把dateList转换为逗号分隔的字符串
        String dateListStr = StringUtils.join(dateList, ",");

        // 新用户和总用户数量
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();

        // 遍历其中每一天的新增用户数量
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("endTime", endTime);
            // 总用户数量
            Integer newUser = userMapper.countByMap(map);
            // 小技巧 先put一个进去 再根据map查询 新增用户数量 - 总用户数量
            map.put("beginTime", beginTime);
            // 新增用户数量
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }

         // 把newUserList转换为逗号分隔的字符串
        String newUserListStr = StringUtils.join(newUserList, ",");
        // 把totalUserList转换为逗号分隔的字符串
        String totalUserListStr = StringUtils.join(totalUserList, ",");
        // 封装返回
        return UserReportVO.builder()
                .dateList(dateListStr)
                .newUserList(newUserListStr)
                .totalUserList(totalUserListStr)
                .build();
    }

    /**
     * 订单统计
     * @param begin 开始日期
     * @param end 结束日期
     * @return 订单统计VO
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 算一下从begin到end之间的日期
        List<LocalDate> dateList = new ArrayList();
        // 遍历从begin到end之间的日期
        for (LocalDate date = begin; !date.isAfter(end); date = date.plusDays(1)) {
            dateList.add(date);
        }
        // 日期列表 把dateList转换为逗号分隔的字符串
        String dateListStr = StringUtils.join(dateList, ",");

        // 存放每天的订单数
        List<Integer> allOrderCountList = new ArrayList<>();
        // 存放每天的有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        // 遍历其中每一天的订单数
        for(LocalDate date : dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            // 每天订单总数
            Integer allOrderCount = getOrderCount(beginTime, endTime, null);
            // 有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);
            // 把每天的订单数和有效订单数添加到列表中
            allOrderCountList.add(allOrderCount);
            validOrderCountList.add(validOrderCount);
        }

        // 把orderCountList转换为逗号分隔的字符串
        String allOrderCountListStr = StringUtils.join(allOrderCountList, ",");
        // 把validOrderCountList转换为逗号分隔的字符串
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");

        // 订单总数
        Integer totalOrderCount = allOrderCountList.stream().reduce(Integer::sum).orElse(0);
        // 有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);
        // 订单完成率
        Double orderCompletionRate = validOrderCount == 0 ? 0.0 : (double) validOrderCount / totalOrderCount;

        // 封装好返回
        return OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(allOrderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();

    }

    // 以下是私有方法 供当前方法调用======================================================

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {

        Map map = new HashMap();
        map.put("beginTime", begin);
        map.put("endTime", end);
        map.put("status", status);

        // 订单数
        Integer orderCount = orderMapper.countByMap(map);
        return orderCount;
    }

}
