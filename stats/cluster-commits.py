from sklearn import cluster, datasets, mixture
import matplotlib.pyplot as plt
import csv
import numpy as np

def loadData():
    data = []
    with open("../data-clean-normal.csv") as f:
        reader = csv.DictReader(f)
        for line in reader:
            observation = [line['LOC_A_TO_B'],
                            line['LOC_A_TO_SOLVED'],
                            line['LOC_B_TO_SOLVED'],
                            line['NO_METHODS'],
                            line['NO_CLASSES'],
                            line['NO_STATEMENTS']
                            ]
            data.append(observation)


    return data #[[float(a) for a in x] for x in data]

data = loadData()

gmm = mixture.GMM()
m = gmm.fit(data)
print(m)
