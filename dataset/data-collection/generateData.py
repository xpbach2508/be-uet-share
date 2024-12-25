import json
import random

# Configuration
num_requests = 150
length = 1.4  # fixed or adjust if necessary

# Load the schools data from a JSON file
with open("schools.json", "r", encoding="utf-8") as f:
    schools = json.load(f)

# Ensure we have at least two entries
if len(schools) < 2:
    raise ValueError("Not enough data in schools.json to pick two distinct locations.")

requests_list = []

for _ in range(num_requests):
    # Pick two distinct random places
    origin = random.choice(schools)
    destination = random.choice(schools)
    while destination is origin:
        destination = random.choice(schools)

    # Generate random pickup time and capacity
    pick_up_time = round(random.uniform(7.25, 7.8), 2)
    capacity = random.randint(1, 4)

    # Construct the request object
    req = {
        "pickUpTime": pick_up_time,
        "capacity": capacity,
        "latOrigin": origin["lat"],
        "lngOrigin": origin["lng"],
        "latDestination": destination["lat"],
        "lngDestination": destination["lng"],
        "addressStart": origin["address"],
        "addressEnd": destination["address"],
        "length": length
    }
    requests_list.append(req)

# Save the list of requests to a JSON file
with open("150newschools.json", "w", encoding="utf-8") as f:
    json.dump(requests_list, f, ensure_ascii=False, indent=4)

print("Done! Generated {} requests in requests.json".format(num_requests))
