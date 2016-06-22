package restmodels;

import java.util.ArrayList;

/**
 * Created by jpaulose on 6/21/2016.
 */
public class ResponseModel {

    private String message;

    private int errorCode;

    private ArrayList<responseDataModel> data=new ArrayList<responseDataModel>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public ArrayList<responseDataModel> getData() {
        return data;
    }

    public void setData(ArrayList<responseDataModel> data) {
        this.data = data;
    }
}
