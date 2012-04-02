#!/usr/bin/python
# Filename: details.py

import json
import httplib

class Details:

	def __init__(self, user_id, friend_id):
	
		self.user_id = user_id
		self.friend_id = friend_id

	def getCommonInterests(self,name):
	
                f = file("/home/saurabh/hadoop-0.20.203.0/data/"+name+"/final_result.txt")

                searchkey1 = self.user_id+","+self.friend_id
                searchkey2 = self.friend_id+","+self.user_id

                while True:

                        line = f.readline()

                        if len(line) == 0:
                                break

                        if (line.find(searchkey1) != -1 or line.find(searchkey2) != -1):
                                startPos = line.find("[")
                                endPos = line.find("]")
                                str = line[startPos+1:endPos]
                                interests = str.split(",")
                                conn = httplib.HTTPConnection('graph.facebook.com')
                                interestsDetails = []
                                for interest in interests:
                                        interest = interest.replace("\"","")
                                        url = "/"+interest
                                        conn.request("GET",url)
                                        r = conn.getresponse()
                                        data = r.read()
                                        conn.close()
                                        jsonInterest = json.loads(data)
                                        interestDetails = {}
                                        interestDetails['id'] = jsonInterest["id"]
                                        interestDetails['name'] = jsonInterest["name"]
                                        interestDetails['category'] = jsonInterest["category"]
                                        interestDetails['link'] = jsonInterest["link"]
                                        interestDetails['likes'] = jsonInterest["likes"]
					if jsonInterest.has_key("picture"):
						interestDetails['picture'] = jsonInterest["picture"]
					else:
						interestDetails['picture'] = "https://s-static.ak.facebook.com/rsrc.php/v1/y6/r/_xS7LcbxKS4.gif"
                                        interestsDetails.append(interestDetails)
                                return interestsDetails

                f.close()

