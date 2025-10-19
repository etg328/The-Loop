// loopScripts.js

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
  
    // Show loading message
    articlesDiv.innerHTML = "<p>Loading articles...</p>";
  
    // Fetch articles from your server
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
  

  // Reopen popup
  changeBtn.addEventListener("click", () => {
    popup.style.display = "flex";
  });

  // === Background Dropdown Functionality ===
  const backgrounds = {
    default: "https://coolbackgrounds.imgix.net/39sOStld2OCyNn3HmCpqco/21d339122a7cb417c83e6ebdc347ea5c/sea-edge-79ab30e2.png?w=3840&q=60&auto=format,compress",
    beach: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e",
    city: "https://images.unsplash.com/photo-1505761671935-60b3a7427bad",
    secret: "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/17/a2/f4/1c/stadio-santiago-bernabeu.jpg?w=900&h=500&s=1",
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
        <ul>
          <li>${article.line1}</li>
          <li>${article.line2}</li>
          <li>${article.line3}</li>
        </ul>
      `;

      articlesDiv.appendChild(card);
    });
  }
});
