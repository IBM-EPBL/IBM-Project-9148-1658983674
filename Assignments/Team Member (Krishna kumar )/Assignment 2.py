import random
import time
while True:
    humidity=random.random()
    temperature=random.randint(-100,100)
    print("Current temperature of weather : ",temperature)
    if temperature > 40 : 
        print("Temperature is too high!")
    elif temperature>30:
        print("Temperature is high!")
    elif temperature<22 and temperature >=10:
        print("Temperature is cold!")
    elif temperature <10:
        print("Temperature is too cold! ")
    else:
        print("Temperature is moderate!")
    time.sleep(1)
