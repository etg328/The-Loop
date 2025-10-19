// loopScripts.js
import { fetchArticles } from './mockDatabase.js';

document.addEventListener("DOMContentLoaded", () => {
  const popup = document.getElementById("popup");
  const checklist = document.getElementById("checklist");
  const closeBtn = document.querySelector(".close-btn");
  const selectedSourcesDiv = document.getElementById("selected-sources");
  const changeBtn = document.getElementById("change-btn");
  const articlesDiv = document.getElementById("articles");
  const backgroundSelect = document.getElementById("background-select");
  const body = document.body;

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

  // === Background Dropdown Functionality ===
  const backgrounds = {
    default: "https://coolbackgrounds.imgix.net/39sOStld2OCyNn3HmCpqco/21d339122a7cb417c83e6ebdc347ea5c/sea-edge-79ab30e2.png?w=3840&q=60&auto=format,compress",
    beach: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
    city: "https://images.unsplash.com/photo-1505761671935-60b3a7427bad",
    secret: "https://assets.goal.com/images/v3/bltb7d9998f06c980a4/dfdade2bc59127c4e1c3e9ccd5b28d4f07ad7b06.jpg?auto=webp&format=pjpg&width=3840&quality=60",
  };

  backgroundSelect.addEventListener("change", () => {
    const value = backgroundSelect.value;
    const url = backgrounds[value];

    if (url) {
      body.style.backgroundImage = `url('${url}')`;
      body.style.backgroundColor = "";
    } else {
      body.style.backgroundImage = "none";
      body.style.backgroundColor = "#1a1a1a";
    }

    // Smooth transition
    body.style.transition = "background 0.6s ease-in-out, background-color 0.6s ease-in-out";
  });

  // === Display Articles ===
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
        <p><strong><a href="${article.link}" target="_blank">${article.source}</a></strong></p>
        <p>${article.line1}</p>
        <p>${article.line2}</p>
        <p>${article.line3}</p>
      `;

      articlesDiv.appendChild(card);
    });
  }
});
