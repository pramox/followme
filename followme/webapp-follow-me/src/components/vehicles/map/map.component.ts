/// <reference types="@types/google.maps" />
import { Component, OnInit, Input } from '@angular/core';
import { GoogleMapsService } from '../../../services/google-maps.service';
import {MotionDataDto} from "../../../models/MotionDataDto";
import {interval, map} from "rxjs";
import {DataStorageService} from "../../../services/data-storage.service";
import Marker = google.maps.Marker;
@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit {
  private POLLING_PERIOD_MS: number = 1_000;

  private map!: google.maps.Map;

  private markers!: Marker[];

  isLiveView: boolean;
  autoFocusOnMarker: boolean;
  constructor(private googleMapsService: GoogleMapsService,
              private dataStorageService: DataStorageService) {
    this.markers = [];
    this.isLiveView = true;
    this.autoFocusOnMarker = true;
  }

  ngOnInit(): void {
    this.googleMapsService.load().then(() => {
      this.initMap();
    }).catch(err => {
      console.error('Error loading Google Maps API', err);
    });
    this.startPolling();
  }

  private initMap(): void {
    const mapOptions: google.maps.MapOptions = {
      center: { lat: 0, lng: 0 },
      zoom: 2
    };
    this.map = new google.maps.Map(document.getElementById('map') as HTMLElement, mapOptions);
  }

  private startPolling() {
    interval(this.POLLING_PERIOD_MS).subscribe(() => {
      if (!this.isLiveView) return;
      this.setMarkers();
      if (!this.autoFocusOnMarker) return;
      this.setMapFrame();
    });
  }

  private setMarkers() {
    //this.clearMarkers();
    const vehicles: MotionDataDto[] = this.dataStorageService.getVehicles();
    if (vehicles != null && vehicles.length > 0) {
      vehicles.forEach(vehicle => {
        this.removeOldVehicleMarker(vehicle);
        this.addMarker(vehicle)
      });
      this.setMapOnAll();
    }
  }

  // Adds a marker to the map and push to the array.
  private addMarker(vehicle: MotionDataDto) {
    const location = {lat: vehicle.latitude, lng: vehicle.longitude};
    const beachFlagImg = document.createElement("img");

    if (vehicle.role === "FOLLOWING_VEHICLE") {
      var img = {
        url: "assets/marker_car_follow.png",
        size: new google.maps.Size(50, 50),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(0, 50),
      };
    } else {
      var img = {
        url: "assets/marker_car_lead.png",
        size: new google.maps.Size(50, 50),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(0, 50),
      };
    }
    const marker = new google.maps.Marker ({
      position: location,
      map: this.map,
      title: vehicle.vin,
      icon: img,
      label: vehicle.vin
    });
    this.markers.push(marker);
  }

  private removeOldVehicleMarker(vehicle: MotionDataDto) {
    this.markers = this.markers.filter(marker => {
      if (marker.getTitle() === vehicle.vin) {
        marker.setMap(null);
        return false;
      }
      return true; // Keep this marker in the new array
    });
  }

  // Sets the map on all markers in the array.
  private setMapOnAll() {
    for (var i = 0; i < this.markers.length; i++) {
      (this.markers)[i].setMap(this.map);
    }
  }

  // Removes the markers from the map, but keeps them in the array.
  private clearMarkers() {
    for (let i = 0; i < this.markers.length; i++) {
      this.markers[i].setMap(null); // Remove marker from map
    }
    this.markers = []; // Clear the markers array
  }

  // Shows any markers currently in the array.
  private showMarkers() {
    this.setMapOnAll();
  }

  // Deletes all markers in the array by removing references to them.
  private deleteMarkers() {
    this.clearMarkers();
    this.markers = [];
  }


  private setMapFrame() {
    const bounds = new google.maps.LatLngBounds();
    const padding = 0.007; // Adjust the padding as needed

    this.markers.forEach((marker) => {
      const position = marker.getPosition();
      if (position) {
        bounds.extend(position);
      }
    });

    // Extend the bounds by adding padding
    const sw = bounds.getSouthWest();
    const ne = bounds.getNorthEast();
    bounds.extend(new google.maps.LatLng(sw.lat() - padding, sw.lng() - padding));
    bounds.extend(new google.maps.LatLng(ne.lat() + padding, ne.lng() + padding));

    this.map.fitBounds(bounds);
  }


  private slightlyZoomOut() {
    if (this.map) { // Check if this.map is defined
      const zoomOutFactor = 0.8; // You can adjust this value as needed
      const listener = google.maps.event.addListener(this.map, 'idle', () => {
        // @ts-ignore
        this.map.setZoom(zoomOutFactor * this.map.getZoom());
        google.maps.event.removeListener(listener);
      });
    }
  }

}
