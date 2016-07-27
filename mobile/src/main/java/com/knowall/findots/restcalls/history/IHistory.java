package com.knowall.findots.restcalls.history;

/**
 * Created by parijathar on 7/27/2016.
 */
public interface IHistory {
    public void onHistorySuccess(HistoryModel historyModel);
    public void onHistoryFailure(String errorMessage);
}
