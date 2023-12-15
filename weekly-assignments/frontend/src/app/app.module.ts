import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgToastModule } from 'ng-angular-popup';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PostsComponent } from './posts/posts.component';
import { StudentsComponent } from './students/students.component';

@NgModule({
  declarations: [AppComponent, PostsComponent, StudentsComponent],
  imports: [
    BrowserModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgToastModule,
    AppRoutingModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
