let shopPacksCache = [];
let pendingPurchasePack = null;
let pendingPurchaseButton = null;

async function initShop() {
    initShopModalHandlers();
    initPurchaseConfirmModalHandlers();
    await loadUserFantasyPoints();
    await loadShopPacks();
}

async function loadShopPacks() {
    const container = document.getElementById('shop-packs');
    if (!container) return;

    try {
        const response = await fetch('/api/v1/cs2/shop/packs', {
            headers: {
                'X-Telegram-Init-Data': Telegram.WebApp.initData
            }
        });

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        shopPacksCache = await response.json();

        renderShopPacks(shopPacksCache);
    } catch (err) {
        console.error('Failed to load shop packs:', err);
        container.innerHTML = '<div class="stub"><span class="stub__text">Failed to load shop packs</span></div>';
    }
}

function renderShopPacks(packs) {
    const container = document.getElementById('shop-packs');
    if (!container) return;

    if (!packs.length) {
        container.innerHTML = '<div class="stub"><span class="stub__text">Паки недоступны</span></div>';
        return;
    }

    container.innerHTML = packs.map((pack, index) => {
        const packType = pack.packType.toLowerCase();
        const rarities = sortRarityChances(pack.rarityChances);
        const canAfford = userFantasyPoints >= pack.cost;

        return `
            <article
                class="shop-pack shop-pack--${packType}"
                data-pack-index="${index}"
            >
                <div class="shop-pack__visual">
                    <div class="shop-pack__icon">${pack.packType.charAt(0)}</div>
                </div>

                <div class="shop-pack__info">
                    <div>
                        <div class="shop-pack__name">${escapeHtml(pack.name)}</div>
                        <div class="shop-pack__description">
                            ${escapeHtml(pack.description)}
                        </div>
                    </div>

                    <span class="shop-pack__price">${pack.cost} PTS</span>
                </div>

                <div class="shop-pack__rarities">
                    ${rarities.map(([rarity, chance]) => `
                        <span class="shop-pack__rarity shop-pack__rarity--${rarity.toLowerCase()}">
                            ${rarity} ${chance}%
                        </span>
                    `).join('')}
                </div>

                <button
                    class="shop-pack__button"
                    type="button"
                    data-pack-id="${pack.id}"
                    data-pack-cost="${pack.cost}"
                    ${canAfford ? '' : 'disabled'}
                >
                    ${canAfford ? 'BUY PACK' : 'NOT ENOUGH POINTS'}
                </button>
            </article>
        `;
    }).join('');

    container.querySelectorAll('.shop-pack').forEach(card => {
        card.addEventListener('click', event => {
            if (event.target.closest('.shop-pack__button')) {
                return;
            }

            const pack = shopPacksCache[Number(card.dataset.packIndex)];

            if (pack) {
                openPackChancesModal(pack);
            }
        });
    });

    container.querySelectorAll('.shop-pack__button').forEach(button => {
        button.addEventListener('click', () => {
            const packId = Number(button.dataset.packId);
            const pack = shopPacksCache.find(p => p.id === packId);

            if (pack) {
                openPurchaseConfirmModal(pack, button);
            }
        });
    });
}

function openPackChancesModal(pack) {
    document.getElementById('pack-chances-modal-name').textContent = pack.name;
    document.getElementById('pack-chances-modal-price').textContent = `${pack.cost} PTS`;

    const buyButton = document.getElementById('pack-chances-modal-buy');
    buyButton.dataset.packId = pack.id;
    buyButton.dataset.packCost = pack.cost;

    const canAfford = userFantasyPoints >= pack.cost;
    buyButton.disabled = !canAfford;
    buyButton.textContent = canAfford ? 'BUY PACK' : 'NOT ENOUGH POINTS';

    const barsContainer = document.getElementById('pack-chances-modal-bars');
    const rarities = sortRarityChances(pack.rarityChances);

    barsContainer.innerHTML = rarities.map(([rarity, chance]) => `
        <div class="pack-chance-bar pack-chance-bar--${rarity.toLowerCase()}">
            <div class="pack-chance-bar__top">
                <span>${rarity}</span>
                <span class="pack-chance-bar__percent">${chance}%</span>
            </div>
            <div class="pack-chance-bar__track">
                <div class="pack-chance-bar__fill" data-target-width="${chance}"></div>
            </div>
        </div>
    `).join('');

    const modal = document.getElementById('pack-chances-modal');

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add('pack-chances-modal--open');

        requestAnimationFrame(() => {
            barsContainer.querySelectorAll('.pack-chance-bar__fill').forEach(fill => {
                fill.style.width = `${fill.dataset.targetWidth}%`;
            });
        });
    });

    document.body.classList.add('modal-open');
}

