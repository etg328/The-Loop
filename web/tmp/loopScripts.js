// loopScripts.js

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

    closeBtn.addEventListener("click", async () => {
      popup.style.display = "none";

      // Collect checked sources
      const selectedSources = [...checklist.querySelectorAll("input:checked")].map(i => i.value);

      // Update top-right display
      selectedSourcesDiv.textContent = selectedSources.length
        ? selectedSources.join(", ")
        : "No sources selected";

      articlesDiv.innerHTML = "<p>Loading articles...</p>";

      // Fetch articles from your EC2 server
      if (selectedSources.length) {
        try {
          const query = encodeURIComponent(selectedSources.join(","));
          const response = await fetch(`http://50.16.113.115:8081/api/articles?sources=${query}`);
          if (!response.ok) throw new Error("Failed to fetch articles");
          const articles = await response.json();
          displayArticles(articles);
        } catch (err) {
          console.error(err);
          articlesDiv.innerHTML = "<p>Failed to load articles.</p>";
        }
      } else {
        displayArticles([]);
      }
    });

  });

  // Reopen popup
  changeBtn.addEventListener("click", () => {
    popup.style.display = "flex";
  });

  // Display articles
  function displayArticles(articles) {
    articlesDiv.innerHTML = "<h3>Latest Articles</h3>";

    if (!articles.length) {
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
        <p>${article.text}</p>
      `;

      articlesDiv.appendChild(card);
    });
  }
});