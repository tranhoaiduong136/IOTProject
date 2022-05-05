from requests import get
#Weather and API:
IP = ""
city = ""
country = ""
region = ""
longtitude = ""
lattitude = ""
weather = ""

wind_speed = ""
current_temperature = ""
current_pressure = ""
current_humidity = ""
#####################API info###########################
API_WEATHER = '3e20360073ec8e1161a513d60681bdeb'
WEBSITE_WEATHER = 'http://api.openweathermap.org/data/2.5/weather?lat={}&lon={}&appid='

################## Function###################
def ipInfo():
    from urllib.request import urlopen
    from json import load
    url = 'https://ipinfo.io/json'
    res = urlopen(url)
    #response from url(if res==None then check connection)
    data = load(res)
    return data
def getWeatherInfo(website,api):
    return get((website + api).format(longtitude, lattitude)).json()
def UpdateDataWeather():
    global data,IP,city, country, region,loc, longtitude,lattitude
    data = ipInfo()
    IP = data['ip']
    city = data['city']
    country = data['country']
    region = data['region']
    loc = data['loc'].split(',')
    longtitude = loc[0]
    lattitude = loc[1]
