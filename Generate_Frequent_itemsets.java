import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;

public class Generate_Frequent_itemsets
{
	static HashMap<Integer, ArrayList<TreeSet<Integer>>> transactionTable = new HashMap<Integer, ArrayList<TreeSet<Integer>>>();
	static HashMap<TreeSet<Integer>, Integer> candidate_itemsets = new HashMap<TreeSet<Integer>, Integer>();
	static HashMap<TreeSet<Integer>, Integer> frequent_itemsets = new HashMap<TreeSet<Integer>, Integer>();
	static HashMap<Integer, String> num_to_item_mapping = new HashMap<Integer, String>();
	static int minsup = 15;
	static double minimum_confidence = 0.8;
	static int global_candidate_size = 1;
	static int num_of_rules = 0;
	
	public static void main(String[] args) {
		generate_freq_itemsets("Output.csv");
		create_num_to_item_hashmap();
		generate_assoc_rules_wrapper();
		System.out.println("Number of rules " + num_of_rules);
	}
	
	public static void create_num_to_item_hashmap()
	{
		BufferedReader reader;
		try
		{
			reader = new BufferedReader(new FileReader("dict.csv"));
			String line;
			String arr[] = new String[2];
			while((line = reader.readLine())!=null)
			{
				arr = line.split(",");
				num_to_item_mapping.put(Integer.parseInt(arr[1]), arr[0]);
			}
			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void generate_freq_itemsets(String csv_file_path)
	{
		try
		{
			create_transaction_table(csv_file_path);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		do
		{
			calculate_support_count();
			filter_itemsets();
			System.out.println("candidate size " + global_candidate_size);
			for(TreeSet<Integer> t : candidate_itemsets.keySet())
			{
				System.out.println(t + " " + candidate_itemsets.get(t));
			}
			global_candidate_size++;
			merge_itemsets();	

		}while(!candidate_itemsets.isEmpty());
		System.out.println("DONE");
		
	}
	
	public static void create_transaction_table(String csv_file_path) throws IOException
	{
		BufferedReader reader =new BufferedReader(new FileReader(csv_file_path));
		String line = "";
		int transaction_ID = 1;
		while((line=reader.readLine())!=null)
		{
			ArrayList<TreeSet<Integer>> itemset = new ArrayList<TreeSet<Integer>>();
			String items[] = line.trim().split(",");
			for(String item : items)
			{
				TreeSet<Integer> item_treeSet = new TreeSet<Integer>();
				item_treeSet.add(Integer.parseInt(item));
				candidate_itemsets.put(item_treeSet, 0);
				itemset.add(item_treeSet);
			}
			transactionTable.put(transaction_ID, itemset);
			transaction_ID++;
		}
		reader.close();
	}
	
	public static void calculate_support_count()
	{
		for(int transaction_index : transactionTable.keySet())
		{
			ArrayList<TreeSet<Integer>> itemset_list = transactionTable.get(transaction_index);
			for(TreeSet<Integer> itemset : itemset_list)
			{
				candidate_itemsets.put(itemset, candidate_itemsets.get(itemset)+1);
			}
		}
	}
	
	public static void filter_itemsets()
	{
		ArrayList<TreeSet<Integer>> itemsets_to_be_removed = new ArrayList<TreeSet<Integer>>(); 
		for(TreeSet<Integer> itemset : candidate_itemsets.keySet())
		{
			if(candidate_itemsets.get(itemset)<minsup) itemsets_to_be_removed .add(itemset);
			else frequent_itemsets.put(itemset, candidate_itemsets.get(itemset));
		}
		for(int transaction_index : transactionTable.keySet())
		{
			for(TreeSet<Integer> itemset : itemsets_to_be_removed)
			{
				transactionTable.get(transaction_index).remove(itemset);
			}
		}
		for(TreeSet<Integer> itemset : itemsets_to_be_removed)
		{
			candidate_itemsets.remove(itemset);
		}
	}
	
	public static void merge_itemsets()
	{
		candidate_itemsets.clear();
		for(int transaction_id : transactionTable.keySet())
		{
			ArrayList<TreeSet<Integer>> itemset_list = transactionTable.get(transaction_id);
			ArrayList<TreeSet<Integer>> new_itemset_list = new ArrayList<TreeSet<Integer>>();
			int itemset_list_length = itemset_list.size(); 
			for(int i=0;i<itemset_list_length-1;i++)
		    {                              
				for(int j=i+1;j<itemset_list_length;j++)
				{
					TreeSet<Integer> temp_itemset = new TreeSet<Integer>(itemset_list.get(i));
					temp_itemset.addAll(itemset_list.get(j));
					if(!new_itemset_list.contains(temp_itemset) && check_itemset_validity(temp_itemset, itemset_list))
					{
						new_itemset_list.add(temp_itemset);		
						candidate_itemsets.put(temp_itemset, 0);
					}

				}
			}
			transactionTable.put(transaction_id, new_itemset_list);
		}		
	}
	
	public static boolean check_itemset_validity(TreeSet<Integer> Kplus1_itemset, ArrayList<TreeSet<Integer>> K_itemset_list)
	{
		if(Kplus1_itemset.size()!=global_candidate_size) return false;
		int K = global_candidate_size;
		int count = 0;
		for(TreeSet<Integer> old_itemset : K_itemset_list)
		{
			if(Kplus1_itemset.containsAll(old_itemset)) count++;
			if(count==K) return true;
		}
		return false;
	}
	
	public static void generate_assoc_rules_wrapper()
	{
		TreeSet<Integer> lhs_rule = new TreeSet<Integer>();
		TreeSet<Integer> rhs_rule = new TreeSet<Integer>();
		System.out.println(frequent_itemsets);
		for(TreeSet<Integer> itemset : frequent_itemsets.keySet())
		{
			if(itemset.size()<=1) continue;
			int itemset_size = itemset.size();
			
			for(int i=0;i< (1<<itemset_size);i++)
			{
				lhs_rule = new TreeSet<Integer>(itemset);
				rhs_rule.clear();
				for(int j=0;j<itemset_size;j++)
				{	
					if((i & (1<<j))>0)
					{
						int item = find_element_in_set(j, itemset);
						lhs_rule.remove(item);
						rhs_rule.add(item);						
					}
				}
				check_confidence(itemset, lhs_rule, rhs_rule);
			}
		}
	}
	
	public static int find_element_in_set(int index, TreeSet<Integer> set)
	{
		int i = 0;
		for(Integer item : set)
		{
			if(i==index) return item;
			i++;
		}
		return -1;
	}
	
	public static void check_confidence(TreeSet<Integer> itemset,TreeSet<Integer> lhs_itemset, TreeSet<Integer> rhs_itemset)
	{
		if(lhs_itemset.isEmpty()) return;
		if(rhs_itemset.isEmpty()) return;
		double confidence = (double)frequent_itemsets.get(itemset)/frequent_itemsets.get(lhs_itemset);
		if(confidence>=minimum_confidence)
		{
			num_of_rules++;
			System.out.print("{");
			for(int item : lhs_itemset)
			{
				System.out.print(num_to_item_mapping.get(item) + ", ");
			}
			System.out.print("} ");
			System.out.print("--> ");
			System.out.print("{");
			for(int item : rhs_itemset)
			{
				System.out.print(num_to_item_mapping.get(item) + ", ");
			}
			System.out.print("} ");
			System.out.println();
		}
	}

}
