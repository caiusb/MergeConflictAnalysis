from sklearn import cluster, datasets, mixture, preprocessing
from sklearn.feature_extraction.text import CountVectorizer
import matplotlib.pyplot as plt
import csv
import numpy as np
import pandas as p

def loadData():
    with open("../per-commit.csv") as f:
        data = p.read_csv(f)
    return data

def dropOutliers(df, columns):
    for c in columns:
        df = df[np.abs(df[c] - df[c].mean()) < 3 * df[c].std()]
    return df

data = loadData()
data['AFFECTED_NODES'] = data['AFFECTED_NODES'].fillna('')

data = data.loc[(data['IS_CONFLICT'] == True) &
            (data['LOC_DIFF']) > 0 &
            (data['LOC_A_TO_B'] > 0) &
            (data['LOC_A_TO_SOLVED'] > 0) &
            (data['LOC_B_TO_SOLVED'] > 0) &
            (data['NO_METHODS'] > 0) &
            (data['NO_STATEMENTS'] > 0) &
            (data['NO_CLASSES'] > 0)]

print(str(len(data)) + " observations")
data.set_index('SHA')

toCluster = data[['SHA', 'LOC_DIFF', 'LOC_A_TO_B', 'LOC_A_TO_SOLVED', 'NO_METHODS', 'NO_STATEMENTS', 'NO_CLASSES', 'AFFECTED_NODES']]
toCluster = dropOutliers(toCluster, ['LOC_DIFF', 'LOC_A_TO_B', 'LOC_A_TO_SOLVED', 'NO_METHODS', 'NO_STATEMENTS', 'NO_CLASSES'])
toCluster = toCluster.reset_index(drop=True)

commits = toCluster[['SHA']]
toCluster = toCluster.drop('SHA', 1)

vectorizer = CountVectorizer(binary=True, lowercase=False)
X = vectorizer.fit_transform(toCluster.AFFECTED_NODES)
print(len(vectorizer.get_feature_names()))
print(X.shape)

toCluster = toCluster.drop('AFFECTED_NODES', 1)

scaler = preprocessing.MinMaxScaler()
toCluster = p.DataFrame(scaler.fit_transform(toCluster), columns=toCluster.columns.values)
print(toCluster.shape)
toCluster = p.concat([toCluster, p.DataFrame(X.toarray(), columns=vectorizer.get_feature_names())], axis=1)
print(toCluster.shape)

toCluster.to_csv("../data-ready.csv")

dbscan = cluster.DBSCAN()
dbscan.fit(toCluster)
print("==== DBSCAN results: ====")
print("# of clusters: " + str(len(set(dbscan.labels_))))

p.concat([commits, toCluster, p.DataFrame(dbscan.labels_, columns=["LABEL"])], axis=1, ignore_index=True).to_csv("data-labeled-birch.csv", index_label=False)

birch = cluster.Birch()
birch.fit(toCluster)
print("==== Birch results: ====")
print("# of clusters: " + str(len(set(birch.labels_))))

p.concat([commits, toCluster, p.DataFrame(birch.labels_, columns=["LABEL"])], axis=1, ignore_index=True).to_csv("data-labeled-birch.csv", index_label=False)
