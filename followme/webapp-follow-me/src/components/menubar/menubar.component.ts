import { Component } from '@angular/core';
import {MenuItem} from "primeng/api";

@Component({
  selector: 'app-menubar',
  templateUrl: './menubar.component.html',
  styleUrl: './menubar.component.scss'
})
export class MenubarComponent {
  items: MenuItem[] | undefined;

  ngOnInit() {
    this.items = [
      {
        label: 'Vehicle Overview',
        icon: 'pi pi-home',
        routerLink: [''],
      },
      // {
      //   label: 'Simulation',
      //   icon: 'pi pi-caret-right',
      //   routerLink: ['/simulation']
      // },
      {
        label: 'About',
        icon: 'pi pi-info-circle',
        routerLink: ['/about']
      }
    ]
  }

}
