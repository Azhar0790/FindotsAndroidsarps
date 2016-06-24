package interfaces;

/**
 * Created by parijathar on 6/14/2016.
 */
public interface IDestinations {

    public void onDestinationSelected(int itemPosition);
    public void callCheckInCheckOutService(int destinationPosition, boolean isCheckedIn);

}
