window.addEventListener('load', () => {
  const popup = document.getElementById('popup');
  const checklist = document.getElementById('checklist');
  const closeBtn = document.querySelector('.close-btn');
  const selectionList = document.getElementById('selection-list');
  const editBtn = document.getElementById('edit-btn');

  // Show popup when page loads
  popup.style.display = 'flex';

  // Confirm button behavior
  closeBtn.addEventListener('click', () => {
    popup.style.display = 'none';
    updateSelections();
  });

  // Open popup when "Change Sources" is clicked
  editBtn.addEventListener('click', () => {
    popup.style.display = 'flex';
  });

  // Update selections in the summary box
  function updateSelections() {
    const selected = [...checklist.querySelectorAll('input:checked')].map(i => i.value);
    selectionList.innerHTML = '';

    if (selected.length === 0) {
      const li = document.createElement('li');
      li.textContent = 'No selections yet';
      selectionList.appendChild(li);
    } else {
      selected.forEach(item => {
        const li = document.createElement('li');
        li.textContent = item;
        selectionList.appendChild(li);
      });
    }
  }
});
