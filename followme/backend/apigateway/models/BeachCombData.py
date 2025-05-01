from datetime import datetime

from pydantic import BaseModel


class BeachCombData(BaseModel):
    vin: str
    role: str
    longitude: float
    latitude: float
    speed: float
    lane: int
    timestamp: datetime
