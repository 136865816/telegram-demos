#!/usr/bin/env python

import requests
from flask import Flask, request, jsonify


################################################################################################
# Run the commands to install the libraries this application depends on:
#   sudo pip install requests
#   sudo pip install Flask
#
# You will need to sign up for the Enterprise plan in order for this script to work for you.
# https://www.whatsmate.net/telegram-gateway-enterprise.html
#
# This is a simplistic application that demonstrates how you can receive Telegram messages
# from your dedicated Enterprise Telegram Gateway and reply to them accordingly.
# 
# The following diagram illustrates how messages flow between your application and your customer.
#     .-----------------------------------------------------------------------------------------.
#     | This application      |      The Enterprise Telegram GW      |      Customer's Telegram |
#     '-----------------------------------------------------------------------------------------'
#  (1)                                                              <---     "Hello"
#  (2)                       <---     "Hello"
#  (3)   "Choose a product"  ---> 
#  (4)                                "Choose a product"            ---> 
#  (5)                                                              <---     "1"
#  (6)                       <---     "1"
#  (7)   "Price: 90.0 "      ---> 
#  (8)                                "Price: 90.0 "                ---> 
################################################################################################


##########################
# TODO: Please put down your own Client ID and secret here:
##########################
CLIENT_ID = "YOUR_OWN_CLIENT_ID"
CLIENT_SECRET = "YOUR_OWN_CLIENT_SECRET"

##########################
# TODO: YOUR GATEWAY INSTANCE ID
##########################
ENTERPRISE_INSTANCE_ID = "99"

##########################
# TODO: Port of this webhook application server
##########################
WEB_PORT = 9999



##########################
# Helper Functions:
##########################
def sendTelegramMessage(destinationNumber, message):
    jsonBody = {
        'number': destinationNumber,
        'message': message
    }

    headers = {
        'X-WM-CLIENT-ID': CLIENT_ID,
        'X-WM-CLIENT-SECRET': CLIENT_SECRET
    }

    r = requests.post('https://enterprise.whatsmate.net/v1/telegram/single/message/%s' % ENTERPRISE_INSTANCE_ID,
        headers=headers,
        json=jsonBody)

    print("Message sent to %s" % destinationNumber)
    print("Status code: %s" % r.status_code)
    print("Response: %s" % r.content)
    return


# Instantiate the Flask web application instance
app = Flask(__name__)


##########################
# Routes:
##########################
@app.route("/")
def noop():
    return "For testing only"


@app.route("/webhook", methods=['POST'])
def handleTelegramMessageReceived():
    jsonDict = request.get_json(force=True, silent=False)
    srcNumber = jsonDict.get('number')
    sanitizedMsg = jsonDict.get('message').encode('utf8')

    app.logger.info(u"Received a message from this number: %s; Content: %s" % (srcNumber, sanitizedMsg))

    replyMessage = u"Choose a product. Send \n1 for product X.\n2 for product Y and so on."
    if sanitizedMsg == "1":
        replyMessage = u"Price of X: $90"
    elif sanitizedMsg == "2":
        replyMessage = u"Price of Y: $100"
    elif sanitizedMsg == "3":
        replyMessage = u"Price of Z: $200"

    sendTelegramMessage(srcNumber, replyMessage)
    return "Data received"


# Actually run the Flask framework
if __name__ == "__main__":
    app.run(host='0.0.0.0', port=WEB_PORT, debug=True)

