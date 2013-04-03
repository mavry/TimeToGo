package com.cheetah;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.cheetah.controller.ILocationController;
import com.cheetah.controller.LocationController;
import com.cheetah.view.ILocationView;
import com.google.inject.AbstractModule;

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
