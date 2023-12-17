package com.neo.weights.controller;

import com.neo.weights.model.TableData;
import com.neo.weights.service.TableDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/output")
public class ProductionViewController {

    private final TableDataService tableDataService;

    //http://localhost:8080/api/v1/output/production_view
    @GetMapping("/production_view")
    public ModelAndView handleView(Model model, HttpServletRequest request, @RequestParam(name = "submitted", defaultValue = "false") String submitted,
                                   @RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber) {
        if (submitted.equals("true")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy");
            LocalDate startDate = LocalDate.parse(request.getParameter("startDate"), formatter);
            LocalDate endDate = LocalDate.parse(request.getParameter("endDate"), formatter);
            ;
            LocalTime startTime = LocalTime.parse(request.getParameter("startTime"));
            LocalTime endTime = LocalTime.parse(request.getParameter("endTime"));
            int hoursDelta = Math.abs(endTime.getHour() - startTime.getHour()) + 1;
            if (startDate != null) {
                Page<TableData> dataPage = tableDataService.getPageDataFromPeriod(startDate, endDate, startTime, endTime, Pageable.ofSize(hoursDelta).withPage(pageNumber));
                System.out.println(dataPage.getTotalPages());
                model.addAttribute("dataPage", dataPage);
            }
        }
        return new ModelAndView("table2");
    }

    @PostMapping("/production_view")
    private ModelAndView handlePostViewParam(@RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                             @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                             @RequestParam("startTime") @DateTimeFormat(pattern = "HH:mm") LocalTime startTime,
                                             @RequestParam("endTime") @DateTimeFormat(pattern = "HH:mm") LocalTime endTime,
                                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("startDate", startDate);
        redirectAttributes.addAttribute("endDate", endDate);
        redirectAttributes.addAttribute("startTime", startTime);
        redirectAttributes.addAttribute("endTime", endTime);
        redirectAttributes.addAttribute("pageNumber", 0);
        redirectAttributes.addAttribute("submitted", "true");
        return new ModelAndView("redirect:/api/v1/output/production_view");
    }
}
