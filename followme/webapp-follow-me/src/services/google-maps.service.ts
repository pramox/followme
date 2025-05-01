import { Injectable, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class GoogleMapsService {
  private apiKey: string = 'AIzaSyCsXjQVfG8nqnsupFJS5_RPP7wkSGiNLKE';
  private scriptLoaded: boolean = false;

  constructor(@Inject(DOCUMENT) private document: Document) {}

  load(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.scriptLoaded) {
        resolve();
        return;
      }

      const script = this.document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${this.apiKey}`;
      script.async = true;
      script.defer = true;
      script.onload = () => {
        this.scriptLoaded = true;
        resolve();
      };
      script.onerror = (error: any) => reject(error);
      this.document.head.appendChild(script);
    });
  }
}
