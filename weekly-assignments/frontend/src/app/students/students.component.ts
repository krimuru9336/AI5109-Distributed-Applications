import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NgToastService } from 'ng-angular-popup';
import { Student } from '../models/student.model';
import { StudentService } from './services/student.service';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css'],
})
export class StudentsComponent implements OnInit {
  students: Student[] = [];
  id = '';

  // studentForm using FormGroup
  studentForm = new FormGroup({
    name: new FormControl('', Validators.required),
    phone: new FormControl('', [
      Validators.required,
      Validators.pattern('[- +()0-9 ]{11,14}'),
    ]),
  });

  // Constructor of the class
  constructor(
    private router: Router,
    private studentService: StudentService,
    private toast: NgToastService
  ) {}

  // life cycle hook
  ngOnInit() {
    // load all the students info on the frontend using getStudentsInfo method from the service class
    this.studentService.getStudentsInfo().subscribe({
      next: (students: Student[]) => {
        this.students = students;
      },
    });
  }

  // onSubmit method to push the form data to db using createStudentsInfo method from the service class
  onSubmit() {
    if (!this.studentForm.valid) {
      this.toast.error({
        detail: 'Error!',
        summary: 'Invalid student information',
        duration: 3000,
      });
      return;
    }

    this.studentService.createStudentInfo(this.studentForm.value).subscribe({
      next: () => {
        this.router.navigate(['/']);
        this.toast.success({
          detail: 'Success Message',
          summary: 'Student information added successfully!',
          duration: 3000,
        });
      },
    });
    this.studentForm.reset();
  }

  // delete a student info using deleteStudentsInfo method from the service class
  deleteStudent = (id: string) => {
    this.studentService.deleteStudentInfo(id).subscribe({
      next: () => {
        this.toast.warning({
          detail: 'Warning!',
          summary: 'Deleted student information!',
          duration: 3000,
        });
        this.students = this.students.filter((student) => student._id != id);
      },
    });
  };
}