function closePackChancesModal() {
    const modal = document.getElementById('pack-chances-modal');

    modal.classList.remove('pack-chances-modal--open');

    document.querySelectorAll('.pack-chance-bar__fill').forEach(fill => {
        fill.style.width = '0';
    });

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove('modal-open');
}

function openPurchaseConfirmModal(pack, button) {
    pendingPurchasePack = pack;
    pendingPurchaseButton = button;

    document.getElementById('shop-purchase-confirm-text').textContent =
        `Are you sure you want to buy ${pack.name} for ${pack.cost} PTS?`;

    const modal = document.getElementById('shop-purchase-confirm-modal');

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add('confirm-modal--open');
    });

    document.body.classList.add('modal-open');
}

function closePurchaseConfirmModal() {
    const modal = document.getElementById('shop-purchase-confirm-modal');

    modal.classList.remove('confirm-modal--open');

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove('modal-open');

    pendingPurchasePack = null;
    pendingPurchaseButton = null;
}

function confirmPurchase() {
    if (!pendingPurchasePack) {
        return;
    }

    const pack = pendingPurchasePack;
    const button = pendingPurchaseButton;

    closePurchaseConfirmModal();
    purchasePack(pack, button);
}

async function purchasePack(pack, button) {
    if (userFantasyPoints < pack.cost) {
        return;
    }

    button.disabled = true;
    const originalText = button.textContent;
    button.textContent = 'PURCHASING...';

    try {
        const response = await fetch(`/api/v1/cs2/packs/purchase/${pack.id}`, {
            method: 'POST',
            headers: {
                'X-Telegram-Init-Data': Telegram.WebApp.initData
            }
        });

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const result = await response.json();
        setUserFantasyPoints(result.userFantasyPoints);

        updateAllBuyButtonsState();
        closePackChancesModal();
    } catch (err) {
        console.error('Failed to purchase pack:', err);
        button.disabled = userFantasyPoints < pack.cost;
        button.textContent = originalText;
    }
}

function updateAllBuyButtonsState() {
    document.querySelectorAll('.shop-pack__button').forEach(button => {
        const cost = Number(button.dataset.packCost);
        const canAfford = userFantasyPoints >= cost;

        button.disabled = !canAfford;
        button.textContent = canAfford ? 'BUY PACK' : 'NOT ENOUGH POINTS';
    });

    const modalBuyButton = document.getElementById('pack-chances-modal-buy');

    if (modalBuyButton && modalBuyButton.dataset.packCost) {
        const cost = Number(modalBuyButton.dataset.packCost);
        const canAfford = userFantasyPoints >= cost;

        modalBuyButton.disabled = !canAfford;
        modalBuyButton.textContent = canAfford ? 'BUY PACK' : 'NOT ENOUGH POINTS';
    }
}

function initShopModalHandlers() {
    document
        .getElementById('pack-chances-modal-backdrop')
        ?.addEventListener('click', closePackChancesModal);

    document
        .getElementById('pack-chances-modal-close')
        ?.addEventListener('click', closePackChancesModal);

    document
        .getElementById('pack-chances-modal-buy')
        ?.addEventListener('click', () => {
            const button = document.getElementById('pack-chances-modal-buy');
            const packId = Number(button.dataset.packId);
            const pack = shopPacksCache.find(p => p.id === packId);

            if (pack) {
                openPurchaseConfirmModal(pack, button);
            }
        });
}

function initPurchaseConfirmModalHandlers() {
    document
        .getElementById('shop-purchase-confirm-backdrop')
        ?.addEventListener('click', closePurchaseConfirmModal);

    document
        .getElementById('shop-purchase-confirm-close')
        ?.addEventListener('click', closePurchaseConfirmModal);

    document
        .getElementById('shop-purchase-confirm-yes')
        ?.addEventListener('click', confirmPurchase);
}