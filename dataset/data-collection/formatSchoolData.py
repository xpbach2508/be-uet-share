import json
import requests
import time

# Load the input data
with open("hanoi_schools.json", "r", encoding="utf-8") as f:
    data = json.load(f)

def reverse_geocode(lat, lon):
    # Using Nominatim for reverse geocoding (with no API key, this is rate-limited)
    url = "https://nominatim.openstreetmap.org/reverse.php"
    params = {
        'lat': lat,
        'lon': lon,
        'format': 'jsonv2',
        'addressdetails': 1
    }
    headers = {
        'Accept': 'application/json',
        'User-Agent': 'MyScript/1.0 (contact@example.com)'  # Add your own user agent and contact info
    }
    r = requests.get(url, params=params, headers=headers)
    # print(r)
    if r.status_code == 200:
        try:
            response_json = r.json()
            return response_json.get("display_name", "")
        except json.JSONDecodeError:
            # In case the server didn't return valid JSON
            return ""
    return ""

results = []
for item in data:
    lat = item["latOrigin"]
    lng = item["lngOrigin"]
    
    # Reverse geocode to get address
    address = reverse_geocode(lat, lng)
    print(lat, lng, address)
    
    # Create new formatted object
    new_obj = {
        "lat": lat,
        "lng": lng,
        "address": address
    }
    results.append(new_obj)

    # Optional: sleep between requests to avoid rate limiting
    time.sleep(1)

# Save to new JSON file
with open("schools.json", "w", encoding="utf-8") as f:
    json.dump(results, f, ensure_ascii=False, indent=4)
