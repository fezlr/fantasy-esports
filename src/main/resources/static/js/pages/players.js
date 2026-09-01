let teamsCache = [];
let currentTeamPlayers = [];

async function loadTeams() {
    const list = document.getElementById('teams-list');
    if (!list) return;

    try {
        const response = await fetch('/api/v1/cs2/teams', {
            headers: {
                'X-Telegram-Init-Data': Telegram.WebApp.initData
            }
        });

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        teamsCache = await response.json();
        renderTeams(teamsCache);
    } catch (err) {
        console.error('Failed to load teams:', err);
        list.innerHTML = '<div class="stub"><span class="stub__text">Не удалось загрузить команды</span></div>';
    }
}

function renderTeams(teams) {
    const list = document.getElementById('teams-list');
    if (!list) return;

    const sorted = [...teams].sort(
        (a, b) => (a.valveRank ?? 999) - (b.valveRank ?? 999)
    );

    teamsCache = sorted;

    list.innerHTML = sorted.map((team, index) => `
        <div class="team-card" data-team-index="${index}">
            <img class="team-card__logo" src="${team.logoUrl || placeholderImg(44)}" alt="">
            <div class="team-card__info">
                <span class="team-card__name">${escapeHtml(team.name)}</span>
                <span class="team-card__rank">VALVE <span class="team-card__rank-value">#${team.valveRank ?? '—'}</span></span>
            </div>
            <span class="team-card__arrow">›</span>
        </div>
    `).join('');

    list.querySelectorAll('.team-card').forEach(card => {
        card.addEventListener('click', () => {
            const team = teamsCache[Number(card.dataset.teamIndex)];

            if (team) {
                openTeamModal(team);
            }
        });
    });
}

function openTeamModal(team) {
    document.getElementById('team-modal-logo').src =
        team.logoUrl || placeholderImg(48);

    document.getElementById('team-modal-name').textContent =
        team.name;

    document.getElementById('team-modal-rank').textContent =
        `VALVE #${team.valveRank ?? '—'}`;

    const playersEl = document.getElementById('team-modal-players');

    const players = [...(team.players || [])].sort(
        (a, b) => Number(b.isActive) - Number(a.isActive)
    );

    currentTeamPlayers = players;

    playersEl.innerHTML = players.map((p, index) => `
        <div class="player-row" data-player-index="${index}">
            <img class="player-row__avatar" src="${p.imageUrl || placeholderImg(36)}" alt="">
            <div class="player-row__info">
                <span class="player-row__nickname">${escapeHtml(p.nickname)}</span>
                <span class="player-row__fullname">${escapeHtml([p.firstName, p.lastName].filter(Boolean).join(' '))}</span>
            </div>
            <span class="player-row__status ${p.isActive ? 'player-row__status--active' : 'player-row__status--bench'}">
                ${p.isActive ? 'ACTIVE' : 'BENCHED'}
            </span>
        </div>
    `).join('');

    playersEl.querySelectorAll('.player-row').forEach(row => {
        row.addEventListener('click', () => {
            const player = currentTeamPlayers[Number(row.dataset.playerIndex)];

            if (player) {
                openPlayerModal(player);
            }
        });
    });

    document.getElementById('team-modal').hidden = false;
    document.getElementById('player-modal').hidden = true;

    lockBodyScroll();
}

function closeTeamModal() {
    document.getElementById('team-modal').hidden = true;
    document.getElementById('player-modal').hidden = true;

    unlockBodyScroll();
}

function openPlayerModal(player) {
    document.getElementById('team-modal').hidden = true;

    document.getElementById('player-modal-avatar').src =
        player.imageUrl || placeholderImg(48);

    document.getElementById('player-modal-nickname').textContent =
        player.nickname;

    document.getElementById('player-modal-fullname').textContent =
        [player.firstName, player.lastName].filter(Boolean).join(' ') || '—';

    const statsEl = document.getElementById('player-modal-stats');
    const stats = player.playerStatistic || {};

    statsEl.innerHTML = `
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

    document.getElementById('player-modal').hidden = false;

    lockBodyScroll();
}

function closePlayerModal() {
    document.getElementById('player-modal').hidden = true;
    document.getElementById('team-modal').hidden = false;
}

function lockBodyScroll() {
    document.body.classList.add('modal-open');
}

function unlockBodyScroll() {
    document.body.classList.remove('modal-open');
}

function initTeamModalHandlers() {
    document.getElementById('team-modal-backdrop')
        ?.addEventListener('click', closeTeamModal);

    document.getElementById('team-modal-close')
        ?.addEventListener('click', closeTeamModal);

    document.getElementById('player-modal-backdrop')
        ?.addEventListener('click', closePlayerModal);

    document.getElementById('player-modal-close')
        ?.addEventListener('click', closePlayerModal);
}