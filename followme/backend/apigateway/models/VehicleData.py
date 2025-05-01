#!/usr/bin/env python3

from datetime import datetime
from pydantic import BaseModel


class VehicleData(BaseModel):
    vin: str
    oem: str
    type: str
    role: str
    longitude: float
    latitude: float
    speed: float
    lane: int
    timestamp: datetime
