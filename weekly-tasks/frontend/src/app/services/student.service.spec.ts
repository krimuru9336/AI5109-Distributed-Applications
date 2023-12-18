import { TestBed } from '@angular/core/testing';

import { StudentService } from './student.service';
//Abu Sadat Md. Soyam
//Matriket Nr: 1365263 
//Date : 07-11-2023


describe('StudentService', () => {
  let service: StudentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StudentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
