async function loadUser() {
    try {
        const response = await fetch('/api/v1/users/me');
        const user = await response.json();

        document.getElementById('username').textContent = user.telegramUsername;
        document.getElementById('telegram-id').textContent = user.telegramId;
    } catch (err) {
        console.error(err);
        document.getElementById('username').textContent = 'Ошибка';
    }
}

loadUser();