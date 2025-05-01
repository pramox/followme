import os
import time
from datetime import datetime
from urllib.parse import urljoin

import requests
from pydantic import BaseModel

api_gateway_url = os.getenv("API_GATEWAY_URL", "http://34.149.86.27")
movement_data_endpoint = "/movementdata"


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


def generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV):
    leadingVehicle = VehicleData(
        vin="3HGCM82633A004352",
        oem="Honda",
        type="Civic",
        role="LEADING_VEHICLE",
        longitude=longLV,
        latitude=latLV,
        speed=speedLV,
        lane=laneLV,
        timestamp=datetime.now()
    )
    followingVehicle = VehicleData(
        vin="4HGCM82633A004352",
        oem="VW",
        type="GOLF 4",
        role="FOLLOWING_VEHICLE",
        longitude=longFV,
        latitude=latFV,
        speed=speedFV,
        lane=laneFV,
        timestamp=datetime.now()
    )
    return leadingVehicle, followingVehicle


def send_data(data):
    try:
        url = urljoin(api_gateway_url, movement_data_endpoint)
        response = requests.post(url, json=data.model_dump(mode='json'))
        # if response.status_code == 200:
        #    #print(f"Data sent successfully: {data}")
        # else:
        # print(f"Failed to send data. Status code: {response.status_code}, Response: {response.text}")
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"An error occurred: {e}")


def main():
    latFV = 32.774064
    longFV = -115.738476
    laneFV = 2
    speedFV = 130

    latLV = 32.774064
    longLV = -115.725476
    laneLV = 1
    speedLV = 80

    print("LV approaching FV")
    while True:
        data = generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV)
        res1 = send_data(data[0])
        res2 = send_data(data[1])

        print(res2)

        if res2['isFollowMeMode']:
            speedFV = res2['requiredSpeed']
            laneFV = res2['requiredLane']
            data = generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV)
            x = send_data(data[1])
            print(x)
            break

        longLV += 0.0001
        longFV += 0.0005
        time.sleep(1)

    print("Approaching completed")

    print("Driving a bit and switching speeds and lanes")
    for i in range(50):
        if i == 3:
            laneLV = 2
        if i == 15:
            speedLV = 90

        if i == 25:
            laneLV = 3
        if i == 40:
            speedLV = 110

        data = generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV)
        res1 = send_data(data[0])
        res2 = send_data(data[1])

        print(res1)
        print(res2)

        if res2['isFollowMeMode']:
            speedFV = res2['requiredSpeed']
            laneFV = res2['requiredLane']

        longLV += 0.0005
        longFV += 0.0005
        time.sleep(1)

    print("Driving completed")

    print("Unmatching soon...")
    for i in range(30):
        if i == 5:
            speedLV = 120

        data = generate_data(latFV, longFV, laneFV, speedFV, latLV, longLV, laneLV, speedLV)
        res1 = send_data(data[0])
        res2 = send_data(data[1])

        longLV += 0.0005
        longFV += 0.0005

        # print(res1)
        print(res2)
        time.sleep(1)


if __name__ == "__main__":
    main()
