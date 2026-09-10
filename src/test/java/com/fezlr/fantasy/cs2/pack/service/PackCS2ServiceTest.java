package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.common.exception.AccessDeniedException;
import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.pack.enums.PackType;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.packshop.service.PackShopCS2Service;
import com.fezlr.fantasy.user.entity.UserCS2Entity;
import com.fezlr.fantasy.user.entity.UserEntity;
import com.fezlr.fantasy.user.service.UserCS2Service;
import com.fezlr.fantasy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(value = MockitoExtension.class)
public class PackCS2ServiceTest {
    @Mock
    private PackShopCS2Service packShopCS2Service;

    @Mock
    private PackCS2Repository packCS2Repository;

    @Mock
    private PackCS2Mapper packCS2Mapper;

    @Mock
    private UserService userService;

    @Mock
    private UserCS2Service userCS2Service;

    @InjectMocks
    private PackCS2Service packCS2Service;

    private UserEntity currentUser;
    private UserCS2Entity userCS2Entity;
    private PackShopCS2Entity shopPack;

    @BeforeEach
    void setUp() {
        userCS2Entity = UserCS2Entity.builder()
                .fantasyPoints(100L)
                .packsCS2(new ArrayList<>())
                .build();

        currentUser = UserEntity.builder()
                .id(1L)
                .isEnabled(true)
                .userCS2Entity(userCS2Entity)
                .build();

        shopPack = PackShopCS2Entity.builder()
                .id(10L)
                .cost(50)
                .isAvailable(true)
                .packType(PackType.COMMON)
                .name("Common Pack")
                .build();
    }

    @Test
    void shouldReturnPurchaseResponse_whenPurchaseIsSuccessful() {
        when(packShopCS2Service.findById(10L)).thenReturn(shopPack);
        when(userCS2Service.subtractFantasyPoints(1L, 50L)).thenReturn(1);

        PackCS2Entity savedPack = PackCS2Entity.builder()
                .id(99L)
                .userCS2(userCS2Entity)
                .packShopCS2(shopPack)
                .receivedAt(Instant.now())
                .build();

        when(packCS2Repository.save(any(PackCS2Entity.class))).thenReturn(savedPack);

        PackPurchaseCS2Response expectedResponse = PackPurchaseCS2Response.builder()
                .packId(99L)
                .userFantasyPoints(50L)
                .build();

        when(packCS2Mapper.toPurchaseResponse(savedPack, 50L)).thenReturn(expectedResponse);

        PackPurchaseCS2Response result = packCS2Service.purchasePackByUser(currentUser, 10L);

        assertThat(result).isEqualTo(expectedResponse);
        assertThat(userCS2Entity.getPacksCS2().size() == 1);

        ArgumentCaptor<PackCS2Entity> packCaptor = ArgumentCaptor.forClass(PackCS2Entity.class);
        verify(packCS2Repository).save(packCaptor.capture());

        PackCS2Entity savedArg = packCaptor.getValue();
        assertThat(savedArg.getUserCS2()).isEqualTo(userCS2Entity);
        assertThat(savedArg.getPackShopCS2()).isEqualTo(shopPack);

        verify(userCS2Service).subtractFantasyPoints(1L, 50L);
        verify(packCS2Mapper).toPurchaseResponse(savedPack, 50L);
        verifyNoInteractions(userService);
    }

    @Test
    void shouldThrowAccessDeniedException_whenUserIsNotEnabled() {
        currentUser = UserEntity.builder()
                .id(1L)
                .isEnabled(false)
                .userCS2Entity(userCS2Entity)
                .build();

        assertThrows(AccessDeniedException.class,
                () -> packCS2Service.purchasePackByUser(currentUser, 10L));

        verifyNoInteractions(packShopCS2Service, userCS2Service, packCS2Repository, packCS2Mapper);
    }

    @Test
    void shouldThrowEntityNotFoundException_whenShopPackDoesNotExist() {
        when(packShopCS2Service.findById(10L))
                .thenThrow(new EntityNotFoundException("Shop pack is not found"));

        assertThrows(EntityNotFoundException.class,
                () -> packCS2Service.purchasePackByUser(currentUser, 10L));

        verifyNoInteractions(userCS2Service, packCS2Repository, packCS2Mapper);
    }

    @Test
    void shouldThrowPackShopIsNotAvailableException_whenShopPackIsNotAvailable() {
        shopPack = PackShopCS2Entity.builder()
                .id(10L)
                .cost(50)
                .isAvailable(false)
                .build();

        when(packShopCS2Service.findById(10L)).thenReturn(shopPack);

        assertThrows(PackShopIsNotAvailableException.class,
                () -> packCS2Service.purchasePackByUser(currentUser, 10L));

        verifyNoInteractions(userCS2Service, packCS2Repository, packCS2Mapper);
    }

    @Test
    void shouldThrowNotEnoughFantasyPointsException_whenCostExceedsBalance() {
        shopPack = PackShopCS2Entity.builder()
                .id(10L)
                .cost(500)
                .isAvailable(true)
                .build();

        when(packShopCS2Service.findById(10L)).thenReturn(shopPack);

        assertThrows(NotEnoughFantasyPointsException.class,
                () -> packCS2Service.purchasePackByUser(currentUser, 10L));

        verifyNoInteractions(userCS2Service, packCS2Repository, packCS2Mapper);
    }

    @Test
    void shouldThrowNotEnoughFantasyPointsException_whenConcurrentPurchaseDrainsBalance() {
        when(packShopCS2Service.findById(10L)).thenReturn(shopPack);
        when(userCS2Service.subtractFantasyPoints(1L, 50L)).thenReturn(0);

        assertThrows(NotEnoughFantasyPointsException.class,
                () -> packCS2Service.purchasePackByUser(currentUser, 10L));

        verifyNoInteractions(packCS2Repository, packCS2Mapper);
        assertThat(userCS2Entity.getPacksCS2().isEmpty());
    }
}
