package com.example.optimalschedule.repository;

import com.example.optimalschedule.entity.Edge;
import com.example.optimalschedule.model.QueryEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EdgeRepository extends JpaRepository<Edge, Integer> {

    @Query("select new com.example.optimalschedule.model.QueryEdge(e.distance, e.duration) " +
            "from Edge as e " +
            "where e.id = ?1")
    QueryEdge findQueryEdgeById(int edgeId);
}
