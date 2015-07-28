#!/opt/local/bin/python

import requests as req

r = req.get('https://api.github.com')

print(r.status_code)
