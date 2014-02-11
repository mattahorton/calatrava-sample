package com.sample;

import com.calatrava.bridge.CalatravaApplication;

public class Sample extends CalatravaApplication
{
  @Override
  public void onCreate()
  {
    // Call this to start Calatrava
    bootCalatrava("com.sample");
  }
}
