package com.knowall.findots.interfaces;

/**
 * Created by parijathar on 6/14/2016.
 */
public interface IDestinations {

    public void onDestinationSelected(int itemPosition);
    public void callCheckInCheckOutService(String checkOutNote, int destinationPosition, boolean isCheckedIn);

}
