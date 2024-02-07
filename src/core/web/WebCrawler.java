package core.web;

import java.io.IOException;

import static core.web.Config.CHROME_DRIVER_PATH;

abstract class WebCrawler
{
    private final String baseUrl;

    public WebCrawler(String baseUrl)
    {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        this.baseUrl = baseUrl;
    }

    public abstract void crawl() throws IOException;

    public String getBaseUrl() {
        return baseUrl;
    }
}
