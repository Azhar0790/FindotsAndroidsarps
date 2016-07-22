package com.knowall.findots.distancematrix;

import android.content.Context;
import android.widget.Toast;

import com.knowall.findots.FinDotsApplication;
import com.knowall.findots.R;
import com.knowall.findots.distancematrix.model.DistanceMatrix;
import com.knowall.findots.restservice.RestClient;
import com.knowall.findots.restservice.RestURLs;
import com.knowall.findots.utils.GeneralUtils;


import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by parijathar on 7/12/2016.
 */
public class DistanceMatrixService {

    public IDistanceMatrix delegate = null;
    Context context = null;
    String units = "metric";

    public DistanceMatrixService() {

    }

    public void callDistanceMatrixService(Context context, String origins, String destinations) {

        //GeneralUtils.initialize_progressbar(context);
        if (context == null) {
            delegate.onDistanceMatrixFailure();
        } else {

            Call<DistanceMatrix> call = FinDotsApplication.getRestClient().getApiService()
                    .distanceMatrix(DistanceMatrixURL.MethodName_DISTANCEMATRIX, units,
                            context.getString(R.string.server_key),
                            origins,
                            destinations);

            call.enqueue(new Callback<DistanceMatrix>() {
                @Override
                public void onResponse(Response<DistanceMatrix> response, Retrofit retrofit) {
                    //GeneralUtils.stop_progressbar();

                    if (response.isSuccess()) {
                        DistanceMatrix distanceMatrix = response.body();
                        delegate.onDistanceMatrixSuccess(distanceMatrix);
                    } else {
                        delegate.onDistanceMatrixFailure();
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    //GeneralUtils.stop_progressbar();
                    delegate.onDistanceMatrixFailure();
                }
            });

        }
    }

}
