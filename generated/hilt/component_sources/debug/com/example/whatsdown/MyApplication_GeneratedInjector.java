package com.example.whatsdown;

import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedEntryPoint;

@OriginatingElement(
    topLevelClass = MyApplication.class
)
@GeneratedEntryPoint
@InstallIn(SingletonComponent.class)
public interface MyApplication_GeneratedInjector {
  void injectMyApplication(MyApplication myApplication);
}
