package com.autodesk.crawler;

import java.io.IOException;

/**
 * Created by gadrea on 12/8/2015.
 */
public class MainCrawler {

    private final static String hostName = "107.167.191.105";


    public static void main(String[] args){

        //Initate other instances
        ElasticSearch elastic = new ElasticSearch();
        SearchCore core = new SearchCore();
        PhantomDriver driver = new PhantomDriver(elastic);

        String[] pages = null;
        try {
            pages = core.getUrls(10);
            System.out.println("No of pages fetched: " +  pages.length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Loop through pages to get the contents
        for(String page:pages){
            try{

                //Delete existing data
                elastic.bulkDeleteByURL(page);

                System.out.println("Navigating to page " + page);
                driver.navigate(page);
                Thread.sleep(5000);

                //Title, H1, H2
                driver.fetchElementsFromPage(page,1);

                //H3, H4, B, strong, I, A
                driver.fetchElementsFromPage(page,2);

                //Div, Span, P
                driver.fetchElementsFromPage(page,3);

                //Update URL Status
                core.updateURLStatus(page,driver.getTitle());

            }
            catch(IOException e){
                System.out.print("IO Exception occured while indexing to Elastic search");
                e.printStackTrace();
            }
            catch(InterruptedException e){
                System.out.print("Interrupted Exception occured while waiting for page load");
                e.printStackTrace();
            }

        }
    }
}
