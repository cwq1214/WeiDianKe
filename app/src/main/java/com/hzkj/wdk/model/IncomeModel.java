package com.hzkj.wdk.model;

import java.util.List;

/**
 * Created by howie on 16/9/2.
 */
public class IncomeModel {
    private String remains;
    private String incomes;
    private String casched;
    private List<IncomesDetailModel> listData;
    private List<IncomesDetailModel> listDataCash;

    public String getRemains() {
        return remains;
    }

    public void setRemains(String remains) {
        this.remains = remains;
    }

    public String getIncomes() {
        return incomes;
    }

    public void setIncomes(String incomes) {
        this.incomes = incomes;
    }

    public String getCasched() {
        return casched;
    }

    public void setCasched(String casched) {
        this.casched = casched;
    }

    public List<IncomesDetailModel> getListData() {
        return listData;
    }

    public void setListData(List<IncomesDetailModel> listData) {
        this.listData = listData;
    }

    public List<IncomesDetailModel> getListDataCash() {
        return listDataCash;
    }

    public void setListDataCash(List<IncomesDetailModel> listDataCash) {
        this.listDataCash = listDataCash;
    }
}
