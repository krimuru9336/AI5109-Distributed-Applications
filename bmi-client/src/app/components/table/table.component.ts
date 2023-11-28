import {Component, OnInit} from '@angular/core';
import {BmiService} from "../../bmi.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit{

  /*
     Author: Azamat Afzalov
     Matriculation number: 1492864
     Date: 05.11.2023
  */

  tableData: any[] = [];
  constructor(private bmiService: BmiService, private router: Router) {
  }
  ngOnInit() {
    this.bmiService.getAll().subscribe((data: any) => {
      this.tableData = data;
    })
  }

  async navigateToForm() {
    await this.router.navigate(['/'])
  }
}
