import sys
import random

def generate_events(count):
    events = []
    timestamp = 1
    step = 0.5
    for event_id in range(1, count):
        car_id = random.randint(0, 30) 
        speed = random.randrange(int(0.0 / step), int(30.0 / step)) * step
        exp_way = random.randint(0, 10)
        lane = random.randint(0, 3)
        direction = random.randint(0, 1)
        x_pos = random.randrange(int(0.0 / step), int(30.0 / step)) * step
        events.append((f"r_{event_id}", timestamp, car_id, speed, exp_way, lane, direction, x_pos))
        timestamp += random.randint(1, 1)  # timestamp strictly increasing, but can include gaps if needed 
    return events

if __name__ == "__main__":
    num_events = 10 #default in case user does not specify stdin size 
    num_events = int(sys.argv[1])
    result = generate_events(num_events)
    with open("./linear_road_events.txt", "w") as f:
        for event in result:
            f.write(f"{event[0]},{event[1]},{event[2]},{event[3]},{event[4]},{event[5]},{event[6]},{event[7]}\n")
    print("file 'linear_road_events.txt' created")