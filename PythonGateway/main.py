import time
import Gateway
import Firebase
import Weather

Time_Delay = 1
def checkLimits(self):
    pass
def formatHistory(temp,humid,mois,light,led,motor):
    temp += "°C"
    mois += "%"
    if(led == "1"):
        led = "ON"  
    else:
        led = "OFF"
    if(motor == "1"):
        motor = "ON"
    else:
        motor = "OFF"
    Firebase.UpdateHistory(temp,humid,mois,light,led,motor)
    pass
def checkCondition():
    pass

def main():
    Firebase.setupFirebase()
    # Gateway.setupPort("COM17")
    # Gateway.setupConnection()
    # while True:
    #     Gateway.readSerial()
    #     checkLimits()
    #     time.sleep(Time_Delay)
if __name__ == "__main__":
    main()