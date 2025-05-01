package dse.followme.services.inventory.exceptions;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(String message) {
        super(message);
    }

    public VehicleNotFoundException() {
    }
}
