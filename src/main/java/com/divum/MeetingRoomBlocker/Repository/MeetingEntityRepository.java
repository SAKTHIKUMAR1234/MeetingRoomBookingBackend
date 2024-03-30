package com.divum.MeetingRoomBlocker.Repository;

import com.divum.MeetingRoomBlocker.Entity.Enum.MeetingStatusEntity;
import com.divum.MeetingRoomBlocker.Entity.MeetingEntity;
import com.divum.MeetingRoomBlocker.Entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface MeetingEntityRepository extends JpaRepository<MeetingEntity,Long> {

    List<MeetingEntity> findByHostIdAndStartTimeAfterOrderByStartTime(Long hostId, Timestamp startTime);

    List<MeetingEntity> findByGuestListAndStartTimeAfterOrderByStartTime(UserEntity user, Timestamp startTime);

    List<MeetingEntity> findByStartTimeAfterOrderByStartTime(Timestamp Date);

    List<MeetingEntity> findByIdIn(List<Long> ids);

    @Query("SELECT m.guestList FROM MeetingEntity m WHERE m.id = :meetingId")
    List<UserEntity> findGuestListByMeetingId(@Param("meetingId") Long meetingId);

    @Query("SELECT m FROM MeetingEntity m " +
            "WHERE m.startTime <= :endDate AND m.endTime >= :startDate")
    List<MeetingEntity> findByStartDateAndEndDate(
            Timestamp startDate,
            Timestamp endDate);

    @Query("SELECT m FROM MeetingEntity m " +
            "WHERE m.roomEntity.id = :roomId " +
            "AND DATE(m.startTime) = :Date")
    List<MeetingEntity> findByRoomEntityIdAndStartTimeBetween(Long roomId, LocalDate Date);

    @Query("SELECT m FROM MeetingEntity m " +
            "WHERE m.roomEntity.id = :roomId " +
            "AND m.host.id = :hostId "+
            "AND DATE(m.startTime) = :Date ")
    List<MeetingEntity> findByRoomEntityIdAndHostIdAndStartTimeBetween(Long roomId, Long hostId, LocalDateTime Date);


    @Query("SELECT DISTINCT  me FROM MeetingEntity me LEFT JOIN  FETCH me.guestList gl" +
            " WHERE DATE(me.startTime) =  :date AND  me.isDeleted=false AND (me.status = :status or me.status = :statusCompleted) AND (me.host.id = :userId OR gl.id = :userId)")

    List<MeetingEntity> findMeetingsByUserEmailAndDate(@Param("date") Date date,
                                                       @Param("status") MeetingStatusEntity status,
                                                       @Param("statusCompleted") MeetingStatusEntity statusCompleted,
                                                       @Param("userId") Long userId);

    @Query("SELECT m FROM MeetingEntity m " +
            "WHERE EXTRACT(YEAR FROM m.startTime) = :year ")
    List<MeetingEntity> findByYear( int year);

    @Query("SELECT DISTINCT me " +
            "FROM MeetingEntity me " +
            "LEFT JOIN FETCH me.guestList gl " +
            "WHERE (me.host.id = :userId OR gl.id = :userId) " +
            "AND me.isDeleted=false "+
            "AND EXTRACT(YEAR FROM me.startTime) = :year ")
    List<MeetingEntity> findByYearByUser(Long userId,int year);

    @Query("SELECT DISTINCT me FROM MeetingEntity me LEFT JOIN FETCH  me.guestList gl " +
        "WHERE(((me.status= :status1)AND" +
        " (me.host.id =:userId))OR ((me.endTime <:date AND  me.isDeleted=false AND me.status =:status " +
        "AND (me.host.id = :userId OR gl.id =:userId))))" +
        "order by me.endTime DESC ")
    List<MeetingEntity> findByHostIdAndDateAndCompletedMeeting(@Param("date") LocalDateTime date,
                                                               @Param("status") MeetingStatusEntity status,
                                                               @Param("status1")MeetingStatusEntity status1,
                                                               @Param("userId") Long userId
                                                                );
    @Query("SELECT  DISTINCT me FROM  MeetingEntity me LEFT JOIN FETCH  me.guestList gl" +
        " WHERE (me.endTime >=:date AND me.isDeleted=false AND me.status =:status  " +
        "AND (me.host.id = :userId OR gl.id =: userId)) ORDER BY me.startTime ASC ")
    List<MeetingEntity> findByHostIdAndDateAndUpcomingMeeting(@Param("date") LocalDateTime date,
                                                              @Param("status") MeetingStatusEntity status,
                                                              @Param("userId") Long userId);

    @Query("SELECT DISTINCT  me FROM MeetingEntity me WHERE (me.startTime >= :date AND me.status = :status " +
        "AND me.isDeleted=false" +
        " AND me.host.id = :userId) ORDER BY me.startTime ASC ")
    List<MeetingEntity> findByHostIdAndPendingMeeting(@Param("date") LocalDateTime date,
                                                               @Param("status") MeetingStatusEntity status,
                                                               @Param("userId") Long userId);

    @Query("SELECT m FROM MeetingEntity m WHERE m.endTime BETWEEN ?1 AND ?2")
    List<MeetingEntity> findMeetingsEndingWithinHalfHour(Timestamp startTime, Timestamp endTime);

    @Query("SELECT me FROM MeetingEntity me " +
            "WHERE me.startTime < :endTime AND me.endTime > :startTime AND me.status= :status AND me.roomEntity.id= :roomId")
    List<MeetingEntity> findOverlappingMeetings(Timestamp startTime, Timestamp endTime, MeetingStatusEntity status, Long roomId);


}