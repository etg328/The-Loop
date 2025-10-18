// loopScripts.js
import { fetchArticles } from './mockDatabase.js';

document.addEventListener("DOMContentLoaded", () => {
  const popup = document.getElementById("popup");
  const checklist = document.getElementById("checklist");
  const closeBtn = document.querySelector(".close-btn");
  const selectedSourcesDiv = document.getElementById("selected-sources");
  const changeBtn = document.getElementById("change-btn");
  const articlesDiv = document.getElementById("articles");

  // Show popup on initial load
  window.addEventListener("load", () => {
    popup.style.display = "flex";
  });

  // Handle confirm button
  closeBtn.addEventListener("click", async () => {
    popup.style.display = "none";

    // Collect checked boxes
    const selectedSources = [...checklist.querySelectorAll("input:checked")].map(i => i.value);

    // Update top-right display
    selectedSourcesDiv.textContent = selectedSources.length
      ? selectedSources.join(", ")
      : "No sources selected";

    // Fetch and display mock articles
    const articles = await fetchArticles(selectedSources);
    displayArticles(articles);
  });

  // Reopen popup
  changeBtn.addEventListener("click", () => {
    popup.style.display = "flex";
  });

  // Display articles
  function displayArticles(articles) {
    articlesDiv.innerHTML = "<h3>Latest Articles</h3>";

    if (articles.length === 0) {
      const noArticles = document.createElement("p");
      noArticles.textContent = "No articles available for selected sources.";
      articlesDiv.appendChild(noArticles);
      return;
    }

    articles.forEach(article => {
      const card = document.createElement("div");
      card.classList.add("article-card");

      card.innerHTML = `
        <h4>${article.title}</h4>
        <p><strong>${article.source}</strong></p>
        <p>${article.line1}</p>
        <p>${article.line2}</p>
        <p>${article.line3}</p>
      `;

      articlesDiv.appendChild(card);
    });
  }
});
