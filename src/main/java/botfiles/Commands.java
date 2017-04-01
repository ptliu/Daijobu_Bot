package botfiles;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.handle.audio.*;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

import com.sedmelluq.discord.lavaplayer.player.*;

public class Commands {
	String prefix = "!";
	final String gachiURL = "https://www.youtube.com/playlist?list=PLnsDTwJ4UrjgAoQE0tzSfyOEjVWKbow_E";
	AudioPlayerManager playerManager; 
	AudioPlayer player1;
	TrackScheduler scheduler;
	AudioProvider provider;
	Commands(){

	    this.playerManager = new DefaultAudioPlayerManager();
	    AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	    player1 = playerManager.createPlayer();
	    scheduler = new TrackScheduler(player1);
	    provider = new AudioProvider(player1);
	    
	    player1.addListener(scheduler);
	}
	
	public void parsePlay(String[] command, MessageReceivedEvent evt){
		List<IVoiceChannel> channels = evt.getMessage().getAuthor().getConnectedVoiceChannels();
		evt.getMessage().getGuild().getAudioManager().setAudioProvider(provider);
		if(channels.size() < 1){
			try {
				evt.getMessage().getChannel().sendMessage("Please connect to a voice channel first");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return;
		}
		try {
			channels.get(0).join();
		} catch (MissingPermissionsException e) {
			// TODO Auto-generated catch block
			try {
				evt.getMessage().getChannel().sendMessage("Oops, I can't access that channel");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		//load the song in question
		playerManager.loadItem(command[1], new AudioLoadResultHandler() {
			
			  //anonymous class to handle results of audio load
			  @Override
			  public void trackLoaded(AudioTrack track) {
			      scheduler.queue(track);
			  }

			  @Override
			  public void playlistLoaded(AudioPlaylist playlist) {
			    for (AudioTrack track : playlist.getTracks()) {
			      scheduler.queue(track);
			    }
			  }

			  @Override
			  public void noMatches() {
				  try {
					evt.getMessage().getChannel().sendMessage("Video not found");
				} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
					e.printStackTrace();
				}
			  }

			  @Override
			  public void loadFailed(FriendlyException throwable) {
				  try {
					evt.getMessage().getChannel().sendMessage("Something went wrong");
				} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
					e.printStackTrace();
				}
			  }
			});
	}
	public void parseCommands(String[] command, MessageReceivedEvent evt){
		
		/*-------Hi Command----------------------------------------*/
		if(command[0].equals(prefix + "hi")){
			try {
				evt.getMessage().getChannel().sendMessage("hi");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*-------Play Command----------------------------------------*/

		if(command[0].equals(prefix + "play")){
			parsePlay(command, evt);
		}
		
		/*-------Stop Command----------------------------------------*/

		if(command[0].equals(prefix + "stop")){
			player1.stopTrack();
			for(IVoiceChannel c : Bot.client.getOurUser().getConnectedVoiceChannels()){
				c.leave();
			}
		}
		
		/*-------Volume Command----------------------------------------*/

		if(command[0].equals(prefix + "volume")){
			try{
				player1.setVolume(Integer.parseInt(command[1]));
				try {
					evt.getMessage().getChannel().sendMessage("Volume set to: " + command[1]);
				} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
					e1.printStackTrace();
				}
			}
			catch(NumberFormatException e){
				try {
					evt.getMessage().getChannel().sendMessage("Invalid volume");
				} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
}
