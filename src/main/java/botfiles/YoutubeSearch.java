package botfiles;



import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;


public class YoutubeSearch {
	private static final String API_KEY = "AIzaSyDgQLgG3AimATHtVg4Q7gBc4LTrBpHM8a8";

	private YouTube youtube;
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final long NUMBER_OF_VIDEOS_RETURNED = 5;
	List<SearchResult> searchResultList;
	SearchListResponse searchResponse;
	
	public YoutubeSearch(){
		
		youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
	        public void initialize(HttpRequest request) throws IOException {
	        }
	    }).setApplicationName("youtube-cmdline-search-sample").build();
		
	}
	
	public void search(String query) throws IOException{
		
		
		try{
			YouTube.Search.List search = youtube.search().list("id,snippet");
			search.setKey(API_KEY);
			search.setType("video");
			search.setQ(query);
			search.setFields("items(id/kind,id/videoId,snippet/title)");
	        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	        searchResponse = search.execute();
            searchResultList = searchResponse.getItems();
		}
		catch (GoogleJsonResponseException e) {
	            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
	                    + e.getDetails().getMessage());
	    } 
		catch(Throwable t){
			
		}	
		
		
		
	}
	
	//returns empty list if nothing found
	public List<String> getURLs(){
		List<String> urlList = new LinkedList<>();
		if(searchResultList.isEmpty()){
			return urlList;
		}
		Iterator<SearchResult> iterator = searchResultList.iterator();
		while(iterator.hasNext()){
			SearchResult result = iterator.next();
			urlList.add("https://www.youtube.com/watch?v=" + result.getId().getVideoId());
			
			
		}
		for(String e : urlList){
			System.out.println(e);
		}
		return urlList;
	}
	
	
	//returns empty list if nothing found

	public List<String> getTitles(){
		List<String> titleList = new LinkedList<>();
		if(searchResultList.isEmpty()){
			return titleList;
		}
		Iterator<SearchResult> iterator = searchResultList.iterator();
		while(iterator.hasNext()){
			SearchResult result = iterator.next();
			titleList.add(result.getSnippet().getTitle());
			
		}
		
		return titleList;
	}
}
