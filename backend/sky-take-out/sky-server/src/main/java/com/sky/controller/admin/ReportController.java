package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * 数据统计相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据统计相关接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额统计  前端传参的参数名与后端方法形参名不一致，导致 Spring MVC 没有成功绑定参数。 必须用begin和end 来接收参数
     * @param begin 开始日期
     * @param end 结束日期
     * @return 营业额统计VO
     */
     @RequestMapping("/turnoverStatistics")
     @ApiOperation("数据统计-营业额统计")
     public Result<TurnoverReportVO> turnoverStatistics(
             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {


         log.info("数据统计-营业额统计 日期范围{}~{}", begin, end);

         return Result.success(reportService.getTurnoverStatistics(begin, end));

     }

     /**
      * 用户统计
      * @param begin 开始日期
      * @param end 结束日期
      * @return 用户统计VO
      */
     @RequestMapping("/userStatistics")
     @ApiOperation("数据统计-用户统计")
     public Result<UserReportVO> userStatistics(
             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
             @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

         log.info("数据统计-用户统计 日期范围{}~{}", begin, end);

         return Result.success(reportService.getUserStatistics(begin, end));

     }


}
