import java.util.ArrayList;

public class JavaSort {

    public static void BubbleSort(ArrayList<Object[]> arr)
    {
        for(int j=0; j<arr.size(); j++)
        {
            for(int i=j+1; i<arr.size(); i++)
            {
                if((arr.get(i)[0]).toString().compareToIgnoreCase(arr.get(j)[0].toString())<0)
                {
                    Object[] words = arr.get(j);
                    arr.set(j, arr.get(i));
                    arr.set(i, words);
                }
            }
            System.out.println(arr.get(j)[0] + " - " + arr.get(j)[1]);
        }
    }

    public static void SelectionSort(ArrayList<Object[]> arr)
    {
        int n = arr.size();

        // One by one move boundary of unsorted subarray
        for (int i = 0; i < n-1; i++)
        {
            // Find the minimum element in unsorted array
            int min_idx = i;
            for (int j = i+1; j < n; j++)
                if (arr.get(j)[1].toString().compareToIgnoreCase(arr.get(min_idx)[1].toString())<0)
                    min_idx = j;

            // Swap the found minimum element with the first
            // element
            Object[] temp = arr.get(min_idx);
            arr.set(min_idx, arr.get(i));
            arr.set(i, temp);
        }
    }

    public static void InsertionSort(ArrayList<Object[]> arr)
    {
        int n = arr.size();

        for (int i = 1; i < n; ++i)
        {
            Object[] key = arr.get(i);
            int j = i - 1;
            while (j >= 0 && (arr.get(j)[5].toString().compareToIgnoreCase(key[5].toString())<0))
            {
                arr.set(j + 1, arr.get(j));
                j = j - 1;
            }
            arr.set(j + 1, key);
        }
    }
}
