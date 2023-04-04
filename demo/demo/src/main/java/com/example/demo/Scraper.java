package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;

@Service
public class Scraper {
    //final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Safari/537.36";

    public void getLinks(String date, HashSet<String> resLinks) {
        Document doc;
        int pageNum = 1;
        boolean flag = true;
        while (flag) {
            try {
                doc = Jsoup.connect("https://hosocongty.vn/ngay-" + date + "/page-" + pageNum).get();
                if(doc.select("h3").text().contains("tìm thấy 0 hồ sơ công ty ")) {
                    flag = false;
                    continue;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Elements list = doc.select("ul.hsdn");
            Elements links = list.select("a");
            for (Element el : links) {
                String link = el.toString().split(" ")[1];
                String res = link.replace("\"", "").replace("href=", "");
                if (!resLinks.contains(res)) {
                    resLinks.add(res);
                }
            }
            pageNum++;
        }
    }

    public Company getInfo(String link) {
        Document profile = null;
        try {
            profile = Jsoup.connect("https://hosocongty.vn/" + link).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Company temp = new Company();
        temp.setLink("https://hosocongty.vn/" + link);
        temp.setTen(profile.select("h1").text());
        Elements li = profile.select("li");
        for (int j = 3; j < 11; j++) {
            Element el = li.get(j);
            if (el.text().contains("Địa chỉ thuế")) {
                String diachi[] = el.text().split(", ");
                String tp = diachi[diachi.length - 1];
                temp.setDiaChi(tp);
                continue;
            }
            if (el.text().contains("Điện thoại")) {
                String str[] = el.text().split(":");
                temp.setDThoai(str[str.length - 1].trim());
            }
            if (el.text().contains("Ngày cấp")) {
                String str[] = el.text().split(":");
                temp.setNgayCap(str[str.length - 1].trim());
            }
            if (el.text().contains("Ngành nghề chính")) {
                String str[] = el.text().split(":");
                temp.setNganhNgheChinh(str[str.length - 1]);
            }
            if (el.text().contains("Mã số thuế")) {
                String str[] = el.text().split(":");
                temp.setTaxCode(str[str.length - 1]);
            }
            if (el.text().contains("Đại diện pháp luật")) {
                String str[] = el.text().split(":");
                temp.setDaiDien(str[str.length - 1]);
            }
        }
        return temp;
    }
}
