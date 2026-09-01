let inventoryCards = [];
let inventoryPacks = [];
let inventoryPackGroups = [];
let pendingOpenPackId = null;

async function initInventory() {
    initInventoryTabs();
    initInventoryModalHandlers();
    initInventoryPackModalHandlers();
    initConfirmModalHandlers();

    await loadInventory();
}

async function loadInventory() {
    await loadInventoryCards();
    await loadInventoryPacks();
}

async function loadInventoryCards() {
    try {
        inventoryCards = await fetchInventoryCards();
        renderInventoryCards();
    } catch (error) {
        inventoryCards = [];
        renderInventoryCardsError();
    }
}

async function fetchInventoryCards() {
    const response = await fetch('/api/v1/cs2/cards', {
        headers: {
            'X-Telegram-Init-Data': Telegram.WebApp.initData
        }
    });

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
    }

    return response.json();
}

async function loadInventoryPacks() {
    try {
        inventoryPacks = await fetchInventoryPacks();
        renderInventoryPacks();
    } catch (error) {
        console.error('Failed to load inventory packs:', error);
        inventoryPacks = [];
        renderInventoryPacksError();
    }
}

async function fetchInventoryPacks() {
    const response = await fetch('/api/v1/cs2/packs', {
        headers: {
            'X-Telegram-Init-Data': Telegram.WebApp.initData
        }
    });

    if (!response.ok) {
        throw new Error(`Request failed with status ${response.status}`);
    }

    return response.json();
}

function initInventoryTabs() {
    document.querySelectorAll('[data-inventory-tab]').forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.dataset.inventoryTab;

            document.querySelectorAll('[data-inventory-tab]')
                .forEach(btn => {
                    btn.classList.remove('inventory-tabs__btn--active');
                });

            button.classList.add('inventory-tabs__btn--active');

            document.getElementById('inventory-cards').hidden =
                tab !== 'cards';

            document.getElementById('inventory-packs').hidden =
                tab !== 'packs';
        });
    });
}

function renderInventoryCards() {
    const container = document.getElementById('inventory-card-grid');

    if (!inventoryCards.length) {
        container.innerHTML = `
            <div class="inventory-empty">
                Your card collection is empty
            </div>
        `;
        return;
    }

    container.innerHTML = inventoryCards.map((card, index) => {
        const player = card.player;
        const rarity = normalizeRarity(card.rarity);

        return `
            <article
                class="inventory-card"
                data-card-index="${index}"
            >
                <div class="inventory-card__visual inventory-card__visual--${rarity}">
                    <img
                        class="inventory-card__image"
                        src="${player.imageUrl || placeholderImg(300)}"
                        alt=""
                    >

                    <img
                        class="inventory-card__team-logo"
                        src="${player.teamLogoUrl || placeholderImg(40)}"
                        alt=""
                    >

                    <span class="inventory-card__rarity inventory-card__rarity--${rarity}">
                        ${rarity.toUpperCase()}
                    </span>
                </div>

                <div class="inventory-card__info">
                    <span class="inventory-card__nickname">
                        ${escapeHtml(player.nickname)}
                    </span>

                    <span class="inventory-card__team">
                        ${escapeHtml(player.teamName || 'Unknown team')}
                    </span>

                    <span class="inventory-card__points">
                        ${player.playerStatistic?.rating ?? '—'} PTS
                    </span>
                </div>
            </article>
        `;
    }).join('');

    container.querySelectorAll('.inventory-card').forEach(card => {
        card.addEventListener('click', () => {
            const index = Number(card.dataset.cardIndex);
            const inventoryCard = inventoryCards[index];

            if (inventoryCard) {
                openPlayerCardModal(inventoryCard);
            }
        });
    });
}

function renderInventoryCardsError() {
    const container = document.getElementById('inventory-card-grid');

    container.innerHTML = `
        <div class="inventory-empty">
            Failed to load your cards
        </div>
    `;
}

function groupInventoryPacksByType(packs) {
    const groups = new Map();

    packs.forEach(pack => {
        const key = pack.packType;

        if (!groups.has(key)) {
            groups.set(key, {
                packType: pack.packType,
                name: pack.name,
                rarityChances: pack.rarityChances,
                items: []
            });
        }

        groups.get(key).items.push(pack);
    });

    return Array.from(groups.values());
}

function renderInventoryPacks() {
    const container = document.getElementById('inventory-pack-grid');

    if (!inventoryPacks.length) {
        container.innerHTML = `
            <div class="inventory-empty">
                Your pack collection is empty
            </div>
        `;
        return;
    }

    inventoryPackGroups = groupInventoryPacksByType(inventoryPacks);

    container.innerHTML = inventoryPackGroups.map((group, index) => {
        const packType = group.packType.toLowerCase();
        const count = group.items.length;

        return `
            <article
                class="inventory-pack inventory-pack--${packType}"
                data-pack-group-index="${index}"
            >
                <span class="inventory-pack__count">×${count}</span>

                <div class="inventory-pack__visual">
                    <div class="inventory-pack__icon">
                        ${group.packType.charAt(0)}
                    </div>
                </div>

                <span class="inventory-pack__name">
                    ${escapeHtml(group.name)}
                </span>

                <button
                    class="inventory-pack__open"
                    type="button"
                    data-pack-group-index="${index}"
                >
                    OPEN
                </button>
            </article>
        `;
    }).join('');

    container.querySelectorAll('.inventory-pack').forEach(card => {
        card.addEventListener('click', event => {
            if (event.target.closest('.inventory-pack__open')) {
                return;
            }

            const index = Number(card.dataset.packGroupIndex);
            const group = inventoryPackGroups[index];

            if (group) {
                openInventoryPackModal(group);
            }
        });
    });

    container.querySelectorAll('.inventory-pack__open').forEach(button => {
        button.addEventListener('click', () => {
            const index = Number(button.dataset.packGroupIndex);
            const group = inventoryPackGroups[index];

            if (group) {
                openConfirmModal(group.items[0].id, group.name);
            }
        });
    });
}

