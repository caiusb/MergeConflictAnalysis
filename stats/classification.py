import pandas as p
import numpy as np
from sklearn.cross_validation import train_test_split, cross_val_score
from sklearn import ensemble, tree
from sklearn.metrics import precision_score, recall_score, accuracy_score, f1_score, r2_score

def precisionRecall(actual, predicted, classifier=""):
    print(classifier + "\nPrecision: {0:.2f}\nRecall: {1:.2f}\n\n".format(precision_score(actual, predicted), recall_score(actual, predicted)))

def r2(estimator, X, y_true):
	y_pred = estimator.predict(X)
	return r2_score(y_true, y_pred)

manual = p.read_csv("../../data/manual.csv")
#data = p.read_csv("../../data/all.csv.bz2", low_memory=False, skip_blank_lines=True, compression="bz2")
data = p.read_csv("../../data/per-commit.csv")
classified = p.merge(manual, data, how='inner', on=['SHA'])._get_numeric_data()
labels = classified['LABEL']
toClassify = classified.drop('LABEL', 1)

#train, test = train_test_split(classified, test_size = 0.1)
#labels = train['LABEL']
#train = train.drop('LABEL', 1)
#test_labels = test['LABEL']
#test = test.drop('LABEL', 1)

adaBoost = ensemble.AdaBoostClassifier(n_estimators=100)
#dt = tree.DecisionTreeClassifier() 
scores = cross_val_score(adaBoost, toClassify, labels, cv=5, scoring=r2) 
print("R^2: {0:.2f} (+/- {1:.2f})".format(scores.mean(), scores.std() * 2))
print("Min: {0:.2f}; Max: {1:.2f}".format(min(scores), max(scores)))

#adaBoost.fit(train, labels)
#pred = adaBoost.predict(test)
#precisionRecall(test_labels, pred, "Ada Boost")
#print(f1_score(test_labels, pred, average='micro'))
#print(pred)
#print(test_labels)
