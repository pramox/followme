import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {MotionDataDto} from "../models/MotionDataDto";
import {MatchDTO} from "../models/MatchDto";

@Injectable({
  providedIn: 'root'
})
export class VehicleServiceService {

  private baseUrl = environment.apiUrl;


  constructor(private http: HttpClient) {

  }


  getAllVehicles(): Observable<MotionDataDto[]> {
    const url = `${this.baseUrl}/vehicle/movement`;
    return this.http.get<MotionDataDto[]>(url);
  }

  getAllMatches(): Observable<MatchDTO[]> {
    const url = `${this.baseUrl}/vehicle/matches`;
    return this.http.get<MatchDTO[]>(url);
  }
}
