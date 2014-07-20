package com.timetogo;

import com.google.inject.AbstractModule;
import com.timetogo.facade.ILocationController;
import com.timetogo.facade.LocationController;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class MyModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ILocationController.class).to(LocationController.class);
    //    bind(RetreivesRouteResult.class).to(RetreivesRouteResult.class);
    //    bind(RetrievesGeoLocation.class).to(RetrievesGeoLocation.class);
    bind(HttpClient.class).to(DefaultHttpClient.class);
    //    bind(LocationActivity.class).to(LocationActivity.class);
//    bind(ILocationView.class).to(LocationActivity.class);
  }
}
