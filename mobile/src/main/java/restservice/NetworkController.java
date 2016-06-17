package restservice;

import locationUtils.LocationModel.BackgroundLocData;
import locationUtils.LocationModel.LocationResponseData;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.POST;


public interface NetworkController {
    @POST(RestURLs.METHOD_LCATION_TRACKING)
    Call<LocationResponseData> getLogin(@Body BackgroundLocData bgData);

}
