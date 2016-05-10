#!/opt/local/bin/python

import pandas as p
import numpy as np
import os

folder = "/Users/caius/osu/TheMergingProblem/build-data/results"
files = os.listdir(folder)

frames = []
for f in files:
	try:
		frame = p.read_csv(os.path.join(folder,f), skip_blank_lines=True)
		frame.columns = ['SHA', 'Parent1', 'Parent2', 'Merge']
		frame['Project'] = os.path.splitext(f)[0]
		frame.reset_index(inplace=True)
		frames.append(frame)
	except ValueError:
		continue

buildData = p.concat(frames)
buildData.drop('index', 1)
buildData.to_csv("../build-data.csv", index=False)
	
