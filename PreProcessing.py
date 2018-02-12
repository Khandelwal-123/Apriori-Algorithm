#!/usr/bin/env python2
# -*- coding: utf-8 -*-
"""
Created on Mon Feb 12 12:03:11 2018

@author: manik
"""
import pandas as pd

csv_dataframe   = pd.read_csv('Preprocess.csv', header = None, sep = '\t',skipinitialspace=True)

new_dict = {}
k=0
for idx, row in csv_dataframe.iterrows():
    for i in range(32):
        if pd.isnull(row[i]):
            break
    
        elif row[i] not in new_dict:
            new_dict[row[i]] = k
            row[i]=k
            k=k+1
        else :
            row[i] = new_dict[row[i]].values()