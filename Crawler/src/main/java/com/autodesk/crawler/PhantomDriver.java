package com.autodesk.crawler;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Created by gadrea on 12/10/2015.
 */
public class PhantomDriver {

    private PhantomJSDriver _driver;
    private ElasticSearch _elastic;

    public PhantomDriver(ElasticSearch elsatic){
        //Initiate PhantomJS
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability("phantomjs.page.settings.loadImages",false);
        _driver = new PhantomJSDriver(dc);
        _elastic = elsatic;
    }


    private String join(String[] array, int start, int end){
        String text = "";
        for(int i = start; i<= end;i++)
            text = text + array[i] + " ";

        return text;
    }

    public void fetchElementsFromPage(String page, int level){

        String[] levelTags = getLevelTags(level);

        if(level == 3){
            try{

                String[] textArray =  _driver.findElementByCssSelector("div.main.panel").getText().split("[ \t\\x0B\f\r]+");
                int iLen = textArray.length;
                int iMod = iLen / 100;
                for(int i = 0; i <= iMod; i++){
                    String text = "";
                    if(i == iMod)
                        text = join(textArray, (i * 100), iLen - 1);
                    else
                        text = join(textArray, (i * 100), (i * 100) + 99);

                    _elastic.createIndex(page, "Panel", level, text);
                }

            }
            catch(ElementNotFoundException elemNotFound){
                elemNotFound.printStackTrace();
            }
            catch(StaleElementReferenceException staleException){
                staleException.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return;
        }

        for(String tagName : levelTags){
            List<WebElement> elements = _driver.findElementsByTagName(tagName);

            for(WebElement element : elements){
                try{
                    _elastic.createIndex(page, tagName, level, element.getText().trim());
                }
                catch(StaleElementReferenceException staleException){
                    staleException.printStackTrace();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void navigate(String url){
        _driver.navigate().to(url);
    }

    public String[] getLevelTags(int level){
        if(level == 1)
            return new String[] {"Title", "H1", "H2"};
        else if (level == 2)
            return new String[] {"H3", "H4", "B", "strong", "I", "A"};
        else if (level == 3)
            return new String[] {"P", "span", "Div"};
        else
            return  new String[] {};
    }

    public String getTitle(){
        return _driver.getTitle();
    }
}
