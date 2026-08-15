Telegram.WebApp.ready();
Telegram.WebApp.expand();

const pageContent = document.getElementById('page-content');
const navButtons = document.querySelectorAll('.bottom-nav__btn');
const pageCache = {};
let currentPage = null;

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

function escapeHtml(str) {
    if (!str) {
        return '';
    }

    return str.replace(/[&<>"']/g, c => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[c]));
}