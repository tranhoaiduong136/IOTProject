import serial.tools.list_ports 

import sys
from  Adafruit_IO import MQTTClient
###################Define mode###########################
TEMP = 0 #Temperature
AH = 1 # Air humidity
LED = 2 # Led
GH = 3 # Ground humidity
LIGHT = 4 # Light level
MOTOR = 5 # Pump    

#################Setup Server##########################
AIO_FEED_ID = ["bbc-temp", "bbc-humid", "bbc-led", 
"bbc-wind-speed", "bbc-pressure","bbc-motor"]
AIO_USERNAME = "duong_bku_13"
AIO_KEY = "aio_KYtu52729QaAtVd7SENpvIksGgfi"
DEVICE_PORT = "com0com"

#####################Setup port############################
ser = ""
def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if DEVICE_PORT in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    print("Connected port: " +commPort)
    return commPort
def setupPort(Defaultport = ""): ####Main focus####
    global ser
    if Defaultport == "":
        ser = serial.Serial( port= getPort(), baudrate=115200)
    else:
        ser = serial.Serial( port= Defaultport, baudrate=115200)

###############Setup connection with Adafruit#####################
client = ""
def connected(client):
    print("Ket noi thanh cong...")
    for i in range(len(AIO_FEED_ID)):
        client.subscribe(AIO_FEED_ID[i])

def subscribe(client , userdata , mid , granted_qos):
    print("Subcribe thanh cong....")

def disconnected(client):
    print("Ngat ket noi...")
    sys.exit(1)

send_flag = False
def message(client, feed_id , payload):
    print("Send Server "+ str(feed_id) + ":" + str(payload))
    global send_flag
    send_flag = True
    ser.write(("!s"+ feed_id + ":" + str(payload) + "#").encode())

def callback(client,feed_id , payload):
    global send_flag
    print("Receiving data from "+ str(feed_id) + ":" + str(payload))
    if send_flag:
        send_flag = False
    else:    
        if feed_id == AIO_FEED_ID[LED]:
            if payload == "1":
                print("LED ON")
                ser.write(( str(payload) + "#").encode())
            else:
                print("LED OFF")
                ser.write(( str(payload) + "#").encode())
        elif feed_id == AIO_FEED_ID[MOTOR]:
            if payload == "1":
                print("MOTOR ON")
                ser.write(("2#").encode())
            else:
                print("MOTOR OFF")
                ser.write(( "3#").encode())



def setupConnection():
    global client
    client = MQTTClient(AIO_USERNAME, AIO_KEY)
    client.on_connect = connected
    client.on_disconnect = disconnected 
    client.on_message = callback
    client.on_subscribe = subscribe
    client.connect()
    client.loop_background()

################### Read and Process Data######################
mess = ""
def processData(data):
    data = data.replace("!", "")
    data = data.replace("#", "")
    splitData = data.split(":")
    print(splitData)
    if len(splitData) == 3: 
        num = splitData[2]
        if splitData[1] == "TEMP":
            sendDataServer(client,AIO_FEED_ID[TEMP], num)
        elif splitData[1] == "AH":
            sendDataServer(client,AIO_FEED_ID[AH], num)
        elif splitData[1] == "LED":
            sendDataServer(client,AIO_FEED_ID[LED],num) 
        elif splitData[1] == "GH":
            sendDataServer(client,AIO_FEED_ID[GH],num)
        elif splitData[1] == "LIGHT":
            sendDataServer(client, AIO_FEED_ID[LIGHT], num)
        elif splitData[1] == "MOTOR":
            sendDataServer(client, AIO_FEED_ID[MOTOR], num) 

mess = ""
def readSerial():
    bytesToRead = ser.inWaiting()
    if (bytesToRead > 0):
        global mess
        mess = mess + ser.read(bytesToRead).decode("UTF-8")
        while ("#" in mess) and ("!" in mess):
            start = mess.find("!")
            end = mess.find("#")
            processData(mess[start:end + 1])
            if (end == len(mess)):
                mess = ""
            else:
                mess = mess[end+1:]

#####################Send data to server############################
def sendDataServer(client,key,data):
        client.publish(key, data)
        message(client,key,data)

