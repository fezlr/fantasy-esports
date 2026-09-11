Telegram.WebApp.ready();
Telegram.WebApp.expand();

let currentPage = null;
let userFantasyPoints = 0;
const pageContent = document.getElementById('page-content');
const navButtons = document.querySelectorAll('.bottom-nav__btn');
const pageCache = {};
const RARITY_ORDER = ['COMMON', 'RARE', 'EPIC', 'MYTHICAL', 'LEGENDARY'];

function sortRarityChances(rarityChances) {
    return Object.entries(rarityChances).sort(
        ([a], [b]) => RARITY_ORDER.indexOf(a) - RARITY_ORDER.indexOf(b)
    );
}

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    showPage('fantasy');
});

function initNavigation() {
    navButtons.forEach(btn => {
        btn.addEventListener('click', () => showPage(btn.dataset.page));
    });
}

async function showPage(pageId) {
    if (pageId === currentPage) {
        return;
    }

    currentPage = pageId;

    navButtons.forEach(btn => {
        btn.classList.remove('bottom-nav__btn--active');
    });

    document
        .querySelector(`.bottom-nav__btn[data-page="${pageId}"]`)
        .classList.add('bottom-nav__btn--active');

    const html = await loadPageHtml(pageId);
    pageContent.innerHTML = html;

    if (pageId === 'players') {
        initTeamModalHandlers();
        loadTeams();
    }

    if (pageId === 'inventory') {
        initInventory();
    }

    if (pageId === 'shop') {
        initShop();
    }

    if (pageId === 'profile') {
        loadUser();
        loadUserFantasyPoints();
    }
}

async function loadPageHtml(pageId) {
    if (pageCache[pageId]) {
        return pageCache[pageId];
    }

    const response = await fetch(`/pages/${pageId}.html`, {
        cache: 'reload'
    });

    if (!response.ok) {
        throw new Error(`Failed to load page ${pageId}`);
    }

    const html = await response.text();

    pageCache[pageId] = html;

    return html;
}

async function loadUser() {
    try {
        const response = await fetch('/api/v1/users/me', {
            headers: {
                'X-Telegram-Init-Data': Telegram.WebApp.initData
            }
        });

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const user = await response.json();
        renderUser(user);
    } catch (err) {
        console.error('Failed to load user:', err);
    }
}

function renderUser(user) {
    const usernameEl = document.getElementById('username');
    const telegramIdEl = document.getElementById('telegram-id');

    if (usernameEl) {
        usernameEl.textContent = user.telegramUsername ?? 'Игрок';
    }

    if (telegramIdEl) {
        telegramIdEl.textContent = `ID: ${user.telegramId}`;
    }
}

async function loadUserFantasyPoints() {
    try {
        const response = await fetch('/api/v1/cs2/users/me/points', {
            headers: {
                'X-Telegram-Init-Data': Telegram.WebApp.initData
            }
        });

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const data = await response.json();
        userFantasyPoints = data.fantasyPoints ?? 0;
    } catch (err) {
        console.error('Failed to load fantasy points:', err);
    }

    renderUserFantasyPoints();
}

function setUserFantasyPoints(points) {
    userFantasyPoints = points;
    renderUserFantasyPoints();
}

function renderUserFantasyPoints() {
    const shopBalanceEl = document.querySelector('.shop-balance__value');

    if (shopBalanceEl) {
        shopBalanceEl.textContent = userFantasyPoints;
    }

    const inventoryBalanceEl = document.getElementById('inventory-balance-value');

    if (inventoryBalanceEl) {
        inventoryBalanceEl.textContent = userFantasyPoints;
    }

    const profilePointsEl = document.getElementById('stat-points');

    if (profilePointsEl) {
        profilePointsEl.textContent = userFantasyPoints;
    }
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