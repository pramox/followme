import {Component, OnInit} from '@angular/core';
import {VehicleServiceService} from "../../../services/vehicle-service.service";
import {MotionDataDto} from "../../../models/MotionDataDto";
import {MatchDTO} from "../../../models/MatchDto";
import {forkJoin, interval, Subscription, switchMap} from "rxjs";
import {DataStorageService} from "../../../services/data-storage.service";
import {EventLog} from "../../../models/EventLog";
import {EventServiceService} from "../../../services/event-service.service";


@Component({
  selector: 'app-vehicle-overview',
  templateUrl: './vehicle-overview.component.html',
  styleUrl: './vehicle-overview.component.scss'
})
export class VehicleOverviewComponent implements OnInit {
  private POLLING_PERIOD_MS: number = 500;
  vehicles!: MotionDataDto[];
  matches!: MatchDTO[];
  eventLogs!: EventLog[];

  constructor(private vehicleServiceService: VehicleServiceService,
              private eventService: EventServiceService,
              private dataStorageService: DataStorageService) {
  }

  ngOnInit(): void {
    this.fetchVehiclesAndMatches();
    this.startPolling();
  }

  private startPolling() {
    interval(this.POLLING_PERIOD_MS).pipe(
        switchMap(() => forkJoin([
          this.vehicleServiceService.getAllVehicles(),
          this.vehicleServiceService.getAllMatches(),
          this.eventService.getAllEvents()
        ]))
    ).subscribe({
      next: ([vehicles, matches, eventLogs]) => {
        vehicles = this.sortVehicles(vehicles);
        matches = this.sortMatches(matches);
        //eventLogs = this.sortEventLogs(eventLogs);
        this.dataStorageService.setVehicles(vehicles);
        this.dataStorageService.setMatches(matches);
        this.vehicles = vehicles;
        this.matches = matches;
        this.eventLogs = eventLogs;
      },
      error: err => {
        console.error(err);
      }
    });
  }

  public getLabelByRole(role: string) {
    if (role === "LEADING_VEHICLE") {
      return "Leading"
    }
    return "Following";
  }

  private sortVehicles(vehicles: MotionDataDto[]): MotionDataDto[] {
    vehicles.sort((a, b) => {
      if (a.vin < b.vin) {
        return -1;
      } else if (a.vin > b.vin) {
        return 1;
      } else {
        return 0;
      }
    });
    return vehicles;
  }

  private sortMatches(matches: MatchDTO[]) {
    matches.sort((a, b) => {
      if (a.leaderVin < b.leaderVin) {
        return -1;
      } else if (a.leaderVin > b.leaderVin) {
        return 1;
      } else {
        return 0;
      }
    });
    return matches;
  }

  private sortEventLogs(eventLogs: EventLog[]) {
    return eventLogs.sort((a, b) => b.timestamp.getTime() - a.timestamp.getTime());
  };


  private fetchVehiclesAndMatches() {
      this.vehicleServiceService.getAllVehicles().subscribe({
        next: vehicles => {
          this.dataStorageService.setVehicles(vehicles)
        }, error: err => {
          console.error(err)
        }
      });
    this.vehicleServiceService.getAllMatches().subscribe({
      next: matches => {
        this.dataStorageService.setMatches(matches)
      }, error: err => console.error(err)
    });
  }

  getVehicles(): MotionDataDto[] {
    return this.vehicles;
  }
  getMatches(): MatchDTO[] {
    return this.matches;
  }

  formatTime(timestamp: string): string {
    const date = new Date(timestamp);
    return date.toLocaleString('de-DE');
  }

  getColorForRole(vehicle: MotionDataDto) {
    if (vehicle.role === 'LEADING_VEHICLE') {
      return 'leading-color';
    }
    return 'following-color'
  }
}
