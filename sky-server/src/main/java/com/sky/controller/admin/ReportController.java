package com.sky.controller.admin;


import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags="统计相关接口")
public class ReportController {

    @Autowired
    ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,
                                                       @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end) {
        log.info("营业额统计begin:{},end:{}", begin, end);
        TurnoverReportVO vo = reportService.turnoverStatistics(begin,end);
        return Result.success(vo);
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<UserReportVO> userStatistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,
                                                   @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end) {
        log.info("用户统计begin:{},end:{}", begin, end);
        UserReportVO vo = reportService.userStatistics(begin,end);
        return Result.success(vo);
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<OrderReportVO> orderStatistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,
                                                @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end) {
        log.info("订单统计begin:{},end:{}", begin, end);
        OrderReportVO vo = reportService.orderStatistics(begin,end);
        return Result.success(vo);
    }

    @GetMapping("/top10")
    @ApiOperation("前十统计")
    public Result<SalesTop10ReportVO>top10Statistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,
                                                      @DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end) {
        log.info("前十统计begin:{},end:{}", begin, end);
        SalesTop10ReportVO vo = reportService.top10Statistics(begin,end);
        return Result.success(vo);
    }

    @GetMapping("/export")
    @ApiOperation("导出文件")
    public void export(HttpServletResponse response) throws IOException {
        reportService.exportData(response);

    }

}
