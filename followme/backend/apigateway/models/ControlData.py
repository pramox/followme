from datetime import datetime

from pydantic import BaseModel


class ControlData(BaseModel):
    vin: str
    speed: float
    lane: int
    timestamp: datetime
