import { Component } from '@angular/core';
import { CardModule } from 'primeng/card';
import {FormControl, FormGroup} from "@angular/forms";
import {SliderChangeEvent} from "primeng/slider";
@Component({
  selector: 'app-simulation-control',
  templateUrl: './simulation-control.component.html',
  styleUrl: './simulation-control.component.scss'
})
export class SimulationControlComponent {
  simulationSpeed: number = 0;
  isRunning: boolean = false;

  startSimulation(){
    this.isRunning = true;
  }

  stopSimulation() {
    this.isRunning = false;
  }

  onSimulationSpeedChange(selectedValue: SliderChangeEvent) {
    // Do something with the selected value
  }
}
