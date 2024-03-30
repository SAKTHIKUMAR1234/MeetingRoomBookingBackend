package com.divum.MeetingRoomBlocker.Repository;

import com.divum.MeetingRoomBlocker.Entity.MeetingCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingCategoryEntityRepository extends JpaRepository<MeetingCategoryEntity,Long> {

    @Query(value = "SELECT c FROM MeetingCategoryEntity c where c.categoryName =:categoryName")
    MeetingCategoryEntity findMeetingCategoryEntities(@Param("categoryName") String categoryName);

    @Query(value = "SELECT  category_name FROM meeting_category_entity ", nativeQuery = true)
    List<String> findAllCategories();

    Optional<MeetingCategoryEntity> findByCategoryName(String meetingCategoryName);

}

