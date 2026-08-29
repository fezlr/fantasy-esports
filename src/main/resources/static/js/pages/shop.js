const RARITY_ORDER = ['COMMON', 'RARE', 'EPIC', 'MYTHICAL', 'LEGENDARY'];
let shopPacksCache = [];

async function initShop() {
    initShopModalHandlers();
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
        container.innerHTML = '<div class="stub"><span class="stub__text">Не удалось загрузить паки</span></div>';
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
                >
                    BUY PACK
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
}

function openPackChancesModal(pack) {
    document.getElementById('pack-chances-modal-name').textContent = pack.name;
    document.getElementById('pack-chances-modal-price').textContent = `${pack.cost} PTS`;

    const buyButton = document.getElementById('pack-chances-modal-buy');
    buyButton.dataset.packId = pack.id;

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

function initShopModalHandlers() {
    document
        .getElementById('pack-chances-modal-backdrop')
        ?.addEventListener('click', closePackChancesModal);

    document
        .getElementById('pack-chances-modal-close')
        ?.addEventListener('click', closePackChancesModal);
}

function sortRarityChances(rarityChances) {
    return Object.entries(rarityChances).sort(
        ([a], [b]) => RARITY_ORDER.indexOf(a) - RARITY_ORDER.indexOf(b)
    );
}

function escapeHtml(str) {
    if (!str) return '';

    return str.replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}