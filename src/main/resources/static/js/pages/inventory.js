const mockInventoryCards = [
    {
        id: 1,
        nickname: 'donk',
        team: 'Spirit',
        rarity: 'LEGENDARY',
        imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=donk',
        points: 24
    },
    {
        id: 2,
        nickname: 'ZywOo',
        team: 'Vitality',
        rarity: 'EPIC',
        imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=ZywOo',
        points: 21
    },
    {
        id: 3,
        nickname: 'NiKo',
        team: 'Falcons',
        rarity: 'RARE',
        imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=NiKo',
        points: 18
    },
    {
        id: 4,
        nickname: 'b1t',
        team: 'Natus Vincere',
        rarity: 'COMMON',
        imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=b1t',
        points: 14
    }
];

const mockInventoryPacks = [
    {
        id: 1,
        name: 'Common Pack',
        description: 'Basic player pack',
        icon: 'C'
    },
    {
        id: 2,
        name: 'Epic Pack',
        description: 'Contains higher rarity cards',
        icon: 'E'
    }
];

function initInventory() {
    renderInventoryCards();
    renderInventoryPacks();
    initInventoryTabs();
}

function initInventoryTabs() {
    document.querySelectorAll('[data-inventory-tab]').forEach(button => {
        button.addEventListener('click', () => {
            const tab = button.dataset.inventoryTab;

            document.querySelectorAll('[data-inventory-tab]')
                .forEach(btn => btn.classList.remove('inventory-tabs__btn--active'));

            button.classList.add('inventory-tabs__btn--active');

            document.getElementById('inventory-cards').hidden = tab !== 'cards';
            document.getElementById('inventory-packs').hidden = tab !== 'packs';
        });
    });
}

function renderInventoryCards() {
    const container = document.getElementById('inventory-card-grid');

    if (!mockInventoryCards.length) {
        container.innerHTML = `
            <div class="inventory-empty">
                Your card collection is empty
            </div>
        `;
        return;
    }

    container.innerHTML = mockInventoryCards.map(card => `
        <div class="inventory-card">
            <span class="inventory-card__rarity">${card.rarity}</span>
            <img
                class="inventory-card__image"
                src="${card.imageUrl}"
                alt="${escapeHtml(card.nickname)}"
            >
            <div class="inventory-card__info">
                <span class="inventory-card__nickname">${escapeHtml(card.nickname)}</span>
                <span class="inventory-card__team">${escapeHtml(card.team)}</span>
                <span class="inventory-card__points">+${card.points} PTS</span>
            </div>
        </div>
    `).join('');
}

function renderInventoryPacks() {
    const container = document.getElementById('inventory-pack-grid');

    container.innerHTML = mockInventoryPacks.map(pack => `
        <div class="inventory-pack">
            <div class="inventory-pack__image">${pack.icon}</div>

            <div class="inventory-pack__info">
                <span class="inventory-pack__name">${escapeHtml(pack.name)}</span>
                <span class="inventory-pack__description">${escapeHtml(pack.description)}</span>
            </div>

            <button class="inventory-pack__open" type="button">
                OPEN
            </button>
        </div>
    `).join('');
}ё