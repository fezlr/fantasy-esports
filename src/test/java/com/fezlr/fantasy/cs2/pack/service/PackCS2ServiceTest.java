package com.fezlr.fantasy.cs2.pack.service;

import com.fezlr.fantasy.common.exception.AccessDeniedException;
import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.cs2.card.entity.CardCS2Entity;
import com.fezlr.fantasy.cs2.card.enums.Rarity;
import com.fezlr.fantasy.cs2.card.service.CardCS2Service;
import com.fezlr.fantasy.cs2.pack.dto.response.InventoryPackCardCS2Response;
import com.fezlr.fantasy.cs2.pack.dto.response.PackPurchaseCS2Response;
import com.fezlr.fantasy.cs2.pack.entity.PackCS2Entity;
import com.fezlr.fantasy.cs2.pack.enums.PackType;
import com.fezlr.fantasy.cs2.pack.mapper.PackCS2Mapper;
import com.fezlr.fantasy.cs2.pack.repository.PackCS2Repository;
import com.fezlr.fantasy.cs2.packshop.entity.PackDropCS2ChanceEntity;
import com.fezlr.fantasy.cs2.packshop.entity.PackShopCS2Entity;
import com.fezlr.fantasy.cs2.packshop.service.PackShopCS2Service;
import com.fezlr.fantasy.cs2.player.entity.PlayerCS2Entity;
import com.fezlr.fantasy.cs2.player.service.PlayerCS2Service;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    private PackRarityRollerService packRarityRollerService;

    @Mock
    private CardCS2Service cardCS2Service;

    @Mock
    private PlayerCS2Service playerCS2Service;

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

    @Test
    void openPackByUserThrows_whenUserIsNotEnabled() {
        UserEntity currentUser = userNotEnabled();

        assertThrows(AccessDeniedException.class,
                () -> packCS2Service.openPackByUser(currentUser, 1L));

        verifyNoInteractions(packCS2Repository, cardCS2Service, playerCS2Service);
    }

    @Test
    void openPackByUserThrows_whenPackNotFound() {
        UserEntity currentUser = enabledUser(userCS2WithId(1L));

        when(packCS2Repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> packCS2Service.openPackByUser(currentUser, 99L));

        verifyNoInteractions(cardCS2Service, playerCS2Service);
    }

    @Test
    void openPackByUserThrows_whenUserDoesNotOwnPack() {
        UserCS2Entity ownerUserCS2 = userCS2WithId(1L);
        UserCS2Entity otherUserCS2 = userCS2WithId(2L);

        UserEntity currentUser = enabledUser(otherUserCS2);
        PackCS2Entity pack = packOwnedBy(ownerUserCS2);

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));

        assertThrows(IllegalArgumentException.class,
                () -> packCS2Service.openPackByUser(currentUser, 10L));

        verifyNoInteractions(cardCS2Service, playerCS2Service);
    }

    @Test
    void openPackByUserThrows_whenPackAlreadyOpened() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);

        PackCS2Entity pack = packOwnedBy(userCS2);
        pack.setOpened(true);

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));

        assertThrows(IllegalStateException.class,
                () -> packCS2Service.openPackByUser(currentUser, 10L));

        verifyNoInteractions(cardCS2Service, playerCS2Service);
    }

    @Test
    void openPackByUserThrows_whenNoActivePlayersAvailable() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);
        PackCS2Entity pack = packOwnedBy(userCS2);

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));
        when(packRarityRollerService.roll(pack.getPackShopCS2().getDropChances())).thenReturn(Rarity.EPIC);
        when(playerCS2Service.findRandomActivePlayer()).thenThrow(new EntityNotFoundException("No active player is available"));

        assertThrows(EntityNotFoundException.class,
                () -> packCS2Service.openPackByUser(currentUser, 10L));

        verifyNoInteractions(cardCS2Service);
    }

    @Test
    void openPackByUser_whenSavesCardWithRolledRarity() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);
        PackCS2Entity pack = packOwnedBy(userCS2);
        PlayerCS2Entity player = activePlayer();

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));
        when(packRarityRollerService.roll(pack.getPackShopCS2().getDropChances())).thenReturn(Rarity.LEGENDARY);
        when(playerCS2Service.findRandomActivePlayer()).thenReturn(player);
        when(packCS2Mapper.toOpenResultResponse(any(), any()))
                .thenReturn(new InventoryPackCardCS2Response(Rarity.LEGENDARY, null));

        packCS2Service.openPackByUser(currentUser, 10L);

        ArgumentCaptor<CardCS2Entity> captor = ArgumentCaptor.forClass(CardCS2Entity.class);
        verify(cardCS2Service).save(captor.capture());

        CardCS2Entity savedCard = captor.getValue();
        assertEquals(Rarity.LEGENDARY, savedCard.getRarity());
        assertEquals(player, savedCard.getPlayerCS2());
        assertEquals(userCS2, savedCard.getUserCS2());
        assertNotNull(savedCard.getReceivedAt());
    }

    @Test
    void openPackByUser_whenMarksPackAsOpened() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);
        PackCS2Entity pack = packOwnedBy(userCS2);
        PlayerCS2Entity player = activePlayer();

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));
        when(packRarityRollerService.roll(pack.getPackShopCS2().getDropChances())).thenReturn(Rarity.RARE);
        when(playerCS2Service.findRandomActivePlayer()).thenReturn(player);
        when(packCS2Mapper.toOpenResultResponse(any(), any()))
                .thenReturn(new InventoryPackCardCS2Response(Rarity.RARE, null));

        packCS2Service.openPackByUser(currentUser, 10L);

        assertTrue(pack.isOpened());
        assertNotNull(pack.getOpenedAt());
        verify(packCS2Repository).save(pack);
    }

    @Test
    void openPackByUser_whenReturnsMappedResponse() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);
        PackCS2Entity pack = packOwnedBy(userCS2);
        PlayerCS2Entity player = activePlayer();

        InventoryPackCardCS2Response expectedResponse =
                new InventoryPackCardCS2Response(Rarity.MYTHICAL, null);

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));
        when(packRarityRollerService.roll(pack.getPackShopCS2().getDropChances())).thenReturn(Rarity.MYTHICAL);
        when(playerCS2Service.findRandomActivePlayer()).thenReturn(player);
        when(packCS2Mapper.toOpenResultResponse(Rarity.MYTHICAL, player)).thenReturn(expectedResponse);

        InventoryPackCardCS2Response actualResponse = packCS2Service.openPackByUser(currentUser, 10L);

        assertSame(expectedResponse, actualResponse);
    }

    @Test
    void openPackByUser_whenRollsUsingPackSpecificDropChances() {
        UserCS2Entity userCS2 = userCS2WithId(1L);
        UserEntity currentUser = enabledUser(userCS2);
        PackCS2Entity pack = packOwnedBy(userCS2);
        PlayerCS2Entity player = activePlayer();

        when(packCS2Repository.findById(10L)).thenReturn(Optional.of(pack));
        when(packRarityRollerService.roll(any())).thenReturn(Rarity.COMMON);
        when(playerCS2Service.findRandomActivePlayer()).thenReturn(player);
        when(packCS2Mapper.toOpenResultResponse(any(), any()))
                .thenReturn(new InventoryPackCardCS2Response(Rarity.COMMON, null));

        packCS2Service.openPackByUser(currentUser, 10L);

        verify(packRarityRollerService).roll(pack.getPackShopCS2().getDropChances());
    }

    private UserEntity userNotEnabled() {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEnabled(false);

        return user;
    }

    private UserEntity enabledUser(UserCS2Entity userCS2) {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setEnabled(true);
        user.setUserCS2Entity(userCS2);

        return user;
    }

    private UserCS2Entity userCS2WithId(Long userId) {
        UserCS2Entity userCS2 = new UserCS2Entity();

        userCS2.setId(userId);

        return userCS2;
    }

    private PackCS2Entity packOwnedBy(UserCS2Entity userCS2) {
        PackCS2Entity pack = new PackCS2Entity();

        pack.setId(10L);
        pack.setUserCS2(userCS2);
        pack.setOpened(false);
        pack.setPackShopCS2(packShopWithDropChances());

        return pack;
    }

    private PackShopCS2Entity packShopWithDropChances() {
        PackShopCS2Entity packShop = new PackShopCS2Entity();

        packShop.setDropChances(List.of(
                chance(Rarity.COMMON, "65.00"),
                chance(Rarity.RARE, "35.00")
        ));

        return packShop;
    }

    private PackDropCS2ChanceEntity chance(Rarity rarity, String chanceValue) {
        PackDropCS2ChanceEntity entity = new PackDropCS2ChanceEntity();

        entity.setRarity(rarity);
        entity.setChance(new BigDecimal(chanceValue));

        return entity;
    }

    private PlayerCS2Entity activePlayer() {
        PlayerCS2Entity player = new PlayerCS2Entity();

        player.setId(5L);
        player.setNickname("s1mple");
        player.setActive(true);

        return player;
    }

}
