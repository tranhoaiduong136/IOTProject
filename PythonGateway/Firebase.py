import pyrebase
import datetime

Time = 30
config = {
    "apiKey": "AIzaSyBySQp9j4zgDcZioi4d9J7A8rsEufY5hr4",
    "authDomain": "multiproject-695c2.firebaseapp.com",
    "databaseURL": "https://multiproject-695c2-default-rtdb.firebaseio.com",
    "projectId": "multiproject-695c2",
    "storageBucket": "multiproject-695c2.appspot.com",
    "messagingSenderId": "1073364349401",
    "appId": "1:1073364349401:web:7cacfecbb5647a412929c8",
    "measurementId": "G-P6ZX4XJG2X"
}
firebase = ""
database = ""
def setupFirebase():
    global firebase,database
    firebase = pyrebase.initialize_app(config)
    database = firebase.database()

#Set History
def UpdateHistory(temp,mois,humid,light,led,motor):
    now = datetime.datetime.now()
    time_now = str(now.year) +"-"+ str(now.month) +"-"+str(now.day) +" "+ str(now.hour) + ":" + str(now.minute)
    data = {"temperature" : temp, "humidity" : humid, 
    "moisture" : mois, 
    "light": light,"led" : led,"motor" : motor}
    database.child("History").child(time_now).set(data)
#Get Limits
def getLowerBoundTemp():
    return database.child("Device").child("temp_min").get()     
def getUpperBoundTemp():
    return database.child("Device").child("temp_max").get()
def getLowerBoundMoisture():
    return database.child("Device").child("moist_min").get()
def getUpperBoundMoisture():
    return database.child("Device").child("moist_max").get()
def getLevelTemp():
    return database.child("Device").child("temp_level").get()
def getMoistLevel():
    return database.child("Device").child("moist_level").get()