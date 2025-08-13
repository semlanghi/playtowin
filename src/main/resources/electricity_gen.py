import sys
import random

def generate_events(count):
    events = []
    timestamp = 1
    for event_id in range(1, count):
        cons_A = random.randint(0, 19) 
        cons_B = random.randint(0, 19)
        events.append((f"r_{event_id}", timestamp, cons_A, cons_B))
        timestamp += random.randint(1, 1)  # timestamp strictly increasing, but can include gaps if needed 
    return events

if __name__ == "__main__":
    num_events = 10 #default in case user does not specify stdin size 
    num_events = int(sys.argv[1])
    result = generate_events(num_events)
    with open("./electricity_events.txt", "w") as f:
        for event in result:
            f.write(f"{event[0]},{event[1]},{event[2]},{event[3]}\n")
    print("file 'electricity_events.txt' created")