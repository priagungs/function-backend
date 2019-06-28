package com.future.function.repository.feature.scoring;

import com.future.function.model.entity.feature.scoring.Room;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {

  Page<Room> findAllByAssignmentIdAndDeletedFalse(String assignmentId, Pageable page);

  List<Room> findAllByAssignmentIdAndDeletedFalse(String assignmentId);

  Optional<Room> findByIdAndDeletedFalse(String id);
}