function renderInventoryPacksError() {
    const container = document.getElementById('inventory-pack-grid');

    container.innerHTML = `
        <div class="inventory-empty">
            Failed to load your packs
        </div>
    `;
}

function openPlayerCardModal(card) {
    const player = card.player;

    document.getElementById('inventory-modal-avatar').src =
        player.imageUrl || placeholderImg(64);

    document.getElementById('inventory-modal-team-logo').src =
        player.teamLogoUrl || placeholderImg(38);

    document.getElementById('inventory-modal-nickname').textContent =
        player.nickname;

    document.getElementById('inventory-modal-team').textContent =
        player.teamName || 'Unknown team';

    document.getElementById('inventory-modal-rarity').textContent =
        normalizeRarity(card.rarity).toUpperCase();

    const stats = player.playerStatistic || {};

    document.getElementById('inventory-modal-stats').innerHTML = `
        <div class="stat stat--rating">
            <span class="stat__value">${stats.rating ?? '—'}</span>
            <span class="stat__label">Rating</span>
        </div>

        <div class="stat">
            <span class="stat__value">${stats.kills ?? '—'}</span>
            <span class="stat__label">Kills</span>
        </div>

        <div class="stat">
            <span class="stat__value">${stats.deaths ?? '—'}</span>
            <span class="stat__label">Deaths</span>
        </div>

        <div class="stat">
            <span class="stat__value">${stats.assists ?? '—'}</span>
            <span class="stat__label">Assists</span>
        </div>

        <div class="stat">
            <span class="stat__value">${stats.headshots ?? '—'}</span>
            <span class="stat__label">Headshots</span>
        </div>
    `;

    const modal = document.getElementById('inventory-player-modal');

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add('player-card-modal--open');
    });

    document.body.classList.add('modal-open');
}

function closePlayerCardModal() {
    const modal = document.getElementById('inventory-player-modal');

    modal.classList.remove('player-card-modal--open');

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove('modal-open');
}

function openInventoryPackModal(group) {
    document.getElementById('inventory-pack-chances-modal-name').textContent = group.name;

    const openButton = document.getElementById('inventory-pack-chances-modal-open');
    openButton.dataset.packId = group.items[0].id;
    openButton.dataset.packName = group.name;

    const barsContainer = document.getElementById('inventory-pack-chances-modal-bars');
    const rarities = sortRarityChances(group.rarityChances);

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

    const modal = document.getElementById('inventory-pack-chances-modal');

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

function closeInventoryPackModal() {
    const modal = document.getElementById('inventory-pack-chances-modal');

    modal.classList.remove('pack-chances-modal--open');

    document.querySelectorAll('#inventory-pack-chances-modal .pack-chance-bar__fill').forEach(fill => {
        fill.style.width = '0';
    });

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove('modal-open');
}

function openConfirmModal(packId, packName) {
    pendingOpenPackId = packId;

    document.getElementById('pack-open-confirm-text').textContent =
        `Are you sure you want to open ${packName}?`;

    const modal = document.getElementById('pack-open-confirm-modal');

    modal.hidden = false;

    requestAnimationFrame(() => {
        modal.classList.add('confirm-modal--open');
    });

    document.body.classList.add('modal-open');
}

function closeConfirmModal() {
    const modal = document.getElementById('pack-open-confirm-modal');

    modal.classList.remove('confirm-modal--open');

    setTimeout(() => {
        modal.hidden = true;
    }, 180);

    document.body.classList.remove('modal-open');

    pendingOpenPackId = null;
}

function confirmOpenPack() {
    if (pendingOpenPackId === null) {
        return;
    }

    openPack(pendingOpenPackId);
    closeConfirmModal();
}

function openPack(packId) {
    console.log('TODO: request to open pack', packId);
}

function initInventoryModalHandlers() {
    document
        .getElementById('inventory-player-modal-backdrop')
        ?.addEventListener('click', closePlayerCardModal);

    document
        .getElementById('inventory-player-modal-close')
        ?.addEventListener('click', closePlayerCardModal);
}

function initInventoryPackModalHandlers() {
    document
        .getElementById('inventory-pack-chances-modal-backdrop')
        ?.addEventListener('click', closeInventoryPackModal);

    document
        .getElementById('inventory-pack-chances-modal-close')
        ?.addEventListener('click', closeInventoryPackModal);

    document
        .getElementById('inventory-pack-chances-modal-open')
        ?.addEventListener('click', () => {
            const button = document.getElementById('inventory-pack-chances-modal-open');
            const packId = Number(button.dataset.packId);
            const packName = button.dataset.packName;

            closeInventoryPackModal();
            openConfirmModal(packId, packName);
        });
}

function initConfirmModalHandlers() {
    document
        .getElementById('pack-open-confirm-backdrop')
        ?.addEventListener('click', closeConfirmModal);

    document
        .getElementById('pack-open-confirm-close')
        ?.addEventListener('click', closeConfirmModal);

    document
        .getElementById('pack-open-confirm-yes')
        ?.addEventListener('click', confirmOpenPack);
}

function normalizeRarity(rarity) {
    return String(rarity || 'COMMON').toLowerCase();
}