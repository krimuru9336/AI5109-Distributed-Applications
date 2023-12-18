//Abu Sadat Md. Soyam
//Matriket Nr: 1365263 
//Date : 07-11-2023

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Student } from '../models/student.model';

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  constructor(private http: HttpClient) {}

  // to create the student info
  createStudentInfo = (payload: any) => {
    return this.http.post(`${environment.apiBaseUrl}/tasks`, payload);
  };

  // to get the student info
  getStudentsInfo(): Observable<Student[]> {
    return this.http.get<Student[]>(`${environment.apiBaseUrl}/tasks`);
  }

  // to delete the student info
  deleteStudentInfo = (id: string) => {
    return this.http.delete(`${environment.apiBaseUrl}/tasks/${id}`);
  };
}
