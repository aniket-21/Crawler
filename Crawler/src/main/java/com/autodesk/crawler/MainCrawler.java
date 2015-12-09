package com.autodesk.crawler;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;


/**
 * Created by gadrea on 12/8/2015.
 */
public class MainCrawler {

    private final static String hostName = "107.167.191.105";

    public static void main(String[] args){

        String[] pages = {
                "https://accounts.autodesk.com"
        };

        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("phantomjs.page.settings.loadImages",false);

        //Initiate PhantomJS
        PhantomJSDriver _driver = new PhantomJSDriver(dc);

        //Initate Elastic search client
        ElasticSearch elastic = new ElasticSearch();
        try{
            elastic.createTransportClient(hostName);
            //elastic.createNodeClient();
        }
        //catch(UnknownHostException e){
        catch(Exception e){
            System.out.print("Unknown Host Exception occured while connecting to Elastic search");
            e.printStackTrace();
            return;
        }

        for(String page:pages){
            System.out.println("Navigating to page " + page);
            _driver.navigate().to(page);

            try{
                //********************** Level 1 *********************************

                //Title
                System.out.println("Title " +  _driver.getTitle());
                elastic.createIndex(page, "Title", "1",  _driver.getTitle());

                //H1
                List<WebElement> elements = _driver.findElementsByTagName("H1");
                for(WebElement element : elements)
                    System.out.println("H1 " +  element.getText());

                //H2
                elements = _driver.findElementsByTagName("H2");
                for(WebElement element : elements)
                    System.out.println("H2 " +  element.getText());

                //********************** Level 2 *********************************

                //H3
                elements = _driver.findElementsByTagName("H3");
                for(WebElement element : elements)
                    System.out.println("H3 " +  element.getText());

                //H4
                elements = _driver.findElementsByTagName("H4");
                for(WebElement element : elements)
                    System.out.println("H4 " +  element.getText());

                //Strong
                elements = _driver.findElementsByTagName("strong");
                for(WebElement element : elements)
                    System.out.println("Strong " +  element.getText());

                //Anchor
                elements = _driver.findElementsByTagName("A");
                for(WebElement element : elements)
                    System.out.println("Anchor " +  element.getText());

                //bold
                elements = _driver.findElementsByTagName("B");
                for(WebElement element : elements)
                    System.out.println("bold " +  element.getText());

                //Strong
                elements = _driver.findElementsByTagName("I");
                for(WebElement element : elements)
                    System.out.println("Italic " +  element.getText());
            }
            catch(IOException e){
                System.out.print("IO Exception occured while indexing to Elastic search");
                e.printStackTrace();
            }

        }
    }
}
