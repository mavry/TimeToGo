package com.timetogo;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.inject.AbstractModule;
import com.timetogo.activity.LocationActivity;
import com.timetogo.controller.ILocationController;
import com.timetogo.controller.LocationController;
import com.timetogo.view.ILocationView;

public class MyModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(ILocationController.class).to(LocationController.class);
    //    bind(RetreivesRouteResult.class).to(RetreivesRouteResult.class);
    //    bind(RetrievesGeoLocation.class).to(RetrievesGeoLocation.class);
    bind(HttpClient.class).to(DefaultHttpClient.class);
    //    bind(LocationActivity.class).to(LocationActivity.class);
    bind(ILocationView.class).to(LocationActivity.class);
  }
}
