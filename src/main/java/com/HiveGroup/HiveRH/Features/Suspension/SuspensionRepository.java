package com.HiveGroup.HiveRH.Features.Suspension;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SuspensionRepository extends JpaRepository<@NonNull SuspensionEntity, @NonNull Long> {

    List<SuspensionEntity> findByEndDateLessThanEqual(LocalDate date);
}
