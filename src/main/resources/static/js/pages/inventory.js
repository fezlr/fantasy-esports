let inventoryCards = [];
let inventoryPacks = [];

const mockInventoryCards = [
    {
        rarity: 'LEGENDARY',
        player: {
            nickname: 'donk',
            firstName: 'Danil',
            lastName: 'Kryshkovets',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=donk',
            teamName: 'Spirit',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=S',
            playerStatistic: {
                rating: 1.24,
                kills: 1248,
                deaths: 1012,
                assists: 302,
                headshots: 641
            }
        }
    },
    {
        rarity: 'MYTHICAL',
        player: {
            nickname: 'm0NESY',
            firstName: 'Ilya',
            lastName: 'Osipov',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=m0NESY',
            teamName: 'G2 Esports',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=G2',
            playerStatistic: {
                rating: 1.21,
                kills: 1321,
                deaths: 1084,
                assists: 341,
                headshots: 702
            }
        }
    },
    {
        rarity: 'EPIC',
        player: {
            nickname: 'ZywOo',
            firstName: 'Mathieu',
            lastName: 'Herbaut',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=ZywOo',
            teamName: 'Vitality',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=V',
            playerStatistic: {
                rating: 1.18,
                kills: 1194,
                deaths: 1030,
                assists: 321,
                headshots: 598
            }
        }
    },
    {
        rarity: 'EPIC',
        player: {
            nickname: 'NiKo',
            firstName: 'Nikola',
            lastName: 'Kovač',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=NiKo',
            teamName: 'Falcons',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=F',
            playerStatistic: {
                rating: 1.16,
                kills: 1124,
                deaths: 1052,
                assists: 280,
                headshots: 672
            }
        }
    },
    {
        rarity: 'RARE',
        player: {
            nickname: 'b1t',
            firstName: 'Valeriy',
            lastName: 'Vakhovskiy',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=b1t',
            teamName: 'Natus Vincere',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=N',
            playerStatistic: {
                rating: 1.14,
                kills: 1012,
                deaths: 1002,
                assists: 290,
                headshots: 550
            }
        }
    },
    {
        rarity: 'RARE',
        player: {
            nickname: 'ropz',
            firstName: 'Robin',
            lastName: 'Kool',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=ropz',
            teamName: 'Vitality',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=V',
            playerStatistic: {
                rating: 1.12,
                kills: 1087,
                deaths: 1018,
                assists: 314,
                headshots: 603
            }
        }
    },
    {
        rarity: 'COMMON',
        player: {
            nickname: 'jL',
            firstName: 'Justinas',
            lastName: 'Lekavicius',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=jL',
            teamName: 'Natus Vincere',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=N',
            playerStatistic: {
                rating: 1.08,
                kills: 987,
                deaths: 1001,
                assists: 301,
                headshots: 517
            }
        }
    },
    {
        rarity: 'COMMON',
        player: {
            nickname: 'device',
            firstName: 'Nicolai',
            lastName: 'Reedtz',
            imageUrl: 'https://placehold.co/300x300/1b1d24/e3a63e?text=device',
            teamName: 'Astralis',
            teamLogoUrl: 'https://placehold.co/100x100/1b1d24/e3a63e?text=A',
            playerStatistic: {
                rating: 1.05,
                kills: 942,
                deaths: 931,
                assists: 276,
                headshots: 481
            }
        }
    }
];

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
    inventoryCards = mockInventoryCards;
    inventoryPacks = mockInventoryPacks.filter(
        pack => Number(pack.count) > 0
    );

    renderInventoryCards();
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