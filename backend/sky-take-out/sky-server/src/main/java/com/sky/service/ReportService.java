package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

     /**
      * 统计指定区间内的营业额
      * @param beginDate 开始日期
      * @param endDate 结束日期
      * @return 营业额统计VO
      */
     TurnoverReportVO getTurnoverStatistics(LocalDate beginDate, LocalDate endDate);

     /**
      * 统计指定区间内的用户数量
      * @param begin 开始日期
      * @param end 结束日期
      * @return 用户统计VO
      */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

     /**
      * 统计指定区间内的订单数量
      * @param begin 开始日期
      * @param end 结束日期
      * @return 订单统计VO
      */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

     /**
      * 统计指定区间内的销量排名Top10
      * @param begin 开始日期
      * @param end 结束日期
      * @return 销量排名Top10统计VO
      */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
