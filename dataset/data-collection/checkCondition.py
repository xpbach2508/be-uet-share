import json

# Load the JSON data
with open('schools.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Define the latitude and longitude boundaries
lat_min, lat_max = 20.99, 21.05
lng_min, lng_max = 105.77, 105.85

# Check each entry in the JSON data
for entry in data:
    lat = entry.get('lat')
    lng = entry.get('lng')
    if lat_min > lat or  lat_max < lat or lng_min > lng or lng_max < lng:
        print(f"Address: {entry.get('address')}, Latitude: {lat}, Longitude: {lng}")