package com.sky.service.impl;


import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.LocalDataSourceJobStore;
import org.springframework.stereotype.Service;

import javax.print.DocFlavor;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WorkspaceService workspaceService;
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime beginT=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endT=LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("begin",beginT);
            map.put("end",endT);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            if(turnover==null){turnover=0.0;}
            turnoverList.add(turnover);

        }

        return TurnoverReportVO.builder()
                               .dateList(StringUtils.join(dateList,","))
                               .turnoverList(StringUtils.join(turnoverList,","))
                               .build();
    }

    @Override
    public UserReportVO userStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime beginT=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endT=LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("end",endT);
            Integer user=userMapper.countByMap(map);
            map.put("begin",beginT);
            Integer newUser=userMapper.countByMap(map);
            if(user==null){user=0;}
            if(newUser==null){newUser=0;}
            totalUserList.add(user);
            newUserList.add(newUser);
        }

        return UserReportVO.builder()
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .dateList(StringUtils.join(dateList,","))
                .build();
    }

    @Override
    public OrderReportVO orderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.isEqual(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        List<Integer> orderCount = new ArrayList<>();
        List<Integer> orderCOMPLETEDCount = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime beginT=LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endT=LocalDateTime.of(date, LocalTime.MAX);
            Map map=new HashMap();
            map.put("end",endT);
            map.put("begin",beginT);
            Integer count =orderMapper.countByMap(map);
            if(count==null){count=0;}
            orderCount.add(count);
            map.put("status",Orders.COMPLETED);
            count=orderMapper.countByMap(map);
            if(count==null){count=0;}
            orderCOMPLETEDCount.add(count);
        }

        Integer allCount=orderCount.stream().reduce(Integer::sum).get();
        Integer valid=orderCOMPLETEDCount.stream().reduce(Integer::sum).get();
        Double  rate=0.0;
        if(allCount!=0){
            rate=valid.doubleValue()/allCount.doubleValue();
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCount,","))
                .validOrderCountList(StringUtils.join(orderCOMPLETEDCount,","))
                .totalOrderCount(allCount)
                .validOrderCount(valid)
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public SalesTop10ReportVO top10Statistics(LocalDate begin, LocalDate end) {

        LocalDateTime beginT=LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endT=LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getTop10(beginT,endT);
        List<String>  names=goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer>  numbers=goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        return SalesTop10ReportVO.builder()
                .numberList(StringUtils.join(numbers,","))
                .nameList(StringUtils.join(names,","))
                .build();
    }

    @Override
    public void exportData(HttpServletResponse response) throws IOException {
        LocalDate begin  =LocalDate.now().plusDays(-30);
        LocalDate end  =LocalDate.now().plusDays(-1);

        BusinessDataVO businessDataVO=workspaceService.getBusinessData
                (LocalDateTime.of(begin,LocalTime.MIN),LocalDateTime.of(end,LocalTime.MAX));

        InputStream in =this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        XSSFWorkbook excel=new XSSFWorkbook(in);
        XSSFSheet sheet=excel.getSheet("Sheet1");
        sheet.getRow(1).getCell(1).setCellValue("时间"+begin+"至"+end);
        XSSFRow row=sheet.getRow(3);
        row.getCell(2).setCellValue(businessDataVO.getTurnover());
        row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
        row.getCell(6).setCellValue(businessDataVO.getNewUsers());
        row=sheet.getRow(4);
        row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
        row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

        for(int i=0;i<30;i++){
            LocalDate date=begin.plusDays(i);
            BusinessDataVO businessData=workspaceService.getBusinessData(LocalDateTime.of(date,LocalTime.MIN),LocalDateTime.of(date,LocalTime.MAX));

            row=sheet.getRow(7+i);
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(3).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessData.getUnitPrice());
            row.getCell(6).setCellValue(businessData.getNewUsers());
        }

        ServletOutputStream out =response.getOutputStream();
        excel.write(out);
        out.close();
        excel.close();
        in.close();
    }
}
