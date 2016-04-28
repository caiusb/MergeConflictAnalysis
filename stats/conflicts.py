import matplotlib.pyplot as plt
import pandas as p
import numpy as np
from sklearn import svm, preprocessing, tree, ensemble
from sklearn.cross_validation import train_test_split
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics import precision_score, recall_score, accuracy_score
from sklearn.grid_search import GridSearchCV


def precisionRecall(actual, predicted, classifier=""):
    print(classifier + "\nPrecision: {0:.2f}\nRecall: {1:.2f}\n\n".format(precision_score(actual, predicted), recall_score(actual, predicted)))

from sklearn.linear_model import SGDClassifier
#with open("../../data/all.csv.bz2", "rb") as f:
data = p.read_csv("../../data/all.csv.bz2", low_memory=False, skip_blank_lines=True, compression="bz2")

data['DIFF_NODES_A_TO_B'] = data['DIFF_NODES_A_TO_B'].fillna('')
conflicts = data[data['IS_CONFLICT'] == True]

conflicts = conflicts.reset_index(drop=True)
conflicts.IS_AST_CONFLICT = conflicts.IS_AST_CONFLICT.astype(int)

labels = conflicts['IS_AST_CONFLICT']
toCluster = conflicts[['LOC_DIFF', 'LOC_A_TO_B', 'LOC_A_TO_SOLVED', 'NO_METHODS', 'NO_STATEMENTS', 'NO_CLASSES', 'DIFF_NODES_A_TO_B']]
vectorizer = CountVectorizer(binary=True, lowercase=False)

X = vectorizer.fit_transform(toCluster.DIFF_NODES_A_TO_B)
toCluster = toCluster.drop('DIFF_NODES_A_TO_B', 1)

toCluster = p.concat([toCluster, p.DataFrame(X.toarray(), columns=vectorizer.get_feature_names())], axis=1)

train, test, train_labels, test_labels = train_test_split(toCluster, labels, test_size=.1)
scaler = preprocessing.StandardScaler()
scaler.fit(train)
train = p.DataFrame(scaler.transform(train), columns=train.columns.values)
test = p.DataFrame(scaler.transform(test), columns=test.columns.values)

clf = svm.SVC()
clf.fit(train, train_labels)
pred = clf.predict(test)
precisionRecall(test_labels, pred, "SVC")

sgd = SGDClassifier(loss="hinge", penalty="l2")
sgd.fit(train, train_labels).decision_function(test)
pred = sgd.predict(test)
precisionRecall(test_labels, pred, "SGD")

dt = tree.DecisionTreeClassifier()
dt.fit(train, train_labels)
pred = dt.predict(test)
precisionRecall(test_labels, pred, "Decision Tree")

rdf = ensemble.RandomForestClassifier()
rdf.fit(train, train_labels)
pred = rdf.predict(test)
precisionRecall(test_labels, pred, "Random Forest")

imp = rdf.feature_importances_
names = train.columns
imp, names = zip(*sorted(zip(imp, names)))

plt.barh(range(len(names)), imp, align = 'center')
plt.yticks(range(len(names)), names)

plt.xlabel('Importance or features')
plt.ylabel('Features')
plt.title('Importance of each feature')
plt.show()

paramGrid = {
    'n_estimators': np.arange(1, 200, 1, dtype=np.int32).tolist(),
    'learning_rate': np.arange(0.1, 3, 0.05, dtype=np.float32).tolist()
}
gs = GridSearchCV(ensemble.AdaBoostClassifier(), param_grid=paramGrid, n_jobs=8)
gs.fit(train, train_labels)
print("Best params for ADA Boost")
print(gs.best_params_)

adaBoost = ensemble.AdaBoostClassifier(n_estimators=100)
adaBoost.fit(train, train_labels)
pred = adaBoost.predict(test)
precisionRecall(test_labels, pred, "Ada Boost")
