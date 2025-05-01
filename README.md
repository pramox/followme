# FollowMe - Autonomous Driving Simulation

## Overview

**FollowMe** is an application that simulates autonomous driving behavior, where cars track other vehicles, copy their speed, and follow the same lane. The system uses a microservice architecture with multiple services communicating through an API gateway and a message broker. The application is deployed on **Google Cloud** using **Kubernetes** for orchestration. The project includes a frontend visualization of the vehicles' positions and movements using the **Google Maps** library.

## Purpose

The general purpose of **FollowMe** is to simulate and test autonomous driving systems where cars can communicate with each other and autonomously follow one another. The system's architecture and components are designed to reflect real-world scenarios in autonomous driving, such as vehicle-to-vehicle communication, speed synchronization, and lane-following.

## Key Components

### Microservice Architecture
- **API Gateway**: Serves as a central entry point for all requests to the system.
- **Message Broker**: Facilitates communication between services, especially for real-time car-to-car data exchange (speed, lane position, etc.).

### Backend Services
- Services that handle data processing, vehicle tracking, and communication between cars.

### Frontend Visualization
- A **Google Maps** integration displays vehicle positions, lanes, and movement in real-time.
- The frontend also simulates user interaction with the vehicles, providing a graphical representation of the autonomous driving behavior.

### Mock Client
- A mock client was developed to simulate car movements and test the communication between the services.

### Deployment
- The application is deployed using **Kubernetes** on **Google Cloud** to scale the services dynamically.
  
### How to run locally: 
- Execute docker-compose file starts all microservices and frontend.  
