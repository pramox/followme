#!/usr/bin/env python3

import os
from urllib.parse import urljoin

import requests
from fastapi import FastAPI, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware

from models.BeachCombData import BeachCombData
from models.ControlData import ControlData
from models.VehicleData import VehicleData

app = FastAPI()

allowed_origins = [
    "http://localhost:4200",  # Example: Your Angular app origin
    "http://127.0.0.1:8000",  # Example: Your FastAPI app origin
    "http://34.149.41.50",
    "http://34.149.86.27"
    # Add more origins as needed
]

# Configure CORS settings
app.add_middleware(
    CORSMiddleware,
    allow_origins=allowed_origins,
    allow_origin_regex="https?://.*",
    allow_credentials=True,
    allow_methods=["GET", "POST", "DELETE", "OPTIONS"],  # Allow only POST method
    allow_headers=['DNT', 'X-CustomHeader', 'Keep-Alive', 'User-Agent', 'X-Requested-With', 'If-Modified-Since', 'Cache-Control', 'Content-Type'],      # Allow all headers
)

inventory_url = os.getenv("INVENTORY_URL", "http://localhost:8001/inventory")
beachcomb_url = os.getenv("BEACHCOMB_URL", "http://localhost:8002/beachcomb")
control_url = os.getenv("CONTROL_URL", "http://localhost:8003/vehicle")


@app.get("/healthcheck")
async def healthcheck():
    return {"health": "good"}


# datafeeder sends movement data periodically to beachcomb service and gets response from control service
@app.post("/movementdata")
async def post_vehicle_movement_data(vehicle_data: VehicleData):
    print(vehicle_data.model_dump())
    try:
        inventory_response = requests.post(
           inventory_url, json=vehicle_data.model_dump(mode='json')
        )
        inventory_response.raise_for_status()

        # send motion data to beachcomb service
        beachcomb_data = mapVehicleDataToBeachcombData(vehicle_data)
        print(beachcomb_data.model_dump())
        beachcomb_response = requests.post(
            beachcomb_url, json=beachcomb_data.model_dump(mode='json')
        )
        beachcomb_response.raise_for_status()

        # send matching related data to control service
        control_data = mapVehicleDataToControlData(vehicle_data)
        target_url = control_url + "/following" if vehicle_data.role == "FOLLOWING_VEHICLE" \
            else control_url + "/leading"
        control_response = requests.post(target_url, json=control_data.model_dump(mode='json'))
        control_response.raise_for_status()
        return control_response.json()

    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        print(e)
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))


@app.get("/vehicle/movement/{vin}")
async def getVehicleMovementData(vin: str):
    try:
        beachcomb_response = requests.get(
            beachcomb_url + "/" + vin
        )
        beachcomb_response.raise_for_status()
        return beachcomb_response
    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))


@app.get("/vehicle/movement")
async def getAllVehicleMovementData():
    try:
        beachcomb_response = requests.get(
            beachcomb_url
        )
        print(beachcomb_response)
        beachcomb_response.raise_for_status()
        return beachcomb_response.json()
    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))


@app.get("/vehicle/matches")
async def getAllMatches():
    print("REQUEST RECEIVED matches")
    try:
        control_response = requests.get(
            control_url
        )
        control_response.raise_for_status()
        return control_response.json()
    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))


@app.get("/vehicle/inventory/{vin}")
async def getVehicleInventoryData(vin: str):
    try:
        inventory_response = requests.get(
            inventory_url + "/" +  vin
        )
        inventory_response.raise_for_status()
        return inventory_response.json()
    except requests.exceptions.RequestException as e:
        # Handle connection errors, timeouts, etc.
        raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))

@app.get("/vehicle/events")
async def getAllEvents():
    try:
        control_response = requests.get(
            control_url + "/events"
        )
        control_response.raise_for_status()
        return control_response.json()
    except requests.exceptions.RequestException as e:
            # Handle connection errors, timeouts, etc.
            raise HTTPException(status_code=status.HTTP_503_SERVICE_UNAVAILABLE, detail=str(e))
    except (KeyError, ValueError) as e:
        # Handle invalid data (e.g., missing key in response)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail=str(e))


def mapVehicleDataToBeachcombData(vehicle_data: VehicleData):
    return BeachCombData(vin=vehicle_data.vin,
                         role=vehicle_data.role,
                         longitude=vehicle_data.longitude,
                         latitude=vehicle_data.latitude,
                         speed=vehicle_data.speed,
                         lane=vehicle_data.lane,
                         timestamp=vehicle_data.timestamp)


def mapVehicleDataToControlData(vehicle_data: VehicleData):
    return ControlData(vin=vehicle_data.vin,
                       speed=vehicle_data.speed,
                       lane=vehicle_data.lane,
                       timestamp=vehicle_data.timestamp)
