let teamsCache = [];

async function loadTeams() {
    const list = document.getElementById('teams-list');
    if (!list) return;

    try {
        const response = await fetch('/api/v1/cs2/teams', {
            headers: { 'X-Telegram-Init-Data': Telegram.WebApp.initData }
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

    const sorted = [...teams].sort((a, b) => (a.hltvRank ?? 999) - (b.hltvRank ?? 999));

    list.innerHTML = sorted.map((team, index) => `
        <div class="team-card" data-team-index="${index}">
            <img class="team-card__logo" src="${team.logoUrl || placeholderImg(44)}" alt="">
            <div class="team-card__info">
                <span class="team-card__name">${escapeHtml(team.name)}</span>
                <span class="team-card__rank">HLTV <span class="team-card__rank-value">#${team.hltvRank ?? '—'}</span></span>
            </div>
            <span class="team-card__arrow">›</span>
        </div>
    `).join('');

    teamsCache = sorted;

    list.querySelectorAll('.team-card').forEach(card => {
        card.addEventListener('click', () => {
            const team = teamsCache[Number(card.dataset.teamIndex)];
            openTeamModal(team);
        });
    });
}

function openTeamModal(team) {
    document.getElementById('team-modal-logo').src = team.logoUrl || placeholderImg(48);
    document.getElementById('team-modal-name').textContent = team.name;
    document.getElementById('team-modal-rank').textContent = `HLTV #${team.hltvRank ?? '—'}`;

    const playersEl = document.getElementById('team-modal-players');
    const players = [...(team.players || [])].sort((a, b) => Number(b.isActive) - Number(a.isActive));

    playersEl.innerHTML = players.map(p => `
        <div class="player-row">
            <img class="player-row__avatar" src="${p.imageUrl || placeholderImg(36)}" alt="">
            <div class="player-row__info">
                <span class="player-row__nickname">${escapeHtml(p.nickname)}</span>
                <span class="player-row__fullname">${escapeHtml([p.firstName, p.lastName].filter(Boolean).join(' '))}</span>
            </div>
            <span class="player-row__status ${p.isActive ? 'player-row__status--active' : 'player-row__status--bench'}">
                ${p.isActive ? 'ACTIVE' : 'BENCH'}
            </span>
        </div>
    `).join('');

    document.getElementById('team-modal').hidden = false;
}

function closeTeamModal() {
    document.getElementById('team-modal').hidden = true;
}

function initTeamModalHandlers() {
    document.getElementById('team-modal-backdrop').addEventListener('click', closeTeamModal);
    document.getElementById('team-modal-close').addEventListener('click', closeTeamModal);
}

function placeholderImg(size) {
    return `https://placehold.co/${size}x${size}/1b1d24/8b8f9c?text=%20`;
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>"']/g, c => ({
        '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
    }[c]));
}