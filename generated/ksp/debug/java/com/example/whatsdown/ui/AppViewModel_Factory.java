package com.example.whatsdown.ui;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AppViewModel_Factory implements Factory<AppViewModel> {
  private final Provider<FirebaseFirestore> dbProvider;

  public AppViewModel_Factory(Provider<FirebaseFirestore> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AppViewModel get() {
    return newInstance(dbProvider.get());
  }

  public static AppViewModel_Factory create(Provider<FirebaseFirestore> dbProvider) {
    return new AppViewModel_Factory(dbProvider);
  }

  public static AppViewModel newInstance(FirebaseFirestore db) {
    return new AppViewModel(db);
  }
}
