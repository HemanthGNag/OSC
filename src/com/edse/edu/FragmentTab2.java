package com.edse.edu;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;

public class FragmentTab2 extends SherlockFragment
{
	ListView listViewRecent;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		// Get the view from fragmenttab2.xml
		View view = inflater.inflate(R.layout.article_display, container, false);
		
		if(MainActivity.selectedFrag == 0)
		{
		  //should call a method to handle News fragment recent 2nd tab
		  
		  NewsTabRecent(view, inflater, container);
		}
		else if(MainActivity.selectedFrag == 1)
		{
			//call method to handle actions when Calendar fragment 1st tab
			
			view = inflater.inflate(R.layout.fragmenttab1, container, false);
			
		}
		return view;
	}
	
	public void NewsTabRecent(View view, LayoutInflater inflater, ViewGroup container)
	{
		//Most of this section will be different in the final version of the app. Right now this is
		//hard coded somewhat like the other section for client UI viewing purposes.
	
    	
    	ArrayList<Article> recentTest = new ArrayList<Article>();
        Article recArtOne = new Article("Arctic cyclones more common than previously thought",
        		"Weather data at the Ohio Supercomputer Center reveals in new study hundreds of smaller storms that had previously escaped detection", "supercomputer", R.drawable.articcyclones, "10-14-2013");
        
        Article recArtTwo = new Article("Simulation experts creating virtual house for healthcare training",
        		"omputer-generated environments will alert workers to potential hazards", "supercomputer", R.drawable.nioshlogo, "01/01/2014");
        
        //since we don't actually have a list of Articles retrieved from the server I added these to an arraylist
        //myself to simulate what we might have...
        recentTest.add(recArtOne);
        recentTest.add(recArtTwo);
        ArrayList<String> testTitle = new ArrayList<String>();
        ArrayList<String> testDesc = new ArrayList<String>();
        ArrayList<Integer> img = new ArrayList<Integer>();
        
        for(Article art : recentTest)
        {
        	testTitle.add(art.getTitle());
        	testDesc.add(art.getsubDesc());
        	img.add(art.getPreviewImage());
        }
        
        String[] specTitle = testTitle.toArray(new String[testTitle.size()]);
        String[] specDesc = testDesc.toArray(new String[testDesc.size()]);
        Integer[] specImg = img.toArray(new Integer[img.size()]);
        
        
    	
    	
 		listViewRecent = (ListView) view.findViewById(R.id.listview);
 		listViewRecent.setAdapter(new ArticleAdapter(getActivity().getApplicationContext(), 
 				specImg,specTitle, specDesc));
    
 		listViewRecent.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				switch(position)
				{
				  
				}
				
			}
 			
 		});
   
	}
	
}