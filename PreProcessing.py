#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Feb 12 12:03:11 2018

@author: manik
"""
import pandas as pd
import csv

csv_dataframe   = pd.read_csv('Preprocess.csv', header = None, sep = '\t',skipinitialspace=True)

new_dict = {}
k=0
for idx, row in csv_dataframe.iterrows():
    for i in range(32):
        if pd.isnull(row[i]):
            break
    
        elif row[i] not in new_dict:
            new_dict[row[i]] = k
            print(row[i] + ' ' + str(k))
            row[i]=k
            k=k+1
        else :
            row[i] = new_dict[row[i]]

csv_dataframe = csv_dataframe.fillna(20000)
with open('dict.csv', 'wb') as csv_file:
    writer = csv.writer(csv_file)
    for key, value in new_dict.items():
       writer.writerow([key, value])

a = csv_dataframe.values    
a.sort(axis=1)  
print(csv_dataframe)  
csv_dataframe.to_csv('Output.csv',header=False, index=False,index_label=False, sep =',')

