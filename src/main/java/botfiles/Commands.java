package botfiles;


import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.handle.audio.*;
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
import java.util.Random;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;

import com.sedmelluq.discord.lavaplayer.player.*;

public class Commands {
	String prefix = "!";
	final String gachiURL = "https://www.youtube.com/playlist?list=PLnsDTwJ4UrjgAoQE0tzSfyOEjVWKbow_E";
	
	//maps use the guild ID as keys
	HashMap<String, AudioPlayerManager> managers;
	HashMap<String, AudioPlayer> players;
	HashMap<String, TrackScheduler> schedulers;
	HashMap<String, AudioProvider> providers;
	YoutubeSearch search;
	private final String HELP_MESSAGE = "**Commands**\n\n"
			+ prefix + "play [url] - Plays music in your voice channel, supports youtube, soundcloud, and more\n\n"
			+ prefix + "gachi - gachiGASM\n\n"
			+ prefix + "hi - Says hi!\n\n"
			+ prefix + "stop - Stops the music\n\n"
			+ prefix + "prefix - Changes the prefix\n\n"
            + prefix + "volume [number] - Changes the volume of the music\n\n"
            + prefix + "skip - Skips the current song\n\n"
            + prefix + "nuke [number] - Deletes [number] messages\n\n"
			+ prefix + "help - Displays this help message\n\n"
			+ prefix + "nowplaying - Displays the currently playing song\n\n";
	
	
	Commands(){

		players = new HashMap<>();
		schedulers = new HashMap<>();
		managers = new HashMap<>();
		providers = new HashMap<>();
		
		
	}
	

