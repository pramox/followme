import {Injectable} from "@angular/core";
import {MotionDataDto} from "../models/MotionDataDto";
import {MatchDTO} from "../models/MatchDto";


@Injectable({
    providedIn: 'root'
})
export class DataStorageService {
    private _vehicles: MotionDataDto[] = [];
    private _matches: MatchDTO[] = [];


    getVehicles(): MotionDataDto[] {
        return this._vehicles;
    }

    setVehicles(value: MotionDataDto[]) {
        this._vehicles = value;
    }

    getMatches(): MatchDTO[] {
        return this._matches;
    }

    setMatches(value: MatchDTO[]) {
        this._matches = value;
    }
}
