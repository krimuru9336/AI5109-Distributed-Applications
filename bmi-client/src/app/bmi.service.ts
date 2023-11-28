import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BmiService {
  /*
    Author: Azamat Afzalov
    Matriculation number: 1492864
    Date: 05.11.2023
  */
  constructor(
    private http: HttpClient,
  ) { }

  getAll() {
    return this.http.get('http://20.172.68.200:8080/api/v1/bmi');
  }

  submitForm(body: any) {
    return this.http.post('http://20.172.68.200:8080/api/v1/bmi', body)
  }
}
