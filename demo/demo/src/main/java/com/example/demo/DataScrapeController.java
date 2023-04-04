package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;


@CrossOrigin
@RestController
public class DataScrapeController {
    @Autowired
    private Scraper scraper;
    public HashSet<Company> data(String date) {
        HashSet<String> resLinks = new HashSet<>();
        HashSet<Company> comList = new HashSet<>();
        scraper.getLinks(date, resLinks);
        for (String link : resLinks) {
            comList.add(scraper.getInfo(link));
        }
        return comList;
    }
    @Autowired
    private ExcelService excelService;
    public ResponseEntity<Resource> exportData(String date){
        HashSet<String> resLinks = new HashSet<>();
        HashSet<Company> comList = new HashSet<>();
        scraper.getLinks(date, resLinks);
        System.out.println(resLinks.size());
        for (String link : resLinks) {
            comList.add(scraper.getInfo(link));
        }
        System.out.println("get info done");
        ResourceDTO resourceDTO = excelService.export(comList);
        System.out.println("write file done");

        HttpHeaders httpHeaders=new HttpHeaders();
        httpHeaders.add("Content-Disposition",
                "attachment; filename="+"hosocongty.xlsx");

        return ResponseEntity.ok()
                .contentType(resourceDTO.getMediaType())
                .headers(httpHeaders)
                .body(resourceDTO.getResource());
    }
}

