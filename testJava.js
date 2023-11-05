function extractYouTubeVideoIDs(searchTerm) {
  // Construct the search URL
  const url = `https://www.youtube.com/results?search_query=${searchTerm}`;

  // Send an HTTP GET request
  fetch(url)
    .then(response => response.text())
    .then(data => {
      // Modify the regular expression pattern
      const pattern = /(v=|v\/|vi=|vi\/|youtu.be\/)([a-zA-Z0-9_-]{11})/g;

      // Use matchAll to find all matches in the HTML content
      const matches = [...data.matchAll(pattern)];

      // Extract the video IDs
      const videoIds = matches.map(match => match[2]);

      console.log(videoIds);
    })
    .catch(error => {
      console.error('An error occurred:', error);
    });
}

// Example usage:
const searchTerm = "your_search_term";
extractYouTubeVideoIDs(searchTerm);
