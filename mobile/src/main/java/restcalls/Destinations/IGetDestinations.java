package restcalls.destinations;

/**
 * Created by parijathar on 6/20/2016.
 */
public interface IGetDestinations {
    public void onGetDestinationSuccess(DestinationsModel destinationsModel);
    public void onGetDestinationFailure(String errorMessage);
}
