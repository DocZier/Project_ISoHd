package com.example.kr.web;

import android.util.Log;

import com.example.kr.database.HardDriveData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


public class WebPageParser
{
    private String referenceLink = "https://smarthdd.com";

    private ArrayList<String> man_links;

    public HardDriveData getDesc(String link) throws IOException
    {
        String drive = Jsoup.connect(link).get().html();
        Document drive_desc = Jsoup.parse(drive);
        Element desc = drive_desc.body().getElementsByClass("table table-hover w-auto mx-auto table-sm table-borderless").get(0);
        Elements trs = desc.getElementsByTag("tr");

        String man = trs.get(1).getElementsByTag("td").get(1).getElementsByTag("a").get(0).ownText();
        String model = trs.get(2).getElementsByTag("td").get(1).ownText();
        String cap = trs.get(3).getElementsByTag("td").get(1).ownText();
        String inter = "";
        String form_factor = "";
        String cache = "";
        double capacity = 0;
        int speed = 0;
        int i=5;

        switch (cap.substring(cap.length()-2))
        {
            case "TB":
                capacity = Double.parseDouble(cap.substring(0,cap.length()-2))*1000;
                break;
            case "GB":
                capacity = Double.parseDouble(cap.substring(0,cap.length()-2));
                break;
        }

        inter = trs.get(4).getElementsByTag("td").get(1).getElementsByTag("a").get(0).ownText();

        if(trs.get(i).getElementsByTag("td").get(0).ownText().equals("version"))
        {
            inter+=trs.get(i).getElementsByTag("td").get(1).ownText();
            i+=1;
        }

        if(trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").get(0).ownText().equals("Form-factor"))
        {
            form_factor = trs.get(i).getElementsByTag("td").get(1).ownText();
            i+=1;
        }

        while(i < trs.size() && !trs.get(i).hasClass("text-center pt-3"))
        {
            i++;
        }
        i++;
        while(i < trs.size() && !trs.get(i).hasClass("text-center pt-3"))
        {
            if(!trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").isEmpty() &&
                    trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").hasAttr("href") &&
                    trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").get(0).ownText().equals("Cache buffer"))
            {
                cache =trs.get(i).getElementsByTag("td").get(1).ownText();
            }

            if(!trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").isEmpty() &&
                    trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").hasAttr("href") &&
                    trs.get(i).getElementsByTag("td").get(0).getElementsByTag("a").get(0).ownText().equals("Rotation rate"))
            {
                speed = Integer.parseInt(trs.get(i).getElementsByTag("td").get(1).ownText().substring(0, trs.get(i).getElementsByTag("td").get(1).ownText().length()-3));
            }
            i++;
        }

        HardDriveData driveData = new HardDriveData(man, model, capacity, inter, form_factor, cache, speed);
        return  driveData;
    }

    public void getLinks() throws IOException
    {
        String webPage = referenceLink + "/database/";
        man_links = new ArrayList<>();
        String html = Jsoup.connect(webPage).get().html();
        Document document = Jsoup.parse(html);
        for(int i=1;i<6;i++)
        {
            Elements tag = document.body().getElementsByClass("table table-hover w-auto mx-auto").get(0).getElementsByTag("tr").get(i).getElementsByTag("a");
            man_links.add(tag.attr("href"));
        }
    }

    public ArrayList<HardDriveData> getData(int index) throws IOException
    {
        ArrayList<HardDriveData> data = new ArrayList<>();

        String man_html = Jsoup.connect(referenceLink + man_links.get(index)).get().html();
        Document man_document = Jsoup.parse(man_html);

        for(int i=1;i<11;i++)
        {
            HardDriveData hardDrive;
            Elements tag = man_document.body().getElementsByClass("table table-hover w-auto mx-auto").get(0).getElementsByTag("tr").get(i).getElementsByTag("a");
            String var_link = tag.attr("href");
            if(var_link.length()-var_link.replace("/", "").length()==4)
            {
                hardDrive = getDesc(referenceLink+var_link);
            }
            else
            {
                String neu_man_html = Jsoup.connect(referenceLink+var_link).get().html();
                Document neu_man_document = Jsoup.parse(neu_man_html);
                Elements next_link = neu_man_document.body().getElementsByClass("table table-hover w-auto mx-auto").get(0).getElementsByTag("tr").get(1).getElementsByTag("a");
                String neu = next_link.get(0).attr("href");
                hardDrive = getDesc(referenceLink+neu);
            }

            data.add(hardDrive);
            Log.i("Process", "ID: "+ i +" Man: " + hardDrive.getManufactor() + "Model: " + hardDrive.getModel());
            try {
                Thread.sleep(new Random().nextInt(10000)+3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return  data;
    }

}