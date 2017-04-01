/*
 * Name: Patrick Liu
 * Date: 4-1-17
 */

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
import de.btobastian.sdcf4j.*;
import de.btobastian.sdcf4j.handler.Discord4JHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.player.*;


class BotListeners{
	static Commands commands = new Commands();
	/*AudioPlayerManager playerManager; 
	BotListeners(){

	    this.playerManager = new DefaultAudioPlayerManager();
	    AudioSourceManagers.registerRemoteSources(playerManager);
	    AudioSourceManagers.registerLocalSource(playerManager);
	}*/
	
	@EventSubscriber
	public static void onMessage(MessageReceivedEvent evt){
		String[] command = evt.getMessage().getContent().split(" ");
		System.out.println(evt.getMessage().getContent());
		commands.parseCommands(command, evt);
	}
}
public class Bot{
	static final String TOKEN = "MjQwOTM4MzA4NjE4NjgyMzY4.C8GDdg.G8Fjl0Y_od2k3okpiZNzFg72O_4";

	static IDiscordClient client;
		
	
	/*------------------------------------------------------------*/
	public static IDiscordClient getClient(String token, boolean login) throws DiscordException{
		ClientBuilder clientBuilder = new ClientBuilder();
		clientBuilder.withToken(token);
		if(login){
			return clientBuilder.login();
		}
		else{
			return clientBuilder.build();
		}
	}
	

	
	public static void main(String args[]) throws DiscordException{
		client = getClient(TOKEN, true);
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new BotListeners());
		
	}
}
