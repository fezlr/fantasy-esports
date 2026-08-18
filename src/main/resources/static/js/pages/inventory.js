let inventoryCards = [];
let inventoryPacks = [];

const mockInventoryPacks = [
    {
        id: 1,
        name: 'COMMON PACK',
        count: 1,
        icon: 'C'
    },
    {
        id: 2,
        name: 'RARE PACK',
        count: 2,
        icon: 'R'
    },
    {
        id: 3,
        name: 'EPIC PACK',
        count: 5,
        icon: 'E'
    },
    {
        id: 4,
        name: 'MYTHICAL PACK',
        count: 0,
        icon: 'M'
    },
    {
        id: 5,
        name: 'LEGENDARY PACK',
        count: 3,
        icon: 'L'
    }
];

async function initInventory() {
    initInventoryTabs();
    initInventoryModalHandlers();

    await loadInventory();
}

async function loadInventory() {
    await loadInventoryCards();
    loadInventoryPacks();
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

function loadInventoryPacks() {
    inventoryPacks = mockInventoryPacks.filter(
        pack => Number(pack.count) > 0
    );

    renderInventoryPacks();
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
                    <img
                        class="inventory-card__photo"
                        src="${player.imageUrl || placeholderImg(60)}"
                        alt=""
                    >

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

    container.innerHTML = inventoryPacks.map(pack => {
        const count = Number(pack.count);

        return `
            <article class="inventory-pack">
                <div class="inventory-pack__visual">
                    <div class="inventory-pack__icon">
                        ${escapeHtml(pack.icon || '?')}
                    </div>
                </div>

                <span class="inventory-pack__name">
                    ${escapeHtml(pack.name)}
                </span>

                <span class="inventory-pack__count ${count > 1 ? 'inventory-pack__count--multiple' : ''}">
                    ${count > 1 ? `×${count}` : 'AVAILABLE'}
                </span>

                <button
                    class="inventory-pack__open"
                    type="button"
                    data-pack-id="${pack.id}"
                >
                    OPEN
                </button>
            </article>
        `;
    }).join('');
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

function initInventoryModalHandlers() {
    document
        .getElementById('inventory-player-modal-backdrop')
        ?.addEventListener('click', closePlayerCardModal);

    document
        .getElementById('inventory-player-modal-close')
        ?.addEventListener('click', closePlayerCardModal);
}

function normalizeRarity(rarity) {
    return String(rarity || 'COMMON').toLowerCase();
}

function placeholderImg(size) {
    return `https://placehold.co/${size}x${size}/1b1d24/8b8f9c?text=%20`;
}

function escapeHtml(str) {
    if (!str) {
        return '';
    }

    return String(str).replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}