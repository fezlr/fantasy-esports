let shopPacksCache = [];
let pendingPurchasePack = null;
let pendingPurchaseButton = null;
let shopEventsInitialized = false;

async function initShop() {
    initShopEvents();

    await loadUserFantasyPoints();
    await loadShopPacks();
}

function initShopEvents() {
    if (shopEventsInitialized) {
        return;
    }

    shopEventsInitialized = true;

    document.addEventListener('click', event => {
        const shopContainer = document.getElementById('shop-packs');

        const chancesBuyButton = event.target.closest(
            '#pack-chances-modal-buy'
        );

        if (chancesBuyButton) {
            event.preventDefault();
            event.stopPropagation();

            const packIndex = Number(
                chancesBuyButton.dataset.packIndex
            );

            if (Number.isNaN(packIndex)) {
                console.error(
                    'Pack index not found on chances buy button'
                );
                return;
            }

            const pack = shopPacksCache[packIndex];

            if (!pack) {
                console.error(
                    'Pack not found at index:',
                    packIndex
                );
                return;
            }

            if (userFantasyPoints < pack.cost) {
                return;
            }

            closePackChancesModal();

            setTimeout(() => {
                openPurchaseConfirmModal(
                    pack,
                    chancesBuyButton
                );
            }, 45);

            return;
        }

        if (
            event.target.closest(
                '#pack-chances-modal-backdrop'
            ) ||
            event.target.closest(
                '#pack-chances-modal-close'
            )
        ) {
            closePackChancesModal();
            return;
        }

        const packButton = event.target.closest(
            '.shop-pack__button'
        );

        if (
            packButton &&
            shopContainer &&
            shopContainer.contains(packButton)
        ) {
            event.preventDefault();
            event.stopPropagation();

            const packIndex = Number(
                packButton.dataset.packIndex
            );

            if (Number.isNaN(packIndex)) {
                return;
            }

            const pack = shopPacksCache[packIndex];

            if (!pack) {
                return;
            }

            if (userFantasyPoints < pack.cost) {
                return;
            }

            openPurchaseConfirmModal(
                pack,
                packButton
            );

            return;
        }

        const packCard = event.target.closest('.shop-pack');

        if (
            packCard &&
            shopContainer &&
            shopContainer.contains(packCard)
        ) {
            const packIndex = Number(
                packCard.dataset.packIndex
            );

            if (Number.isNaN(packIndex)) {
                return;
            }

            const pack = shopPacksCache[packIndex];

            if (!pack) {
                return;
            }

            openPackChancesModal(pack);

            return;
        }

        if (
            event.target.closest(
                '#shop-purchase-confirm-backdrop'
            ) ||
            event.target.closest(
                '#shop-purchase-confirm-close'
            )
        ) {
            closePurchaseConfirmModal();
            return;
        }

        if (
            event.target.closest(
                '#shop-purchase-confirm-yes'
            )
        ) {
            confirmPurchase();
        }
    });
}

async function loadShopPacks() {
    const container = document.getElementById('shop-packs');

    if (!container) {
        return;
    }

    try {
        const response = await fetch(
            '/api/v1/cs2/shop/packs',
            {
                headers: {
                    'X-Telegram-Init-Data':
                        Telegram.WebApp.initData
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                `Request failed with status ${response.status}`
            );
        }

        shopPacksCache = await response.json();

        renderShopPacks(shopPacksCache);
    } catch (err) {
        console.error(
            'Failed to load shop packs:',
            err
        );

        container.innerHTML = `
            <div class="stub">
                <span class="stub__text">
                    Failed to load shop packs
                </span>
            </div>
        `;
    }
}

function renderShopPacks(packs) {
    const container = document.getElementById(
        'shop-packs'
    );

    if (!container) {
        return;
    }

    if (!packs.length) {
        container.innerHTML = `
            <div class="stub">
                <span class="stub__text">
                    Паки недоступны
                </span>
            </div>
        `;

        return;
    }

    container.innerHTML = packs.map((pack, index) => {
        const packType =
            pack.packType.toLowerCase();

        const rarities =
            sortRarityChances(
                pack.rarityChances
            );

        const canAfford =
            userFantasyPoints >= pack.cost;

        return `
            <article
                class="shop-pack shop-pack--${packType}"
                data-pack-index="${index}"
            >
                <div class="shop-pack__visual">
                    <div class="shop-pack__icon">
                        ${pack.packType.charAt(0)}
                    </div>
                </div>

                <div class="shop-pack__info">
                    <div>
                        <div class="shop-pack__name">
                            ${escapeHtml(pack.name)}
                        </div>

                        <div class="shop-pack__description">
                            ${escapeHtml(pack.description)}
                        </div>
                    </div>

                    <span class="shop-pack__price">
                        ${pack.cost} PTS
                    </span>
                </div>

                <div class="shop-pack__rarities">
                    ${rarities.map(
                        ([rarity, chance]) => `
                            <span
                                class="
                                    shop-pack__rarity
                                    shop-pack__rarity--${rarity.toLowerCase()}
                                "
                            >
                                ${rarity} ${chance}%
                            </span>
                        `
                    ).join('')}
                </div>

                <button
                    class="shop-pack__button"
                    type="button"
                    data-pack-index="${index}"
                    data-pack-cost="${pack.cost}"
                >
                    ${
                        canAfford
                            ? 'BUY PACK'
                            : 'NOT ENOUGH POINTS'
                    }
                </button>
            </article>
        `;
    }).join('');

    updateAllBuyButtonsState();
}

function openPackChancesModal(pack) {
    const modal = document.getElementById(
        'pack-chances-modal'
    );

    if (!modal) {
        return;
    }

    const nameElement = document.getElementById(
        'pack-chances-modal-name'
    );

    const priceElement = document.getElementById(
        'pack-chances-modal-price'
    );

    const buyButton = document.getElementById(
        'pack-chances-modal-buy'
    );

    const barsContainer = document.getElementById(
        'pack-chances-modal-bars'
    );

    if (
        !nameElement ||
        !priceElement ||
        !buyButton ||
        !barsContainer
    ) {
        return;
    }

    const packIndex =
        shopPacksCache.indexOf(pack);

    if (packIndex === -1) {
        return;
    }

    nameElement.textContent = pack.name;

    priceElement.textContent =
        `${pack.cost} PTS`;

    buyButton.dataset.packIndex =
        String(packIndex);

    buyButton.dataset.packCost =
        String(pack.cost);

    const canAfford =
        userFantasyPoints >= pack.cost;

    buyButton.disabled = !canAfford;

    buyButton.textContent = canAfford
        ? 'BUY PACK'
        : 'NOT ENOUGH POINTS';

    const rarities =
        sortRarityChances(
            pack.rarityChances
        );

    barsContainer.innerHTML =
        rarities.map(
            ([rarity, chance]) => `
                <div
                    class="
                        pack-chance-bar
                        pack-chance-bar--${rarity.toLowerCase()}
                    "
                >
                    <div class="pack-chance-bar__top">
                        <span>${rarity}</span>

                        <span class="pack-chance-bar__percent">
                            ${chance}%
                        </span>
                    </div>

                    <div class="pack-chance-bar__track">
                        <div
                            class="pack-chance-bar__fill"
                            data-target-width="${chance}"
                        ></div>
                    </div>
                </div>
            `
        ).join('');

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add(
            'pack-chances-modal--open'
        );

        requestAnimationFrame(() => {
            barsContainer
                .querySelectorAll(
                    '.pack-chance-bar__fill'
                )
                .forEach(fill => {
                    fill.style.width =
                        `${fill.dataset.targetWidth}%`;
                });
        });
    });

    document.body.classList.add(
        'modal-open'
    );
}

