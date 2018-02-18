import collections
from collections import OrderedDict
import os
import csv

transaction_table = {}
support_count_table = {}
candidate_itemsets = []

def create_transaction_table(processed_data_path):
    row_index = 0
    with open(processed_data_path,'r') as csvFile:
        csv_reader = csv.reader(csvFile, delimiter=',')
        for row in csv_reader:
            row_index+=1
            temp_list = []
            for item in row:
                temp_dict = {}
                temp_dict[item] = None
                if (OrderedDict(temp_dict),0) not in candidate_itemsets:
                    candidate_itemsets.append({(OrderedDict(temp_dict),0):0})
                temp_list.append(OrderedDict(temp_dict))
            transaction_table[row_index] = temp_list #we might have to create a copy
    print(transaction_table)
    print(candidate_itemsets)
'''
#TODO
def create_supersets():
'''
def calculate_support_counts():
    for transaction_id in transaction_table:
        itemset_list = transaction_table[transaction_id]
        for itemset in itemset_list:
            candidate_itemsets[] += 1 

'''
#TODO
def update_transaction_table():

#TODO
'''
def __main__(processed_data_path):
    create_transaction_table(processed_data_path)

create_transaction_table('sample.csv')
