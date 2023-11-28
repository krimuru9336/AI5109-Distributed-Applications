import { Component } from '@angular/core';
import {BmiService} from "../../bmi.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent {

  // Author: Azamat Afzalov
  // Matriculation number: 1492864
  // Date: 05.11.2023
  formData: {name: string, phone: string, weight: string, height: string} = {
    name: '',
    phone: '',
    weight: '',
    height: ''
  };
  bmiData: any;

  constructor(private bmiService: BmiService, private router: Router) {}


  onSubmit(event: Event) {
    event.preventDefault();
    console.log(this.formData);
    this.bmiService.submitForm(this.formData).subscribe(async (response) => {
     await this.navigateToTable();
    })
  }

  async navigateToTable() {
    await this.router.navigate(['table']);
  }
}