function closePackChancesModal() {
    const modal = document.getElementById(
        'pack-chances-modal'
    );

    if (!modal) {
        return;
    }

    modal.classList.remove(
        'pack-chances-modal--open'
    );

    modal
        .querySelectorAll(
            '.pack-chance-bar__fill'
        )
        .forEach(fill => {
            fill.style.width = '0';
        });

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove(
        'modal-open'
    );
}

function openPurchaseConfirmModal(
    pack,
    button
) {
    const modal = document.getElementById(
        'shop-purchase-confirm-modal'
    );

    const textElement =
        document.getElementById(
            'shop-purchase-confirm-text'
        );

    const yesButton =
        document.getElementById(
            'shop-purchase-confirm-yes'
        );

    const closeButton =
        document.getElementById(
            'shop-purchase-confirm-close'
        );

    if (
        !modal ||
        !textElement ||
        !yesButton ||
        !closeButton
    ) {
        console.error(
            'Purchase confirmation modal elements not found'
        );

        return;
    }

    pendingPurchasePack = pack;
    pendingPurchaseButton = button;

    textElement.textContent =
        `Are you sure you want to buy ${pack.packType}?`;

    yesButton.disabled = false;
    yesButton.textContent = 'YES';

    closeButton.disabled = false;
    closeButton.textContent = 'CLOSE';

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add(
            'confirm-modal--open'
        );
    });

    document.body.classList.add(
        'modal-open'
    );
}

function closePurchaseConfirmModal() {
    const modal = document.getElementById(
        'shop-purchase-confirm-modal'
    );

    if (!modal) {
        return;
    }

    modal.classList.remove(
        'confirm-modal--open'
    );

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove(
        'modal-open'
    );

    pendingPurchasePack = null;
    pendingPurchaseButton = null;
}

function confirmPurchase() {
    if (!pendingPurchasePack) {
        return;
    }

    if (
        userFantasyPoints <
        pendingPurchasePack.cost
    ) {
        closePurchaseConfirmModal();

        updateAllBuyButtonsState();

        return;
    }

    closePurchaseConfirmModal();

    // Purchase logic will be added here.
}

function updateAllBuyButtonsState() {
    document
        .querySelectorAll(
            '.shop-pack__button'
        )
        .forEach(button => {
            const cost =
                Number(
                    button.dataset.packCost
                );

            const canAfford =
                userFantasyPoints >= cost;

            button.disabled = false;

            button.textContent =
                canAfford
                    ? 'BUY PACK'
                    : 'NOT ENOUGH POINTS';
        });

    const modalBuyButton =
        document.getElementById(
            'pack-chances-modal-buy'
        );

    if (
        modalBuyButton &&
        modalBuyButton.dataset.packCost
    ) {
        const cost =
            Number(
                modalBuyButton.dataset.packCost
            );

        const canAfford =
            userFantasyPoints >= cost;

        modalBuyButton.disabled =
            !canAfford;

        modalBuyButton.textContent =
            canAfford
                ? 'BUY PACK'
                : 'NOT ENOUGH POINTS';
    }
}