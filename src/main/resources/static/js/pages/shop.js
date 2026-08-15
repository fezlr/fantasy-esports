const mockShopPacks = [
    {
        id: 'common',
        name: 'COMMON PACK',
        description: 'Basic pack with a chance to receive common and rare players.',
        price: 100,
        icon: 'C',
        rarities: ['COMMON', 'RARE']
    },
    {
        id: 'epic',
        name: 'EPIC PACK',
        description: 'Improved pack with a chance to receive epic players.',
        price: 250,
        icon: 'E',
        rarities: ['RARE', 'EPIC', 'MYTHICAL']
    },
    {
        id: 'legendary',
        name: 'LEGENDARY PACK',
        description: 'Premium pack with the highest rarity players.',
        price: 600,
        icon: 'L',
        rarities: ['EPIC', 'MYTHICAL', 'LEGENDARY']
    }
];

function initShop() {
    renderShopPacks();
}

function renderShopPacks() {
    const container = document.getElementById('shop-packs');

    container.innerHTML = mockShopPacks.map(pack => `
        <article class="shop-pack shop-pack--${pack.id}">
            <div class="shop-pack__visual">
                <div class="shop-pack__icon">${pack.icon}</div>
            </div>

            <div class="shop-pack__info">
                <div>
                    <div class="shop-pack__name">${escapeHtml(pack.name)}</div>
                    <div class="shop-pack__description">
                        ${escapeHtml(pack.description)}
                    </div>
                </div>

                <span class="shop-pack__price">${pack.price} PTS</span>
            </div>

            <div class="shop-pack__rarities">
                ${pack.rarities.map(rarity => `
                    <span class="shop-pack__rarity">${rarity}</span>
                `).join('')}
            </div>

            <button
                class="shop-pack__button"
                type="button"
                data-pack-id="${pack.id}"
            >
                BUY PACK
            </button>
        </article>
    `).join('');
}