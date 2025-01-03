package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.GuidanceSchedule;
import com.example.optimalschedule.model.response.ScheduleAdminProphetResponse;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import com.example.optimalschedule.model.response.ScheduleProphetDriverResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GuidanceScheduleRepository extends JpaRepository<GuidanceSchedule, Integer> {

    @Query(value = "select * from guidance_schedule where group_id = ?1 and expected_time < ?2 order by expected_time desc limit 1", nativeQuery = true)
    GuidanceSchedule findByGroupAndSort(int groupId, double expectedTime);

    @Query(value = "select * from guidance_schedule where group_id = ?1 and expected_time > ?2 and expected_time < ?3", nativeQuery = true)
    List<GuidanceSchedule> findBetweenOriginAndDes(int groupId, double expectedTimeOrigin, double expectedTimeDes);

    @Transactional
    @Modifying
    @Query(value = "update guidance_schedule set capacity_available = capacity_available - 1 where group_id = ?1 and expected_time > ?2 and expected_time < ?3", nativeQuery = true)
    void updateCapacity(int groupId, double expectedTimeOrigin, double expectedTimeDes);

    @Query(value = "update guidance_schedule set capacity_available = capacity_available - ?1 where group_id = ?2", nativeQuery = true)
    void updateCapacityByGroup(int deltaSeat, int groupId);

    @Query(value = "delete from guidance_schedule where group_id = ?1 and passenger_id = ?2", nativeQuery = true)
    void deleteOnePassenger(int groupId, int userId);

    @Query(nativeQuery = true, name = "getGuidanceScheduleByGroupIdOrderByExpectedTime")
    List<ScheduleProphetDriverResponse> getScheduleByGroupIdOrderByExpectedTime(int groupId);

    @Query(nativeQuery = true, name = "getAllGuidanceScheduleOrderByExpectedTime")
    List<ScheduleAdminProphetResponse> getAllScheduleOrderByExpectedTime();

    List<GuidanceSchedule> findByGroupIdOrderByExpectedTime(int groupId);

    List<GuidanceSchedule> findGuidanceSchedulesByGroupIdAndScheduleIdOrderByExpectedTime(int groupId, int scheduleId);

    List<GuidanceSchedule> findByGroupIdAndPassengerId(int groupId, int passengerId);

    Boolean existsByGroupIdAndPassengerId(int groupId, int passengerId);

}
