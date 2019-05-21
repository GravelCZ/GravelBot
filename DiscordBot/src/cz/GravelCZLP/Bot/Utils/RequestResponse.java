package cz.GravelCZLP.Bot.Utils;

import sx.blah.discord.util.RequestBuffer.IRequest;

public abstract class RequestResponse<T, S> implements IRequest<T>{
	
	public S s;
	
	public RequestResponse(S s) {
		this.s = s;
	}
	
}
