import requests
import json

# Step 1: Get all schools in Hanoi via Overpass
overpass_url = "https://overpass-api.de/api/interpreter"
query = """
[out:json][timeout:25];
(
  node["amenity"="school"](20.99,105.77,21.05,105.85);
  way["amenity"="school"](20.99,105.77,21.05,105.85);
  relation["amenity"="school"](20.99,105.77,21.05,105.85);
);
out center;
"""
response = requests.post(overpass_url, data={'data': query})
data = response.json()

# Step 2: Extract coordinates from the response
# Overpass returns nodes, ways, and relations. 
# - For nodes, lat/lon are direct.
# - For ways/relations, we use "center" from the response.
features = data.get('elements', [])

# Define a function for reverse geocoding
def reverse_geocode(lat, lon):
    # Using Nominatim for reverse geocoding
    nominatim_url = "https://nominatim.openstreetmap.org/reverse"
    params = {
        'lat': lat,
        'lon': lon,
        'format': 'jsonv2',
        'addressdetails': 1
    }
    r = requests.get(nominatim_url, params=params)
    print(r.json())
    if r.status_code == 200:
        rev_data = r.json()
        return rev_data.get('display_name', '')
    return ''

results = []

for feature in features:
    # Determine lat/lon
    if feature['type'] == 'node':
        lat = feature['lat']
        lon = feature['lon']
    else:
        # For ways and relations, center is provided
        lat = feature['center']['lat']
        lon = feature['center']['lon']

    address = reverse_geocode(lat, lon)

    # Format the data as requested
    place_info = {
        "latOrigin": lat,
        "lngOrigin": lon,
        "addressStart": address
    }

    results.append(place_info)

# Step 3: Save to a JSON file or print
with open("hanoi_schools.json", "w", encoding='utf-8') as f:
    json.dump(results, f, ensure_ascii=False, indent=4)

print("Done! Extracted {} schools.".format(len(results)))
