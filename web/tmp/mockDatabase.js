// mockDatabase.js

const mockDatabase = {
  "New York Times": [
    {
      title: "Global Markets React to Fed Decision",
      line1: "Rates drop by 5%",
      line2: "Expect increase in gas prices"
    },
    {
      title: "Tech Giants Report Record Earnings",
      line1: "Apple and Microsoft exceed Q3 expectations",
      line2: "Analysts predict continued growth into 2026"
    }
  ],
  "Wall Street Journal": [
    {
      title: "Oil Prices Climb as Demand Rebounds",
      line1: "U.S. production struggles to keep up",
      line2: "Investors eye OPEC’s next move"
    },
    {
      title: "Manufacturing Sector Hits Record High",
      line1: "Strong output across Midwest",
      line2: "Supply chains continue gradual recovery"
    }
  ],
  "ESPN": [
    {
      title: "Lakers Triumph in Overtime Thriller",
      line1: "LeBron leads with 32 points",
      line2: "Coach praises team resilience"
    },
    {
      title: "NFL Week 6 Power Rankings Released",
      line1: "Eagles reclaim top spot after big win",
      line2: "Analysts debate playoff favorites"
    }
  ]
};

// Simulate backend API call
export function fetchArticles(sources) {
  return new Promise((resolve) => {
    setTimeout(() => {
      let results = [];
      sources.forEach(source => {
        if (mockDatabase[source]) {
          results = results.concat(
            mockDatabase[source].map(article => ({
              source,
              ...article
            }))
          );
        }
      });
      resolve(results);
    }, 500);
  });
}
