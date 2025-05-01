import os
import time
import warnings
from datetime import datetime
from urllib.parse import urljoin

import numpy as np
import requests
from pydantic import BaseModel
from pyproj import Proj, transform
import matplotlib.pyplot as plt

warnings.simplefilter(action='ignore', category=FutureWarning)
warnings.simplefilter(action='ignore', category=np.RankWarning)
warnings.simplefilter(action='ignore', category=DeprecationWarning)

api_gateway_url = os.getenv("API_GATEWAY_URL", "http://34.149.86.27:8000")
api_gateway_url = "http://34.149.86.27:8000"
print(api_gateway_url)
movement_data_endpoint = "/movementdata"

# in lat lon
simStartLat = -36.358
simStartLon = 144.80418230895236

# in seconds
simStartTime = 0
matchStartTime = 30
matchEndTime = 50
simEndTime = 65
matchDuration = matchEndTime - matchStartTime

# in meters
safetyDistance = 100
matchingDistance = 200
simStartDistance = 0
leadingHeadStart = 1_800
matchStartDistance = 3_000
matchEndDistance = 5_000
simEndDistance = 5_700
leadingEndHeadStart = 1_000

fit = True
plotTrack = False
polynomialFitDeg = 60
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


def meters_to_latlon(start_lat, start_lon, delta_east, delta_north):
    wgs84 = Proj(proj='latlong', datum='WGS84')
    local_proj = Proj(proj='aeqd', lat_0=start_lat, lon_0=start_lon)

    x0, y0 = transform(wgs84, local_proj, start_lon, start_lat)

    x_new = x0 + delta_east
    y_new = y0 + delta_north

    new_lon, new_lat = transform(local_proj, wgs84, x_new, y_new)

    return new_lat, new_lon


def generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV):
    leadingVehicle = VehicleData(
        vin="W-1234",
        oem="Honda",
        type="Civic",
        role="LEADING_VEHICLE",
        longitude=longLV,
        latitude=latLV,
        speed=speedLV,
        lane=laneLV,
        timestamp=datetime.utcnow()
    )
    followingVehicle = VehicleData(
        vin="KO-7281",
        oem="VW",
        type="GOLF 4",
        role="FOLLOWING_VEHICLE",
        longitude=longFV,
        latitude=latFV,
        speed=speedFV,
        lane=laneFV,
        timestamp=datetime.utcnow()
    )
    return leadingVehicle, followingVehicle


def send_data(data):
    try:
        url = urljoin(api_gateway_url, movement_data_endpoint)
        response = requests.post(url, json=data.model_dump(mode='json'))
        if response.status_code == 200:
            return response
        else:
            print(f"Failed to send data. Status code: {response.status_code}, Response: {response.text}")
            return False
    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")


def get_leading_data():
    y = np.zeros(simEndTime)
    x = np.zeros(simEndTime)

    x[simStartTime:matchStartTime] = np.linspace(simStartDistance + leadingHeadStart, matchStartDistance,
                                                  num=matchStartTime - simStartTime)
    x[matchStartTime:matchEndTime] = np.linspace(matchStartDistance, matchEndDistance,
                                                  num=matchEndTime - matchStartTime)
    x[matchEndTime:simEndTime] = np.linspace(matchEndDistance, simEndDistance + leadingEndHeadStart, num=simEndTime - matchEndTime)

    if fit:
        coefficients = np.polyfit(np.arange(simStartTime, simEndTime), x, deg=polynomialFitDeg)
        x = np.poly1d(coefficients)(np.arange(simStartTime, simEndTime, step=1))
    v = np.gradient(x)

    return x, y, v

def get_following_data():
    y = np.zeros(simEndTime)
    x = np.zeros(simEndTime)

    x[simStartTime:matchStartTime] = np.linspace(simStartDistance, matchStartDistance - matchingDistance,
                                                  num=matchStartTime - simStartTime)
    x[matchStartTime:matchEndTime] = np.linspace(matchStartDistance - matchingDistance, matchEndDistance - safetyDistance,
                                                  num=matchEndTime - matchStartTime)
    x[matchEndTime:simEndTime] = np.linspace(matchEndDistance - safetyDistance, simEndDistance,
                                              num=simEndTime - matchEndTime)

    if fit:
        coefficients = np.polyfit(np.arange(simStartTime, simEndTime), x, deg=polynomialFitDeg)
        x = np.poly1d(coefficients)(np.arange(simStartTime, simEndTime, step=1))
    v = np.gradient(x)

    return x, y, v

def start_routine():
    print("Start simulation routine")
    simulationTime = np.arange(simStartTime, simEndTime)

    xL, yL, vL, lL = *get_leading_data(), 1
    xF, yF, vF, lF = *get_following_data(), 1

    if plotTrack:
        plt.plot(xL, label="leading")
        plt.plot(xF, label="following")
        plt.legend()
        plt.show()

    for t in simulationTime:
        if t == matchStartTime:
            print("Start matching")

        if t == matchEndTime:
            print("End matching")

        if t == matchStartTime + int(matchDuration / 3):
            print("Do Lane Switch 1/2")
            lL = 2

        if t == matchStartTime + int(2 * matchDuration / 3):
            print("Do Lane Switch 2/2")
            lL = 3

        latL, lonL, laneL, velL = *meters_to_latlon(simStartLat, simStartLon, xL[t], yL[t]), lL, vL[t]
        latF, lonF, laneF, velF = *meters_to_latlon(simStartLat, simStartLon, xF[t], yF[t]), lF, vF[t]

        data = generate_data(latF, lonF, laneF, velF, latL, lonL, laneL, velL)

        resL = send_data(data[0])
        resF = send_data(data[1])

        if resL or not resF:
            time.sleep(1)
            continue

        if resF['isFollowMeMode'] and lF != lL:
            print("Following Vehicle adapts to Lane")
            lF = lL

        dist = abs(xF[t]-xL[t])
        speedD = abs(velL-velF)
        isM = True if resF['isFollowMeMode'] else False
        print(f"Distance on track {dist:.1f}, Vel Lead: {velL:.1f}, Vel Foll: {velF:.1f}, Velocity diff: {speedD:.1f}, Matched: {isM}")
        time.sleep(1)




if __name__ == "__main__":
    start_routine()