document.querySelectorAll('.christmas-filters button').forEach(button => {
    button.addEventListener('click', () => {
        document.querySelectorAll('.christmas-filters button').forEach(item => {
            item.classList.toggle('active', item === button);
            item.setAttribute('aria-pressed', String(item === button));
        });
        document.querySelectorAll('.christmas-card').forEach(card => {
            card.hidden = Boolean(button.dataset.category) && card.dataset.category !== button.dataset.category;
        });
    });
});
