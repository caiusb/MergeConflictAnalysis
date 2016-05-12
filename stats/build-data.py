#!/opt/local/bin/python

import pandas as p
import numpy as np
import os


def readBuildData(folder):
    files = [f for f in os.listdir(folder) if f.endswith('.csv')]
    frames = []
    for f in files:
        try:
            frame = p.read_csv(os.path.join(folder, f), skip_blank_lines=True)
            frame.columns = ['SHA', 'Parent1', 'Parent2', 'Merge']
            frame['Project'] = os.path.splitext(f)[0]
            frame.reset_index(inplace=True)
            frames.append(frame)
        except ValueError:
            continue

    return p.concat(frames).drop('index', 1)


folder = "../../build-data/results"
buildData = readBuildData(folder)
buildData.to_csv("../../data/build-data.csv", index=False)
print(buildData.shape)
print(buildData.columns.values)
