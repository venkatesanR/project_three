package com.techmania.spboot.reporsitory;

import com.techmania.spboot.beans.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventRepository extends PagingAndSortingRepository<Repository, Integer> {
}