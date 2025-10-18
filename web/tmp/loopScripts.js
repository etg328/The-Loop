const popup = document.getElementById('popup');
const checklist = document.getElementById('checklist');
const checkboxes = checklist.querySelectorAll('input[type="checkbox"]');
const STORAGE_KEY = 'selectedSources';

// Load saved selections from localStorage
function loadSelections() {
  const saved = JSON.parse(localStorage.getItem(STORAGE_KEY)) || [];
  checkboxes.forEach(cb => {
    cb.checked = saved.includes(cb.value);
  });
}

// Save selected checkboxes to localStorage
function saveSelections() {
  const selected = Array.from(checkboxes)
    .filter(cb => cb.checked)
    .map(cb => cb.value);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(selected));
}

// Show popup on page load
window.addEventListener('load', () => {
  loadSelections();
  popup.style.display = 'flex';
});

// On confirm, save selections and close popup
document.querySelector('.close-btn').addEventListener('click', () => {
  saveSelections();
  popup.style.display = 'none';
});
