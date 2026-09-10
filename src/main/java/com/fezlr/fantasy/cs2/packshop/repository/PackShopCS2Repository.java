package com.fezlr.fantasy.cs2.packshop.repository;

import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface PackShopCS2Repository extends JpaRepository<PackShopCS2Entity, Long> {

    @Query("""
            SELECT p FROM PackShopCS2Entity p
            JOIN FETCH p.dropChances
            WHERE p.isAvailable = true
            """)
    List<PackShopCS2Entity> findAllAvailable();
}
