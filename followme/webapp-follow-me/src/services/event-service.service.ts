import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../environments/environment";
import {Observable} from "rxjs";
import {MotionDataDto} from "../models/MotionDataDto";
import {MatchDTO} from "../models/MatchDto";
import {EventLog} from "../models/EventLog";

@Injectable({
  providedIn: 'root'
})
export class EventServiceService {

  private baseUrl = environment.apiUrl;


  constructor(private http: HttpClient) {

  }


  getAllEvents(): Observable<EventLog[]> {
    const url = `${this.baseUrl}/vehicle/events`;
    return this.http.get<EventLog[]>(url);
  }
}
