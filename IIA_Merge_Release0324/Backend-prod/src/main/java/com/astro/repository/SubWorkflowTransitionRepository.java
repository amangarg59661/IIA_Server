package com.astro.repository;

import com.astro.entity.SubWorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubWorkflowTransitionRepository extends JpaRepository<SubWorkflowTransition, Integer> {
    List<SubWorkflowTransition> findByWorkflowTransitionIdAndStatus(Integer workflowTransitionId, String pendingType);

    List<SubWorkflowTransition> findByActionOn(String updatedBy);

    List<SubWorkflowTransition> findByWorkflowTransitionIdAndRequestIdAndTransitionTypeAndTransitionName(Integer workflowTransitionId, String requestId, String transitionType, String transitionName);
}
