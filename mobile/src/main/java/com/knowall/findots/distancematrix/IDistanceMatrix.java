package com.knowall.findots.distancematrix;

import com.knowall.findots.distancematrix.model.DistanceMatrix;

/**
 * Created by parijathar on 7/12/2016.
 */
public interface IDistanceMatrix {
    public void onDistanceMatrixSuccess(DistanceMatrix distanceMatrix);
    public void onDistanceMatrixFailure();
}
