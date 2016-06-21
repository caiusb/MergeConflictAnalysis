import pandas as p
import numpy as np
from sklearn.cross_validation import train_test_split, cross_val_score
from sklearn import ensemble, tree
from sklearn.metrics import precision_score, recall_score, accuracy_score, f1_score, r2_score
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.multiclass import OneVsRestClassifier
from sklearn.svm import SVC

def precisionRecall(actual, predicted, classifier=""):
    print(classifier + "\nPrecision: {0:.2f}\nRecall: {1:.2f}\n\n".format(precision_score(actual, predicted), recall_score(actual, predicted)))

def r2(estimator, X, y_true):
	y_pred = estimator.predict(X)
	return r2_score(y_true, y_pred)

def vectorize_column(data, column):
    result = data
    result = result.reset_index(drop=True)
    vectorizer = CountVectorizer(binary=True, lowercase=False)
    X = vectorizer.fit_transform(data[column])
    result = result.drop(column, 1)
    result = p.concat([result, p.DataFrame(X.toarray(), columns=vectorizer.get_feature_names())], axis=1)
    return result

manual = p.read_csv("../../data/manual-classification-raw.csv")
data = p.read_csv("../../data/per-commit.csv")

classified = p.merge(manual, data, how='inner', on=['SHA'])#._get_numeric_data()
classified = vectorize_column(classified, 'LABEL')
classified = classified._get_numeric_data()
print(classified.columns.values)
#print(classified['LABEL'])
#print(classified.loc[classified['SHA'] == "357724af3ee50cc2195988220275182d69e5eacd"])

labels = classified[['COMMENTS', 'CONFIGURATION', 'DELETE', 'FORMATTING', 'OTHER', 'SEMANTIC', 'UNLABELED', 'UNRELATED']]
toClassify = classified.drop(['COMMENTS', 'CONFIGURATION', 'DELETE', 'FORMATTING', 'OTHER', 'SEMANTIC', 'UNLABELED', 'UNRELATED'], 1)

adaBoost = OneVsRestClassifier(ensemble.AdaBoostClassifier(n_estimators=100), n_jobs=-1) #ensemble.AdaBoostClassifier(n_estimators=100)
scores = cross_val_score(adaBoost, toClassify, labels, cv=10, scoring=r2)
print("R^2: {0:.2f} (+/- {1:.2f})".format(scores.mean(), scores.std() * 2))

adaBoost.fit(toClassify, labels)
print(adaBoost.score(toClassify, labels))
predicted = adaBoost.predict(data._get_numeric_data())
p.DataFrame(predicted).to_csv("../../data/predicted.csv", index=False)

quit()

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