package botfiles;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.sedmelluq.discord.lavaplayer.player.*;

public class TrackScheduler extends AudioEventAdapter {
	
	  private BlockingQueue<AudioTrack> queue;
	  private BlockingQueue<String> urls;
	  private final AudioPlayer player;
	  private String nowPlaying = "";
	  public TrackScheduler(AudioPlayer player){
		  this.player = player;
		  this.queue = new LinkedBlockingQueue<AudioTrack>();
		  this.urls = new LinkedBlockingQueue<String>();
	  }
	  @Override
	  public void onPlayerPause(AudioPlayer player) {
	    // Player was paused
	  }

	  @Override
	  public void onPlayerResume(AudioPlayer player) {
		  
	    // Player was resumed
	  }

	  @Override
	  public void onTrackStart(AudioPlayer player, AudioTrack track) {
	    // A track started playing
	  }

	  @Override
	  public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		  //nowPlaying = "";
	      if (endReason.mayStartNext) {
	    	  nextTrack();
	      // Start next track
	      }
 
	    // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
	    // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
	    // endReason == STOPPED: The player was stopped.
	    // endReason == REPLACED: Another track started playing while this had not finished
	    // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
	    //                       clone of this back to your queue
	  }

	  @Override
	  public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
	    // An already playing track threw an exception (track end event will still be received separately)
	  }

	  @Override
	  public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
	    // Audio track has been unable to provide us any audio, might want to just start a new track
		  System.out.println("no audio");
	  }
	  
	  public void queue(AudioTrack track, String url){
		  if(!player.startTrack(track, true)){
			  queue.offer(track);
			  urls.offer(url);
		  }
	  }
	  
	  public void queue(AudioTrack track){
		  if(!player.startTrack(track, true)){
			  queue.offer(track);
		  }
	  }
	  
	  public void nextTrack(){
		  player.startTrack(queue.poll(), false);
		  nowPlaying = urls.poll();
		  System.out.println("nowPlaying = " + nowPlaying);

	  }
	  
	  public boolean queueEmpty(){
		  return queue.isEmpty();
	  }
	  
	  public String getNowPlaying(){
		  return nowPlaying;
	  }
	  
	}
