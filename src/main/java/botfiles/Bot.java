/*
 * Name: Patrick Liu
 * Date: 4-1-17
 */

package botfiles;


import com.sedmelluq.discord.lavaplayer.player.event.TrackEndEvent;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.audio.events.TrackFinishEvent;




class BotListeners{
	static Commands commands = new Commands();

	
	@EventSubscriber
	public static void onMessage(MessageReceivedEvent evt){
		String[] command = evt.getMessage().getContent().split(" ");
		System.out.println(evt.getMessage().getContent());
		commands.parseCommands(command, evt);
	}
	
	@EventSubscriber
	public static void onTrackEnd(TrackEndEvent evt){
		
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
	

	
	public static void main(String args[]) {
		try {
			client = getClient(TOKEN, true);
		} catch (DiscordException e) {
			e.printStackTrace();
		}
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(new BotListeners());
		
	}
}
