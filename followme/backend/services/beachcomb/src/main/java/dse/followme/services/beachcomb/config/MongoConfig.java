package dse.followme.services.beachcomb.config;

import dse.followme.services.beachcomb.model.entities.MotionData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class MongoConfig {

    @Autowired
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {

        mongoTemplate.indexOps(MotionData.class).ensureIndex(new GeospatialIndex("position.coordinates"));
    }
}
