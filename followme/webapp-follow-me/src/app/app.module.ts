import { NgModule } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {SimulationControlComponent} from "../components/sim/simulation-control/simulation-control.component";
import {CardModule} from "primeng/card";
import {SliderModule} from "primeng/slider";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {PanelModule} from "primeng/panel";
import {Button} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {SimulationViewComponent} from "../components/sim/simulation-view/simulation-view.component";
import {MenubarComponent} from "../components/menubar/menubar.component";
import {MenubarModule} from "primeng/menubar";
import {SimulationComponent} from "../components/sim/simulation/simulation.component";
import {MapComponent} from "../components/vehicles/map/map.component";
import {VehicleOverviewComponent} from "../components/vehicles/vehicle-overview/vehicle-overview.component";
import {AboutComponent} from "../components/about/about.component";
import {provideHttpClient, withFetch, withInterceptorsFromDi} from "@angular/common/http";
import {TableModule} from "primeng/table";
import {CommonModule} from "@angular/common";
import {AccordionModule} from "primeng/accordion";
import {DividerModule} from "primeng/divider";
import {SplitterModule} from "primeng/splitter";
import {ToggleButtonModule} from "primeng/togglebutton";

@NgModule({
  declarations: [
    AppComponent,
    SimulationControlComponent,
    SimulationViewComponent,
    MenubarComponent,
    SimulationComponent,
    MapComponent,
    VehicleOverviewComponent,
    AboutComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    CardModule,
    SliderModule,
    FormsModule,
    PanelModule,
    Button,
    ReactiveFormsModule,
    InputTextModule,
    MenubarModule,
    TableModule,
    CommonModule,
    AccordionModule,
    DividerModule,
    SplitterModule,
    ToggleButtonModule
  ],
  providers: [
    provideClientHydration(),
    provideHttpClient(withInterceptorsFromDi())
  ],
  exports: [
    MenubarComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
