package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.UUID;

@Controller
public class MyController {

    @GetMapping("/")
    public String index(Model model) {
        MyData data = new MyData();
        model.addAttribute("data", data);
        return "index";
    }
    @Autowired
    private Scraper scraper;

    @Autowired
    private ExcelService excelService;

    @PostMapping("/submit")
    public ResponseEntity<Resource> exportData(Model model, @ModelAttribute MyData data) {
        HashSet<String> resLinks = new HashSet<>();
        HashSet<Company> comList = new HashSet<>();
        scraper.getLinks(data.getInputValue(), resLinks);
        System.out.println(resLinks.size());
        for (String link : resLinks) {
            comList.add(scraper.getInfo(link));
        }
        System.out.println("get info done");
        ResourceDTO resourceDTO = excelService.export(comList);
        System.out.println("write file done");

        model.addAttribute("comList", comList);

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Content-Disposition",
                "attachment; filename="+"hosocongty.xlsx");

        return ResponseEntity.ok()
                .contentType(resourceDTO.getMediaType())
                .headers(httpHeaders)
                .body(resourceDTO.getResource());
    }
}
