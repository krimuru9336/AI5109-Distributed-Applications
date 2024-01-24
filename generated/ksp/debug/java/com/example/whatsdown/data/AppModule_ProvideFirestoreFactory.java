package com.example.whatsdown.data;

import com.google.firebase.firestore.FirebaseFirestore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideFirestoreFactory implements Factory<FirebaseFirestore> {
  private final AppModule module;

  public AppModule_ProvideFirestoreFactory(AppModule module) {
    this.module = module;
  }

  @Override
  public FirebaseFirestore get() {
    return provideFirestore(module);
  }

  public static AppModule_ProvideFirestoreFactory create(AppModule module) {
    return new AppModule_ProvideFirestoreFactory(module);
  }

  public static FirebaseFirestore provideFirestore(AppModule instance) {
    return Preconditions.checkNotNullFromProvides(instance.provideFirestore());
  }
}