	 /**
	 * Executes actions for !play
	 * @param command array of Strings representing commands
	 * @param evt MessageReceivedEvent triggered
	 */
	public void parsePlay(String[] command, MessageReceivedEvent evt){
		String id = evt.getMessage().getChannel().getGuild().getID();

		//if players, etc don't exist for this guild, create them
		if(!managers.containsKey(id)){
			managers.put(id, new DefaultAudioPlayerManager());
			AudioSourceManagers.registerLocalSource(managers.get(id));
			AudioSourceManagers.registerRemoteSources(managers.get(id));
			players.put(id, managers.get(id).createPlayer());
			schedulers.put(id, new TrackScheduler(players.get(id), evt.getMessage().getClient(), evt.getMessage().getGuild()));
			providers.put(id, new AudioProvider(players.get(id)));
			players.get(id).addListener(schedulers.get(id));
		}
		
		
		List<IVoiceChannel> channels = evt.getMessage().getAuthor().getConnectedVoiceChannels();
		evt.getMessage().getGuild().getAudioManager().setAudioProvider(providers.get(id));
		if(channels.size() < 1){
			try {
				evt.getMessage().getChannel().sendMessage("Please connect to a voice channel first");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				e1.printStackTrace();
			}
			return;
		}
		try {
			channels.get(0).join();
		} catch (MissingPermissionsException e) {
			try {
				evt.getMessage().getChannel().sendMessage("Oops, I can't access that channel");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				e1.printStackTrace();
			}
		}
		
		
		

		//load the song in question
		managers.get(id).loadItem(command[1], new AudioLoadResultHandler() {
			
			  //anonymous class to handle results of audio load
			  @Override
			  public void trackLoaded(AudioTrack track) {
			      schedulers.get(id).queue(track, command[1]);
			  }

			  @Override
			  public void playlistLoaded(AudioPlaylist playlist) {
			    for (AudioTrack track : playlist.getTracks()) {
			      schedulers.get(id).queue(track, command[1]);
			    }
			  }

			  @Override
			  public void noMatches() {
				  search = new YoutubeSearch();
				  String query = "";
				  for(int i = 1; i < command.length; i++){
					  query += command[i] + " ";
				  }
				  try {
					search.search(query);
				  } catch (IOException e1) {
					  try {
						  evt.getMessage().getChannel().sendMessage("Something went wrong");
					  } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
						  e.printStackTrace();
					  }
				  }
				  if(search.getURLs().isEmpty()){
					  try {
						  evt.getMessage().getChannel().sendMessage("Video not found");
					  } catch (MissingPermissionsException | RateLimitException | DiscordException e) {
						  e.printStackTrace();
					  }
				  }
				  else{
					  managers.get(id).loadItem(search.getURLs().get(0),new AudioLoadResultHandler() {
						  @Override
						  public void trackLoaded(AudioTrack track) {
						      schedulers.get(id).queue(track, command[1]);
						  }

						@Override
						public void playlistLoaded(AudioPlaylist playlist) {
							for (AudioTrack track : playlist.getTracks()) {
							      schedulers.get(id).queue(track, command[1]);
							}
							
						}

						@Override
						public void noMatches() {
							try {
								  evt.getMessage().getChannel().sendMessage("Video not found2");
							} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
								  e.printStackTrace();
							  
							}
						}

						@Override
						public void loadFailed(FriendlyException exception) {
							try {
								evt.getMessage().getChannel().sendMessage("Something went wrong");
							} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
								e.printStackTrace();
							}
							
						}
						  
					  });
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
	
	
	/**
	 * Executes actions for !gachi
	 * @param evt MessageReceivedEvent triggered
	 */
	public void parseGachi(MessageReceivedEvent evt){
		String id = evt.getMessage().getChannel().getGuild().getID();

		if(!managers.containsKey(id)){
			managers.put(id, new DefaultAudioPlayerManager());
			AudioSourceManagers.registerLocalSource(managers.get(id));
			AudioSourceManagers.registerRemoteSources(managers.get(id));
			players.put(id, managers.get(id).createPlayer());
			schedulers.put(id, new TrackScheduler(players.get(id), evt.getMessage().getClient(), evt.getMessage().getGuild()));
			providers.put(id, new AudioProvider(players.get(id)));
			players.get(id).addListener(schedulers.get(id));
		}
		List<IVoiceChannel> channels = evt.getMessage().getAuthor().getConnectedVoiceChannels();
		evt.getMessage().getGuild().getAudioManager().setAudioProvider(providers.get(id));
		if(channels.size() < 1){
			try {
				evt.getMessage().getChannel().sendMessage("Please connect to a voice channel first");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				e1.printStackTrace();
			}
			return;
		}
		try {
			channels.get(0).join();
		} catch (MissingPermissionsException e) {
			try {
				evt.getMessage().getChannel().sendMessage("Oops, I can't access that channel");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				e1.printStackTrace();
			}
		}
		
	
		//load the song in question
		managers.get(id).loadItem(gachiURL, new AudioLoadResultHandler() {
			
			  //anonymous class to handle results of audio load
			  @Override
			  public void trackLoaded(AudioTrack track) {
			      schedulers.get(id).queue(track);
			  }

			  @Override
			  public void playlistLoaded(AudioPlaylist playlist) {
				List<AudioTrack> tracks = playlist.getTracks();
			    int listSize = tracks.size();
				//shuffle
				for(int i = 0; i < listSize; i++){
					Random rng = new Random();
					int nextIndex = rng.nextInt(tracks.size());
					schedulers.get(id).queue(tracks.remove(nextIndex));
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
	
	
	public void parseCommands(String[] command, MessageReceivedEvent evt) {
		
		String id = evt.getMessage().getChannel().getGuild().getID();
		/*
		try {
			evt.getMessage().addReaction(evt.getMessage().getChannel().getGuild().getEmojiByID("303722611018432523"));
		} catch (MissingPermissionsException | RateLimitException | DiscordException e3) {
			
		}*/
		
		if(evt.getMessage().getAuthor().getName() == "TehTralah") {
			
			try {
				evt.getMessage().delete();
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*-------Hi Command----------------------------------------*/
		if(command[0].equals(prefix + "hi")){
			try {
				evt.getMessage().getChannel().sendMessage("hi");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				e.printStackTrace();
			}
		}
		
		/*-------Play Command----------------------------------------*/

		if(command[0].equals(prefix + "play")){
			parsePlay(command, evt);
		}
		
		/*-------Stop Command----------------------------------------*/

		if(command[0].equals(prefix + "stop")){
			try{
				players.get(id).stopTrack();
			}
			catch(NullPointerException e){
				try {
					evt.getMessage().getChannel().sendMessage("Currently no playing track");
				} catch (MissingPermissionsException | RateLimitException | DiscordException e2) {
					e2.printStackTrace();
				}
			}
		
			for(IVoiceChannel c : Bot.client.getOurUser().getConnectedVoiceChannels()){
				if(c.getGuild().getID().equals(id)){
					c.leave();
				}
			}
		}
		
		/*-------Volume Command----------------------------------------*/

		if(command[0].equals(prefix + "volume")){
			if(command.length < 2){
				try {
					evt.getMessage().getChannel().sendMessage("Volume set to: " + players.get(id).getVolume());
				} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
					e1.printStackTrace();
				}
			}
			else{
				try{
					players.get(id).setVolume(Integer.parseInt(command[1]));
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
		/*-------gachiGASM Command----------------------------------------*/
		if(command[0].equals(prefix + "gachi")){
			parseGachi(evt);
		}
		
		/*-------prefix Command----------------------------------------*/
		if(command[0].equals(prefix + "prefix")){
			if(command.length < 2){
				try {
					evt.getMessage().getChannel().sendMessage("The prefix is currently: " + prefix);
				} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
					e.printStackTrace();
				}
			}
			else{
				prefix = command[1];
				try {
					evt.getMessage().getChannel().sendMessage("The prefix is now: " + prefix);
				} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
					e.printStackTrace();
				}
			}
		}
		
		/*-------skip command------------------------------------------*/
		if(command[0].equals(prefix + "skip")){
			schedulers.get(id).nextTrack();
			try {
				evt.getMessage().getChannel().sendMessage("Song skipped");
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				e.printStackTrace();
			}
		}
		/*-------help command------------------------------------------*/
		if(command[0].equals(prefix + "help")){
			try{
				evt.getMessage().getChannel().sendMessage(HELP_MESSAGE);
			} catch (MissingPermissionsException | RateLimitException | DiscordException e) {
				e.printStackTrace();
			}
		}

		
		/*-------nuke command------------------------------------------*/
		if(command[0].equals(prefix + "nuke")){
			EnumSet<Permissions> permissions = evt.getMessage().getAuthor().getPermissionsForGuild(evt.getMessage().getGuild());			
			if(permissions.contains(Permissions.ADMINISTRATOR)){
				
				//user can delete, go delete
				if(command.length > 1){
					try{
						int numberToNuke = Integer.parseInt(command[1]);
						if(numberToNuke < 50){
							for(int i = 0; i < numberToNuke; i++){
								try {
									evt.getMessage().getChannel().getMessages().getLatestMessage().delete();
									try{
										Thread.sleep(20);
									}
									catch(InterruptedException e){
										Thread.currentThread().interrupt();
									}
								} catch (MissingPermissionsException | DiscordException e) {
									e.printStackTrace();
								} catch(sx.blah.discord.util.RateLimitException e){
								
									//rate limited, pause for a while
									try {
										Thread.sleep(e.getRetryDelay());                
									} catch(InterruptedException ex) {
										Thread.currentThread().interrupt();
									}
								}
							
													
							}
						}
						else{
							try {
								evt.getMessage().getChannel().sendMessage("Sorry, you can't nuke that many!");
							} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
								e1.printStackTrace();
							}
						}
					}
					catch(NumberFormatException e){
						try {
							evt.getMessage().getChannel().sendMessage("Usage: [prefix]nuke [lines]");
						} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
							e1.printStackTrace();
						}
					}
				}
				else{
					try {
						evt.getMessage().getChannel().sendMessage("Usage: [prefix]nuke [lines]");
					} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
						e1.printStackTrace();
					}
				}
				
			}
			else{
				
				try {
					evt.getMessage().getChannel().sendMessage("Sorry, you can't do this!");
				} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
					e1.printStackTrace();
				}
			}
			
		}
		
		/*---------------nowplaying----------------------------------------------------*/
		if(command[0].equals(prefix + "nowplaying")){
			TrackScheduler scheduler = schedulers.get(evt.getMessage().getChannel().getGuild().getID());
			try {
				evt.getMessage().getChannel().sendMessage("The current playing song is: " + scheduler.getNowPlaying());
			} catch (MissingPermissionsException | RateLimitException | DiscordException e1) {
				e1.printStackTrace();
			}
			
		}
	}
	
	
}
