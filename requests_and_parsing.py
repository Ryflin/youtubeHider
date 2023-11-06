import requests as rs
import json as js
import re

search = True
url = ""
if search == True:
  term = input("input your search term: ")
  url = "https://www.youtube.com/results?search_query=" + term
getRequest = rs.get(url)
pattern = r'"videoId":"([a-zA-Z0-9_-]{11})"'

# Use re.findall to find all matches in the URL
matches = re.findall(pattern, getRequest.text)

# Extract the video IDs
video_ids = list (dict.fromkeys(matches))
new_Video_ids = ["https://www.youtube.com/watch?v=" + ids for ids in video_ids]
print(getRequest.text)