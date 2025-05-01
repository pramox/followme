package dse.followme.services.beachcomb.mapper;

import dse.followme.services.beachcomb.model.dtos.MotionDataDTO;
import dse.followme.services.beachcomb.model.entities.MotionData;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

public class MotionDataMapper {
    public static MotionData toEntity(MotionDataDTO dto) {
        return new MotionData(
                dto.getVin(),
                new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()),
                dto.getRole(),
                dto.getLane(),
                dto.getSpeed(),
                dto.getTimestamp()
        );
    }

    public static MotionDataDTO toDto(MotionData entity) {
        return new MotionDataDTO(
                entity.getVin(),
                entity.getPosition().getY(),
                entity.getPosition().getX(),
                entity.getRole(),
                entity.getLane(),
                entity.getSpeed(),
                entity.getTimestamp()
        );
    }
}
