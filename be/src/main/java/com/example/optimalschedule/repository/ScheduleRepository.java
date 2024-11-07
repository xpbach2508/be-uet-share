package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Driver;
import com.example.optimalschedule.entity.Schedule;
import com.example.optimalschedule.model.response.ScheduleAdminResponse;
import com.example.optimalschedule.model.response.ScheduleDriverResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query(value = "select * from schedule where group_id = ?1 and expected_time < ?2 order by expected_time desc limit 1", nativeQuery = true)
    Schedule findByGroupAndSort(int groupId, double expectedTime);

    @Query(value = "select * from schedule where group_id = ?1 and expected_time > ?2 and expected_time < ?3", nativeQuery = true)
    List<Schedule> findBetweenOriginAndDes(int groupId, double expectedTimeOrigin, double expectedTimeDes);

    @Transactional
    @Modifying
    @Query(value = "update schedule set capacity_available = capacity_available - 1 where group_id = ?1 and expected_time > ?2 and expected_time < ?3", nativeQuery = true)
    void updateCapacity(int groupId, double expectedTimeOrigin, double expectedTimeDes);

    @Query(value = "update schedule set capacity_available = capacity_available - ?1 where group_id = ?2", nativeQuery = true)
    void updateCapacityByGroup(int deltaSeat, int groupId);

    @Query(value = "delete from schedule where group_id = ?1 and passenger_id = ?2", nativeQuery = true)
    void deleteOnePassenger(int groupId, int userId);

    @Query(nativeQuery = true, name = "getScheduleByGroupIdOrderByExpectedTime")
    List<ScheduleDriverResponse> getScheduleByGroupIdOrderByExpectedTime(int groupId);

    @Query(nativeQuery = true, name = "getAllScheduleOrderByExpectedTime")
    List<ScheduleAdminResponse> getAllScheduleOrderByExpectedTime();

    List<Schedule> findByGroupIdOrderByExpectedTime(int groupId);

    List<Schedule> findByGroupIdAndPassengerId(int groupId, int passengerId);

    Boolean existsByGroupIdAndPassengerId(int groupId, int passengerId);

    @Query(value = "select SUM(COALESCE(expected_time - LAG(expected_time) OVER (ORDER BY expected_time), 0)) AS total_difference from schedule", nativeQuery = true)
    Double calculateTotalTime();

}
