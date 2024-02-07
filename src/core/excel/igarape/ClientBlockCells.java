package core.excel.igarape;

import core.excel.converter.RequestItem;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.ArrayList;

public class ClientBlockCells {

    private Sheet sheet;
    private String[] storeInfos;
    private ArrayList<RequestItem> items;

    public ClientBlockCells(Sheet sheet,String[] storeInfos, ArrayList<RequestItem> items) {
        this.sheet = sheet;
        this.storeInfos = storeInfos;
        this.items = items;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public String[] getStoreInfos() {
        return storeInfos;
    }

    public ArrayList<RequestItem> getItems() {
        return items;
    }

    public String getStoreNumber() {
        return storeInfos[0];
    }

    public String getStoreName() {
        return storeInfos[1];
    }

    public String getOrderNumber() {
        return storeInfos[2];
    }

    public String getStoreObs(){
        return storeInfos[3];
    }
}
