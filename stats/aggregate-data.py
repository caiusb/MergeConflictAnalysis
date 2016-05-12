import pandas as p
import numpy as n
import os

from sklearn.feature_extraction.text import CountVectorizer

first = lambda x: x.iloc[0]

def groupNodes(v):
    allNodes = list()
    for value in v.values.tolist():
        nodes = value.split(';')
        allNodes.extend(nodes)
    return ';'.join(set(allNodes))

def vectorize_column(data, column):
    result = data
    result = result.reset_index(drop=True)
    vectorizer = CountVectorizer(binary=True, lowercase=False)
    X = vectorizer.fit_transform(data[column])
    result = result.drop(column, 1)
    result = p.concat([result, p.DataFrame(X.toarray(), columns=vectorizer.get_feature_names())], axis=1)
    return result


groupDict = {'SHA': first,
             'AST_A_SIZE': n.sum,
             'LOC_A_SIZE': n.sum,
             'AST_B_SIZE': n.sum,
             'LOC_B_SIZE': n.sum,
             'COMBINED_AST_A_TO_B': n.sum,
             'LOC_A_TO_B': n.sum,
             'COMBINED_AST_A_TO_SOLVED': n.sum,
             'LOC_A_TO_SOLVED': n.sum,
             'COMBINED_AST_B_TO_SOLVED': n.sum,
             'LOC_B_TO_SOLVED': n.sum,
             'IS_CONFLICT': any,
             'LOC_SIZE_SOLVED': n.sum,
             'NO_AUTHORS': n.sum,
             'LOC_DIFF': n.sum,
             'AST_DIFF': n.sum,
             'MERGED_IN_MASTER': any,
             'PROJECT': first,
             'SHA_A': first,
             'SHA_B': first,
             'BASE_SHA': first,
             'TIME_SOLVED': first,
             'TIME_OFFSET': first,
             'TIME_A': first,
             'TIME_B': first,
             'NO_STATEMENTS': n.sum,
             'NO_METHODS': n.sum,
             'NO_CLASSES': n.sum,
             'DIFF_NODES_A_TO_B': groupNodes,
             'IS_AST_CONFLICT': any
}

def readData(folder):
    dataFrames = []
    csvFiles = [f for f in os.listdir(folder) if f.endswith(".csv")]
    csvFiles = [os.path.join(folder, f) for f in csvFiles]
    for file in csvFiles:
        try:
            df = p.read_csv(file, skip_blank_lines=True, low_memory=False)
            dataFrames.append(df)
        except ValueError:
            continue # Because I have empty files
        except p.parser.CParserError:
            continue # Because some of the files are shorter
    return p.concat(dataFrames)

#data = readData("../../ase16/data-cost-conc-devel-ase16/merge-data")
data = readData("../../data/with-types")
data = data.fillna('')
data['TIME_A'] = p.to_datetime(data['TIME_A'], unit='s')
data['TIME_B'] = p.to_datetime(data['TIME_B'], unit='s')
data['TIME_SOLVED'] = p.to_datetime(data['TIME_SOLVED'], unit='s')

#data['IS_CONFLICT'] = data['IS_CONFLICT'].replace({'TRUE': True, 'FALSE': False})
#data['MERGED_IN_MASTER'] = data['MERGED_IN_MASTER'].replace({'TRUE': True, 'FALSE': False})

perCommit = data.groupby('SHA').aggregate(groupDict)

#data = vectorize_column(data, 'DIFF_NODES_A_TO_B')
perCommit = vectorize_column(perCommit, 'DIFF_NODES_A_TO_B')

print(perCommit.columns)
perCommit.to_csv('../../data/per-commit.csv', index=False)
data.to_csv("../../data/all.csv.bz2", index=False, compression="bz2")
