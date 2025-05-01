import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AboutComponent} from "../components/about/about.component";
import {SimulationComponent} from "../components/sim/simulation/simulation.component";
import {VehicleOverviewComponent} from "../components/vehicles/vehicle-overview/vehicle-overview.component";

const routes: Routes = [
  { path: '', component: VehicleOverviewComponent },
  { path: 'simulation', component: SimulationComponent },
  { path: 'about', component: AboutComponent },
  { path: '**', component: VehicleOverviewComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
